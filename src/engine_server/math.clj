(ns engine-server.math)

(def epsilon (Math/pow 10 -6))

(defn squared [x]
  (* x x))

(defn vector-as-string [v]
  (str "[" (clojure.string/join " " (map str v)) "]"))

(defn vector-diff [v1 v2]
  (map - v2 v1))

(defn same-vector [v1 v2]
  (and
    (== (count v1) (count v2))
    (every? true? 
      (map (fn [x] (<= (Math/abs x) epsilon)) 
        (vector-diff v1 v2)))))

(defn quantity [v]
  (Math/sqrt (reduce + (map squared v))))

(defn unity-vector [v]
  (if (== (quantity v) 0)
    [0 0]
    (map (fn [c] (/ c (quantity v))) v)
  )
)