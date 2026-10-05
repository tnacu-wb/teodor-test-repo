---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/piba-account-service-opera/**"
---

# Product Overview

PIBA Account Service Opera is a microservice within the Whitbread digital backend platform. It provides Premier Inn Business Account (PIBA) functionality, allowing business customers to manage their accounts, view balances, transactions, invoices, and perform account operations through the Worldline payment platform.

## Core Responsibilities

- Expose REST APIs for PIBA account management (v1 and v2)
- Retrieve and aggregate account balances from Worldline (GB and DE schemes)
- Fetch and display account transactions with download capability (CSV and XLS)
- Manage invoices with PDF download support
- Handle credit limit proposals
- Manage tethered user details and memorable word resets
- Register tethered users via CDH (Customer Data Hub)
- Cache responses using Redis cluster

## Key Integrations

- **Worldline** — SOAP (legacy) and REST APIs for account info, balances, transactions, invoices, and credit operations
- **PIBA GUID Service** — Feign client to resolve tethered user GUIDs from company/employee IDs
- **CDH (Customer Data Hub)** — Azure OAuth-secured API for tethered user registration
- **Auth0** — JWT-based authorization with tenant configuration
- **Redis** — Clustered cache for response caching

## Domain Context

PIBA (Premier Inn Business Account) is a B2B payment product allowing corporate customers to manage employee spending at Premier Inn hotels. The service supports multi-scheme operations (GB and DE) with Worldline as the underlying payment processor. "Tethered users" are employees linked to a corporate PIBA account.

## API Endpoints

### V1 (`/piba/account/`)
- `GET /balance` — View current account balance (all accounts)
- `GET /balance/summary` — View balance summary
- `POST /transactions` — View account transactions
- `GET /transactions/download/{schemeCustomerId}/{tetheredUserGuid}` — Download transactions as CSV
- `POST /invoices` — View customer invoices
- `GET /invoices/download/{schemeCustomerId}/{tetheredUserGuid}/{fileId}` — Download invoice as PDF
- `POST /proposecreditlimit` — Propose a new credit limit
- `GET /tethereduserdetail/{tetheredUserGuid}/{scheme}` — Get tethered user details
- `GET /tethereduserdetail/{tetheredUserGuid}` — Get tethered user details (legacy, no scheme)
- `POST /memorableword` — Reset memorable word
- `GET /customers` — Get all customer accounts
- `POST /register/tetheredUser` — Register a tethered user in CDH

### V2 (`/v2/piba/account/`)
- `POST /invoices` — View invoices (v2, with auth-based lookup)
- `GET /invoices/download/{schemeCustomerId}/{tetheredUserGuid}/{fileId}` — Download invoice PDF
- `GET /balance/summary/{tetheredUserGuid}/{scheme}` — Get balance summary for specific user
- `GET /transactions/download/{schemeCustomerId}/{tetheredUserGuid}/{invoiceNo}` — Download transactions as XLS for specific invoice
- `GET /transactions/download/{schemeCustomerId}/{tetheredUserGuid}` — Download all transactions as XLS
