(ns noir.cljs.repl
  (:use [cljs.repl.browser :only (repl-env)])
  (:require [cljs.repl :as repl]))

(defn browser []
  (repl/repl (repl-env)))
