(defproject engine-server "1.0.0-SNAPSHOT"
  :description "An engine server for Espaco Guerra"
  :url "http://espaco-guerra-engine.herokuapp.com"
  :license {:name "Apache License v2.0"
  :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.6.0"]
    [javax.servlet/servlet-api "2.5"]
    [compojure "1.3.1"]
    [ring/ring-devel "1.3.2"]
    [ring/ring-core "1.3.2"]
    [http-kit "2.0.0"]
    [environ "1.0.0"]]
  :main engine-server.web
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.3.1"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "espaco-guerra-engine-standalone.jar"
  :profiles {:production {:env {:production true}}})
