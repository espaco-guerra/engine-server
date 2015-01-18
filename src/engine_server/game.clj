(ns engine-server.game
  (:require [engine-server.engine :refer [next-frame]]
    [engine-server.body-builder :refer :all]))

(def time-interval 100)
(def base-planet (with-diameter 2000.0 (with-mass 1e6 body)))
(def base-player (with-diameter 100.0 (with-mass 1.0 body)))
(def base-positions [[-4000.0 0.0]
  [4000.0 0.0]
  [0.0 4000.0]
  [0.0 -4000.0]])
(def base-bodies {:planet1 base-planet})
(def base-universe {:width 4e10 :height 3e10 :bodies {}})

(defn add-ship-to-bodies [bodies n]
  (merge bodies
    {(keyword (str "player" (inc n)))
    (with-position ((base-positions n) 0) ((base-positions n) 1) base-player)}))

(defn remove-ship-from-bodies [bodies n]
  (dissoc bodies
    (keyword (str "player" n))))

(defn new-universe [players]
  (merge base-universe
    {:bodies (reduce add-ship-to-bodies base-bodies (range (or players 0)))}))

(defn new-game [id total-players]
  {
    :id id
    :step time-interval
    :universe (new-universe total-players)
    :players total-players
  })

(defn add-player-to-game [game]
  (let [
    universe (game :universe)
    bodies (universe :bodies)
    players (game :players)]
    (merge game
      {:universe (merge universe {:bodies (add-ship-to-bodies bodies players)})
      :players (inc (game :players))})
  ))

(defn remove-player-from-game [game player]
  (let [
    universe (game :universe)
    bodies (universe :bodies)
    players (game :players)]
    (merge game
      {:universe (merge universe {:bodies (remove-ship-from-bodies bodies player)})
      :players (max 0 (dec (game :players)))})
  ))

(defn iterate-game [game commands]
  (merge game
    {:universe (merge
      (game :universe)
      {:bodies (next-frame (game :step) ((game :universe) :bodies) commands)})
    }))

(defn over? [game]
  (== (game :players) 0))
