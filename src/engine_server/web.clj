(ns engine-server.web
  (:use org.httpkit.server engine-server.game)
  (:require
    [ring.middleware.reload :as reload]
    [compojure.handler :refer [site]]
    [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
    [compojure.route :as route]
    [clojure.java.io :as io]
    [environ.core :refer [env]]
    [org.httpkit.timer :refer [schedule-task]]
  )
)

(defn splash []
  {
    :status 200
    :headers {"Content-Type" "text/plain"}
    :body (pr-str ["Hello" :from 'Engine])
  }
)

(defn kill-player [game player-id]
  (remove-player (game :universe) player-id)
  )

(defn send-universe [game channel]
  (send! channel (str (game :universe) "<br/>\n") false)
  (schedule-task (game :step)
     (send-universe (iterate-game game []) channel)))

(defn connect [game-info]
  (fn [req]
    (with-channel req channel
      (on-close channel (fn [status] (kill-player (game-info :game) (game-info :player))))
      (send-universe (game-info :game) channel)
      (on-receive channel (fn [data] (println (str data)))))))

(defn my-game-info [id]
  (let [game (find-or-create-game id)]
    {:player (inc (game :players)) :game (add-player-to-game game)}))

(defroutes all-routes
  (GET "/" [] (splash))
  (GET "/join/:id" [id] (connect (my-game-info id)))     ;; websocket
  (ANY "*" [] (route/not-found (slurp (io/resource "404.html")))))


(defn in-dev? [] (env :dev)) ;; TODO read a config variable from command line, env, or file?

(defn -main [& [port]] ;; entry point, lein run will pick up and start from here
  (let [handler (if (in-dev?)
                  (reload/wrap-reload (site #'all-routes)) ;; only reload when dev
                  (site all-routes))
        port (Integer. (or port (env :port) 5000))]
    (run-server handler {:port port :join? false})))

