(ns clj-backend.domain.bike-repository
  "Port (interface) for bike persistence operations")

(defprotocol BikeRepository
  "Protocol defining the interface for bike persistence"
  (get-all-bikes [this] "Retrieve all bikes")
  (get-bike-by-id [this id] "Retrieve a single bike by ID")
  (create-bike [this bike-data] "Create a new bike"))

