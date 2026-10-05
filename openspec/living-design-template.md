# <Capability> Design

Related contract: [spec.md](./spec.md)

<!--
Describe the current implemented architecture and recorded rationale for one capability.
Proposed redesigns belong under Open questions, explicitly labelled as proposals;
do not present future service boundaries as existing architecture. Keep
normative, externally observable behaviour in spec.md and link to it instead of
repeating requirements here.
Code-derived wire contracts belong below; reference the governing requirements
without copying their normative text or scenarios. This is a standalone HLD/LLD:
no source-file/line citations, Evidence paragraphs, or workflow_plan links and provenance
in the living document. Use exact repository service names followed by (hosting-squad) in prose and
diagrams. Identify internal modules/libraries as internal to their actual service,
external products by their actual names, and unknown hosting explicitly; never invent a
service name for a responsibility. Short diagram aliases are fine when the visible label
uses the actual name. These rules do not exclude useful endpoint or wire-field names.
-->

## Boundary and participants

Capability owner: <squad responsible for the complete product outcome>

<!-- State what this capability owns, its boundary, and its principal actors.
Distinguish capability ownership from the squads currently hosting its services.
Implementation can span squads without moving the capability or the services. -->

## Architecture and workflow

<!-- Lead with a short explanation of how the feature works, before inventories and API
schemas. Describe the entry action, orchestration, ordered calls and their purpose,
conditional branches, resulting state and the caller-visible completion point. Separate
independent requests and user actions; do not imply an automatic chain without evidence.
Add Mermaid sequences when they clarify the implementation. Identify each deployed
service with its exact repository name (hosting-squad), and internal work as internal.

Explain where state lives and distinguish intermediate results from completed outcomes
(for example returned bytes, device installation, a local callback and hotel check-in).
Describe partial writes, early exits, retries and recovery limits before API details.
Do not imply atomicity, rollback, idempotency or success that the code does not establish.
Keep this capability bounded and link neighbours for wider flows.

Parse AND render every Mermaid block with an actual Mermaid renderer and inspect the output;
fence checks alone are not validation. Avoid semicolons in sequence message/note labels: they can split statements.
-->

### Implementation walkthrough

<Ordered explanation of the main flow and relevant alternate entry paths.>

### State and completion

<Where state lives, what each success signal establishes, and what remains separate.>

### Branches, failures and recovery

<Important gates, partial effects, failure exits and actual recovery behaviour.>

## Participating surfaces

<!-- Assess every surface. Use "Not affected" with a short reason when a surface
is intentionally outside the capability. -->

| Surface | Responsibility and impact |
|---|---|
| Backend | <APIs, orchestration, persistence, events, or not affected> |
| Web | <UI flow, client integration, or not affected> |
| iOS | <Mobile flow, client integration, or not affected> |
| Android | <Mobile flow, client integration, or not affected> |
| GraphQL/BFF | <Aggregation, translation, or not affected> |

<!-- List components that implement this capability or provide a necessary reusable
contract. Scope shared components to the relevant branch or operation. Incidental page
behaviour does not expand the design. Use "Not established" for unknown hosting owners. -->

| Component or service | Surface | Current hosting squad | Contribution and role |
|---|---|---|---|
| <component> | <surface> | <squad or not established> | <capability implementation or reusable dependency; specific scope> |

## API contracts

<!-- Document the backend endpoints used by this capability, including implementation
hosted by other squads and the relevant directly consumed dependency operations.
Do not expand into an inventory of unrelated endpoints in shared services.
If the capability has no relevant HTTP API, state "None" and identify its actual
entry mechanism (for example a local action or event) with its owning component.

For each operation, record:
- serving service and current hosting squad; implementation or dependency role;
- HTTP method and complete service-relative path, distinguishing any evidenced gateway
  prefix from the service mapping;
- callers and transport mapping: relevant GraphQL operation, web route or mobile call;
- authentication/authorization enforced in code, forwarded credentials and any unknown
  gateway/network enforcement;
- request content type, path/query/header parameters and body fields, including types,
  nested structures, required/optional/nullable distinctions and validation actually applied;
- success status, content type and response shape (including binary/empty responses);
- relevant error statuses and payloads, with their producing/translation layer;
- governing requirement links.

Use endpoint tables and compact field tables or schemas. Include clearly labelled
synthetic request/response examples for primary operations where useful; examples are
illustrative, not captured traffic or exhaustive schemas. Distinguish full primary
operation shapes from consumed-field projections of broad dependency responses.
Do not infer requiredness from Java types, validation from unused annotations, GraphQL
nullability as REST validation, or actual output from an unused DTO field. Check controller,
client, serializer and exception handling together. Do not invent missing contracts,
deployed URL prefixes or provider guarantees: write "Not established" with an owned
open question. Compare API annotations with controller and exception-handler results;
label documentation/runtime, version or caller/server mismatches explicitly.
-->

## Design decisions

### <Decision>

Current implementation choice: <what the code demonstrably does>

Rationale: <documented reason, or "Rationale not recorded">

Consequences: <supported architectural consequences and trade-offs>

<!-- Repeat this block per decision. Clearly label inferred consequences and explain their
basis; a plausible reason is not evidence of why a choice was made. -->

## Feature flags and rollout

<!--
For each relevant flag, record:
- flag name;
- owner and evaluation point;
- affected surfaces;
- link to the governing requirement in spec.md;
- configured default, missing-flag fallback and failure behaviour, distinguished from live state;
- removal condition.

Also record backend configuration switches and material configured defaults: key,
default, and behaviour when unset. These are not live state.

Record stable rollout constraints, not current environment values or rollout
percentages. Write "None" when the capability has no feature flags.
For any owner, fallback, failure behaviour or removal condition not established by
evidence, write "Not established" and add an owned open question. Do not infer a flag's
live state from defaults or invent a removal condition. A missing governing requirement
is a spec gap to record, not a requirement to invent in this design.
-->

## Failure, security, and operability

<!-- Capture only concerns that materially shape this capability. -->

## Neighbouring capabilities

<!-- State each related capability's boundary and link its spec.md and design.md when
those files exist. For a known capability whose artifact is absent, record the capability
name and "Spec not yet generated" or "Design not yet generated" as plain text instead
of creating a broken link. A shared service alone does not establish a neighbour. -->

## Open questions

<!-- Include an owner for each unresolved decision, normally the capability-owning squad,
and identify any contributing squad needed to resolve it. Mark redesign ideas as
"Proposed redesign" and keep them separate from facts about current implementation.
Record a divergence between the code and spec.md here when it needs a decision; plain
defects belong in the issue tracker, not in this document.
Write "None" when resolved. -->
