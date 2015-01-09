(ns engine-server.physics)

(def G (* 6.67428 (Math/pow 10 -11)))
(def epsilon (Math/pow 10 -6))

(defn vector-diff [v1 v2]
  (map - v2 v1))

(defn same-vector [v1 v2]
  (and
    (== (count v1) (count v2))
    (every? true? 
      (map (fn [x] (<= (Math/abs x) epsilon)) 
        (vector-diff v1 v2)))))

(defn squared [x]
  (* x x))

(defn quantity [v]
  (Math/sqrt (reduce + (map squared v))))

(defn unity-vector [v]
  (if (== (quantity v) 0)
    [0 0]
    (map (fn [c] (/ c (quantity v))) v)
  )
)

(defn gravity-between [body other-body]
  (let [r (vector-diff (get body :position) (get other-body :position))]
    (map (fn [x] (* x
      (/ (* (get body :mass) (get other-body :mass))
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
    (get body :engine-acceleration)))

(defn iterate-with-time [current factor timestep]
  (map + current
    (map #(* timestep %) factor)))

(defn new-velocity [body acceleration timestep]
  (iterate-with-time (get body :velocity) acceleration timestep))

(defn iterate-position [body velocity timestep]
  (iterate-with-time (get body :position) velocity timestep))

(defn new-body [time-interval other-bodies body]
  (let [acceleration (acceleration-for body other-bodies)]
    (let [velocity (new-velocity body acceleration time-interval)]
      (merge body {:position (iterate-position body velocity time-interval)
        :velocity velocity}))))

(defn step [time-interval bodies]
  (map
    (fn [body] (new-body time-interval (remove #(= body %) bodies) body))
    bodies))

; external_gravity = (cs-c).map{|o| o.gravity_on(c) }.inject(:+)
; resulting_acceleration = c.engine_acceleration + external_gravity
; velocity = c.velocity + resulting_acceleration * timestep
; position = c.position + velocity * timestep
; {position: position velocity: velocity mass: c.mass}
