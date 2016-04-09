(ns imprimatur.core
  (:require [sablono.core :refer-macros [html]]))

(defprotocol Renderable
  (render [x]))

(defn- render-coll [class opening content closing]
  (html
   [:div {:class class} [:span.opening opening] content [:span.closing closing]]))

(defn- ordered-content [coll]
  (html
   [:ol.content
    (map-indexed (fn [i x] (html [:li {:key i} (render x)])) coll)]))

(defn- unordered-content [coll]
  (html
   [:ul.content
    (for [x coll]
      [:li {:key (pr-str x)} (render x)])]))

(defn- map-content [m]
  (html
   [:dl.content
    (for [[k v] m]
      (list
       [:dt {:key (str "key$" (pr-str k))} (render k)]
       [:dd {:key (str "val$" (pr-str v))} (render v)]))]))

(extend-protocol Renderable
  nil
  (render [_] (html [:code "nil"]))
  string
  (render [x] (html [:code (pr-str x)]))
  number
  (render [x] (html [:code (str x)]))
  boolean
  (render [x] (html [:code (str x)]))
  cljs.core.Symbol
  (render [x] (html [:code (str x)]))
  cljs.core.Keyword
  (render [x] (html [:code (str x)]))
  cljs.core.List
  (render [xs] (render-coll "list" "(" (ordered-content xs) ")"))
  cljs.core.PersistentVector
  (render [xs] (render-coll "vector" "[" (ordered-content xs) "]"))
  cljs.core.PersistentHashSet
  (render [xs] (render-coll "set" "#{" (unordered-content xs) "}"))
  cljs.core.PersistentHashMap
  (render [m] (render-coll "map" "{" (map-content m) "}"))
  cljs.core.PersistentArrayMap
  (render [m] (render-coll "map" "{" (map-content m) "}")))
