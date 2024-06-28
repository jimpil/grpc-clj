(ns grpc-clj.state)

(defonce services (atom {})) ;; service-class-name => map
