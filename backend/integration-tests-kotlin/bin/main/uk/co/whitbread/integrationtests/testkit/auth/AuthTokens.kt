package uk.co.whitbread.integrationtests.testkit.auth

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import uk.co.whitbread.integrationtests.testkit.model.LoggedUser
import java.time.Clock
import java.time.Duration
import java.util.Date

/**
 * Creates Auth0-style JWTs for integration tests that call services protected by
 * `common-auth0` / `@EnableAuth`.
 *
 * The token is signed with a fixed, test-only RSA private key checked in under
 * `src/main/resources/keys`, so every JVM signs with the same key. Services validate the
 * signature by calling their configured JWKS URI, which the integration stack serves from
 * `backend/integration-env/wiremock/cdh-auth0/mappings/auth-jwks.json` — no test installs it.
 * The private key never leaves this helper; that mapping publishes only the matching public
 * key. A protected service reads the JWT `kid`, fetches JWKS, and uses that public key to
 * verify that the JWT was signed by the matching private key.
 *
 * ```text
 * private key in AuthTokens          public half baked into the stack's
 *   signs JWT                        WireMock startup mappings
 *      |                                        |
 *      v                                        v
 * JWT sent in Authorization / WB-Authorization  |
 *      |                                        |
 *      v                                        |
 * protected service fetches JWKS from WireMock <'
 *      |
 *      v
 * public key from JWKS verifies the JWT signature
 * ```
 *
 * This is reusable across services as long as their local auth configuration points at
 * the same issuer, namespace, and JWKS endpoint used by this helper.
 */
object AuthTokens {
    const val ISSUER = "https://mock-auth0-domain/"
    const val AUDIENCE = "https://mock-auth0-audience/api/v2/"
    const val NAMESPACE = "https://premierinn.com"

    private const val SIGNING_KEY_RESOURCE = "/keys/integration-test-signing-key.json"

    /**
     * Test-only RSA key pair, checked in at [SIGNING_KEY_RESOURCE].
     *
     * **This is not a leaked credential.** It signs nothing outside the disposable local
     * integration stack, no deployed environment trusts it — real environments resolve their JWKS
     * from Auth0 rather than from WireMock — and the only data it reaches is synthetic fixture data
     * created by the tests themselves. The private half never leaves this JVM: [jwksJson] publishes
     * `toPublicJWK()`, so WireMock and every service receive public key material only. See
     * `src/main/resources/keys/README`.
     *
     * It is loaded rather than generated because the JWKS that publishes its public half is a
     * static file owned by the stack, not something a test installs — the services' JWT decoders
     * do not propagate the scenario baggage header, so that mapping could never be scoped to one
     * scenario or reclaimed by its cleanup. A key generated per process could not be published
     * that way at all, and previously meant a Gradle test worker and the local provisioning server
     * overwrote each other's JWKS, with whichever lost seeing its tokens rejected.
     *
     * The two must therefore agree. `BakedAuthMappingsTest` fails if they drift.
     */
    private val rsaKey: RSAKey by lazy {
        val key =
            checkNotNull(AuthTokens::class.java.getResource(SIGNING_KEY_RESOURCE)) {
                "Missing integration-test signing key on the classpath: $SIGNING_KEY_RESOURCE"
            }
        RSAKey.parse(key.readText())
    }

    private val keyId: String get() = rsaKey.keyID

    /**
     * Returns the complete header value for `Authorization` or `WB-Authorization`.
     *
     * Example:
     *
     * ```kotlin
     * wbAuthorization = AuthTokens.bearer(booking.loggedUser!!)
     * ```
     */
    fun bearer(
        loggedUser: LoggedUser,
        clock: Clock = Clock.systemUTC(),
        ttl: Duration = Duration.ofHours(1),
    ): String = "Bearer ${jwt(loggedUser, clock, ttl)}"

    /**
     * Builds and signs the raw JWT without the `Bearer ` prefix.
     *
     * Claims are shaped for the account parser in `common-auth0`: namespace-prefixed
     * account claims plus the nested `profile` values used by authorization checks such
     * as `authentication.account.accessLevel == 'SUPER'`.
     */
    fun jwt(
        loggedUser: LoggedUser,
        clock: Clock = Clock.systemUTC(),
        ttl: Duration = Duration.ofHours(1),
    ): String {
        val now = clock.instant()
        val claims =
            JWTClaimsSet
                .Builder()
                .issuer(ISSUER)
                .audience(AUDIENCE)
                .subject(loggedUser.email)
                .issueTime(Date.from(now))
                .notBeforeTime(Date.from(now.minusSeconds(5)))
                .expirationTime(Date.from(now.plus(ttl)))
                .claim("$NAMESPACE/companyAccountId", loggedUser.companyAccountId)
                .claim("$NAMESPACE/employeeAccountId", loggedUser.employeeId)
                .claim("$NAMESPACE/email", loggedUser.email)
                .claim("email", loggedUser.email)
                .claim(
                    "profile",
                    mapOf(
                        "accessLevel" to loggedUser.accessLevel,
                        "companyId" to loggedUser.companyId,
                        "employeeId" to loggedUser.employeeId,
                    ),
                ).build()

        val signedJwt =
            SignedJWT(
                JWSHeader
                    .Builder(JWSAlgorithm.RS256)
                    .keyID(keyId)
                    .type(JOSEObjectType.JWT)
                    .build(),
                claims,
            )

        signedJwt.sign(RSASSASigner(rsaKey))
        return signedJwt.serialize()
    }

    /**
     * Public JWKS document matching the private signing key used by [jwt].
     *
     * This intentionally contains only public key material and is safe to serve from
     * WireMock.
     */
    fun jwksJson(): String = JWKSet(rsaKey.toPublicJWK()).toString()
}
