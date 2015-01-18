(ns engine-server.game
  (:require [engine-server.engine :refer [next-frame]]
    [engine-server.body-builder :refer :all]))

(def time-interval 100)
(def base-planet (with-diameter 100 (with-mass 1e6 body)))
(def base-player (with-mass 1.0 body))
(def base-positions [[-1000.0 0.0]
  [1000.0 0.0]
  [0.0 1000.0]
  [0.0 -1000.0]])
(def base-universe {:planet1 base-planet :width 800 :height 600})

(defn add-player-to-universe [universe n]
  (merge universe 
    {(keyword (str "player" (inc n)))
    (with-position ((base-positions n) 0) ((base-positions n) 1) base-player)}))

(defn remove-player-from-universe [universe n]
  (dissoc universe
    (keyword (str "player" n))))

(defn new-universe [players]
  (reduce add-player-to-universe base-universe (range (or players 0))))

(defn new-game [id total-players]
  {
    :id id
    :step time-interval
    :universe (new-universe total-players)
    :players total-players
  })

(defn add-player-to-game [game]
  (merge
    game
    {:universe (add-player-to-universe (game :universe) (game :players))
      :players (inc (game :players))}))

(defn remove-player-from-game [game player]
  (merge
    game
    {:universe (remove-player-from-universe (game :universe) player)
      :players (dec (game :players))}))

(defn iterate-game [game commands]
  (merge
    (new-game (game :id) (game :players))
    {:universe (next-frame (game :step) (game :universe) commands)}))

(defn over? [game]
  (== (game :players) 0))
