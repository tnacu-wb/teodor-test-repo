---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/feedback-service-opera/**"
---

# Product Overview

Handles guest feedback submissions by forwarding them to Microsoft Dynamics CRM. Authenticates with Azure AD using ADAL4J to obtain tokens for CRM API access. Simple REST service with minimal domain logic.

## Core Responsibilities

- Accept guest feedback via REST endpoints
- Authenticate with Azure AD (ADAL4J) to get CRM access tokens
- Submit feedback records to Microsoft Dynamics CRM
- Handle error/retry scenarios for CRM submission

## Consumer Services / Integration Points

- Called by frontend/mobile clients after a guest stay
- Integrates with Microsoft Dynamics CRM (via Azure AD auth)
- Port: 9033
