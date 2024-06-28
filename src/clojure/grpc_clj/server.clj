(ns grpc-clj.server
  (:require [grpc-clj.util    :as util]
            [grpc-clj.state   :as state]
            [grpc-clj.context :as context])
  (:import [io.grpc ServerBuilder Server BindableService ServerInterceptor ServerInterceptors]
            [java.util.concurrent Executors]
            [grpc_clj MetaToCtxInterceptor]))

(defn- add-services
  ^ServerBuilder [^ServerBuilder builder service-classes]
  (reduce-kv
   (fn [^ServerBuilder builder class-name {:keys [context]}]
     (let [^BindableService srv (util/invoke-contructor class-name)]
       (->> [(MetaToCtxInterceptor. context)]
            (ServerInterceptors/intercept srv)
            (.addService builder))))
   builder
   service-classes))

#_(defn meta->context-interceptor
  ^ServerInterceptor []
  (MetaToCtxInterceptor.))

(defn stop!
  [^Server server]
  (some-> server .shutdown))

#_(defn- add-interceptors
  ^ServerBuilder [ builder interceptors]
  (if (seq interceptors)
    (reduce (fn [^ServerBuilder b ^ServerInterceptor i]
              (.intercept b i))
            builder
            interceptors)
    builder))

(defn start!
  "Create new gRPC server listening on the provided <port>.
   The service namespaces need to have been loaded before this runs.
   Returns the server instance, so that it can stopped later on."
  ([opts]
   (start! opts @state/services))
  ([{:keys [port]} services]
   (let [exec (context/wrap-exec (Executors/newVirtualThreadPerTaskExecutor))
         server (-> (ServerBuilder/forPort port)
                    (add-services services)
                    (.executor exec)
                    (.build)
                    (.start))]
     #_(-> (Runtime/getRuntime)
         (.addShutdownHook
          (Thread. ^Runnable (partial stop! server))))
     server)))

(comment
  (def SERVER
    (start! {:port 5050}))


  )
