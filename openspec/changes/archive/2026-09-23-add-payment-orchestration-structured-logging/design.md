## Context

See `proposal.md` — Why. `payment-orchestration-service` emits plain-text console logs while every
book-pay peer already emits FluentD/Logstash JSON via the shared `commons-logging` library. This
design records the few technical choices that make payment match the peers; there is no enduring
architectural change to the payment domain, so this document is deliberately short.

Relevant current state, established from the code:

- `commons-logging` (`backend/book-pay/libs/commons-logging`) is a Spring Boot auto-config jar. It
  registers `TraceLoggingConfiguration` and `LoggingConfigurationProperties` via
  `META-INF/spring.factories`, but its `FluentdLogger` — the bean that installs the JSON console
  appender — is a plain `@Component` in `uk.co.whitbread.shared.commons.logging`, NOT listed in
  `spring.factories`. So it is picked up ONLY if the consuming app's component scan reaches that
  package. `FluentdLogger` is gated by `@ConditionalOnProperty("logging.configuration.logger",
  havingValue="fluentd", matchIfMissing=true)` — but that condition is only ever evaluated once the
  bean is in scan range; the scan, not the default, is the real gate.
- `TraceLoggingConfiguration` (auto-configured via `spring.factories`, so always in range) is gated
  by `@ConditionalOnProperty("logging.configuration.debug.enabled", havingValue="true")` — the
  `X-Trace-Id` `TraceFilter` and request/response debug advices are OFF unless explicitly enabled.
- `FluentdLogger` reads the service name at bean construction via
  `environment.getProperty("spring.application.name")`.
- `payment-orchestration-service` sets `spring.application.name` nowhere; its existing
  `logging.pattern.level` line references `${spring.application.name:}`, which resolves empty today.
- Payment's `@SpringBootApplication` uses the DEFAULT component scan (its own package
  `uk.co.whitbread.payment.orchestrator` only). Every JSON-emitting peer instead declares
  `scanBasePackages = {"uk.co.whitbread"}`. This is the actual reason payment logs stayed plain
  even with a correct yml: `FluentdLogger` was never scanned, so the JSON appender was never
  installed.
- Payment defines its OWN `GlobalExceptionHandler` (`@RestControllerAdvice`, payment-specific error
  mappings) AND carries `commons-entity-exceptions` on the classpath, whose
  `uk.co.whitbread.commons.exceptions.advice.GlobalExceptionHandler` has the same simple name.
  Peers have no own advice of that name, so a wide scan gives them one bean; for payment it gives
  two beans named `globalExceptionHandler` → `ConflictingBeanDefinitionException`.

## Goals / Non-Goals

**Goals:**
- Emit structured JSON logs to the console in deployed profiles, correlatable in EFK/ELK with a
  correct `appName` and `traceId`/`spanId`, matching the book-pay peer convention.
- Keep local development output human-readable.

**Non-Goals:**
- Field masking of log content — deferred; `logging.configuration.mask` is left unset (field list
  TBD in a follow-up).
- Any change to payment behaviour, endpoints, workflow states, event schemas, or the error-response
  contract (the `GlobalExceptionHandler` rename is behaviour-preserving).
- Any change to the shared `commons-logging` library.

## Decisions

- **Consume the shared `commons-logging` library rather than hand-rolling a logback encoder.**
  Every peer does this; it bundles the logstash JSON encoder, trace filter, and masking hooks.
  Alternative (a service-local `logback-spring.xml`) rejected: it would diverge from the peers and
  duplicate maintained code.
- **Exclude `com.fasterxml.jackson.core:jackson-databind` from the dependency.** Established peer
  convention (e.g. `address-lookup-entity-service`) so the service's managed Jackson version wins
  and no second version is pulled onto the classpath.
- **Set `spring.application.name` in `application.yml`.** The name is read during context refresh
  when `FluentdLogger` is constructed, by which point `application.yml` is loaded into the
  `Environment`, so no `bootstrap.yml` is needed. (Setting it is necessary but NOT sufficient on its
  own — the logger bean must also be in scan range; see the next decision.)
- **Widen the component scan to `uk.co.whitbread` (`scanBasePackages = {"uk.co.whitbread"}`).**
  `FluentdLogger` is a `@Component` not registered in `spring.factories`, so payment's default
  package-only scan never constructs it and JSON never engages — the root cause of the logs staying
  plain. This is exactly what every JSON-emitting peer does. Alternatives rejected: a narrower scan
  listing only the logging package is bespoke (no peer does it) and still mis-scoped; changing the
  library to auto-register `FluentdLogger` was explicitly ruled out (no library changes).
- **Rename payment's `GlobalExceptionHandler` to `PaymentGlobalExceptionHandler`.** The wide scan
  brings the shared `commons.exceptions.advice.GlobalExceptionHandler` into range; same simple name
  → two beans named `globalExceptionHandler` → `ConflictingBeanDefinitionException`. Renaming
  payment's class makes the bean name unique while keeping its payment-specific mappings, its
  `@Order(LOWEST_PRECEDENCE)` pairing with `PaymentExceptionHandler`, and its response body exactly
  as they are. Alternatives considered: (a) explicit bean name `@RestControllerAdvice("...")` — a
  smaller diff but leaves two identically-named classes, which is more confusing to read; (b)
  deleting payment's handler to use the shared one — rejected, the shared handler knows none of
  payment's domain exceptions or error codes and would break the documented API error contract.
- **Enable JSON everywhere except `local`.** Set `logging.configuration.logger: fluentd` explicitly
  in the base document (as the peers do, rather than relying on `matchIfMissing`), plus
  `debug.enabled: true`. The `local` profile document sets `logger: default` to fall back to a
  human-readable console. Alternative (JSON locally too) rejected: hurts local debuggability.
- **Keep the existing `logging.pattern.level` line.** It is the peer-standard line and becomes
  correct once the app name resolves; removing it would make payment diverge from the peers.
- **Disable the `commons-logging` debug advices in tests via a default `src/test/resources/application.yml`
  (discovered during apply).** The wide scan also sweeps `uk.co.whitbread.shared.commons.logging.debug`
  `@ControllerAdvice` beans (`DebugRequestBodyAdvice` / `DebugResponseBodyAdvice`) into a
  `@WebMvcTest` slice. They are gated by
  `@ConditionalOnProperty("logging.configuration.debug.enabled" == "true")` and require
  `LoggingConfigurationProperties`, which is provided by auto-configuration a web slice does not run,
  so the slice context failed to load. A default test `application.yml` sets
  `logging.configuration.debug.enabled: false`, so those advices are never candidate beans in any
  test and the slice loads. Because a default test `application.yml` shadows
  `src/main/resources/application.yml` for the isolated `ApplicationContextRunner` tests
  (`IntegrationPropertiesValidationTest`, `DatatransReconciliationSpringContextTest`) that read the
  checked-in defaults, it also mirrors the `integrations.datatrans.base-url` and
  `integrations.datatrans.reconciliation.*` config those tests assert on. Alternatives rejected:
  (a) `excludeFilters` / `@TestPropertySource` on the one slice — scoped but does not centralize, and
  the config approach applies module-wide; (b) narrowing the production scan — the production context
  needs the full scan for `FluentdLogger`, so the fix belongs in test config, not the app.

## Risks / Trade-offs

- [Sensitive data in logs while masking is deferred] → Masking is off, so any future log statement
  that logs a card alias, PAN, or secret is emitted in clear. Mitigation: this change adds no new
  log statements; the follow-up to populate `logging.configuration.mask.fields` is tracked
  separately and should land before any PCI-adjacent value is logged. Existing PCI discipline
  (`cardAlias` must not be logged) is unchanged by this change.
- [Forgetting `debug.enabled: true` yields JSON but no trace filter] → Without it,
  `TraceLoggingConfiguration` is inert and `X-Trace-Id`/debug advices are not registered. Mitigation:
  it is an explicit task line and is verified by build/run check.
- [Log-format change is operationally visible] → Deployed console output flips from text to JSON.
  Mitigation: this is the intended outcome and matches every peer already in the EFK/ELK stack; no
  consumer parses payment's plain-text console today.
- [Widening the scan to `uk.co.whitbread` can pull in more shared `@Component`/`@Configuration`
  beans than just `FluentdLogger`] → Two instances surfaced: the `GlobalExceptionHandler` bean-name
  collision (resolved by the rename) and the `commons-logging` debug `@ControllerAdvice` beans being
  swept into the `@WebMvcTest` slice (resolved by `debug.enabled: false` in the default test
  `application.yml`). Other shared libs on the classpath could in principle contribute further beans.
  Mitigation: the full service test suite (521 tests, incl. an advice-ordering `@WebMvcTest`) and a
  clean context start are the gate — any further collision fails the build loudly rather than
  silently, and the scan matches the proven peer configuration.

## Living-design reconciliation

No enduring architectural change and no capability spec (this change sets `skip_specs: true`).
Archive has nothing to reconcile into a living `design.md`; there is no capability `spec.md`/`design.md`
to create or update.
