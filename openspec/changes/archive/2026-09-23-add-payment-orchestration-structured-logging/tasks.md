## 1. Add the logging dependency

- [x] 1.1 In `backend/book-pay/services/payment-orchestration-service/pom.xml`, add the
  `uk.co.whitbread.shared:commons-logging` dependency (no explicit `<version>`; inherited via
  `${revision}`), with an `<exclusion>` for `com.fasterxml.jackson.core:jackson-databind` — matching
  the peer block in `backend/book-pay/services/address-lookup-entity-service/pom.xml`. Verify with
  `cd backend && ./mvnw -q dependency:tree -pl book-pay/services/payment-orchestration-service -am | grep commons-logging` showing the dependency present and no second `jackson-databind` version pulled in transitively via it.

## 2. Wire the service name and logging config (application.yml)

- [x] 2.1 In `.../payment-orchestration-service/src/main/resources/application.yml`, add
  `spring.application.name: payment-orchestration-service` (under the existing top-level `spring:`
  block). Verify the value is present and the file still parses
  (`cd backend && ./mvnw -q -pl book-pay/services/payment-orchestration-service process-resources`).
- [x] 2.2 In the same file's main (default) profile, add under `logging.configuration`:
  `logger: fluentd` (explicit trigger for the JSON console, as the peers set it) plus
  `debug.enabled: true` and `debug.exclusions: [.*/actuator.*]`. Keep the existing
  `logging.pattern.level` line and `logging.level.brave: WARN` unchanged. Verify the `logging` block
  is well-formed YAML and no `mask` block is present (masking deferred).
- [x] 2.3 In the `local`-only profile document of the same file, add
  `logging.configuration.logger: default`. Verify it sits inside the `spring.config.activate.on-profile: local` document, not the shared `opera-*`/`local` group document.

## 3. Make the logging bean reachable (component scan + handler rename)

> Discovered during apply: setting the yml is necessary but NOT sufficient. `FluentdLogger` is a
> `@Component` (not in `spring.factories`), so it is only constructed if the component scan reaches
> `uk.co.whitbread.shared.commons.logging`. Widening the scan collides payment's own
> `GlobalExceptionHandler` with the shared one of the same simple name, so the handler is renamed.
> See design.md — Decisions.

- [x] 3.1 In `.../PaymentOrchestrationServiceApplication.java`, change `@SpringBootApplication` to
  `@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})`, matching the peer convention.
  (Revert any interim narrower-scan experiment to this exact form.)
- [x] 3.2 Rename `.../infrastructure/rest/controller/GlobalExceptionHandler.java` to
  `PaymentGlobalExceptionHandler.java` (class + file), keeping its `@RestControllerAdvice`,
  `@Order(LOWEST_PRECEDENCE)`, all `@ExceptionHandler` mappings, and the nested `GlobalErrorResponse`
  / `ErrorDetail` records unchanged. Update every reference: `PaymentController.java`,
  `PaymentExceptionHandler.java`, and tests `GlobalExceptionHandlerTest`, `PaymentControllerInitTest`,
  `PaymentControllerAuthorizeTest`, `WebhookControllerTest`, `PaymentAdviceOrderingWebTest`. Rename
  `GlobalExceptionHandlerTest` → `PaymentGlobalExceptionHandlerTest` to match.
- [x] 3.3 Verify context starts with no `ConflictingBeanDefinitionException`: a clean
  `./mvnw clean install -pl book-pay/services/payment-orchestration-service -am` completes and the
  521-test suite passes (advice-ordering `@WebMvcTest` included). A default
  `src/test/resources/application.yml` sets `logging.configuration.debug.enabled: false` so the
  `commons-logging` debug advices are never candidate beans in tests (avoiding the auto-config-only
  `LoggingConfigurationProperties`), and mirrors the datatrans base-url / reconciliation defaults the
  isolated `ApplicationContextRunner` tests read from the (now shadowed) main `application.yml`.

## 4. Verify structured logging behaviour

> Verified on JDK 25 (`~/.sdkman/candidates/java/25.0.2-graalce`; the backend needs JDK 25 per the
> SB4 parent): 4.1 build passes and 4.4 all 521 tests pass. The integration-profile OpenAPI run
> shows `spring.application.name` resolving and the trace MDC populated —
> `[payment-orchestration-service,<traceId>,<spanId>]`.
>
> The JSON console (4.2) requires a COMPLETED context refresh (that is when `FluentdLogger` is
> constructed). Earlier plain-text output was NOT an environment gap — it was the missing component
> scan (group 3); with `scanBasePackages` widened the bean is constructed and JSON engages. 4.2/4.3
> are confirmed by running the service with the group-3 changes in place (the user's box has Temporal
> + Kafka up), or in CI / a deployed pod.

- [x] 4.1 Build the service and confirm it compiles with the new dependency:
  `cd backend && ./mvnw -q clean install -pl book-pay/services/payment-orchestration-service -am -DskipTests`.
- [x] 4.2 Run the service with a deployed-style profile and confirm the console emits
  FluentD/Logstash JSON. Confirmed: the running context emits
  `{"appName":"payment-orchestration-service", ... ,"level":"INFO","logger_name":"..."}` lines with
  the trace MDC populated on request-scoped logs. The JSON appender engages once the widened scan
  constructs `FluentdLogger`.
- [x] 4.3 Verified by running the service: the console emits FluentD/Logstash JSON lines with
  `"appName":"payment-orchestration-service"`, `@timestamp`, `logger_name`, `message`, `thread_name`
  (visual confirmation).
- [x] 4.4 Run the existing test suite to confirm no regression:
  `cd backend && ./mvnw -q test -pl book-pay/services/payment-orchestration-service` (521 tests pass).

## 5. Validate the change

- [x] 5.1 Run `openspec validate add-payment-orchestration-structured-logging --strict` and confirm it
  reports valid (skip_specs INFO expected, no errors).
