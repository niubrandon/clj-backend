# Architecture Documentation

## Port and Adapter Pattern (Hexagonal Architecture)

This project uses the **Port and Adapter Pattern** (also known as Hexagonal Architecture) to separate business logic from infrastructure concerns.

### Architecture Overview

```
┌─────────────────────────────────────────────────┐
│                  CORE LOGIC                     │
│  (Routes, Business Logic)                       │
│  - Depends on Port (Protocol)                  │
└─────────────────────────────────────────────────┘
                        │
                        │ depends on
                        ▼
┌─────────────────────────────────────────────────┐
│                  PORT                           │
│  BikeRepository Protocol                       │
│  - Defines interface contract                 │
└─────────────────────────────────────────────────┘
                        ▲
                        │ implements
          ┌────────────┴────────────┐
          │                         │
          ▼                         ▼
┌──────────────────┐      ┌──────────────────┐
│  ADAPTERS        │      │  ADAPTERS         │
│  PostgreSQL      │      │  In-Memory        │
│  Adapter         │      │  (for testing)    │
│  (Production)   │      │  (Development)    │
└──────────────────┘      └──────────────────┘
```

### Project Structure

```
src/clj_backend/
├── core.clj                           # Main server (depends on port)
├── domain/
│   └── bike_repository.clj            # PORT: Protocol definition
├── adapter/
│   ├── postgres_repository.clj        # ADAPTER: PostgreSQL implementation
│   └── memory_repository.clj          # ADAPTER: In-memory implementation
├── db.clj                             # Database connection
└── migrations.clj                     # Database migrations
```

### Components

#### 1. Port (Interface)

**File:** `src/clj_backend/domain/bike_repository.clj`

The port defines the contract that all adapters must implement:

```clojure
(defprotocol BikeRepository
  (get-all-bikes [this])
  (get-bike-by-id [this id])
  (create-bike [this bike-data]))
```

#### 2. PostgreSQL Adapter

**File:** `src/clj_backend/adapter/postgres_repository.clj`

Production adapter that stores bikes in PostgreSQL:

```clojure
(defrecord PostgresBikeRepository [datasource]
  BikeRepository
  (get-all-bikes [this] ...)
  (get-bike-by-id [this id] ...)
  (create-bike [this bike-data] ...))
```

#### 3. In-Memory Adapter

**File:** `src/clj_backend/adapter/memory_repository.clj`

Testing/development adapter that stores bikes in memory:

```clojure
(defrecord MemoryBikeRepository [bikes]
  BikeRepository
  (get-all-bikes [this] ...)
  (get-bike-by-id [this id] ...)
  (create-bike [this bike-data] ...))
```

### Benefits

1. **Separation of Concerns**

   - Business logic doesn't depend on database implementation
   - Easy to switch between storage backends

2. **Testability**

   - Can use in-memory adapter for fast tests
   - Business logic can be tested independently

3. **Flexibility**

   - Easy to add new adapters (e.g., MongoDB, Redis)
   - Can run different adapters in different environments

4. **Maintainability**
   - Clear boundaries between layers
   - Changes to database don't affect business logic

### How It Works

1. **Port Definition**: The `BikeRepository` protocol defines the interface
2. **Adapter Implementation**: Each adapter implements the protocol methods
3. **Dependency Injection**: Core logic receives the adapter via dependency injection
4. **Wiring**: The main function wires up the appropriate adapter

### Usage Examples

#### Using PostgreSQL Adapter (Production)

```clojure
(set-repository! (make-postgres-repository datasource))
```

#### Using In-Memory Adapter (Testing)

```clojure
(set-repository! (make-memory-repository))
```

### Testing

```clojure
(deftest test-bike-operations
  (let [repo (make-memory-repository)]
    (create-bike repo {...})
    (is (= 1 (count (get-all-bikes repo))))))
```

### Adding a New Adapter

To add a new adapter (e.g., MongoDB):

1. Create the adapter file: `src/clj_backend/adapter/mongo_repository.clj`
2. Implement the `BikeRepository` protocol
3. Wire it up in `-main` function (if needed)
4. Add factory function: `make-mongo-repository`

```clojure
(defrecord MongoBikeRepository [connection]
  BikeRepository
  (get-all-bikes [this] ...)
  (get-bike-by-id [this id] ...)
  (create-bike [this bike-data] ...))
```
