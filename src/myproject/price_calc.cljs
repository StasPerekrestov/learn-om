(ns myproject.price-calc
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(def app-state (atom {:calc {
                             :dimensions {:width nil
                                          :length nil
                                          :height nil
                                          :weight nil}
                             :carriers [
                                         {:name "EMS" :fee 10 :price_per_kg 1.5 :max_weight 30}
                                         {:name "Priority" :fee 15 :price_per_kg 5 :max_weight 22}
                                         {:name "Courier" :fee 20 :price_per_kg 15 :max_weight 10}]
                                 }}))

(defn handle-dimension-change [e dimensions upd-dimension]
  (om/update! dimensions upd-dimension (js/parseFloat (.. e -target -value))))

(defn dimensions-component [dimensions owner]
  (reify
    om/IRender
    (render [this]
     (dom/div nil
       (dom/input #js {:type "text" :placeholder "Width"  :onChange #(handle-dimension-change % dimensions :width)})
       (dom/input #js {:type "text" :placeholder "Length" :onChange #(handle-dimension-change % dimensions :length)})
       (dom/input #js {:type "text" :placeholder "Height" :onChange #(handle-dimension-change % dimensions :height)})
       (dom/input #js {:type "text" :placeholder "Weight" :onChange #(handle-dimension-change % dimensions :weight)})))))


(defn carrier-component [carrier owner]
  (reify
    om/IRender
    (render [this]
      (dom/li nil
        (dom/input #js {:type "radio" :name "cr"} (:name carrier))))))

(defn carriers-component [carriers owner]
  (reify
    om/IRender
    (render [this]
       (apply dom/ul #js {:className "inline-list"} (om/build-all carrier-component carriers)))))


(defn eval-price [calc-data]
  (print calc-data)
  (let [{width :width length :length height :height weight :weight} calc-data]
    (if (and (number? width) (number? length) (number? height) (number? weight))
      (* width length height weight) "error")))


(defn eval-component [calc-data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil
        (dom/input #js {:type "text" :placeholder "Price" :value (eval-price calc-data)})))))

(defn calc-component [app-data owner]
  (dom/div nil
     (om/build dimensions-component (:dimensions (:calc app-data)))
     (om/build carriers-component (:carriers (:calc app-data)))
     (om/build eval-component (:dimensions (:calc app-data)))))


(enable-console-print!)

(om/root
 calc-component
  app-state
  {:target (. js/document (getElementById "app"))})
