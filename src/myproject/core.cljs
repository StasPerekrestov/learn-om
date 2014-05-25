(ns myproject.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.net.XhrIo :as xhr]
            [cljs.core.async :as async :refer [chan close!]])
  (:require-macros
    [cljs.core.async.macros :refer [go alt!]]))

(def app-state (atom {:my-list [{:text "take out the trash." :done false}
                                {:text "remember the milk" :done true}
                                {:text "party like its 1999" :done false}
                                {:text "by some milk" :done false}]}))
(defn GET [url]
  (let [ch (chan 1)]
    (xhr/send url
              (fn [event]
                (let [res (js->clj (-> event .-target .getResponseJson) :keywordize-keys true)]
                  (go (>! ch res)
                      (close! ch)))))
    ch))

(defn log
  [s]
  (.log js/console (str s)))

(enable-console-print!)


(defn load-data [items]
  (go
    (let [new-tasks  (<! (GET "/"))]
      (om/transact! items #(vec (concat % new-tasks))))))



; todo is a cursor and we are outside of render.
; so need to use transact!/update! and @ on todo
(defn handle-toggle [todo]
  (om/update! todo [:done] (not (:done @todo))))

(defn todo-component [todo owner]
  (dom/li #js {:className (str "done-" (:done todo))
               :onClick #(handle-toggle todo)}
   (:text todo)))

(defn reload-btn [todos owner]
  (reify
    om/IRender
    (render [this]
      (dom/button #js {:onClick #(load-data todos)} "Add tasks"))))

(defn todo-list-component [app-data owner]
  (dom/div nil
   (dom/h1 nil "My TODO List is Awesome")
   (om/build reload-btn (:my-list app-data))
   (dom/div #js {:className "panel"}
   (apply dom/ul nil (om/build-all todo-component (:my-list app-data))))))

(om/root
 todo-list-component
  app-state
  {:target (. js/document (getElementById "app"))})
