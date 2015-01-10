(ns engine-server.engine-test
  (:use clojure.test engine-server.test-helper
    engine-server.body-builder engine-server.engine)
)

(def planet (with-mass 1e6 (body-at 0.0 0.0)))
(def player (with-mass 1.0 (body-at -1000.0 0.0)))

(deftest next-frame-test
  (testing "when empty"
    (let [universe {}]
      (is (= universe (next-frame 1 universe [])))
      (is (= universe (next-frame 1 universe [{:player1 :left}])))
    )
  )
  (testing "with just one planet"
    (let [universe {:planet1 planet}]
      (is (= universe (next-frame 1 universe [])))
      (is (= universe (next-frame 1 universe [{:player1 :left}])))
    )
  )
  (testing "with one planet and one player in balance"
    (let [
      universe {:planet1 (accelerating-to 1e-6 0.0 planet)
        :player1 (accelerating-to -1.0 0.0 player)}]
      (is (= universe
        (next-frame 1 universe [])))
    )
  )
  (testing "with one planet and one player falling"
    (let [
      universe {:planet1 planet
        :player1 player}]
      (is (= {:planet1 (with-position -1e-6 0.0 (with-velocity -1e-6 0.0 planet))
        :player1 (with-position -999.0 0.0 (with-velocity 1.0 0.0 player))}
        (next-frame 1 universe [])))
    )
  )
)
