(ns grpc-clj.util
  (:require [camel-snake-kebab.core :as csk])
  (:import  [clojure.lang Reflector]))

(defn class-symbol-for-service
  "Generate name of Java class implementing service of name `SERVICE-NAME` from within Clojure namespace `NS`"
  [ns-name service-name]
  (-> ns-name
      (csk/->snake_case)
      (str \. service-name "ServiceImpl")
      symbol))

(def resolve++ (comp var-get resolve))

(defn class-simple-name
  [class-sym]
  (.getSimpleName ^Class (resolve class-sym)))

(defn invoke-contructor
  [^String c & args]
  (-> (Class/forName c)
      (Reflector/invokeConstructor (object-array args))))
