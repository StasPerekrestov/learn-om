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

(defn update-task [tasks]
  "Updates tasks with new items"
   (print "! " tasks)
   (swap! app-state #(assoc-in % [:my-list] (apply conj (:my-list %) tasks))))

(go
  (let [t  (<! (GET "/"))]
    (update-task t)
    (print @app-state)))



; todo is a cursor and we are outside of render.
; so need to use transact!/update! and @ on todo
(defn handle-toggle [todo]
  (om/update! todo [:done] (not (:done @todo))))

(defn todo-component [todo owner]
  (dom/li #js {:className (str "done-" (:done todo))
               :onClick #(handle-toggle todo)}
   (:text todo)))

(defn todo-list-component [app-data owner]
  (dom/div nil
   (dom/h1 nil "My TODO List is Awesome")
   (apply dom/ul nil (om/build-all todo-component (:my-list app-data)))))

(om/root
 todo-list-component
  app-state
  {:target (. js/document (getElementById "app"))})
