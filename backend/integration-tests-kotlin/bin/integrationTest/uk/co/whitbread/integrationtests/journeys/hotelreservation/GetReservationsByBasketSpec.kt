package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationBookingChannel
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRateRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.HotelReservationFeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.OperaPaymentCard
import uk.co.whitbread.integrationtests.testkit.model.PreRegistration
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.SelectedPackage
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// Setup-only pins, copied from ConfirmReservationSpec / CreateReservationGuestSpec: they hold the
// created basket on the plain pay-on-arrival shape this endpoint reads back. The channel is `PI`
// because the deployed rules service answers no channel rule for a `DISTR` creation.
private val createReservationFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
        HotelReservationFeatureFlag.CITY_TAX_UK to false,
        HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to false,
    )

// The two flags the flow doc lists on this path, pinned OFF. `mobile_preRegistered_repurpose`
// is evaluated by hotel-reservation-entity-service and decides whether the adapter's
// `deRegCardCompleted` survives; `mobile_accepts_ota_booking` is evaluated by ohip-adapter-service
// and decides whether a basket whose reservations disagree on their deposit policy code is
// rejected. Each has its own ON twin below.
private val basketReadFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false,
    )

private val preRegisteredRepurposeOnPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to true,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false,
    )

private val acceptsOtaBookingOnPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to true,
    )

/**
 * Proves the canonical basket-scoped read: `GET /v1/reservations/basket/{basketReference}` turns a
 * created OPEN basket's items into Opera reservation ids and answers each reservation enriched
 * from Opera — one reservation read, rate-amount read and folio read per basket item, one hotel
 * configuration, one profile per room, and exactly one credit-card lookup for the whole basket.
 * It also proves that `priceBreakdownNeeded` buys no extra Opera call on a `PI`-channel basket,
 * that `mobile_preRegistered_repurpose` alone decides whether Opera's de-reg-card alert reaches
 * the caller, that `mobile_accepts_ota_booking` alone decides whether a basket with mismatched
 * deposit policy codes is rejected, that a priced `CITYTAX` package makes the read report city
 * tax and buys one extra per-consumption-date rate-info call, and how a lost or rejected Opera
 * reservation read is mapped.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/GetReservationsByBasket.md
 */
class GetReservationsByBasketSpec :
    JourneySpec(
        "a basket's reservations can be read back with their Opera enrichment",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("a two-room OPEN basket is read back with every Opera enrichment and exactly one card lookup") {
                val booking = basketReadBooking(reservationIds = listOf("6012001", "6012002"))

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Two-Room Basket To Read Back")

                expect("creates the OPEN two-room basket the read acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    createdReservation.body.reservations shouldHaveSize 2
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_TWO_ROOM_CREATE
                }

                val result =
                    hotelReservationApi.getReservationsByBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                        featureFlagOverrides = basketReadFlagPins,
                    )

                result.attachEvidence("Get Reservations By Basket")

                expect("returns both of the basket's reservations with its basket context") {
                    result.response.status.value shouldBe 200
                    // Reservation-id ordering is deliberately not asserted: ohip-adapter collects
                    // the created ids into a Set (bug/MultiRoomReservationPackageOrdering.md).
                    result.body.reservationByIdList shouldHaveSize 2
                    result.body.basketReference shouldBe createdReservation.body.basketReference
                    result.body.basketStatus shouldBe "OPEN"
                }

                expect("reads each reservation from Opera but looks the payment card up only once") {
                    // Ten Opera calls beyond the setup create, for the two basket items.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_TWO_ROOM_CREATE + 10
                    // GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, once per item.
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_TWO_ROOM_CREATE + 2
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo?summaryInfo=true, once per
                    // item; the nightly detailDate variant shares this endpoint and is off the path.
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_GET_RATE_INFO_AFTER_TWO_ROOM_CREATE + 2
                    // GET /csh/v1/hotels/{hotelId}/reservations/{reservationId}/folios.
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 2
                    // GET /ent/config/v1/hotels/{hotelId}?fetchInstructions=General, once for the
                    // whole basket however many items it holds.
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_TWO_ROOM_CREATE + 1
                    // GET /crm/v1/profiles/{profileId}, once per distinct profile id.
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 2
                    // GET /fof/config/v1/creditCardInfo — the headline of this scenario: two rooms
                    // each carrying their own Opera card, and `getReservationCardDetails` still
                    // reads only the first one it finds.
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    // A read writes nothing: the deposit and reservation-update mappings installFor
                    // really did install stay at the setup create's cost.
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(OperaEndpoint.GET_RESERVATION_DEPOSITS) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe OPERA_UPDATE_RESERVATION_AFTER_TWO_ROOM_CREATE
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("priceBreakdownNeeded=true adds no Opera call on a PI-channel basket") {
                val booking = basketReadBooking(reservationIds = listOf("6012003", "6012004"))

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket To Read With Price Breakdown")

                expect("creates the OPEN two-room basket the read acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_TWO_ROOM_CREATE
                }

                val result =
                    hotelReservationApi.getReservationsByBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                        priceBreakdownNeeded = true,
                        featureFlagOverrides = basketReadFlagPins,
                    )

                result.attachEvidence("Get Reservations By Basket With Price Breakdown")

                expect("returns the same two reservations as without the parameter") {
                    result.response.status.value shouldBe 200
                    result.body.reservationByIdList shouldHaveSize 2
                    result.body.basketReference shouldBe createdReservation.body.basketReference
                }

                expect("makes byte-for-byte the same Opera calls as the plain read") {
                    // The absence proof: ohip-adapter only runs the nightly rate-info loop once
                    // its rules-agent source-info lookup resolves the channel as Distribution, and
                    // this basket was created on `PI`. GET_RATE_INFO therefore stays at exactly one
                    // reservation-amounts read per item — no per-stay-date detailDate calls. The
                    // rules-agent hop itself is a real deployed service with no WireMock target, so
                    // it cannot be counted here.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_TWO_ROOM_CREATE + 10
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_GET_RATE_INFO_AFTER_TWO_ROOM_CREATE + 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_TWO_ROOM_CREATE + 2
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_TWO_ROOM_CREATE + 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("the de-reg-card alert is passed through when mobile_preRegistered_repurpose is on") {
                val booking = basketReadBooking(reservationIds = listOf("6012005"), deRegCardCompleted = true)

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket With De-Reg Card Alert")

                expect("creates the OPEN basket the read acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_ONE_ROOM_CREATE
                }

                val result =
                    hotelReservationApi.getReservationsByBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                        featureFlagOverrides = preRegisteredRepurposeOnPins,
                    )

                result.attachEvidence("Get Reservations By Basket Repurpose On")

                expect("keeps the de-reg-card flag ohip-adapter derived from Opera's alert") {
                    result.response.status.value shouldBe 200
                    result.body.reservationByIdList shouldHaveSize 1
                    result.body.reservationByIdList
                        .single()
                        .deRegCardCompleted shouldBe true
                }

                expect("reads the single reservation and its enrichment once each") {
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_ONE_ROOM_CREATE + 6
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_ONE_ROOM_CREATE + 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_GET_RATE_INFO_AFTER_ONE_ROOM_CREATE + 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_ONE_ROOM_CREATE + 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("the same de-reg-card alert is suppressed when mobile_preRegistered_repurpose is off") {
                val booking = basketReadBooking(reservationIds = listOf("6012006"), deRegCardCompleted = true)

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket With De-Reg Card Alert Repurpose Off")

                expect("creates the OPEN basket the read acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_ONE_ROOM_CREATE
                }

                val result =
                    hotelReservationApi.getReservationsByBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                        featureFlagOverrides = basketReadFlagPins,
                    )

                result.attachEvidence("Get Reservations By Basket Repurpose Off")

                expect("clears the de-reg-card flag even though Opera raised the alert") {
                    result.response.status.value shouldBe 200
                    result.body.reservationByIdList shouldHaveSize 1
                    result.body.reservationByIdList
                        .single()
                        .deRegCardCompleted shouldBe false
                }

                expect("makes exactly the same Opera calls as the flag-on twin") {
                    // The suppression is an in-process rewrite in
                    // HotelReservationOhipOutPortImpl.getReservationsByIds, so only the mapped
                    // field moves — every downstream count is identical to the ON scenario.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_ONE_ROOM_CREATE + 6
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_ONE_ROOM_CREATE + 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_GET_RATE_INFO_AFTER_ONE_ROOM_CREATE + 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_ONE_ROOM_CREATE + 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a priced CITYTAX package makes the read report city tax and costs one extra rate-info call") {
                val booking =
                    basketReadBooking(
                        reservationIds = listOf("6012010"),
                        selectedPackages = listOf(SelectedPackage(code = "CITYTAX", unitPrice = 12.0)),
                    )

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket With City Tax Package")

                expect("creates the OPEN basket the read acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_ONE_ROOM_CREATE
                }

                val result =
                    hotelReservationApi.getReservationsByBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                        featureFlagOverrides = basketReadFlagPins,
                    )

                result.attachEvidence("Get Reservations By Basket City Tax")

                expect("reports the basket as carrying city tax") {
                    result.response.status.value shouldBe 200
                    result.body.reservationByIdList shouldHaveSize 1
                    result.body.hasCityTax shouldBe true
                }

                expect("adds the per-consumption-date rate-info read on top of the summary read") {
                    // One Opera call more than the package-free single-room read: the city-tax
                    // value is priced by a second GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo,
                    // this one with summaryInfo=false and a detailDate per consumption date.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_ONE_ROOM_CREATE + 7
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_GET_RATE_INFO_AFTER_ONE_ROOM_CREATE + 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_ONE_ROOM_CREATE + 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_ONE_ROOM_CREATE + 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("differing deposit policy codes across the basket are rejected when mobile_accepts_ota_booking is off") {
                val booking =
                    basketReadBooking(
                        reservationIds = listOf("6012007", "6012008"),
                        depositPolicyCodes = listOf("DEP", "PREPAY"),
                    )

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket With Mismatched Policy Codes")

                expect("creates the OPEN two-room basket the read is refused on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_TWO_ROOM_CREATE
                }

                val result =
                    hotelReservationApi.getReservationsByBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                        featureFlagOverrides = basketReadFlagPins,
                    )

                result.attachEvidence("Get Reservations By Basket Policy Mismatch")

                expect("rejects the basket with the adapter's policy-code error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe DIGITAL_POLICY_CODE_NOT_UNIQUE_ERR_CODE
                }

                expect("still pays for the full Opera read before the uniqueness guard runs") {
                    // The guard sits in ohip-adapter's in-port, after the whole out-port fan-out
                    // has already completed, so the read costs exactly what the happy path costs.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_TWO_ROOM_CREATE + 10
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_TWO_ROOM_CREATE + 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_GET_RATE_INFO_AFTER_TWO_ROOM_CREATE + 2
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_TWO_ROOM_CREATE + 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("the same mismatched basket is accepted as a third-party booking when mobile_accepts_ota_booking is on") {
                val booking =
                    basketReadBooking(
                        reservationIds = listOf("6012009", "6012010"),
                        depositPolicyCodes = listOf("DEP", "PREPAY"),
                    )

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket With Mismatched Policy Codes Ota On")

                expect("creates the OPEN two-room basket the read acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_TWO_ROOM_CREATE
                }

                val result =
                    hotelReservationApi.getReservationsByBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                        featureFlagOverrides = acceptsOtaBookingOnPins,
                    )

                result.attachEvidence("Get Reservations By Basket Policy Mismatch Ota On")

                expect("returns both reservations instead of rejecting the mismatch") {
                    // The flag turns third-party detection on, and it resolves TRUE here: a
                    // create-minted basket's Booking carries no bookingReference, so the Opera
                    // reservation read emits no externalReferences and the adapter sees a null
                    // idContext — neither a digital nor a desktop nor a distribution booking.
                    result.response.status.value shouldBe 200
                    result.body.reservationByIdList shouldHaveSize 2
                    result.body.basketReference shouldBe createdReservation.body.basketReference
                }

                expect("makes exactly the same Opera calls as the flag-off twin") {
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_TWO_ROOM_CREATE + 10
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_TWO_ROOM_CREATE + 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_GET_RATE_INFO_AFTER_TWO_ROOM_CREATE + 2
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_TWO_ROOM_CREATE + 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a basket whose Opera reservation is gone maps to the adapter not-found") {
                val booking = basketReadBooking(reservationIds = listOf("6012011"))
                val reservationId = requireNotNull(booking.room.reservationId)

                // A single room keeps GET_RESERVATION deterministic: ohip-adapter fans the reads
                // out through Flux.flatMap, so a multi-room basket could short-circuit mid-flight.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket Whose Reservation Opera Loses")

                expect("creates the OPEN basket the read is refused on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_ONE_ROOM_CREATE
                }

                val result =
                    hotelReservationApi.getReservationsByBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                        featureFlagOverrides = basketReadFlagPins,
                    )

                result.attachEvidence("Get Reservations By Basket Reservation Gone")

                expect("answers not found") {
                    result.response.status.value shouldBe 404
                    // `errCode` is deliberately not asserted: ohip-adapter sends
                    // DIGITAL_RESERVATION_NOT_FOUND (16) and it arrives as 0, because
                    // OhipAdapterClient deserializes the 4xx body into
                    // HotelReservationNotFoundException, which carries no @JsonCreator. See
                    // bug/get-reservations-by-basket-errcode-swallowed.md. The 5xx twin below
                    // proves the same hop preserves a code when the target class has one.
                }

                expect("stops at the empty reservation read without any enrichment") {
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_ONE_ROOM_CREATE + 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_ONE_ROOM_CREATE + 1
                    // The whole enrichment fan-out is downstream of the reservation read, so every
                    // mapping installFor really did install stays at the setup create's cost.
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_GET_RATE_INFO_AFTER_ONE_ROOM_CREATE
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_ONE_ROOM_CREATE
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // Disabled on bug/get-reservations-by-basket-partial-basket-500.md: a basket Opera
            // answers for only part of maps to HTTP 500 today, because
            // HotelReservationInPortImpl.getReservationByBasketRefResponse re-stamps reservation
            // ids with `for (i in reservationsIds.indices) reservationByIdList.get(i)` over a
            // shorter adapter response and throws IndexOutOfBoundsException. The scenario asserts
            // the correct behaviour — the same not-found the wholly missing basket gets.
            scenario("!a basket Opera answers for only one of its two reservations is reported as not found") {
                val booking = basketReadBooking(reservationIds = listOf("6012012", "6012013"))
                val lostReservationId = requireNotNull(booking.rooms.first().reservationId)

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket Opera Half Loses")

                expect("creates the OPEN two-room basket the read is refused on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_TWO_ROOM_CREATE
                }

                // Mid-journey override: only one of the two rooms disappears from Opera, and
                // only for the read below — the create still needed both.
                installStub(getReservationEmpty(booking.hotel.hotelId, lostReservationId))

                val result =
                    hotelReservationApi.getReservationsByBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                        featureFlagOverrides = basketReadFlagPins,
                    )

                result.attachEvidence("Get Reservations By Basket Half Gone")

                expect("answers not found, exactly as a wholly missing basket does") {
                    result.response.status.value shouldBe 404
                }

                expect("reads both reservations from Opera before failing") {
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_TWO_ROOM_CREATE + 2
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the reservation read surfaces the adapter failure code") {
                val booking = basketReadBooking(reservationIds = listOf("6012014"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket Whose Reservation Opera Rejects")

                expect("creates the OPEN basket the read is refused on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_ONE_ROOM_CREATE
                }

                val result =
                    hotelReservationApi.getReservationsByBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                        featureFlagOverrides = basketReadFlagPins,
                    )

                result.attachEvidence("Get Reservations By Basket Opera Rejects")

                expect("propagates the adapter's reservation-read failure code") {
                    // The contrast with the not-found scenario is the point: this 5xx body decodes
                    // into HotelReservationOhipException, which does carry a @JsonCreator, so the
                    // adapter's code survives the hop.
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe OHIP_GET_RESERVATION_ERR_CODE
                }

                expect("stops at the rejected reservation read without any enrichment") {
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_ONE_ROOM_CREATE + 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_ONE_ROOM_CREATE + 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_GET_RATE_INFO_AFTER_ONE_ROOM_CREATE
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_ONE_ROOM_CREATE
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

/**
 * Opera cost of the setup create, measured rather than derived. Itemizing it belongs to
 * CreateReservationSpec; these scenarios only need it fixed so the read's cost is the delta. The
 * one-room figure matches Wave 3's specs; the two-room figure is this row's own measurement.
 */
private const val OPERA_CALLS_AFTER_ONE_ROOM_CREATE = 4
private const val OPERA_CALLS_AFTER_TWO_ROOM_CREATE = 7

/**
 * How that setup cost splits per Opera endpoint, so each per-endpoint assertion states the read's
 * own delta rather than a bare total. The setup create never reads the reservation back and never
 * touches folios, profiles or Front Desk card details.
 */
private const val OPERA_GET_RESERVATION_AFTER_ONE_ROOM_CREATE = 0
private const val OPERA_HOTEL_CONFIG_AFTER_ONE_ROOM_CREATE = 1
private const val OPERA_GET_RATE_INFO_AFTER_ONE_ROOM_CREATE = 1

private const val OPERA_GET_RESERVATION_AFTER_TWO_ROOM_CREATE = 0
private const val OPERA_HOTEL_CONFIG_AFTER_TWO_ROOM_CREATE = 1
private const val OPERA_GET_RATE_INFO_AFTER_TWO_ROOM_CREATE = 2
private const val OPERA_UPDATE_RESERVATION_AFTER_TWO_ROOM_CREATE = 2

/**
 * ohip-adapter's `DIGITAL_POLICY_CODE_NOT_UNIQUE_EXCEPTION`, raised when the basket's reservations
 * disagree on their deposit policy code and the booking is not third-party.
 */
private const val DIGITAL_POLICY_CODE_NOT_UNIQUE_ERR_CODE = 19

/** ohip-adapter's `OHIP_GET_RESERVATION_EXCEPTION`. */
private const val OHIP_GET_RESERVATION_ERR_CODE = 960

/**
 * The rooms the basket read acts on: one Opera reservation per entry in [reservationIds], each
 * with its own guest profile and Opera payment card.
 *
 * Every room is pay-on-arrival — `amountAlreadyPaid` stays at its `0.0` default — which keeps the
 * setup create on the plain shape Wave 3 pinned. The distinct profile ids are what make the
 * profile reads countable per room, and the per-room card is what makes the single credit-card
 * lookup a real finding rather than an artifact of only one card existing. The guest profile is
 * also required by the create setup, which otherwise mints a TEMP profile id no profile gate
 * installs. [depositPolicyCodes] carries the one contested fact of the OTA-booking pair; every
 * other fact stays symmetric across the rooms because reservation-id ordering is
 * non-deterministic (bug/MultiRoomReservationPackageOrdering.md). [selectedPackages] is applied to
 * every room and carries the city-tax pair's contested fact: a `CITYTAX` package with a positive
 * unit price is what makes Opera report city tax on the reservation. [deRegCardCompleted] is the
 * other contested fact: it makes the reservation read carry Opera's completed-pre-check-in alert,
 * the alert ohip-adapter reads back as `deRegCardCompleted`.
 */
private fun basketReadBooking(
    reservationIds: List<String>,
    depositPolicyCodes: List<String> = reservationIds.map { "DEP" },
    selectedPackages: List<SelectedPackage> = emptyList(),
    deRegCardCompleted: Boolean = false,
): Booking {
    val arrival = LocalDate.now().plusDays(21)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            reservationIds.mapIndexed { index, reservationId ->
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    sourceCode = "44",
                    depositPolicyCode = depositPolicyCodes[index],
                    selectedPackages = selectedPackages,
                    preRegistration =
                        PreRegistration(deRegCardCompleted = deRegCardCompleted)
                            .takeIf { deRegCardCompleted },
                    guestProfile =
                        GuestProfile(
                            profileId = "8012$reservationId",
                            firstName = "Robin",
                            lastName = "Fletcher",
                            email = "robin.fletcher.$reservationId@test.com",
                            phone = "+447700900031",
                            addressLine = "31 High Street",
                            city = "London",
                            postcode = "SW1A 1AC",
                        ),
                    operaPaymentCard =
                        OperaPaymentCard(
                            cardId = "12345",
                            cardType = "Va",
                            cardNumber = "4764776852337921103",
                            expirationDate = "2030-03-31",
                            cardHolderName = "Robin Fletcher",
                        ),
                )
            },
    )
}

/** The create that mints the OPEN basket every scenario reads back. */
private fun createReservationRequest(booking: Booking): CreateReservationRequest {
    val rate = booking.hotel.availableRates.single()
    val arrival = requireNotNull(booking.arrival).toString()
    val departure = requireNotNull(booking.departure).toString()

    return CreateReservationRequest(
        reservations =
            booking.rooms.map { room ->
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
                )
            },
        bookingChannel =
            CreateReservationBookingChannel(
                channel = "PI",
                subchannel = "WEB",
                language = "EN",
            ),
        bookingFlowId = "hre-get-reservations-by-basket",
    )
}
