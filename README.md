# noir-cljs
A utility that adds instant CLJS compilation to your noir project, as well as a client-side interface for controlling it as you work.

![client side interface](https://github.com/noir-clojure/noir-cljs/raw/master/buttons.png)

## Usage
In your Noir project add the following dependency **AND skip aot compilation**:

```clojure
[noir-cljs "0.3.0"]

;;You also need to set :main to skip aot compilation, e.g:

(defproject noir-cljs-test "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [noir-cljs "0.3.0"]
                           [noir "1.3.0-alpha10"]]
            :main ^{:skip-aot true} noir-cljs-test.server)

```

Then in your server.clj require `noir.cljs.core` and add the following:

```clojure
;; compiler options are optional ;) and are keyed by the compilation
;; type (:simple or :advanced)
(def cljs-options {:advanced {:externs ["externs/jquery.js"]}})

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8090"))]
    (noir.cljs.core/start mode cljs-options) ;; the line you need to add
    (server/start port {:mode mode
                        :ns 'cljstemplate})))
```

Now every time a .cljs file is changed in your src/ directory, the ClojureScript compiler will recompile your project. By default, the optimization mode is set to :simple if the server is in dev mode or :advanced if in prod mode. The output is put in resources/cljs/bootstrap.js, so just add this to your layout:

```clojure
(include-js "/cljs/bootstrap.js")

;;or

(noir.cljs.core/include-scripts)

;;or to load jquery for the client-side interface

(noir.cljs.core/include-scripts :with-jquery)
```

Noir CLJS also includes a client-side interface for switching between Advanced, Simple, and Instant compilation modes. To include it, in one of your cljs files, require `noir.cljs.client.watcher` and add

```clojure
(noir.cljs.client.watcher/init)
```

On the bottom right-hand corner you will now see the buttons you can use to switch between modes. Instant mode will cause the browser to poll the server for changes and any modification to a file will cause the updated forms to be sent to the client, allowing you to modify your cljs in real-time.

And you can see the results of your compilation. By default, the optimization level is set to simple as it compiles much faster during development.

## License

Copyright (C) 2012 Chris Granger

Distributed under the Eclipse Public License, the same as Clojure.
