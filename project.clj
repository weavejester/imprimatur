(defproject imprimatur "0.1.4-SNAPSHOT"
  :description "Data visualization library for ClojureScript and React"
  :url "https://github.com/weavejester/imprimatur"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.293"]
                 [cljsjs/react "15.3.1-0"]
                 [cljsjs/react-dom "15.3.1-0"]
                 [brutha "0.2.1"]
                 [sablono "0.7.5"]]
  :plugins [[lein-cljsbuild "1.1.4"]]
  :cljsbuild
  {:builds {:main {:source-paths ["src"]
                   :compiler {:output-to "target/main.js"
                              :optimizations :whitespace}}}})
