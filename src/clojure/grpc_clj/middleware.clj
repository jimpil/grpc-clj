(ns grpc-clj.middleware
  (:require [grpc-clj.state :as state]))

(defn wrap-handler
  [handler middleware]
  {:pre [(sequential? middleware)]}
  (reduce #(%2 %1) handler middleware))

#_(defn with-context
  [find-current-ctx handler]
  (fn [req]
    (binding [state/*context* (find-current-ctx req)]
      (handler req))))

(defn with-trace-logs
  [handler]
  (fn [req]
    (println "[INCOMING REQUEST] -" req)
    (let [resp (handler req)]
      (println "[OUTGOING RESPONSE] -" resp)
      resp)))
