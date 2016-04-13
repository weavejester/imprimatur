(ns imprimatur.core
  (:refer-clojure :exclude [print])
  (:require [brutha.core :as br]
            [sablono.core :refer-macros [html]]))

(defprotocol IRenderable
  (-render [x state]))

(defn show [m ks]
  (assoc-in (or m {}) ks {}))

(defn hide [m ks]
  (if (next ks)
    (update-in m (butlast ks) dissoc (last ks))
    (if-let [k (first ks)]
      (dissoc m (first ks)))))

(defn toggle [m ks]
  (if (get-in m ks)
    (hide m ks)
    (show m ks)))

(def print
  (br/component
   (fn [state]
     (html [:div.imprimatur (-render (:root state) state)]))))

(def ^:private ellipses
  (html [:span.ellipses "..."]))

(defn- on-click-handler [f index]
  (let [index (vec (reverse index))]
    (fn [event]
      (.stopPropagation event)
      (f index))))

(defn- render-coll [{:keys [on-click index]} class opening content closing]
  (html
   [:div {:class    class
          :on-click (if on-click (on-click-handler on-click index))}
    [:span.opening opening]
    content
    [:span.closing closing]]))

(defn- ordered-element [state i x]
  (html
   [:li {:key (str i)}
    (print (-> state
               (assoc :root x)
               (update :visibility get i)
               (update :index conj i)))]))

(defn- ordered-content [state coll]
  (if (:visibility state)
    (html [:ol.content (map-indexed #(ordered-element state %1 %2) coll)])
    ellipses))

(defn- unordered-element [state x]
  (html
   [:li {:key (pr-str x)}
    (print (-> state
               (assoc :root x)
               (update :visibility get x)
               (update :index conj x)))]))

(defn- unordered-content [state coll]
  (if (:visibility state)
    (html [:ul.content (map #(unordered-element state %) coll)])
    ellipses))

(defn- map-entry-element [state [k v]]
  (html
   [:tr
    [:th {:key (str "key$" (pr-str k))}
     (print (-> state
                (assoc :root k)
                (update :visibility #(-> % (get k) :key))
                (update :index #(-> % (conj k) (conj :key)))))]
    [:td {:key (str "val$" (pr-str v))}
     (print (-> state
                (assoc :root v)
                (update :visibility #(-> % (get k) :val))
                (update :index #(-> % (conj k) (conj :val)))))]]))

(defn- map-content [state m]
  (if (:visibility state)
    (html [:table.content (map #(map-entry-element state %) m)])
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
  (-render [xs state] (render-coll state "list" "(" (ordered-content state xs) ")"))
  cljs.core.PersistentVector
  (-render [xs state] (render-coll state "vector" "[" (ordered-content state xs) "]"))
  cljs.core.PersistentHashSet
  (-render [xs state] (render-coll state "set" "#{" (unordered-content state xs) "}"))
  cljs.core.PersistentHashMap
  (-render [m state] (render-coll state "map" "{" (map-content state m) "}"))
  cljs.core.PersistentArrayMap
  (-render [m state] (render-coll state "map" "{" (map-content state m) "}")))
