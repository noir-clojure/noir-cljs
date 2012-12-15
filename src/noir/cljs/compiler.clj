(ns noir.cljs.compiler
  (:require [cljs.compiler :as comp]
            [cljs.analyzer :as ana])
  (:import (clojure.lang LineNumberingPushbackReader)
           (java.io StringReader)))

(defn lined-reader
  "Create a line preserving reader for line-aware code evaluation in a sandbox."
  [s]
  (let [rdr (StringReader. s)]
    (LineNumberingPushbackReader. rdr)))

(defn ->form [s]
  (lined-reader (str "(do " s ")")))

(defn find-changed [form line-nums]
  (let [form (read (->form form))
        lined (map (juxt (comp :line meta) identity) (rest form))
        grouped (partition 2 1 lined)]
    (reduce
      (fn [changed line]
        (let [line (inc line)
              _ (println line)
              found (first (filter (fn [[[l1] [l2] :as me]]
                                     (println me)
                                     (and (>= line (or l1 0))
                                          (< line l2)))
                                   grouped))]
          (if found
            (-> found
                first
                second)
            (-> grouped
                last
                second
                second))))
      []
      line-nums)))

(defn ->cljs [f & [nsp]]
  (binding [ana/*cljs-ns* (or nsp 'cljs.user)]
    (let [form (if (string? f)
                 (binding [*ns* (create-ns ana/*cljs-ns*)]
                   (read (->form f)))
                 f)
          env {:context :statement :locals {}}
          env (assoc env :ns (@ana/namespaces ana/*cljs-ns*))
          ast (ana/analyze env form)
          js (comp/emits ast)
          wrap-js (comp/emits (binding [ana/*cljs-warn-on-undeclared* false]
                                (ana/analyze env form)))]
      wrap-js)))

