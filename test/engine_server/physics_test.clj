(ns engine-server.physics-test
  (:use clojure.test engine-server.test-helper
    engine-server.body-builder engine-server.math
    engine-server.physics)
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
  (testing "massive body with light body"
    (let [massive (with-mass 1e6 (body-at 0 0))
      light (body-at -1000 0)]
      (testing "light body is dragged"
        (is-same-vector [1 0] (gravity-between light massive))
      )
      (testing "massive body almost doesn't move"
        (is-same-vector [-1e-6 0] (gravity-between massive light))
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
    (is-same-body (body-at 0.0 0.0) (new-body 1 [] (body-at 0.0 0.0)))
  )
  (testing "two equal bodies opposite"
    (is-same-body (with-velocity 0.25 0.0 (body-at -0.75 0.0))
          (new-body 1 [(body-at 1 0)] (body-at -1 0)))
    (is-same-body (with-velocity -0.25 0.0 (body-at 0.75 0.0))
          (new-body 1 [(body-at -1 0)] (body-at 1 0)))
  )
  (testing "two equal bodies opposite in balance"
    (let [player-left (accelerating-to -0.25 0 (body-at -1 0))
      player-right (accelerating-to 0.25 0 (body-at 1 0))]
    (is-same-body player-left
          (new-body 1 [player-right] player-left))
    (is-same-body player-right
          (new-body 1 [player-left] player-right))
    )
  )
  ; (testing "balanced system"
  ;   (let [d (/ (Math/sqrt 2) 2)
  ;     mass (- 1 d)
  ;     earth (with-mass mass (body-at 0 0))
  ;     velocity (Math/sqrt mass)
  ;     step (/ (/ (* 2 Math/PI) velocity) 8)]
  ;     (is-same-body
  ;       (with-velocity (* 0.5 velocity) (* 0.5 velocity) (body-at (- 0 d) d))
  ;       (new-body step [earth] (with-velocity 0 velocity (body-at -1 0)))
  ;       "Frame 1")
  ;     (is-same-body
  ;       (with-velocity velocity 0 (body-at 0 1))
  ;       (new-body step [earth] (with-velocity (* 0.5 velocity) (* 0.5 velocity) (body-at (- 0 d) d)))
  ;       "Frame 2")
  ;     (is-same-body
  ;       (with-velocity d (- d 1) (body-at d d))
  ;       (new-body 1 [earth] (with-velocity d 0 (body-at 0 1)))
  ;       "Frame 3")
  ;     (is-same-body
  ;       (with-velocity 0 (- d 0) (body-at 1 0))
  ;       (new-body 1 [earth] (with-velocity d (- d 1) (body-at d d)))
  ;       "Frame 4")
  ;     (is-same-body
  ;       (with-velocity (- d 1) (- 0 d) (body-at d (- 0 d)))
  ;       (new-body 1 [earth] (with-velocity 0 (- 0 d) (body-at 1 0)))
  ;       "Frame 5")
  ;     (is-same-body
  ;       (with-velocity (- 0 d) 0 (body-at 0 -1))
  ;       (new-body 1 [earth] (with-velocity (- d 1) (- 0 d) (body-at d (- 0 d))))
  ;       "Frame 6")
  ;     (is-same-body
  ;       (with-velocity (- 0 d) (- 1 d) (body-at (- 0 d) (- 0 d)))
  ;       (new-body 1 [earth] (with-velocity (- 0 d) 0 (body-at 0 -1)))
  ;       "Frame 7")
  ;     (is-same-body
  ;       (with-velocity 0 d (body-at -1 0))
  ;       (new-body 1 [earth] (with-velocity (- 0 d) d (body-at (- 0 d) (- 0 d))))
  ;       "Frame 8")
  ;   )
  ; )
)

; (deftest step-test
;   (testing "balanced system"
;     (let [system [(with-velocity 0.25 0.0 (body-at 0 1)) (with-velocity -0.25 0.0 (body-at 0 -1))]]
;       (is (= system (step 1 (step 1 (step 1 (step 1 system))))))
;     )
;   )
; )
