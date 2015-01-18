(ns engine-server.game-test
  (:use clojure.test engine-server.test-helper
    engine-server.game engine-server.body-builder)
)

(deftest new-universe-test
  (testing "new-universe with no players creates only 1 planet"
    (is (= {:planet1 base-planet :width 800 :height 600} (new-universe nil))))
  (testing "new-universe with negative players creates only 1 planet"
    (is (= {:planet1 base-planet :width 800 :height 600} (new-universe -1))))
  (testing "new-universe with 0 players creates only 1 planet"
    (is (= {:planet1 base-planet :width 800 :height 600} (new-universe 0))))
  (testing "new-universe with 1 player creates a planet and player falling"
    (is (= {:planet1 base-planet :player1 (with-position -1000.0 0.0 base-player) :width 800 :height 600} (new-universe 1))))
  (testing "new-universe with 2 players creates a planet and 2 opposite players falling"
    (is (= {:planet1 base-planet :width 800 :height 600
      :player1 (with-position -1000.0 0.0 base-player)
      :player2 (with-position 1000.0 0.0 base-player)} (new-universe 2))))
)
