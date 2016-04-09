(ns example.core
  (:require [brutha.core :as br]
            [imprimatur.core :as imp]))

(def data
  ["foo" :bar 2 true nil '(foo bar)])

(br/mount
 (imp/render data)
 (.getElementById js/document "main"))
