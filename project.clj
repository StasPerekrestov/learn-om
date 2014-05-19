(defproject myproject "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [om "0.6.2"]
                 [compojure "1.1.8"]
                 [hiccup "1.0.5"]
                 [ring-server "0.3.1"]
                 [ring/ring-json "0.3.1"]]

  :plugins [[lein-cljsbuild "1.0.3"][lein-ring "0.8.10"]]
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
    :builds [{:id "myproject"
              :source-paths ["src"]
              :compiler {
                :output-to "resources/public/js/myproject.js"
                :output-dir "resources/public/js/out"
                :optimizations :none
                :source-map true}}]})
