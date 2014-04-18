(ns myproject.routes.home
  (:require [compojure.core :refer :all]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response]]))


(defn handler [request]
  (response {:foo "bar"}))

(defroutes home-routes
  (GET "/" [] (wrap-json-response handler)))
