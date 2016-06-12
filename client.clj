(ns engine-server.client
  (:use org.httpkit.client)
  (:require [org.httpkit.client :as http])
)

(defn open-channel [uri]
  (http/get uri {}
    (fn [{:keys [status headers body error]}] ;; asynchronous response handling
      (if error
        (println "Failed, exception is " error)
        (println "Async HTTP GET: " status)))))
