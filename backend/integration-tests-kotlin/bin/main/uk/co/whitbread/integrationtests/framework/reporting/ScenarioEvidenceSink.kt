package uk.co.whitbread.integrationtests.framework.reporting

import uk.co.whitbread.integrationtests.framework.http.HttpEvidence
import uk.co.whitbread.integrationtests.framework.http.HttpEvidenceRecorder
import uk.co.whitbread.integrationtests.framework.wiremock.CleanupResult
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

/** The final execution state written into a scenario evidence artifact. */
internal enum class ScenarioEvidenceOutcome {
    /** The scenario and its cleanup completed successfully. */
    PASSED,

    /** The scenario, cleanup, or both failed. */
    FAILED,
}

/** One named, ordered section in a scenario evidence artifact. */
internal data class EvidenceSection(
    /** Human-readable heading rendered above [body]. */
    val name: String,
    /** Preformatted evidence content rendered below [name]. */
    val body: String,
)

/**
 * Collects bounded diagnostic evidence for one journey scenario and writes one text artifact.
 *
 * Recording is synchronized so HTTP and stub callbacks may safely contribute from concurrent
 * coroutines while preserving the order in which entries reach the sink. The default output is
 * `build/test-evidence/<testId>/evidence.txt`.
 *
 * @property testId unique scenario identifier used as the artifact directory name.
 * @param rootDirectory root directory under which the scenario directory is created.
 */
internal class ScenarioEvidenceSink(
    val testId: String,
    private val rootDirectory: Path = Path.of("build", "test-evidence"),
) : StubInstallEvidenceSink,
    HttpEvidenceRecorder {
    /** Synchronization monitor protecting ordered section mutation and snapshots. */
    private val lock = Any()

    /** Mutable ordered evidence accumulated until final artifact writing. */
    private val sections = mutableListOf<EvidenceSection>()

    /** Final artifact path for this scenario. */
    val artifactPath: Path = rootDirectory.resolve(testId).resolve("evidence.txt")

    /**
     * Appends one preformatted section to this scenario in arrival order.
     *
     * @param name section heading.
     * @param body section content.
     */
    fun recordSection(
        name: String,
        body: String,
    ) {
        synchronized(lock) {
            sections += EvidenceSection(name, body)
        }
    }

    /**
     * Formats and records one successfully installed WireMock mapping in scenario order.
     *
     * @param mockName stable logical mock identifier selected by the installation plan.
     * @param mapping mapping accepted by WireMock.
     * @param testId scenario ownership identifier carried by the mapping.
     */
    override fun recordInstalledStub(
        mockName: String,
        mapping: StubMapping,
        testId: String,
    ) {
        recordSection(
            name = "Installed Stub: $mockName",
            body = StubFormatUtils.formatEvidenceEntry(mockName, mapping, testId),
        )
    }

    /**
     * Records an installation plan that intentionally produced no WireMock mappings.
     *
     * @param modelName logical scenario-data model evaluated by the plan.
     * @param testId scenario ownership identifier for the plan.
     */
    override fun recordEmptyPlan(
        modelName: String,
        testId: String,
    ) {
        recordSection(
            name = "Installed Stubs: $modelName",
            body = "[$modelName] [$testId] — no mocks installed",
        )
    }

    override fun recordFullyExcludedPlan(
        modelName: String,
        excluded: Set<String>,
        testId: String,
    ) {
        recordSection(
            name = "Installed Stubs: $modelName",
            body =
                "[$modelName] [$testId] — all ${excluded.size} selected stubs excluded " +
                    "(${excluded.sorted()}); no defaults installed",
        )
    }

    /**
     * Records one captured service-under-test request and response.
     *
     * @param prefix optional test-author label added to both section headings.
     * @param evidence structured request and response evidence.
     * @param failure optional capture or decoding failure associated with the exchange.
     */
    override fun recordHttp(
        prefix: String?,
        evidence: HttpEvidence,
        failure: Throwable?,
    ) {
        evidenceSections(prefix, evidence, failure).forEach { section ->
            recordSection(section.name, section.body)
        }
    }

    /**
     * Records the complete per-scenario cleanup outcome, including every structured failure.
     *
     * @param result result returned by strict test-ID cleanup or carried by its exception.
     */
    fun recordCleanup(result: CleanupResult) {
        recordSection("Cleanup", formatCleanup(result))
    }

    /**
     * Records the primary scenario failure without duplicating the stack trace already retained by
     * Kotest and JUnit.
     *
     * @param failure scenario or cleanup exception that determines the failed outcome.
     */
    fun recordScenarioFailure(failure: Throwable) {
        recordSection(
            name = "Scenario Failure",
            body = "${failure::class.qualifiedName}: ${failure.message ?: "<no message>"}",
        )
    }

    /**
     * Writes all recorded sections to [artifactPath], replacing an artifact for the same test ID.
     *
     * @param outcome final scenario execution state.
     * @return the path written for reporting to stdout.
     * @throws java.io.IOException when the scenario directory or artifact cannot be written.
     */
    fun write(outcome: ScenarioEvidenceOutcome): Path {
        val snapshot = synchronized(lock) { sections.toList() }
        Files.createDirectories(artifactPath.parent)
        Files.writeString(
            artifactPath,
            renderArtifact(outcome, snapshot),
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE,
        )
        return artifactPath
    }

    /** Renders the artifact header and ordered sections into stable plain text. */
    private fun renderArtifact(
        outcome: ScenarioEvidenceOutcome,
        snapshot: List<EvidenceSection>,
    ): String =
        buildString {
            appendLine("Test ID: $testId")
            appendLine("Outcome: $outcome")
            snapshot.forEach { section ->
                appendLine()
                appendLine("=== ${section.name} ===")
                appendLine(section.body)
            }
        }.trimEnd() + "\n"

    /** Formats the cleanup outcome and individual failures for a scenario artifact. */
    private fun formatCleanup(result: CleanupResult): String =
        buildString {
            appendLine("Scope: testId '${result.testId}'")
            appendLine("Completed: ${result.completed}")
            appendLine("Mappings removed: ${result.removedMappings}")
            appendLine("Request events removed: ${result.removedRequestEvents}")
            if (result.failures.isNotEmpty()) {
                appendLine("Failures:")
                result.failures.forEach { failure ->
                    append("- ${failure.wireMockName} (${failure.wireMockUrl}) ${failure.operation}")
                    failure.mappingId?.let { mappingId -> append(" mapping=$mappingId") }
                    appendLine(": ${failure.cause.message ?: failure.cause::class.simpleName ?: "unknown failure"}")
                }
            }
        }.trimEnd()
}
