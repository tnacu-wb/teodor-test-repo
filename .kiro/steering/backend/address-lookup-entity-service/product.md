---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/address-lookup-entity-service/**"
---

# Product Overview

REST microservice providing UK address lookup functionality via QAS (Quick Address Search). Accepts partial address or postcode input and returns matched addresses. Delegates SOAP communication to the qas-address-lookup-lib shared library.

## Core Responsibilities

- Expose REST endpoints for address search and address detail retrieval
- Delegate to qas-address-lookup-lib for SOAP calls to the QAS provider
- Transform QAS responses into standardised address DTOs
- Handle error scenarios (invalid postcodes, QAS service unavailability)

## Consumer Services / Integration Points

- Called by frontend/mobile clients during checkout and registration flows
- Depends on qas-address-lookup-lib (SOAP client for QAS)
- Port: 9111
