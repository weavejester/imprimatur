(ns example.core
  (:require [brutha.core :as br]
            [imprimatur.core :as imp]))

(defonce data
  (atom ["foo"
         {:time 0}
         #{:bar '(foo bar baz)}
         true
         (subvec [1 2 3] 0 2)
         nil
         {:foo "hello"
          :bar (js/Date.)
          :baz (random-uuid)
          :quz (into cljs.core.PersistentQueue.EMPTY [1 2 3])}]))

(defonce visibility
  (atom {}))

(def main
  (.getElementById js/document "main"))

(defn render []
  (br/mount
   (imp/print
    {:root @data
     :visibility @visibility
     :on-toggle #(swap! visibility imp/toggle %)})
   main))

(defn inc-time []
  (swap! data update-in [1 :time] inc)
  (js/setTimeout inc-time 1000))

(defonce start
  (do
    (add-watch visibility :change (fn [_ _ _ _] (render)))
    (add-watch data       :change (fn [_ _ _ _] (render)))
    (render)
    (inc-time)))

;; uncomment and save file for some figwheel sweetness
; (swap! data assoc 0 "baz")

(defn on-js-reload []
  (render))
