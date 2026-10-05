package uk.co.whitbread.integrationtests.provisioning

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String,
)

@Serializable
data class MockSessionResponse(
    val testId: String,
    val baggage: String,
)

/** Failure body for every non-2xx provisioning response. The server log carries the cause. */
@Serializable
data class ApiError(
    val message: String,
)
