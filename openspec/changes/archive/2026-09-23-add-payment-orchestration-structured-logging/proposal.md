## Why

`payment-orchestration-service` is the only book-pay service still emitting plain-text console
logs. Every peer (basket-service, promo-service, address-lookup-entity-service,
threec-payment-service-opera, …) consumes the shared `commons-logging` library, so its logs land
in the EFK/ELK stack as structured JSON with `traceId`/`spanId` and an `appName`. Payment's logs
do not, which breaks cross-service correlation and searchable diagnosis for the payment journey.
CTECH-13753 asks to close that gap.

## What Changes

- Add the `uk.co.whitbread.shared:commons-logging` dependency to the service `pom.xml`,
  excluding `com.fasterxml.jackson.core:jackson-databind` (the established peer convention, so the
  service's managed Jackson version wins).
- Set `spring.application.name: payment-orchestration-service` in `application.yml`. This is what
  the library's `FluentdLogger` reads (via `environment.getProperty("spring.application.name")`)
  to stamp `appName` on every JSON line. It currently resolves empty, which is why the existing
  `logging.pattern.level` line renders a blank service name.
- Widen the application's component scan to `uk.co.whitbread` via
  `@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})` on
  `PaymentOrchestrationServiceApplication`. This is REQUIRED, not optional: the library's
  `FluentdLogger` (the bean that installs the JSON console appender) is a plain `@Component` in
  `uk.co.whitbread.shared.commons.logging`, NOT registered via `spring.factories`. Payment's
  default scan covers only `uk.co.whitbread.payment.orchestrator`, so the bean is never
  constructed and logs stay plain no matter what the yml says. Every JSON-emitting peer
  (basket-service, promo-service, address-lookup-entity-service, …) uses exactly this
  `scanBasePackages = {"uk.co.whitbread"}`.
- Rename payment's own `GlobalExceptionHandler` to `PaymentGlobalExceptionHandler` (and update its
  references in `PaymentController`, `PaymentExceptionHandler`, and the controller tests). The wide
  scan pulls the shared `uk.co.whitbread.commons.exceptions.advice.GlobalExceptionHandler` (from
  `commons-entity-exceptions`) into range; it has the SAME simple name as payment's own advice, so
  both would register as bean `globalExceptionHandler` and Spring fails with
  `ConflictingBeanDefinitionException`. Peers do not hit this because they have no own advice of
  that name — they use the shared one. Payment keeps its own (payment-specific error contract), so
  the class is renamed to make the bean name unique. No behaviour changes — same mappings, same
  `@Order` pairing with `PaymentExceptionHandler`, same response body.
- Add a default `src/test/resources/application.yml` setting
  `logging.configuration.debug.enabled: false` so the wide scan does not sweep the `commons-logging`
  debug advices into the `@WebMvcTest` slice (they need a bean the slice's non-auto-configured
  context cannot supply). Since a default test `application.yml` shadows the main one for the
  isolated `ApplicationContextRunner` tests, it also mirrors the `integrations.datatrans.base-url`
  and `integrations.datatrans.reconciliation.*` defaults those tests read. Test-only; the production
  scan and config are unchanged. See design.md — Decisions.
- Enable the library's trace/debug plumbing in `application.yml` under `logging.configuration`:
  `logger: fluentd` (explicit, as the peers set it) and `debug.enabled: true` with
  `debug.exclusions: [.*/actuator.*]`. Without `debug.enabled: true` the `TraceLoggingConfiguration`
  auto-config is inert, so the `X-Trace-Id` `TraceFilter` and the request/response debug advices are
  not registered.
- Keep the existing `logging.pattern.level: "%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]"`
  line. It is not redundant: every adopted peer keeps this exact line, and it becomes correct once
  the application name resolves. Keep `logging.level.brave: WARN`.
- In the `local`-only profile document, add `logging.configuration.logger: default` so local
  development keeps a human-readable console instead of JSON (peer convention).
- Masking is deferred. `logging.configuration.mask` is left unset for now (JSON logging works
  without it); the field list will be decided in a follow-up. The `mask` block is omitted rather
  than added empty.

No API, endpoint, payment behaviour, workflow state, or event schema changes. The handler rename
is internal — the error responses it produces are byte-for-byte identical.

## References

- **Jira:** [CTECH-13753](https://whitbreadis.atlassian.net/browse/CTECH-13753) — "[BE] Enable
  Structured logging in Payment Orchestration" (child of CTECH-11148; description empty at time of
  writing).
- **Figma:** Not applicable.
- **Confluence:** Not applicable.
- **Design documents:** Not applicable.
- **Other documents:** `commons-logging` library README
  (`backend/book-pay/libs/commons-logging/README.md`).

## Capabilities

### New Capabilities

None. This change adds structured JSON logging (operability/tooling) and changes no observable
payment behaviour, so it introduces no capability spec.

### Modified Capabilities

None. No existing capability's requirements change.

This change sets `skip_specs: true` in `.openspec.yaml`: it is pure infrastructure/tooling wiring
with no spec-level behavioural change, which is exactly the opt-out the OpenSpec workflow sanctions
for refactor/tooling/docs work. No requirement is invented to satisfy validation.

## Impact

- **Squad / domain:** book-pay.
- **Surfaces:** backend only (`payment-orchestration-service`). No web, iOS, Android, or GraphQL
  impact.
- **Files:**
  - `backend/book-pay/services/payment-orchestration-service/pom.xml`
  - `backend/book-pay/services/payment-orchestration-service/src/main/resources/application.yml`
  - `.../src/main/java/uk/co/whitbread/payment/orchestrator/PaymentOrchestrationServiceApplication.java`
    (widen `scanBasePackages`)
  - `.../infrastructure/rest/controller/GlobalExceptionHandler.java` → renamed to
    `PaymentGlobalExceptionHandler.java`, plus reference updates in `PaymentController.java`,
    `PaymentExceptionHandler.java`, and the controller tests
    (`GlobalExceptionHandlerTest`, `PaymentControllerInitTest`, `PaymentControllerAuthorizeTest`,
    `WebhookControllerTest`, `PaymentAdviceOrderingWebTest`).
  - `.../src/test/resources/application.yml` (new) — default test config: `debug.enabled: false`
    plus the datatrans base-url / reconciliation defaults the isolated context tests read.
- **Dependencies:** adds a compile/runtime dependency on the existing in-repo `commons-logging`
  library (already on the reactor; version inherited via `${revision}`).
- **Runtime / operational:** console log format changes from plain text to FluentD/Logstash JSON
  in deployed profiles; local profile stays human-readable. Downstream EFK/ELK ingestion gains
  correct `appName` and trace correlation for this service. No behavioural or contractual impact on
  callers.
- **Living design:** no capability spec.md/design.md is created or modified (skip_specs).
