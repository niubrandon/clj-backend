(ns clj-backend.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [org.httpkit.server :refer [run-server]]
            [cheshire.core :as json]
            [clj-backend.db :as db]
            [clj-backend.migrations :as migrations]
            [clojure.java.jdbc :as jdbc])
  (:import [java.util UUID])
  (:gen-class))

;; Database functions
(defn get-all-bikes []
  (jdbc/query {:datasource @db/datasource}
              ["SELECT id, brand, model, year, color, price FROM bikes ORDER BY created_at DESC"]))

(defn get-bike-by-id [id]
  (first (jdbc/query {:datasource @db/datasource}
                     ["SELECT id, brand, model, year, color, price FROM bikes WHERE id = ?::uuid" id])))

(defn create-bike! [brand model year color price]
  (let [id (UUID/randomUUID)]
    (jdbc/insert! {:datasource @db/datasource}
                  :bikes
                  {:id id
                   :brand brand
                   :model model
                   :year year
                   :color color
                   :price price})
    {:id (str id)
     :brand brand
     :model model
     :year year
     :color color
     :price price}))

;; Routes
(defroutes app-routes
  ;; POST /bikes - Create a new bike
  (POST "/bikes" request
    (try
      (let [body (:body request)
            bike (create-bike! (:brand body)
                               (:model body)
                               (:year body)
                               (:color body)
                               (bigdec (:price body)))]
        {:status 201
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string bike)})
      (catch Exception e
        {:status 500
         :headers {"Content-Type" "application/json"}
         :body (json/generate-string {:error (.getMessage e)})})))

  ;; GET /bikes - Get all bikes
  (GET "/bikes" []
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string (get-all-bikes))})

  ;; GET /bikes/:id - Get a specific bike
  (GET "/bikes/:id" [id]
    (if-let [bike (get-bike-by-id id)]
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string bike)}
      {:status 404
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string {:error "Bike not found"})}))

  ;; Health check endpoint
  (GET "/health" []
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string {:status "ok" :message "Server is running"})})

  ;; Catch-all for undefined routes
  (route/not-found
    {:status 404
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string {:error "Not found"})}))

;; Main app with middleware
(def app
  (-> app-routes
      (wrap-json-body {:keywords? true})
      (wrap-json-response)
      (wrap-defaults api-defaults)))

(defn -main
  "Start the bike server"
  [& args]
  (println "Initializing database...")
  (db/init-db!)
  (migrations/create-table-if-not-exists!)
  (println "Starting bike server on port 3000...")
  (run-server app {:port 3000})
  (println "Server started successfully!"))
