(ns imprimatur.core
  (:require [flupot.dom :as dom]))

(defprotocol Renderable
  (render [x]))

(extend-protocol Renderable
  nil
  (render [_] (dom/code "nil"))
  string
  (render [x] (dom/code (pr-str x)))
  number
  (render [x] (dom/code (str x)))
  boolean
  (render [x] (dom/code (str x))))
