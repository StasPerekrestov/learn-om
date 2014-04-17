(ns myproject.routes.home
  (:require [compojure.core :refer :all]
            [hiccup.form :refer :all]
            [ring.middleware.json :as middleware]
            [hiccup.core :as h]))


(defn say-hello []
  (h/html [:h1 "Hello Word"]))




(defroutes home-routes
  (GET "/" [] (say-hello)))
