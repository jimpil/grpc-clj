# grpc-clj


## What

Clojure server/client utilities for GRPC communication. 

## Where

FIXME: add clojars coords

## How
This library is **not** concerned with compiling `.proto` files (protocol buffers). 
These are expected to have been compiled in advance (via `protoc`). In addition, the  
generated Java source files, are expected to have been compiled too (via `javac`).

In other words, all the necessary Java class files should be in-place, before attempting to 
load namespaces with service definitions. Moreover, such namespaces should be AOT-compiled.

### Service definition
After compiling a `.proto` service you end up with a Class like `xxxGrpc`, 
containing an inner Class `xxxImplBase`. This contains abstract method(s)
which we will need to override. 

Assuming the standard [greeting.proto](src/clojure/grpc_clj/example/greeting.proto), 
the following macro will emit `gen-class` expressions for extending `GreeterGrpc$GreeterImplBase`.

```clj
(service/impl "Greeter" 
 :java-package "com.foo.services"
 :java-outer-classname "greeting"
 :metadata [:user-id]) ;; more on this later
```
After the above is evaluated we have essentially a proxy to `GreeterGrpc$GreeterImplBase` 
in the current namespace. We now need to implement the service method(s), and 
`service/defgrpc` does exactly that:

```clj
(declare greet!)

(service/defgrpc SayHello       ;; fn-name/method we're overriding
  [HelloRequest HelloReply]     ;; request/response types
  `[middleware/with-trace-logs] ;; middleware stack
  `greet!)                      ;; the actual (business-logic) handler
```

### Handlers
A handler is simply a function taking a request (conforming the provided request type), 
and producing a response (conforming the provided response type). The following function
is a perfect candidate for our example:

```clj
;; takes a request-map conforming to `HelloRequest`,
;; and MUST produce a response-map conforming to `HelloReply`
(defn greet!
  [{:keys [name] :as req}]
  (println "Request metadata:" (meta req))
  {:message (str "Hi " name)})
```

### Metadata
Clients calling a particular grpc-method have the option to attach metadata.
This library exposes a mechanism for propagating that metadata automatically 
via the Context. All you need to do is to declare the set of keys each service 
expects, and they will be made available as clojure metadata (on the request map),
to all service handlers.

For example, earlier we saw that the `Greeter` service-impl declared `[:user-id]` as
the metadata it expects, and then we saw that the `greet!` function can just call `meta`
on its argument.

## Server  
Once you have defined all your services, a server can be started with
`(def SERVER (start! {:target "localhost:5050"}))`. You don't need to 
explicitly add the services (they are auto-discovered), although you can.

## Client
FIXME

## License

Copyright © 2024 Dimitrios Piliouras

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
