(ns imprimatur.core
  (:require [flupot.dom :as dom]))

(defn render [x]
  (dom/pre (pr-str x)))
