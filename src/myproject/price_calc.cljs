(ns myproject.price-calc
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(def app-state (atom {:calc {:width nil
                             :length nil
                             :height nil
                             :weight nil
                             :carriers [
                                         {:name "EMS" :fee 10 :price_per_kg 1.5 :max_weight 30}
                                         {:name "Priority" :fee 15 :price_per_kg 5 :max_weight 22}
                                         {:name "Courier" :fee 20 :price_per_kg 15 :max_weight 10}]
                                 }}))
(defn as-int [input]
  (js/parseInt (.. input -target -value)))

(defn dimensions [app-data owner]
   (dom/div nil
   (dom/input #js {:type "text" :placeholder "Width"  :onChange #(om/update! app-data [:width]  (as-int %))})
   (dom/input #js {:type "text" :placeholder "Length" :onChange #(om/update! app-data [:length] (as-int %))})
   (dom/input #js {:type "text" :placeholder "Height" :onChange #(om/update! app-data [:height] (as-int %))})
   (dom/input #js {:type "text" :placeholder "Weight" :onChange #(om/update! app-data [:weight] (as-int %))})))


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
     (om/build dimensions (:calc app-data))
     (om/build carriers-component (:carriers (:calc app-data)))
     (om/build eval-component (:calc app-data))))


(enable-console-print!)

(om/root
 calc-component
  app-state
  {:target (. js/document (getElementById "app"))})
