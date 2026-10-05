package uk.co.whitbread.integrationtests.testkit.auth

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jwt.SignedJWT
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import uk.co.whitbread.integrationtests.testkit.model.LoggedUser

private val testUser =
    LoggedUser(
        accessLevel = "SUPER",
        companyAccountId = "company-account-1",
        companyId = "company-1",
        employeeId = "employee-1",
        email = "integration.user@example.test",
    )

/**
 * Pins the signed token against the published JWKS, and the public/private split.
 *
 * The key is deliberately fixed rather than generated per process: the mocked JWKS mapping is
 * unscoped and shared, WireMock serves the most recently installed of equally matching mappings,
 * and a per-process key therefore let two JVMs overwrite each other's JWKS. That stability is
 * guarded by `BakedAuthMappingsTest`, which compares [AuthTokens.jwksJson] against the mapping
 * checked in under `integration-env`; a regression to a generated key changes the modulus and fails
 * there. It cannot be guarded here — every assertion in one JVM reads the same memoised key, so a
 * per-process generator with the same `kid` would satisfy it.
 */
class AuthTokensTest :
    FunSpec({

        test("the published JWKS carries the key that signed the token") {
            val header = SignedJWT.parse(AuthTokens.jwt(testUser)).header
            val published = JWKSet.parse(AuthTokens.jwksJson()).keys.single()

            header.keyID shouldBe published.keyID
        }

        test("the published JWKS exposes no private key material") {
            val published = JWKSet.parse(AuthTokens.jwksJson()).keys.single()

            published.isPrivate shouldBe false
            // The RSA private exponent, its prime factors, and their CRT companions.
            val json = published.toJSONString()
            json shouldNotContain "\"d\""
            json shouldNotContain "\"p\""
            json shouldNotContain "\"q\""
            json shouldNotContain "\"dp\""
            json shouldNotContain "\"dq\""
            json shouldNotContain "\"qi\""
        }
    })
