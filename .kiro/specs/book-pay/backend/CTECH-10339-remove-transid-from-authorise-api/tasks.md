---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-10339
---
# Implementation Plan: Remove transactionId from Authorize API

## Overview

Remove `transactionId` from the `POST /api/payments/authorize` request. The Temporal workflow already stores `transactionId` during Secure Fields initialization, making the external parameter redundant. Update all layers (DTO, controller, ports, adapters, workflow interface, workflow implementation) and their corresponding unit tests.

## Tasks

- [x] 1. Update Domain Layer
  - [x] 1.1 Update `AuthorizePaymentRequest` DTO
    - Modify `domain/model/payment/in/AuthorizePaymentRequest.java`
    - Remove `transactionId` field and all its annotations (`@Schema`, `@NotBlank`, `@Size`)
    - Keep only `basketId` with its existing validation annotations
    - Update the record Javadoc to reflect the single-field contract
    - _Requirements: 1.1, 1.2, 1.3, 1.5_

  - [x] 1.2 Update `PaymentOrchestrationInPort` interface
    - Modify `domain/ports/primary/PaymentOrchestrationInPort.java`
    - Change `void authorizePayment(String transactionId, String basketId)` to `void authorizePayment(String basketId)`
    - Update Javadoc
    - _Requirements: 3.1_

  - [x] 1.3 Update `PaymentOrchestrationInPortImpl`
    - Modify `domain/logic/PaymentOrchestrationInPortImpl.java`
    - Update `authorizePayment()` method signature to accept only `basketId`
    - Change call from `paymentWorkflowPort.signalAuthorize(basketId, transactionId)` to `paymentWorkflowPort.signalAuthorize(basketId)`
    - Update log statement to remove `transactionId` reference
    - _Requirements: 3.1, 3.4_

  - [x] 1.4 Update `PaymentWorkflowPort` interface
    - Modify `domain/ports/secondary/PaymentWorkflowPort.java`
    - Change `AuthorizeResult signalAuthorize(String basketId, String transactionId)` to `AuthorizeResult signalAuthorize(String basketId)`
    - Update Javadoc to reflect that transactionId is no longer passed externally
    - _Requirements: 3.2_

- [x] 2. Update Temporal Workflow Interface and Implementation
  - [x] 2.1 Update `SecureFieldsPaymentWorkflow` interface
    - Modify `domain/workflow/SecureFieldsPaymentWorkflow.java`
    - Change `@SignalMethod void authorize(String transactionId)` to `@SignalMethod void authorize()`
    - Update Javadoc: "Signal to authorize the payment using the workflow's stored transactionId"
    - _Requirements: 2.1_

  - [x] 2.2 Update `SecureFieldsPaymentWorkflowImpl.authorize()` method
    - Modify `domain/workflow/SecureFieldsPaymentWorkflowImpl.java`
    - Change method signature from `authorize(String transactionId)` to `authorize()`
    - Replace all uses of the parameter `transactionId` with `this.transactionId`
    - Add guard clause: if `this.transactionId == null`, set `authorizeResult` to failure with `GATEWAY_ERROR` / "Workflow not initialized: missing transaction state" and set `paymentStatus = FAILED`, then return
    - Keep existing guard for `bookingReference == null || reservationId == null || currency == null`
    - _Requirements: 2.1, 2.2, 2.3, 2.4_

- [x] 3. Update Infrastructure Adapters
  - [x] 3.1 Update `TemporalWorkflowAdapter.signalAuthorize()`
    - Modify `infrastructure/temporal/TemporalWorkflowAdapter.java`
    - Change method signature from `signalAuthorize(String basketId, String transactionId)` to `signalAuthorize(String basketId)`
    - Update the `workflowStub.authorize(transactionId)` call to `workflowStub.authorize()`
    - Remove `transactionId` from log statements (keep `workflowId` logging)
    - _Requirements: 3.2, 3.3_

  - [x] 3.2 Update `PaymentWorkflowPortStub.signalAuthorize()`
    - Modify `infrastructure/temporal/PaymentWorkflowPortStub.java`
    - Change method signature to match updated `PaymentWorkflowPort` interface
    - _Requirements: 3.5_

  - [x] 3.3 Update `PaymentController.authorizePayment()`
    - Modify `infrastructure/rest/controller/payment/PaymentController.java`
    - Change `paymentOrchestrationInPort.authorizePayment(request.transactionId(), request.basketId())` to `paymentOrchestrationInPort.authorizePayment(request.basketId())`
    - Update log statement: remove `transactionId`, keep `basketId`
    - _Requirements: 1.3, 4.3_

- [x] 4. Update Unit Tests
  - [x] 4.1 Update `PaymentOrchestrationInPortImplAuthorizeTest`
    - Update all `authorizePayment()` calls to single-parameter version
    - Update all `signalAuthorize()` mock setups to single-parameter version
    - Remove any `TRANSACTION_ID` constant usage from authorize test scenarios
    - _Requirements: 5.1, 5.2_

  - [x] 4.2 Update `TemporalWorkflowAdapterAuthorizeTest`
    - Update all `signalAuthorize()` calls to single-parameter version
    - Verify `workflowStub.authorize()` is called with no arguments
    - Remove `TRANSACTION_ID` from test scenarios where it was passed to signalAuthorize
    - _Requirements: 5.2_

  - [x] 4.3 Update `PaymentController` authorize tests (if they exist)
    - Update request body in test fixtures to contain only `basketId`
    - Remove `transactionId` from test request JSON
    - _Requirements: 5.4_

  - [x] 4.4 Update workflow implementation authorize tests
    - Signal `authorize()` with no arguments
    - Verify workflow uses stored `this.transactionId` in activity calls
    - Add test: authorize signalled with null stored transactionId → GATEWAY_ERROR result
    - _Requirements: 5.3_

  - [x] 4.5 Verify all tests pass
    - Run `cd backend && ./mvnw test -pl book-pay/services/payment-orchestration-service`
    - Fix any compilation or test failures resulting from the signature changes
    - _Requirements: 5.5_

## Notes

- The `transactionId` is stored in the workflow state at line 141 of `SecureFieldsPaymentWorkflowImpl` during `initSecureFields`: `this.transactionId = txnId`
- Jackson records ignore unknown fields by default, so clients that still send `transactionId` will not get errors — it will simply be ignored
- If there are in-flight Temporal workflows at deployment time that were started with the old `authorize(String transactionId)` signal signature, Temporal versioning (`Workflow.getVersion`) may be needed. Assess at deployment time.
- The `MobileSdkPaymentWorkflow` (if it exists) may also have an authorize signal — check and update if applicable

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "1.4", "2.1"] },
    { "id": 1, "tasks": ["1.3", "2.2", "3.1", "3.2", "3.3"] },
    { "id": 2, "tasks": ["4.1", "4.2", "4.3", "4.4"] },
    { "id": 3, "tasks": ["4.5"] }
  ]
}
```
