package uk.co.whitbread.integrationtests.framework.http

/** Baggage member carrying this scenario's feature-flag overrides. */
const val FEATURE_FLAG_OVERRIDES_BAGGAGE_KEY = "wb-feature-overrides"

/**
 * One flag whose override can travel in the request baggage.
 *
 * The framework owns only this abstraction. The sealed, author-facing flag vocabulary in
 * `testkit.featureflags` implements it, so the override encoding lives next to the rest of
 * the baggage grammar without a `framework` -> `testkit` edge.
 */
interface BaggageFlag {
    /** Flag name exactly as the service under test resolves it. */
    val key: String
}

/**
 * Builds the complete `baggage` header for one service-under-test call.
 *
 * The test-ID member is always present. Overrides are appended as a second member only
 * when the scenario asks for them, sorted by flag key so the value is deterministic.
 */
fun scenarioBaggage(
    testId: String,
    featureFlagOverrides: Map<out BaggageFlag, Boolean> = emptyMap(),
): String =
    baggageHeaderValue(
        buildList {
            add(testIdHeaderValue(testId))
            if (featureFlagOverrides.isNotEmpty()) {
                val overrides =
                    featureFlagOverrides.entries
                        .sortedBy { it.key.key }
                        .joinToString("|") { (flag, enabled) -> "${flag.key}:${if (enabled) "on" else "off"}" }
                add("$FEATURE_FLAG_OVERRIDES_BAGGAGE_KEY=$overrides")
            }
        },
    )
