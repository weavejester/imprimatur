(ns imprimatur.core
  (:refer-clojure :exclude [print])
  (:require [brutha.core :as br]
            [sablono.core :refer-macros [html]]))

(defprotocol IRenderable
  (-render [x state]))

(def print
  (br/component
   (fn [state] (-render (:root state) state))))

(def ^:private ellipses
  (html [:span.ellipses "..."]))

(defn- render-coll [class opening content closing]
  (html
   [:div {:class class}
    [:span.opening opening]
    content
    [:span.closing closing]]))

(defn- ordered-element [i x state]
  (html
   [:li {:key (str i)}
    (print (-> state
               (assoc :root x)
               (update :open get i)
               (update :index conj i)))]))

(defn- ordered-content [coll state]
  (if (:open state)
    (html [:ol.content (map-indexed #(ordered-element %1 %2 state) coll)])
    ellipses))

(defn- unordered-element [x state]
  (html
   [:li {:key (pr-str x)}
    (print (-> state
               (assoc :root x)
               (update :open get x)
               (update :index conj x)))]))

(defn- unordered-content [coll state]
  (if (:open state)
    (html [:ul.content (map #(unordered-element % state) coll)])
    ellipses))

(defn- map-entry-element [[k v] state]
  (html
   (list
    [:dt {:key (str "key$" (pr-str k))}
     (print (-> state
                (assoc :root k)
                (update :open #(-> % (get k) :key))
                (update :index #(-> % (conj k) (conj :key)))))]
    [:dd {:key (str "val$" (pr-str v))}
     (print (-> state
                (assoc :root v)
                (update :open #(-> % (get k) :val))
                (update :index #(-> % (conj k) (conj :val)))))])))

(defn- map-content [m state]
  (if (:open state)
    (html [:dl.content (map #(map-entry-element % state) m)])
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
