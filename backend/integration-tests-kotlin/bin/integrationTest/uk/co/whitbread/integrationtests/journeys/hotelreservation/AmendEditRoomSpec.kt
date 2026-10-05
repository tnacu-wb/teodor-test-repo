package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationBookingChannel
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRateRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.EditRoomLeadGuest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.EditRoomOccupancy
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.EditRoomRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.HotelReservationFeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Aem
import uk.co.whitbread.integrationtests.testkit.model.AemAllowedRoomTypesByOccupancy
import uk.co.whitbread.integrationtests.testkit.model.AemBookingWidgetConfig
import uk.co.whitbread.integrationtests.testkit.model.AemGlobalConfig
import uk.co.whitbread.integrationtests.testkit.model.AemSite
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.CancellationPolicyAmountPercent
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.OperaPaymentCard
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationCancellationPolicy
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.SelectedPackage
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// Setup-only pins, copied from ConfirmReservationSpec / CreateReservationGuestSpec: they hold the
// created basket on the plain pay-on-arrival shape the amend acts on. The channel is `PI` because
// the deployed rules service answers no channel rule for a `DISTR` creation (Wave 3 lesson).
private val createReservationFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
        HotelReservationFeatureFlag.CITY_TAX_UK to false,
        HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to false,
    )

// The six flags the flow doc lists on this path. `release_amend_distribution_single_call` is
// pinned ON in every scenario but one: with a `DISTR` channel it is the only combination that gets
// past the token gate on a basket the create minted, whose `originalBasketId` is null. Its OFF
// twin is the errCode-120 scenario at the bottom of the file.
//
// `release_pi_bb_ccui_aem_search_rules` is pinned ON everywhere and has no OFF twin here: with the
// flag off, max-rooms and max-room-occupancy move to the deployed rules-agent-entity-service, which
// answers 404 for `channelId=DISTR` — the only channel this endpoint is reachable on — and the
// journey 500s with errCode 0. Logged in data_model_issues/hotel-reservation-data-model.md rather
// than shipped as a weakened 500 assertion.
private val editRoomFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.AMEND_DISTRIBUTION_SINGLE_CALL to true,
        HotelReservationFeatureFlag.AEM_SEARCH_RULES to true,
        HotelReservationFeatureFlag.MAX_ROOMS_AMEND to false,
        HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to false,
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false,
    )

// `release_pi_ccui_distr_web3_occupancy_supplement` ON: the supplement leg runs against
// rules-agent-entity-service and basket-service, neither of which is a WireMock upstream.
private val occupancySupplementOnPins: Map<FeatureFlag, Boolean> =
    editRoomFlagPins + (HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to true)

// The three flags that this endpoint reads but cannot act on, pinned ON together.
private val inertFlagsOnPins: Map<FeatureFlag, Boolean> =
    editRoomFlagPins +
        mapOf(
            HotelReservationFeatureFlag.MAX_ROOMS_AMEND to true,
            HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to true,
            OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to true,
        )

// `release_amend_distribution_single_call` OFF: the token gate runs against the basket's
// `originalBasketId`, which a created (non-copy) basket leaves null.
private val singleCallOffPins: Map<FeatureFlag, Boolean> =
    editRoomFlagPins + (HotelReservationFeatureFlag.AMEND_DISTRIBUTION_SINGLE_CALL to false)

/**
 * Proves the amend journey's room-level edit: `PUT /v1/reservations/amend/editRoom` runs the whole
 * non-refundable guard against a real two-room basket, validates a WB room type's occupancy against
 * the AEM search rules, strips the reservation's meal packages when the adult count drops, and
 * writes exactly one Opera reservation update — while a reservation the basket does not hold, an
 * occupancy the rules reject, a missing single-call flag and an Opera rejection each stop the write.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/AmendEditRoom.md
 */
class AmendEditRoomSpec :
    JourneySpec(
        "a room of an amend basket can be edited",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("a DISTR amend edits one room of the created basket and returns its reference") {
                val booking = editRoomBooking(reservationIds = listOf("6044001", "6044002"))
                val editedRoom = booking.rooms[1]

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Two-Room Basket To Amend")

                expect("creates the OPEN two-room basket the amend acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.amendEditRoom(
                        request =
                            editRoomRequest(
                                tempBookingRef = createdReservation.body.basketReference,
                                reservationId = requireNotNull(editedRoom.reservationId),
                                roomType = "LOWDBL",
                                adults = 2,
                            ),
                        testId = testId,
                        featureFlagOverrides = editRoomFlagPins,
                    )

                result.attachEvidence("Amend Edit Room")

                expect("returns the amend basket reference it was given") {
                    result.response.status.value shouldBe 200
                    result.body.tempBookingRef shouldBe createdReservation.body.basketReference
                }

                expect("runs the guard and the temp read, then writes the edit to Opera exactly once") {
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + EDIT_OPERA_CALLS
                    // GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}: the guard's own
                    // reservations read and its cancel-information read, plus the temp read.
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe EDIT_GET_RESERVATION
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo?summaryInfo=true.
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_RATE_INFO_AFTER_CREATE + EDIT_RATE_INFO
                    // GET /csh/v1/hotels/{hotelId}/reservations/{reservationId}/folios.
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe EDIT_FOLIOS
                    // GET /ent/config/v1/hotels/{hotelId}?fetchInstructions=General.
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_CREATE + EDIT_HOTEL_CONFIG
                    // GET /crm/v1/profiles/{profileId}.
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe EDIT_PROFILE
                    // GET /fof/config/v1/creditCardInfo.
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe EDIT_CREDIT_CARD_INFO
                    // GET /rtp/v1/ratePlans, twice: the rate-plan set and getAmendableInformation.
                    callCount(OperaEndpoint.GET_RATE_PLANS) shouldBe EDIT_RATE_PLANS
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}: the edit itself,
                    // exactly one beyond the setup create's own reservation updates.
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe OPERA_UPDATE_RESERVATION_AFTER_CREATE + 1
                    // The meal reset never runs (the adult count is unchanged), and nothing on this
                    // path cancels, deposits or refunds.
                    callCount(OperaEndpoint.GET_PACKAGES) shouldBe 0
                    callCount(OperaEndpoint.GET_PACKAGE_GROUPS) shouldBe 0
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(OperaEndpoint.GET_RESERVATION_DEPOSITS) shouldBe 0
                    // The edit keeps the reservation's own room type, so ohip-adapter never has to
                    // resolve a WB room type against Opera's hotel inventory.
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 0
                    // Only the guard's search-rules read reaches AEM: `LOWDBL` is not a WB room
                    // type, so neither the hotel-detail nor the occupancy-rule hop happens.
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a WB room type additionally validates occupancy against the AEM search rules") {
                val booking = editRoomBooking(reservationIds = listOf("6044003", "6044004"))
                val editedRoom = booking.rooms[1]

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket To Amend Into A WB Room Type")

                expect("creates the OPEN two-room basket the amend acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.amendEditRoom(
                        request =
                            editRoomRequest(
                                tempBookingRef = createdReservation.body.basketReference,
                                reservationId = requireNotNull(editedRoom.reservationId),
                                roomType = "DB",
                                adults = 2,
                            ),
                        testId = testId,
                        featureFlagOverrides = editRoomFlagPins,
                    )

                result.attachEvidence("Amend Edit Room WB Room Type")

                expect("returns the amend basket reference it was given") {
                    result.response.status.value shouldBe 200
                    result.body.tempBookingRef shouldBe createdReservation.body.basketReference
                }

                expect("adds the content hops and the room-type resolution the WB room type needs") {
                    // The materially different cross-boundary path: three AEM documents instead of
                    // one — the guard's search rules, the hotel-detail read that resolves the brand,
                    // and the search rules again for max-room-occupancy.
                    callCount(Upstream.AEM) shouldBe 3
                    // GET /inv/v1/hotels/{hotelId}/hotelInventory: because the edit really changes
                    // the Opera room type, ohip-adapter resolves `DB` against the hotel's inventory
                    // before writing. The rest of the Opera profile is the plain amend's.
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe WB_ROOM_TYPE_INVENTORY_READS
                    callCount(Upstream.OPERA) shouldBe
                        OPERA_CALLS_AFTER_CREATE + EDIT_OPERA_CALLS + WB_ROOM_TYPE_INVENTORY_READS
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe EDIT_GET_RESERVATION
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_RATE_INFO_AFTER_CREATE + EDIT_RATE_INFO
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe EDIT_FOLIOS
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_CREATE + EDIT_HOTEL_CONFIG
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe EDIT_PROFILE
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe EDIT_CREDIT_CARD_INFO
                    callCount(OperaEndpoint.GET_RATE_PLANS) shouldBe EDIT_RATE_PLANS
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe OPERA_UPDATE_RESERVATION_AFTER_CREATE + 1
                    callCount(OperaEndpoint.GET_PACKAGES) shouldBe 0
                    callCount(OperaEndpoint.GET_PACKAGE_GROUPS) shouldBe 0
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("reducing the adult count resets the reservation's meal packages before the edit") {
                val booking =
                    editRoomBooking(
                        reservationIds = listOf("6044005", "6044006"),
                        selectedPackages = listOf(SelectedPackage(code = "OBFBRK")),
                    )
                val editedRoom = booking.rooms[1]

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket With A Meal Package")

                expect("creates the OPEN two-room basket the amend acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.amendEditRoom(
                        request =
                            editRoomRequest(
                                tempBookingRef = createdReservation.body.basketReference,
                                reservationId = requireNotNull(editedRoom.reservationId),
                                roomType = "LOWDBL",
                                adults = 1,
                            ),
                        testId = testId,
                        featureFlagOverrides = editRoomFlagPins,
                    )

                result.attachEvidence("Amend Edit Room Fewer Adults")

                expect("returns the amend basket reference it was given") {
                    result.response.status.value shouldBe 200
                    result.body.tempBookingRef shouldBe createdReservation.body.basketReference
                }

                expect("reads the ancillaries catalogue and rewrites the reservation before the edit") {
                    // GET /rtp/v1/packages and GET /rtp/v1/hotels/{hotelId}/packageGroups, read once
                    // by the ancillaries lookup the adult decrease triggers.
                    callCount(OperaEndpoint.GET_PACKAGES) shouldBe MEAL_RESET_PACKAGES
                    callCount(OperaEndpoint.GET_PACKAGE_GROUPS) shouldBe MEAL_RESET_PACKAGE_GROUPS
                    // The meal-strip rewrite is a real Opera write made before the edit and never
                    // rolled back if the edit then fails: it lands as two reservation PUTs on the
                    // edited reservation, and the edit itself is the third beyond the setup create.
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe OPERA_UPDATE_RESERVATION_AFTER_CREATE + MEAL_RESET_UPDATES
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + MEAL_RESET_OPERA_CALLS
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe MEAL_RESET_GET_RESERVATION
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_RATE_INFO_AFTER_CREATE + EDIT_RATE_INFO
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_CREATE + EDIT_HOTEL_CONFIG
                    callCount(OperaEndpoint.GET_RATE_PLANS) shouldBe EDIT_RATE_PLANS
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("changing the adult count runs the occupancy-supplement leg with the flag on") {
                val booking = editRoomBooking(reservationIds = listOf("6044009", "6044010"))
                val editedRoom = booking.rooms[1]

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket To Amend With Occupancy Supplement")

                expect("creates the OPEN two-room basket the amend acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.amendEditRoom(
                        request =
                            editRoomRequest(
                                tempBookingRef = createdReservation.body.basketReference,
                                reservationId = requireNotNull(editedRoom.reservationId),
                                roomType = "LOWDBL",
                                adults = 3,
                            ),
                        testId = testId,
                        featureFlagOverrides = occupancySupplementOnPins,
                    )

                result.attachEvidence("Amend Edit Room Occupancy Supplement On")

                expect("returns the amend basket reference it was given") {
                    result.response.status.value shouldBe 200
                    result.body.tempBookingRef shouldBe createdReservation.body.basketReference
                }

                expect("leaves every counted downstream exactly where the flag-off amend leaves it") {
                    // Both flag states are indistinguishable here: the supplement leg only touches
                    // rules-agent-entity-service (GET /v1/rules/occupancy-supplement) and
                    // basket-service (PUT /v1/baskets/{ref}/occupancy), neither of which is a
                    // WireMock upstream, and the response carries only `tempBookingRef` — so the ON
                    // and OFF states are byte-identical here. One pinned-ON scenario is shipped and
                    // every other scenario pins the flag OFF.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + EDIT_OPERA_CALLS
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe EDIT_GET_RESERVATION
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_RATE_INFO_AFTER_CREATE + EDIT_RATE_INFO
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_CREATE + EDIT_HOTEL_CONFIG
                    callCount(OperaEndpoint.GET_RATE_PLANS) shouldBe EDIT_RATE_PLANS
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe OPERA_UPDATE_RESERVATION_AFTER_CREATE + 1
                    // An increase in adults never resets meals, so the ancillaries lookup stays off.
                    callCount(OperaEndpoint.GET_PACKAGES) shouldBe 0
                    callCount(OperaEndpoint.GET_PACKAGE_GROUPS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("the OTA, pre-registration and max-rooms-amend flags change nothing on this path") {
                val booking = editRoomBooking(reservationIds = listOf("6044011", "6044012"))
                val editedRoom = booking.rooms[1]

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket To Amend With Inert Flags On")

                expect("creates the OPEN two-room basket the amend acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.amendEditRoom(
                        request =
                            editRoomRequest(
                                tempBookingRef = createdReservation.body.basketReference,
                                reservationId = requireNotNull(editedRoom.reservationId),
                                roomType = "LOWDBL",
                                adults = 2,
                            ),
                        testId = testId,
                        featureFlagOverrides = inertFlagsOnPins,
                    )

                result.attachEvidence("Amend Edit Room Inert Flags On")

                expect("returns the amend basket reference it was given") {
                    result.response.status.value shouldBe 200
                    result.body.tempBookingRef shouldBe createdReservation.body.basketReference
                }

                expect("makes byte-for-byte the same downstream calls as the flag-off amend") {
                    // Covers the ON half of three flow-doc flags at once. `mobile_accepts_ota_booking`
                    // is only consulted for a basket whose idContext is the OTA one, which this
                    // create never mints; `mobile_preRegistered_repurpose` only forces
                    // `deRegCardCompleted` on a field the response does not carry; and
                    // `release_pi_bb_ccui_maxrooms_amend` really does take its non-compliant branch
                    // here (AEM `maxRoomsAmend` is 1 against a two-room basket), but that branch
                    // answers isAmendable=false, and the edit only rejects when the booking is both
                    // non-cancellable and amendable — so the outcome and the call profile hold.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + EDIT_OPERA_CALLS
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe EDIT_GET_RESERVATION
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_RATE_INFO_AFTER_CREATE + EDIT_RATE_INFO
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe EDIT_FOLIOS
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_CREATE + EDIT_HOTEL_CONFIG
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe EDIT_PROFILE
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe EDIT_CREDIT_CARD_INFO
                    callCount(OperaEndpoint.GET_RATE_PLANS) shouldBe EDIT_RATE_PLANS
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe OPERA_UPDATE_RESERVATION_AFTER_CREATE + 1
                    callCount(OperaEndpoint.GET_PACKAGES) shouldBe 0
                    callCount(OperaEndpoint.GET_PACKAGE_GROUPS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("without the single-call flag the same DISTR amend is rejected before any Opera call") {
                val booking = editRoomBooking(reservationIds = listOf("6044013", "6044014"))
                val editedRoom = booking.rooms[1]

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket To Amend Without The Single-Call Flag")

                expect("creates the OPEN two-room basket the amend acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.amendEditRoom(
                        request =
                            editRoomRequest(
                                tempBookingRef = createdReservation.body.basketReference,
                                reservationId = requireNotNull(editedRoom.reservationId),
                                roomType = "LOWDBL",
                                adults = 2,
                            ),
                        testId = testId,
                        featureFlagOverrides = singleCallOffPins,
                    )

                result.attachEvidence("Amend Edit Room Single-Call Flag Off")

                expect("rejects the unauthenticated caller's missing token") {
                    // With the flag off the token is validated against the basket's
                    // `originalBasketId`, which only the copy-booking flow mints — so the OFF state
                    // has no reachable happy path on this row until `POST /v1/reservations/copy`
                    // (matrix row 33) lands. The gap is logged in data_model_issues/.
                    result.response.status.value shouldBe 400
                    result.errorBody?.errCode shouldBe INVALID_TOKEN_ERR_CODE
                }

                expect("stops before the guard, so no Opera or AEM call is made at all") {
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_RATE_PLANS) shouldBe 0
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_CREATE
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe OPERA_UPDATE_RESERVATION_AFTER_CREATE
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation id the basket does not hold is not found") {
                val booking = editRoomBooking(reservationIds = listOf("6044015", "6044016"))

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket To Amend With An Unknown Reservation")

                expect("creates the OPEN two-room basket the amend acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.amendEditRoom(
                        request =
                            editRoomRequest(
                                tempBookingRef = createdReservation.body.basketReference,
                                reservationId = "9990001",
                                roomType = "LOWDBL",
                                adults = 2,
                            ),
                        testId = testId,
                        featureFlagOverrides = editRoomFlagPins,
                    )

                result.attachEvidence("Amend Edit Room Unknown Reservation")

                expect("rejects the reservation the basket's items do not name") {
                    // Orchestration-determined, not bean validation: the temp basket's item
                    // `sourceId`s are what decide this.
                    result.response.status.value shouldBe 404
                    result.errorBody?.errCode shouldBe RESERVATION_NOT_FOUND_ERR_CODE
                }

                expect("runs the guard and the temp read in full but writes nothing to Opera") {
                    // The whole point: the reservation PUT never happens, so the amend costs exactly
                    // one Opera call less than the happy path.
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe OPERA_UPDATE_RESERVATION_AFTER_CREATE
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + EDIT_OPERA_CALLS - 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe EDIT_GET_RESERVATION
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_RATE_INFO_AFTER_CREATE + EDIT_RATE_INFO
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_CREATE + EDIT_HOTEL_CONFIG
                    callCount(OperaEndpoint.GET_RATE_PLANS) shouldBe EDIT_RATE_PLANS
                    callCount(OperaEndpoint.GET_PACKAGES) shouldBe 0
                    callCount(OperaEndpoint.GET_PACKAGE_GROUPS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a WB room type the occupancy rule rejects is refused before the Opera write") {
                val booking = editRoomBooking(reservationIds = listOf("6044017", "6044018"))
                val editedRoom = booking.rooms[1]

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket To Amend Into A Rejected Occupancy")

                expect("creates the OPEN two-room basket the amend acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.amendEditRoom(
                        request =
                            editRoomRequest(
                                tempBookingRef = createdReservation.body.basketReference,
                                reservationId = requireNotNull(editedRoom.reservationId),
                                roomType = "SB",
                                adults = 2,
                            ),
                        testId = testId,
                        featureFlagOverrides = editRoomFlagPins,
                    )

                result.attachEvidence("Amend Edit Room Rejected Occupancy")

                expect("refuses the room type the search rules do not accept at two adults") {
                    // Driven entirely by the stubbed AEM global-config document, which lists `SB`
                    // only at one adult — deployed-collaborator data, not input validation.
                    result.response.status.value shouldBe 400
                    result.errorBody?.errCode shouldBe WRONG_ROOM_TYPE_ERR_CODE
                }

                expect("reads all three AEM documents but never writes the edit") {
                    callCount(Upstream.AEM) shouldBe 3
                    // The rejection precedes both the meal reset and the edit, so neither the
                    // reservation PUT nor the room-type resolution against Opera's inventory runs.
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe OPERA_UPDATE_RESERVATION_AFTER_CREATE
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 0
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + EDIT_OPERA_CALLS - 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe EDIT_GET_RESERVATION
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_RATE_INFO_AFTER_CREATE + EDIT_RATE_INFO
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_CREATE + EDIT_HOTEL_CONFIG
                    callCount(OperaEndpoint.GET_RATE_PLANS) shouldBe EDIT_RATE_PLANS
                    callCount(OperaEndpoint.GET_PACKAGES) shouldBe 0
                    callCount(OperaEndpoint.GET_PACKAGE_GROUPS) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the edit is propagated and nothing else is written") {
                val booking = editRoomBooking(reservationIds = listOf("6044019", "6044020"))
                val editedRoom = booking.rooms[1]

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Basket To Amend Against A Refusing Opera")

                expect("creates the OPEN two-room basket the amend acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                // Mid-journey override: the setup create needs the healthy reservation PUT,
                // and only the edit below must see Opera refuse the change.
                installStub(putReservationFailure(booking, listOf(editedRoom)))

                val result =
                    hotelReservationApi.amendEditRoom(
                        request =
                            editRoomRequest(
                                tempBookingRef = createdReservation.body.basketReference,
                                reservationId = requireNotNull(editedRoom.reservationId),
                                roomType = "LOWDBL",
                                adults = 2,
                            ),
                        testId = testId,
                        featureFlagOverrides = editRoomFlagPins,
                    )

                result.attachEvidence("Amend Edit Room Opera Rejects")

                expect("propagates the adapter's change-reservation failure code unchanged") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe OHIP_CHANGE_RESERVATION_ERR_CODE
                }

                expect("attempts the edit exactly once and makes no compensating write") {
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe OPERA_UPDATE_RESERVATION_AFTER_CREATE + 1
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + EDIT_OPERA_CALLS
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe EDIT_GET_RESERVATION
                    callCount(OperaEndpoint.GET_RATE_PLANS) shouldBe EDIT_RATE_PLANS
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_PACKAGES) shouldBe 0
                    callCount(OperaEndpoint.GET_PACKAGE_GROUPS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

/**
 * Opera cost of the setup create, measured rather than derived. Itemizing it belongs to
 * CreateReservationSpec; these scenarios only need it fixed so the amend's cost is the delta.
 */
private const val OPERA_CALLS_AFTER_CREATE = 7

/** How that setup cost splits across the endpoints the amend also uses. */
private const val OPERA_HOTEL_CONFIG_AFTER_CREATE = 1
private const val OPERA_RATE_INFO_AFTER_CREATE = 2
private const val OPERA_UPDATE_RESERVATION_AFTER_CREATE = 2

/**
 * Opera cost of one amend beyond the setup create, measured at implement stage. It is dominated by
 * the non-refundable guard — a full two-room reservations read, a two-room cancel-information read
 * and two rate-plan reads — plus the temp basket's own reservations read; the edit itself is a
 * single Opera PUT.
 */
private const val EDIT_OPERA_CALLS = 28
private const val EDIT_GET_RESERVATION = 6
private const val EDIT_RATE_INFO = 4
private const val EDIT_FOLIOS = 4
private const val EDIT_HOTEL_CONFIG = 5
private const val EDIT_PROFILE = 4
private const val EDIT_CREDIT_CARD_INFO = 2
private const val EDIT_RATE_PLANS = 2

/**
 * The room-type resolution ohip-adapter runs when the edit really changes the Opera room type: it
 * reads `GET /inv/v1/hotels/{hotelId}/hotelInventory` twice to map the WB room type onto an Opera
 * one. An edit that keeps the reservation's current room type never makes the hop.
 */
private const val WB_ROOM_TYPE_INVENTORY_READS = 2

/** The extra Opera work the meal reset adds when the adult count drops. */
private const val MEAL_RESET_OPERA_CALLS = 35
private const val MEAL_RESET_GET_RESERVATION = 9
private const val MEAL_RESET_PACKAGES = 1
private const val MEAL_RESET_PACKAGE_GROUPS = 1
private const val MEAL_RESET_UPDATES = 3

/** `DIGITAL_INVALID_TOKEN2` — the token gate's rejection. */
private const val INVALID_TOKEN_ERR_CODE = 120

/** `DIGITAL_RESERVATION_ID_EXCEPTION` — the basket does not hold that reservation. */
private const val RESERVATION_NOT_FOUND_ERR_CODE = 56

/** `DIGITAL_WRONG_ROOM_EXCEPTION` — the occupancy rule refuses the room type. */
private const val WRONG_ROOM_TYPE_ERR_CODE = 65

/** ohip-adapter's `OHIP_CHANGE_RESERVATION_EXCEPTION`. */
private const val OHIP_CHANGE_RESERVATION_ERR_CODE = 958

/**
 * The two-room amend basket every scenario edits one room of.
 *
 * Both rooms are identical apart from their reservation id — reservation-id ordering inside the
 * basket is non-deterministic (bug/MultiRoomReservationPackageOrdering.md) — and both are
 * pay-on-arrival, which keeps the setup create on the plain shape Wave 3 pinned. The guest profile
 * is required by the create setup and the Opera payment card by the guard's reservations read; the
 * shared `depositPolicyCode` keeps ohip-adapter's deposit-policy uniqueness guard quiet.
 *
 * `aem.globalConfig` is the fact that makes the search-rules hops resolvable: HRE asks
 * content-entity-service for the rules with no country, language or brand, and content-entity
 * defaults them to `gb`/`en`/`pi` and maps channel `DISTR` onto the AEM `distribution` site, which
 * is exactly the document this config gates. Its `allowedRoomTypesByOccupancy` accepts `DB` at two
 * adults and `SB` only at one, and `maxRoomsAmend` of 1 is what the max-rooms-amend scenario trips.
 */
private fun editRoomBooking(
    reservationIds: List<String>,
    selectedPackages: List<SelectedPackage> = emptyList(),
): Booking {
    val arrival = LocalDate.now().plusDays(28)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            reservationIds.map { reservationId ->
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    sourceCode = "44",
                    depositPolicyCode = "DEP",
                    selectedPackages = selectedPackages,
                    guestProfile =
                        GuestProfile(
                            profileId = "8044$reservationId",
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
                    // The fact the non-refundable guard turns on: ohip-adapter reports a
                    // reservation with no cancellation policy as not cancellable, and a booking
                    // that is not cancellable but is amendable is exactly what `editRoom` rejects
                    // with errCode 102. A deadline five days before arrival is still in the future.
                    cancellationPolicies =
                        listOf(
                            ReservationCancellationPolicy(
                                policyId = "129722",
                                deadline = arrival.minusDays(5).toString(),
                                revenueType = "Rooms",
                                amountPercent =
                                    CancellationPolicyAmountPercent(
                                        basisType = "FlatAmount",
                                        nights = 1,
                                        percent = 0.0,
                                        amount = 50.0,
                                    ),
                                policyCode = "DOA",
                                manual = false,
                                effective = true,
                                percentageDue = 100.0,
                            ),
                        ),
                )
            },
        aem =
            Aem(
                globalConfig =
                    AemGlobalConfig(
                        country = "gb",
                        language = "en",
                        site = AemSite.DISTRIBUTION,
                        brand = "pi",
                        bookingWidgetConfig =
                            AemBookingWidgetConfig(
                                maxRooms = 9,
                                maxRoomsAmend = 1,
                                numberOfNights = 16,
                                maxArrivalDate = 365,
                                allowedRoomTypesByOccupancy =
                                    listOf(
                                        AemAllowedRoomTypesByOccupancy(
                                            acceptedRoomTypes = listOf("DB", "TWIN", "DIS"),
                                            adultsNumber = 2,
                                            childrenNumber = 0,
                                        ),
                                        AemAllowedRoomTypesByOccupancy(
                                            acceptedRoomTypes = listOf("SB"),
                                            adultsNumber = 1,
                                            childrenNumber = 0,
                                        ),
                                    ),
                            ),
                    ),
            ),
    )
}

/** The create that mints the OPEN two-room basket every scenario amends. */
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
        bookingFlowId = "hre-amend-edit-room",
    )
}

/**
 * The room-level edit under test. The lead guest differs from the one the create wrote, so every
 * scenario really does change it; `bookingChannel` is `DISTR` because that is the only channel the
 * single-call flag admits without a copy-minted `originalBasketId`, and no `token` is sent.
 */
private fun editRoomRequest(
    tempBookingRef: String,
    reservationId: String,
    roomType: String,
    adults: Int,
    children: Int = 0,
): EditRoomRequest =
    EditRoomRequest(
        tempBookingRef = tempBookingRef,
        reservationId = reservationId,
        roomType = roomType,
        roomOccupancy = EditRoomOccupancy(adultsNumber = adults, childrenNumber = children),
        leadGuest =
            EditRoomLeadGuest(
                title = "Mr",
                firstName = "Alexander",
                lastName = "Hartley",
                emailAddress = "alexander.hartley@test.com",
            ),
        bookingChannel =
            CreateReservationBookingChannel(
                channel = "DISTR",
                subchannel = "WEB",
                language = "EN",
            ),
    )
