(ns engine-server.game
  (:require [engine-server.engine :refer [next-frame]]
    [engine-server.body-builder :refer :all]))

(def time-interval 1000)
(def base-planet (with-mass 1e6 body))
(def base-player (with-mass 1.0 body))
(def base-positions [[-1000.0 0.0]
  [1000.0 0.0]
  [0.0 1000.0]
  [0.0 -1000.0]])
(def base-universe {:planet1 base-planet})

(defn add-player-to-universe [universe n]
  (merge universe 
    {(keyword (str "player" (inc n)))
    (with-position ((base-positions n) 0) ((base-positions n) 1) base-player)}))

(defn remove-player [universe n]
  (dissoc universe
    (keyword (str "player" n))))

(defn new-universe [players]
  (reduce add-player-to-universe base-universe (range (or players 0))))

(defn new-game [total-players]
  {:step time-interval :universe (new-universe total-players) :players total-players})

(defn find-or-create-game [id]
  (let [total-players 0]
    (new-game total-players)))

(defn add-player-to-game [game]
  (merge
    game
    {:universe (add-player-to-universe (game :universe) (game :players))}))

(defn iterate-game [game commands]
  (merge
    (new-game (game :players))
    {:universe (next-frame (game :step) (game :universe) commands)}))
