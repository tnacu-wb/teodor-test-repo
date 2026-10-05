# commons-auth0
Commons components related to auth0 and Jwt tokens to be used across micro-services.

## Consumers of this library by component:
| Service                        | TokenService | ManagementService | EncryptionService | PasswordlessService |
|--------------------------------|--------------|-------------------|-------------------|---------------------|
| business-tether-microservice   | X            |                   |                   |                     |
| company-employee-microservice  |              | X                 |                   |                     |
| hotel-account-microservice     | X            | X                 |                   |                     |
| hotel-login-microservice       |              | X                 | X                 |                     |
| hotel-register-microservice    |              | X                 | X                 |                     |
| hotel-reservation-microservice | X            |                   |                   |                     |
| marketing-microservice         | X            |                   |                   |                     |
| otp-service                    |              |                   |                   | X                   |
| piba-account-service           | X            |                   |                   |                     |
| piba-guid-service              | X            |                   |                   |                     |
| piba-registration-service      | X            |                   |                   |                     |

## v6.0.0
| Type    | Ticket(s)   | Description                                                                                    | Notes                                                       |
|---------|-------------|------------------------------------------------------------------------------------------------|-------------------------------------------------------------|
| Feature | CTECH-7455  | Upgrade to Spring Boot 4.0.3 and Java 25                                                       | Incremented version to 6.0.0 because it's a breaking change |
| Feature | CTECH-7455  | Upgrade Auth0 SDK to 2.12.0, java-jwt to 4.4.0, jwks-rsa to 0.22.1                             | Breaking API changes from Auth0 SDK v2                      |
| Feature | CTECH-7455  | Replace javax.xml.bind:jaxb-api with jakarta.xml.bind:jakarta.xml.bind-api 4.0.2               |                                                             |
| Feature | CTECH-7455  | Upgrade jjwt to 0.12.6 (split into jjwt-api, jjwt-impl, jjwt-jackson), commons-codec to 1.17.1 |                                                             |

## v5.2.1
| Type    | Ticket(s)  | Description                                                                     | Notes |
|---------|------------|---------------------------------------------------------------------------------|-------|
| Feature | DNRQ-87628 | Fix add accessLevel to the response of retrieveCdhEmployeeDetailsAndVerifyToken |       |

## v5.2.0
| Type    | Ticket(s)  | Description                                                                  | Notes |
|---------|------------|------------------------------------------------------------------------------|-------|
| Feature | DNRQ-87628 | Add accessLevel to the response of retrieveCdhEmployeeDetailsAndVerifyToken  |       |

## v5.1.0
| Type    | Ticket(s)  | Description                              | Notes |
|---------|------------|------------------------------------------|-------|
| Feature | DNRQ-57007 | Return CCUI specific claims              |       |
| Feature | DNRQ-54795 | Added new method for verifying JWT token |       |

## v5.0.0
| Type         | Ticket(s)  | Description              | Notes                                                       |
|--------------|------------|--------------------------|-------------------------------------------------------------|
| Housekeeping | DNRQ-52579 | Upgrade to Spring Boot 3 | Incremented version to 5.0.0 because it's a breaking change |

## v4.0.0
| Type         | Ticket(s)  | Description                                                                                                         | Notes                                                       |
|--------------|------------|---------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------|
| Housekeeping | DNRQ-43169 | Renamed @EnableAuth, @EnableAuthFilter and AuthConfiguration so that it will not conflict with common-auth0 library | Incremented version to 4.0.0 because it's a breaking change |

## v3.0.0
| Type    | Ticket(s)  | Description                                          | Notes                                                                                            |
|---------|------------|------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| Feature | DNRQ-27055 | Added logic to extract the user email from JWT Token | Incremented version to 3.0.0, because there are breaking changes to the CdhEmployeeDetails class |

## v2.2.0
| Type    | Ticket(s)  | Description                                                                | Notes |
|---------|------------|----------------------------------------------------------------------------|-------|
| Feature | DNRQ-27055 | Add logic to extract companyAccountId and employeeAccountId from JWT Token |       |

## v2.1.0
| Type    | Ticket(s)  | Description                                           | Notes |
|---------|------------|-------------------------------------------------------|-------|
| Feature | DNRQ-26455 | Add logic to extract customerAccountId from JWT Token |       |

## v2.0.0
| Type | Ticket(s)  | Description         | Notes                                                                                |
| --- |------------|---------------------|--------------------------------------------------------------------------------------|
| Feature | DNRQ-15711 | Add support for OTP | Breaking changes. Services and configuration properties have been renamed and moved. |

## v1.6.0
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature | DNRQ-16470 | Make Auth0 bean conditional | No config changes |

## v1.5.0
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature | DNRQ-15139 | Added encryption algorithm for ghn and session id | No config changes |

## v1.4.0
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature | DNRQ-13475 | Update commons-auth0 library to enhance Auth0 API /api/v2/jobs/{JOB_ID}/errors unsupported currently by com.auth0| No config changes |

## v1.3.0
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature | DNRQ-13012 | Add logic to extract companyId and employeeId from JWT Token | No config changes |


## v1.2.0
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature | DNRQ-13092 | Upgrade com.auth0.* dependencies of Whitbread commons-auth0 library | No config changes |

## v1.1.1
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature | DNRQ-8378 | Added HandlerExceptionResolver to AuthenticationFilter, so that ControllerAdvice can catch errors thrown by this class | Config changes |
| Feature | DNRQ-8115 | Added request filter to verify token per request automatically | Config changes |
| Feature | DNRQ-7326 | Add spring-cloud-lib-parent pom. Remove nexus | No config changes |

### How to configure
To Enable the Auth0 common components please use the **@EnableAuthFilter** annotation.

The following property must be added for any URL that requires JWT Authorization, under 'auth.token' in application.yml

      urlPathRegex:  
        - .*\/piba\/.*

## v1.1.0
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature | DNRQ-2254 | Add method to retrieve email | No config changes |
| Feature | DNRQ-12776 | Add new version for spring-cloud-lib-parent and commons-exceptions | No config changes |

## v1.0.1
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature | DMS-3940 | Java 11 | No config changes |

## v1.0.0
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature | DMS-3760, DMS-3614 | Auth0 common services | No config changes |  


## How to use

To Enable the Auth0 common components please use the **@EnableAuth** annotation.

This will enable up to four components, depending on which configurations have been provided:
* **TokenService**: Can retrieve and verify a token.
* **ManagementService**: Can check if a user already exists in Auth0 and also gives access to the Auth0 Management API.
* **EncryptionService**: Can encrypt and decrypt guest history numbers and sessionIds.
* **PasswordlessService**: Can trigger sending a one-time password (OTP) to a given email address and exchange it for a JWT.

## How to configure

The properties need to be added under 'auth:' in the application.yml file.

For **TokenService**, please set the following properties:
* **providers**: The list of providers to read the JWT tokens.
* **urlPathRegex**

        auth:
          token:
            providers:
              - domain:
                host:
                # The slash here seems to be required to match auth generated token
                issuer:
            urlPathRegex:


For **ManagementService**, please set the following properties:
* **domain**
* **clientId**
* **clientSecret**
* **audience**
* **connection**

        auth:
          management:
            domain:
            clientId:
            clientSecret:
            audience:
            connection:

For **EncryptionService**, please set the following properties:
* **key**

        auth:
          encryption:
            key:

For **PasswordlessService**, please set the following properties:
* **domain**
* **clientId**
* **clientSecret**

        auth:
          passwordless:
            domain:
            clientId:
            clientSecret: