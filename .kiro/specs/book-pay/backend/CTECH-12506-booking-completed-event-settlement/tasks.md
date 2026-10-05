---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-12506
---
# Implementation Plan: BookingCompletedEvent Settlement

## Overview
Implement deferred payment settlement by consuming BookingCompletedEvents from Kafka and signaling Temporal workflows to settle or cancel payments based on booking outcome.

## Tasks

- [x] 1. Domain Model and Port Updates
  - [x] 1.1 Create BookingCompletedEvent domain model
    - Add `record BookingCompletedEvent(String basketReference, String status)` in `domain/model/`
    - Add validation for status values (COMPLETED, FAILED)
    - _Requirements: 1.2_
  - [x] 1.2 Add cancelTransaction method to DatatransOutPort
    - Define `void cancelTransaction(String transactionId, String merchantId)` in `DatatransOutPort`
    - Add Javadoc with API contract details
    - _Requirements: 4.1_
  - [x] 1.3 Add cancelTransaction method to PaymentActivities
    - Define `@ActivityMethod void cancelTransaction(String transactionId, String merchantId)` 
    - Add Javadoc with error handling expectations
    - _Requirements: 4.1_
  - [x] 1.4 Add bookingCompleted signal to workflow interfaces
    - Add `@SignalMethod void bookingCompleted(BookingCompletedEvent event)` to `SecureFieldsPaymentWorkflow`
    - Add same signal method to `MobileSdkPaymentWorkflow`
    - _Requirements: 2.1, 2.2_

- [x] 2. Infrastructure Layer Implementation
  - [x] 2.1 Implement Datatrans cancel API integration
    - Add `cancelTransaction` implementation to `DatatransRestAdapter`
    - Use `POST /v2/transactions/{transactionId}/cancel` with Basic Auth (merchantId)
    - Map HTTP errors to domain exceptions (404 → TransactionNotFoundException, 400 → DatatransGatewayException)
    - _Requirements: 4.5_
  - [-] 2.2 Implement PaymentActivitiesImpl cancel method
    - Add `cancelTransaction` implementation delegating to `DatatransOutPort`
    - Add logging for cancel attempts and outcomes
    - _Requirements: 4.1_
  - [x] 2.3 Create PaymentWorkflowSignaler component
    - Implement `@Component PaymentWorkflowSignaler` with Temporal WorkflowClient injection
    - Add `signalBookingCompleted(String basketId, BookingCompletedEvent event)` method
    - Use workflow ID pattern `payment-{basketId}` to target the correct workflow
    - Handle workflow not found errors gracefully (log warning, do not throw)
    - _Requirements: 2.3, 6.3_

- [x] 3. Kafka Consumer Implementation
  - [x] 3.1 Create BookingCompletedEventConsumer
    - Implement `@Component` with `@KafkaListener` annotation
    - Add JSON deserialization for BookingCompletedEvent
    - Delegate to PaymentWorkflowSignaler for workflow signaling
    - Add error handling for malformed messages
    - _Requirements: 1.1, 1.2, 1.3, 1.4_
  - [-] 3.2 Add Kafka configuration properties
    - Update `application.yml` with `booking-completed` topic configuration
    - Add consumer group ID and commit settings
    - Add environment variable placeholders
    - _Requirements: 5.1, 5.2, 5.3_

- [x] 4. Workflow Implementation Updates
  - [x] 4.1 Update SecureFieldsPaymentWorkflowImpl settlement timing
    - Remove immediate `settleTransaction` call after `publishAuthorisedPaymentEvent`
    - After AUTHORIZED, workflow awaits BookingCompletedEvent signal
    - Implement `bookingCompleted` signal handler with settlement/cancellation logic
    - Add state guards to prevent duplicate processing
    - _Requirements: 2.4, 2.5, 3.1, 3.2, 3.3, 3.4, 4.2, 4.3_
  - [x] 4.2 Update MobileSdkPaymentWorkflowImpl settlement timing
    - Remove immediate `settleTransaction` call after `publishAuthorisedPaymentEvent`
    - After AUTHORIZED, workflow awaits BookingCompletedEvent signal
    - Implement `bookingCompleted` signal handler with same logic as Secure Fields
    - Add state guards to prevent duplicate processing
    - _Requirements: 2.4, 2.5, 3.1, 3.2, 3.3, 3.4, 4.2, 4.3_

- [x] 5. Configuration and Hotel Updates
  - [x] 5.1 Add HAVFOR to provisioned hotels list
    - Update `application.yml` merchant-id.provisioned-hotels list
    - Add HAVFOR alongside HARHOR and GRESOU
    - _Requirements: 5.4_

- [x] 6. Testing
  - [x] 6.1 Unit tests for BookingCompletedEventConsumer
    - Test successful message processing and workflow signaling
    - Test error handling for malformed JSON
    - _Requirements: 1.1, 1.2, 1.3, 1.4_
  - [x] 6.2 Unit tests for PaymentWorkflowSignaler
    - Test successful workflow signaling with mock Temporal client
    - Test workflow not found error handling
    - _Requirements: 6.3_
  - [x] 6.3 Workflow tests for SecureFieldsPaymentWorkflowImpl
    - Test signal handling in AUTHORIZED state (settlement path)
    - Test signal handling in AUTHORIZED state (cancellation path)
    - Test signal ignored in non-AUTHORIZED states
    - Test unknown status defaults to cancellation
    - _Requirements: 2.1, 2.4, 2.5, 6.2_
  - [x] 6.4 Workflow tests for MobileSdkPaymentWorkflowImpl
    - Test signal handling in AUTHORIZED state (settlement path)
    - Test signal handling in AUTHORIZED state (cancellation path)
    - Test signal ignored in non-AUTHORIZED states
    - Test unknown status defaults to cancellation
    - _Requirements: 2.2, 2.4, 2.5, 6.2_

## Notes
- No property-based or integration testing — focus on unit tests and Temporal workflow tests
- All existing PaymentAuthorisedEvent publishing remains unchanged
- Workflows await the BookingCompletedEvent signal after reaching AUTHORIZED
- Tracing propagates automatically via Micrometer + Brave (already configured)
- design-e2e.md already updated with the deferred settlement flow

## Task Dependency Graph
```json
{
  "waves": [
    {
      "id": 0,
      "tasks": ["1.1", "1.2", "1.3", "1.4", "5.1"]
    },
    {
      "id": 1,
      "tasks": ["2.1", "2.2", "3.2"]
    },
    {
      "id": 2,
      "tasks": ["2.3", "3.1"]
    },
    {
      "id": 3,
      "tasks": ["4.1", "4.2"]
    },
    {
      "id": 4,
      "tasks": ["6.1", "6.2", "6.3", "6.4"]
    }
  ]
}
```
