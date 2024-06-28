(defproject grpc-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure       "1.11.3" :scope "provided"]
                 [org.clojure/java.data     "1.2.107"]
                 [camel-snake-kebab         "0.4.3"]
                 [com.appsflyer/pronto      "3.0.0"]
                 ;; minimum java libs
                 [io.grpc/grpc-netty-shaded "1.64.0" :scope "runtime"]
                 [io.grpc/grpc-protobuf     "1.64.0"]
                 [io.grpc/grpc-stub         "1.64.0"]
                 [io.grpc/protoc-gen-grpc-java "1.64.0" :extension "pom"]
                 [com.google.protobuf/protobuf-java "3.25.3"]
                 [org.apache.tomcat/annotations-api "6.0.53"]]
  :injections [(require 'clojure.pprint)]
  :aot :all ;[grpc_clj.example.core]
  :prep-tasks ["protoc" "javac" "compile"]
  :plugins [[com.circleci/lein-protoc "0.6.0"]]
  :proto-source-paths ["src/clojure/grpc_clj/example"]
  :protoc-version "3.10.0"
  :protoc-grpc {:version "1.6.1"}
  :proto-target-path "target/generated-sources/protobuf"
  :java-source-paths ["src/java" "target/generated-sources/protobuf"]
  :source-paths ["src/clojure"]
  :javac-options ["--release" "8"]

  :repl-options {:init-ns grpc-clj.example.core})
