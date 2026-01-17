# Mini DMS – Orchestration Design

 Document Management System around modern Java microservices.  
The focus of this repository is on the **Orchestration Design Pattern**: one service coordinates the full document lifecycle across multiple backend services.

> Tech focus: Spring Boot, Spring Cloud, Eureka, API Gateway, Keycloak, Resilience4j, Messaging (RabbitMQ/Kafka), Observability (Prometheus), and Kubernetes.

---

## 1. Project Goals

This project is designed as a practical design to:

- demonestrate **microservice architecture** with Java & Spring.
- Demonstrate the **orchestration** approach for cross-service workflows.
- Explore **service discovery, API gateway, and centralized auth**.
- Integrate **infrastructure components** like message brokers, storage, and monitoring.
- Prepare for **real-world system design discussions** (e.g. DMS, search, notifications).

---

## 2. High-Level Architecture

The system models a simplified **Document Management System**:

- Users can upload documents.
- Metadata is stored in a dedicated service.
- Binary content is stored in a storage service.
- Documents are indexed for search.
- Notifications are sent to interested parties.
- All steps are coordinated by an **orchestrator**.

### Main Components

- **auth/**  
  Authentication & authorization (e.g. Keycloak integration or dedicated auth service).

- **gatewayserver/**  
  API Gateway (Spring Cloud Gateway) – single entry point for all clients.

- **eurekaserver/**  
  Service Discovery (Eureka) – all microservices register here.

- **metadata-service/**  
  Manages document metadata (title, owner, tags, timestamps, etc.).

- **storage-service/**  
  Handles physical document storage (e.g. object storage / file system / S3-compatible).

- **search-service/**  
  Indexes documents and provides full-text search capabilities.

- **notification/**  
  Sends notifications (e-mail, messaging, etc.) about document events.

- **infra/**  
  Infrastructure configuration (e.g. containers for DB, broker, monitoring).

- **k8/**  
  Kubernetes manifests for running the system in a cluster.

---

## 3. Orchestration Design Pattern

Instead of each service emitting events and reacting independently (**choreography**), this project uses **orchestration**:

- A central **orchestrator** (e.g. inside `metadata-service` or a dedicated orchestration component) controls the full workflow.

### Example: Document Upload Flow

1. Client calls `POST /documents` via the **gateway**.
2. The request is routed to the **orchestrator**.
3. The orchestrator:
   - Calls **storage-service** to store the binary file.
   - On success, calls **metadata-service** to persist document metadata.
   - Triggers **search-service** to index the document.
   - Notifies **notification** service to inform subscribers.
4. The orchestrator aggregates the results and returns a single response to the client.

Benefits:

- One place to see and control the full process.
- Easier reasoning about error handling and retries.
- Good fit for long-running or multi-step business workflows.

---

## 4. Tech Stack

### Backend

- Java (modern LTS version)
- Spring Boot (REST APIs, configuration, dependency injection)
- Spring Cloud:
  - Eureka (Service Discovery)
  - Spring Cloud Gateway (API Gateway)
  - OpenFeign / RestTemplate (inter-service communication)
- Resilience4j (circuit breaker, retries, timeouts)
- Spring Security (integration with Keycloak or other IdP)

### Data & Messaging

- Relational Database (e.g. PostgreSQL) for metadata
- Object Storage for files (e.g. MinIO/S3, or file system)
- Search Engine (e.g. Elasticsearch/OpenSearch) for indexing
- Message Broker (RabbitMQ or Kafka) for async communication (optional)

### Infrastructure & Ops

- Docker / Containers
- Kubernetes manifests (`k8/`)
- Prometheus (metrics)
- (Optionally) Grafana / Loki for dashboards and logs

---

## 5. Getting Started

> **Note:** The exact commands and versions may depend on your local setup.  
> Please adjust the steps according to your IDE, JDK, and tooling.

### Prerequisites

- JDK 17+ installed
- Maven or Gradle
- Docker (for infra components)
- Optional: Kubernetes cluster (kind, k3d, Minikube, or a real cluster)

### 5.1 Clone the Repository

```bash
git clone https://github.com/Zuhair1979/mini-dms-Orchestration-Design.git
cd mini-dms-Orchestration-Design
