(ns myproject.login
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [myproject.io :refer [GET]]))

(def app-state (atom {:cred {:login "" :password ""}}))

(defn login-component [app-data owner]
   (dom/div nil
   (dom/a #js {:className "button dropdown"} "Login")
   (dom/br nil)
   (dom/input #js {:type "text"})
   (dom/input #js {:type "password"})))

(enable-console-print!)

(print "Hello there")

(om/root
 login-component
  app-state
  {:target (. js/document (getElementById "login"))})
