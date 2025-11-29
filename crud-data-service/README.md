# Generic CRUD Backend Service

## Overview
This is a **generic, reusable CRUD microservice** for the Food Delivery Platform.  
It exposes generic endpoints for MongoDB collections and is intentionally designed **without business logic**.

The Frontend API Layer sends filtered queries via Retrofit, and this service executes them generically.

## Purpose
- Centralized place for generic CRUD operations
- No business logic or domain rules
- Reusable for any collection:
    - Restaurants
    - Orders
    - Users (Optional)
    - Any future new section

## Features
- Generic POST / PUT / PATCH / DELETE / GET
- Filtered query support
- Pagination support
- MongoDB Repository Wrapper
- Automatically maps collection name based on request
- Clean, reusable architecture
- Works as data-layer for Frontend API Service
- Lightweight, fast, stateless

## Technologies
- Spring Boot 3
- Java 17
- MongoDB
- Spring Web
- Spring Data MongoDB

## Structure
```
crud/
 ├── src/main/java/com/example/crud
 │    ├── controller/GenericController.java
 │    ├── service/GenericService.java
 │    ├── repository/GenericRepository.java
 │    └── model/...
 ├── src/main/resources/application.yaml
 └── README.md
```

## Endpoints

### Generic CRUD
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/{collection}` | Get all records |
| GET | `/api/{collection}/{id}` | Fetch by ID |
| POST | `/api/{collection}` | Create new document |
| PUT | `/api/{collection}/{id}` | Update full document |
| PATCH | `/api/{collection}/{id}` | Partial update |
| DELETE | `/api/{collection}/{id}` | Remove document |

### Filter Endpoint
| POST | `/api/{collection}/filter?page=x&pageSize=y` |
Execute dynamic filters with pagination. |

## Sample Filter Payload
```
{
  "filters": [
    { "field": "price", "op": "GT", "value": 50 },
    { "field": "cuisine", "op": "EQ", "value": "Indian" }
  ]
}
```

## How It Works
1. Frontend builds query filters dynamically.
2. Frontend sends POST request to this service.
3. CRUD parses filters → builds Mongo query.
4. CRUD returns paginated result.

## How to Run
```
./gradlew bootRun
```

## Why Separate from Frontend?
- Frontend = domain/business logic
- CRUD = reusable data layer
- Makes system modular + clean + scalable

## Notes
- No authentication here. Frontend handles it.
- Keep CRUD repo independent for microservice readiness.
