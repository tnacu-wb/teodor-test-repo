---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/rules-agent-entity-service/**"
---

# Product Overview

Rules Agent Entity Service reads business rules from a PostgreSQL database into memory and serves them to other components via REST endpoints. It periodically polls the database for updates (configurable, default 60s) so that rule changes propagated by the Rules Manager Entity Service are picked up without restart.

## Core Responsibilities

- Load all rule configurations from DB into an in-memory cache on startup
- Periodically refresh the in-memory cache from PostgreSQL
- Expose REST endpoints for rule evaluation (amendment, base rate, channel, max limitations, occupancy supplement, paypal, rate suppression, RBAC, room substitution, VAT, business allowance)
- Validate incoming requests and return rule evaluation results

## Consumer Services / Integration Points

- Called by availability-cache-service-opera (rules evaluation during availability checks)
- Called by booking flow services (amendment rules, rate suppression)
- Companion to rules-manager-entity-service (which provides CRUD; this service provides read-only evaluation)
- Port: 9108

## Important Notes

- The service runs on **Spring Boot 4 / Java 25** and inherits from the standard
  `digital-monorepo-service-parent`, like every other service in the monorepo.
- Shared library dependencies resolve from the reactor at `${revision}` via the parent — do not
  pin them to Nexus versions.
- The service was imported via `git subtree` from `whitbread-eos/rules-agent-entity-service`.
  If further upstream work needs pulling in, use `git subtree pull` against that repo at prefix
  `backend/discover-search/services/rules-agent-entity-service` and resolve `pom.xml` in favour
  of the monorepo shape.
