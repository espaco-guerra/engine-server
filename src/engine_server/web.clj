(ns engine-server.web
  (:use org.httpkit.server)
  (:require
    [ring.middleware.reload :as reload]
    [compojure.handler :refer [site]]
    [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
    [compojure.route :as route]
    [clojure.java.io :as io]
    [environ.core :refer [env]]
  )
)

(defn splash []
  {
    :status 200
    :headers {"Content-Type" "text/plain"}
    :body (pr-str ["Hello" :from 'Engine])
  }
)

(defn handler [req]
  (with-channel req channel              ; get the channel
    ;; communicate with client using method defined above
    (on-close channel (fn [status]
                        (println "channel closed")))
    (if (websocket? channel)
      (println "WebSocket channel")
      (println "HTTP channel"))
    (on-receive channel (fn [data]       ; data received from client
           ;; An optional param can pass to send!: close-after-send?
           ;; When unspecified, `close-after-send?` defaults to true for HTTP channels
           ;; and false for WebSocket.  (send! channel data close-after-send?)
                          (send! channel data))))) ; data is sent directly to the client

(defroutes all-routes
  (GET "/" [] (splash))
  (GET "/save" [] handler)     ;; websocket
  (ANY "*" [] (route/not-found (slurp (io/resource "404.html")))))


(defn in-dev? [] (env :dev)) ;; TODO read a config variable from command line, env, or file?

(defn -main [& [port]] ;; entry point, lein run will pick up and start from here
  (let [handler (if (in-dev?)
                  (reload/wrap-reload (site #'all-routes)) ;; only reload when dev
                  (site all-routes))
        port (Integer. (or port (env :port) 5000))]
    (run-server handler {:port port :join? false})))

