(defproject myproject "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [org.clojure/core.async "0.1.278.0-76b25b-alpha"]
                 [om "0.5.3"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [ring-server "0.3.1"]
                 [org.clojure/data.json "0.2.4"]
                 [ring/ring-json "0.3.0"]]

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
                :output-to "myproject.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
