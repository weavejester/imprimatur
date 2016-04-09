(ns imprimatur.core
  (:require [sablono.core :refer-macros [html]]))

(defprotocol Renderable
  (render [x open]))

(def ^:private ellipses
  (html [:span.ellipses "..."]))

(defn- render-coll [class opening content closing]
  (html
   [:div {:class class} [:span.opening opening] content [:span.closing closing]]))

(defn- ordered-content [coll open]
  (if open
    (html
     [:ol.content
      (map-indexed (fn [i x] (html [:li {:key i} (render x (get open i))])) coll)])
    ellipses))

(defn- unordered-content [coll open]
  (if open
    (html
     [:ul.content
      (for [x coll]
        [:li {:key (pr-str x)} (render x (get open x))])])
    ellipses))

(defn- map-content [m open]
  (if open
    (html
     [:dl.content
      (for [[k v] m]
        (list
         [:dt {:key (str "key$" (pr-str k))} (render k (get open k))]
         [:dd {:key (str "val$" (pr-str v))} (render v (get open k))]))])
    ellipses))

(extend-protocol Renderable
  nil
  (render [_ _] (html [:code "nil"]))
  string
  (render [x _] (html [:code (pr-str x)]))
  number
  (render [x _] (html [:code (str x)]))
  boolean
  (render [x _] (html [:code (str x)]))
  cljs.core.Symbol
  (render [x _] (html [:code (str x)]))
  cljs.core.Keyword
  (render [x _] (html [:code (str x)]))
  cljs.core.List
  (render [xs open] (render-coll "list" "(" (ordered-content xs open) ")"))
  cljs.core.PersistentVector
  (render [xs open] (render-coll "vector" "[" (ordered-content xs open) "]"))
  cljs.core.PersistentHashSet
  (render [xs open] (render-coll "set" "#{" (unordered-content xs open) "}"))
  cljs.core.PersistentHashMap
  (render [m open] (render-coll "map" "{" (map-content m open) "}"))
  cljs.core.PersistentArrayMap
  (render [m open] (render-coll "map" "{" (map-content m open) "}")))
