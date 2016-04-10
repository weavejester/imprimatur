(ns imprimatur.core
  (:refer-clojure :exclude [print])
  (:require [brutha.core :as br]
            [sablono.core :refer-macros [html]]))

(defprotocol IRenderable
  (-render [x state]))

(defn render [state]
  (-render (:root state) state))

(def print
  (br/component render))

(def ^:private ellipses
  (html [:span.ellipses "..."]))

(defn- render-coll [class opening content closing]
  (html
   [:div {:class class}
    [:span.opening opening]
    content
    [:span.closing closing]]))

(defn- ordered-content [coll {:keys [open]}]
  (if open
    (html
     [:ol.content
      (map-indexed
       (fn [i x] (html [:li {:key i} (print {:root x :open (get open i)})]))
       coll)])
    ellipses))

(defn- unordered-content [coll {:keys [open]}]
  (if open
    (html
     [:ul.content
      (for [x coll]
        [:li {:key (pr-str x)} (print {:root x :open (get open x)})])])
    ellipses))

(defn- map-content [m {:keys [open]}]
  (if open
    (html
     [:dl.content
      (for [[k v] m]
        (list
         [:dt {:key (str "key$" (pr-str k))}
          (print {:root k :open (:key (get open k))})]
         [:dd {:key (str "val$" (pr-str v))}
          (print {:root v :open (:val (get open k))})]))])
    ellipses))

(extend-protocol IRenderable
  nil
  (-render [_ _] (html [:code "nil"]))
  string
  (-render [x _] (html [:code (pr-str x)]))
  number
  (-render [x _] (html [:code (str x)]))
  boolean
  (-render [x _] (html [:code (str x)]))
  cljs.core.Symbol
  (-render [x _] (html [:code (str x)]))
  cljs.core.Keyword
  (-render [x _] (html [:code (str x)]))
  cljs.core.List
  (-render [xs state] (render-coll "list" "(" (ordered-content xs state) ")"))
  cljs.core.PersistentVector
  (-render [xs state] (render-coll "vector" "[" (ordered-content xs state) "]"))
  cljs.core.PersistentHashSet
  (-render [xs state] (render-coll "set" "#{" (unordered-content xs state) "}"))
  cljs.core.PersistentHashMap
  (-render [m state] (render-coll "map" "{" (map-content m state) "}"))
  cljs.core.PersistentArrayMap
  (-render [m state] (render-coll "map" "{" (map-content m state) "}")))
