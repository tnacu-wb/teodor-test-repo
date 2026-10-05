---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: none
---
# Requirements Document

## Introduction
Building a barebone Payment Orchestration Service microservice as Phase 1 infrastructure for the new card payment journey across Secure Fields (web) and Mobile SDK Version 4 (native apps). This service will coordinate payment transactions through Datatrans, manage payment workflows using Temporal, and integrate with Basket, OPERA, and downstream services. Based on the end-to-end payment architecture design document.

## Glossary
- **Payment Orchestrator**: Backend service coordinating the payment flow between client applications and payment providers
- **Datatrans**: Payment gateway hosting Secure Fields iframe (web) and Mobile SDK version 4 (native)
- **Temporal**: Workflow orchestration platform for managing payment workflows and retries
- **Basket**: Service holding reservation/basket state for bookings being paid
- **OPERA**: Property/hospitality platform receiving deposit/folio postings

## Requirements

### Requirement 1: Service Bootstrap and Infrastructure
**User Story:** As a developer, I want a properly structured Spring Boot microservice, so that I can implement payment orchestration functionality following monorepo conventions.

#### Acceptance Criteria
1. THE service SHALL follow the standard book-pay microservice structure with hexagonal architecture
2. THE service SHALL use Java 25 and Spring Boot 4.0.7 with Spring Cloud 2025.1.1
3. THE service SHALL inherit from the digital-monorepo parent POM with proper module declaration
4. THE service SHALL include standard dependencies: Spring Web, Spring Boot Actuator, MapStruct, Lombok, SpringDoc OpenAPI
5. THE service SHALL use port 9112 and context path `/payment-orchestrator/`
6. THE service SHALL include google-checkstyle.xml configuration and coding convention rules
7. THE service SHALL have proper Maven build configuration with JaCoCo, PIT testing, and OpenAPI documentation generation

### Requirement 2: Temporal Workflow Integration
**User Story:** As a payment orchestrator, I want Temporal workflow capabilities, so that I can manage complex payment flows with retries and failure handling.

#### Acceptance Criteria
1. THE service SHALL include Temporal Java SDK dependencies
2. THE service SHALL configure Temporal client connection to workflow service
3. THE service SHALL define basic workflow and activity interfaces for payment processing
4. THE service SHALL implement workflow worker configuration
5. THE service SHALL provide configuration properties for Temporal connection settings

### Requirement 3: Health Check and Monitoring
**User Story:** As an operations team member, I want comprehensive health checks, so that I can monitor service availability and dependencies.

#### Acceptance Criteria
1. THE service SHALL expose Spring Boot Actuator health endpoint at `/payment-orchestrator/actuator/health`
2. THE service SHALL include custom health indicators for critical dependencies (Temporal, external services)
3. THE service SHALL provide Micrometer metrics with Prometheus registry
4. THE service SHALL include distributed tracing configuration with Brave
5. THE service SHALL expose OpenAPI documentation at `/payment-orchestrator/swagger-ui.html`

### Requirement 4: Payment API Endpoint Structure
**User Story:** As a frontend/mobile developer, I want well-defined payment API endpoints, so that I can integrate payment flows.

#### Acceptance Criteria
1. THE service SHALL define REST controller structure for payment endpoints
2. THE service SHALL implement placeholder endpoints for:
   - `POST /api/payments/secure-fields`
   - `POST /api/payments/mobile-sdk`  
   - `POST /api/payments/authorize`
   - `POST /api/payments/webhooks/mobile-sdk`
3. THE service SHALL return appropriate HTTP status codes (501 Not Implemented for Phase 1)
4. THE service SHALL include proper request/response DTOs with validation annotations
5. THE service SHALL document all endpoints with OpenAPI annotations

### Requirement 5: Project Structure and Package Organization
**User Story:** As a developer, I want a well-organized codebase, so that I can easily navigate and extend the service.

#### Acceptance Criteria
1. THE service SHALL follow hexagonal architecture with domain, infrastructure, and application layers
2. THE service SHALL organize packages as:
   - `domain/` - business logic and port interfaces
   - `infrastructure/` - Spring adapters and external integrations  
   - `application/` - REST controllers and configuration
3. THE service SHALL separate configuration classes for different concerns (web, temporal, monitoring)
4. THE service SHALL include proper Docker containerization with Dockerfile
5. THE service SHALL have comprehensive test structure with unit, integration, and contract test directories

### Requirement 6: Configuration and Profiles
**User Story:** As a DevOps engineer, I want environment-specific configuration, so that I can deploy the service across different environments.

#### Acceptance Criteria
1. THE service SHALL support Spring profiles: local, opera-dev, opera-qa, opera-perf
2. THE service SHALL externalize configuration through application properties files
3. THE service SHALL include configuration for:
   - Database connection settings (for future persistence)
   - Temporal workflow service connection
   - External service endpoints (Datatrans, Basket, OPERA)
   - Security and authentication settings
4. THE service SHALL use environment variables for sensitive configuration values
5. THE service SHALL validate required configuration properties on startup