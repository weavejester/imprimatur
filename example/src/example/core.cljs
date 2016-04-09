(ns example.core
  (:require [brutha.core :as br]
            [imprimatur.core :as imp]))

(def data
  ["foo" 2 #{:bar '(foo bar)} true nil {:foo "bar" :baz "quz"}])

(br/mount
 (imp/render data)
 (.getElementById js/document "main"))
