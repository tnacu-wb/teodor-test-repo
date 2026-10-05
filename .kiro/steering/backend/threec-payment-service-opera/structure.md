---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/threec-payment-service-opera/**"
---

# Project Structure

## Architecture

This service uses a **reactive layered architecture** with Spring WebFlux functional routing. Unlike hexagonal services in the monorepo, it uses a service/handler pattern with functional router definitions rather than annotated controllers.

```
src/main/java/uk/co/whitbread/payments/
├── client/                            # Outbound WebClient adapters
│   ├── BasketClient.java              # Webhook callbacks to basket-service
│   ├── HotelBookingClient.java        # Make-booking calls post-payment
│   ├── HotelCardClient.java           # Save card details
│   └── ThreeCPaymentClient.java       # 3C Web2Pay gateway calls
├── config/                            # Spring configuration
│   ├── DynamoDBConfig.java            # DynamoDB client/table setup
│   ├── Paypal*.java                   # PayPal/Braintree configuration
│   ├── WebConfig.java                 # WebFlux configuration
│   ├── WebfluxSecurityConfig.java     # Security filter chain
│   └── TemplateConfiguration.java     # Thymeleaf for XML templates
├── converters/                        # Request/response mappers for 3C API
│   ├── InitialiseRequestMapper.java   # iPage initialisation requests
│   ├── PaypalTransactionRequestMapper.java
│   ├── ReverseByTransactionRequestMapper.java
│   └── TokenRequestMapper.java        # Token create/update requests
├── exception/                         # Global error handling
│   ├── GlobalErrorWebExceptionHandler.java  # Reactive error handler
│   └── PaymentServiceException.java   # Domain-specific exceptions
├── handlers/                          # WebFlux request handlers (use-case logic)
│   ├── CardHandler.java               # Saved card operations
│   ├── PaymentsHandler.java           # Payment initiation and webhooks
│   ├── ReconciliationHandler.java     # Settlement reconciliation
│   ├── RefundHandler.java             # Refund processing
│   ├── ScaHandler.java                # 3D Secure / SCA flows
│   └── TokensHandler.java            # Token create/update
├── mapper/                            # MapStruct mappers
├── model/                             # Domain models
│   ├── booking/                       # Booking-related models
│   ├── card/                          # Card detail models
│   ├── dynamo/                        # DynamoDB entity models
│   ├── eckoh/                         # Eckoh integration models
│   ├── feature/                       # Feature flag models
│   └── threec/                        # 3C gateway request/response models
├── properties/                        # Configuration properties beans
│   ├── AccountConfigProperties.java   # Provider account routing config
│   ├── ThreeCProperties.java          # 3C gateway connection settings
│   └── DynamoDBProperties.java        # DynamoDB table/connection config
├── repository/                        # DynamoDB persistence
│   └── PaymentRepository.java         # Payment state CRUD
├── routes/                            # WebFlux functional router definitions
│   ├── CardRouter.java                # /cards/* routes
│   ├── PaymentsRouter.java            # /payments/* routes
│   ├── ReconciliationRouter.java      # /reconciliation/* routes
│   ├── RefundRouter.java              # /refunds/* routes
│   ├── ScaRouter.java                 # /sca/* routes
│   └── TokensRouter.java             # /tokens/* routes
├── service/                           # Business logic services
│   ├── impl/                          # Service implementations
│   ├── webhook/                       # Webhook processing logic
│   ├── PaymentService.java            # Core payment orchestration
│   ├── RefundService.java             # Refund orchestration
│   ├── TokensService.java            # Token lifecycle
│   └── PaypalTokenService.java        # PayPal client token management
├── util/                              # Utility classes
│   └── mapper/                        # Additional mapping utilities
├── validation/                        # Custom validators
│   ├── PaymentRequestValidator.java   # Payment request validation
│   └── RefundRequestValidator.java    # Refund request validation
└── Application.java
```

## Key Conventions

- **Reactive stack** — Spring WebFlux with Netty, returns `Mono<T>` and `Flux<T>`
- **Functional routing** — `RouterFunction<ServerResponse>` in `routes/` package, handlers in `handlers/`
- **XML templates** — Thymeleaf renders 3C request XML payloads from templates
- **Provider account routing** — `ProviderAccountFactory` selects merchant config by booking type, currency, channel, and payment sub-type
- **Models use Lombok** — `@Builder`, `@Data`, `@Value`
- **DynamoDB persistence** — DynamoDbEnhancedClient with TTL-based record expiry
- **Global reactive error handling** — `GlobalErrorWebExceptionHandler` extends `AbstractErrorWebExceptionHandler`
- **WebClient for HTTP** — non-blocking outbound calls to 3C, basket, hotel-booking, hotel-card

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — MockWebServer (OkHttp3) for HTTP stubs
- **Mutation testing** — Pitest with standard mutators
- **Coverage** — JaCoCo (excludes models, config, mappers, exceptions)
