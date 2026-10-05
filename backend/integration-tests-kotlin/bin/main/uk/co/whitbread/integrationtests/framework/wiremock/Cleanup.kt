package uk.co.whitbread.integrationtests.framework.wiremock

/** The WireMock Admin operation that could not be completed. */
enum class CleanupOperation {
    /** Reading mappings before selecting those owned by a test ID. */
    LIST_MAPPINGS,

    /** Deleting one mapping selected by its WireMock-assigned ID. */
    DELETE_MAPPING,

    /** Removing request-journal events selected by the scenario baggage matcher. */
    REMOVE_REQUEST_EVENTS,
}

/**
 * Structured description of one incomplete cleanup operation.
 *
 * [mappingId] is populated only for mapping-specific deletion failures. The original
 * [cause] is retained so callers and reports keep the underlying HTTP or decoding error.
 */
data class CleanupFailure(
    val wireMockName: String,
    val wireMockUrl: String,
    val operation: CleanupOperation,
    val mappingId: String? = null,
    val cause: Throwable,
)

/**
 * Aggregate outcome of one complete cleanup attempt for [testId].
 *
 * Cleanup always attempts every reachable target before producing this result, so an incomplete
 * attempt still reports what it did remove. Extra mappings installed but unused by a scenario are
 * removed as well, because ownership is decided by the test-ID matcher rather than by what the
 * scenario consumed. Each entry in [failures] names the target, operation, and mapping that could
 * not be completed.
 */
data class CleanupResult(
    val testId: String,
    val removedMappings: Int,
    val removedRequestEvents: Int,
    val failures: List<CleanupFailure>,
) {
    /** `true` only when every attempted cleanup operation completed. */
    val completed: Boolean = failures.isEmpty()

    /**
     * Returns this result when complete or throws one exception containing every failure.
     *
     * @throws CleanupException when [completed] is `false`.
     */
    fun orThrow(): CleanupResult {
        if (!completed) throw CleanupException(this)
        return this
    }
}

/**
 * Signals that cleanup remained incomplete after all reachable operations were attempted.
 *
 * The structured [result] is available to Kotlin tests and REST callers. Each
 * underlying failure cause is also attached as a suppressed exception for stack traces.
 */
class CleanupException(
    val result: CleanupResult,
) : IllegalStateException(messageFor(result)) {
    init {
        require(!result.completed) { "CleanupException requires an incomplete CleanupResult" }
        result.failures.forEach { failure -> addSuppressed(failure.cause) }
    }

    private companion object {
        /** Formats all failures into one stable diagnostic message. */
        fun messageFor(result: CleanupResult): String =
            buildString {
                appendLine(
                    "Cleanup failed for testId '${result.testId}': " +
                        "${result.failures.size} operation(s) incomplete",
                )
                result.failures.forEach { failure ->
                    append("- ${failure.wireMockName} (${failure.wireMockUrl}) ")
                    append(failure.operation)
                    failure.mappingId?.let { append(" mapping=$it") }
                    append(": ${failure.cause.message ?: failure.cause::class.simpleName ?: "unknown failure"}")
                    appendLine()
                }
            }.trimEnd()
    }
}
