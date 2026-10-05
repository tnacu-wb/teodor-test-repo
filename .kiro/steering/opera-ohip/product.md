---
inclusion: manual
---

# Opera & OHIP — Product Context

## What is Opera?

Oracle Hospitality OPERA Cloud is the Property Management System (PMS) used by Whitbread (Premier Inn) to manage hotel operations including reservations, guest profiles, room inventory, rates, and financial transactions.

## What is OHIP?

Oracle Hospitality Integration Platform (OHIP) is the REST API layer that sits in front of Opera Cloud, enabling external systems (like Whitbread's digital platform) to interact with Opera programmatically.

## Whitbread's Integration with Opera

Whitbread's digital booking platform integrates with Opera via OHIP to provide:

- **Hotel search & availability** — room types, rates, inventory
- **Booking creation** — on-hold reservations, confirmation, payment
- **Reservation management** — amendments (dates, rooms, occupancy, ancillaries)
- **Cancellation & refunds** — deposit folio refunds, reservation cancellation
- **Guest profiles** — creation and retrieval of guest/booker profiles
- **Packages & ancillaries** — breakfast, meals, donations, upgrades

## Key Business Flows

### Booking Journey
1. Hotel Detail Page (HDP) — availability check, room selection
2. Ancillaries — packages, meals, upgrades
3. Guest Details — profile creation, personal info
4. Payment — payment method selection, transaction
5. Confirmation — deposit folios, reservation confirmation
6. Abandon — cleanup of on-hold reservations

### Cancel Journey
- Retrieve reservation → process refund → cancel reservation

### Amend Journey
- Copy booking → amend (dates/rooms/occupancy/ancillaries) → confirm amend
- Most complex flow with multiple reservation copies, deposit folios, and routing instructions

## Opera Versioning

- Whitbread targets the **26.2 property API family**
- Current upgrade path: **26.2.2.0** (patch set)
- Next available minor release: **26.3.0.0**
- API specs baseline: `property_26.2.0.0/rest-api-specs` branch on GitHub

## Key Stakeholders

- **Digital Engineering** — builds and maintains the integration
- **Hotel Operations** — uses Opera Cloud UI for day-to-day hotel management
- **CRM/Loyalty** — profile and membership management
- **Finance** — deposit folios, refunds, commission processing
