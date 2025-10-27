(ns clj-backend.db
  (:require [hikari-cp.core :as hikari]
            [environ.core :refer [env]]))

(def ^:private db-config
  {:jdbc-url      (str "jdbc:postgresql://"
                       (or (env :database-host) "localhost")
                       ":"
                       (or (env :database-port) "5432")
                       "/"
                       (or (env :database-name) "clj_backend"))
   :username      (or (env :database-user) "postgres")
   :password      (or (env :database-password) "postgres")
   :maximum-pool-size 10})

(defonce datasource
  (atom nil))

(defn init-db!
  "Initialize database connection pool"
  []
  (when-not @datasource
    (reset! datasource (hikari/make-datasource db-config))
    (println "Database connection pool initialized"))
  @datasource)

(defn close-db!
  "Close database connection pool"
  []
  (when @datasource
    (hikari/close-datasource @datasource)
    (reset! datasource nil)
    (println "Database connection pool closed")))

(defn get-connection
  "Get a connection from the pool"
  []
  (when-let [ds @datasource]
    (.getConnection ds)))

