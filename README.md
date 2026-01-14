# IoT Distributed Environmental Monitoring System

A robust distributed system designed to monitor environmental metrics (temperature/humidity) using a microservices architecture. This project demonstrates the integration of multiple communication protocols (**MQTT, gRPC, REST**) to handle different types of IoT device requirements efficiently.

Developed as part of the Distributed Systems curriculum at **Universidade de Évora**.

## Key Features

* **Multi-Protocol Support:**
    * **MQTT:** Optimized for low-power, asynchronous IoT sensors.
    * **gRPC + Protobuf:** High-performance, low-latency binary communication for gateways.
    * **REST API:** Standard HTTP integration for system management and web clients.
* **Containerized Database:** Uses **PostgreSQL** deployed via Docker for data persistence.
* **ORM Integration:** Hibernate (JPA) for automatic entity mapping without manual SQL.
* **Performance Analysis:** Includes a comparative study of protocol throughput and overhead.

## Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.2
* **Database:** PostgreSQL (Dockerized)
* **Messaging:** Mosquitto MQTT Broker
* **Serialization:** Protocol Buffers (Protobuf) & JSON
* **Tools:** Maven, Docker & Docker Compose

## System Architecture

The system is composed of the following modules:

1.  **Central Server:** Handles data ingestion from all protocols and persists it to the database.
2.  **Admin CLI:** A management console to register devices and view logs.
3.  **Simulated Clients:**
    * `client-mqtt`: Simulates sensors publishing via Mosquitto.
    * `client-grpc`: Simulates high-speed data transmission.
    * `client-rest`: Simulates standard web-based data submission.

## Performance Analysis

During development, the following protocol behaviors were observed:

| Protocol | Type | Overhead | Speed | Use Case |
| :--- | :--- | :--- | :--- | :--- |
| **gRPC** | Synchronous | Very Low (Binary) | **Very High** | Ideal for gateways requiring high throughput using Protobuf. |
| **MQTT** | Asynchronous | Minimal | **High** | Perfect for continuous streaming without blocking the client. |
| **REST** | Synchronous | Medium (JSON) | Medium | Sufficient for management tasks; slower due to HTTP/1.1 overhead. |

## How to Run

### Prerequisites
* Java 21 or higher
* Maven
* Docker & Docker Compose

### 1. Start Infrastructure
Start PostgreSQL and the Mosquitto Broker using Docker Compose:
```bash
docker-compose up -d
```

### 2. Run the Central Server
```bash
cd server
mvn spring-boot:run
```
* Server runs on port: 8081
* gRPC Server runs on port: 9090

### 3. Run the Admin Console
Open a new terminal to register sensors before starting clients:
```bash
cd admin-cli
mvn spring-boot:run
```

### 4. Run Simulated Clients
Open separate terminals for each client simulation:
* MQTT Client:
  ```bash
  cd client-mqtt
  mvn spring-boot:run
* gRPC Client:
  ```bash
  cd client-grpc
  mvn spring-boot:run
* REST Client:
  ```bash
  cd client-rest
  mvn spring-boot:run

### Author
Ana Valero Redondo
  * Computer Engineering Student @ UJA | Erasmus Scholar @ U. Évora
  * LinkedIn Profile: www.linkedin.com/in/anavalero-dev
