(ns example.core
  (:require [brutha.core :as br]
            [imprimatur.core :as imp]))

(def data
  ["foo" 2 '(true nil)])

(br/mount
 (imp/render data)
 (.getElementById js/document "main"))
