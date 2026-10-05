package uk.co.whitbread.integrationtests.testkit.mocks

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget

/**
 * `Upstream` mirrors `WireMockTarget` because journey specs may not import `framework`, so the
 * author-facing vocabulary has to live in `testkit`. That mirror is the one duplication this design
 * accepts, and this test is what stops it drifting: a fifth WireMock fails the build here until
 * `Upstream` gains its entry, rather than silently becoming unassertable from a journey.
 */
class UpstreamTargetTest :
    FunSpec({
        test("every WireMock target is namable by a journey, exactly once") {
            Upstream.entries.map { it.target } shouldContainExactlyInAnyOrder WireMockTarget.entries
        }
    })
