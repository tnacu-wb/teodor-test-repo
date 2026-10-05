package uk.co.whitbread.integrationtests.testkit.auth

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.BakedMappings
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.readText

private val BAKED_MAPPINGS_DIR = Path("../integration-env/wiremock")
private const val JWKS_MAPPING = "cdh-auth0/mappings/auth-jwks.json"

private val BAKED_MAPPING_FILES =
    listOf(
        JWKS_MAPPING,
        "cdh-auth0/mappings/cdh-oauth-token.json",
        "opera/mappings/opera-oauth-token.json",
    )

/**
 * Pins the JWKS the integration stack serves against the key this helper signs with.
 *
 * The mapping is a static file loaded by WireMock at startup rather than something a test
 * installs, because the services' JWT decoders do not propagate the scenario baggage header and
 * so the mapping could never be scoped to one scenario. Nothing else in the suite compares the two:
 * a stale `n` or `kid` in that file would fail every journey at once with an opaque 401, so this
 * test exists to name the cause. See `backend/integration-env/wiremock/README.md`.
 */
class BakedAuthMappingsTest :
    FunSpec({

        test("the stack's JWKS mapping publishes exactly the key AuthTokens signs with") {
            val response = bakedMapping(JWKS_MAPPING)["response"].shouldNotBeNull().jsonObject
            val served = response["jsonBody"].shouldNotBeNull()
            val expected = Json.parseToJsonElement(AuthTokens.jwksJson())

            withClue(
                "$JWKS_MAPPING is out of step with keys/integration-test-signing-key.json. " +
                    "Regenerate it from the signing key rather than hand-editing the modulus.",
            ) {
                served shouldBe expected
            }
        }

        test("the stack's JWKS mapping exposes no private key material") {
            // d, p and q are the RSA private exponent and prime factors.
            val text = (BAKED_MAPPINGS_DIR / JWKS_MAPPING).readText()

            text shouldNotContain "\"d\""
            text shouldNotContain "\"p\""
            text shouldNotContain "\"q\""
        }

        test("preflight requires exactly the mapping names the stack bakes in") {
            val onDisk =
                BAKED_MAPPING_FILES.map { path ->
                    withClue("$path must declare a name; preflight matches the mapping on it") {
                        bakedMapping(path)["name"]?.jsonPrimitive?.contentOrNull.shouldNotBeNull()
                    }
                }

            withClue(
                "BakedMappings is the set preflight asserts is present on the stack's WireMocks. " +
                    "A name changed or added on disk without updating it leaves preflight " +
                    "asserting a mapping that no longer exists, or silently not asserting a new one.",
            ) {
                BakedMappings.CDH + BakedMappings.OPERA shouldBe onDisk.toSet()
            }
        }
    })

private fun bakedMapping(relativePath: String): JsonObject =
    Json.parseToJsonElement((BAKED_MAPPINGS_DIR / relativePath).readText()).jsonObject
