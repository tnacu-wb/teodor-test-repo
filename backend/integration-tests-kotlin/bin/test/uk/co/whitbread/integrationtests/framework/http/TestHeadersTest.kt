package uk.co.whitbread.integrationtests.framework.http

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern

/** Verifies the baggage grammar and the matcher that isolates scenario-owned WireMock mappings. */
class TestHeadersTest :
    FunSpec({
        test("test ID header value uses the configured baggage member") {
            testIdHeaderValue("scenario-1") shouldBe "wb-test-id=scenario-1"
        }

        test("baggage members are joined with the W3C list separator") {
            baggageHeaderValue(listOf("wb-test-id=scenario-1")) shouldBe "wb-test-id=scenario-1"
            baggageHeaderValue(
                listOf("wb-test-id=scenario-1", "wb-feature-overrides=a-flag:off"),
            ) shouldBe "wb-test-id=scenario-1,wb-feature-overrides=a-flag:off"
        }

        test("matcher accepts the test ID as an exact or positioned baggage member") {
            val matcher = testIdHeaderMatcher("scenario-1")

            matcher.matchesHeader("wb-test-id=scenario-1") shouldBe true
            matcher.matchesHeader("wb-test-id=scenario-1,trace-id=trace-1") shouldBe true
            matcher.matchesHeader("trace-id=trace-1, wb-test-id=scenario-1,locale=en-GB") shouldBe true
            matcher.matchesHeader("trace-id=trace-1,wb-test-id=scenario-1") shouldBe true
        }

        test("matcher accepts baggage metadata attached to the test ID") {
            val matcher = testIdHeaderMatcher("scenario-1")

            matcher.matchesHeader("wb-test-id=scenario-1;source=journey") shouldBe true
            matcher.matchesHeader("trace-id=trace-1, wb-test-id=scenario-1;source=journey;version=1,locale=en-GB") shouldBe true
        }

        test("matcher rejects prefixes suffixes similar keys and near-collision IDs") {
            val matcher = testIdHeaderMatcher("scenario-1")

            matcher.matchesHeader("wb-test-id=prefix-scenario-1") shouldBe false
            matcher.matchesHeader("wb-test-id=scenario-1-suffix") shouldBe false
            matcher.matchesHeader("other-wb-test-id=scenario-1") shouldBe false
            matcher.matchesHeader("wb-test-id-extra=scenario-1") shouldBe false
            matcher.matchesHeader("wb-test-id=scenario-10") shouldBe false
            matcher.matchesHeader("trace-id=scenario-1") shouldBe false
        }

        test("matcher treats regex metacharacters in test IDs literally") {
            val testId = "scenario.[1]+(west)\\E"
            val matcher = testIdHeaderMatcher(testId)

            matcher.matchesHeader(testIdHeaderValue(testId)) shouldBe true
            matcher.matchesHeader("wb-test-id=scenarioX11westE") shouldBe false
            matcher.matchesHeader("wb-test-id=scenario.[1]+(west)") shouldBe false
        }

        test("ownership recognition accepts only exact-value or canonical regex matchers") {
            val testId = "scenario-1"

            StringValuePattern(equalTo = testIdHeaderValue(testId)).matchesTestId(testId) shouldBe true
            testIdHeaderMatcher(testId).matchesTestId(testId) shouldBe true
            testIdHeaderMatcher("scenario-10").matchesTestId(testId) shouldBe false
            StringValuePattern(matches = ".*scenario-1.*").matchesTestId(testId) shouldBe false
        }
    })

/** Evaluates this WireMock string pattern against one complete baggage header value. */
private fun StringValuePattern.matchesHeader(value: String): Boolean = equalTo == value || matches?.let { Regex(it).matches(value) } == true
