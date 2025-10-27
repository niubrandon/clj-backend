# Clojure Bike Backend API

A RESTful API for managing bike information, built with Clojure, Ring, Compojure, and PostgreSQL.

## Features

- ✅ REST API for bike management (POST, GET endpoints)
- ✅ PostgreSQL database with migrations
- ✅ Connection pooling with HikariCP
- ✅ JSON request/response handling
- ✅ In-memory database support (fallback)

## Prerequisites

- Leiningen 2.x
- Java 17+
- PostgreSQL 12+

## Setup

### 1. Install PostgreSQL

```bash
# macOS
brew install postgresql@14
brew services start postgresql@14

# Linux
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql
```

### 2. Create Database

```bash
# Create the database
createdb clj_backend

# Or using psql
psql -U postgres
CREATE DATABASE clj_backend;
\q
```

### 3. Environment Variables (Optional)

Create a `.env` file or set environment variables:

```bash
export DATABASE_HOST=localhost
export DATABASE_PORT=5432
export DATABASE_NAME=clj_backend
export DATABASE_USER=postgres
export DATABASE_PASSWORD=postgres
```

### 4. Install Dependencies

```bash
lein deps
```

## Running

### Start the Server

```bash
lein run
```

The server will:

1. Initialize the database connection pool
2. Run migrations to create the bikes table
3. Start the HTTP server on port 3000

### Run Tests

```bash
lein test
```

### Run with REPL

```bash
lein repl
```

## API Endpoints

### Health Check

```bash
curl http://localhost:3000/health
```

### Create a Bike

```bash
curl -X POST http://localhost:3000/bikes \
  -H "Content-Type: application/json" \
  -d '{"brand":"Trek","model":"Fuel EX","year":2023,"color":"Blue","price":2500}'
```

### Get All Bikes

```bash
curl http://localhost:3000/bikes
```

### Get Bike by ID

```bash
curl http://localhost:3000/bikes/{bike-id}
```

## Database Migrations

### Run Migrations

```bash
lein run
```

Migrations run automatically on server startup.

### Manual Migration Commands (in REPL)

```clojure
(require '[clj-backend.migrations :as migrations])

;; Run migrations
(migrations/migrate!)

;; Rollback last migration
(migrations/rollback!)

;; Create new migration
(migrations/create-migration! "add-columns")

;; Check migration status
(migrations/status!)
```

## Project Structure

```
clj-backend/
├── src/clj_backend/
│   ├── core.clj       # Main server and routes
│   ├── db.clj         # Database connection pool
│   └── migrations.clj # Migration management
├── test/
│   └── clj_backend/
│       └── core_test.clj
├── migrations/
│   └── 001-create-bikes-table.sql
├── project.clj        # Leiningen project config
└── README.md
```

## License

Copyright © 2025

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
https://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
