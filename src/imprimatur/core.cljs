(ns imprimatur.core
  (:require [sablono.core :refer-macros [html]]))

(defn- ordered-collection [class opening coll closing]
  (html
   [:div {:class class}
    [:span.opening opening]
    [:ol.content
     (map-indexed (fn [i x] (html [:li {:key i} (render x)])) coll)]
    [:span.ending closing]]))

(defprotocol Renderable
  (render [x]))

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
  (render [xs] (ordered-collection "list" "(" xs ")"))
  cljs.core.PersistentVector
  (render [xs] (ordered-collection "vector" "[" xs "]")))
