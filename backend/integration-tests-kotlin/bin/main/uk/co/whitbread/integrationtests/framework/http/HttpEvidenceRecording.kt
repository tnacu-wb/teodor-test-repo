package uk.co.whitbread.integrationtests.framework.http

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Receives HTTP evidence captured while a scenario coroutine is active.
 *
 * The HTTP framework owns this seam so response decoding can report failures without depending on
 * a concrete artifact or reporting implementation.
 */
internal interface HttpEvidenceRecorder {
    /** Records one captured HTTP exchange and its optional associated failure. */
    fun recordHttp(
        prefix: String?,
        evidence: HttpEvidence,
        failure: Throwable? = null,
    )
}

/** Coroutine context element exposing the active HTTP evidence [recorder]. */
internal class HttpEvidenceContext(
    val recorder: HttpEvidenceRecorder,
) : AbstractCoroutineContextElement(Key) {
    /** Key used by response decoding to find the active recorder. */
    companion object Key : CoroutineContext.Key<HttpEvidenceContext>
}
