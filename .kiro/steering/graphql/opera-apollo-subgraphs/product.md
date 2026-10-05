---
inclusion: fileMatch
fileMatchPattern: "graphql/**"
---

# Opera Apollo Subgraphs — Product Context

## Purpose

A federated GraphQL subgraph layer that adapts backend REST APIs for the Premier Inn
web and mobile frontends. The service runs as a single Apollo Server 5 application
containing 38 subgraphs, each mapping a backend domain to a portion of the federated
supergraph consumed by the Apollo Router.

## Core Responsibilities

- Translate REST API responses into GraphQL types defined per-subgraph schema
- Compose a federated supergraph with the Apollo Router (gateway)
- Provide a unified query interface for the Premier Inn web (Next.js SSR + CSR) and
  native mobile applications
- Handle error mapping, retry logic, and request correlation across downstream services

## Key Integrations

| Integration | Direction | Protocol | Purpose |
|-------------|-----------|----------|---------|
| Apollo Router | Inbound | GraphQL (federation) | Receives subgraph queries from the gateway |
| Backend REST APIs | Outbound | HTTP/REST (Axios) | Fetches domain data from Java microservices |
| OpenTelemetry Collector | Outbound | OTLP/HTTP | Exports distributed traces |
| Prometheus | Outbound | HTTP (scrape) | Exposes application metrics |

## Domain Context

The subgraphs align with backend service domains:

- **Identity** — account, login, registration, company, employee, card management
- **Discover & Search** — hotel information, availability, content, reviews
- **Book & Pay** — basket, payment, reservations, table bookings, donations
- **Manage & Modify** — dashboard, booking amendments, cancellations, check-in
- **Arrive Stay & Leave** — wallet passes, digital keys

Each subgraph folder under `src/apollo/subgraphs/<name>/` contains its own schema,
resolvers, and data sources (Axios client wrappers hitting one or more backend services).
