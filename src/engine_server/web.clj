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
    [clojure.data.json :as json]
  )
)

(def games (atom {}))
(def clients (atom {}))

(defn find-or-create-game [id]
  (if (nil? (@games id))
    (swap! games assoc id (new-game id 0)))
  (@games id))

(defn connect-to-game-id [id]
  (fn [req]
    (with-channel req channel
      (let [game (find-or-create-game id)
        player (inc (game :players))]
        (swap! games assoc id (add-player-to-game game))
        (swap! clients assoc channel {:game-id id :player player}))
      (on-close channel (fn [status]
        (let [game (find-or-create-game id)
          player ((@clients channel) :player)]
          (println (str "Closing channel! :( Player " player " gave up"))
          (swap! games assoc id (remove-player-from-game game player))
          (swap! clients dissoc channel) )))
      (future (loop []
        (let [game (iterate-game (find-or-create-game id) [])]
          (swap! games assoc id game)
          (doseq [client @clients]
            (send! channel (json/write-str (game :universe)) (over? game)))
          (Thread/sleep (game :step))
          (if (not (over? game)) (recur)))))
      (on-receive channel (fn [data] (println (str data))))
    )))

(defroutes all-routes
  (GET "/join/:id" [id] (connect-to-game-id id))     ;; websocket
  (ANY "*" [] (route/not-found (slurp (io/resource "404.html")))))


(defn in-dev? [] (env :dev)) ;; TODO read a config variable from command line, env, or file?

(defn -main [& [port]] ;; entry point, lein run will pick up and start from here
  (let [handler (if (in-dev?)
                  (reload/wrap-reload (site #'all-routes)) ;; only reload when dev
                  (site all-routes))
        port (Integer. (or port (env :port) 5000))]
    (run-server handler {:port port :join? false})))
