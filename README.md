# noir-cljs
A utility that adds CLJS compilation as middleware.

NOTE: requires Noir 1.1.1-SNAPSHOT or later for Clojure 1.3.0 compliance.

## Usage
In your Noir project add the following dependency, switch your Clojure version to 1.3.0-beta1, and Noir to 1.1.1-SNAPSHOT:

```clojure
[org.clojure/clojure "1.3.0-beta1"]
[noir "1.1.1-SNAPSHOT"]
[noir-cljs "0.1.0-SNAPSHOT"]
```

Then in your server.clj add the middleware:

```clojure
(server/add-middleware noir.util.cljs/wrap-cljs)
```

Now every time a .cljs file is changed in your src/ directory, a refresh will trigger the ClojureScript compiler to recompile. By default, the output is put in resources/cljs/bootstrap.js, so simply add this to your layout:

```clojure
(include-js "/cljs/bootstrap.js")
```

And you can see the results of your compilation. If you need to change the options that the CLJS compiler uses, simply add an option key to server/start for :cljsc

```clojure
(server/start 8080 {:cljsc {:optimizations :advanced}})
```

By default, the optimization level is set to simple as it compiles much faster during development.

## License

Copyright (C) 2011 Chris Granger

Distributed under the Eclipse Public License, the same as Clojure.
