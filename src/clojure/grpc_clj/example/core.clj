(ns grpc-clj.example.core
  (:require [grpc-clj.middleware :as middleware]
            [grpc-clj.service :as service])
  (:import [com.foo.services HelloRequest HelloReply]) ;; don't remove
  )
(set! *warn-on-reflection* true)

;; `lein protoc && lein javac` generate the necessary
;; classes from protos. We just need to override 
;; the public methods in the following generated class:
;; com.foo.grpc.services.GreeterGrpc$GreeterImplBase
(service/impl
 "Greeter" 
 :java-package "com.foo.services"
 :java-outer-classname "greeting"
 :metadata [:user-id]) ;; service expects/requires these keys

(defn greet!
  [{:keys [name] :as req}]
  (println "Request metadata:" (meta req))
  ;; takes request map, and produces a response map
  ;; whose keys must map to fields in HelloReply
  {:message (str "Hi " name)})

;; com.foo.grpc.services.GreeterGrpc$GreeterImplBase::SayHello
(service/defgrpc
  SayHello                      ;; fn-name/method we're overriding
  [HelloRequest HelloReply]     ;; request/response types
  `[middleware/with-trace-logs] ;; middleware stack
  `greet!)                      ;; the actual (business-logic) handler

(comment
  (require 'pronto.core '[camel-snake-kebab.core :as csk])

  (macroexpand-1
    '(pronto.core/defmapper
    hello-request-mapper
    [HelloRequest]
    :key-name-fn csk/->kebab-case))

  (macroexpand
    '(service/defgrpc
       SayHello    ;; the fn-name/method we're overriding
       [HelloRequest HelloReply]
       []           ;; middleware stack
       `greet!))

  (macroexpand-1
    '(service/impl
       "Greeter"
       :java-package "com.foo.services"
       :java-outer-classname "greeting"))


  )
