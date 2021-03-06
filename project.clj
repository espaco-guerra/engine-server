(defproject engine-server "1.0.0-SNAPSHOT"
  :description "An engine server for Espaco Guerra"
  :url "http://espaco-guerra-engine.herokuapp.com"
  :license {:name "Apache License v2.0"
  :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.8.0"]
    [javax.servlet/javax.servlet-api "4.0.0"]
    [compojure "1.6.0"]
    [ring/ring-devel "1.6.3"]
    [ring/ring-core "1.6.3"]
    [http-kit "2.2.0"]
    [environ "1.1.0"]
    [org.clojure/data.json "0.2.6"]]
  :main engine-server.web
  :aot [engine-server.web]
  :min-lein-version "2.0.0"
  :plugins [[lein-environ "1.0.2"]]
  :uberjar-name "espaco-guerra-engine-standalone.jar"
  :profiles {:production {:env {:production true}}
    :dev {
      :plugins [[com.jakemccrary/lein-test-refresh "0.5.5"]
        [lein-cloverage "1.0.2"]
        [lein-cooper "1.2.2"]]
      :cooper {"test"   ["lein" "test-refresh"]
               "server" ["lein" "run"]}}
    :uberjar {:aot :all}}
  :test-refresh {:notify-command ["terminal-notifier" "-title" "Engine Server Tests" "-message"]})
