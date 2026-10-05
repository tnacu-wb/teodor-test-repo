---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: "CTECH-12559"
---
# Requirements Document

## Introduction

This requirements document specifies the unification of the existing separate payment workflow implementations (`SecureFieldsPaymentWorkflow` and `MobileSdkPaymentWorkflow`) into a single `PaymentWorkflow` with a strategy pattern. Additionally, this includes the introduction of a new unified payment initialization API (`POST /api/payments/init`) that replaces the separate method-specific endpoints. This unified approach will support current payment methods (web Secure Fields, mobile SDK) and future methods (Google Pay, Apple Pay, saved cards) through a generic interface and polymorphic request structure. The implementation is a clean replacement with no backward compatibility needed as the service is not in production. This document incorporates critical defect fixes including webhook mismatch handling, authorization timeout guards, HTTPS returnUrl validation, and proper activity exception handling patterns.

## Glossary

- **Payment Workflow**: Single unified Temporal workflow managing the payment lifecycle for a basket
- **Payment Method Strategy**: Plain Java strategy implementing payment-method-specific initialization and authorization logic  
- **Payment Init Request**: Discriminated request hierarchy carrying method-specific parameters
- **Workflow Adapter**: Infrastructure adapter managing Temporal client interactions
- **PaymentMethod**: Enum discriminating between Datatrans integration types (NEW_CARD_WEB, NEW_CARD_MOBILE)
- **Event-Inbox Pattern**: Signal handlers deposit events into workflow state, strategies consume via Workflow.await()
- **PaymentErrorCode**: Typed error codes shared across all payment components
- **Method Config**: Sealed interface for future payment-method-specific configuration data

## Requirements

### Requirement 1: Unified Payment Workflow Interface
**User Story:** As a payment orchestration system, I want a single workflow interface handling all payment methods, so that adding new payment types doesn't require new workflow definitions.

#### Acceptance Criteria
1. THE system SHALL provide a single `PaymentWorkflow` interface with generic methods
2. THE interface SHALL include `@WorkflowMethod void run(String basketId)`
3. THE interface SHALL include `@UpdateMethod PaymentInitResult init(PaymentInitCommand command)`
4. THE interface SHALL include `@UpdateMethod AuthorizeResult authorize()` for synchronous authorization
5. THE interface SHALL include `@SignalMethod void webhookReceived(WebhookPayload payload)` for Datatrans callbacks
6. THE interface SHALL include `@SignalMethod void bookingCompleted(BookingCompletedEvent event)`
7. THE interface SHALL include `@QueryMethod PaymentStatus getPaymentStatus()`
8. THE interface SHALL include `@QueryMethod AuthorizeResult getAuthorizeResult()` for optional status checks

### Requirement 2: Strategy Pattern Implementation
**User Story:** As a payment workflow, I want pluggable strategies for different payment methods, so that method-specific logic is encapsulated and testable.

#### Acceptance Criteria
1. THE system SHALL provide a `PaymentMethodStrategy` interface for method-specific logic
2. THE strategy interface SHALL include `PaymentInitResult init(PaymentInitCommand command, PaymentActivities activities, WorkflowState state)`
3. THE strategy interface SHALL include `void awaitAuthorization(PaymentActivities activities, WorkflowState state)`
4. THE strategies SHALL be plain deterministic Java classes with no Spring dependencies
5. THE strategies SHALL be stateless, with state managed in workflow fields
6. THE system SHALL provide a `PaymentMethodStrategyFactory` with deterministic strategy creation

### Requirement 3: Polymorphic Request Structure with Security Validation
**User Story:** As a client, I want to initialize any payment method through a single endpoint with secure parameter validation, so that the API remains consistent and secure as new methods are added.

#### Acceptance Criteria
1. THE system SHALL provide a discriminated `PaymentInitRequest` hierarchy
2. THE request hierarchy SHALL include `NewCardWebInitRequest` with returnUrl, basketId, country, language, userType, clientChannel
3. THE request hierarchy SHALL include `NewCardMobileInitRequest` with basketId, country, language, userType, clientChannel (no returnUrl)
4. THE request SHALL use Jackson @JsonTypeInfo with paymentMethod discriminator
5. THE request SHALL use @NotBlank validation on method-specific fields per subtype
6. THE returnUrl field SHALL be validated to use HTTPS protocol only (reject HTTP)
7. THE returnUrl field SHALL be validated against a configurable allowlist of permitted hosts (e.g. premierinn.com, premierinn.digital)
8. THE system SHALL configure allowed return URL hosts via `payment.security.allowed-return-url-hosts` property

### Requirement 4: Unified Payment Initialization API
**User Story:** As a client application, I want a single payment initialization endpoint, so that I don't need to choose between different endpoints based on payment method.

#### Acceptance Criteria
1. THE system SHALL provide a `POST /api/payments/init` endpoint accepting discriminated request hierarchy
2. THE endpoint SHALL return HTTP 201 Created for successful initialization
3. THE endpoint SHALL return extensible PaymentInitResponse with paymentMethod, transactionId, and methodConfig fields
4. THE system SHALL map requests 1:1 to PaymentInitCommand instances
5. THE system SHALL validate method-specific requirements using Jackson polymorphic deserialization

### Requirement 5: Endpoint Migration and Cleanup
**User Story:** As an API maintainer, I want to remove deprecated endpoints, so that the API surface remains clean and focused.

#### Acceptance Criteria
1. THE system SHALL delete the `POST /api/payments/secure-fields` endpoint
2. THE system SHALL delete the `POST /api/payments/mobile-sdk` endpoint
3. THE system SHALL rename `POST /api/payments/webhooks/mobile-sdk` to `POST /api/payments/webhooks/datatrans`
4. THE system SHALL preserve `POST /api/payments/authorize` endpoint unchanged
5. THE webhook endpoint SHALL maintain identical behavior with a more generic name
6. THE system SHALL verify no Datatrans dashboard config pins old webhook path before renaming

### Requirement 6: PaymentMethod Enumeration as Integration Type
**User Story:** As a payment system, I want clear naming for payment methods representing Datatrans integrations, so that each value's purpose is immediately obvious.

#### Acceptance Criteria
1. THE system SHALL provide a `PaymentMethod` enum with NEW_CARD_WEB and NEW_CARD_MOBILE values
2. THE enum SHALL be extensible for future integration types (GOOGLE_PAY, APPLE_PAY, SAVED_CARD)
3. THE enum SHALL use descriptive names that clearly indicate the Datatrans integration type
4. THE enum SHALL include Javadoc comment: "Each value represents a distinct Datatrans integration type, not a payment instrument"
5. THE enum SHALL drive strategy factory selection logic

### Requirement 7: Event-Inbox Signal Handling with Webhook Security
**User Story:** As a payment workflow, I want signal handlers to deposit validated events for strategies to consume, so that signal routing doesn't hardcode method assumptions and webhook security is enforced.

#### Acceptance Criteria
1. THE system SHALL implement event-inbox pattern in signal handlers
2. THE webhookReceived signal SHALL validate payload.transactionId() matches state.transactionId BEFORE depositing
3. THE webhookReceived signal SHALL log and drop mismatched payloads without depositing to inbox
4. THE webhookReceived signal SHALL set webhookPayload field only for validated payloads
5. THE authorize signal SHALL set authorizeSignalReceived field in WorkflowState
6. THE bookingCompleted signal SHALL set bookingCompletedEvent field in WorkflowState
7. THE strategies SHALL use Workflow.await(predicate) to consume relevant inbox fields
8. THE system SHALL NOT route signals directly to specific strategies
9. THE strategies SHALL handle transactionId validation by only consuming validated inbox events

### Requirement 8: Synchronous Authorization Update Method with Blocking Semantics
**User Story:** As a frontend client, I want synchronous authorization responses with proper pre-condition validation, so that I don't need to poll for authorization results and invalid states are caught early.

#### Acceptance Criteria
1. THE system SHALL convert authorize from @SignalMethod to @UpdateMethod
2. THE authorize update SHALL return AuthorizeResult synchronously
3. THE authorize handler SHALL validate pre-conditions before awaiting strategy completion
4. IF state.transactionId is null THEN authorize SHALL return AuthorizeResult(false, "INVALID_TRANSACTION_STATE", "Payment not initialized")
5. IF authorizationInProgress is true THEN authorize SHALL return AuthorizeResult(false, "AUTHORIZATION_IN_PROGRESS", "Authorization already in progress")
6. IF paymentStatus != INITIALIZED THEN authorize SHALL return AuthorizeResult(false, "INVALID_TRANSACTION_STATE", "Payment not in valid state for authorization")
7. THE authorize handler SHALL set authorizeSignalReceived=true then await state.getAuthorizeResult() != null OR state.attemptFailed()
8. THE system SHALL eliminate the signal+polling pattern in workflow adapter
9. THE adapter SHALL use Update-With-Start for initialization operations
10. THE adapter SHALL bound the authorization Update wait with a configurable timeout (`integrations.payment.workflow.authorize-wait`, default 30 seconds — kept under typical 60-second load-balancer idle timeouts); WHEN the wait elapses the adapter SHALL return AUTHORIZATION_PENDING (surfaced as HTTP 202 with a pollable status endpoint) rather than an error, because the update is durably accepted and the workflow completes the authorization regardless
11. THE getAuthorizeResult query SHALL remain for optional status checks

### Requirement 9: Preserved Workflow Functionality
**User Story:** As an existing payment user, I want all current payment capabilities to work identically, so that the refactoring doesn't introduce regressions.

#### Acceptance Criteria
1. THE system SHALL preserve deferred settlement via BookingCompletedEvent signals
2. THE system SHALL preserve basket PAY_PENDING status transitions
3. THE system SHALL preserve PaymentAuthorisedEvent publishing to Kafka
4. THE system SHALL preserve Mobile SDK reconciliation polling behavior
5. THE system SHALL preserve webhook HMAC validation
6. THE system SHALL maintain workflow ID pattern `payment-{basketId}`

### Requirement 10: Single Adapter Implementation
**User Story:** As a payment controller, I want a single workflow adapter, so that I don't need to choose between different adapters based on payment method.

#### Acceptance Criteria
1. THE system SHALL provide a single `PaymentWorkflowAdapter` replacing both existing adapters
2. THE adapter SHALL handle unified payment initialization via `initPayment` method
3. THE adapter SHALL handle synchronous authorization via `authorize` method
4. THE adapter SHALL handle webhook signals for mobile payments
5. THE adapter SHALL use Update-With-Start for initialization operations

### Requirement 11: Workflow Expiry Management with Authorization Safety Guard
**User Story:** As a payment system, I want workflows to expire after inactivity while preventing cancellation of in-flight authorizations, so that abandoned baskets don't leak workflows but active payments complete safely.

#### Acceptance Criteria
1. THE workflow SHALL implement configurable timeout in run() method
2. THE workflow SHALL use Workflow.await(timeout, predicate) for initial expiry check
3. THE workflow SHALL check isTerminal() OR (timedOut AND NOT authorizationInProgress) before expiring
4. WHEN timeout fires AND authorizationInProgress is true THEN workflow SHALL await !authorizationInProgress
5. THE workflow SHALL only transition to EXPIRED state after confirming no authorization is in flight
6. THE system SHALL configure timeout value via application properties
7. THE workflow SHALL clean up expired transactions appropriately
8. THE PaymentStatus.isTerminal() method SHALL return true for EXPIRED state

### Requirement 12: Explicit Re-initialization Policy Matrix
**User Story:** As a payment workflow, I want clear re-initialization rules, so that payment state transitions are predictable and safe.

#### Acceptance Criteria
1. THE system SHALL allow same-method re-init while INITIALIZED (cancel prior transaction, create new)
2. THE system SHALL allow cross-method re-init while INITIALIZED (cancel prior transaction, switch strategy, create new) 
3. THE system SHALL reject re-init while authorizationInProgress with conflict error
4. THE system SHALL reject re-init after AUTHORIZED/SETTLED/CANCELLED (terminal for this attempt)
5. THE system SHALL allow re-init after FAILED (workflow stays alive, new attempt with potentially different method)

### Requirement 13: Framework-Free Typed Error Code System
**User Story:** As a payment system, I want typed error codes throughout the stack that remain framework-independent, so that error handling is consistent, type-safe, and not coupled to web frameworks.

#### Acceptance Criteria
1. THE system SHALL provide PaymentErrorCode enum for all payment errors
2. THE PaymentInitResult SHALL use PaymentErrorCode instead of String errorCode
3. THE PaymentErrorCode enum SHALL be framework-free (no HttpStatus dependency)
4. THE error codes SHALL be shared by strategies, workflow, adapter, and exception handler
5. THE system SHALL eliminate string-based error code plumbing
6. THE @ControllerAdvice SHALL map PaymentErrorCode to HTTP status codes using switch or Map
7. THE PaymentErrorCode enum SHALL include EXPIRED as a terminal error state

### Requirement 14: Standardized Amount Handling
**User Story:** As a payment system, I want consistent amount representation, so that precision is maintained end-to-end.

#### Acceptance Criteria
1. THE system SHALL use long for all amount fields (not Integer)
2. THE WorkflowState.authorizedAmount SHALL be long type
3. THE system SHALL represent all amounts in minor units (pence/cents)
4. THE system SHALL maintain amount precision throughout the payment flow
5. THE system SHALL validate amount consistency between initialization and authorization

### Requirement 15: Activity Exception Handling with FQCN Mapping
**User Story:** As a payment workflow, I want proper activity exception handling with fully qualified class name mapping, so that domain exceptions are correctly identified from Temporal wrappers.

#### Acceptance Criteria
1. THE strategies SHALL handle ActivityFailure wrapping ApplicationFailure from activity calls
2. THE system SHALL provide extractFailureType(Throwable) utility for unwrapping cause chains
3. THE strategies SHALL extract type string from ApplicationFailure.getType() which returns fully qualified class names
4. THE system SHALL use constants derived from ClassName.class.getName() for exception type matching
5. THE strategies SHALL NOT directly catch domain exception types (BasketNotFoundException, etc.)
6. THE type mapping constants SHALL use fully qualified class names (e.g. "uk.co.whitbread.payment.orchestrator.domain.exceptions.BasketNotFoundException")
7. THE extractFailureType method SHALL map FQCN strings to PaymentErrorCode enum values

### Requirement 16: Generic Webhook Payload Structure
**User Story:** As a webhook handler, I want gateway-scoped webhook discrimination, so that payloads aren't tied to specific payment methods.

#### Acceptance Criteria
1. THE system SHALL remove paymentMethod() from WebhookPayload interface
2. THE system SHALL discriminate webhooks by gateway type (DATATRANS) if using hierarchy
3. THE webhook correlation SHALL use stored transactionId matching
4. THE signal handler SHALL validate payload.transactionId() matches state.transactionId
5. THE webhook validation SHALL serve as replay-attack guard

### Requirement 17: Replay Testing and Versioning
**User Story:** As a workflow developer, I want replay testing and versioning support, so that workflow changes are safe and backwards compatible.

#### Acceptance Criteria
1. THE system SHALL implement WorkflowReplayer-based replay tests
2. THE tests SHALL use recorded workflow histories for validation
3. THE system SHALL add Workflow.getVersion() policy for future spine changes
4. THE replay tests SHALL validate deterministic strategy factory behavior
5. THE system SHALL test workflow compatibility across command type changes

### Requirement 18: Clean Replacement
**User Story:** As a development team, I want to completely replace the old workflow implementations and endpoints, so that maintenance complexity is reduced.

#### Acceptance Criteria
1. THE system SHALL delete `SecureFieldsPaymentWorkflow` interface and implementation
2. THE system SHALL delete `MobileSdkPaymentWorkflow` interface and implementation
3. THE system SHALL delete `TemporalWorkflowAdapter` class
4. THE system SHALL delete `MobileSdkWorkflowAdapter` class
5. THE system SHALL delete old payment initialization endpoints
6. THE system SHALL update all existing tests to use the unified implementation

### Requirement 20: Extensible Method Configuration Response
**User Story:** As an API client, I want typed configuration responses for different payment methods, so that method-specific metadata can be provided in a structured way for future extensions.

#### Acceptance Criteria
1. THE system SHALL provide a sealed MethodConfig marker interface for payment method configuration
2. THE MethodConfig SHALL be extensible for future payment methods (SecureFieldsConfig, MobileSdkConfig, etc.)
3. THE PaymentInitResponse SHALL include methodConfig field of type MethodConfig (nullable)
4. THE methodConfig field SHALL initially return null for current payment methods
5. THE MethodConfig SHALL provide typed OpenAPI schema generation instead of untyped Object
6. THE system SHALL reserve methodConfig for future use (Apple Pay merchant ID, Google Pay configuration, etc.)
### Requirement 19: Configuration and Integration Points
**User Story:** As a system administrator, I want the unified workflow to integrate seamlessly with existing infrastructure, so that deployment requires minimal configuration changes.

#### Acceptance Criteria
1. THE system SHALL register only `PaymentWorkflowImpl` in TemporalConfig
2. THE system SHALL preserve existing Kafka consumer configurations
3. THE system SHALL preserve webhook endpoint configurations
4. THE system SHALL maintain compatibility with existing health check implementations
5. THE system SHALL preserve reconciliation polling configuration properties