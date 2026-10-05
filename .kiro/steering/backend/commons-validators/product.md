---
inclusion: fileMatch
fileMatchPattern: "backend/identity/libs/commons-validators/**"
---

# Product Overview

Shared library providing custom Jakarta Bean Validation annotations and their constraint validators. Used across identity services to enforce password policy, postcode format, date format, and conditional null checks.

## Core Responsibilities

- Provide `@Password` annotation backed by Passay password-policy rules
- Provide `@ValidPostcode` annotation for UK postcode validation
- Provide `@DateFormat` annotation for date string format validation
- Provide `@NotNullIfAnotherFieldHasValue` for cross-field conditional validation
- Expose reusable validators consumable by any Spring Boot service

## Consumer Services / Integration Points

- Consumed as a Maven dependency by identity squad services (hotel-register-service-opera, hotel-login-service-opera, and others)
- Integrates with Jakarta Validation API (hibernate-validator runtime)
