(ns engine-server.test-helper
  (:use clojure.test engine-server.body-builder engine-server.math)
)

(defn is-same-vector [v1 v2]
  (is (same-vector v1 v2)
    (str "Expected: " (vector-as-string v1)
      "\n  Actual: " (vector-as-string v2)
      "\n    Diff: " (vector-as-string (vector-diff v1 v2)))))

(defn is-same-body [b1 b2 & context]
  (is (same-vector (b1 :position) (b2 :position))
    (str context "Expected Position: " (vector-as-string (b1 :position))
      "\n  Actual Position: " (vector-as-string (b2 :position))
      "\n    Diff Position: " (vector-as-string (vector-diff (b1 :position) (b2 :position)))))
  (is (same-vector (b1 :velocity) (b2 :velocity))
    (str context "Expected velocity: " (vector-as-string (b1 :velocity))
      "\n  Actual velocity: " (vector-as-string (b2 :velocity))
      "\n    Diff velocity: " (vector-as-string (vector-diff (b1 :velocity) (b2 :velocity)))))
  (is (= (b1 :mass) (b2 :mass)) context)
  (is (= (b1 :engine-acceleration) (b2 :engine-acceleration))) context)


(defn with-velocity [x y body]
  (merge body {:velocity [x y]}))

(defn accelerating-to [x y body]
  (merge body {:engine-acceleration [x y]}))

(defn body-at [x y]
  (with-position x y body))
