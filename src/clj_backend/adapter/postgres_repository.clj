(ns clj-backend.adapter.postgres-repository
  "PostgreSQL adapter implementing the BikeRepository protocol"
  (:require [clj-backend.domain.bike-repository :refer [BikeRepository]]
            [clojure.java.jdbc :as jdbc]))

(defrecord PostgresBikeRepository [datasource]
  BikeRepository
  (get-all-bikes [this]
    (jdbc/query {:datasource (:datasource this)}
                ["SELECT id, brand, model, year, color, price FROM bikes ORDER BY created_at DESC"]))

  (get-bike-by-id [this id]
    (first (jdbc/query {:datasource (:datasource this)}
                       ["SELECT id, brand, model, year, color, price FROM bikes WHERE id = ?::uuid" id])))

  (create-bike [this bike-data]
    (let [id (java.util.UUID/randomUUID)]
      (jdbc/insert! {:datasource (:datasource this)}
                    :bikes
                    {:id id
                     :brand (:brand bike-data)
                     :model (:model bike-data)
                     :year (:year bike-data)
                     :color (:color bike-data)
                     :price (:price bike-data)})
      {:id (str id)
       :brand (:brand bike-data)
       :model (:model bike-data)
       :year (:year bike-data)
       :color (:color bike-data)
       :price (:price bike-data)})))

(defn make-postgres-repository
  "Factory function to create a PostgreSQL repository"
  [datasource]
  (->PostgresBikeRepository {:datasource datasource}))

