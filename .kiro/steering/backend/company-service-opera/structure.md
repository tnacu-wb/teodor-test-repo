---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/company-service-opera/**"
---

# Project Structure

## Architecture

This service follows a **layered architecture** with clear separation between controllers, services, clients, and models. It uses Spring MVC for REST endpoints and OpenFeign for outbound HTTP calls to the hotel-account service. The CDH integration is handled via the shared `commons-cdh-lib` library with dedicated service classes per domain concern.

```
src/main/java/uk/co/whitbread/company/
├── CompanyServiceApplication.java       # Spring Boot entry point
├── cache/                               # Redis caching providers
│   └── CustomerCacheProvider.java       # Company profile cache (24h TTL)
├── client/                              # Feign clients for external services
│   ├── model/                           # Client-specific DTOs
│   ├── HotelAccountClient.java          # Feign client to hotel-account service
│   ├── HotelAccountClientFallbackFactory.java  # Circuit breaker fallback
│   └── HotelAccountErrorDecodeConfig.java      # Feign error decoder
├── config/                              # Spring configuration classes
│   ├── JacksonConfig.java              # JSON serialization settings
│   ├── SecurityConfig.java             # Auth0 multi-tenant JWT security
│   ├── UnleashConfig.java              # Feature flag configuration
│   └── WebConfig.java                  # Web/CORS configuration
├── controller/                          # REST API controllers
│   ├── CompaniesController.java         # /companies endpoints (list, search, check)
│   ├── CompanyController.java           # /company/{id} endpoints (details, employees, allowances, alerts)
│   └── CompanyQuestionController.java   # /company/{id}/questions endpoints (user & business questions)
├── exceptions/                          # Custom exception classes
│   ├── AnswerTypeNotFoundException.java
│   ├── CompanyNotFoundException.java
│   ├── EmployeeNotFoundException.java
│   ├── ExceededLimitOfQuestionsException.java
│   ├── HotelAccountClientException.java
│   ├── InvalidEmailException.java
│   ├── QuestionNotFoundException.java
│   └── UnsupportedQuestionIdException.java
├── filters/                             # CDH request/response filters
│   ├── CdhEmployeeFilter.java          # Employee data filtering logic
│   └── CdhSuperAccessLevelFilter.java  # Super-admin access level filtering
├── mapper/                              # MapStruct mappers
│   ├── AddressMapper.java
│   ├── BookingAlertsMapper.java
│   ├── CellCodeMapper.java
│   ├── CompanyMapper.java
│   ├── EmployeeMapper.java
│   ├── PaymentCardMapper.java
│   ├── PriceMapper.java
│   └── QuestionMapper.java
├── model/                               # Request/response DTOs and domain models
│   ├── feature/                         # Feature flag related models
│   ├── Company.java, CompanyDetails.java, CompanySummary.java
│   ├── Employee.java, EmployeeStatus.java, AccessLevel.java
│   ├── BookingAllowances.java, BookingAlerts.java, BookingAlertsFrequency.java
│   ├── UserDefinedQuestion.java, BusinessQuestions.java, ManagementInformationQuestion.java
│   ├── PaymentCard.java, PaymentDetails.java, Price.java, RatePlan.java
│   └── ... (additional DTOs)
├── properties/                          # Configuration property classes
│   ├── Auth0Properties.java            # Auth0 tenant configuration
│   └── UpsellItemAllowancesProperties.java  # Upsell item codes mapping
├── service/                             # Business logic layer
│   ├── cdh/                             # CDH-backed service implementations
│   │   ├── CdhAuthorizationService.java       # CDH authorization operations
│   │   ├── CdhBookingAlertsService.java       # Booking alerts via CDH
│   │   ├── CdhBookingAllowancesService.java   # Booking allowances via CDH
│   │   ├── CdhCompanyDetailsService.java      # Company details via CDH
│   │   ├── CdhCompanyQuestionService.java     # Management questions via CDH
│   │   └── CdhService.java                    # Core CDH orchestration service
│   ├── Auth0Service.java               # Auth0 management API operations
│   └── EmailService.java               # Azure email sending
├── utils/                               # Utility classes
│   ├── EmailValidator.java             # Email format validation
│   ├── EmployeeHeaderDetails.java      # Extract employee info from headers
│   ├── EnumConverter.java              # Enum conversion utilities
│   ├── FilterUtils.java                # CDH response filter helpers
│   ├── HeadersUtil.java                # HTTP header extraction utilities
│   ├── ManagementQuestionBuilder.java  # Question model construction
│   ├── PaymentCardMasker.java          # PCI-compliant card masking
│   ├── QuestionUtils.java              # Question validation/transformation
│   ├── RegistrationQuestionsTransformer.java  # Registration Q&A transformations
│   └── UpsellItemsAllowedUtils.java    # Upsell item allowance calculations
└── validation/                          # Feature-flagged validators
    └── CompanyNameSwitchImpl.java       # Company name validation (Unleash-controlled)
```

## Key Conventions

- **Controllers are thin** — delegate immediately to CDH service layer
- **CDH service classes per concern** — separate classes for booking alerts, allowances, questions, details
- **Feign client with fallback factory** — circuit breaker pattern for hotel-account calls
- **Models use Lombok** — `@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`
- **MapStruct for object mapping** — interface-based mappers between CDH responses and API models
- **Filter classes for CDH responses** — dedicated filters for employee and access-level data transformations
- **Feature flags via Unleash** — company name validation and CDH API deprecation toggles
- **Payment card masking** — PCI-compliant masking in `PaymentCardMasker` before returning card data

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Controller tests** — MockMvc-based with `TestSecurityConfig` for auth bypass
- **Fixtures** — `CompanyFixture` and `EmployeeFixture` for reusable test data
- **Random Beans** — Test data generation via `io.github.benas:random-beans`
- **PIT mutation testing** — Excludes models, config, mappers, constants, exceptions, properties
