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

(defn- click-handler [f index]
  (let [index (vec (reverse index))]
    (fn [event]
      (.stopPropagation event)
      (f index))))

(defn- render-coll
  [{:keys [on-toggle index visibility]} class opening content closing]
  (html
   [:div.coll {:class class}
    [:div.toggle {:on-click (click-handler on-toggle index)}
     (if visibility [:span.open] [:span.closed])]
    [:div.inner
     [:span.opening opening]
     (if visibility
       [:div.content content]
       [:span.ellipses "..."])
     [:span.closing closing]]]))

(defn- ordered-element [state i x]
  (html
   [:li {:key (str i)}
    (print (-> state
               (assoc :root x)
               (update :visibility get i)
               (update :index conj i)))]))

(defn- ordered-content [state coll]
  (html [:ol (map-indexed #(ordered-element state %1 %2) coll)]))

(defn- unordered-element [state x]
  (html
   [:li {:key (pr-str x)}
    (print (-> state
               (assoc :root x)
               (update :visibility get x)
               (update :index conj x)))]))

(defn- unordered-content [state coll]
  (html [:ul (map #(unordered-element state %) coll)]))

(defn- map-entry-element [state [k v]]
  (html
   [:tr {:key (pr-str k)}
    [:th
     (print (-> state
                (assoc :root k)
                (update :visibility #(-> % (get k) :key))
                (update :index #(-> % (conj k) (conj :key)))))]
    [:td
     (print (-> state
                (assoc :root v)
                (update :visibility #(-> % (get k) :val))
                (update :index #(-> % (conj k) (conj :val)))))]]))

(defn- map-content [state m]
  (html [:table [:tbody (map #(map-entry-element state %) m)]]))

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
