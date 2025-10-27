(ns clj-backend.migrations
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [clj-backend.db :as db]))

(defn get-db-spec []
  {:dbtype   "postgresql"
   :dbname   (or (env :database-name) "clj_backend")
   :host     (or (env :database-host) "localhost")
   :port     (or (env :database-port) 5432)
   :user     (or (env :database-user) "postgres")
   :password (or (env :database-password) "postgres")})

(defn read-migration-file [file-name]
  (slurp (io/resource file-name)))

(defn execute-sql! [db-spec sql]
  (println "Executing SQL...")
  (jdbc/execute! db-spec sql))

(defn migrate!
  "Run all pending migrations"
  []
  (try
    (println "Running migrations...")
    (let [db-spec (get-db-spec)
          sql (read-migration-file "migrations/001-create-bikes-table.sql")]
      (execute-sql! db-spec sql)
      (println "Migrations completed successfully"))
    (catch Exception e
      (println "Migration error:" (.getMessage e)))))

(defn create-table-if-not-exists!
  "Create bikes table if it doesn't exist"
  []
  (try
    (let [db-spec (get-db-spec)
          create-table-sql "CREATE TABLE IF NOT EXISTS bikes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    year INTEGER NOT NULL,
    color VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);"]
      (jdbc/execute! db-spec create-table-sql)
      (println "Database table created/verified"))
    (catch Exception e
      (println "Warning: Could not create table:" (.getMessage e)))))

