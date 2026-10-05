---
inclusion: fileMatch
fileMatchPattern: "backend/identity/libs/bart-shared-lib/**"
---

# Tech Stack

- Java 25, Spring Boot 4.0.7, Spring Cloud 2025.1.1
- Apache CXF (WSDL-to-Java codegen + SOAP runtime)
- Dozer (object mapping)
- 25+ WSDL definitions

## Build

```bash
./mvnw clean install -pl backend/identity/libs/bart-shared-lib -am
```

Note: The `generate-sources` phase runs CXF codegen from WSDLs. First build may be slow.
