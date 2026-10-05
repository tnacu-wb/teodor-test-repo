---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-12506
---
# Requirements Document

## Introduction
The Payment Orchestration Service must consume BookingCompletedEvents from Kafka to determine when to settle or cancel payments. This changes the current settlement timing from immediate (after PaymentAuthorisedEvent) to deferred (after booking completion). Both Secure Fields (web) and Mobile SDK (native) workflows require this capability to handle downstream booking confirmation failures properly.

## Glossary
- **BookingCompletedEvent**: Kafka event published by Basket Service indicating final booking status
- **Settlement**: Final capture of authorized payment funds via Datatrans
- **Cancellation**: Void/cancel of authorized payment via Datatrans when booking fails
- **Payment Workflow**: Temporal workflow managing payment lifecycle (SecureFieldsPaymentWorkflowImpl, MobileSdkPaymentWorkflowImpl)
- **Workflow Signal**: Temporal mechanism for external systems to notify running workflows

## Requirements

### Requirement 1: Kafka Consumer Integration
**User Story:** As the Payment Orchestration Service, I want to consume BookingCompletedEvents from Kafka, so that I can determine the final booking status and act accordingly.

#### Acceptance Criteria
1.1 THE system SHALL consume messages from the `booking-completed` Kafka topic
1.2 THE system SHALL deserialize BookingCompletedEvent messages with basketReference and status fields
1.3 THE system SHALL handle consumer failures gracefully without losing messages
1.4 WHEN a BookingCompletedEvent is received THE system SHALL log the event details (basketReference and status)

### Requirement 2: Workflow Signal Handler
**User Story:** As a payment workflow, I want to receive BookingCompletedEvent signals, so that I can proceed with settlement or cancellation based on booking outcome.

#### Acceptance Criteria
2.1 THE SecureFieldsPaymentWorkflowImpl SHALL implement a signal method to handle BookingCompletedEvent
2.2 THE MobileSdkPaymentWorkflowImpl SHALL implement a signal method to handle BookingCompletedEvent
2.3 THE signal handler SHALL only process events when workflow status is AUTHORIZED
2.4 WHEN booking status is COMPLETED THE workflow SHALL proceed to settlement
2.5 WHEN booking status is FAILED THE workflow SHALL cancel the payment

### Requirement 3: Deferred Settlement
**User Story:** As a payment workflow, I want to defer settlement until booking completion, so that failed bookings don't result in captured payments.

#### Acceptance Criteria
3.1 THE workflow SHALL NOT settle payments immediately after publishing PaymentAuthorisedEvent
3.2 THE workflow SHALL await BookingCompletedEvent signal after reaching AUTHORIZED state
3.3 WHEN BookingCompletedEvent status is COMPLETED THE workflow SHALL call settleTransaction activity
3.4 WHEN settlement succeeds THE workflow SHALL transition to SETTLED state
3.5 WHEN settlement fails THE workflow SHALL transition to FAILED state

### Requirement 4: Payment Cancellation
**User Story:** As a payment workflow, I want to cancel authorized payments when booking fails, so that customers are not charged for unsuccessful bookings.

#### Acceptance Criteria
4.1 THE system SHALL implement a cancelTransaction activity for Datatrans integration
4.2 WHEN BookingCompletedEvent status is FAILED THE workflow SHALL call cancelTransaction activity
4.3 WHEN cancellation succeeds THE workflow SHALL transition to CANCELLED state
4.4 WHEN cancellation fails THE workflow SHALL transition to FAILED state
4.5 THE cancelTransaction activity SHALL call Datatrans POST /v2/transactions/{transactionId}/cancel API

### Requirement 5: Configuration Management
**User Story:** As an operator, I want configurable Kafka consumer settings, so that I can tune performance and reliability for different environments.

#### Acceptance Criteria
5.1 THE system SHALL support configurable Kafka bootstrap servers via application.yml
5.2 THE system SHALL support configurable consumer group ID for the booking-completed topic
5.3 THE system SHALL support configurable topic name for booking-completed events
5.4 THE system SHALL add HAVFOR to the provisioned hotels list alongside HARHOR and GRESOU

### Requirement 6: Error Handling and Observability
**User Story:** As a developer, I want comprehensive logging and error handling, so that I can troubleshoot payment settlement issues effectively.

#### Acceptance Criteria
6.1 THE system SHALL log all BookingCompletedEvent processing attempts with correlation details
6.2 THE system SHALL handle unknown booking statuses by defaulting to FAILED workflow transition
6.3 THE system SHALL handle workflow not found errors gracefully when signaling
6.4 WHEN Datatrans settlement or cancellation fails THE system SHALL log the error with transaction details
6.5 THE system SHALL increment appropriate metrics for settlement success/failure rates