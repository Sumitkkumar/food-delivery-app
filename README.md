# Food Delivery System – Monorepo Root

This is the **main root folder** of the Food Delivery System project.  
It contains all services, Docker infrastructure, and shared documentation.

---

## 📁 Repository Structure

```
/food-delivery/
│
├── frontend-api-service/     # Frontend orchestration layer (Spring Boot)
│   └── README.md
│
├── crud-service/             # Backend CRUD microservice (Spring Boot)
│   └── README.md
│
├── docker-compose.yml        # Infrastructure: Kafka, Zookeeper, Redis, Kafka-UI
│
└── README.md                 # (This file)
```

---

## 🚀 Project Overview

This monorepo contains **two backend applications** and **shared infrastructure** required to run the food delivery platform.

### 1️⃣ Frontend API Service
- Acts as the **gateway** for Flutter/web/mobile clients
- Contains **business logic**
- Calls CRUD service using **Retrofit**
- Uses **MongoDB**, **Redis caching**, **JWT auth**, **Kafka producers/consumers**

### 2️⃣ CRUD Service
- Handles all generic CRUD operations
- Uses **MongoDB**
- Does not contain business logic
- Exposed only to Frontend layer

---

## 🧱 Infrastructure (Docker Compose)

The root contains a **docker-compose.yml** file that launches:

| Component | Purpose |
|----------|---------|
| **Zookeeper** | Required by Kafka broker |
| **Kafka Broker** | Event streaming for Order events |
| **Kafka UI Dashboard** | Inspect Kafka topics/messages |
| **Redis** | Caching layer |

### ✔️ Run Everything
From the root folder:

```bash
docker compose up -d
```

### ✔️ Stop
```bash
docker compose down
```

---

## 🛠️ Local Development Setup

### 1️⃣ Start Docker Infrastructure
```bash
docker compose up -d
```

### 2️⃣ Start CRUD Service
```bash
cd crud-service
./mvnw spring-boot:run
```

### 3️⃣ Start Frontend API Service
```bash
cd frontend-api-service
./mvnw spring-boot:run
```

---

## 🧪 Testing Order Flow

1. Create an order → Sent to CRUD → Stored in MongoDB
2. Frontend API publishes `order.created` event to Kafka
3. Kafka UI will show messages
4. Consumer in frontend receives event
5. Future: Payment service listens and processes payment

---

## 📦 Deployment Notes

- Both services can be containerized separately
- Docker Compose is **only for local environment**
- In production you will use **Kubernetes / ECS / Docker Swarm**
- Kafka/Zookeeper should be deployed as managed services or clusters

---

## 📝 Additional Documentation

- `frontend-api-service/README.md` → Full details of orchestration layer
- `crud-service/README.md` → CRUD microservice explanation

---

## 🤝 Contribution Guidelines

- Follow layered architecture
- Keep business logic in **Frontend API layer**
- CRUD layer must stay **generic**
- Infrastructure changes go into root folder

---

## 📄 License

This project is licensed for learning and personal portfolio use.

---

If you need a full architecture diagram, ER diagrams, or UML sequence charts added to this README, tell me — I’ll generate them.
