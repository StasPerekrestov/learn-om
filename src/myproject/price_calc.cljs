(ns myproject.price-calc
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]
            [figwheel.client :as fw]))

(defonce app-state (atom {:calc {
                                 :dimensions {:width nil
                                              :length nil
                                              :height nil
                                              :weight nil}
                                 :carriers {
                                            :selected nil
                                            :items [
                                                       {:name "EMS" :fee 10 :price_per_kg 1.5 :max_weight 30}
                                                       {:name "Priority" :fee 15 :price_per_kg 5 :max_weight 22}
                                                       {:name "Courier" :fee 20 :price_per_kg 15 :max_weight 10}]}
                                 }}))

(defn handle-dimension-change [newVal dimensions target-dimension]
         (print newVal)
  (om/update! dimensions target-dimension (js/parseFloat newVal)))

(defn text-box[props owner]
  (reify
    om/IRender
    (render [_]
       (let [{value :value placeholder :placeholder onChange :onChange} props]
       (dom/input #js {:type "text"
                       :value value
                       :placeholder placeholder
                       :onChange #(-> (.. % -target -value)
                                      onChange)})))))

(defn dimensions-view [dimensions owner]
  (reify
     om/IInitState
    (init-state [_]
      {:width-ch  (chan)
       :height-ch (chan)
       :length-ch (chan)
       :weight-ch (chan)
       })
    om/IWillMount
    (will-mount [_]
      (let [width  (om/get-state owner :width-ch)
            height (om/get-state owner :height-ch)
            length (om/get-state owner :length-ch)
            weight (om/get-state owner :weight-ch)]
        (go (loop []
          (let [[v c] (alts! [width height length weight])]
            (cond
             (= c width)  (print "YES-WIDTH: "  v)
             (= c height) (print "YES-HEIGHT: " v)
             (= c length) (print "YES-LENGTH: " v)
             (= c weight) (print "YES-WEIGHT: " v)
             )
            (recur))))))
    om/IRenderState
    (render-state [_ {:keys [width-ch height-ch length-ch weight-ch]}]
     (let [{width :width length :length height :height weight :weight} dimensions]
       (dom/div #js {:className "panel callout radius"}
         (dom/h5 nil "Dimensions")
         (om/build text-box
                   {:value width
                    :placeholder "Width"
                    :onChange #(put! width-ch %)})
          (om/build text-box
                   {:value length
                    :placeholder "Lenght"
                    :onChange #(put! length-ch %)})
         (om/build text-box
                   {:value height
                    :placeholder "Height"
                    :onChange #(put! height-ch %)})
          (om/build text-box
                   {:value weight
                    :placeholder "Weight"
                    :onChange #(put! weight-ch %)})

        )))))

(defn carrier-view [carrier-data owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [select]}]
      (let [carrier (:carrier carrier-data)]
      (dom/li nil
        (dom/input #js {:type "radio"
                        :checked (true? (:selected carrier-data))
                        :name "cr"
                        :onChange (fn [e] (put! select @carrier))}
                       (:name carrier)))))))

(defn carriers-view [carriers-data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:select (chan)})
    om/IWillMount
    (will-mount [_]
      (let [select (om/get-state owner :select)]
        (go (loop []
          (let [selected-item (<! select)]
            (om/update! carriers-data [:selected] selected-item)
            (recur))))))
    om/IRenderState
    (render-state [this {:keys [select]}]
       (apply dom/ul #js {:className "inline-list"}
              (om/build-all carrier-view
                            (map #(hash-map :carrier % :selected (= % (:selected carriers-data)))
                            (:items carriers-data))
                            {:init-state {:select select}})))))

(def moveFastFee 15)

(defn eval-price [calc-data]
  (let [{width :width length :length height :height weight :weight} (:dimensions calc-data)
        carrier (filter #(true? (:selected %)) (:carriers calc-data))]
    (if (every? number? (list width length height weight))
      (str "price is num: " (+ width length)) "Correct all the dimensions")))


(defn eval-component [calc-data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
        (dom/input #js {:type "text" :placeholder "Price" :value (eval-price calc-data)})))))

(defn calc-component [app-data owner]
  (reify
    om/IRender
    (render [_]
      (apply dom/div nil
         (let [calc (:calc app-data)]
           [(om/build dimensions-view (:dimensions calc))
           (om/build carriers-view (:carriers calc))
           (om/build eval-component calc)])))))

(enable-console-print!)
(comment
  (get-in (deref app-state) [:calc :carriers :selected])
  (get-in (deref app-state) [:calc :dimensions])
  )

(om/root
 calc-component
  app-state
  {:target (. js/document (getElementById "app"))})

(fw/watch-and-reload
 :jsload-callback (fn []
                    ;; (stop-and-start-my app)
                    ))
