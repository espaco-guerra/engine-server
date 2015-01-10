(ns engine-server.engine
  (:require [engine-server.physics :refer [new-body]]))

(defn add-to-universe [time-interval universe]
  (fn [new-universe [body-name body]]
    (let [other-bodies (remove #(= body %) (vals universe))]
      (merge new-universe
        {body-name (new-body time-interval other-bodies body)}))))

(defn step [time-interval universe]
  (reduce (add-to-universe time-interval universe) {} universe))

(defn next-frame [time-interval universe commands]
  (step time-interval universe))
