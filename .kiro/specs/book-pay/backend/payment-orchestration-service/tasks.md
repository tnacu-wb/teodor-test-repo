---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: none
---
# Implementation Plan: Payment Orchestration Service

## Overview
Phase 1 implementation creating the barebone microservice scaffold with Temporal integration, Spring Boot setup, health check endpoint, and basic project structure.

## Tasks
- [x] 1. Project Structure and Maven Configuration
  - [x] 1.1 Create project directory structure
    - Create `backend/book-pay/services/payment-orchestration-service/` directory
    - Set up Maven project structure: `src/main/java`, `src/test/java`, `src/main/resources`
    - Create hexagonal architecture package structure under `uk.co.whitbread.payment.orchestrator`
    - _Requirements: 1.1, 1.4, 5.1, 5.2_
  - [x] 1.2 Configure Maven POM
    - Create `pom.xml` inheriting from digital-monorepo-service-parent
    - Set `<artifactId>payment-orchestration-service</artifactId>`
    - Configure `<relativePath>../../../parents/service-parent/pom.xml</relativePath>`
    - Add standard Spring Boot, Temporal, and testing dependencies
    - Configure port 9112 and application properties
    - _Requirements: 1.2, 1.3, 1.6_
  - [x] 1.3 Add module declaration to parent POM
    - Update `backend/pom.xml` to include new module `book-pay/services/payment-orchestration-service`
    - Verify module inheritance and CI-friendly versioning
    - _Requirements: 1.3_

- [x] 2. Spring Boot Application Bootstrap
  - [x] 2.1 Create main application class
    - Create `PaymentOrchestrationServiceApplication.java` with `@SpringBootApplication`
    - Configure component scanning for hexagonal architecture packages
    - Add application startup logging
    - _Requirements: 1.1, 5.3_
  - [x] 2.2 Configure application properties
    - Create `application.yml` with port 9112 and context path `/payment-orchestrator/`
    - Set up profile-specific configurations (local, opera-dev, opera-qa, opera-perf)
    - Configure Spring application name and basic logging
    - _Requirements: 1.5, 6.1, 6.2, 6.3_
  - [x] 2.3 Set up configuration classes
    - Create `WebConfig` for REST configuration
    - Create `MonitoringConfig` for metrics and tracing
    - Create `TemporalConfig` for workflow service connection
    - _Requirements: 5.3, 6.4, 6.5_

- [x] 3. Temporal Workflow Integration
  - [x] 3.1 Add Temporal dependencies
    - Add Temporal Java SDK to Maven dependencies
    - Configure Temporal client and worker dependencies
    - _Requirements: 2.1_
  - [x] 3.2 Create workflow interfaces and implementations
    - Define `SecureFieldsPaymentWorkflow` interface and implementation stub
    - Define `MobileSdkPaymentWorkflow` interface and implementation stub
    - Create workflow activity interfaces for external service calls
    - _Requirements: 2.3_
  - [x] 3.3 Configure Temporal client and worker
    - Create `TemporalClientConfig` with connection settings
    - Configure workflow worker with proper task queue
    - Set up workflow registration and startup
    - _Requirements: 2.2, 2.4, 2.5_

- [x] 4. Hexagonal Architecture Implementation
  - [x] 4.1 Define domain ports
    - Create `PaymentOrchestrationPort` primary port interface
    - Create `TemporalWorkflowPort` primary port interface  
    - Create secondary ports: `DatatransPort`, `BasketPort`, `OperaPort`
    - _Requirements: 5.2_
  - [x] 4.2 Implement domain models
    - Create domain DTOs: `PaymentSession`, `PaymentChannel`, `PaymentStatus`
    - Create request/response models for API endpoints
    - Add proper validation annotations
    - _Requirements: 4.4, 5.2_
  - [x] 4.3 Create infrastructure adapters
    - Create REST client adapters (stub implementations)
    - Create Temporal workflow adapter
    - Set up proper dependency injection configuration
    - _Requirements: 5.2_

- [x] 5. REST API Implementation
  - [x] 5.1 Create payment controller
    - Implement `PaymentController` with placeholder endpoints
    - Add endpoints: `/api/payments/secure-fields`, `/api/payments/mobile-sdk`, `/api/payments/authorize`
    - Return 501 Not Implemented with proper error structure
    - _Requirements: 4.1, 4.2, 4.3_
  - [x] 5.2 Create webhook controller  
    - Implement `WebhookController` for Datatrans callbacks
    - Add endpoint: `/api/payments/webhooks/mobile-sdk`
    - Return 501 Not Implemented with acknowledgment structure
    - _Requirements: 4.1, 4.2_
  - [x] 5.3 Add OpenAPI documentation
    - Configure SpringDoc OpenAPI dependencies
    - Add OpenAPI annotations to controllers and DTOs
    - Set up Swagger UI at `/payment-orchestrator/swagger-ui.html`
    - _Requirements: 3.5, 4.5_

- [x] 6. Health Checks and Monitoring
  - [x] 6.1 Configure Spring Boot Actuator
    - Add Actuator dependencies and endpoints configuration
    - Expose health, info, metrics, and prometheus endpoints
    - Set health endpoint to show details
    - _Requirements: 3.1, 3.3_
  - [x] 6.2 Implement custom health indicators
    - Create `TemporalHealthIndicator` for workflow service connectivity
    - Create placeholder health indicators for external dependencies
    - Configure health check aggregation
    - _Requirements: 3.2_
  - [x] 6.3 Set up metrics and tracing
    - Configure Micrometer with Prometheus registry
    - Set up Brave distributed tracing configuration
    - Add custom metrics for payment operations
    - _Requirements: 3.3, 3.4_

- [x] 7. Build and Quality Configuration
  - [x] 7.1 Configure code quality tools
    - Add `google-checkstyle.xml` file
    - Configure Checkstyle Maven plugin with validation phase
    - Add coding convention rules dependency
    - _Requirements: 1.6_
  - [x] 7.2 Set up testing framework
    - Configure JaCoCo for code coverage with exclusions
    - Set up PIT mutation testing plugin
    - Configure test directory structure for unit, integration, and contract tests
    - _Requirements: 1.7, 5.5_
  - [x] 7.3 Configure OpenAPI documentation generation
    - Add SpringDoc OpenAPI Maven plugin
    - Configure API documentation generation during build
    - Set up integration test phase for documentation
    - _Requirements: 1.7, 3.5_

- [x] 8. Containerization and Deployment
  - [x] 8.1 Create Dockerfile
    - Create multi-stage Dockerfile for Java 25 runtime
    - Configure proper JAR execution and port exposure
    - Add health check configuration
    - _Requirements: 5.4_
  - [x] 8.2 Add infrastructure configuration
    - Create `infrastructure/` directory with deployment manifests
    - Add Docker Compose configuration for local development
    - Create environment-specific configuration templates
    - _Requirements: 6.1, 6.2_

- [x] 9. Testing Implementation
  - [x] 9.1 Unit tests
    - Create test classes for controllers with MockMVC
    - Test domain logic and port interfaces with Mockito
    - Validate DTO serialization and request/response handling
    - _Requirements: 5.5_
  - [x] 9.2 Integration tests
    - Create Spring Boot test configuration
    - Test REST endpoints with TestRestTemplate
    - Verify health endpoint functionality
    - _Requirements: 5.5_
  - [x] 9.3 Contract tests setup
    - Configure Spring Cloud Contract Maven plugin
    - Create base test class structure for contract verification
    - Set up contract testing framework
    - _Requirements: 5.5_

- [x] 10. Service Registration and Documentation
  - [x] 10.1 Create Tier 3 steering documentation
    - Create `.kiro/steering/backend/payment-orchestration-service/product.md`
    - Create `.kiro/steering/backend/payment-orchestration-service/structure.md`  
    - Create `.kiro/steering/backend/payment-orchestration-service/tech.md`
    - Add proper fileMatchPattern for the service directory
    - _Requirements: 5.1_
  - [x] 10.2 Create service README
    - Document service purpose, API endpoints, and local development setup
    - Include build commands, testing instructions, and deployment notes
    - Add architecture diagrams and integration points
    - _Requirements: 5.1_

## Notes
- This is Phase 1 focused on service infrastructure and scaffolding
- All payment endpoints return 501 Not Implemented as placeholder
- Full payment orchestration logic will be implemented in subsequent phases
- Temporal workflows are stubbed but properly configured for future implementation
- Configuration supports all required environments (local, opera-dev, opera-qa, opera-perf)
- Follows existing book-pay service patterns and hexagonal architecture principles

## Task Dependency Graph
```json
{ 
  "waves": [ 
    { "id": 0, "tasks": ["1.1", "1.2"] }, 
    { "id": 1, "tasks": ["1.3", "2.1", "3.1", "7.1"] },
    { "id": 2, "tasks": ["2.2", "4.1", "8.1"] },
    { "id": 3, "tasks": ["2.3", "3.2", "4.2", "5.1", "6.1"] },
    { "id": 4, "tasks": ["3.3", "4.3", "5.2", "6.2", "7.2", "9.1"] },
    { "id": 5, "tasks": ["5.3", "6.3", "7.3", "8.2", "9.2"] },
    { "id": 6, "tasks": ["9.3", "10.1", "10.2"] }
  ] 
}
```