---
inclusion: fileMatch
fileMatchPattern: "backend/identity/libs/commons-validators/**"
---

# Project Structure

Standard Maven library layout. Annotation classes and their corresponding `ConstraintValidator` implementations live under `src/main/java`. No Spring auto-configuration — validators are picked up by the Bean Validation runtime.
