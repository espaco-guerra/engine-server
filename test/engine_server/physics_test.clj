(ns engine-server.physics-test
  (:require [clojure.test :refer :all]
            [engine-server.physics :refer :all]
  )
)

(def time-interval 1)

(defn vector-as-string [v]
  (str "[" (clojure.string/join " " (map str v)) "]")
  )

(defn is-same-vector [v1 v2]
  (is (same-vector v1 v2)
    (str "Expected: " (vector-as-string v1)
      "\n  Actual: " (vector-as-string v2)
      "\n    Diff: " (vector-as-string (vector-diff v1 v2)))))

(defn with-velocity [x y body]
  (merge body {:velocity [x y]}))

(defn body-at [x y]
  {:mass 1 :position [x y] :velocity [0 0] :engine-acceleration [0 0]})

(deftest same-vector-test
  (testing "null vector"
    (is (= true (same-vector [0 0] [0 0])))
  )
  (testing "unit vector"
    (is (= true (same-vector [1 0] [1 0])))
  )
  (testing "generic vector"
    (is (= true (same-vector [1 2] [1 (/ 4 2)])))
  )
  (testing "floating point vector"
    (is (= true (same-vector [(/ (Math/sqrt 2) 2) (/ (Math/sqrt 2) -2)]
      (unity-vector [2 -2]))))
  )
  (testing "different vectors"
    (is (= false (same-vector [1 1] [1 2])))
  )
  (testing "opposite vectors"
    (is (= false (same-vector [1 1] [-1 -1])))
  )
)

(deftest vector-diff-test
  (testing "same null vectors"
    (is-same-vector [0 0] (vector-diff [0 0] [0 0]))
  )
  (testing "same other vectors"
    (is-same-vector [0 0] (vector-diff [1 1] [1 1]))
  )
  (testing "opposite vectors"
    (is-same-vector [-2 -2] (vector-diff [1 1] [-1 -1]))
  )
  (testing "null vector and horizontal vector"
    (is-same-vector [1 0] (vector-diff [0 0] [1 0]))
  )
  (testing "null vector and vertical vector"
    (is-same-vector [0 1] (vector-diff [0 0] [0 1]))
  )
  (testing "null vector and diagonal vector"
    (is-same-vector [1 1] (vector-diff [0 0] [1 1]))
  )
)

(deftest squared-test
  (testing "positive number"
    (is (== 4 (squared 2)))
  )
  (testing "negative number"
    (is (== 4 (squared -2)))
  )
  (testing "zero"
    (is (== 0 (squared 0)))
  )
  (testing "floating number"
    (is (== 0.25 (squared 0.5)))
  )
)

(deftest quantity-test
  (testing "zero vector"
    (is (== 0 (quantity [0 0])))
  )
  (testing "unit horizontal vector"
    (is (== 1 (quantity [1 0])))
  )
  (testing "unit vertical vector"
    (is (== 1 (quantity [0 1])))
  )
  (testing "horizontal vector"
    (is (== 2 (quantity [-2 0])))
  )
  (testing "vertical vector"
    (is (== 2 (quantity [0 -2])))
  )
  (testing "diagonal vector"
    (is (== (Math/sqrt 8) (quantity [2 -2])))
  )
  (testing "other angled vector"
    (is (== (Math/sqrt 13) (quantity [-3 2])))
  )
)

(deftest unity-vector-test
  (testing "null vector"
    (is-same-vector [0 0] (unity-vector [0 0]))
  )
  (testing "unity horizontal vector"
    (is-same-vector [1 0] (unity-vector [1 0]))
  )
  (testing "unity vertical vector"
    (is-same-vector [0 1] (unity-vector [0 1]))
  )
  (testing "longer vertical vector"
    (is-same-vector [0 -1] (unity-vector [0 -2]))
  )
  (testing "longer horizontal vector"
    (is-same-vector [-1 0] (unity-vector [-2 0]))
  )
  (testing "diagonal vector"
    (is-same-vector [(/ 2 (Math/sqrt 8)) (/ -2 (Math/sqrt 8))] (unity-vector [2 -2]))
  )
  (testing "other angled vector"
    (is-same-vector [(/ -3 (Math/sqrt 13)) (/ 2 (Math/sqrt 13))] (unity-vector [-3 2]))
  )
)

(deftest gravity-between-test
  (testing "body to the left"
    (let [body (body-at -1 0)]
      (testing "equal body at opposite position"
        (is-same-vector [0.25 0] (gravity-between body (body-at 1 0)))
      )
      (testing "equal body pretty close"
        (is-same-vector [4 0] (gravity-between body (body-at -0.5 0)))
      )
    )
  )
  (testing "body to the right"
    (let [body (body-at 1 0)]
      (testing "equal body at opposite position"
        (is-same-vector [-0.25 0] (gravity-between body (body-at -1 0)))
      )
      (testing "equal body pretty close"
        (is-same-vector [-4 0] (gravity-between body (body-at 0.5 0)))
      )
    )
  )
)

(deftest resulting-gravity-test
  (let [body (body-at 0 0)]
    (testing "no other bodies around"
      (is-same-vector [0 0] (resulting-gravity [] body))
    )
    (testing "one equal body at opposite position"
      (is-same-vector [0.25 0] (resulting-gravity [(body-at 2 0)] body))
    )
    (testing "two equal bodies opposite to each other"
      (is-same-vector [0 0] (resulting-gravity [(body-at 2 0) (body-at -2 0)] body))
    )
    (testing "three equal bodies opposite two of which cancel each other"
      (is-same-vector [0 0.25] (resulting-gravity [(body-at 2 0) (body-at -2 0) (body-at 0 2)] body))
    )
  )
)

(deftest new-body-test
  (testing "no other bodies around"
    (is (= (body-at 0.0 0.0) (new-body 1 [] (body-at 0.0 0.0))))
  )
  (testing "two equal bodies opposite"
    (is (= (with-velocity 0.25 0.0 (body-at -0.75 0.0))
          (new-body 1 [(body-at 1 0)] (body-at -1 0))))
    (is (= (with-velocity -0.25 0.0 (body-at 0.75 0.0))
          (new-body 1 [(body-at -1 0)] (body-at 1 0))))
  )
)
