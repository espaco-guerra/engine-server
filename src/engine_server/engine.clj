(ns engine-server.engine
  (:require [engine-server.physics :refer [new-body]]))

(defn torusify [dimensions body]
  (merge
    body
    {:position
      (map (fn [[coordinate size]]
        (if (and (<= (- (/ 2 size)) coordinate) (>= (/ 2 size) coordinate))
          coordinate
          (-
            (mod
              (+
                (- coordinate (* (Math/floor (/ coordinate size)) size))
                (/ size 2))
              size)
            (/ size 2))))
          (map vector (body :position) dimensions))}))
        

(defn iterate-body-positions [time-interval universe]
  (fn [new-bodies [body-name body]]
    (let [other-bodies (remove #(= body %) (vals (universe :bodies)))]
      (merge new-bodies
        {body-name (torusify (universe :dimensions) (new-body time-interval other-bodies body))}))))

(defn step [time-interval universe]
  (reduce (iterate-body-positions time-interval universe) {} (universe :bodies)))

(defn next-frame [time-interval universe commands]
  (step time-interval universe))
