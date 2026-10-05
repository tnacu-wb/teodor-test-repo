# WireMock startup mappings

Mappings in these folders are mounted read-only into the WireMock containers at
`/home/wiremock/mappings` and loaded at startup. They belong to the **stack**, not to any test.

Only authentication plumbing lives here:

| File | `name` | Container | Serves |
| --- | --- | --- | --- |
| `cdh-auth0/mappings/auth-jwks.json` | `auth.jwks` | `wiremock-cdh-auth0` | the Auth0 JWKS that services fetch to verify JWT signatures |
| `cdh-auth0/mappings/cdh-oauth-token.json` | `cdh.oauth-token` | `wiremock-cdh-auth0` | the CDH client-credentials token response |
| `opera/mappings/opera-oauth-token.json` | `opera.oauth-token` | `wiremock-opera` | the Opera client-credentials token response |

**The `name` field is load-bearing.** The integration suite's environment preflight asserts these
mappings are present by name before it runs any journey, so an unmounted or empty directory fails
once with a clear message instead of producing a wave of opaque 401s. The names are listed in
`BakedMappings` in `backend/integration-tests-kotlin`; `BakedAuthMappingsTest` fails if the two
drift, so rename here and there together.

## Why these three are not installed by the tests

`backend/integration-tests-kotlin` installs every other mapping itself, scoped to one scenario by
a `baggage: wb-test-id=<id>` header matcher, and removes it during that scenario's cleanup.

These three cannot work that way. The callers that hit them — Spring Security's JWT decoder and
the services' OAuth clients — do not propagate the scenario baggage header, so the mappings cannot
carry the matcher that makes them scoped. Installed at runtime they were therefore unscoped,
unreclaimable, and re-added by every scenario: a stack that had run the suite a few times held 73
JWKS and 787 OAuth duplicates, which cleanup then parsed on every subsequent scenario.

Loading them from disk removes the problem rather than managing it. It also means `docker compose
up` alone gives you a working authentication stack, before any service boots and without running a
test first.

## The JWKS and the test signing key must agree

`auth-jwks.json` publishes the **public** half of the fixed RSA key checked in at
`backend/integration-tests-kotlin/src/main/resources/keys/integration-test-signing-key.json`, which
is what `AuthTokens` signs integration-test JWTs with. If one changes, so must the other.

That coupling is guarded — `BakedAuthMappingsTest` in `backend/integration-tests-kotlin` fails if
this file stops matching `AuthTokens.jwksJson()`. Do not hand-edit the `keys` array; regenerate it
from the signing key.

No private key material is present here, and none should ever be added: these files are served to
every service in the stack.

## Editing

Changes require a container restart (`docker compose up -d --force-recreate wiremock-cdh-auth0`)
because the files are read once at startup. WireMock also reloads them on
`POST /__admin/mappings/reset`, which the integration suite never calls — its cleanup deletes
mappings individually by ID, so it will not disturb these.
