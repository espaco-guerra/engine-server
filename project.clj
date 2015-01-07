(defproject engine-server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :main engine-server.core)
(defproject engine-server "1.0.0-SNAPSHOT"
  :description "An engine server for Espaco Guerra"
  :url "http://espaco-guerra-engine.herokuapp.com"
  :license {:name "Apache License v2.0"
  :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.6.0"]
    [compojure "1.1.8"]
    [ring/ring-jetty-adapter "1.2.2"]
    [environ "0.5.0"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.2.1"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "espaco-guerra-engine-standalone.jar"
  :profiles {:production {:env {:production true}}})
