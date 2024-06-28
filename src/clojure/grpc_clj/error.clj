(ns grpc-clj.error
  (:import [io.grpc Status]))

(def codes
  {:cancelled           Status/CANCELLED
   :unauthenticated     Status/UNAUTHENTICATED
   :unauthorised        Status/PERMISSION_DENIED
   :unavailable         Status/UNAVAILABLE
   :invalid-arg         Status/INVALID_ARGUMENT
   :not-found           Status/NOT_FOUND
   :aborted             Status/ABORTED
   :failed-precondition Status/FAILED_PRECONDITION
   :deadline-exceeded   Status/DEADLINE_EXCEEDED})
