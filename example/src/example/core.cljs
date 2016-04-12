(ns example.core
  (:require [brutha.core :as br]
            [imprimatur.core :as imp]))

(def data
  ["foo" 2 #{:bar '(foo bar)} true nil {:foo "bar" :baz "quz"}])

(def opened
  (atom {}))

(def main
  (.getElementById js/document "main"))

(defn render []
  (br/mount
   (imp/print
    {:root data
     :opened @opened
     :on-click #(swap! opened imp/toggle-form %)})
   main))

(add-watch opened :change (fn [_ _ _ _] (render)))

(render)
