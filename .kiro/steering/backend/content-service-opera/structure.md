---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/content-service-opera/**"
---

# Project Structure

## Architecture

This service follows a **layered architecture** with controller → service → client separation. Controllers handle HTTP requests, services orchestrate business logic and caching, and Feign clients manage outbound HTTP calls to AEM. Models are split between API response models and AEM-specific deserialization models.

```
src/main/java/uk/co/whitbread/contentservice/roomtypes/
├── client/
│   └── feign/                                # OpenFeign client interfaces and fallbacks
│       ├── AEMFeignClient.java               # Room types Feign client
│       ├── AEMFeignClientRates.java          # Rate classifications Feign client
│       ├── AEMFeignClientCookiePolicies.java # Cookie policies Feign client
│       ├── AEMBookingNotificationsFeignClient.java  # Booking notifications Feign client
│       ├── AEMFeignClientHystrixFallbackFactory.java
│       ├── AEMFeignClientRatesHystrixFallbackFactory.java
│       ├── AEMFeignCookiePolicyHystrixFallbackFactory.java
│       ├── AEMFeignClientBookingNotificationsHystrixFallbackFactory.java
│       └── FeignConfiguration.java           # Feign interceptor and config
├── config/
│   ├── properties/                           # Configuration property classes
│   │   ├── CacheConfigProperties.java
│   │   ├── CacheConfigProperty.java
│   │   ├── FeignClientProperties.java
│   │   └── FeignProperties.java
│   ├── CacheConfig.java                      # Redis cache manager configuration
│   └── WebConfig.java                        # Web/CORS configuration
├── controller/                               # REST controllers
│   ├── RoomTypesController.java              # GET /content/roomtypes
│   ├── RateClassificationsController.java    # GET /content/rateclassifications
│   ├── CookiePoliciesController.java         # GET /content/cookiepolicies
│   └── BookingNotificationsController.java   # GET /content/bookingnotifications
├── exception/                                # Custom exceptions
│   ├── FeignNonServerException.java          # Non-server errors (ignored by circuit breaker)
│   ├── NoDataFoundException.java
│   ├── NoRatesDataFoundException.java
│   ├── NoCookiePoliciesDataFoundException.java
│   └── NoBookingNotificationsDataFoundException.java
├── model/                                    # API response models
│   ├── aem/                                  # AEM deserialization models (content fragments)
│   │   ├── Root.java, ContentFragment.java   # Generic AEM response wrappers
│   │   ├── AEMRoomType.java, AEMRoomTypesResponse.java
│   │   ├── AEMRateClassifications.java, AEMRateClassificationsResponse.java
│   │   ├── AEMCookiePolicies.java, AEMCookiePoliciesResponse.java
│   │   ├── AEMBookingNotifications.java, AEMBookingNotificationsResponse.java
│   │   └── ... (room images, facilities, rate details)
│   ├── RoomType.java, RoomTypesResponse.java
│   ├── RateClassification.java, RateClassificationResponse.java
│   ├── CookiePolicies.java, CookiePoliciesResponse.java
│   ├── BookingNotifications.java, BookingNotificationsResponse.java
│   ├── BrandCode.java, CountryCode.java, LanguageCode.java, SubBrandCode.java
│   └── RateCode.java, Facility.java
├── service/                                  # Business logic layer
│   ├── client/                               # AEM service wrappers (invoke Feign + convert)
│   │   ├── AEMRoomTypesService.java
│   │   ├── AEMRateClassificationsService.java
│   │   ├── AEMCookiePoliciesService.java
│   │   └── AEMBookingNotificationsService.java
│   ├── RoomTypesService.java                 # Room types orchestration + caching
│   ├── RateClassificationService.java        # Rate classification orchestration + caching
│   ├── CookiePoliciesService.java            # Cookie policies orchestration + caching
│   └── BookingNotificationsService.java      # Booking notifications orchestration + caching
├── util/
│   └── AEMResponseConverter.java             # AEM response transformation utilities
└── Application.java                          # Spring Boot entry point
```

## Key Conventions

- **Feign clients per content domain** — Each AEM content type (rooms, rates, cookies, notifications) has its own Feign client interface
- **Fallback factories** — Named `*HystrixFallbackFactory` (legacy naming) but use Resilience4j circuit breakers
- **Two-level service pattern** — Top-level services handle caching; `service/client/` classes handle AEM invocation and response conversion
- **AEM model separation** — Raw AEM response models live in `model/aem/`; cleaned API response models in `model/`
- **Models use Lombok** — `@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor` for DTOs
- **Cache names are property-driven** — Defined in `application.yml` under `content-service.cache.*`
- **Basic auth for AEM** — Username/password injected via environment variables, applied in `FeignConfiguration`
- **Contract tests** — Spring Cloud Contract stubs in `src/test/resources/contracts/` for consumer-driven testing
- **Port** — Default application port is `9058`
