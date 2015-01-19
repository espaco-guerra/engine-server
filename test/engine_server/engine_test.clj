(ns engine-server.engine-test
  (:use clojure.test engine-server.test-helper
    engine-server.body-builder engine-server.engine)
)

(def planet (with-mass 1e6 (body-at 0.0 0.0)))
(def player (with-mass 1.0 (body-at -1000.0 0.0)))

(deftest torusify-test
  (testing "position within dimensions stays the same"
    (let [dimensions [10.0 10.0]]
      (is (= {:position [2.0 2.0]} (torusify dimensions {:position [2.0 2.0]})))
      (is (= {:position [-2.0 -2.0]} (torusify dimensions {:position [-2.0 -2.0]})))
    )
  )
  (testing "position at the edge of the dimensions always go to negative"
    (let [dimensions [10.0 10.0]]
      (is (= {:position [0.0 -5.0]} (torusify dimensions {:position [0.0 5.0]})))
      (is (= {:position [-5.0 0.0]} (torusify dimensions {:position [5.0 0.0]})))
      (is (= {:position [0.0 -5.0]} (torusify dimensions {:position [0.0 -5.0]})))
      (is (= {:position [-5.0 0.0]} (torusify dimensions {:position [-5.0 0.0]})))
    )
  )
  (testing "position beyond the edge of the dimensions changes"
    (let [dimensions [24.0 18.0]]
      (is (= {:position [0.0 -8.0]} (torusify dimensions {:position [0.0 10.0]})))
      (is (= {:position [-11.0 0.0]} (torusify dimensions {:position [13.0 0.0]})))
      (is (= {:position [0.0 8.0]} (torusify dimensions {:position [0.0 -10.0]})))
      (is (= {:position [11.0 0.0]} (torusify dimensions {:position [-13.0 0.0]})))
      (is (= {:position [-6.0 0.0]} (torusify dimensions {:position [18.0 0.0]})))
      (is (= {:position [6.0 0.0]} (torusify dimensions {:position [30.0 0.0]})))
      (is (= {:position [0.0 -3.0]} (torusify dimensions {:position [0.0 15.0]})))
      (is (= {:position [0.0 7.0]} (torusify dimensions {:position [0.0 25.0]})))
      (is (= {:position [-9.0 6.0]} (torusify dimensions {:position [15.0 -12.0]})))
    )
  )
)

(deftest next-frame-test
  (testing "when empty"
    (let [universe {:dimensions [1e6 1e6] :bodies {}}]
      (is (= (universe :bodies) (next-frame 1 universe [])))
      (is (= (universe :bodies) (next-frame 1 universe [{:player1 :left}])))
    )
  )
  (testing "with just one planet"
    (let [universe {:dimensions [1e6 1e6] :bodies {:planet1 planet}}]
      (is (= (universe :bodies) (next-frame 1 universe [])))
      (is (= (universe :bodies) (next-frame 1 universe [{:player1 :left}])))
    )
  )
  (testing "with one planet and one player in balance"
    (let [
      universe {:dimensions [1e6 1e6] :bodies {:planet1 (accelerating-to 1e-6 0.0 planet)
        :player1 (accelerating-to -1.0 0.0 player)}}]
      (is (= (universe :bodies)
        (next-frame 1 universe [])))
    )
  )
  (testing "with one planet and one player falling"
    (let [
      universe {:dimensions [1e4 1e4] :bodies {:planet1 planet
        :player1 player}}]
      (is (= {:planet1 (with-position -1e-6 0.0 (with-velocity -1e-6 0.0 planet))
        :player1 (with-position -999.0 0.0 (with-velocity 1.0 0.0 player))}
        (next-frame 1 universe [])))
    )
  )
)
