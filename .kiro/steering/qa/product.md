---
inclusion: fileMatch
fileMatchPattern: "qa/**"
---

# Product

End-to-end test automation suite for the Premier Inn digital web platform.

## Scope

Browser-based E2E coverage of the customer booking journey against deployed environments
(`uat`, `dit`) of `premierinn.digital`: home/search, hotel details, ancillaries, guest details,
payment (including 3-D Secure) and booking confirmation.

It validates the deployed frontend applications; it does not test backend services in isolation.
