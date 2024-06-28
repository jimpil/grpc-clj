(ns grpc-clj.example.core
  (:require [grpc-clj.middleware :as middleware]
            [grpc-clj.service :as service])
  (:import [com.foo.services HelloRequest HelloReply]) ;; don't remove
  )
(set! *warn-on-reflection* true)

;; Assuming that protos have been compiled,
;; we just need a type which overrides the public
;; method(s) in the following (generated) class:
;; com.foo.grpc.services.GreeterGrpc$GreeterImplBase

;; generate the (proxy) class which will extend `GreeterImplBase`
;; all the info required can be found in the proto itself, except
;; :metadata - these have to be agreed upon with potential client(s).
(service/impl
 "Greeter" 
 :java-package "com.foo.services"
 :java-outer-classname "greeting"
 :metadata [:user-id]) ;; service expects/requires these keys

;; define the handler
(defn greet!
  [{:keys [name] :as req}]
  (println "Request metadata:" (meta req))
  ;; takes a request-map conforming to `HelloRequest`,
  ;; and MUST produce a response-map conforming to `HelloReply`
  {:message (str "Hi " name)})

;; override GreeterImplBase::SayHello
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
