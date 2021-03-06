(defproject myproject "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2411"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.facebook/react "0.11.2"]
                 [om "0.8.0-beta3"]
                 [compojure "1.3.1"]
                 [figwheel "0.1.7-SNAPSHOT"]
                 [hiccup "1.0.5"]
                 [ring-server "0.3.1"]
                 [ring/ring-json "0.3.1"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]
            [lein-ring "0.8.13"]
            [lein-figwheel "0.1.7-SNAPSHOT"]]
  :ring {:handler myproject.handler/app
         :init myproject.handler/init
         :destroy myproject.handler/destroy
         :port 8080}
  :aot :all
  :repl-options {
                  :init-ns myproject.repl
                  :init (start-server)}

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :compiler {
                :output-to  "resources/public/js/myproject.js"
                :output-dir "resources/public/js/out"
                :optimizations :none
                :source-map true
                :preamble ["react/react.min.js"]
                :externs  ["react/externs/react.js"]}}]
    :figwheel {
             :http-server-root "public" ;; default and assumes "resources"
             :server-port 3449 ;; default
             :css-dirs ["public/resources/css"] ;; watch and update CSS
             ;; :ring-handler hello-world.server/handler
             }})
