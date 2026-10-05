# Infrastructure — Payment Orchestration Service

## Local Development with Docker Compose

The `docker-local/docker-compose.yml` provides the dependencies needed to run the service locally:

- **postgresql** — PostgreSQL database for Temporal persistence
- **temporal** — Temporal workflow engine (PostgreSQL-backed, auto-setup)
- **temporal-ui** — Web UI for inspecting Temporal workflows (http://localhost:8080)

The service itself runs outside Docker via Maven — see below.

### Starting dependencies

```bash
cd backend/book-pay/services/payment-orchestration-service/infrastructure/docker-local
docker compose up -d
```

### Starting the service

Once Temporal is up, start the service:

```bash
cd backend
./mvnw spring-boot:run -pl book-pay/services/payment-orchestration-service \
  -Dspring.profiles.active=local
```

### Accessing services

| Service | URL |
|---------|-----|
| Payment Orchestration API | http://localhost:9200/api/payments/init |
| Health Check | http://localhost:9200/payment-orchestrator/actuator/health |
| Swagger UI | http://localhost:9200/swagger-ui.html |
| Temporal UI | http://localhost:8080 |
| Temporal gRPC | localhost:7233 |

### Stopping dependencies

```bash
cd backend/book-pay/services/payment-orchestration-service/infrastructure/docker-local
docker compose down
```

To remove volumes as well:

```bash
docker compose down -v
```
