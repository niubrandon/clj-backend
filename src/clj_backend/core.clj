(ns clj-backend.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [org.httpkit.server :refer [run-server]]
            [cheshire.core :as json])
  (:gen-class))

;; In-memory storage for bikes
(def bikes (atom {}))

;; Bike data model
(defn create-bike [id brand model year color price]
  {:id id
   :brand brand
   :model model
   :year year
   :color color
   :price price})

;; Routes
(defroutes app-routes
  ;; POST /bikes - Create a new bike
  (POST "/bikes" request
    (let [body (:body request)
          id (str (java.util.UUID/randomUUID))
          bike (create-bike id
                           (:brand body)
                           (:model body)
                           (:year body)
                           (:color body)
                           (:price body))]
      (swap! bikes assoc id bike)
      {:status 201
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string bike)}))

  ;; GET /bikes - Get all bikes
  (GET "/bikes" []
    {:status 200
     :headers {"Content-Type" "application/json"}
       :body (json/generate-string (vals @bikes))})

  ;; GET /bikes/:id - Get a specific bike
  (GET "/bikes/:id" [id]
    (if-let [bike (get @bikes id)]
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
  (println "Starting bike server on port 3000...")
  (run-server app {:port 3000}))
