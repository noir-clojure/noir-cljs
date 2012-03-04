(ns noir.cljs.core
  (:require [noir.options :as nopts]
            [clojure.string :as string]
            [noir.cljs.compiler :as compiler]
            [noir.cljs.watcher :as watcher]
            [noir.cljs.watcher :as watcher])
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.page-helpers :only [include-js]]))

(defpage "/noir-cljs-get-updated" []
  (when (nopts/dev-mode?)
    (dosync
      (let [entries @watcher/diffs]
        (ref-set watcher/diffs [])
        (string/join "\n" (for [[nsp form] entries]
                            (compiler/->cljs form nsp)))))))

(defpage "/noir-cljs-mode" []
  (when (nopts/dev-mode?)
    (pr-str @watcher/mode)))

(defpage [:post "/noir-cljs-mode"] {:keys [m]}
  (when (nopts/dev-mode?)
    (let [neue (keyword (read-string m))]
      (reset! watcher/mode neue)
      (watcher/on-file-changed neue [])
      (pr-str neue))))

(defpage "/noir-cljs-activate-interactive" []
  (when (nopts/dev-mode?)
    (dosync
      (let [entries @watcher/diffs]
        (ref-set watcher/diffs [])
        (string/join "\n" (for [[nsp form] entries]
                            (compiler/->cljs form nsp)))))))

(defpartial include-scripts [jquery?]
  (when jquery?
    (include-js "https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"))
  (include-js "/cljs/bootstrap.js"))

(defn start [& [opts]]
  (watcher/start opts))


