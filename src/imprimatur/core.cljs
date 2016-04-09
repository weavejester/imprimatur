(ns imprimatur.core
  (:require [sablono.core :refer-macros [html]]))

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
  (render [x] (html [:code (str x)])))
