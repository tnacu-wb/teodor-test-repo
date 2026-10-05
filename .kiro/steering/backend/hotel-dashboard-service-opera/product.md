---
inclusion: fileMatch
fileMatchPattern: "backend/manage-modify/services/hotel-dashboard-service-opera/**"
---

# Product Overview

Generates the home screen dashboard content for Premier Inn mobile apps. Aggregates data from multiple downstream services (CDH adapter, hotel-account-service, hotel-info-service) using WebFlux for non-blocking orchestration. API models are generated via OpenAPI.

## Core Responsibilities

- Serve personalised dashboard content for Premier Inn app home screen
- Orchestrate non-blocking calls to CDH adapter, hotel-account-service, hotel-info-service
- Aggregate and transform downstream responses into dashboard payload
- Generate API models via OpenAPI code generation

## Consumer Services / Integration Points

- Called by Premier Inn mobile apps for home screen content
- Depends on CDH adapter, hotel-account-service, hotel-info-service
- Port: 9056
