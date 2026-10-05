Before changing anything here, read the steering for this stack — it is the source of truth
for tooling, versions, and conventions:

- **Tier 2 (stack):** `.kiro/steering/stacks/backend.md`
- **Tier 3 (module):** `.kiro/steering/backend/<module>/{product,structure,tech}.md`

The Tier 3 folder is keyed by **module name only**, with the squad segment dropped — code at
`backend/identity/services/account-entity-service/` is documented at
`.kiro/steering/backend/account-entity-service/`. Load every module a change spans.

Build with the Maven wrapper (`./mvnw` from `backend/`), never a local `mvn`.
