(ns imprimatur.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [imprimatur.core-test]))

(doo-tests 'imprimatur.core-test)
