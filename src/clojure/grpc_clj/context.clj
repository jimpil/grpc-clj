(ns grpc-clj.context
  (:import [io.grpc Context Context$Key]
           (java.util.concurrent Executor)))

(defn new-key
  [k default]
  (let [k (name k)]
    (if default
      (Context/keyWithDefault k default)
      (Context/key k))))

(defn wrap-exec
  ^Executor [^Executor exec]
  (Context/currentContextExecutor exec))

(def current-value (memfn ^Context$Key get))
