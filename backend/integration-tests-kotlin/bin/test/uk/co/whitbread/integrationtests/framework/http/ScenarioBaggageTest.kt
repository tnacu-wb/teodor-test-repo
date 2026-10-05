package uk.co.whitbread.integrationtests.framework.http

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag

/** Verifies how scenario feature-flag overrides are encoded into the request baggage header. */
class ScenarioBaggageTest :
    FunSpec({
        test("request baggage uses only the test ID when no feature flags are overridden") {
            scenarioBaggage("scenario-1") shouldBe "wb-test-id=scenario-1"
        }

        test("request baggage includes the feature flag's Unleash key") {
            scenarioBaggage(
                testId = "scenario-1",
                featureFlagOverrides =
                    mapOf(
                        OhipFeatureFlag.BB_FLEX_RATE_STRIKETHROUGH to false,
                    ),
            ) shouldBe
                "wb-test-id=scenario-1," +
                "wb-feature-overrides=release_bb_flex_rate_strikethrough:off"
        }

        test("appending overrides keeps the test ID matchable by scenario-owned stubs") {
            val testId = "scenario-1"
            val baggage =
                scenarioBaggage(
                    testId = testId,
                    featureFlagOverrides = mapOf(OhipFeatureFlag.BB_FLEX_RATE_STRIKETHROUGH to false),
                )
            val matcher = requireNotNull(testIdHeaderMatcher(testId).matches)

            Regex(matcher).matches(baggage) shouldBe true
        }
    })
