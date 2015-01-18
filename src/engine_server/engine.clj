(ns engine-server.engine
  (:require [engine-server.physics :refer [new-body]]))

(defn iterate-body-positions [time-interval bodies]
  (fn [new-bodies [body-name body]]
    (let [other-bodies (remove #(= body %) (vals bodies))]
      (merge new-bodies
        {body-name (new-body time-interval other-bodies body)}))))

(defn step [time-interval bodies]
  (reduce (iterate-body-positions time-interval bodies) {} bodies))

(defn next-frame [time-interval bodies commands]
  (step time-interval bodies))
