---
inclusion: fileMatch
fileMatchPattern: "backend/identity/libs/bart-shared-lib/**"
---

# Project Structure

WSDL files live under `src/main/resources/wsdl/`. CXF codegen produces Java source into the build output during the `generate-sources` phase. Hand-written code includes Dozer mapping configurations and any service-layer wrappers around generated stubs.
