---
inclusion: fileMatch
fileMatchPattern: "backend/identity/libs/bart-shared-lib/**"
---

# Product Overview

Legacy shared library providing generated Java types and SOAP service stubs for Whitbread's BART (Business Accommodation Reservation Technology) system. Code is generated from 25+ WSDL definitions using Apache CXF codegen. Dozer is used for object mapping between generated types and domain models.

## Core Responsibilities

- Generate Java classes from BART WSDL definitions via CXF codegen
- Provide SOAP service client stubs for BART interactions
- Map between BART-generated types and internal domain objects using Dozer
- Encapsulate all BART protocol details behind a reusable library interface

## Consumer Services / Integration Points

- Consumed by marketing-service-opera (newsletter subscriptions)
- Consumed by hotel-login-service-opera (guest authentication)
- Consumed by other identity services that interact with the BART reservation system
