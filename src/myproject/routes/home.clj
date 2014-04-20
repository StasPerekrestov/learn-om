(ns myproject.routes.home
  (:require [compojure.core :refer :all]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response]]))


(defn handler [request]
  (response [{:text "Task1" :done false} {:text "Task2" :done false} {:text "Task3" :done true}]))

(defroutes home-routes
  (GET "/" [] (wrap-json-response handler)))
