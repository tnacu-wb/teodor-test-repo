package uk.co.whitbread.integrationtests.testkit.mocks

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class OperaEndpointTest :
    FunSpec({
        test("GET_RESERVATION excludes named reservation subresources") {
            val pattern = Regex(OperaEndpoint.GET_RESERVATION.urlPathPattern)

            pattern.matches("/rsv/v1/hotels/HEAPTI/reservations/6001001") shouldBe true
            pattern.matches("/rsv/v1/hotels/HEAPTI/reservations/rateInfo") shouldBe false
            pattern.matches("/rsv/v1/hotels/HEAPTI/reservations/activityLog") shouldBe false
        }
    })
