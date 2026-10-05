---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-12471
---
# Requirements Document

## Introduction

The Payment Orchestration Service currently hardcodes the Datatrans merchant ID as `"deWB-ABEAIB"` in both SecureFieldsPaymentWorkflowImpl and MobileSdkPaymentWorkflowImpl. This ticket implements dynamic merchant ID resolution based on hotel codes from reservations, using different strategies for production and sandbox environments.

## Glossary

- **Merchant ID**: Datatrans-assigned identifier for a hotel merchant account (format: `deWB-{hotelCode}`)
- **Hotel Code**: Unique identifier for a Premier Inn property (e.g. `HARHOR`, `GRESOU`) 
- **Production Environment**: Live environment where all hotels have provisioned merchant IDs
- **Sandbox Environment**: Test environment with limited provisioned merchant IDs, requiring fallback to defaults

## Requirements

### Requirement 1: Dynamic Merchant ID Resolution Interface
**User Story:** As a payment workflow, I want to resolve the correct Datatrans merchant ID for a hotel, so that payments are processed through the appropriate merchant account.

#### Acceptance Criteria
1. THE system SHALL provide a `MerchantIdResolver` port interface in `domain/ports/secondary/`
2. THE `MerchantIdResolver` SHALL expose method `String resolveMerchantId(String hotelCode)`
3. THE method SHALL return the full merchant ID string (e.g. `"deWB-HARHOR"`)

### Requirement 2: Production Merchant ID Resolution
**User Story:** As a production payment workflow, I want merchant IDs constructed directly from hotel codes, so that each hotel uses its dedicated merchant account.

#### Acceptance Criteria
1. THE production implementation SHALL use profile `@Profile("opera-prod")`
2. THE production resolver SHALL return `"deWB-" + hotelCode` for any hotel code
3. THE production resolver SHALL NOT require any external configuration

### Requirement 3: Sandbox Merchant ID Resolution
**User Story:** As a sandbox payment workflow, I want merchant IDs resolved through a configured allowlist, so that testing works with Datatrans' limited sandbox merchant accounts.

#### Acceptance Criteria
1. THE sandbox implementation SHALL use profile `@Profile("!opera-prod")`
2. THE sandbox resolver SHALL check hotel codes against a configured list of provisioned hotels
3. IF the hotel code is in the provisioned list THEN THE resolver SHALL return `"deWB-" + hotelCode`
4. IF the hotel code is NOT in the provisioned list THEN THE resolver SHALL return the configured default merchant ID
5. THE default merchant ID SHALL be `"deWB-default"`
6. THE provisioned hotels list SHALL be configurable in `application.yml`

### Requirement 4: Configuration Properties
**User Story:** As a system operator, I want merchant ID resolution configurable via application properties, so that sandbox merchant lists can be updated without code changes.

#### Acceptance Criteria
1. THE system SHALL define a `MerchantIdProperties` configuration class
2. THE properties SHALL include `prefix` (default: `"deWB-"`)
3. THE properties SHALL include `defaultMerchantId` (default: `"deWB-default"`)  
4. THE properties SHALL include `provisionedHotels` (list, e.g. `["HARHOR", "GRESOU"]`)
5. THE configuration root SHALL be `integrations.datatrans.merchant-id`

### Requirement 5: Temporal Activity Integration
**User Story:** As a payment workflow, I want merchant ID resolution available as a Temporal activity, so that I can resolve merchant IDs after retrieving reservation data.

#### Acceptance Criteria
1. THE system SHALL add `String resolveMerchantId(String hotelCode)` method to `PaymentActivities` interface
2. THE `PaymentActivitiesImpl` SHALL inject `MerchantIdResolver` port and delegate to it
3. THE activity method SHALL be annotated with `@ActivityMethod`

### Requirement 6: Secure Fields Workflow Integration
**User Story:** As a Secure Fields payment workflow, I want to use dynamically resolved merchant IDs, so that payments route through the correct hotel merchant account.

#### Acceptance Criteria
1. THE `SecureFieldsPaymentWorkflowImpl` SHALL call `activities.resolveMerchantId(reservation.hotelId())` after retrieving reservation data
2. THE workflow SHALL replace the hardcoded `"deWB-ABEAIB"` with the resolved merchant ID
3. THE resolved merchant ID SHALL be passed to `DatatransSecureFieldsRequest`

### Requirement 7: Mobile SDK Workflow Integration  
**User Story:** As a Mobile SDK payment workflow, I want to use dynamically resolved merchant IDs, so that payments route through the correct hotel merchant account.

#### Acceptance Criteria
1. THE `MobileSdkPaymentWorkflowImpl` SHALL call `activities.resolveMerchantId(reservation.hotelId())` after retrieving reservation data
2. THE workflow SHALL replace the hardcoded `"deWB-ABEAIB"` with the resolved merchant ID
3. THE resolved merchant ID SHALL be passed to `DatatransMobileSdkRequest`

### Requirement 8: Merchant ID Prefix Consistency
**User Story:** As a payment processor, I want all merchant IDs to use the Datatrans-standard prefix, so that merchant account routing works correctly.

#### Acceptance Criteria
1. THE merchant ID prefix SHALL always be `"deWB-"` (Datatrans convention for Whitbread)
2. THE production implementation SHALL use this prefix when constructing merchant IDs
3. THE sandbox default merchant ID SHALL use this prefix (`"deWB-default"`)