package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CITY_TAX_RATE_INFO_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_AMOUNTS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.cityTaxRateInfoFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.stubs.opera.custom.reservationAmountsFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.SelectedPackage
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// Deterministic stay used by every scenario: a two-night stay on one flat nightly rate, so the
// previewed folio charges one deposit line per night and the lines total the whole stay.
private const val PREVIEW_STAY_NIGHTS = 2L
private const val PREVIEW_NIGHTLY_RATE = 59.0
private const val PREVIEW_DEPOSIT_TOTAL = PREVIEW_NIGHTLY_RATE * PREVIEW_STAY_NIGHTS

/**
 * Proves `GET /v1/reservations/preview-deposits`: hotel-reservation-entity-service passes the
 * hotel and reservation ids straight through ohip-adapter-service, which reads each Opera
 * reservation, prices it from the reservation-scoped amounts summary (plus the per-date city-tax
 * detail when the reservation carries a CITYTAX package) and returns one read-only deposit folio
 * per reservation, an empty list when Opera holds no reservation, and the downstream OHIP error
 * code as a 500 when any Opera read is rejected.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/GetPreviewDeposits.md
 */
class GetPreviewDepositsSpec :
    JourneySpec(
        "Hotel reservation previews the deposit folios for reservations",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("single reservation preview returns one deposit folio") {
                val booking = previewDepositsBooking(reservationIds = listOf("6109201"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    hotelReservationApi.getPreviewDeposits(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Preview Deposits Single Reservation")

                expect("returns one deposit folio charging every night of the stay") {
                    result.response.status.value shouldBe 200
                    val folio = result.body.depositFolios.single()
                    folio.reservationId shouldBe reservationId
                    folio.hotelId shouldBe booking.hotel.hotelId
                    folio.vatRegion shouldBe booking.hotel.vatRegion
                    folio.charges.size shouldBe PREVIEW_STAY_NIGHTS.toInt()
                    folio.charges.sumOf { charge ->
                        charge.currencyAmount?.amount ?: 0.0
                    } shouldBe PREVIEW_DEPOSIT_TOTAL
                }

                expect("reads the reservation then its amounts summary") {
                    // Two Opera calls: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}
                    // and GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo?id=..&summaryInfo=true.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("two reservation ids produce two deposit folios") {
                val booking = previewDepositsBooking(reservationIds = listOf("6109202", "6109203"))
                val reservationIds = booking.rooms.map { room -> requireNotNull(room.reservationId) }

                installFor(booking)

                val result =
                    hotelReservationApi.getPreviewDeposits(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = reservationIds,
                        testId = testId,
                    )

                result.attachEvidence("Preview Deposits Two Reservations")

                expect("returns one deposit folio per requested reservation") {
                    result.response.status.value shouldBe 200
                    result.body.depositFolios.size shouldBe 2
                    result.body.depositFolios.map { folio ->
                        folio.reservationId
                    } shouldContainExactlyInAnyOrder reservationIds
                }

                expect("reads each reservation and each amounts summary") {
                    // Four Opera calls: one reservation read and one rateInfo summary per id.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a CITYTAX reservation reads the per-date city-tax rate info") {
                val booking =
                    previewDepositsBooking(
                        reservationIds = listOf("6109204"),
                        selectedPackages = listOf(SelectedPackage(code = "CITYTAX")),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    hotelReservationApi.getPreviewDeposits(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Preview Deposits City Tax")

                expect("returns one deposit folio for the city-tax reservation") {
                    result.response.status.value shouldBe 200
                    val folio = result.body.depositFolios.single()
                    folio.reservationId shouldBe reservationId
                    folio.vatRegion shouldBe booking.hotel.vatRegion
                }

                expect("reads the reservation, its amounts summary and the city-tax detail") {
                    // Three Opera calls: the reservation read plus two rateInfo reads on
                    // /rsv/v1/hotels/{hotelId}/reservations/rateInfo — the summaryInfo=true
                    // amounts read and the summaryInfo=false detailDate city-tax read.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an empty Opera reservation returns an empty deposit folio list without pricing calls") {
                val booking = previewDepositsBooking(reservationIds = listOf("6109205"))
                val reservationId = requireNotNull(booking.room.reservationId)

                // Opera holding nothing for the id is exceptional world state, not a Booking
                // fact, so the generic reservation read is excluded and answered by the custom
                // empty-body stub carrying its own id.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    hotelReservationApi.getPreviewDeposits(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Preview Deposits Empty Reservation")

                expect("returns an empty deposit folio list") {
                    result.response.status.value shouldBe 200
                    result.body.depositFolios.shouldBeEmpty()
                }

                expect("stops after the reservation read without pricing the stay") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    // No rateInfo read is made because there is nothing to price.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected Opera reservation read maps to the get-reservation error") {
                val booking = previewDepositsBooking(reservationIds = listOf("6109206"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    hotelReservationApi.getPreviewDeposits(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Preview Deposits Reservation Read Rejected")

                expect("returns the OHIP get-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("stops after the rejected reservation read") {
                    // One Opera call: the rejected
                    // GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected reservation-amounts read maps to the reservation-amounts error") {
                val booking = previewDepositsBooking(reservationIds = listOf("6109207"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_AMOUNTS_STUB_ID))
                installStub(reservationAmountsFailure(booking))

                val result =
                    hotelReservationApi.getPreviewDeposits(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Preview Deposits Amounts Rejected")

                expect("returns the OHIP reservation-amounts error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 946
                }

                expect("stops after the rejected amounts summary") {
                    // Two Opera calls: the reservation read and the rejected
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo summary.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected city-tax rate-info read maps to the rate-info error") {
                val booking =
                    previewDepositsBooking(
                        reservationIds = listOf("6109208"),
                        selectedPackages = listOf(SelectedPackage(code = "CITYTAX")),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_CITY_TAX_RATE_INFO_STUB_ID))
                installStub(cityTaxRateInfoFailure(booking))

                val result =
                    hotelReservationApi.getPreviewDeposits(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Preview Deposits City Tax Rejected")

                expect("returns the OHIP rate-info error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 900
                }

                expect("stops after the rejected city-tax detail read") {
                    // Three Opera calls: the reservation read, the accepted amounts summary and
                    // the rejected summaryInfo=false detailDate city-tax rateInfo read.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun previewDepositsBooking(
    reservationIds: List<String>,
    selectedPackages: List<SelectedPackage> = emptyList(),
): Booking {
    val arrival = LocalDate.now().plusDays(24)
    val rate =
        Rate(
            ratePlan = "SEMIFLEX",
            roomType = "LOWDBL",
            adults = 2,
            nightlyRate = PREVIEW_NIGHTLY_RATE,
        )

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(PREVIEW_STAY_NIGHTS),
        rooms =
            reservationIds.map { reservationId ->
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    selectedPackages = selectedPackages,
                )
            },
    )
}
