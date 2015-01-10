(ns engine-server.math-test
  (:use clojure.test engine-server.test-helper)
  (:require [engine-server.math :refer :all])
)

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
