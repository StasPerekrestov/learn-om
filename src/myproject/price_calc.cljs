(ns myproject.price-calc
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [figwheel.client :as fw]))

(defonce app-state (atom {:calc {
                             :dimensions {:width nil
                                          :length nil
                                          :height nil
                                          :weight nil}

                             :selected-carrier nil
                             :carriers [
                                         {:name "EMS" :fee 10 :price_per_kg 1.5 :max_weight 30}
                                         {:name "Priority" :fee 15 :price_per_kg 5 :max_weight 22}
                                         {:name "Courier" :fee 20 :price_per_kg 15 :max_weight 10}]
                                 }}))

(defn handle-dimension-change [e dimensions target-dimension]
  (om/update! dimensions target-dimension (js/parseFloat (.. e -target -value))))

(defn dimensions-view [dimensions owner]
  (reify
    om/IRender
    (render [_]
     (let [{width :width length :length height :height weight :weight} dimensions]
       (dom/div #js {:className "panel callout radius"}
         (dom/h5 nil "Dimensions")
         (dom/input #js {:type "text" :value width   :placeholder "Width"  :onChange #(handle-dimension-change % dimensions :width)})
         (dom/input #js {:type "text" :value length  :placeholder "Length" :onChange #(handle-dimension-change % dimensions :length)})
         (dom/input #js {:type "text" :value height  :placeholder "Height" :onChange #(handle-dimension-change % dimensions :height)})
         (dom/input #js {:type "text" :value weight  :placeholder "Weight" :onChange #(handle-dimension-change % dimensions :weight)}))))))


(defn handle-carrier-select [e carrier]
  (om/update! carrier [:selected] (.. e -target -checked)))

(defn carrier-component [carrier owner]
  (reify
    om/IRender
    (render [_]
      (dom/li nil
        (dom/input #js {:type "radio" :name "cr" :checked (:selected carrier) :onChange #(handle-carrier-select % carrier)} (:name carrier))))))

(defn carriers-view [carriers owner]
  (reify
    om/IRender
    (render [_]
       (apply dom/ul #js {:className "inline-list"} (om/build-all carrier-component carriers)))))

(def moveFastFee
  15)

(defn eval-price [calc-data]
  (let [{width :width length :length height :height weight :weight} (:dimensions calc-data)
        carrier (filter #(true? (:selected %)) (:carriers calc-data))]
    (print carrier)
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
            (om/build eval-component (:calc app-data))])))))

(enable-console-print!)
(comment
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
