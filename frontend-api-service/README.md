# Frontend API Service

## Overview
This service acts as the **API Gateway + Business Logic Layer** for the Food Delivery Application.  
It handles authentication, authorization, orchestration, Redis caching, Kafka event publishing/consuming, and communicates with the CRUD layer via Retrofit.

## Features
- User Authentication & JWT-based Authorization
- Google OAuth Login Integration
- Role-based Access Control (Admin / User)
- Restaurant Browsing & Individual Menu Fetching
- Order Placement & Order History
- Redis Caching for Restaurants & Orders (Improves Performance)
- Kafka Integration:
    - Publishes `order.created` events
    - Publishes order status events
    - Consumes `order.created` & `order.status.updated`
- Retrofit-Based Integration with CRUD Layer
- Centralized Logging (RequestId, UserId)
- Error Handling + Validation
- Modular Package Structure
- Docker-Ready

## Technologies
- Spring Boot 3
- Java 17
- Retrofit for Http Client
- Kafka + Kafka UI
- Redis Cache
- JWT
- MongoDB (through CRUD layer)

## Project Structure
```
frontend/
 ├── src/main/java/com/example/frontend
 │    ├── auth/...
 │    ├── controller/...
 │    ├── service/...
 │    ├── producer/...
 │    ├── consumer/...
 │    ├── config/...
 │    └── ...
 ├── src/main/resources/application.yaml
 └── README.md
```

## Kafka Topics
- `order.created`
- `order.status.updated`

## How to Run
### 1. Start Docker Services
```
docker-compose up -d
```

### 2. Start CRUD Layer
```
./gradlew bootRun
```

### 3. Start Frontend Layer
```
./gradlew bootRun
```

## API Endpoints
### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login using JWT |
| POST | `/api/auth/google` | Google OAuth Login |

### Restaurants
| GET | `/api/restaurants` | Get list of restaurants |
| GET | `/api/restaurants/{id}` | Get restaurant details |

### Orders
| POST | `/api/orders` | Place an order |
| GET | `/api/orders/user` | Get user order history |
| PUT | `/api/orders/{id}/status` | Admin updates order status |

## Kafka Event Flow
1. User places order → API stores order → Publishes `order.created`
2. Consumer picks event → Logs it
3. Admin updates status → Publishes `order.status.updated`
4. Consumer logs the update

## Docker Compose Includes
- Zookeeper
- Kafka Broker
- Redis
- Kafka UI Dashboard

## Environment Variables
Configured in `application.yaml`.

## Notes
- This is the main business logic layer.
- CRUD layer contains only reusable generic controllers.
- Keep this repo separate from CRUD for clean architecture.

