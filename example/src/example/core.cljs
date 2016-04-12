(ns example.core
  (:require [brutha.core :as br]
            [imprimatur.core :as imp]))

(def data
  ["foo" 2 #{:bar '(foo bar)} true nil {:foo "bar" :baz "quz"}])

(def visibility
  (atom {}))

(def main
  (.getElementById js/document "main"))

(defn render []
  (br/mount
   (imp/print
    {:root data
     :visibility @visibility
     :on-click #(swap! visibility imp/toggle %)})
   main))

(add-watch visibility :change (fn [_ _ _ _] (render)))

(render)
