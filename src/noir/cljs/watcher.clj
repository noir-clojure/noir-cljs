(ns noir.cljs.watcher
  (:import [java.util Calendar]
           [java.text SimpleDateFormat])
  (:require [noir.cljs.compiler :as compiler]
            [cljs.closure :as cljs]
            [colorize.core :as c]
            [clojure.set :as set]
            [clojure.walk :as walk]
            [clojure.string :as string])
  (:use [watchtower.core :only [watcher get-files ignore-dotfiles
                                extensions rate file-filter on-change]]))

(def options (atom {}))
(def watched (atom {}))
(def diffs (ref []))
(def mode (atom :simple))
(def build-dirs ["src/" "checkouts/"])

(defn ts []
    (let [c (Calendar/getInstance)
          f (SimpleDateFormat. "HH:mm:ss")]
      (c/magenta (.format f (.getTime c)))))

(defn ->name [f]
  (.getPath f))

(defn ->ns [form]
  (let [ns-form (first (filter #(= 'ns (first %)) (rest form)))
        sym (first (filter symbol? (rest ns-form)))]
    (or sym 'cljs.user)))

(defn clean [form]
  (walk/postwalk (fn [x]
                   (if (symbol? x)
                     (let [sname (name x)
                           index (.indexOf sname "__")] 
                       (if (> index -1)
                         (symbol (subs sname 0 (+ 2 index)))
                         x)) 
                     x))
                 form))

(defn ->cljs-file [f]
  (let [form (read (compiler/->form (slurp f)))]
    {:ns (->ns form)
     :form (clean form)}))

(defn init-file [f]
  (let [neue (->cljs-file f)]
    (compiler/->cljs (:form neue) (:ns neue))
    (swap! watched assoc (->name f) neue)))

(defn update-file [f]
  (let [old-form (set (rest (get-in @watched [(->name f) :form])))
        neue (->cljs-file f)
        neue-form (set (rest (:form neue)))
        diff (set/difference neue-form old-form)
        updated (filter diff (rest (:form neue)))]
    (swap! watched assoc (->name f) neue)
    (when (seq updated)
      (let [entry [(:ns neue) (list* 'do updated)]]
        (println (ts) (c/green ":: sending ::") updated)
        (dosync (alter diffs conj entry))))))

(defn compile-options [m]
  (merge {:output-dir "resources/public/cljs/"
          :output-to "resources/public/cljs/bootstrap.js"
          :src-dir "src/"
          :optimizations m
          :pretty-print true}
         @options))

(defn build [m]
  (let [options (compile-options m)]
    (cljs/build (:src-dir options) options)))

(defmulti on-file-changed (fn [m _] m))

(defmethod on-file-changed :interactive [_ fs]
  (doseq [f fs]
    (if-not (@watched (->name f))
      (init-file f)
      (update-file f))))

(defmethod on-file-changed :simple [_ fs]
  (println (ts) (c/cyan ":: Simple compile"))
  (build :simple)
  (println (ts) (c/green ":: Done")))

(defmethod on-file-changed :advanced [_ fs]
  (println (ts) (c/cyan ":: Advanced compile"))
  (build :advanced)
  (println (ts) (c/green ":: Done")))

(defn update-files [fs]
  (println (ts) (c/cyan ":: Files updated") @mode)
  (try
    (on-file-changed @mode fs)
    (catch Exception e
      (.printStackTrace e))))

(defn start [& [opts]]
  (reset! options opts)
  (remove-aot)
  (doseq [f (get-files [(or (:src-dir opts) "src/")]
                       #(every? (fn [func] (func %))
                                [(extensions :cljs) ignore-dotfiles]))]
    (init-file f))
  (watcher build-dirs 
           (rate 100)
           (file-filter (extensions :cljs))
           (file-filter ignore-dotfiles)
           (on-change update-files)))
