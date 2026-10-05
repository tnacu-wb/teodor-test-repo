---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-10339
---
# Requirements Document

## Introduction
Remove the `transactionId` field from the `POST /api/payments/authorize` request in the Payment Orchestration Service. Currently the authorize API accepts both `transactionId` and `basketId` in the request body. However, the Temporal workflow already stores the `transactionId` during the Secure Fields init phase (when Datatrans returns it). The externally-provided `transactionId` is therefore redundant and creates an unnecessary coupling between the frontend and the orchestrator's internal transaction state. After this change, the authorize API will accept only `basketId`, and the workflow will use its own stored `transactionId` for all downstream operations.

## Glossary
- **transactionId**: The Datatrans transaction identifier, returned during Secure Fields initialization and stored in the Temporal workflow state.
- **basketId**: The basket/reservation identifier passed from the frontend. Used to derive the Temporal workflow ID (`payment-{basketId}`).
- **AuthorizePaymentRequest**: The DTO representing the authorize API request body.
- **SecureFieldsPaymentWorkflow**: The Temporal workflow interface that orchestrates the payment lifecycle for the web channel.
- **PaymentWorkflowPort**: The secondary port interface for interacting with Temporal workflows.

## Requirements

### Requirement 1: Remove transactionId from Authorize API Request
**User Story:** As a frontend client, I want to authorize payment using only the basketId, so that I do not need to track and pass back the Datatrans transactionId.

#### Acceptance Criteria
1. THE `AuthorizePaymentRequest` record SHALL contain only the `basketId` field
2. THE `transactionId` field SHALL be removed from `AuthorizePaymentRequest`
3. THE `POST /api/payments/authorize` endpoint SHALL accept a JSON body containing only `basketId`
4. THE endpoint SHALL continue to return `204 No Content` on success
5. THE existing `basketId` validation (not blank, pattern match) SHALL be retained

### Requirement 2: Workflow Uses Stored transactionId
**User Story:** As the Payment Orchestrator, I want the authorize signal to use the transactionId already stored in the Temporal workflow state, so that the payment flow is self-contained and not dependent on external callers passing the correct transactionId.

#### Acceptance Criteria
1. THE `SecureFieldsPaymentWorkflow.authorize()` signal method SHALL accept no parameters (change from `authorize(String transactionId)` to `authorize()`)
2. THE workflow implementation SHALL use its stored `this.transactionId` field (set during `initSecureFields`) for all downstream authorize activities
3. THE workflow SHALL fail with error code `GATEWAY_ERROR` and message "Workflow not initialized: missing transaction state" if `this.transactionId` is null when authorize is signalled
4. THE workflow SHALL continue to pass `this.transactionId` to `authorizeActivities.authorizeTransaction()`, `postDeposit()`, `confirmBooking()`, `settleTransaction()`, and `updateBasket()`

### Requirement 3: Update Port and Adapter Signatures
**User Story:** As a developer, I want the internal port and adapter interfaces to reflect the removal of transactionId, so that the codebase is consistent and no dead parameters exist.

#### Acceptance Criteria
1. THE `PaymentOrchestrationInPort.authorizePayment()` method SHALL accept only `basketId` (remove `transactionId` parameter)
2. THE `PaymentWorkflowPort.signalAuthorize()` method SHALL accept only `basketId` (remove `transactionId` parameter)
3. THE `TemporalWorkflowAdapter.signalAuthorize()` implementation SHALL no longer pass `transactionId` to the workflow signal
4. THE `PaymentOrchestrationInPortImpl.authorizePayment()` SHALL call `signalAuthorize(basketId)` with only basketId
5. THE `PaymentWorkflowPortStub.signalAuthorize()` SHALL be updated to match the new signature

### Requirement 4: Backward Compatibility Consideration
**User Story:** As a frontend developer, I want the API change to be clearly communicated, so that I can update my client code accordingly.

#### Acceptance Criteria
1. THE OpenAPI schema SHALL reflect the updated request body (only `basketId`)
2. THE service SHALL return `400 Bad Request` if an unrecognized field is sent (default Jackson behaviour with strict deserialization) — OR alternatively, ignore unknown fields gracefully (choose one approach and document)
3. THE service SHALL log a warning-level message at the controller when the authorize endpoint is called, including the basketId for traceability

### Requirement 5: Existing Test Coverage Must Be Updated
**User Story:** As a developer, I want all existing unit tests to be updated to reflect the new signatures, so that the test suite passes and validates the new behaviour.

#### Acceptance Criteria
1. ALL unit tests referencing `authorizePayment(transactionId, basketId)` SHALL be updated to `authorizePayment(basketId)`
2. ALL unit tests referencing `signalAuthorize(basketId, transactionId)` SHALL be updated to `signalAuthorize(basketId)`
3. THE workflow unit tests SHALL verify that the workflow uses its stored `transactionId` (not an externally provided one)
4. THE controller tests SHALL verify the new request body format (only `basketId`)
5. ALL tests SHALL pass after the changes
