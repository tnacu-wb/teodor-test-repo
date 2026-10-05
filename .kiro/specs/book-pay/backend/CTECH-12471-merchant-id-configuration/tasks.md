---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-12471
---
# Implementation Plan: Dynamic Merchant ID Configuration

## Overview

Replace hardcoded Datatrans merchant IDs with dynamic resolution based on hotel codes, implementing different strategies for production and sandbox environments using Spring profiles.

## Tasks

- [x] 1. Create merchant ID resolution infrastructure
  - [x] 1.1 Create domain port interface
    - Create `MerchantIdResolver` interface in `domain/ports/secondary/`
    - Define `String resolveMerchantId(String hotelCode)` method
    - Add comprehensive Javadoc documentation
    - _Requirements: 1.1, 1.2, 1.3_
  - [x] 1.2 Create configuration properties class
    - Create `MerchantIdProperties` in `infrastructure/config/`
    - Add `@ConfigurationProperties(prefix = "integrations.datatrans.merchant-id")`
    - Define `prefix`, `defaultMerchantId`, and `provisionedHotels` properties
    - Add `@PostConstruct` validation method
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_
  - [x] 1.3 Update application.yml configuration
    - Add `integrations.datatrans.merchant-id` configuration section
    - Set default values: prefix="deWB-", default-merchant-id="deWB-default"
    - Configure provisioned-hotels list with HARHOR and GRESOU
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

- [x] 2. Implement profile-based merchant ID resolvers
  - [x] 2.1 Create production merchant ID resolver
    - Create `ProductionMerchantIdResolver` in `infrastructure/adapter/`
    - Annotate with `@Profile("opera-prod")`
    - Implement direct hotel code to merchant ID mapping (`"deWB-" + hotelCode`)
    - Add logging for resolved merchant IDs
    - _Requirements: 2.1, 2.2, 2.3_
  - [x] 2.2 Create sandbox merchant ID resolver
    - Create `SandboxMerchantIdResolver` in `infrastructure/adapter/`
    - Annotate with `@Profile("!opera-prod")`
    - Implement provisioned hotels list lookup logic
    - Use default merchant ID for non-provisioned hotels
    - Add logging for provisioned vs default resolution
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6_

- [x] 3. Integrate merchant ID resolution with Temporal activities
  - [x] 3.1 Update PaymentActivities interface
    - Add `String resolveMerchantId(String hotelCode)` method
    - Annotate with `@ActivityMethod`
    - Add Javadoc documentation
    - _Requirements: 5.1, 5.3_
  - [x] 3.2 Update PaymentActivitiesImpl
    - Inject `MerchantIdResolver` via constructor
    - Implement `resolveMerchantId` method to delegate to port
    - Add activity execution logging
    - _Requirements: 5.2_

- [x] 4. Update payment workflows to use dynamic merchant IDs
  - [x] 4.1 Update SecureFieldsPaymentWorkflowImpl
    - Replace hardcoded `"deWB-ABEAIB"` with `activities.resolveMerchantId(reservation.hotelId())`
    - Position call after reservation retrieval
    - Remove TODO comment about deriving merchantId
    - Update `DatatransSecureFieldsRequest` construction
    - _Requirements: 6.1, 6.2, 6.3_
  - [x] 4.2 Update MobileSdkPaymentWorkflowImpl  
    - Replace hardcoded `"deWB-ABEAIB"` with `activities.resolveMerchantId(reservation.hotelId())`
    - Position call after reservation retrieval
    - Remove TODO comment about deriving merchantId
    - Update `DatatransMobileSdkRequest` construction
    - _Requirements: 7.1, 7.2, 7.3_

- [x] 5. Add test coverage
  - [x] 5.1 Unit test ProductionMerchantIdResolver
    - Test direct hotel code mapping with various inputs
    - Test null and empty hotel code handling
    - Test merchant ID format validation
    - _Requirements: 2.1, 2.2, 2.3, 8.1, 8.2_
  - [x] 5.2 Unit test SandboxMerchantIdResolver
    - Test provisioned hotel codes return specific merchant IDs
    - Test non-provisioned hotel codes return default merchant ID
    - Test empty provisioned hotels list
    - Test null and empty hotel code handling
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 8.3_
  - [x] 5.3 Unit test MerchantIdProperties validation
    - Test validation fails for null/empty prefix
    - Test validation fails for null/empty default merchant ID
    - Test successful validation with valid properties
    - _Requirements: 4.1, 4.2, 4.3_
  - [x] 5.4 Test workflow integration
    - Mock `PaymentActivities.resolveMerchantId()` in workflow tests
    - Verify workflows use resolved merchant ID in Datatrans requests
    - Test both SecureFields and MobileSDK workflows
    - _Requirements: 6.1, 6.2, 6.3, 7.1, 7.2, 7.3_

## Notes

- **Profile-based resolution pattern**: Following established pattern from `threec-payment-service-opera` with `ProductionEMerchantService` and `DefaultEMerchantService`
- **Configuration over hardcoding**: Sandbox hotel list is configurable to support future provisioning changes without code deployment
- **Temporal activity approach**: Since reservation data (including hotelId) is retrieved inside the workflow as an activity, merchant ID resolution must also be an activity rather than pre-computed in the workflow adapter
- **Backwards compatibility**: Changes are internal to the service - no external API contract changes required
- **Error handling**: Null/empty hotel codes gracefully fall back to default merchant ID to prevent payment failures
- **Logging strategy**: Debug-level logging for normal resolution, warn-level for fallback scenarios

## Task Dependency Graph

```json
{
  "waves": [
    {
      "id": 0,
      "tasks": ["1.1", "1.2", "1.3"]
    },
    {
      "id": 1, 
      "tasks": ["2.1", "2.2"]
    },
    {
      "id": 2,
      "tasks": ["3.1", "3.2"]
    },
    {
      "id": 3,
      "tasks": ["4.1", "4.2"]
    },
    {
      "id": 4,
      "tasks": ["5.1", "5.2", "5.3", "5.4"]
    }
  ]
}
```
