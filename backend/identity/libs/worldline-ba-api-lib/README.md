# Worldline Business Account API Library
This library contains the Java classes generated from Worldline Business Account SOAP API.

# Technologies
 - Java 25
 - Maven 3+
 - Spring Boot 4.0.3 (Spring Framework 7.x)
 - Apache CXF Plugin v4.0.5
 
# Release Notes

## v3.0.0
| Type | Ticket(s)  | Description              | Notes |
| --- |------------|--------------------------| --- |
| Feature | CTECH-7460 | Upgrade to Java 25 and Spring Boot 4.0.3 | **BREAKING CHANGE** - See upgrade notes below |

### Upgrade Notes for v3.0.0
**This is a BREAKING CHANGE requiring downstream service upgrades:**

#### Required Changes:
- **Java 25** - Consuming services MUST upgrade to Java 25 (bytecode version 69)
- **Spring Boot 4.0.3** - Parent upgraded from spring-cloud-lib-parent:2.0.0 to 4.0.0
- **Jakarta namespace** - Migrated from `javax.xml.bind` to `jakarta.xml.bind`

#### Dependency Updates:
- JAX-WS: 4.0.1 → 4.0.3
- jakarta.xml.bind-api: 4.0.4 (was javax.xml.bind:jaxb-api:2.3.1)
- jaxb-runtime: 4.0.6 (was com.sun.xml.bind:jaxb-impl:2.3.1)
- commons-exceptions: 2.0.0 → 3.0.1
- cxf-codegen-plugin: 4.0.3 → 4.0.5

#### API Compatibility:
- ✅ No changes to public API classes
- ✅ Binary compatible for services running Java 25 + Spring Boot 4.x
- ❌ NOT compatible with Java 17/21 or Spring Boot 2.x/3.x

## v2.1.0
| Type | Ticket(s)  | Description              | Notes |
| --- |------------|--------------------------| --- |
| Feature | DNRQ-87490 | Added new parameter for CustomerAccountCardBaseDetailsType | mex_1.xsd changed |

## v2.0.1
| Type | Ticket(s)  | Description                    | Notes |
| --- |------------|--------------------------------| --- |
| Feature | DNRQ-70029 | Update to the 2024 XSD release | mex.wsdl, mex_1.wsdl, mex.xsd, mex_1.xsd changed |

## v2.0.0 
| Type | Ticket(s) | Description                         | Notes |
| --- | --- |-------------------------------------| --- |
| Feature | DNRQ-64249 | Update worldline-api-lib to java 17 |

## v1.2.2 (31/05/2022)
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature | DNRQ-21840 | Add DE and GB configuration |

## v1.2.1
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature |DNRQ-12144 | Update common-exception lib version | No Config Change |

## v1.2.0
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature |DNRQ-10590 | MS- SPIKE- userDetails- Discovery of schema | mex.wsdl, mex_1.wsdl, mex.xsd, mex_1.xsd changed |
| Feature |DNRQ-12144 | update spring cloud lib version|

## v1.1.0
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature | DNRQ-8115 | Updated Worldline Api Version to v1.1 | mex.wsdl, mex_1.wsdl, mex.xsd, mex_1.xsd changed |

## v1.0.1
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature | DNRQ-8378 | Add -xjc-Xvalue-constructor arg to add constructors to auto generated classes | No config changes |
| Feature | DNRQ-8586 | Add spring-cloud-lib-parent and remove nexus server | No config changes |
| Feature | DNRQ-8115 | Updated Worldline Api Version to v1.1 | mex.wsdl, mex_1.wsdl, mex.xsd, mex_1.xsd changed |
| Feature | DNRQ-8140 | Added common classes that can be used by all worldline microservices | mex.wsdl, mex_1.wsdl, mex.xsd, mex_1.xsd changed |

## v1.0.0
| Type | Ticket(s) | Description | Notes |
| --- | --- | --- | --- |
| Feature | DMS-3940 | Java 11 | No config changes |
| Task | DDT-7532 | Existing Worldline API into a seperate new library  | Added mex.wsdl and plugin to generate java classes |

