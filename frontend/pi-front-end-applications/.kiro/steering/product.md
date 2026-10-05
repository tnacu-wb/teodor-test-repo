# Product

Whitbread Premier Inn Front End Applications — a monorepo housing the customer- and agent-facing web applications for the Premier Inn hotel booking platform.

## Applications

- **premier-inn** — The public-facing booking site. Covers the full guest journey: hotel search, hotel details, room selection, guest details, payment, confirmation, booking amends, account management, pre-check-in, and restaurants.
- **business-booker** — Booking experience tailored for business customers.
- **ccui** — Contact Centre User Interface, used internally by agents to manage bookings on behalf of customers.

## Shared Component Catalog

A set of internal `@whitbread-eos/*` packages provide reusable building blocks shared across all three apps, organized using Atomic Design (atoms, molecules, organisms) plus layout, utils, api, and page-level packages.

## Domain Context

Core domains revolve around hotel booking: search and availability, reservations, amendments, payment, loyalty/account, and restaurants. Data is served primarily through a GraphQL API at the `premierinn.digital` domain.
