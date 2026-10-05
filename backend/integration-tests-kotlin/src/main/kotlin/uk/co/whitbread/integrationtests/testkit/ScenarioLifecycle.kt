package uk.co.whitbread.integrationtests.testkit

import uk.co.whitbread.integrationtests.framework.reporting.ScenarioEvidenceOutcome
import uk.co.whitbread.integrationtests.framework.reporting.ScenarioEvidenceSink
import java.nio.file.Path

/**
 * Writes one scenario artifact and reports its path without masking an existing failure.
 *
 * If writing or path reporting fails for a successful scenario, that failure is thrown. When
 * [primaryFailure] already exists, the evidence failure is attached as suppressed because the
 * caller is already propagating the primary scenario or cleanup exception.
 *
 * @param sink completed scenario evidence sink.
 * @param outcome final scenario state written into the artifact header.
 * @param primaryFailure failure already being propagated, if any.
 * @param reportPath output callback; defaults to one concise stdout line.
 * @throws Throwable when artifact writing fails and no primary failure exists.
 */
internal fun finalizeScenarioEvidence(
    sink: ScenarioEvidenceSink,
    outcome: ScenarioEvidenceOutcome,
    primaryFailure: Throwable?,
    reportPath: (Path) -> Unit = { path -> println("Scenario evidence: $path") },
) {
    try {
        reportPath(sink.write(outcome))
    } catch (evidenceFailure: Throwable) {
        if (primaryFailure != null) {
            primaryFailure.addSuppressed(evidenceFailure)
        } else {
            throw evidenceFailure
        }
    }
}

/**
 * Executes scenario [test] and guarantees one subsequent [cleanup] attempt.
 *
 * The supplied cleanup operation owns its cancellation policy. `MockInstaller.removeFor` is
 * cancellation-safe.
 *
 * If only cleanup fails, that failure is propagated. If both blocks fail, the test failure
 * stays primary and the cleanup failure is attached to it as a suppressed exception so the
 * business failure is not hidden by teardown.
 */
internal suspend fun runWithCleanup(
    test: suspend () -> Unit,
    cleanup: suspend () -> Unit,
) {
    var testFailure: Throwable? = null
    try {
        test()
    } catch (failure: Throwable) {
        testFailure = failure
        throw failure
    } finally {
        try {
            cleanup()
        } catch (cleanupFailure: Throwable) {
            if (testFailure != null) {
                testFailure.addSuppressed(cleanupFailure)
            } else {
                throw cleanupFailure
            }
        }
    }
}
