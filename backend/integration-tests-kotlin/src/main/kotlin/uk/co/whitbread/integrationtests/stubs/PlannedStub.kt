package uk.co.whitbread.integrationtests.stubs

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping

/** One logical stub, including its identity, WireMock target, and ordered mappings. */
data class PlannedStub(
    val id: String,
    val target: WireMockTarget,
    val mappings: List<StubMapping>,
) {
    init {
        require(mappings.isNotEmpty()) { "Planned stub '$id' must contain at least one mapping" }
    }
}
