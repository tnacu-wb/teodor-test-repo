package uk.co.whitbread.integrationtests.framework.wiremock

/**
 * Names of the stack-owned WireMock mappings loaded from disk at container startup.
 *
 * These are authentication plumbing rather than test data. The callers that reach them — the
 * services' JWT decoders and OAuth clients — do not propagate the scenario baggage header, so the
 * mappings can carry no ownership matcher and no scenario cleanup could ever reclaim them. They are
 * therefore bind-mounted from `backend/integration-env/wiremock` instead of being installed by a
 * test. See that directory's `README.md`.
 *
 * Preflight asserts they are present, because their absence is otherwise silent: Docker creates a
 * missing bind-mount source as an empty directory, and a WireMock serving zero mappings answers
 * both the Compose healthcheck and `GET /__admin/mappings` successfully. Without this check the
 * first symptom is every authenticated journey failing at once with an opaque 401.
 *
 * `BakedAuthMappingsTest` pins each constant against the `name` in the mapping file itself, so a
 * rename on disk cannot leave this list quietly asserting a mapping that no longer exists.
 */
internal object BakedMappings {
    const val AUTH_JWKS = "auth.jwks"
    const val CDH_OAUTH_TOKEN = "cdh.oauth-token"
    const val OPERA_OAUTH_TOKEN = "opera.oauth-token"

    /** Served by the `wiremock-cdh-auth0` container. */
    val CDH = setOf(AUTH_JWKS, CDH_OAUTH_TOKEN)

    /** Served by the `wiremock-opera` container. */
    val OPERA = setOf(OPERA_OAUTH_TOKEN)
}
