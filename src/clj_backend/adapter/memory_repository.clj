(ns clj-backend.adapter.memory-repository
  "In-memory adapter implementing the BikeRepository protocol (useful for testing)"
  (:require [clj-backend.domain.bike-repository :refer [BikeRepository]]))

(defrecord MemoryBikeRepository [bikes]
  BikeRepository
  (get-all-bikes [this]
    (vals @(:bikes this)))

  (get-bike-by-id [this id]
    (get @(:bikes this) id))

  (create-bike [this bike-data]
    (let [id (java.util.UUID/randomUUID)]
      (swap! (:bikes this) assoc (str id) (assoc bike-data :id (str id)))
      (get @(:bikes this) (str id)))))

(defn make-memory-repository
  "Factory function to create an in-memory repository"
  []
  (->MemoryBikeRepository {:bikes (atom {})}))

