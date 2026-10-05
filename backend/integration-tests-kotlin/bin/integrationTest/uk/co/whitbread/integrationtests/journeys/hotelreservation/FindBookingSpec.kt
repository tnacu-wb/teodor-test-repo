package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import uk.co.whitbread.integrationtests.clients.basket.BasketApi
import uk.co.whitbread.integrationtests.clients.basket.model.ChangeBasketStatusRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationBookingChannel
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRateRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.FindBookingRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.externalReservationSearchUnavailable
import uk.co.whitbread.integrationtests.stubs.opera.getReservation
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.HotelReservationFeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Aem
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderData
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderFavicon
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderSeo
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate
import java.util.concurrent.ThreadLocalRandom

private val createReservationFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
        HotelReservationFeatureFlag.CITY_TAX_UK to false,
        HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to false,
    )

private val findBookingFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.PI_SEARCH_BY_OPERA_CONFIRMATION to true,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false,
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
    )

private val migratedFindBookingFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.PI_SEARCH_BY_OPERA_CONFIRMATION to false,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false,
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
    )

private val otaFindBookingFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.PI_SEARCH_BY_OPERA_CONFIRMATION to false,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to true,
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
    )

private val migratedPreRegisteredFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.PI_SEARCH_BY_OPERA_CONFIRMATION to false,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false,
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to true,
    )

private val otaPreRegisteredFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.PI_SEARCH_BY_OPERA_CONFIRMATION to false,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to true,
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to true,
    )

private val confirmationPreRegisteredFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.PI_SEARCH_BY_OPERA_CONFIRMATION to true,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false,
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to true,
    )

private val confirmationOtaFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.PI_SEARCH_BY_OPERA_CONFIRMATION to true,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to true,
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
    )

private val confirmationOtaPreRegisteredFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.PI_SEARCH_BY_OPERA_CONFIRMATION to true,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to true,
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to true,
    )

class FindBookingSpec :
    JourneySpec(
        "hotel reservations can be found or imported for manage booking",
        {
            val hotelReservationApi = HotelReservationApi()
            val basketApi = BasketApi()

            scenario("a completed basket returns its matching Opera reservation") {
                val booking = findableBooking()
                val room = booking.room

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation To Find")

                expect("creates the basket and reservation used by the lookup") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                }

                val basket =
                    basketApi.getBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                    )

                basket.attachEvidence("Read Basket To Find")

                val bookingReference = basket.body.bookingReference.shouldNotBeNull()
                bookingReference.shouldNotBeBlank()

                val completedBasket =
                    basketApi.changeStatus(
                        bookingReference = bookingReference,
                        request = ChangeBasketStatusRequest(status = "COMPLETED"),
                        testId = testId,
                    )

                completedBasket.attachEvidence("Complete Basket To Find")

                expect("makes the basket eligible for manage-booking lookup") {
                    completedBasket.response.status.value shouldBe 200
                    completedBasket.body.status shouldBe "COMPLETED"
                }

                val result =
                    hotelReservationApi.findBooking(
                        request =
                            FindBookingRequest(
                                resNo = bookingReference,
                                arrivalDate = requireNotNull(booking.arrival).toString(),
                                lastName = requireNotNull(room.guestProfile).lastName,
                                channel = "DISTR",
                                subchannel = "WEB",
                            ),
                        testId = testId,
                        featureFlagOverrides = findBookingFlagPins,
                    )

                result.attachEvidence("Find Completed Booking")

                expect("returns the matching basket and hotel references") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.ref shouldBe bookingReference
                    result.body.basketReference shouldBe createdReservation.body.basketReference
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.token
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                }

                expect("reads the complete reservation and its content redirect configuration") {
                    // Nine Opera calls: setup create uses rate-info, reservation POST,
                    // reservation PUT, and deposit-folio POST. Find uses reservation GET,
                    // amounts GET, folios GET, profile GET, and hotel-config GET.
                    // Content entity makes one AEM call.
                    callCount(Upstream.OPERA) shouldBe 9
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a completed basket is cancelled when Opera reports every reservation cancelled") {
                val booking = cancellableFindableBooking()
                val room = booking.room

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation To Cancel")

                expect("creates the basket and reservation used by the lookup") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                }

                val basket =
                    basketApi.getBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                    )

                basket.attachEvidence("Read Basket To Cancel")

                val bookingReference = basket.body.bookingReference.shouldNotBeNull()
                bookingReference.shouldNotBeBlank()

                val completedBasket =
                    basketApi.changeStatus(
                        bookingReference = bookingReference,
                        request = ChangeBasketStatusRequest(status = "COMPLETED"),
                        testId = testId,
                    )

                completedBasket.attachEvidence("Complete Basket Before Opera Cancellation")

                expect("makes the basket eligible for manage-booking lookup") {
                    completedBasket.response.status.value shouldBe 200
                    completedBasket.body.status shouldBe "COMPLETED"
                }

                // Exceptional temporal override: creation must see RESERVED, while the later
                // lookup must see that Opera has since changed the reservation to CANCELLED.
                installStub(
                    getReservation(
                        booking = booking,
                        room = room.copy(status = ReservationStatus.CANCELLED),
                    ),
                )

                val result =
                    hotelReservationApi.findBooking(
                        request =
                            FindBookingRequest(
                                resNo = bookingReference,
                                arrivalDate = requireNotNull(booking.arrival).toString(),
                                lastName = requireNotNull(room.guestProfile).lastName,
                                channel = "DISTR",
                                subchannel = "WEB",
                            ),
                        testId = testId,
                        featureFlagOverrides = findBookingFlagPins,
                    )

                result.attachEvidence("Find Booking Cancelled In Opera")

                val cancelledBasket =
                    basketApi.getBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                    )

                cancelledBasket.attachEvidence("Read Basket Cancelled From Opera")

                expect("returns the existing booking and synchronizes its basket to cancelled") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.ref shouldBe bookingReference
                    result.body.basketReference shouldBe createdReservation.body.basketReference
                    result.body.hotelId shouldBe booking.hotel.hotelId

                    cancelledBasket.response.status.value shouldBe 200
                    cancelledBasket.body.reference shouldBe createdReservation.body.basketReference
                    cancelledBasket.body.bookingReference shouldBe bookingReference
                    cancelledBasket.body.status shouldBe "CANCELLED"
                    cancelledBasket.body.items
                        .shouldNotBeNull()
                        .single()
                        .sourceId shouldBe room.reservationId
                }

                expect("reads the complete cancelled reservation and its content configuration") {
                    // Nine Opera calls: setup create uses rate-info, reservation POST,
                    // reservation PUT, and deposit-folio POST. Find uses reservation GET,
                    // amounts GET, folios GET, profile GET, and hotel-config GET.
                    // Content entity makes one AEM call.
                    callCount(Upstream.OPERA) shouldBe 9
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a migrated Opera reservation is imported into a manageable basket") {
                val booking = migratedBooking()
                val room = booking.room

                installFor(booking)

                val result =
                    hotelReservationApi.findBooking(
                        request =
                            FindBookingRequest(
                                resNo = requireNotNull(booking.bookingReference),
                                arrivalDate = requireNotNull(booking.arrival).toString(),
                                lastName = requireNotNull(room.guestProfile).lastName,
                                channel = "PI",
                                subchannel = "WEB",
                            ),
                        testId = testId,
                        featureFlagOverrides = migratedFindBookingFlagPins,
                    )

                result.attachEvidence("Find Migrated Booking")

                expect("imports the Opera reservation and returns its new basket") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.ref
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                    result.body.basketReference
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                    result.body.token
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                }

                expect("reads and enriches the external reservation without confirmation fallback") {
                    // Four Opera calls: external-reference search, ReservationContact profile,
                    // reservation amounts, and hotel configuration. Nothing is paid, so the import
                    // resolves the reservation as PAY_ON_ARRIVAL and never reads its deposits.
                    // Content entity makes one AEM call. Confirmation search is pinned off, so CDH
                    // is not used.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an eligible OTA reservation is imported as a third-party basket") {
                val booking = otaBooking()
                val room = booking.room

                installFor(booking)

                val result =
                    hotelReservationApi.findBooking(
                        request =
                            FindBookingRequest(
                                resNo = requireNotNull(booking.bookingReference),
                                arrivalDate = requireNotNull(booking.arrival).toString(),
                                lastName = requireNotNull(room.guestProfile).lastName,
                                channel = "PI",
                                subchannel = "MOBILE",
                            ),
                        testId = testId,
                        featureFlagOverrides = otaFindBookingFlagPins,
                    )

                result.attachEvidence("Find OTA Booking")

                val basketReference = result.body.basketReference.shouldNotBeNull()
                basketReference.shouldNotBeBlank()
                val importedBasket =
                    basketApi.getBasket(
                        basketReference = basketReference,
                        testId = testId,
                    )

                importedBasket.attachEvidence("Read Imported OTA Basket")

                expect("imports the eligible OTA reservation as a completed third-party basket") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.idContext shouldBe "3rd Party"
                    result.body.ref
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                    result.body.token
                        .shouldNotBeNull()
                        .shouldNotBeBlank()

                    importedBasket.response.status.value shouldBe 200
                    importedBasket.body.status shouldBe "COMPLETED"
                    importedBasket.body.idContext shouldBe "3rd Party"
                    importedBasket.body.hotelId shouldBe booking.hotel.hotelId
                    importedBasket.body.items
                        .shouldNotBeNull()
                        .single()
                        .sourceId shouldBe room.reservationId
                }

                expect("reads and enriches the OTA reservation without confirmation fallback") {
                    // Four Opera calls: external-reference search, ReservationContact profile,
                    // reservation amounts, and hotel configuration. Nothing is paid, so the import
                    // resolves the reservation as PAY_ON_ARRIVAL and never reads its deposits.
                    // Content entity makes one AEM call. Confirmation search is pinned off, so CDH
                    // is not used.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an eligible OTA reservation with a mismatched surname is rejected") {
                val booking = otaBooking()

                installFor(booking)

                val result =
                    hotelReservationApi.findBooking(
                        request =
                            FindBookingRequest(
                                resNo = requireNotNull(booking.bookingReference),
                                arrivalDate = requireNotNull(booking.arrival).toString(),
                                lastName = "Baker",
                                channel = "PI",
                                subchannel = "MOBILE",
                            ),
                        testId = testId,
                        featureFlagOverrides = otaFindBookingFlagPins,
                    )

                result.attachEvidence("Reject OTA Booking With Mismatched Surname")

                expect("returns the third-party details-mismatch error without importing a basket") {
                    result.response.status.value shouldBe 400
                    result.errorBody
                        .shouldNotBeNull()
                        .errCode shouldBe 292
                }

                expect("stops after Opera enrichment without content or confirmation fallback") {
                    // Three Opera calls: external-reference search, ReservationContact profile,
                    // and summary rate-info. The mismatch is detected before basket creation and
                    // content enrichment. Confirmation search is pinned off.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a PAY_NOW migrated reservation preserves its Opera payment reference") {
                val booking = payNowMigratedBooking()
                val room = booking.room
                val paymentReference = requireNotNull(room.depositPaymentReference)

                installFor(booking)

                val result =
                    hotelReservationApi.findBooking(
                        request =
                            FindBookingRequest(
                                resNo = requireNotNull(booking.bookingReference),
                                arrivalDate = requireNotNull(booking.arrival).toString(),
                                lastName = requireNotNull(room.guestProfile).lastName,
                                channel = "PI",
                                subchannel = "WEB",
                            ),
                        testId = testId,
                        featureFlagOverrides = migratedFindBookingFlagPins,
                    )

                result.attachEvidence("Find PAY_NOW Migrated Booking")

                val basketReference = result.body.basketReference.shouldNotBeNull()
                val importedBasket =
                    basketApi.getBasket(
                        basketReference = basketReference,
                        testId = testId,
                    )

                importedBasket.attachEvidence("Read PAY_NOW Imported Basket")

                expect("imports the reservation with its Opera payment reference") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.hotelId shouldBe booking.hotel.hotelId

                    importedBasket.response.status.value shouldBe 200
                    importedBasket.body.reference shouldBe basketReference
                    importedBasket.body.bookingReference shouldBe result.body.ref
                    importedBasket.body.status shouldBe "COMPLETED"
                    importedBasket.body.paymentOption shouldBe "PAY_NOW"
                    importedBasket.body.paymentID shouldBe paymentReference
                    importedBasket.body.items
                        .shouldNotBeNull()
                        .single()
                        .sourceId shouldBe room.reservationId
                }

                expect("reads the payment reference without restoring generated deposit folios") {
                    // Five Opera calls: external-reference search, ReservationContact profile,
                    // initial reservation rate-info, reservation deposits, and amend-summary
                    // rate-info. The remaining guest-pay amount prevents generated-folio
                    // restoration. Content entity makes one AEM call.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a cancelled migrated Opera reservation is imported as a cancelled basket") {
                val booking = cancelledMigratedBooking()
                val room = booking.room

                installFor(booking)

                val result =
                    hotelReservationApi.findBooking(
                        request =
                            FindBookingRequest(
                                resNo = requireNotNull(booking.bookingReference),
                                arrivalDate = requireNotNull(booking.arrival).toString(),
                                lastName = requireNotNull(room.guestProfile).lastName,
                                channel = "PI",
                                subchannel = "WEB",
                            ),
                        testId = testId,
                        featureFlagOverrides = migratedFindBookingFlagPins,
                    )

                result.attachEvidence("Find Cancelled Migrated Booking")

                val basketReference = result.body.basketReference.shouldNotBeNull()
                val importedBasket =
                    basketApi.getBasket(
                        basketReference = basketReference,
                        testId = testId,
                    )

                importedBasket.attachEvidence("Read Cancelled Imported Basket")

                expect("imports the reservation into a cancelled basket") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.ref
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                    result.body.token
                        .shouldNotBeNull()
                        .shouldNotBeBlank()

                    importedBasket.response.status.value shouldBe 200
                    importedBasket.body.reference shouldBe basketReference
                    importedBasket.body.hotelId shouldBe booking.hotel.hotelId
                    importedBasket.body.status shouldBe "CANCELLED"
                    importedBasket.body.items
                        .shouldNotBeNull()
                        .single()
                        .sourceId shouldBe room.reservationId
                }

                expect("reads the cancelled reservation without payment or confirmation fallback") {
                    // Four Opera calls: external-reference search, ReservationContact profile,
                    // reservation amounts, and hotel configuration. Nothing is paid, so the import
                    // resolves the reservation as PAY_ON_ARRIVAL and never reads its deposits.
                    // Content entity makes one AEM call. Confirmation search is pinned off, so CDH
                    // is not used.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a migrated reservation is imported when pre-registration repurpose is enabled") {
                val booking = migratedPreRegisteredBooking()
                val room = booking.room

                installFor(booking)

                val result =
                    hotelReservationApi.findBooking(
                        request =
                            FindBookingRequest(
                                resNo = requireNotNull(booking.bookingReference),
                                arrivalDate = requireNotNull(booking.arrival).toString(),
                                lastName = requireNotNull(room.guestProfile).lastName,
                                channel = "PI",
                                subchannel = "WEB",
                            ),
                        testId = testId,
                        featureFlagOverrides = migratedPreRegisteredFlagPins,
                    )

                result.attachEvidence("Find Migrated Booking With Pre-Registration Repurpose")

                val basketReference = result.body.basketReference.shouldNotBeNull()
                val importedBasket = basketApi.getBasket(basketReference, testId)
                importedBasket.attachEvidence("Read Pre-Registered Migrated Basket")

                expect("imports the migrated reservation into a completed basket") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.token
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                    importedBasket.response.status.value shouldBe 200
                    importedBasket.body.status shouldBe "COMPLETED"
                    importedBasket.body.items
                        .shouldNotBeNull()
                        .single()
                        .sourceId shouldBe room.reservationId
                }

                expect("uses the external-reference path with the complete flag row pinned") {
                    // Four Opera calls: external-reference search, profile, summary rate-info and
                    // hotel config. Nothing is paid, so the import resolves the reservation as
                    // PAY_ON_ARRIVAL and never reads its deposits. Content makes one AEM call; CDH
                    // is not used.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an OTA reservation is imported when pre-registration repurpose is enabled") {
                val booking = otaPreRegisteredBooking()
                val room = booking.room

                installFor(booking)

                val result =
                    hotelReservationApi.findBooking(
                        request =
                            FindBookingRequest(
                                resNo = requireNotNull(booking.bookingReference),
                                arrivalDate = requireNotNull(booking.arrival).toString(),
                                lastName = requireNotNull(room.guestProfile).lastName,
                                channel = "PI",
                                subchannel = "MOBILE",
                            ),
                        testId = testId,
                        featureFlagOverrides = otaPreRegisteredFlagPins,
                    )

                result.attachEvidence("Find OTA Booking With Pre-Registration Repurpose")

                val basketReference = result.body.basketReference.shouldNotBeNull()
                val importedBasket = basketApi.getBasket(basketReference, testId)
                importedBasket.attachEvidence("Read Pre-Registered OTA Basket")

                expect("imports the OTA reservation as a completed third-party basket") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.idContext shouldBe "3rd Party"
                    result.body.token
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                    importedBasket.response.status.value shouldBe 200
                    importedBasket.body.status shouldBe "COMPLETED"
                    importedBasket.body.idContext shouldBe "3rd Party"
                    importedBasket.body.items
                        .shouldNotBeNull()
                        .single()
                        .sourceId shouldBe room.reservationId
                }

                expect("uses the OTA external-reference path with the complete flag row pinned") {
                    // Four Opera calls: external-reference search, profile, summary rate-info and
                    // hotel config. Nothing is paid, so the import resolves the reservation as
                    // PAY_ON_ARRIVAL and never reads its deposits. Content makes one AEM call; CDH
                    // is not used.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera confirmation number found through CDH is imported into a manageable basket") {
                val booking = cdhConfirmationBooking()
                val room = booking.room

                installFor(booking)

                val result =
                    hotelReservationApi.findBooking(
                        request =
                            FindBookingRequest(
                                resNo = requireNotNull(booking.cdhBookingReference),
                                arrivalDate = requireNotNull(booking.arrival).toString(),
                                lastName = requireNotNull(room.guestProfile).lastName,
                                channel = "PI",
                                subchannel = "WEB",
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmationPreRegisteredFlagPins,
                    )

                result.attachEvidence("Find Opera Confirmation Through CDH")

                val basketReference = result.body.basketReference.shouldNotBeNull()
                val importedBasket = basketApi.getBasket(basketReference, testId)
                importedBasket.attachEvidence("Read CDH Imported Basket")

                expect("imports the CDH reservation into a completed manageable basket") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.token
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                    importedBasket.response.status.value shouldBe 200
                    importedBasket.body.status shouldBe "COMPLETED"
                    importedBasket.body.items
                        .shouldNotBeNull()
                        .single()
                        .sourceId shouldBe room.reservationId
                }

                expect("falls back through CDH and links the resulting Opera reservation") {
                    // Eight Opera calls: failed external search, reservation, amounts, folios,
                    // deposits, profile, hotel config, and external-reference update. CDH and AEM
                    // each receive one call; Worldline is not used.
                    callCount(Upstream.OPERA) shouldBe 8
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 1
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an OTA confirmation found through CDH is imported as a third-party basket") {
                val booking = cdhOtaBooking(preRegistered = false)
                val room = booking.room

                installFor(booking)

                val result =
                    hotelReservationApi.findBooking(
                        request =
                            FindBookingRequest(
                                resNo = requireNotNull(booking.cdhBookingReference),
                                arrivalDate = requireNotNull(booking.arrival).toString(),
                                lastName = requireNotNull(room.guestProfile).lastName,
                                channel = "PI",
                                subchannel = "MOBILE",
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmationOtaFlagPins,
                    )

                result.attachEvidence("Find OTA Confirmation Through CDH")

                val basketReference = result.body.basketReference.shouldNotBeNull()
                val importedBasket = basketApi.getBasket(basketReference, testId)
                importedBasket.attachEvidence("Read CDH OTA Basket")

                expect("imports the CDH result as a completed third-party basket") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.idContext shouldBe "3rd Party"
                    result.body.token
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                    importedBasket.response.status.value shouldBe 200
                    importedBasket.body.status shouldBe "COMPLETED"
                    importedBasket.body.idContext shouldBe "3rd Party"
                    importedBasket.body.items
                        .shouldNotBeNull()
                        .single()
                        .sourceId shouldBe room.reservationId
                }

                expect("uses the OTA-enabled CDH confirmation path") {
                    // Eight Opera calls: failed external search, reservation, amounts, folios,
                    // deposits, profile, hotel config, and external-reference update. CDH and AEM
                    // each receive one call; Worldline is not used.
                    callCount(Upstream.OPERA) shouldBe 8
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 1
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an OTA confirmation found through CDH remains importable with pre-registration repurpose enabled") {
                val booking = cdhOtaBooking(preRegistered = true)
                val room = booking.room

                installFor(booking)

                val result =
                    hotelReservationApi.findBooking(
                        request =
                            FindBookingRequest(
                                resNo = requireNotNull(booking.cdhBookingReference),
                                arrivalDate = requireNotNull(booking.arrival).toString(),
                                lastName = requireNotNull(room.guestProfile).lastName,
                                channel = "PI",
                                subchannel = "MOBILE",
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmationOtaPreRegisteredFlagPins,
                    )

                result.attachEvidence("Find OTA Confirmation Through CDH With Pre-Registration Repurpose")

                val basketReference = result.body.basketReference.shouldNotBeNull()
                val importedBasket = basketApi.getBasket(basketReference, testId)
                importedBasket.attachEvidence("Read Pre-Registered CDH OTA Basket")

                expect("imports the CDH result as a completed third-party basket") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.idContext shouldBe "3rd Party"
                    result.body.token
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                    importedBasket.response.status.value shouldBe 200
                    importedBasket.body.status shouldBe "COMPLETED"
                    importedBasket.body.idContext shouldBe "3rd Party"
                    importedBasket.body.items
                        .shouldNotBeNull()
                        .single()
                        .sourceId shouldBe room.reservationId
                }

                expect("uses the fully enabled CDH confirmation path") {
                    // Eight Opera calls: failed external search, reservation, amounts, folios,
                    // deposits, profile, hotel config, and external-reference update. CDH and AEM
                    // each receive one call; Worldline is not used.
                    callCount(Upstream.OPERA) shouldBe 8
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 1
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an OTA reservation is rejected when third-party imports are disabled") {
                val booking = ineligibleOtaBooking()
                val room = booking.room

                installFor(booking)

                val result =
                    hotelReservationApi.findBooking(
                        request =
                            FindBookingRequest(
                                resNo = requireNotNull(booking.bookingReference),
                                arrivalDate = requireNotNull(booking.arrival).toString(),
                                lastName = requireNotNull(room.guestProfile).lastName,
                                channel = "PI",
                                subchannel = "MOBILE",
                            ),
                        testId = testId,
                        featureFlagOverrides = migratedFindBookingFlagPins,
                    )

                result.attachEvidence("Reject OTA Booking When Third-Party Imports Are Disabled")

                expect("returns the third-party-not-allowed error without importing a basket") {
                    result.response.status.value shouldBe 400
                    result.errorBody
                        .shouldNotBeNull()
                        .errCode shouldBe 291
                }

                expect("stops after external-reservation enrichment") {
                    // Three Opera calls: external-reference search, ReservationContact profile,
                    // and summary rate-info. OTA rejection happens before basket creation and
                    // content lookup; confirmation fallback is pinned off.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera external-reference search failure is returned through OHIP") {
                val booking = migratedBooking()
                val room = booking.room

                // A downstream failure cannot coexist with the generic successful Opera search.
                installFor(booking, excluded = setOf(OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID))
                installStub(externalReservationSearchUnavailable(booking, booking.rooms))

                val result =
                    hotelReservationApi.findBooking(
                        request =
                            FindBookingRequest(
                                resNo = requireNotNull(booking.bookingReference),
                                arrivalDate = requireNotNull(booking.arrival).toString(),
                                lastName = requireNotNull(room.guestProfile).lastName,
                                channel = "PI",
                                subchannel = "WEB",
                            ),
                        testId = testId,
                        featureFlagOverrides = migratedFindBookingFlagPins,
                    )

                result.attachEvidence("Find Booking Opera External Search Failure")

                expect("returns the mapped external-reference failure") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 959
                    result.errorBody?.globalErrTextTemplate shouldBe "internal.server.exception"
                }

                expect("stops after the failed external-reference search") {
                    // One Opera call through OHIP: the external-reference search fails before enrichment.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun findableBooking(): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "6004201",
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    guestProfile =
                        GuestProfile(
                            profileId = "PROF-6421",
                            firstName = "Riley",
                            lastName = "Morgan",
                            email = "riley.morgan.find@test.com",
                            phone = "+447700900021",
                            addressLine = "21 High Street",
                            city = "London",
                            postcode = "SW1A 1AA",
                        ),
                ),
            ),
        aem =
            Aem(
                indexHeaderData =
                    AemIndexHeaderData(
                        country = "gb",
                        language = "en",
                        seo =
                            AemIndexHeaderSeo(
                                pageTitle = "Manage your booking",
                                pageDescription = "Find and manage an existing booking.",
                                cardImageUrl = "/content/dam/pi/manage-booking.jpg",
                            ),
                        favicon =
                            AemIndexHeaderFavicon(
                                faviconUrl = "/content/dam/pi/favicon.ico",
                            ),
                    ),
            ),
    )
}

private fun cancellableFindableBooking(): Booking {
    val booking = findableBooking()
    val room = booking.room
    val guest = requireNotNull(room.guestProfile)

    return booking.copy(
        rooms =
            listOf(
                room.copy(
                    reservationId = "6004206",
                    guestProfile =
                        guest.copy(
                            profileId = "PROF-6426",
                            email = "riley.morgan.cancel-sync@test.com",
                        ),
                ),
            ),
    )
}

private fun migratedBooking(): Booking {
    val arrival = LocalDate.now().plusDays(21)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "6004202",
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    sourceCode = "44",
                    guestProfile =
                        GuestProfile(
                            profileId = "PROF-6422",
                            firstName = "Jamie",
                            lastName = "Taylor",
                            email = "jamie.taylor.migrated@test.com",
                            phone = "+447700900022",
                            addressLine = "22 High Street",
                            city = "London",
                            postcode = "SW1A 1AB",
                        ),
                ),
            ),
        aem = findBookingAem(),
        bookingReference = "BART7421",
        bookingReferenceIdContext = "BART_OHIP",
    )
}

private fun otaBooking(): Booking {
    val booking = migratedBooking()
    val room = booking.room
    val guest = requireNotNull(room.guestProfile)

    return booking.copy(
        bookingReference = "OTA7422",
        bookingReferenceIdContext = "BOOKING.COM",
        rooms =
            listOf(
                room.copy(
                    reservationId = "6004203",
                    sourceCode = "35",
                    guestProfile =
                        guest.copy(
                            profileId = "PROF-6423",
                            firstName = "Alex",
                            lastName = "Walker",
                            email = "alex.walker.ota@test.com",
                        ),
                ),
            ),
    )
}

private fun payNowMigratedBooking(): Booking {
    val booking = migratedBooking()
    val room = booking.room
    val guest = requireNotNull(room.guestProfile)

    return booking.copy(
        bookingReference = "BART7423",
        rooms =
            listOf(
                room.copy(
                    reservationId = "6004204",
                    amountAlreadyPaid = 50.0,
                    depositPaymentReference = "PAY-6004204",
                    guestProfile =
                        guest.copy(
                            profileId = "PROF-6424",
                            email = "jamie.taylor.pay-now@test.com",
                        ),
                ),
            ),
    )
}

private fun cancelledMigratedBooking(): Booking {
    val booking = migratedBooking()
    val room = booking.room
    val guest = requireNotNull(room.guestProfile)

    return booking.copy(
        bookingReference = "BART7424",
        rooms =
            listOf(
                room.copy(
                    reservationId = "6004205",
                    status = ReservationStatus.CANCELLED,
                    guestProfile =
                        guest.copy(
                            profileId = "PROF-6425",
                            email = "jamie.taylor.cancelled@test.com",
                        ),
                ),
            ),
    )
}

private fun migratedPreRegisteredBooking(): Booking =
    findBookingVariant(
        bookingReference = "BART7431",
        reservationId = "6004211",
        profileId = "PROF-6431",
        email = "jamie.taylor.pre-registered@test.com",
    )

private fun otaPreRegisteredBooking(): Booking =
    findBookingVariant(
        bookingReference = "OTA7432",
        reservationId = "6004212",
        profileId = "PROF-6432",
        email = "alex.walker.pre-registered-ota@test.com",
        bookingReferenceIdContext = "BOOKING.COM",
        sourceCode = "35",
    )

private fun cdhConfirmationBooking(): Booking {
    val reservationId = uniqueReservationId()
    return findBookingVariant(
        bookingReference = null,
        cdhBookingReference = "0$reservationId",
        reservationId = reservationId,
        profileId = "PROF-$reservationId",
        email = "jamie.taylor.cdh@test.com",
    )
}

private fun cdhOtaBooking(preRegistered: Boolean): Booking {
    val reservationId = uniqueReservationId()
    val variant = if (preRegistered) "pre-registered" else "standard"
    return findBookingVariant(
        bookingReference = "OTA-CDH-$reservationId",
        cdhBookingReference = "0$reservationId",
        reservationId = reservationId,
        profileId = "PROF-$reservationId",
        email = "alex.walker.cdh-$variant@test.com",
        bookingReferenceIdContext = "BOOKING.COM",
        sourceCode = "35",
    )
}

/**
 * CDH imports persist baskets in the shared integration environment. A fresh reservation ID also
 * gives each execution a fresh confirmation number, preventing reruns from finding an existing
 * basket and bypassing the CDH fallback path that these scenarios are intended to exercise.
 */
private fun uniqueReservationId(): String =
    ThreadLocalRandom
        .current()
        .nextInt(7_000_000, 10_000_000)
        .toString()

private fun ineligibleOtaBooking(): Booking =
    findBookingVariant(
        bookingReference = "OTA7436",
        reservationId = "6004216",
        profileId = "PROF-6436",
        email = "alex.walker.ineligible-ota@test.com",
        bookingReferenceIdContext = "BOOKING.COM",
        sourceCode = "35",
    )

private fun findBookingVariant(
    bookingReference: String?,
    reservationId: String,
    profileId: String,
    email: String,
    cdhBookingReference: String? = null,
    bookingReferenceIdContext: String = "BART_OHIP",
    sourceCode: String = "44",
): Booking {
    val booking = migratedBooking()
    val room = booking.room
    val guest = requireNotNull(room.guestProfile)

    return booking.copy(
        bookingReference = bookingReference,
        cdhBookingReference = cdhBookingReference,
        bookingReferenceIdContext = bookingReferenceIdContext,
        rooms =
            listOf(
                room.copy(
                    reservationId = reservationId,
                    sourceCode = sourceCode,
                    guestProfile =
                        guest.copy(
                            profileId = profileId,
                            email = email,
                        ),
                ),
            ),
    )
}

private fun findBookingAem(): Aem =
    Aem(
        indexHeaderData =
            AemIndexHeaderData(
                country = "gb",
                language = "en",
                seo =
                    AemIndexHeaderSeo(
                        pageTitle = "Manage your booking",
                        pageDescription = "Find and manage an existing booking.",
                        cardImageUrl = "/content/dam/pi/manage-booking.jpg",
                    ),
                favicon =
                    AemIndexHeaderFavicon(
                        faviconUrl = "/content/dam/pi/favicon.ico",
                    ),
            ),
    )

private fun createReservationRequest(booking: Booking): CreateReservationRequest {
    val room = booking.room
    val rate = booking.hotel.availableRates.single()
    val arrival = requireNotNull(booking.arrival).toString()
    val departure = requireNotNull(booking.departure).toString()

    return CreateReservationRequest(
        reservations =
            listOf(
                CreateReservationRoomRequest(
                    hotelId = booking.hotel.hotelId,
                    arrival = arrival,
                    departure = departure,
                    adultsNumber = requireNotNull(room.adults),
                    childrenNumber = room.children,
                    roomRates =
                        CreateReservationRoomRateRequest(
                            ratePlanCode = rate.ratePlan,
                            pmsRoomType = requireNotNull(room.roomType),
                            startDate = arrival,
                            endDate = departure,
                        ),
                ),
            ),
        bookingChannel =
            CreateReservationBookingChannel(
                channel = "PI",
                subchannel = "WEB",
                language = "EN",
            ),
        bookingFlowId = "find-booking",
    )
}
