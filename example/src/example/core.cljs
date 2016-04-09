(ns example.core
  (:require [brutha.core :as br]
            [imprimatur.core :as imp]))

(def data
  "Hello World")

(br/mount
 (imp/render data)
 (.getElementById js/document "main"))
