(defproject noir-cljs "0.3.7"
  :description "A noir utility to add CLJS compiling through middleware"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [noir "1.3.0"]
                 [fetch "0.1.0-alpha2" :exclusions [org.clojure/clojure]]
                 [jayq "1.0.0"]
                 [crate "0.2.3"]
                 [watchtower "0.1.1" :exclusions [org.clojure/clojure]]
                 [colorize "0.1.1" :exclusions [org.clojure/clojure]]
                 [org.clojure/clojurescript "0.0-1450"]])
