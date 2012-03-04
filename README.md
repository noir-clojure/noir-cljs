# noir-cljs
A utility that adds instant CLJS compilation to your noir project, as well as a client-side interface for controlling it as you work.

![client side interface](https://github.com/ibdknox/noir-cljs/raw/master/buttons.png)

## Usage
In your Noir project add the following dependency, switch your Clojure version to 1.3.0-beta1, and Noir to 1.1.1-SNAPSHOT:

```clojure
[noir-cljs "0.2.0"]
```

Then in your server.clj require `noir.cljs.core` and add the following line:

```clojure
(noir.cljs.core/start any-cljs-compiler-opts)
```

Now every time a .cljs file is changed in your src/ directory, the ClojureScript compiler will recompile your project. By default, the optimization mode is set to simple and the output is put in resources/cljs/bootstrap.js, so just add this to your layout:

```clojure
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
