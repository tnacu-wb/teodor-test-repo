package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.stubs.cdh.CDH_RESERVATION_SEARCH_STUB_ID
import uk.co.whitbread.integrationtests.stubs.cdh.custom.cdhReservationSearchLimitExceeded
import uk.co.whitbread.integrationtests.stubs.cdh.custom.cdhReservationSearchServerError
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booker
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

/**
 * Proves `GET /v1/reservations/search/booking/cdh`: hotel-reservation-entity-service searches the
 * Customer Data Hub through cdh-adapter-service in a single call, reducing an Opera-style
 * reference to its digits before the search and echoing the original back as `operaConfNumber`,
 * collapses the CDH status into a booking status, re-groups a multi-status page and drops results
 * outside their rolling one-year window, cuts the caller's page out of that one downstream search,
 * filters on the result's booker surname rather than its guest, stamps `sourceSystem` `OPERA` on
 * results that carry a hotel code, empties the page when CDH reports more than fifty matches, and
 * surfaces a CDH server error as a 500 — all without touching Opera.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/SearchBookingCdh.md
 */
class SearchBookingCdhSpec :
    JourneySpec(
        "Hotel reservation searches CDH for a booking reference",
        {
            val hotelReservationApi = HotelReservationApi()

            // No flag pins: the only flag on this path, `release_pi_cdh_api_deprecation`, is
            // evaluated inside cdh-adapter-service, which has no baggage override resolver, so
            // `featureFlagOverrides` cannot select the V2 or the V3 CDH route. Every CDH search
            // stub here installs both routes with the same body, leaving each scenario
            // route-agnostic. Logged as a flag-coverage gap in data_model_issues/.
            scenario("a numeric CDH booking reference returns the matching booking stamped sourceSystem OPERA") {
                val booking = cdhSearchBooking(cdhBookingReference = "1391808", reservationId = "6107801")
                val reference = requireNotNull(booking.cdhBookingReference)

                installFor(booking)

                val result =
                    hotelReservationApi.searchBookingFromCdh(
                        bookingReference = reference,
                        pageSize = 10,
                        pageNumber = 1,
                        testId = testId,
                    )

                result.attachEvidence("Search Booking From CDH")

                expect("returns the single matching booking as an upcoming Opera-sourced result") {
                    result.response.status.value shouldBe 200
                    val match = result.body.results.single()
                    match.bookingReference shouldBe reference
                    match.status shouldBe "Upcoming"
                    match.sourceSystem shouldBe "OPERA"
                    result.body.searchResults shouldBe 1
                    result.body.hasMore shouldBe false
                    result.body.responseLimitExceeded shouldBe false
                    result.body.operaConfNumber shouldBe reference
                }

                expect("searches CDH once and leaves Opera untouched") {
                    // One CDH call: POST /BookingServices/V2|V3/ReservationSearch, reached through
                    // cdh-adapter-service POST /v1/cdh/reservation/search.
                    callCount(Upstream.CDH) shouldBe 1
                    // The CDH reference also installs Opera's external-reference search, so the
                    // untouched Opera upstream is proven against an installed mapping.
                    callCount(Upstream.OPERA) shouldBe 0
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera-style reference is stripped to its digits downstream and echoed back") {
                val booking = cdhSearchBooking(cdhBookingReference = "1391809", reservationId = "6107802")
                val strippedReference = requireNotNull(booking.cdhBookingReference)
                val operaStyleReference = "LONEUS$strippedReference"

                installFor(booking)

                val result =
                    hotelReservationApi.searchBookingFromCdh(
                        bookingReference = operaStyleReference,
                        pageSize = 10,
                        pageNumber = 1,
                        testId = testId,
                    )

                result.attachEvidence("Search Booking From CDH Opera Style Reference")

                expect("returns the booking found under the stripped reference and echoes the original") {
                    result.response.status.value shouldBe 200
                    // The CDH stub matches only the post-strip reference, so a successful search
                    // is itself the proof that the six-letter prefix was dropped downstream.
                    result.body.results
                        .single()
                        .bookingReference shouldBe strippedReference
                    result.body.operaConfNumber shouldBe operaStyleReference
                }

                expect("searches CDH once and leaves Opera untouched") {
                    // One CDH call: POST /BookingServices/V2|V3/ReservationSearch.
                    callCount(Upstream.CDH) shouldBe 1
                    callCount(Upstream.OPERA) shouldBe 0
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a multi-status page is re-sorted by status group and stale cancellations are dropped") {
                val booking = cdhSearchPageBooking(cdhBookingReference = "1391812")
                val recentlyCancelledReservationId =
                    requireNotNull(booking.rooms[2].reservationId)

                installFor(booking)

                val result =
                    hotelReservationApi.searchBookingFromCdh(
                        bookingReference = requireNotNull(booking.cdhBookingReference),
                        pageSize = 10,
                        pageNumber = 1,
                        testId = testId,
                    )

                result.attachEvidence("Search Booking From CDH Multi Status Page")

                expect("returns the page ordered Checked-In, Upcoming, Cancelled without the year-old cancellation") {
                    result.response.status.value shouldBe 200
                    // CDH returned the four results in booking-room order: Upcoming, Checked-In,
                    // then the two Cancelled ones. The out-port re-groups them.
                    result.body.results.map { it.status } shouldBe listOf("Checked-In", "Upcoming", "Cancelled")
                    result.body.results
                        .last()
                        .rooms
                        .single()
                        .reservationId shouldBe recentlyCancelledReservationId
                    // `searchResults` counts what CDH matched, `pageResults` what survived the
                    // rolling one-year window, so the gap is the dropped stale cancellation.
                    result.body.searchResults shouldBe 4
                    result.body.pageResults shouldBe 3
                    result.body.hasMore shouldBe false
                }

                expect("searches CDH once and leaves Opera untouched") {
                    // One CDH call: POST /BookingServices/V2|V3/ReservationSearch.
                    callCount(Upstream.CDH) shouldBe 1
                    callCount(Upstream.OPERA) shouldBe 0
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("the caller's second page is cut locally from the same single CDH search") {
                val booking = cdhSearchPageBooking(cdhBookingReference = "1391813")
                val upcomingReservationId = requireNotNull(booking.rooms[0].reservationId)

                installFor(booking)

                val result =
                    hotelReservationApi.searchBookingFromCdh(
                        bookingReference = requireNotNull(booking.cdhBookingReference),
                        pageSize = 1,
                        pageNumber = 2,
                        testId = testId,
                    )

                result.attachEvidence("Search Booking From CDH Second Page")

                expect("returns only the second entry of the re-sorted page and reports more to come") {
                    result.response.status.value shouldBe 200
                    result.body.results
                        .single()
                        .status shouldBe "Upcoming"
                    result.body.results
                        .single()
                        .rooms
                        .single()
                        .reservationId shouldBe upcomingReservationId
                    result.body.pageResults shouldBe 1
                    result.body.searchResults shouldBe 4
                    result.body.hasMore shouldBe true
                }

                expect("still searches CDH exactly once and leaves Opera untouched") {
                    // One CDH call: POST /BookingServices/V2|V3/ReservationSearch. The downstream
                    // page is always size 50 page 1, so the caller's paging costs no extra call.
                    callCount(Upstream.CDH) shouldBe 1
                    callCount(Upstream.OPERA) shouldBe 0
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a booker surname filter reads the booker block and not the room's guest") {
                val booking = cdhBookerBooking(cdhBookingReference = "1391814", reservationId = "6107814")
                val guestSurname = requireNotNull(booking.room.guestProfile).lastName

                installFor(booking)

                val result =
                    hotelReservationApi.searchBookingFromCdh(
                        bookingReference = requireNotNull(booking.cdhBookingReference),
                        pageSize = 10,
                        pageNumber = 1,
                        testId = testId,
                        bookerLastName = guestSurname,
                    )

                result.attachEvidence("Search Booking From CDH Booker Surname Filter")

                expect("drops the result CDH returned because only its guest carries that surname") {
                    result.response.status.value shouldBe 200
                    // CDH matched the reference and returned the booking; the booker surname filter
                    // compares the result's Booker block, which carries a different surname, so
                    // nothing survives. An unfiltered page would return the single result here.
                    result.body.results.shouldBeEmpty()
                    result.body.searchResults shouldBe 0
                    result.body.pageResults shouldBe 0
                    result.body.hasMore shouldBe false
                    // Not the over-fifty short circuit, which empties the page for another reason.
                    result.body.responseLimitExceeded shouldBe false
                }

                expect("searches CDH once and leaves Opera untouched") {
                    // One CDH call: POST /BookingServices/V2|V3/ReservationSearch.
                    callCount(Upstream.CDH) shouldBe 1
                    callCount(Upstream.OPERA) shouldBe 0
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // Documented service bug: `toFilterByBookerOrGuestLastnameModel` dereferences
            // `CdhResults.getBooker()` without a null check, although the mapper above it treats
            // the booker as optional, so a result CDH returns without a booker 500s the whole
            // search instead of failing to match. Asserts the correct behaviour but ships
            // disabled. See bug/cdh-search-booker-lastname-npe.md.
            scenario("!a booker surname search over results CDH returns without a booker empties the page") {
                val booking = cdhSearchBooking(cdhBookingReference = "1391815", reservationId = "6107815")

                installFor(booking)

                val result =
                    hotelReservationApi.searchBookingFromCdh(
                        bookingReference = requireNotNull(booking.cdhBookingReference),
                        pageSize = 10,
                        pageNumber = 1,
                        testId = testId,
                        bookerLastName = "Blackwood",
                    )

                result.attachEvidence("Search Booking From CDH Booker Surname Without Booker")

                expect("returns an empty page rather than failing") {
                    result.response.status.value shouldBe 200
                    result.body.results.shouldBeEmpty()
                    result.body.searchResults shouldBe 0
                    result.body.responseLimitExceeded shouldBe false
                }

                expect("searches CDH once and leaves Opera untouched") {
                    // One CDH call: POST /BookingServices/V2|V3/ReservationSearch.
                    callCount(Upstream.CDH) shouldBe 1
                    callCount(Upstream.OPERA) shouldBe 0
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // Documented service bug: `CdhReservationException`, like `ChangeLogException` one endpoint over,
            // has only a single-argument constructor that hard-codes errCode 0, so the adapter's
            // own code is discarded and no errCode can distinguish this failure. Only the 500 and
            // the single-call shape are asserted. See bug/cdh-search-errcode-swallowed.md.
            scenario("a CDH server error surfaces as HTTP 500") {
                val booking = cdhSearchBooking(cdhBookingReference = "1391810", reservationId = "6107803")

                // A downstream failure is exceptional behavior, not a Booking world state, so the
                // default CDH search is excluded and a custom stub with its own id answers instead.
                installFor(booking, excluded = setOf(CDH_RESERVATION_SEARCH_STUB_ID))
                installStub(cdhReservationSearchServerError(booking))

                val result =
                    hotelReservationApi.searchBookingFromCdh(
                        bookingReference = requireNotNull(booking.cdhBookingReference),
                        pageSize = 10,
                        pageNumber = 1,
                        testId = testId,
                    )

                result.attachEvidence("Search Booking From CDH Server Error")

                expect("returns a CDH search failure") {
                    result.response.status.value shouldBe 500
                }

                expect("made the single CDH search and left Opera untouched") {
                    // One CDH call: POST /BookingServices/V2|V3/ReservationSearch.
                    callCount(Upstream.CDH) shouldBe 1
                    callCount(Upstream.OPERA) shouldBe 0
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("CDH reporting more than fifty total results returns an empty page with responseLimitExceeded") {
                val booking = cdhSearchBooking(cdhBookingReference = "1391811", reservationId = "6107804")

                // The frozen default always reports one result and cannot express an over-limit
                // page, so a custom stub with its own id answers the same search instead.
                installFor(booking, excluded = setOf(CDH_RESERVATION_SEARCH_STUB_ID))
                installStub(cdhReservationSearchLimitExceeded(booking))

                val result =
                    hotelReservationApi.searchBookingFromCdh(
                        bookingReference = requireNotNull(booking.cdhBookingReference),
                        pageSize = 10,
                        pageNumber = 1,
                        testId = testId,
                    )

                result.attachEvidence("Search Booking From CDH Limit Exceeded")

                expect("returns an empty page carrying the CDH total and the limit flag") {
                    result.response.status.value shouldBe 200
                    result.body.responseLimitExceeded shouldBe true
                    result.body.results.shouldBeEmpty()
                    result.body.cdhSearchResults shouldBe 51
                    result.body.searchResults shouldBe 0
                    result.body.pageResults shouldBe 0
                    result.body.hasMore shouldBe false
                }

                expect("made the single CDH search and left Opera untouched") {
                    // One CDH call: POST /BookingServices/V2|V3/ReservationSearch.
                    callCount(Upstream.CDH) shouldBe 1
                    callCount(Upstream.OPERA) shouldBe 0
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

/**
 * The CDH booking reference gates both the CDH reservation-search default and Opera's
 * external-reference search default, so every scenario's untouched-Opera assertion runs against an
 * installed Opera mapping. The reserved room arriving in three weeks makes the result Upcoming and
 * inside the rolling one-year window the out-port filters on.
 */
private fun cdhSearchBooking(
    cdhBookingReference: String,
    reservationId: String,
): Booking {
    val arrival = LocalDate.now().plusDays(21)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        cdhBookingReference = cdhBookingReference,
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                ),
            ),
    )
}

/**
 * A four-room booking whose rooms land in three different status groups, so one CDH page carries
 * results the out-port must re-group and window-filter. CDH returns them in room order — Upcoming,
 * Checked-In, a cancellation from last week, a cancellation from over a year ago — and only the
 * last is outside the rolling one-year window. The shared stay dates keep every result's arrival in
 * the future, so no room can fall into the Past group.
 */
private fun cdhSearchPageBooking(cdhBookingReference: String): Booking {
    val arrival = LocalDate.now().plusDays(21)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    fun room(
        reservationId: String,
        status: ReservationStatus,
        cancellationDate: LocalDate? = null,
    ) = BookingRoom(
        reservationId = reservationId,
        roomType = rate.roomType,
        adults = rate.adults,
        status = status,
        cancellationDate = cancellationDate,
    )

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        cdhBookingReference = cdhBookingReference,
        rooms =
            listOf(
                room("6107810", ReservationStatus.RESERVED),
                room("6107811", ReservationStatus.CHECKED_IN),
                room("6107812", ReservationStatus.CANCELLED, LocalDate.now().minusDays(10)),
                room("6107813", ReservationStatus.CANCELLED, LocalDate.now().minusDays(400)),
            ),
    )
}

/**
 * A booking whose booker identity differs from the room's lead guest, so a booker-surname search
 * can be told apart from a guest-surname one. The room is reserved and arrives in three weeks, so
 * the result is Upcoming and inside the rolling one-year window.
 */
private fun cdhBookerBooking(
    cdhBookingReference: String,
    reservationId: String,
): Booking {
    val arrival = LocalDate.now().plusDays(21)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        cdhBookingReference = cdhBookingReference,
        booker = Booker(firstName = "Marguerite", lastName = "Blackwood"),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    guestProfile =
                        GuestProfile(
                            profileId = "PROF-6107814",
                            firstName = "Grace",
                            lastName = "Hopper",
                            email = "grace.hopper.cdh@test.com",
                            phone = "+447700900031",
                            addressLine = "31 High Street",
                            city = "London",
                            postcode = "SW1A 1AA",
                        ),
                ),
            ),
    )
}
