(ns engine-server.body-builder)

(def body {:mass 1.0 :position [0.0 0.0]
  :velocity [0.0 0.0] :engine-acceleration [0.0 0.0]
  :diameter 10})

(defn with-mass [m body]
  (merge body {:mass m}))

(defn with-position [x y body]
  (merge body {:position [x y]}))

(defn with-diameter [d body]
  (merge body {:diameter d}))
