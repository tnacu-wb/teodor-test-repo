---
inclusion: fileMatch
fileMatchPattern: "tools/python/ddb-stream-basket-to-dt/**"
---

# Product Overview

`ddb-stream-basket-to-dt` turns the PI basket service's DynamoDB table stream into Dynatrace
business events. It reads inserted and updated basket items from the table's stream,
summarises each record down to a small set of monitoring fields, strips all PII, and POSTs
batches to the Dynatrace Business Events API.

It exists so that basket and payment behaviour can be analysed in Dynatrace — conversion
through the booking funnel, payment outcomes, and error rates by hotel and channel — without
the basket service itself having to emit analytics events.

## Core Responsibilities

- Discover and follow every open shard of the basket table's DynamoDB stream.
- Summarise each stream record to monitoring-relevant fields only.
- Classify each event by channel and basket status.
- POST batches to Dynatrace, retrying rather than dropping data when Dynatrace is unavailable.

## Event Contract

Every event posted carries:

| Field | Value |
|-------|-------|
| `event.provider` | Always `whit.pi.basket` |
| `event.type` | `<prefix>.<status>`, where `status` is the basket status and `prefix` is `DISTR` for the DISTR channel, otherwise the `subChannel`, otherwise the literal `null` |
| `build.timestamp` | Image build time, so a data change can be attributed to a code version |

Business fields carried through when present: `basketId`, `type`, `hotelId`,
`threeLetterHotelId`, `channel`, `subChannel`, `reference`, `createdAt`, `lastModifiedAt`,
`paymentID`, `paymentOption`, `paymentStatus`, `currency`, `totalCost`. Errors are flattened
out of the nested `basketError` map into `errorCode`, `errorDescription`, `errorType`.

A single `watcher-started` event is posted at startup as a liveness check; the process exits
if that POST fails, so a broken API key or environment fails fast rather than silently.

**No PII is ever sent.** The field list is an allow-list, not a redaction pass: anything not
named above never leaves the process. Guest names, addresses, emails, and card details are
therefore structurally excluded. Any change that widens the field set is a data-protection
decision, not just a code change — treat it as such, and keep the
`test_extract_relevant_fields_no_pii_in_output` test meaningful.

## Delivery Semantics

At-least-once. The shard iterator is only advanced after Dynatrace accepts the batch, so a
failed POST causes the same records to be retried rather than lost. Duplicates are possible;
consumers in Dynatrace must tolerate them.

The trade-off is at the other end: on restart, existing shards resume at `LATEST`, so records
written while the process was down are **not** back-filled. Deployments therefore lose a small
window of events — which is why this tool should not be redeployed casually in prod, and why
automated prod rollouts want a gate rather than a fully automatic image bump.

## Environments

| Cluster | Basket table | Dynatrace environment |
|---------|--------------|----------------------|
| `opera-dev` | `opera-newBasket-dev` (eu-west-1) | `whitbread-non-prod` |
| `opera-perf` | `opera-newBasket-perf` (eu-west-1) | `whitbread-non-prod` |
| `opera-hulk` | `opera-newBasket-hulk` (eu-central-1) | `whitbread-prod` |
| `opera-wanda` | `opera-newBasket-wanda` (eu-central-1) | `whitbread-prod` |

Prod runs at `LOG_LEVEL=WARNING` and with substantially larger resource requests than nonprod,
because prod shard counts and record volume are far higher.

## Domain Context

The upstream table is owned by `basket-service` (`backend/book-pay/services/basket-service`),
which manages the booking basket lifecycle from creation through payment to confirmation. This
tool is a read-only observer of that table's stream: it must never write to DynamoDB, and it
has no API of its own. If the basket item schema changes, the summariser here needs updating —
it reads raw DynamoDB attribute maps, not a shared model.
