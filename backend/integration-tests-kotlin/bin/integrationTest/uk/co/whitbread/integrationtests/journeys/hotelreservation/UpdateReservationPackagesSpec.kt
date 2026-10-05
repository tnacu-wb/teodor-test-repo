package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationBookingChannel
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRateRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ReservationPackageSelection
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ReservationRoomPackageSelections
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.UpdateReservationPackagesRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PACKAGES_LIST_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PACKAGE_GROUP_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.HotelReservationFeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.SelectedPackage
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

/**
 * Flags pinned on every setup create so the exercised path never depends on a deployed default:
 * DS payment method off (Opera create stubs match CA), city tax off (no content-entity fixtures
 * in these scenarios), occupancy supplement off (plain multi-room path).
 */
private val createReservationFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
        HotelReservationFeatureFlag.CITY_TAX_UK to false,
        HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to false,
    )

private val updateReservationPackagesFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.DISTRIBUTION_BOOKING_FEE to false,
    )

private val arrival: LocalDate = LocalDate.now().plusDays(14)
private val departure: LocalDate = arrival.plusDays(2)

class UpdateReservationPackagesSpec :
    JourneySpec(
        "reservation packages can be updated through the reservation entity service",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("PUT /v1/reservations/ancillaries adds two selections of one package") {
                val packageToAdd = SelectedPackage(code = "BFADBF", quantity = 2)
                val booking = booking(room("6003101", selectedPackagesAfterUpdate = listOf(packageToAdd)))

                installFor(booking)

                val createResult =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking, "add-one-package"),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )
                createResult.attachEvidence("Create Basket For Package Addition")

                expect("creates the basket used by the update") {
                    createResult.response.status.value shouldBe 201
                }

                val updateResult =
                    hotelReservationApi.updateReservationPackages(
                        request =
                            updateReservationPackagesRequest(
                                booking = booking,
                                basketReference = createResult.body.basketReference,
                                currentPackages = listOf(listOf(packageToAdd)),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReservationPackagesFlagPins,
                    )
                updateResult.attachEvidence("Add Package To One Reservation")

                expect("returns the basket reference") {
                    updateResult.response.status.value shouldBe 200
                    updateResult.body.basketReference shouldBe createResult.body.basketReference
                }
            }

            /**
             * Reproduces a known reservation-to-package ordering bug:
             *
             * 1. Create two reservations whose Opera IDs are `6003102` and `6003103`.
             * 2. Submit `HSCKIN` for the first room and `HSCOU2` for the second room.
             * 3. Keep strict Opera PUT mappings that expect those packages against the corresponding
             *    reservation IDs.
             * 4. Observe the endpoint returning 500 because OHIP sends `HSCKIN` to `6003103`.
             *
             * The create flow collects reservation IDs into a Set before adding them to the basket,
             * so their order can differ from the room-selection order. The ancillary update is
             * positional and can therefore apply a package to the wrong reservation. When the service
             * preserves reservation order, this scenario should be changed to expect 200 and the
             * basket reference.
             */
            scenario("PUT /v1/reservations/ancillaries exposes the multi-room package ordering bug") {
                val packagesByRoom =
                    listOf(
                        listOf(SelectedPackage(code = "HSCKIN")),
                        listOf(SelectedPackage(code = "HSCOU2")),
                    )
                val booking =
                    booking(
                        room("6003102", selectedPackagesAfterUpdate = packagesByRoom[0]),
                        room("6003103", selectedPackagesAfterUpdate = packagesByRoom[1]),
                    )

                installFor(booking)

                val createResult =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking, "add-two-packages"),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )
                createResult.attachEvidence("Create Two-Room Basket For Package Addition")

                expect("creates the two-room basket used by the update") {
                    createResult.response.status.value shouldBe 201
                    createResult.body.reservations.size shouldBe booking.rooms.size
                }

                val updateResult =
                    hotelReservationApi.updateReservationPackages(
                        request =
                            updateReservationPackagesRequest(
                                booking = booking,
                                basketReference = createResult.body.basketReference,
                                currentPackages = packagesByRoom,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReservationPackagesFlagPins,
                    )
                updateResult.attachEvidence("Add Packages To Two Reservations")

                expect("documents the known reservation-to-package ordering failure") {
                    updateResult.response.status.value shouldBe 500
                }
            }

            scenario("PUT /v1/reservations/ancillaries replaces an existing package") {
                val previousPackage = SelectedPackage(code = "CARPRK")
                val packageToAdd = SelectedPackage(code = "BFADBF")
                val booking =
                    booking(
                        room(
                            "6003104",
                            selectedPackages = listOf(previousPackage),
                            selectedPackagesAfterUpdate = listOf(packageToAdd),
                        ),
                    )

                installFor(booking)

                val createResult =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking, "replace-one-package"),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )
                createResult.attachEvidence("Create Basket For Package Replacement")

                expect("creates the basket used by the override") {
                    createResult.response.status.value shouldBe 201
                }

                val updateResult =
                    hotelReservationApi.updateReservationPackages(
                        request =
                            updateReservationPackagesRequest(
                                booking = booking,
                                basketReference = createResult.body.basketReference,
                                currentPackages = listOf(listOf(packageToAdd)),
                                previousPackages = listOf(listOf(previousPackage)),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReservationPackagesFlagPins,
                    )
                updateResult.attachEvidence("Replace Existing Package")

                expect("returns the basket reference after replacing the package") {
                    updateResult.response.status.value shouldBe 200
                    updateResult.body.basketReference shouldBe createResult.body.basketReference
                }
            }

            scenario("PUT /v1/reservations/ancillaries removes a package without adding another") {
                val previousPackage = SelectedPackage(code = "CARPRK")
                val booking =
                    booking(
                        room(
                            "6003108",
                            selectedPackages = listOf(previousPackage),
                            selectedPackagesAfterUpdate = emptyList(),
                        ),
                    )

                installFor(booking)

                val createResult =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking, "remove-one-package"),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )
                createResult.attachEvidence("Create Basket For Package Removal")

                expect("creates the basket used by the removal") {
                    createResult.response.status.value shouldBe 201
                }

                val updateResult =
                    hotelReservationApi.updateReservationPackages(
                        request =
                            updateReservationPackagesRequest(
                                booking = booking,
                                basketReference = createResult.body.basketReference,
                                currentPackages = listOf(emptyList()),
                                previousPackages = listOf(listOf(previousPackage)),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReservationPackagesFlagPins,
                    )
                updateResult.attachEvidence("Remove Package Without Replacement")

                expect("returns the basket reference after removing the package") {
                    updateResult.response.status.value shouldBe 200
                    updateResult.body.basketReference shouldBe createResult.body.basketReference
                }
            }

            scenario("PUT /v1/reservations/ancillaries expands a package group before updating Opera") {
                val packageGroup = SelectedPackage(code = "MDP")
                val expandedPackages =
                    Hotels.HEAPTI.packageCatalogue
                        ?.packages
                        ?.single { it.code == packageGroup.code }
                        ?.composition
                        ?.members
                        ?.map { member -> SelectedPackage(code = member.code) }
                        ?: error("MDP package composition is required by this scenario")
                val booking = booking(room("6003109", selectedPackagesAfterUpdate = expandedPackages))

                installFor(booking)

                val createResult =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking, "expand-package-group"),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )
                createResult.attachEvidence("Create Basket For Package Group Expansion")

                expect("creates the basket used by the package-group update") {
                    createResult.response.status.value shouldBe 201
                }

                val updateResult =
                    hotelReservationApi.updateReservationPackages(
                        request =
                            updateReservationPackagesRequest(
                                booking = booking,
                                basketReference = createResult.body.basketReference,
                                currentPackages = listOf(listOf(packageGroup)),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReservationPackagesFlagPins,
                    )
                updateResult.attachEvidence("Expand Package Group And Update Opera")

                expect("returns the basket reference after adding every package-group member") {
                    updateResult.response.status.value shouldBe 200
                    updateResult.body.basketReference shouldBe createResult.body.basketReference
                }
            }

            scenario("PUT /v1/reservations/ancillaries sets the early check-in arrival time") {
                val earlyCheckIn = SelectedPackage(code = "HSCKIN")
                val booking = booking(room("6003110", selectedPackagesAfterUpdate = listOf(earlyCheckIn)))

                installFor(booking)

                val createResult =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking, "early-check-in"),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )
                createResult.attachEvidence("Create Basket For Early Check-In")

                expect("creates the basket used by the early check-in update") {
                    createResult.response.status.value shouldBe 201
                }

                val updateResult =
                    hotelReservationApi.updateReservationPackages(
                        request =
                            updateReservationPackagesRequest(
                                booking = booking,
                                basketReference = createResult.body.basketReference,
                                currentPackages = listOf(listOf(earlyCheckIn)),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReservationPackagesFlagPins,
                    )
                updateResult.attachEvidence("Set Early Check-In Arrival Time")

                expect("returns the basket reference after setting the 11:00 arrival") {
                    updateResult.response.status.value shouldBe 200
                    updateResult.body.basketReference shouldBe createResult.body.basketReference
                }
            }

            scenario("PUT /v1/reservations/ancillaries sets the late check-out departure time") {
                val lateCheckOut = SelectedPackage(code = "HSCOU2")
                val booking = booking(room("6003111", selectedPackagesAfterUpdate = listOf(lateCheckOut)))

                installFor(booking)

                val createResult =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking, "late-check-out"),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )
                createResult.attachEvidence("Create Basket For Late Check-Out")

                expect("creates the basket used by the late check-out update") {
                    createResult.response.status.value shouldBe 201
                }

                val updateResult =
                    hotelReservationApi.updateReservationPackages(
                        request =
                            updateReservationPackagesRequest(
                                booking = booking,
                                basketReference = createResult.body.basketReference,
                                currentPackages = listOf(listOf(lateCheckOut)),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReservationPackagesFlagPins,
                    )
                updateResult.attachEvidence("Set Late Check-Out Departure Time")

                expect("returns the basket reference after setting the 14:00 departure") {
                    updateResult.response.status.value shouldBe 200
                    updateResult.body.basketReference shouldBe createResult.body.basketReference
                }
            }

            scenario("PUT /v1/reservations/ancillaries replaces the donation stored in Opera") {
                val previousDonation = SelectedPackage(code = "ZCHRY1")
                val newDonation = SelectedPackage(code = "ZCHRY2")
                val booking =
                    booking(
                        room(
                            "6003112",
                            selectedPackages = listOf(previousDonation),
                            selectedPackagesAfterUpdate = listOf(newDonation),
                        ),
                    )

                installFor(booking)

                val createResult =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking, "replace-donation"),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )
                createResult.attachEvidence("Create Basket For Donation Replacement")

                expect("creates the basket containing the existing donation") {
                    createResult.response.status.value shouldBe 201
                }

                val updateResult =
                    hotelReservationApi.updateReservationPackages(
                        request =
                            updateReservationPackagesRequest(
                                booking = booking,
                                basketReference = createResult.body.basketReference,
                                currentPackages = listOf(listOf(newDonation)),
                                previousPackages = listOf(emptyList()),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReservationPackagesFlagPins,
                    )
                updateResult.attachEvidence("Replace Existing Donation")

                expect("returns the basket reference after replacing the donation") {
                    updateResult.response.status.value shouldBe 200
                    updateResult.body.basketReference shouldBe createResult.body.basketReference
                }
            }

            scenario("PUT /v1/reservations/ancillaries ignores caller supplied reservation ids") {
                val booking = booking(room("6003105"))

                installFor(booking)

                val createResult =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking, "ignore-caller-reservation-id"),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )
                createResult.attachEvidence("Create Basket For Reservation Id Resolution")

                expect("creates the basket containing the real reservation id") {
                    createResult.response.status.value shouldBe 201
                }

                val updateResult =
                    hotelReservationApi.updateReservationPackages(
                        request =
                            updateReservationPackagesRequest(
                                booking = booking,
                                basketReference = createResult.body.basketReference,
                                currentPackages = listOf(listOf(SelectedPackage(code = "BFADBF"))),
                                reservationsId = listOf("caller-supplied-id"),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReservationPackagesFlagPins,
                    )
                updateResult.attachEvidence("Ignore Caller Supplied Reservation Id")

                expect("uses the reservation id from the basket") {
                    updateResult.response.status.value shouldBe 200
                    updateResult.body.basketReference shouldBe createResult.body.basketReference
                }
            }

            scenario("PUT /v1/reservations/ancillaries succeeds when no packages change") {
                val booking = booking(room("6003106"))

                installFor(
                    booking,
                    excluded =
                        setOf(
                            OPERA_PACKAGE_GROUP_STUB_ID,
                            OPERA_PACKAGES_LIST_STUB_ID,
                            OPERA_RESERVATION_GET_STUB_ID,
                        ),
                )

                val createResult =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking, "no-package-changes"),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )
                createResult.attachEvidence("Create Basket For No-Op Package Update")

                expect("creates the basket used by the no-op update") {
                    createResult.response.status.value shouldBe 201
                }

                val updateResult =
                    hotelReservationApi.updateReservationPackages(
                        request =
                            updateReservationPackagesRequest(
                                booking = booking,
                                basketReference = createResult.body.basketReference,
                                currentPackages = listOf(emptyList()),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReservationPackagesFlagPins,
                    )
                updateResult.attachEvidence("No Package Changes")

                expect("returns success without Opera package processing") {
                    updateResult.response.status.value shouldBe 200
                    updateResult.body.basketReference shouldBe createResult.body.basketReference
                }
            }

            scenario("PUT /v1/reservations/ancillaries returns not found for an unknown basket") {
                val booking = booking(room("6003107"))
                val missingBasketReference = "missing-basket-$testId"

                val updateResult =
                    hotelReservationApi.updateReservationPackages(
                        request =
                            updateReservationPackagesRequest(
                                booking = booking,
                                basketReference = missingBasketReference,
                                currentPackages = listOf(listOf(SelectedPackage(code = "BFADBF"))),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReservationPackagesFlagPins,
                    )
                updateResult.attachEvidence("Unknown Basket Package Update")

                expect("returns basket not found") {
                    updateResult.response.status.value shouldBe 404
                }
            }
        },
    )

private fun booking(vararg rooms: BookingRoom): Booking {
    val rate =
        Rate(
            ratePlan = "SEMIFLEX",
            roomType = "VPPDBL",
            adults = 1,
        )
    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = departure,
        rooms = rooms.toList(),
    )
}

private fun room(
    reservationId: String,
    selectedPackages: List<SelectedPackage> = emptyList(),
    selectedPackagesAfterUpdate: List<SelectedPackage>? = null,
): BookingRoom =
    BookingRoom(
        reservationId = reservationId,
        roomType = "VPPDBL",
        adults = 1,
        selectedPackages = selectedPackages,
        selectedPackagesAfterUpdate = selectedPackagesAfterUpdate,
    )

private fun createReservationRequest(
    booking: Booking,
    bookingFlowId: String,
): CreateReservationRequest {
    val rate = booking.hotel.availableRates.single()
    val arrivalDate = requireNotNull(booking.arrival).toString()
    val departureDate = requireNotNull(booking.departure).toString()
    return CreateReservationRequest(
        reservations =
            booking.rooms.map { room ->
                CreateReservationRoomRequest(
                    hotelId = booking.hotel.hotelId,
                    arrival = arrivalDate,
                    departure = departureDate,
                    adultsNumber = requireNotNull(room.adults),
                    childrenNumber = room.children,
                    roomRates =
                        CreateReservationRoomRateRequest(
                            ratePlanCode = rate.ratePlan,
                            pmsRoomType = requireNotNull(room.roomType),
                            specialRequests = listOf("SING"),
                            startDate = arrivalDate,
                            endDate = departureDate,
                        ),
                )
            },
        bookingChannel =
            CreateReservationBookingChannel(
                channel = "PI",
                subchannel = "WEB",
                language = "EN",
            ),
        bookingFlowId = bookingFlowId,
    )
}

private fun updateReservationPackagesRequest(
    booking: Booking,
    basketReference: String,
    currentPackages: List<List<SelectedPackage>>,
    previousPackages: List<List<SelectedPackage>>? = null,
    reservationsId: List<String>? = null,
): UpdateReservationPackagesRequest {
    require(currentPackages.size == booking.rooms.size)
    require(previousPackages == null || previousPackages.size == booking.rooms.size)
    return UpdateReservationPackagesRequest(
        basketReferenceId = basketReference,
        hotelId = booking.hotel.hotelId,
        arrivalDate = requireNotNull(booking.arrival).toString(),
        departureDate = requireNotNull(booking.departure).toString(),
        reservationsId = reservationsId,
        roomsSelections = currentPackages.map(::roomPackageSelections),
        previousRoomsSelections = previousPackages?.map(::roomPackageSelections),
    )
}

private fun roomPackageSelections(packages: List<SelectedPackage>): ReservationRoomPackageSelections =
    ReservationRoomPackageSelections(
        packagesSelection =
            packages.map { selectedPackage ->
                ReservationPackageSelection(
                    id = selectedPackage.code,
                    noOfSelections = selectedPackage.quantity,
                )
            },
    )
