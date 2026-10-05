<h1>Azure Email Service SDK</h1>

Currently, in scenarios that imply the sending of an email to the customers, like updating a profile or resetting a password,
BART is the one sending the associated email to the impacted customers. Since BART is planned to be decommissioned, we need to move
the functionality of building and sending those emails at the Microservices level.

Project Scope: Have an Azure email service SDK which exposes a Java API for triggering the required emails for different scenarios.
This shared library will be used by microservices which need to integrate with the email service in order to send out case specific emails.

## Requirements

- **Java**: 21 or higher (Java 25 recommended)
- **Spring Boot**: 4.0.x

## Version History

## v3.0.0
| Type    | Ticket(s)  | Description                                                          | Notes                                                                                   |
|---------|------------|----------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| Feature | CTECH-7457 | Upgraded to Spring Boot 4.0.3 and Java 25                           | Major version bump due to minimum Java 21 requirement. No API changes for consumers.  |

**Breaking Changes:**
- Minimum Java version: 21 (Java 25 recommended)
- Requires Spring Boot 4.0.x in consuming applications

**Dependency Updates:**
- Spring Boot: 2.7.0 → 4.0.3
- Java: 11 → 25
- MapStruct: 1.5.2.Final → 1.6.3
- Commons Lang3: 3.9 → 3.19.0 (managed by Spring Boot)
- Jakarta XML WS API: 4.0.1 → 4.0.2 (managed by Spring Boot)
- Jakarta XML Bind API: 4.0.1 → 4.0.4 (managed by Spring Boot)
- JAX-WS Runtime: 4.0.1 → 4.0.3
- JAXB Runtime: 4.0.5 → 4.0.6 (managed by Spring Boot)
- SLF4J: 2.0.12 → 2.0.17 (managed by Spring Boot)
- Maven Compiler Plugin: 3.8.1 → 3.13.0

**Migration Notes:**
- No code changes required in consuming applications
- All configuration properties remain unchanged
- All public APIs remain backward compatible
- Update Spring Boot to 4.0.x and Java to 21+ before upgrading this dependency

## v2.13.0
| Type    | Ticket(s)  | Description                                                                | Notes |
|---------|------------|----------------------------------------------------------------------------|-------|
| Feature | DNRQ-69156 | Replaced the cxf plugin with jaxws-maven-plugin (jakarta instead of javax) |       |
## v2.12.0
| Type    | Ticket(s)  | Description                                                | Notes |
|---------|------------|------------------------------------------------------------|-------|
| Feature | DNRQ-67696 | Move email contracts from bart-shared-lib, in this service |       |
## v2.11.0
| Type    | Ticket(s)  | Description                                       | Notes |
|---------|------------|---------------------------------------------------|-------|
| Feature | DNRQ-64691 | Receiving emails in german for users at register. |       |

## v2.10.0
| Type    | Ticket(s)  | Description                                            | Notes |
|---------|------------|--------------------------------------------------------|-------|
| Feature | DNRQ-26295 | Implemented accept request to join email for BB users. |       |

## v2.9.0
| Type    | Ticket(s)  | Description                                          | Notes |
|---------|------------|------------------------------------------------------|-------|
| Feature | DNRQ-26054 | [BE] - Email - BB Password reset - triggered by user |       |

## v2.8.0
| Type    | Ticket(s)  | Description                                                 | Notes |
|---------|------------|-------------------------------------------------------------|-------|
| Feature | DNRQ-26299 | Implemented TM rejects requests to join email for BB users  |       |

## v2.7.0
| Type    | Ticket(s)  | Description                                        | Notes |
|---------|------------|----------------------------------------------------|-------|
| Feature | DNRQ-26053 | Implemented out of policy setup email for BB users |       |

## v2.6.0
| Type    | Ticket(s)  | Description                                           | Notes |
|---------|------------|-------------------------------------------------------|-------|
| Feature | DNRQ-26303 | Implemented Email employee access level changed on BB |       |

## v2.5.0
| Type    | Ticket(s)  | Description                                           | Notes |
|---------|------------|-------------------------------------------------------|-------|
| Feature | DNRQ-26121 | [BE] - Email - Request to join BB - triggered by user |       |

## v2.4.0
| Type    | Ticket(s)  | Description                                    | Notes |
|---------|------------|------------------------------------------------|-------|
| Feature | DNRQ-25077 | Implemented employee invite email for BB users |       |

## v2.3.0
| Type    | Ticket(s)  | Description                                       | Notes |
|---------|------------|---------------------------------------------------|-------|
| Feature | DNRQ-26052 | [BE] - Email - Employee added by the TM           |       |

## v2.2.0
| Type    | Ticket(s)  | Description                                       | Notes |
|---------|------------|---------------------------------------------------|-------|
| Feature | DNRQ-25068 | Implemented company activation email for BB users |       |

## v2.1.0
| Type    | Ticket(s)  | Description                                   | Notes                                              |
|---------|------------|-----------------------------------------------|----------------------------------------------------|
| Feature | DNRQ-22922 | [BE] - Email - PI register flow | added support for leisure users registration email |

## v2.0.0
| Type    | Ticket(s)  | Description                                   | Notes                                                                                                                                                      |
|---------|------------|-----------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Feature | DNRQ-24904 | Implemented reset password email for BB users | azure.generic-email-service properties must be added in order to use this implementation. <br/>azure.email-service was renamed to azure.pti-email-service. |

## v1.0.0
| Type    | Ticket(s)  | Description                            | Notes |
|---------|------------|----------------------------------------|-------|
| Feature | DNRQ-23299 | Azure email service SDK implementation |

## Supported APIs

Currently, the following APIs are supported:

* **Send PI reset password email**
* **Send BB reset password email**
* **Send update account email**

## How to configure

The following properties are to be added in application.yml/bootstrap.yml file:

    azure:
        pti-email-service:
            host: https://is-api-uat.whitbread.co.uk/internal/PTIEmailService/v1
            username: ${AZURE_EMAIL_SERVICE_USERNAME}
            password: ${AZURE_EMAIL_SERVICE_PASSWORD}
            subscription-key-header-name: Ocp-Apim-Subscription-Key
            subscription-key: ${AZURE_EMAIL_SERVICE_SUBSCRIPTION_KEY}
        generic-email-service:
            host: https://is-api-uat.whitbread.co.uk/internal/EmailService/v1
            username: ${AZURE_GENERIC_EMAIL_SERVICE_USERNAME}
            password: ${AZURE_GENERIC_EMAIL_SERVICE_PASSWORD}
            subscription-key-header-name: Ocp-Apim-Subscription-Key
            subscription-key: ${AZURE_GENERIC_EMAIL_SERVICE_SUBSCRIPTION_KEY}