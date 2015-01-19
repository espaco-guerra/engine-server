(ns engine-server.game-test
  (:use clojure.test engine-server.test-helper
    engine-server.game engine-server.body-builder)
  (:require [engine-server.engine :refer [next-frame]])
)

(deftest add-ship-to-bodies-test
  (testing "adding a ship to empty bodies leaves just the ship"
    (is (= {:player1 (with-position (- orbit-distance) 0.0 base-player)}
          (add-ship-to-bodies {} 0))))
  (testing "adding a ship to other bodies just adds the ship"
    (is
      (=
        {:planet1 base-planet
          :player1 (with-position (- orbit-distance) 0.0 base-player)
          :player2 (with-position orbit-distance 0.0 base-player)}
        (add-ship-to-bodies {:planet1 base-planet
          :player1 (with-position (- orbit-distance) 0.0 base-player)} 1))))
  (testing "adding a ship that is already there replaces the ship"
    (is
      (=
        {:planet1 base-planet
          :player1 (with-position (- orbit-distance) 0.0 base-player)
          :player2 (with-position orbit-distance 0.0 base-player)}
        (add-ship-to-bodies {:planet1 base-planet
          :player1 (with-position (- orbit-distance) 0.0 base-player)
          :player2 (with-position 200.0 0.0 base-player)} 1))))
)

(deftest remove-ship-from-bodies-test
  (testing "removing a ship from empty bodies does nothing"
    (is (= {}
          (remove-ship-from-bodies {} 0))))
  (testing "removing only ship from bodies leaves the universe empty"
    (is
      (=
        {}
        (remove-ship-from-bodies
          {:player1 (with-position (- orbit-distance) 0.0 base-player)} 1))))
  (testing "removing second ship from others removes that ship"
    (is
      (=
        {:player1 (with-position (- orbit-distance) 0.0 base-player)}
        (remove-ship-from-bodies
          {:player1 (with-position (- orbit-distance) 0.0 base-player)
          :player2 (with-position (- orbit-distance) 0.0 base-player)} 2))))
)

(deftest new-universe-test
  (testing "new-universe with no players creates only 1 planet"
    (is (= {:dimensions [4e6 3e6] :bodies {:planet1 base-planet}} (new-universe nil))))
  (testing "new-universe with negative players creates only 1 planet"
    (is (= {:dimensions [4e6 3e6] :bodies {:planet1 base-planet}} (new-universe -1))))
  (testing "new-universe with 0 players creates only 1 planet"
    (is (= {:dimensions [4e6 3e6] :bodies {:planet1 base-planet}} (new-universe 0))))
  (testing "new-universe with 1 player creates a planet and player falling"
    (is (= {:dimensions [4e6 3e6] :bodies {
      :planet1 base-planet
      :player1 (with-position (- orbit-distance) 0.0 base-player)
      }} (new-universe 1))))
  (testing "new-universe with 2 players creates a planet and 2 opposite players falling"
    (is (= {:dimensions [4e6 3e6] :bodies {
      :planet1 base-planet
      :player1 (with-position (- orbit-distance) 0.0 base-player)
      :player2 (with-position orbit-distance 0.0 base-player)
      }} (new-universe 2))))
)

(deftest new-game-test
  (testing "new-game hold id"
    (is (=
      {
        :id :id
        :step time-interval
        :universe (new-universe 0)
        :players 0
      }
     (new-game :id 0))))
)

(deftest add-player-to-game-test
  (testing "adding a player to empty game leaves 1 player in"
    (is (= {
        :id :id
        :step time-interval
        :universe (new-universe 1)
        :players 1
      }
      (add-player-to-game (new-game :id 0)))))
  (testing "adding a player to game with 1 player makes it two"
    (is (= {
        :id :id
        :step time-interval
        :universe (new-universe 2)
        :players 2
      }
      (add-player-to-game (new-game :id 1)))))
)

(deftest remove-player-from-game-test
  (testing "removing a player from empty game keep an empty game"
    (is (= {
        :id :id
        :step time-interval
        :universe (new-universe 0)
        :players 0
      }
      (remove-player-from-game (new-game :id 0) 1))))
  (testing "removing first player from game with 1 player makes it empty game"
    (is (= {
        :id :id
        :step time-interval
        :universe (new-universe 0)
        :players 0
      }
      (remove-player-from-game (new-game :id 1) 1))))
  (testing "removing last player from game with 2 players leaves the first one"
    (is (= {
        :id :id
        :step time-interval
        :universe (new-universe 1)
        :players 1
      }
      (remove-player-from-game (new-game :id 2) 2))))
)

(deftest iterate-game-test
  (testing "game steps moves only bodies without commands"
    (is (=
      {
        :id :id
        :step time-interval
        :universe {:dimensions [4e6 3e6] :bodies 
          (engine-server.engine/next-frame time-interval
            ((new-game :id 1) :universe) [])
        }
        :players 1
      }
      (iterate-game (new-game :id 1) []))))
)

(deftest over-test
  (testing "game is over when no more player stands"
    (is (=
      true
      (over? (new-game :id 0)))))
  (testing "game is not over when players are left"
    (is (=
      false
      (over? (new-game :id 1)))))
)
