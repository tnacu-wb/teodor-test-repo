package uk.co.whitbread.integrationtests.testkit.mocks

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget

/**
 * Mocked upstream system a scenario can make assertions about.
 *
 * This is the author-facing vocabulary for the four WireMock instances. Journey specs are not
 * allowed to import `framework`, so they name an upstream through this enum rather than through
 * [WireMockTarget], which belongs to the transport layer.
 *
 * It describes deployed WireMock instances, not scenarios or mocks: it changes only when the
 * integration environment gains another WireMock. Each constant carries the [WireMockTarget] it
 * resolves to, so there is no conversion logic to keep in step — and `UpstreamTargetTest` fails the
 * build if the two enums ever stop covering the same set.
 */
enum class Upstream(
    internal val target: WireMockTarget,
) {
    OPERA(WireMockTarget.OPERA),
    CDH(WireMockTarget.CDH),
    AEM(WireMockTarget.AEM),
    WORLDLINE(WireMockTarget.WORLDLINE),
}
