---
inclusion: fileMatch
fileMatchPattern: "backend/arrive-stay-leave/services/hotel-wallet-service-opera/**"
---

# Product Overview

The Hotel Wallet Service Opera is a microservice within the Whitbread digital backend platform. It generates post-booking Apple Wallet passes (`.pkpass` files) for iOS devices, allowing guests to store their hotel reservation details in their phone's wallet.

## Core Responsibilities

- Generate Apple Wallet passes for hotel reservations
- Retrieve reservation details from downstream services (Reservation Service, Content Service, Basket Service)
- Render wallet pass content using Thymeleaf HTML templates (brand-specific)
- Sign wallet passes using Apple certificates (jPassKit library)
- Store and retrieve signing certificates from AWS S3
- Serve generated `.pkpass` files to clients
- Support multiple brands via brand-specific templates (PI, BB, HUB, PIGE, ZIP)

## Key API Domains

| Controller Domain | Purpose |
|-------------------|---------|
| Wallet | Generate and serve Apple Wallet passes for hotel reservations |

## Key Integrations

- **Reservation Service** — Retrieve reservation details for wallet pass content
- **Content Service** — Retrieve hotel/content information for display
- **Basket Service** — Retrieve basket/booking details
- **AWS S3** — Store and retrieve Apple signing certificates
- **Apple PassKit** — Sign and package `.pkpass` wallet files (jPassKit)

## Domain Context

This service is part of the "Arrive, Stay, Leave" domain. It sits within the post-booking journey, enabling guests who have completed a reservation to add their booking to their Apple Wallet for easy access on arrival. The service communicates with Opera/OHIP indirectly through the Reservation Service and Content Service.

## Brand Templates

The service supports multiple Whitbread brands, each with its own wallet pass template:
- **PI** (Premier Inn)
- **BB** (Beefeater)
- **HUB** (hub by Premier Inn)
- **PIGE** (Premier Inn Germany)
- **ZIP** (ZIP by Premier Inn)
