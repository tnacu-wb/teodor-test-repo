# New Card Payment Specification

## Purpose

Paying for a hotel reservation with a card the guest has not used before, and having the
resulting booking confirmed. The capability covers the whole path from the guest choosing to
pay to the authorisation being captured: collecting card details without those details
entering Whitbread systems, completing Strong Customer Authentication, authorising the
amount, telling the booking pipeline the payment succeeded, and capturing the money only
once the booking is confirmed.

One contract covers both surfaces the guest can pay from — the Premier Inn web payment page
and the native iOS and Android apps. The surfaces differ in how card details are collected
and how authorisation is triggered; they do not differ in the outcome the guest is owed.

Out of scope: paying with a stored or wallet card, Apple Pay, Google Pay, PayPal, refunds,
and amend or cancel journeys. Those are separate capabilities.

## Requirements

### Requirement: Payment session initialisation

The system SHALL open a payment session for a basket on request, naming the integration the
guest is paying through, and SHALL return a gateway transaction identifier the client uses to
collect card details. The system SHALL derive the amount, currency and booking reference from
the reservation rather than accepting them from the client.

A session SHALL be scoped to exactly one basket, and at most one live gateway transaction
SHALL exist per basket at any time.

#### Scenario: Web session opened

- **WHEN** a client requests a session for a basket, naming the web card integration and
  supplying a return address for the authentication redirect
- **THEN** the system SHALL create a gateway transaction for the reservation's amount and
  currency
- **AND** SHALL respond `201` with the transaction identifier and the integration it was
  opened for

#### Scenario: Mobile session opened

- **WHEN** a client requests a session for a basket, naming the native card integration
- **THEN** the system SHALL create a gateway transaction for the reservation's amount and
  currency, registering a notification address that identifies the basket
- **AND** SHALL respond `201` with the transaction identifier and the integration it was
  opened for
- **AND** SHALL NOT require a return address, because authentication is completed inside the
  app

#### Scenario: Basket does not exist

- **WHEN** a session is requested for a basket that cannot be found
- **THEN** the system SHALL respond `404` with `BASKET_NOT_FOUND` and SHALL NOT create a
  gateway transaction

#### Scenario: Reservation is missing payment-critical detail

- **WHEN** the reservation resolves but has no booking reference, no total cost, or no
  currency
- **THEN** the system SHALL respond `502` with `GATEWAY_ERROR` and SHALL NOT create a gateway
  transaction

### Requirement: Return address restriction

The system SHALL accept an authentication return address only when it uses HTTPS and its host
is on a configured allow-list. The system SHALL reject any other address before a gateway
transaction is created.

#### Scenario: Insecure return address

- **WHEN** a web session is requested with a return address using HTTP
- **THEN** the system SHALL respond `400` with `INVALID_REQUEST` and SHALL NOT create a
  gateway transaction

#### Scenario: Unrecognised return host

- **WHEN** a web session is requested with an HTTPS return address whose host is not
  allow-listed
- **THEN** the system SHALL respond `400` with `INVALID_REQUEST` and SHALL NOT create a
  gateway transaction

### Requirement: Card payment availability

The system SHALL confirm that card payment is available for the reservation's hotel, guest
type and client channel before opening a gateway transaction, and SHALL refuse the session
when it is not.

#### Scenario: Card payment not offered for the hotel

- **WHEN** a session is requested and card payment is absent, disabled, or not served by the
  card gateway for that hotel and channel
- **THEN** the system SHALL respond `422` with `PAYMENT_METHOD_NOT_AVAILABLE` and SHALL NOT
  create a gateway transaction

#### Scenario: Accepted card brands restrict native card entry

- **WHEN** a native session is opened and the hotel advertises a set of accepted card brands
- **THEN** the system SHALL pass those brands to the gateway so the app offers only cards the
  hotel accepts

### Requirement: Amount derived from the reservation

The system SHALL charge the reservation's total cost of stay, converted to the currency's
minor units, and SHALL refuse a payment whose amount cannot be represented exactly.

#### Scenario: Amount converted to minor units

- **WHEN** a session is opened for a reservation priced in a supported currency
- **THEN** the system SHALL send the gateway the amount in that currency's minor units

#### Scenario: Unsupported currency

- **WHEN** a session is opened for a reservation priced in a currency the system does not
  support
- **THEN** the system SHALL refuse the session rather than charge a guessed amount

#### Scenario: Amount finer than the currency allows

- **WHEN** a reservation total carries more precision than the currency's minor unit permits
- **THEN** the system SHALL refuse the session rather than round the guest's charge

### Requirement: Basket reserved for payment

The system SHALL mark the basket as pending payment before returning a transaction identifier
to the client, so that downstream booking services accept the payment notification when it
arrives. Failure to mark the basket SHALL prevent the payment from starting.

#### Scenario: Basket marked before the client can pay

- **WHEN** a gateway transaction has been created for a session
- **THEN** the system SHALL move the basket to its pending-payment state before returning the
  transaction identifier

#### Scenario: Basket cannot be marked

- **WHEN** the basket cannot be moved to its pending-payment state
- **THEN** the system SHALL fail the session, SHALL NOT return a transaction identifier, and
  SHALL leave the payment open for the guest to retry

### Requirement: Re-initialisation of an unfinished payment

The system SHALL allow a guest to start again on a payment that has not been authorised,
replacing any live gateway transaction with a fresh one so the guest always pays the current
amount. The system SHALL refuse re-initialisation once money is authorised or held.

#### Scenario: Retrying after a failed attempt

- **WHEN** a session is requested for a basket whose previous attempt failed
- **THEN** the system SHALL release any live gateway transaction, create a new one for the
  current amount, and return the new transaction identifier

#### Scenario: Switching integration mid-payment

- **WHEN** a session is requested naming a different card integration from the one previously
  used, and no authorisation has occurred
- **THEN** the system SHALL release any live gateway transaction and open the session for the
  newly named integration

#### Scenario: Authorisation already in flight

- **WHEN** a session is requested while an authorisation is being processed
- **THEN** the system SHALL respond `409` with `AUTHORIZATION_IN_PROGRESS` and SHALL NOT
  disturb the authorisation

#### Scenario: Payment already concluded

- **WHEN** a session is requested for a basket already authorised, captured, or cancelled
- **THEN** the system SHALL respond `409` with `INVALID_TRANSACTION_STATE`

#### Scenario: Funds already held

- **WHEN** a session is requested while an authorisation is outstanding awaiting capture or
  operator reconciliation
- **THEN** the system SHALL respond `409` with `INVALID_TRANSACTION_STATE`, so the guest's
  money is never held twice for one basket

#### Scenario: Session has expired

- **WHEN** a session is requested for an expired payment
- **THEN** the system SHALL respond `410` with `EXPIRED`, and the guest SHALL start a new
  payment

### Requirement: Session expiry

The system SHALL expire a payment that has not been authorised within a bounded window
measured from when the payment was first started, releasing any live gateway transaction. The
window SHALL cover a session that never initialises successfully, so a payment cannot wait
indefinitely.

#### Scenario: Nobody completes the payment

- **WHEN** the expiry window elapses with no authorisation
- **THEN** the system SHALL release any live gateway transaction and mark the payment expired

#### Scenario: Expiry coincides with an authorisation

- **WHEN** the expiry window elapses while an authorisation is being processed
- **THEN** the system SHALL wait for that authorisation to conclude rather than cancel a
  transaction underneath it

#### Scenario: Initialisation never succeeds

- **WHEN** repeated initialisation attempts all fail and the expiry window elapses
- **THEN** the system SHALL mark the payment expired rather than remain open

### Requirement: Web authorisation

For the web integration the system SHALL authorise the payment when the client reports that
card entry and any authentication challenge are complete. The client SHALL identify only the
basket; the system SHALL supply the transaction identifier from the session it already holds.

The system SHALL answer within a bounded time. Where the authorisation is still running the
system SHALL report it as pending rather than as a failure, and the guest's payment SHALL
continue.

#### Scenario: Authorisation succeeds

- **WHEN** a client asks to authorise a basket whose session is awaiting authorisation
- **THEN** the system SHALL authorise the amount with the gateway and respond `200` reporting
  success

#### Scenario: Authorisation outlasts the bounded wait

- **WHEN** the authorisation has been durably accepted but has not concluded within the
  bounded wait
- **THEN** the system SHALL respond `202` with `AUTHORIZATION_PENDING`
- **AND** the client SHALL resolve the outcome by reading the payment status
- **AND** the system SHALL continue the authorisation regardless of the client

#### Scenario: No session to authorise

- **WHEN** a client asks to authorise a basket with no session awaiting authorisation
- **THEN** the system SHALL respond `404` with `TRANSACTION_NOT_FOUND`

#### Scenario: Authentication failed at the bank

- **WHEN** the gateway reports that the authentication challenge did not succeed
- **THEN** the system SHALL fail the attempt and SHALL leave the payment open for the guest to
  retry within the expiry window

#### Scenario: No self-authorising backup exists for web

- **WHEN** a web client never asks to authorise
- **THEN** the system SHALL expire the payment at its deadline and SHALL NOT authorise on the
  guest's behalf

### Requirement: Native authorisation by gateway notification

For the native integration the system SHALL accept an unsolicited notification from the
gateway identifying the basket, and SHALL use it as a prompt to resolve the payment. The
system SHALL acknowledge a notification it cannot act on rather than reject it, because the
gateway does not retry.

#### Scenario: Notification prompts resolution

- **WHEN** the gateway notifies the system about a basket's payment
- **THEN** the system SHALL acknowledge receipt with `200`
- **AND** SHALL resolve the payment from the gateway's own record of the transaction

#### Scenario: Notification cannot be correlated

- **WHEN** a notification arrives without an identifiable basket, for an unknown payment, or
  naming a transaction that is not the one the session holds
- **THEN** the system SHALL acknowledge receipt with `200`, SHALL change no payment state, and
  SHALL record that the notification was discarded

#### Scenario: Notification is unauthenticated

- **WHEN** signature verification is required and a notification arrives unsigned, wrongly
  signed, or with no verification key available
- **THEN** the system SHALL respond `401` and SHALL change no payment state

#### Scenario: Notification is malformed

- **WHEN** a notification arrives with no body, an unreadable body, or without a transaction
  identifier and status
- **THEN** the system SHALL respond `400` and SHALL change no payment state

### Requirement: Gateway-confirmed status resolution

The system SHALL treat a gateway notification's claimed status as a hint only. Before
resolving a payment the system SHALL read the transaction's status from the gateway directly,
and SHALL act on that answer. This SHALL apply to every claimed status, favourable or
unfavourable, so an unverified claim can neither authorise a payment nor kill a live attempt.

#### Scenario: Claim confirmed as authorised

- **WHEN** the gateway's own record reports the transaction authorised or captured
- **THEN** the system SHALL authorise the payment using the card detail from that record

#### Scenario: Claim confirmed as failed or cancelled

- **WHEN** the gateway's own record reports the transaction failed or cancelled
- **THEN** the system SHALL fail or cancel the payment accordingly

#### Scenario: Claim not confirmed

- **WHEN** the gateway's own record still reports the transaction in flight
- **THEN** the system SHALL discard the notification and SHALL keep waiting, leaving the
  guest's attempt alive

#### Scenario: Transaction no longer exists

- **WHEN** the gateway reports no such transaction
- **THEN** the system SHALL fail the payment

### Requirement: Native authorisation backup

For the native integration the system SHALL additionally poll the gateway on a configured
cadence after a configured initial delay, and SHALL authorise, fail or cancel on what it
finds. Whichever of the notification and the poll resolves the payment first SHALL own the
outcome; the other SHALL have no effect. The system SHALL expire the payment if no conclusive
status is reached by the reconciliation deadline.

#### Scenario: Notification is lost

- **WHEN** no notification arrives but the gateway reports the transaction authorised
- **THEN** the system SHALL authorise the payment without any client involvement

#### Scenario: Both paths report the same outcome

- **WHEN** a notification and a poll both report an authorised transaction
- **THEN** the system SHALL authorise once, and SHALL notify the booking pipeline once

#### Scenario: Gateway is temporarily unreachable

- **WHEN** a poll cannot reach the gateway
- **THEN** the system SHALL leave the payment awaiting authorisation and SHALL try again on
  the next cadence

#### Scenario: Reconciliation deadline reached

- **WHEN** the reconciliation deadline passes with no conclusive status
- **THEN** the system SHALL expire the payment

### Requirement: Authorisation notified to the booking pipeline

The system SHALL announce a successful authorisation to the booking pipeline, carrying the
basket, the transaction, the tokenised card, the authorised amount and currency, and the
guest's language. The system SHALL treat the payment as authorised only once that
announcement has been accepted.

Where the announcement cannot be delivered within its bounded horizon, the system SHALL
release the authorisation and fail the attempt, so no money is held against a booking that
was never started.

#### Scenario: Authorisation announced

- **WHEN** the gateway has authorised the amount
- **THEN** the system SHALL announce the authorisation to the booking pipeline keyed by basket
- **AND** SHALL only then report the payment as authorised

#### Scenario: Announcement cannot be delivered

- **WHEN** the announcement fails for longer than its bounded horizon
- **THEN** the system SHALL release the authorisation with the gateway, SHALL fail the
  attempt, and SHALL leave the guest able to retry

#### Scenario: Announcement is never duplicated

- **WHEN** more than one path observes the same authorised transaction
- **THEN** the system SHALL announce the authorisation exactly once for that payment

### Requirement: Capture after booking confirmation

The system SHALL hold the authorisation without capturing it until the booking outcome is
known. On confirmation the system SHALL capture; on failure the system SHALL release the
authorisation. The system SHALL NOT capture money for a booking that was not confirmed.

#### Scenario: Booking confirmed

- **WHEN** the booking pipeline reports the booking complete
- **THEN** the system SHALL capture the authorised amount

#### Scenario: Booking failed

- **WHEN** the booking pipeline reports the booking failed
- **THEN** the system SHALL release the authorisation so the guest is not charged

#### Scenario: Booking outcome not reported

- **WHEN** no booking outcome is reported
- **THEN** the system SHALL ask the basket for its status on a configured cadence and SHALL
  resolve capture or release from that answer

#### Scenario: Capture survives a gateway outage

- **WHEN** capture fails against a confirmed booking
- **THEN** the system SHALL keep retrying over a long horizon rather than abandon the capture,
  because the guest already holds a confirmed booking

### Requirement: Payments parked for operator reconciliation

Where the system can neither safely capture nor safely release, it SHALL park the payment in a
state that names the reason, leave the authorisation intact, and surface the payment as
requiring attention. The system SHALL refuse a new payment attempt on a parked basket.

#### Scenario: Capture horizon exhausted

- **WHEN** capture has been retried to the end of its horizon against a confirmed booking
- **THEN** the system SHALL park the payment as requiring manual capture, SHALL leave the
  authorisation intact, and SHALL surface the payment as failed for operational attention

#### Scenario: Booking outcome never becomes knowable

- **WHEN** the booking outcome is still undecided at the end of its polling horizon
- **THEN** the system SHALL park the payment as requiring manual reconciliation
- **AND** SHALL neither capture, because the booking may never complete, nor release, because
  it still might

#### Scenario: Parked payment cannot be retried by the guest

- **WHEN** a session is requested for a parked basket
- **THEN** the system SHALL refuse it, because a second attempt would hold the guest's money
  twice

### Requirement: Card tokenisation

On successful authorisation the system SHALL record the reusable card token the gateway
returns, together with the card's last four digits and expiry, and SHALL pass them to the
booking pipeline for use in future payments. The token SHALL never be written to logs.

#### Scenario: Token captured on authorisation

- **WHEN** the gateway authorises a card payment and returns a card token
- **THEN** the system SHALL carry the token, last four digits and expiry on the authorisation
  announcement

#### Scenario: Gateway returns no card detail

- **WHEN** the gateway's record carries no card detail
- **THEN** the system SHALL announce the authorisation without card fields rather than fail
  the payment

### Requirement: Payment status is readable

The system SHALL let a client read a payment's current status and the outcome of its most
recent authorisation attempt. Reading status SHALL NOT alter the payment.

#### Scenario: Status read

- **WHEN** a client reads the status of a known basket's payment
- **THEN** the system SHALL respond `200` with the current status and the last authorisation
  outcome, where one exists

#### Scenario: Status read for an unknown basket

- **WHEN** a client reads the status of a basket with no payment
- **THEN** the system SHALL respond `404` with `BASKET_NOT_FOUND`

### Requirement: Card details never enter Whitbread systems

Card number and security code SHALL be collected by gateway-hosted fields on web and by the
gateway's native interface in the apps, and SHALL be transmitted directly to the gateway.
Whitbread frontends, gateway-facing services and logs SHALL never receive, store or record
them.

#### Scenario: Web card entry

- **WHEN** a guest types a card number and security code on the web payment page
- **THEN** those values SHALL be confined to gateway-hosted fields and SHALL NOT be readable
  by Whitbread code

#### Scenario: Native card entry

- **WHEN** a guest types card details in an app
- **THEN** those values SHALL be collected by the gateway's own interface and SHALL NOT be
  passed through app code

#### Scenario: Merchant-owned fields

- **WHEN** the guest supplies cardholder name, expiry, or a billing address
- **THEN** the system MAY collect those in Whitbread-owned fields and submit them alongside
  the gateway-hosted values

### Requirement: Merchant account resolution

The system SHALL bill each payment to the merchant account belonging to the reservation's
hotel. In production every hotel SHALL resolve to its own account and a payment SHALL NOT
proceed without an identifiable hotel. Outside production, hotels without a provisioned
account SHALL resolve to a shared default account so testing can proceed.

#### Scenario: Production hotel

- **WHEN** a payment is initialised in production for a hotel
- **THEN** the system SHALL bill the merchant account for that hotel

#### Scenario: Production payment with no identifiable hotel

- **WHEN** a payment is initialised in production and the reservation names no hotel
- **THEN** the system SHALL refuse to resolve a merchant account rather than bill an
  unintended one

#### Scenario: Non-production hotel without a provisioned account

- **WHEN** a payment is initialised outside production for a hotel with no provisioned account
- **THEN** the system SHALL bill the shared default account and SHALL record that it did so

### Requirement: Gateway faults are never reported as guest faults

Where the gateway rejects the system's own credentials or permissions, the system SHALL report
an upstream fault. It SHALL NOT surface the gateway's authentication failure to a client as an
authentication or authorisation failure of the client's own.

#### Scenario: Gateway rejects our credentials

- **WHEN** the gateway answers that the system's credentials or permissions are rejected
- **THEN** the system SHALL respond `502` with `GATEWAY_AUTHENTICATION_FAILED`
- **AND** SHALL NOT respond `401` or `403`
