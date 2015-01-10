(ns engine-server.physics
  (:require [engine-server.math :refer :all]))

(def G (* 6.67428 (Math/pow 10 -11)))

(defn gravity-between [body other-body]
  (let [r (vector-diff (body :position) (other-body :position))]
    (map (fn [x] (* x
      (/ (other-body :mass)
        (squared (quantity r))
      ))) (unity-vector r))))

(defn resulting-gravity [other-bodies body]
  (if (empty? other-bodies)
    [0 0]
    (apply map +
      (map
        (fn [other-body] (gravity-between body other-body))
        other-bodies
      )
    )
  )
)

(defn acceleration-for [body other-bodies]
  (map + (resulting-gravity other-bodies body)
    (body :engine-acceleration)))

(defn iterate-with-time [current factor timestep]
  (map + current
    (map #(* timestep %) factor)))

(defn new-velocity [body acceleration timestep]
  (iterate-with-time (body :velocity) acceleration timestep))

(defn iterate-position [body velocity timestep]
  (iterate-with-time (body :position) velocity timestep))

(defn new-body [time-interval other-bodies body]
  (let [acceleration (acceleration-for body other-bodies)]
    (let [velocity (new-velocity body acceleration time-interval)]
      (merge body {:position (iterate-position body velocity time-interval)
        :velocity velocity}))))

