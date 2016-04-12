(ns example.core
  (:require [brutha.core :as br]
            [imprimatur.core :as imp]))

(def data
  ["foo" 2 #{:bar '(foo bar)} true nil {:foo "bar" :baz "quz"}])

(def open
  (atom {}))

(def main
  (.getElementById js/document "main"))

(defn toggle-open [m ks]
  (if (get-in m ks)
    (if (next ks)
      (update-in m (butlast ks) dissoc (last ks))
      (if-let [k (first ks)]
        (dissoc m (first ks))))
    (assoc-in (or m {}) ks {})))

(defn render []
  (br/mount
   (imp/print
    {:root data
     :open @open
     :on-click #(swap! open toggle-open %)})
   main))

(add-watch open :change (fn [_ _ _ _] (render)))

(render)
