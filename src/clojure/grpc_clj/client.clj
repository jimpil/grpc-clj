(ns grpc-clj.client
  (:require [clojure.string :as str]
            [pronto.core :as pronto])
  (:import [clojure.lang Reflector]
           [io.grpc  ManagedChannelBuilder]
           [grpc_clj MapCallCredentials]
           (io.grpc.stub AbstractStub)))

(defn for-service
  [service-name {:keys [target host port async?]} [req-mapper req-class]]
  (let [stub-suffix (if async? "FutureStub" "BlockingStub")
        chan (-> (if target
                   (ManagedChannelBuilder/forTarget target)
                   (ManagedChannelBuilder/forAddress host port))
                 .usePlaintext
                 .build)
        ^AbstractStub stub (Reflector/invokeStaticMethod
                             (str service-name "Grpc")
                             (str "new" stub-suffix)
                             (object-array [chan]))]
    ;(println stub)
    (fn invoke!
      ([] (.shutdown chan))
      ([method req-map]
       (let [stub (or (some->> (meta req-map)
                               (MapCallCredentials.)
                               (.withCallCredentials stub))
                      stub)]
         (bean
           (Reflector/invokeInstanceMethod
             stub
             (name method)
             (object-array [(-> req-mapper
                                (pronto/clj-map->proto-map req-class req-map)
                                (pronto/proto-map->proto))])))))
      ([method ret-key req-map]
       (-> method
           (invoke! req-map)
           (get ret-key))))))

(comment
  ;(import 'com.foo.services.HelloRequest)

  (def greeter
    (partial for-service
             "com.foo.services.Greeter"
             {:target "localhost:5050"}))

  (def say-hello!
    (partial
      (greeter
        [grpc-clj.example.core/hello-request-mapper
         com.foo.services.HelloRequest])
      'sayHello
      :message))

  (time
    (say-hello!
      (with-meta {:name "jim"}
                 {"user-id" (str (random-uuid))})))
  )