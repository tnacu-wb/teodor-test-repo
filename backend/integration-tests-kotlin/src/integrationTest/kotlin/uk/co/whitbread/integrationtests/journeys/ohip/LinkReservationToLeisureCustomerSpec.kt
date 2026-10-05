package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.LinkReservationToLeisureCustomerRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.CharacterUdf
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// The flow lists only the two Opera transport-auth flags; both are environment-pinned OFF and
// evaluated outside request scope, so every scenario states them at that fixed value.
private val linkLeisureCustomerFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

private const val CUSTOMER_ACCOUNT_UDF_NAME = "UDFC35"
private const val BOOKING_CHANNEL_UDF_NAME = "UDFC09"
private const val BOOKING_CHANNEL_VALUE = "PI"

/**
 * Proves `PUT /ohip/v1/reservations/link-leisure-customer` at the adapter boundary: every
 * distinct requested reservation of one hotel receives the same CDH leisure customer account in
 * Opera character UDF `UDFC35` alongside the fixed `PI` booking-channel marker in `UDFC09`, sent
 * as one body mapped before the fan-out whose reservation identity comes only from the Opera URL,
 * with no reservation read, cache, or rollback anywhere on the path.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/LinkReservationToLeisureCustomer.md
 */
class LinkReservationToLeisureCustomerSpec :
    JourneySpec(
        "OHIP adapter links reservations to a leisure customer account",
        {
            val ohipApi = OhipApi()

            scenario("two distinct reservations are linked to one leisure customer account") {
                val customerAccountId = "CDH-8830041"
                val booking =
                    linkLeisureCustomerBooking(
                        reservationIds = listOf("6183001", "6183002"),
                        customerAccountId = customerAccountId,
                    )
                val reservationIds = booking.rooms.map { requireNotNull(it.reservationId) }

                installFor(booking)

                val result =
                    ohipApi.linkReservationToLeisureCustomer(
                        request =
                            LinkReservationToLeisureCustomerRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = reservationIds,
                                customerAccountId = customerAccountId,
                            ),
                        testId = testId,
                        featureFlagOverrides = linkLeisureCustomerFlagPins,
                    )

                result.attachEvidence("Link Reservations To Leisure Customer")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("links both reservations and reads neither") {
                    // Two PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, serialized at
                    // the deployed maxConcurrency of one and each accepted only by the mapping
                    // pinning the request hotel plus exactly the UDFC35 account id and the
                    // hard-coded UDFC09 = PI marker, with no reservation identity in the body; the
                    // reservation GET default is installed by the same gate, so the zero proves
                    // there is no read before the write and no rollback after it.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the link maps to the change-reservation error") {
                val booking = linkLeisureCustomerBooking(reservationIds = listOf("6183003"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.linkReservationToLeisureCustomer(
                        request =
                            LinkReservationToLeisureCustomerRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                customerAccountId = "CDH-8830042",
                            ),
                        testId = testId,
                        featureFlagOverrides = linkLeisureCustomerFlagPins,
                    )

                result.attachEvidence("Link Reservations To Leisure Customer Opera Rejection")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("attempts the link once and takes no compensating action") {
                    // One rejected PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}: the
                    // Internal Server Error envelope is not retryable, and the installed
                    // reservation GET default proves there is no read or undo phase after it.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun linkLeisureCustomerBooking(
    reservationIds: List<String>,
    customerAccountId: String? = null,
): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rates =
        listOf(
            Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2),
            Rate(ratePlan = "SEMIFLEX", roomType = "TWINRM", adults = 1),
        ).take(reservationIds.size)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = rates)),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            reservationIds.mapIndexed { index, reservationId ->
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rates[index].roomType,
                    adults = rates[index].adults,
                    status = ReservationStatus.RESERVED,
                    characterUdfsAfterUpdate =
                        customerAccountId?.let {
                            listOf(
                                CharacterUdf(name = CUSTOMER_ACCOUNT_UDF_NAME, value = it),
                                CharacterUdf(name = BOOKING_CHANNEL_UDF_NAME, value = BOOKING_CHANNEL_VALUE),
                            )
                        },
                )
            },
    )
}
