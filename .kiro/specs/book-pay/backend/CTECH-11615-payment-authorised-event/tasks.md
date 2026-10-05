# Implementation Plan: PaymentAuthorisedEvent & Webhook Status Validation (CTECH-11615)

## Overview

This plan implements:
1. The `PaymentAuthorisedEvent` Kafka publication (both Mobile SDK and Secure Fields flows).
2. Webhook status validation for Mobile SDK (call `getTransactionStatus` before authorising).
3. Docker-compose Kafka setup for local development.
4. Removal of the postDeposit/confirmBooking/settle/updateBasket chain from both workflows
   (booking confirmation is now driven entirely by the downstream choreography after consuming
   the event).

The implementation follows the existing hexagonal architecture, Temporal activity/adapter
patterns, and versioning conventions already in the payment-orchestration-service.

## Tasks

- [x] 1. Domain model, port, and activity signature extension
  - [x] 1.1 Create `PaymentAuthorisedEvent` domain record
    - Create `PaymentAuthorisedEvent` record in `domain/model/` with fields: `basketId`,
      `transactionId`, `paymentProvider`, `paymentMethod`, `cardAlias`, `last4Digits`,
      `expiry`, `authorizedAmount`, `currency`
    - _Requirements: 1.1, 1.3_

  - [x] 1.2 Create `PaymentEventPublisherPort` secondary port interface
    - Create interface in `domain/ports/secondary/` with method `void publish(PaymentAuthorisedEvent event)`
    - _Requirements: 2.1_

  - [x] 1.3 Add `paymentMethod` field to `DatatransTransactionStatus` record
    - Extend the existing record to capture the payment method code from the Datatrans
      GET status response
    - _Requirements: 5.6_

  - [x] 1.4 Add `paymentMethod` field to `DatatransAuthorizeResponse` record
    - Extend the existing record to capture the payment method code from the Datatrans
      authorize response
    - _Requirements: 4.2_

  - [x] 1.5 Extend `PaymentActivities.publishAuthorisedPaymentEvent` signature
    - Add parameters: `paymentMethod`, `last4Digits`, `expiry` (total: 8 parameters)
    - Update Javadoc to document the full parameter set
    - _Requirements: 6.1, 6.4_

- [x] 2. Infrastructure: Kafka producer and configuration
  - [x] 2.1 Add `spring-kafka` dependency to `pom.xml`
    - Add `spring-boot-starter` for Kafka (managed by Spring Boot BOM)
    - Add `spring-kafka-test` for test scope
    - _Requirements: 2.6_

  - [x] 2.2 Add Kafka configuration to `application.yml`
    - Add `spring.kafka.bootstrap-servers` with env var placeholder
    - Add `spring.kafka.producer.acks=all`
    - Add `spring.kafka.producer.key-serializer` and `value-serializer`
    - Add `payment.events.topics.payment-authorised` with env var placeholder
    - _Requirements: 2.1, 2.2, 2.3, 2.4_

  - [x] 2.3 Add Kafka bootstrap to `application-local.yml`
    - Set `spring.kafka.bootstrap-servers: localhost:9092`
    - _Requirements: 9.4_

  - [x] 2.4 Create `KafkaProducerConfig` configuration class
    - Configure `KafkaTemplate<String, PaymentAuthorisedEvent>` bean
    - Use `JsonSerializer` for values, `StringSerializer` for keys
    - _Requirements: 2.3_

  - [x] 2.5 Create `KafkaPaymentEventPublisher` adapter implementing `PaymentEventPublisherPort`
    - Inject `KafkaTemplate` and topic name from configuration
    - Publish with key = `event.basketId()`
    - Log at INFO on success (basketId, transactionId only — no card data)
    - Log at ERROR on failure (basketId, transactionId, error — no card data)
    - _Requirements: 2.1, 2.5, 7.1, 7.2, 7.3_

  - [x] 2.6 Wire `PaymentEventPublisherPort` into `PaymentActivitiesImpl`
    - Inject `PaymentEventPublisherPort` via constructor
    - Replace the no-op logging placeholder with: construct `PaymentAuthorisedEvent` from
      activity parameters (derive `last4Digits` from masked card, format `expiry` as MM/YY),
      call `publisher.publish(event)`
    - Keep the `cardAlias != null ? "present" : "absent"` pattern for any residual logging
    - _Requirements: 3.3, 4.3, 6.4_

- [x] 3. Workflow: Mobile SDK webhook status validation and event publication
  - [x] 3.1 Add `Workflow.getVersion` marker for CTECH-11615 in `MobileSdkPaymentWorkflowImpl`
    - Add `Workflow.getVersion("CTECH-11615-payment-authorised-event", Workflow.DEFAULT_VERSION, 1)`
    - Old version: existing behavior (direct authorize from webhook, old 5-param activity call)
    - New version: status validation + extended 8-param activity call
    - _Requirements: 6.5_

  - [x] 3.2 Implement webhook status validation in `webhookReceived`
    - After guard checks and when webhook `status == "authorized"`:
      - Set `authorizationInProgress = true`
      - Call `statusActivities.getTransactionStatus(transactionId)`
      - If confirmed status is `authorized` or `settled`:
        - Build `AuthorizationOutcome` from the STATUS RESPONSE (not webhook payload)
        - Call existing `authorize(outcome)` path
      - If confirmed status is `failed`:
        - Set `paymentStatus = FAILED`
      - If confirmed status is `canceled`:
        - Set `paymentStatus = CANCELLED`
      - If `TransactionNotFoundException`:
        - Set `paymentStatus = FAILED`
      - If transient error after retries:
        - Set `paymentStatus = FAILED`
      - Finally: `authorizationInProgress = false`
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.8_

  - [x] 3.3 Update `authorize()` method to use extended activity signature and remove settlement chain
    - Under version 1: call `publishAuthorisedPaymentEvent` with all 8 parameters
    - Extract `paymentMethod` from `DatatransTransactionStatus.paymentMethod()`
    - Derive `last4Digits` from masked card number (last 4 chars)
    - Format `expiry` as `"MM/YY"` from expiryMonth + expiryYear
    - Remove `postDeposit`, `confirmBooking`, `settleTransactionV2`, `updateBasket` calls
    - After event publication: set `paymentStatus = SETTLED`
    - _Requirements: 3.1, 3.2, 3.5, 6.2_

  - [x] 3.4 Update `AuthorizationOutcome` record to include `paymentMethod` and `maskedCardNumber`
    - Add `paymentMethod` and `maskedCardNumber` fields
    - Update `authorizationOutcome(DatatransTransactionStatus)` to extract them
    - Update `authorizationOutcome(MobileSdkWebhookRequest)` for reconciliation compatibility
    - _Requirements: 5.6, 3.2_

- [x] 4. Workflow: Secure Fields event publication and chain removal
  - [x] 4.1 Add `Workflow.getVersion` marker for CTECH-11615 in `SecureFieldsPaymentWorkflowImpl`
    - Add `Workflow.getVersion("CTECH-11615-payment-authorised-event", Workflow.DEFAULT_VERSION, 1)`
    - _Requirements: 6.5_

  - [x] 4.2 Change `authorizeTransaction` activity to return `DatatransAuthorizeResponse`
    - Update `PaymentActivities.authorizeTransaction` return type from `void` to
      `DatatransAuthorizeResponse`
    - Update `PaymentActivitiesImpl.authorizeTransaction` to return the response from
      `datatransOutPort`
    - Update `DatatransOutPort.authorizeTransaction` to return the response
    - Update `DatatransRestAdapter.authorizeTransaction` to return the deserialized response
      (currently it already fetches it but discards it)
    - _Requirements: 4.2_

  - [x] 4.3 Update `SecureFieldsPaymentWorkflowImpl.authorize()` — add event, remove chain
    - After `authorizeTransaction` returns successfully and `paymentStatus = AUTHORIZED`:
    - Extract card data from `DatatransAuthorizeResponse` (alias, masked → last4, expiry → MM/YY,
      paymentMethod)
    - Call `activities.publishAuthorisedPaymentEvent(basketId, transactionId, cardAlias,
      authorizedAmount, currency, paymentMethod, last4Digits, expiry)`
    - Remove `postDeposit`, `confirmBooking`, `settleTransaction`, `updateBasket` calls
    - After event publication: set `paymentStatus = SETTLED`
    - _Requirements: 4.1, 4.3, 4.4, 4.5, 6.3_

- [x] 5. Infrastructure: Docker Compose
  - [x] 5.1 Add Kafka and kafka-init services to `docker-compose.yml`
    - Add KRaft-mode Kafka broker (Strimzi image) on port 9092
    - Add `kafka-init` service that creates `payment-authorised` topic
    - Add healthcheck for Kafka readiness
    - _Requirements: 9.1, 9.2, 9.3_

- [x] 6. Testing
  - [x] 6.1 Unit test: `PaymentAuthorisedEvent` serialization
    - Verify JSON serialization produces expected structure with `last4Digits` and `expiry`
    - Verify null fields are handled gracefully
    - _Requirements: 8.1_

  - [x] 6.2 Unit test: `KafkaPaymentEventPublisher`
    - Mock `KafkaTemplate`, verify correct topic, key, and payload
    - Verify success logging (no card data logged)
    - Verify failure logging (no card data logged)
    - _Requirements: 8.2, 7.1, 7.2, 7.3_

  - [x] 6.3 Unit test: `PaymentActivitiesImpl.publishAuthorisedPaymentEvent`
    - Verify it constructs `PaymentAuthorisedEvent` correctly from all 8 params
    - Verify it calls `PaymentEventPublisherPort.publish`
    - Verify null cardAlias/amount are handled
    - Verify `last4Digits` and `expiry` derivation logic
    - _Requirements: 8.2_

  - [x] 6.4 Workflow test: Mobile SDK webhook triggers status validation
    - Send authorized webhook signal
    - Assert `getTransactionStatus` is called before `publishAuthorisedPaymentEvent`
    - Assert card data in event comes from status response
    - _Requirements: 8.5_

  - [x] 6.5 Workflow test: Mobile SDK webhook with status validation returning `failed`
    - Send authorized webhook, mock `getTransactionStatus` to return `status=failed`
    - Assert `paymentStatus = FAILED` (not AUTHORIZED)
    - Assert `publishAuthorisedPaymentEvent` is NOT called
    - _Requirements: 8.6_

  - [x] 6.6 Workflow test: Mobile SDK publishes event after status validation succeeds
    - Send authorized webhook, mock status returns authorized
    - Assert `publishAuthorisedPaymentEvent` is called with correct params
    - Assert workflow reaches SETTLED
    - _Requirements: 8.3_

  - [x] 6.7 Workflow test: Secure Fields publishes event after authorisation
    - Trigger authorize signal
    - Assert `publishAuthorisedPaymentEvent` is called
    - Assert no postDeposit/confirmBooking/settle/updateBasket calls
    - Assert workflow reaches SETTLED
    - _Requirements: 8.4_

  - [x] 6.8 Workflow test: Reconciliation poller still works (regression)
    - Verify reconciliation poller triggers authorization with extended event publication
    - Verify no behavioral change from CTECH-12128 (other than removal of settlement chain)
    - _Requirements: 8.7_

- [x] 7. Final checkpoint
  - Run full test suite: `cd backend && ./mvnw clean install -pl book-pay/services/payment-orchestration-service -am`
  - Run checkstyle: zero warnings
  - Verify JaCoCo coverage for new non-excluded classes

## Notes

- The activity signature change is a breaking Temporal change for replay. The version marker
  is critical: without it, in-flight workflows would fail on replay when Temporal encounters
  the old history with 5 parameters but the code expects 8.
- The webhook status validation adds one network call to the Mobile SDK authorisation path.
  This is acceptable because: (a) the webhook is already async (signal, not update), (b) the
  existing `statusActivities` stub has a 5s timeout, and (c) the reconciliation poller already
  makes the same call.
- `DatatransCardInfo` currently lacks `paymentMethod`. Rather than changing the shared record
  used by both flows, we add `paymentMethod` as a top-level field on both
  `DatatransAuthorizeResponse` and `DatatransTransactionStatus`.
- The `DatatransRestAdapter` already fetches the full authorize response body including card
  data but currently discards it. The change in task 4.2 surfaces the return value.
- The postDeposit, confirmBooking, settleTransaction/V2, and updateBasket activities are
  removed from both workflow implementations. The downstream choreography (Basket → BAOP → OPERA)
  handles all of that after consuming the event.
- `last4Digits` is derived from the masked card number (last 4 characters of
  `DatatransCardInfo.masked()`). `expiry` is formatted as `"MM/YY"` from `expiryMonth` and
  `expiryYear`.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "1.3", "1.4"] },
    { "id": 1, "tasks": ["1.5", "2.1"] },
    { "id": 2, "tasks": ["2.2", "2.3", "2.4"] },
    { "id": 3, "tasks": ["2.5", "2.6"] },
    { "id": 4, "tasks": ["3.1", "3.4", "4.1", "4.2"] },
    { "id": 5, "tasks": ["3.2", "3.3", "4.3"] },
    { "id": 6, "tasks": ["5.1"] },
    { "id": 7, "tasks": ["6.1", "6.2", "6.3"] },
    { "id": 8, "tasks": ["6.4", "6.5", "6.6", "6.7", "6.8"] },
    { "id": 9, "tasks": ["7"] }
  ]
}
```
