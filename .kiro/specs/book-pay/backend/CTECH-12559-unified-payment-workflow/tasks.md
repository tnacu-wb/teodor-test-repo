---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: "CTECH-12559"
---
# Implementation Plan: Unified Payment Workflow

## Overview

Unify the existing separate payment workflow implementations into a single `PaymentWorkflow` using the Strategy pattern with event-inbox signal handling. Introduce a unified payment initialization API (`POST /api/payments/init`) using polymorphic request discrimination. Convert authorize to @UpdateMethod for synchronous responses and implement proper activity exception handling. This update incorporates critical defect fixes: webhook mismatch validation at signal level, authorization safety guards for expiry, HTTPS returnUrl validation with host allowlisting, and FQCN-based exception mapping.

## Tasks

- [x] 1. Create Domain Models and Interfaces
  - [x] 1.1 Update PaymentMethod enum with integration type documentation
    - Change enum values from SECURE_FIELDS/MOBILE_SDK to NEW_CARD_WEB/NEW_CARD_MOBILE
    - Add Javadoc comment: "Each value represents a distinct Datatrans integration type, not a payment instrument"
    - Add extensible structure for future methods (GOOGLE_PAY, APPLE_PAY, SAVED_CARD)
    - Update all existing references to use new enum values
    - _Requirements: 6.1, 6.2, 6.3, 6.4_
  - [x] 1.2 Create framework-free PaymentErrorCode system
    - Create PaymentErrorCode enum with all payment error types including EXPIRED
    - Add getErrorMessage() method to enum (no getHttpStatus() - keep framework-free)
    - Update PaymentInitResult to use PaymentErrorCode instead of String errorCode
    - Create PaymentInitializationException with PaymentErrorCode field
    - Create HTTP status mapping in @ControllerAdvice using Map or switch statement
    - _Requirements: 13.1, 13.2, 13.3, 13.4, 13.5, 13.6, 13.7_
  - [x] 1.3 Create polymorphic PaymentInitRequest hierarchy with security validation
    - Create base PaymentInitRequest sealed interface with @JsonTypeInfo discriminator
    - Create NewCardWebInitRequest record with @NotBlank returnUrl
    - Create NewCardMobileInitRequest record without returnUrl field
    - Add class-level @ConditionalValidation annotation (NOT field-level)
    - Configure Jackson polymorphic deserialization with paymentMethod discriminator
    - Create ReturnUrlValidator component for HTTPS and host allowlist validation
    - Add payment.security.allowed-return-url-hosts configuration property
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8_
  - [x] 1.4 Create extensible PaymentInitResponse with typed MethodConfig
    - Create sealed MethodConfig marker interface with SecureFieldsConfig, MobileSdkConfig implementations
    - Create PaymentInitResponse record with paymentMethod, transactionId, and MethodConfig fields
    - Set methodConfig as nullable MethodConfig for typed OpenAPI schema generation
    - Add @JsonPropertyOrder annotation for consistent field ordering
    - Reserve methodConfig for future extension (Apple Pay merchant ID, Google Pay config, etc.)
    - _Requirements: 4.3, 20.1, 20.2, 20.3, 20.4, 20.5, 20.6_
  - [x] 1.5 Update WebhookPayload hierarchy for gateway-scoped discrimination
    - Remove paymentMethod() method from WebhookPayload interface
    - Rename MobileSdkWebhookPayload to DatatransWebhookPayload
    - Update Jackson type information to use gateway discriminator (DATATRANS)
    - Remove method-specific determination logic - use transactionId correlation only
    - _Requirements: 16.1, 16.2, 16.3, 16.4, 16.5_
  - [x] 1.6 Update WorkflowState class with event-inbox and standardized amounts
    - Add event-inbox fields: authorizeSignalReceived, webhookPayload, bookingCompletedEvent
    - Add authorizationInProgress boolean field for expiry safety guard
    - Change authorizedAmount from Integer to long type
    - Consolidate state fields from both existing workflow implementations
    - Update field names and types to align with new PaymentMethod enum
    - Preserve all strategy-specific state (reconciliation, timing, authorization progress)
    - Add isTerminal() method to PaymentStatus enum including EXPIRED state
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 14.1, 14.2, 14.3, 11.8_
- [x] 2. Implement Strategy Pattern with Event-Inbox
  - [x] 2.1 Create PaymentMethodStrategy interface with event-inbox pattern
    - Define PaymentInitResult init(PaymentInitCommand, PaymentActivities, WorkflowState) method
    - Define void awaitAuthorization(PaymentActivities, WorkflowState) method using Workflow.await()
    - Define PaymentMethod getSupportedMethod() method
    - Document that strategies consume events via Workflow.await(predicate) from inbox fields
    - _Requirements: 2.1, 2.2, 2.3, 7.5_
  - [x] 2.2 Create NewCardWebStrategy with FQCN exception handling
    - Extract initialization logic from SecureFieldsPaymentWorkflowImpl
    - Handle NewCardWebInitCommand type and extract returnUrl parameter
    - Implement extractFailureType(Throwable) utility using FQCN constants derived from ClassName.class.getName()
    - Map ApplicationFailure.getType() FQCN strings to PaymentErrorCode enum values
    - Implement awaitAuthorization using Workflow.await(() -> state.isAuthorizeSignalReceived())
    - Maintain existing error handling and state transitions with typed errors
    - _Requirements: 2.4, 7.1, 7.2, 7.3, 7.5, 15.1, 15.2, 15.3, 15.4, 15.5, 15.6, 15.7_
  - [x] 2.3 Create NewCardMobileStrategy with full reconciliation loop and FQCN exception handling
    - Extract initialization logic from MobileSdkPaymentWorkflowImpl
    - Handle NewCardMobileInitCommand type (no returnUrl handling)
    - Implement full reconciliation polling loop with timing configuration (not just webhook await)
    - Include initial delay, poll interval, max duration, and Datatrans status polling
    - Implement awaitAuthorization with complete CTECH-12128 reconciliation pattern
    - Preserve existing webhook HMAC validation and status handling with FQCN exception unwrapping
    - _Requirements: 2.4, 7.4, 7.5, 15.1, 15.2, 15.3, 15.4, 15.5, 15.6, 15.7, 16.4, 16.5_
  - [x] 2.4 Update PaymentMethodStrategyFactory
    - Update switch expression to use NEW_CARD_WEB and NEW_CARD_MOBILE enum values
    - Return NewCardWebStrategy for NEW_CARD_WEB
    - Return NewCardMobileStrategy for NEW_CARD_MOBILE
    - Ensure factory remains stateless and deterministic for Temporal workflows
    - Document deterministic behavior for replay testing
    - _Requirements: 2.6, 6.4, 17.4_

- [x] 3. Implement Unified Payment Workflow with Event-Inbox and @UpdateMethod
  - [x] 3.1 Update PaymentWorkflow interface with @UpdateMethod authorize and pre-condition validation
    - Keep existing @WorkflowMethod void run(String basketId) with timeout handling
    - Keep existing @UpdateMethod PaymentInitResult init(PaymentInitCommand command)
    - Convert @SignalMethod void authorize() to @UpdateMethod AuthorizeResult authorize() with blocking semantics
    - Add pre-condition validation in authorize handler for transactionId, authorizationInProgress, paymentStatus
    - Keep existing signal methods: webhookReceived(), bookingCompleted()
    - Keep existing query methods but make getAuthorizeResult() optional for status checks
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 8.1, 8.2, 8.3, 8.4, 8.5, 8.6, 8.7, 8.10, 8.11_
  - [x] 3.2 Update PaymentWorkflowImpl with secure webhook signal handling and authorization pre-conditions
    - Implement authorize() as @UpdateMethod with pre-condition validation and blocking semantics
    - Update webhookReceived signal to validate payload.transactionId() BEFORE depositing to inbox
    - Log and drop mismatched webhook payloads (never deposit invalid webhooks to inbox)
    - Update signal handlers to deposit only validated events into WorkflowState inbox fields
    - Implement bookingCompleted() to set bookingCompletedEvent field
    - Update strategy creation to use new PaymentMethod enum values
    - Manage workflow state with updated field mappings and long amounts
    - _Requirements: 2.5, 7.1, 7.2, 7.3, 7.4, 7.5, 7.8, 7.9, 8.3, 8.4, 8.5, 8.6, 8.7, 14.2_
  - [x] 3.3 Implement re-initialization policy matrix
    - Implement explicit re-init rules: same-method and cross-method allowed while INITIALIZED
    - Reject re-init while authorizationInProgress with PaymentErrorCode.AUTHORIZATION_IN_PROGRESS
    - Reject re-init after AUTHORIZED/SETTLED/CANCELLED (terminal states)
    - Allow re-init after FAILED (workflow stays alive, new attempt with potentially different method)
    - Implement transaction cancellation for superseded attempts
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5_
  - [x] 3.4 Implement workflow expiry management with authorization safety guard
    - Add configurable timeout in run() method using Workflow.await(timeout, predicate)
    - CRITICAL FIX: Add authorization safety guard - await !authorizationInProgress after timeout fires
    - Only transition to EXPIRED state after confirming no authorization is in flight
    - Implement isTerminal() OR (timedOut AND NOT authorizationInProgress) predicate logic
    - Add configuration property for timeout value (default: 30 minutes)
    - Clean up expired transactions via cancelTransaction activity call
    - _Requirements: 11.1, 11.2, 11.3, 11.4, 11.5, 11.6, 11.7, 11.8_
  - [x] 3.5 Preserve BookingCompletedEvent handling with typed errors
    - Keep existing settlement/cancellation logic based on event status
    - Maintain compatibility with existing Kafka signaler
    - Handle settlement timing and Datatrans API interactions
    - Update error handling to use PaymentErrorCode enum
    - _Requirements: 9.1, 13.1_
- [x] 4. Create Unified API Controller Implementation
  - [x] 4.1 Implement class-level validation annotation with returnUrl security (FIXED)
    - Create @ConditionalValidation as @Target(TYPE) class-level constraint
    - Implement ConditionalValidationValidator working on PaymentInitRequest instances
    - Use context.buildConstraintViolationWithTemplate().addPropertyNode("returnUrl") for field errors
    - Integrate ReturnUrlValidator to check HTTPS protocol and host allowlist
    - Test validation receives PaymentInitRequest record, not field values
    - Document why field-level validation fails (Bean Validation UnexpectedTypeException)
    - _Requirements: 3.5, 3.6, 3.7, 3.8, 12.1, 12.2, 12.3, 12.4, 12.5_
  - [x] 4.2 Implement unified POST /api/payments/init endpoint
    - Create single endpoint accepting polymorphic PaymentInitRequest with Jackson discrimination
    - Return 201 Created for successful initialization (resource creation)
    - Implement trivial 1:1 mapping from request subtypes to PaymentInitCommand instances
    - Return extensible PaymentInitResponse with paymentMethod, transactionId, methodConfig
    - Implement proper error handling using PaymentErrorCode and exception mapper
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 13.4, 13.5_
  - [x] 4.3 Delete old payment initialization endpoints
    - Remove @PostMapping("/secure-fields") endpoint method
    - Remove @PostMapping("/mobile-sdk") endpoint method
    - Clean up any associated request/response classes if no longer needed
    - _Requirements: 5.1, 5.2, 18.5_
  - [x] 4.4 Update authorization endpoint for synchronous responses
    - Keep existing POST /api/payments/authorize endpoint unchanged
    - Update implementation to call @UpdateMethod authorize() for immediate response
    - Remove Thread.sleep(500) query polling pattern from adapter
    - Verify it works with unified workflow adapter and returns AuthorizeResult directly
    - _Requirements: 5.4, 8.3_
  - [x] 4.5 Rename webhook endpoint with verification
    - Verify no Datatrans dashboard config or environment variables pin old /webhooks/mobile-sdk path
    - Rename @PostMapping("/webhooks/mobile-sdk") to @PostMapping("/webhooks/datatrans")
    - Update method to handle DatatransWebhookPayload with gateway-scoped discrimination
    - Preserve existing webhook validation and HMAC checking logic
    - Maintain existing endpoint behavior and response handling
    - _Requirements: 5.3, 5.5, 5.6_
  - [x] 4.6 Implement framework-free error handling and HTTP status mapping
    - Create @ControllerAdvice for handling PaymentInitializationException
    - Map PaymentErrorCode enum values to HTTP status codes using Map or switch (not enum methods)
    - Return proper error responses with consistent structure
    - Provide clear error messages from PaymentErrorCode.getErrorMessage()
    - Keep PaymentErrorCode enum free of framework dependencies
    - _Requirements: 13.3, 13.4, 13.5, 13.6_

- [x] 5. Create Unified Workflow Adapter with Synchronous Authorization
  - [x] 5.1 Implement PaymentWorkflowAdapter with @UpdateMethod support and authorization timeout
    - Replace both TemporalWorkflowAdapter and MobileSdkWorkflowAdapter
    - Implement single initPayment(String basketId, PaymentInitCommand command) method
    - Implement synchronous authorize(String basketId) method calling @UpdateMethod
    - Bound the authorize Update wait with a configurable timeout (authorize-wait, default 30s); return AUTHORIZATION_PENDING (HTTP 202, pollable status) when it elapses
    - Eliminate signal+polling pattern - return AuthorizeResult directly from update
    - Implement handleWebhook(String basketId, WebhookPayload payload) method
    - _Requirements: 10.1, 10.2, 10.3, 10.4, 8.3, 8.10_
  - [x] 5.2 Implement Update-With-Start operations
    - Use Temporal's executeUpdateWithStart for payment initialization
    - Maintain existing workflow ID pattern payment-{basketId}
    - Preserve USE_EXISTING workflow conflict policy
    - Handle workflow error mapping and ApplicationFailure propagation
    - Update authorization to use direct update call (no polling)
    - _Requirements: 10.5, 8.3_
  - [x] 5.3 Update webhook URL building and reconciliation settings
    - Update webhook URL construction for renamed /datatrans endpoint
    - Map reconciliation configuration properties to strategy settings
    - Handle webhook callback base URL configuration updates
    - _Requirements: 9.4_

- [x] 6. Update Configuration and Infrastructure
  - [x] 6.1 Update TemporalConfig
    - Remove registration of SecureFieldsPaymentWorkflowImpl and MobileSdkPaymentWorkflowImpl
    - Register only PaymentWorkflowImpl for workflow execution
    - Preserve existing worker and task queue configuration
    - _Requirements: 19.1_
  - [x] 6.2 Update Spring bean configuration
    - Replace both adapter bean definitions with single PaymentWorkflowAdapter
    - Update constructor arguments to match unified adapter requirements
    - Preserve existing webhook and reconciliation property injection
    - Maintain profile-based configuration (exclude integration profile)
    - _Requirements: 19.2, 19.3, 19.4, 19.5_
  - [x] 6.3 Configure Jackson polymorphic type handling
    - Register PaymentInitRequest hierarchy with type information for NEW_CARD_WEB/NEW_CARD_MOBILE
    - Register WebhookPayload hierarchy with gateway-based type information
    - Ensure sealed interface deserialization works correctly
    - Test command serialization/deserialization round-trip
    - _Requirements: 3.5_
  - [x] 6.4 Add workflow expiry and security configuration properties
    - Add integrations.payment.workflow.timeout property (default: 30 minutes)
    - Add payment.security.allowed-return-url-hosts property for returnUrl validation
    - Configure timeout injection into workflow implementation
    - Document timeout behavior and cleanup process
    - Document returnUrl security configuration and allowlist format
    - _Requirements: 11.3, 11.4, 3.8_
- [x] 7. Update Tests
  - [x] 7.1 Create strategy unit tests with FQCN mapping and returnUrl security (thin - helper methods only)
    - Test extractFailureType(Throwable) utility for ApplicationFailure FQCN unwrapping
    - Test PaymentErrorCode mapping from ApplicationFailure.getType() FQCN strings
    - Test ReturnUrlValidator for HTTPS enforcement and host allowlist validation
    - Mock PaymentActivities and WorkflowState dependencies for pure helper methods
    - Test AmountCalculator and other utility classes (NOT full strategy behavior)
    - Focus on error mapping, amount conversion, security validation, and FQCN handling logic only
    - _Requirements: 15.1, 15.2, 15.3, 15.4, 15.5, 15.6, 15.7, 3.6, 3.7, 18.6_
  - [x] 7.2 Update unified workflow tests via TestWorkflowEnvironment (comprehensive)
    - Convert existing workflow tests to use new PaymentMethod enum values
    - Test both NEW_CARD_WEB and NEW_CARD_MOBILE flows through single workflow
    - Test secure webhook signal handling (validation before deposit, drop invalid payloads)
    - Test authorize @UpdateMethod with pre-condition validation and blocking semantics
    - Test synchronous authorization responses (no polling)
    - Test retry-after-failure scenarios with method switching
    - Test re-initialization policy matrix with all state combinations
    - Test workflow expiry timeout behavior with authorization safety guard
    - Test command polymorphism and type discrimination
    - Test full reconciliation loop for mobile strategy (not just webhook await)
    - Weight: All strategy behavior tested through TestWorkflowEnvironment since awaitAuthorization uses Workflow.await
    - _Requirements: 18.6, 12.1, 12.2, 12.3, 12.4, 12.5, 7.2, 7.3, 7.8, 7.9, 8.3, 8.4, 8.5, 8.6, 8.7, 11.1, 11.2, 11.4, 11.5, 16.4, 16.5_
  - [x] 7.3 Create unified adapter tests
    - Convert existing adapter tests to use single PaymentWorkflowAdapter
    - Test unified initPayment method with both command types
    - Test synchronous authorize() method (no polling verification)
    - Test command mapping from controller to workflow updates
    - Test error handling and workflow exception propagation with typed errors
    - _Requirements: 18.6, 8.3, 13.4_
  - [x] 7.4 Update controller tests with polymorphic request validation and returnUrl security
    - Test new unified /api/payments/init endpoint with both PaymentInitRequest subtypes
    - Test class-level @ConditionalValidation for returnUrl requirement per payment method
    - Test returnUrl security validation (HTTPS enforcement, host allowlist)
    - Test Jackson polymorphic deserialization with discriminator property
    - Test proper HTTP 201 Created responses for successful initialization
    - Test framework-free error responses with PaymentErrorCode mapping
    - Verify old endpoint methods are deleted (compilation test)
    - Test renamed webhook endpoint /api/payments/webhooks/datatrans
    - _Requirements: 18.6, 4.2, 4.3, 13.5, 13.6, 3.6, 3.7_
  - [x] 7.5 Update integration tests
    - Modify existing end-to-end tests to use new unified API endpoints
    - Test payment flows for both NEW_CARD_WEB and NEW_CARD_MOBILE
    - Verify Kafka event publishing and settlement logic works unchanged
    - Test webhook handling with renamed endpoint and transactionId validation
    - Test synchronous authorization responses in full flow
    - _Requirements: 18.6, 16.4, 8.3_
  - [x] 7.6 Create replay tests with WorkflowReplayer (corrected API usage)
    - Implement WorkflowReplayer-based replay tests using static method replayWorkflowExecution
    - Test workflow determinism with new command types and enum values
    - Validate strategy factory produces deterministic results for replay
    - Test backward compatibility across workflow changes
    - Add Workflow.getVersion() policy for future spine changes
    - Use Files.readString() and static WorkflowReplayer.replayWorkflowExecution() method
    - _Requirements: 17.1, 17.2, 17.3, 17.4, 17.5_

- [x] 8. Clean Up Legacy Code
  - [x] 8.1 Delete old workflow interfaces and implementations
    - Delete SecureFieldsPaymentWorkflow.java interface
    - Delete SecureFieldsPaymentWorkflowImpl.java implementation
    - Delete MobileSdkPaymentWorkflow.java interface
    - Delete MobileSdkPaymentWorkflowImpl.java implementation
    - _Requirements: 18.1, 18.2_
  - [x] 8.2 Delete old adapter implementations
    - Delete TemporalWorkflowAdapter.java class
    - Delete MobileSdkWorkflowAdapter.java class
    - Remove associated port interfaces if no longer needed
    - _Requirements: 18.3, 18.4_
  - [x] 8.3 Delete old request/response models
    - Delete method-specific request classes for old endpoints
    - Delete method-specific response classes for old endpoints
    - Keep only unified PaymentInitRequest/PaymentInitResponse
    - _Requirements: 18.5_
  - [x] 8.4 Delete old test files
    - Delete tests for removed workflow implementations
    - Delete tests for removed adapter implementations
    - Delete tests for removed controller endpoints
    - Ensure no compilation errors remain
    - _Requirements: 18.6_
  - [x] 8.5 Update import statements and references
    - Fix any remaining import references to deleted classes
    - Update package references in configuration files
    - Clean up unused imports in remaining classes
    - Update documentation and comments referencing old class names
    - _Requirements: 18.6_
- [x] 9. API Documentation and Validation
  - [x] 9.1 Update OpenAPI documentation with polymorphic schemas and security requirements
    - Document new unified /api/payments/init endpoint with oneOf discriminated schemas
    - Document class-level validation rules for returnUrl per payment method
    - Document returnUrl security requirements (HTTPS only, host allowlist)
    - Document PaymentMethod enum values and their integration purposes
    - Document extensible PaymentInitResponse structure with typed MethodConfig
    - Remove documentation for deleted endpoints
    - Update webhook endpoint documentation for renamed /webhooks/datatrans path
    - Document framework-free error response structure with PaymentErrorCode
    - _Requirements: 4.1, 4.2, 4.3, 6.4, 3.6, 3.7, 3.8, 20.5_
  - [x] 9.2 Update integration documentation with security considerations
    - Update client integration guides to use unified polymorphic endpoint
    - Document returnUrl security requirements and host allowlist configuration
    - Document migration path from old endpoints (if needed for external teams)
    - Update error response documentation with framework-free PaymentErrorCode messages
    - Document synchronous authorization behavior (no polling required)
    - Document webhook security improvements and transactionId validation
    - _Requirements: 13.5, 13.6, 8.3, 3.6, 3.7, 3.8_
  - [x] 9.3 Verify endpoint behavior
    - Test all documented endpoints are accessible and working
    - Verify error responses match documented schemas with typed errors
    - Test request/response examples from documentation
    - Verify webhook endpoint rename is complete and functional
    - _Requirements: 5.4, 5.5_

- [x] 10. Performance and Compatibility Validation
  - [x] 10.1 Verify integration points
    - Test Kafka consumer compatibility with existing events (unchanged)
    - Verify health check implementations work with unified workflow
    - Test reconciliation polling configuration properties
    - Verify webhook URL generation for renamed endpoint
    - Test workflow expiry timeout configuration and behavior
    - _Requirements: 19.2, 19.3, 19.4, 19.5, 11.3_
  - [x] 10.2 End-to-end flow validation with security and safety features
    - Test complete NEW_CARD_WEB flow: init → authorize → settlement
    - Test complete NEW_CARD_MOBILE flow with full reconciliation: init → webhook/polling → settlement
    - Verify BookingCompletedEvent triggers settlement correctly
    - Test retry scenarios with method switching between failures
    - Test synchronous authorization responses eliminate polling
    - Test workflow expiry behavior with authorization safety guard
    - Verify re-initialization policy matrix in real scenarios
    - Test webhook security validation in full flow (invalid payloads dropped)
    - Test returnUrl security validation in NEW_CARD_WEB flow
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 8.3, 11.1, 11.2, 11.4, 11.5, 12.1, 12.2, 12.3, 12.4, 12.5, 7.2, 7.3, 7.8, 7.9, 3.6, 3.7_

## Notes

### Key Architectural Fixes Applied

- **CRITICAL: Webhook mismatch workflow hang fix** - Validate transactionId in signal handler BEFORE depositing (mismatched payloads logged and dropped)
- **CRITICAL: Expiry timer authorization safety guard** - Prevent cancellation of in-flight authorization via authorizationInProgress check
- **@UpdateMethod authorize conversion** - Eliminates signal+Thread.sleep(500) query-polling defect, adds pre-condition validation
- **Event-inbox pattern** - Signal handlers deposit validated events, strategies consume via Workflow.await()
- **Class-level validation** - Fixes Bean Validation UnexpectedTypeException with proper addPropertyNode()
- **FQCN exception unwrapping** - Proper extractFailureType(Throwable) using ClassName.class.getName() constants
- **Gateway-scoped webhooks** - Remove paymentMethod() from WebhookPayload, use transactionId correlation
- **Framework-free error codes** - PaymentErrorCode enum with @ControllerAdvice HTTP status mapping
- **Polymorphic requests** - Jackson @JsonTypeInfo with per-subtype validation and returnUrl security
- **ReturnUrl security** - HTTPS enforcement + configurable host allowlist (prevents open redirect attacks)
- **Typed MethodConfig** - Sealed interface for OpenAPI schema generation (replaces Object)

### Critical Defects Fixed

1. **Webhook mismatch throw hangs workflow** - Signal handler validates transactionId BEFORE inbox deposit
2. **Expiry timer cancels in-flight authorization** - Authorization safety guard prevents unsafe cancellation
3. **FQCN mapping required** - ApplicationFailure.getType() returns fully qualified class names
4. **ReturnUrl open redirect** - HTTPS + host allowlist validation prevents security vulnerability
5. **Framework coupling** - PaymentErrorCode stays domain-pure, HTTP mapping in @ControllerAdvice

### Re-initialization Policy Matrix
- **INITIALIZED + same-method** → allowed (cancel prior transaction, create new)
- **INITIALIZED + cross-method** → allowed (cancel prior transaction, switch strategy, create new)
- **authorizationInProgress** → reject with conflict error
- **AUTHORIZED/SETTLED/CANCELLED** → reject (terminal for this attempt)
- **FAILED** → allowed (workflow stays alive, new attempt with potentially different method)

### Event-Inbox Benefits
- **No hardcoded signal routing** - authorize() and webhookReceived() don't assume specific strategies
- **Future-proof flexibility** - Google Pay can use webhooks on web, Apple Pay can use authorize on mobile
- **Deterministic replay** - All events captured in workflow state history

### Synchronous Authorization Benefits
- **Immediate responses** - HTTP thread gets AuthorizeResult directly from @UpdateMethod
- **No polling overhead** - Eliminates Thread.sleep(500) query loops in adapter
- **Reduced Temporal queries** - One update call instead of signal + multiple queries
- **Pre-condition validation** - Invalid states caught early before awaiting strategy
- **Timeout protection** - configurable bounded authorize wait (default 30s) prevents indefinite HTTP blocking; on elapse the caller gets 202 AUTHORIZATION_PENDING and polls

### Authorization Safety Guard
The expiry timeout includes a critical safety mechanism:
```java
boolean completed = Workflow.await(expiryTimeout, () -> paymentStatus.isTerminal());
if (!completed) {
    // Timeout fired — but don't expire while authorization is in flight
    Workflow.await(() -> !authorizationInProgress);
    if (!paymentStatus.isTerminal()) {
        // Still not terminal after auth completed — safe to expire
        paymentStatus = PaymentStatus.EXPIRED;
    }
}
```
This prevents the race condition where a customer inits at minute 29, authorization starts at minute 30, and expiry cancels an in-flight transaction.

### PaymentMethod as Integration Type
Each enum value represents a **Datatrans integration**, not a payment instrument:
- `NEW_CARD_WEB` → Secure Fields integration (init + explicit authorize)
- `NEW_CARD_MOBILE` → Mobile SDK integration (init + webhook)
- Future: `GOOGLE_PAY` → Mobile SDK/Payment Button integration (init + webhook)
- Future: `APPLE_PAY` → Mobile SDK/Payment Button integration (init + webhook)
- Future: `SAVED_CARD` → Direct authorize with alias (no init)

### Testing Strategy Changes
- **Strategy unit tests (thin)** - Test only pure helper methods like error mapping, amount conversion, FQCN handling, returnUrl validation
- **Strategy behavior via TestWorkflowEnvironment** - All strategy logic tested through workflow since awaitAuthorization uses Workflow.await
- **Replay tests mandatory** - WorkflowReplayer.replayWorkflowExecution() static method over recorded histories
- **Re-init policy matrix tests** - Comprehensive state transition testing
- **Security validation tests** - ReturnUrl HTTPS + allowlist, webhook transactionId validation

### API Response Extensibility
```json
{
  "paymentMethod": "NEW_CARD_WEB",
  "transactionId": "dt-12345",
  "methodConfig": null  // Future: typed MethodConfig sealed interface
}
```

### Migration Safety
- **Clean replacement approach** - No backward compatibility needed (pre-production service)
- **Webhook path verification required** - Check no Datatrans dashboard config pins old path
- **201 Created for init** - Resource creation semantics (not 200 OK)
- **long amounts end-to-end** - All amounts in minor units (pence/cents) as long type
- **Configuration migration** - payment.security.allowed-return-url-hosts required

### Security Configuration Example
```yaml
payment:
  security:
    allowed-return-url-hosts:
      - "premierinn.com"
      - "premierinn.digital"
      - "www.premierinn.com"
integrations:
  payment:
    workflow:
      timeout: 30m
```

## Task Dependency Graph

```json
{
  "waves": [
    {
      "id": 0,
      "tasks": ["1.1", "1.2", "1.3", "1.4", "1.5", "1.6"]
    },
    {
      "id": 1,
      "tasks": ["2.1"]
    },
    {
      "id": 2,
      "tasks": ["2.2", "2.3", "2.4"]
    },
    {
      "id": 3,
      "tasks": ["3.1"]
    },
    {
      "id": 4,
      "tasks": ["3.2", "3.3", "3.4", "3.5"]
    },
    {
      "id": 5,
      "tasks": ["4.1", "4.2", "4.3", "4.4", "4.5", "4.6"]
    },
    {
      "id": 6,
      "tasks": ["5.1", "5.2", "5.3"]
    },
    {
      "id": 7,
      "tasks": ["6.1", "6.2", "6.3", "6.4"]
    },
    {
      "id": 8,
      "tasks": ["7.1", "7.2", "7.3", "7.4", "7.5", "7.6"]
    },
    {
      "id": 9,
      "tasks": ["8.1", "8.2", "8.3", "8.4", "8.5"]
    },
    {
      "id": 10,
      "tasks": ["9.1", "9.2", "9.3"]
    },
    {
      "id": 11,
      "tasks": ["10.1", "10.2"]
    }
  ]
}
```