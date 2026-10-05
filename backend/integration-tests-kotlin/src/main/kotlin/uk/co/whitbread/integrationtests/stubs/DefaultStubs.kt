package uk.co.whitbread.integrationtests.stubs

import uk.co.whitbread.integrationtests.stubs.aem.allHotels
import uk.co.whitbread.integrationtests.stubs.aem.cookiePolicies
import uk.co.whitbread.integrationtests.stubs.aem.footer
import uk.co.whitbread.integrationtests.stubs.aem.globalConfig
import uk.co.whitbread.integrationtests.stubs.aem.hotelDetails
import uk.co.whitbread.integrationtests.stubs.aem.indexHeaderData
import uk.co.whitbread.integrationtests.stubs.aem.roomType
import uk.co.whitbread.integrationtests.stubs.aem.searchResultsData
import uk.co.whitbread.integrationtests.stubs.cdh.accountSpend
import uk.co.whitbread.integrationtests.stubs.cdh.cdhRegistrationDetails
import uk.co.whitbread.integrationtests.stubs.cdh.companySearch
import uk.co.whitbread.integrationtests.stubs.cdh.companySpend
import uk.co.whitbread.integrationtests.stubs.cdh.employeeSpend
import uk.co.whitbread.integrationtests.stubs.cdh.reservationSearch
import uk.co.whitbread.integrationtests.stubs.cdh.transactionAggregates
import uk.co.whitbread.integrationtests.stubs.datatrans.mobileSdkInit
import uk.co.whitbread.integrationtests.stubs.datatrans.secureFieldsInit
import uk.co.whitbread.integrationtests.stubs.opera.activityLog
import uk.co.whitbread.integrationtests.stubs.opera.addFileAttachment
import uk.co.whitbread.integrationtests.stubs.opera.cancelPolicyConfigs
import uk.co.whitbread.integrationtests.stubs.opera.cancelReservation
import uk.co.whitbread.integrationtests.stubs.opera.cancellationReasons
import uk.co.whitbread.integrationtests.stubs.opera.checkIn
import uk.co.whitbread.integrationtests.stubs.opera.cityTaxRateInfo
import uk.co.whitbread.integrationtests.stubs.opera.companiesProfileSearch
import uk.co.whitbread.integrationtests.stubs.opera.companyNegotiatedRates
import uk.co.whitbread.integrationtests.stubs.opera.companyProfiles
import uk.co.whitbread.integrationtests.stubs.opera.companyProfilesById
import uk.co.whitbread.integrationtests.stubs.opera.createCancellationPolicies
import uk.co.whitbread.integrationtests.stubs.opera.createProfiles
import uk.co.whitbread.integrationtests.stubs.opera.createReservation
import uk.co.whitbread.integrationtests.stubs.opera.creditCardInfo
import uk.co.whitbread.integrationtests.stubs.opera.deleteCancellationPolicies
import uk.co.whitbread.integrationtests.stubs.opera.deleteReservation
import uk.co.whitbread.integrationtests.stubs.opera.deleteReservationAttachment
import uk.co.whitbread.integrationtests.stubs.opera.deleteRoutingInstructions
import uk.co.whitbread.integrationtests.stubs.opera.depositFolios
import uk.co.whitbread.integrationtests.stubs.opera.donationPackagesDetails
import uk.co.whitbread.integrationtests.stubs.opera.hotelAvailability
import uk.co.whitbread.integrationtests.stubs.opera.hotelConfigs
import uk.co.whitbread.integrationtests.stubs.opera.hotelDetailsStatus
import uk.co.whitbread.integrationtests.stubs.opera.hotelInventory
import uk.co.whitbread.integrationtests.stubs.opera.hotelItemInventory
import uk.co.whitbread.integrationtests.stubs.opera.hotelPreferences
import uk.co.whitbread.integrationtests.stubs.opera.hotelRestaurants
import uk.co.whitbread.integrationtests.stubs.opera.housekeepingOverview
import uk.co.whitbread.integrationtests.stubs.opera.minimumRateAvailability
import uk.co.whitbread.integrationtests.stubs.opera.multiHotelAvailability
import uk.co.whitbread.integrationtests.stubs.opera.multiHotelNegotiatedAvailability
import uk.co.whitbread.integrationtests.stubs.opera.multiRoomRateAvailability
import uk.co.whitbread.integrationtests.stubs.opera.packageGroups
import uk.co.whitbread.integrationtests.stubs.opera.packagesList
import uk.co.whitbread.integrationtests.stubs.opera.policySchedules
import uk.co.whitbread.integrationtests.stubs.opera.preCheckInStatus
import uk.co.whitbread.integrationtests.stubs.opera.profiles
import uk.co.whitbread.integrationtests.stubs.opera.promotionCodes
import uk.co.whitbread.integrationtests.stubs.opera.rateInfoForAllHotels
import uk.co.whitbread.integrationtests.stubs.opera.ratePlanInfo
import uk.co.whitbread.integrationtests.stubs.opera.ratePlans
import uk.co.whitbread.integrationtests.stubs.opera.reservationAmounts
import uk.co.whitbread.integrationtests.stubs.opera.reservationDeposits
import uk.co.whitbread.integrationtests.stubs.opera.reservationFolios
import uk.co.whitbread.integrationtests.stubs.opera.reservationReadUpdateStubs
import uk.co.whitbread.integrationtests.stubs.opera.reservationsByExternalReference
import uk.co.whitbread.integrationtests.stubs.opera.restrictionsByDateRange
import uk.co.whitbread.integrationtests.stubs.opera.roomAssignment
import uk.co.whitbread.integrationtests.stubs.opera.roomTypes
import uk.co.whitbread.integrationtests.stubs.opera.updateProfiles
import uk.co.whitbread.integrationtests.stubs.opera.vacantRooms
import uk.co.whitbread.integrationtests.stubs.worldline.accountInfo
import uk.co.whitbread.integrationtests.stubs.worldline.paymentInfo
import uk.co.whitbread.integrationtests.stubs.worldline.tetheredUserDetails
import uk.co.whitbread.integrationtests.testkit.model.Booking

/**
 * Returns every default stub selected by the [booking] data.
 *
 * This file is the complete Booking-to-stubs table, one function per upstream system,
 * plus [selectedStubs] as the exclusion pass over that table. Each entry in those
 * functions pairs one visible Booking gate with the stub it selects. [selectedStubs] then
 * validates and applies exclusions.
 */
fun defaultStubsFor(booking: Booking): List<PlannedStub> =
    cdhStubs(booking) +
        worldlineStubs(booking) +
        datatransStubs(booking) +
        aemStubs(booking) +
        operaStubs(booking)

/**
 * Defaults selected by [booking], minus [excluded], preserving default order.
 *
 * Unknown excluded IDs throw [IllegalArgumentException] before any I/O, naming the offending
 * IDs and the IDs this Booking selected. To override an excluded default, install a custom
 * stub with its own unique stub ID through `installStub`.
 */
fun selectedStubs(
    booking: Booking,
    excluded: Set<String> = emptySet(),
): List<PlannedStub> {
    val defaults = defaultStubsFor(booking)
    val planIds = defaults.mapTo(LinkedHashSet()) { it.id }
    val unknownExcluded = excluded.filterNotTo(LinkedHashSet()) { it in planIds }
    require(unknownExcluded.isEmpty()) {
        "Unknown stub IDs in excluded: $unknownExcluded.\nThis Booking selected: $planIds."
    }
    return defaults.filterNot { it.id in excluded }
}

private fun cdhStubs(booking: Booking): List<PlannedStub> =
    buildList {
        if (booking.cdhBookingReference != null) {
            add(reservationSearch(booking))
        }
        if (booking.companies.isNotEmpty()) {
            add(companySearch(booking.companies))
        }
        val user = booking.loggedUser ?: return@buildList
        user.companySpend?.let { add(companySpend(user, it)) }
        user.employeeSpend?.let { add(employeeSpend(user, it)) }
        val tetheredAccount = user.tetheredAccount ?: return@buildList
        add(cdhRegistrationDetails(user, tetheredAccount))
        tetheredAccount.accountSpend?.let { add(accountSpend(tetheredAccount, it)) }
        val aggregates = tetheredAccount.accountActivity?.transactionAggregates.orEmpty()
        if (aggregates.isNotEmpty()) {
            add(transactionAggregates(tetheredAccount, aggregates))
        }
    }

private fun worldlineStubs(booking: Booking): List<PlannedStub> =
    buildList {
        val account = booking.loggedUser?.tetheredAccount ?: return@buildList
        add(tetheredUserDetails(account))
        account.accountActivity?.worldlineAccount?.let { add(accountInfo(account, it)) }
        account.accountActivity?.paymentHistory?.let { add(paymentInfo(account, it.payments)) }
    }

private fun datatransStubs(booking: Booking): List<PlannedStub> =
    buildList {
        if (
            booking.cardPayment != null &&
            booking.hotels.isNotEmpty() &&
            booking.rooms.isNotEmpty() &&
            booking.arrival != null &&
            booking.departure != null
        ) {
            add(secureFieldsInit(booking))
            // Both payment channels are installed for a card-payment booking: the Booking says the
            // guest is about to pay by card, not which client they are paying from.
            add(mobileSdkInit(booking))
        }
    }

private fun aemStubs(booking: Booking): List<PlannedStub> =
    buildList {
        booking.aem?.footer?.let { add(footer(it)) }
        booking.aem?.indexHeaderData?.let { add(indexHeaderData(it)) }
        booking.aem?.searchResultsData?.let { add(searchResultsData(it)) }
        booking.aem?.globalConfig?.let { add(globalConfig(it)) }
        booking.aem?.roomType?.let { add(roomType(it)) }
        booking.aem?.cookiePolicies?.let { add(cookiePolicies(it)) }
        if (booking.hotels.isNotEmpty()) {
            add(allHotels(booking))
            add(hotelDetails(booking.hotels))
        }
    }

private fun operaStubs(booking: Booking): List<PlannedStub> =
    buildList {
        val hotel = booking.hotels.firstOrNull()
        val availableRates = hotel?.availableRates.orEmpty()
        val promotionalRates = availableRates.filter { rate -> rate.promotionCode != null }
        val hasStay = booking.arrival != null && booking.departure != null
        val requestableRooms =
            booking.rooms.filter { room ->
                hasStay && room.roomType != null && room.adults != null
            }
        val roomsWithReservationIds = booking.rooms.filter { room -> room.reservationId != null }
        val reservationRooms = requestableRooms.filter { room -> room.reservationId != null }
        val hasReservationData =
            requestableRooms.isNotEmpty() && reservationRooms.size == requestableRooms.size
        // Multi-hotel availability data: rooms to search, no reservations yet. The single-hotel
        // variant additionally requires exactly one hotel because the per-hotel availability
        // stub models Opera's single-property search.
        val hasMultiHotelAvailabilityData =
            booking.hotels.isNotEmpty() && requestableRooms.isNotEmpty() && reservationRooms.isEmpty()
        val hasAvailabilityData = hasMultiHotelAvailabilityData && booking.hotels.size == 1
        val hasReservationAvailabilityData =
            hasReservationData &&
                booking.hotels.size == 1 &&
                availableRates.isNotEmpty() &&
                hotel?.availableRoomTypes.orEmpty().isNotEmpty()

        if (hotel != null) {
            add(hotelConfigs(booking.hotels))
            add(roomTypes(booking.hotels))
        }

        val hotelsWithCancellationReasons = booking.hotels.filter { it.cancellationReasons != null }
        if (hotelsWithCancellationReasons.isNotEmpty()) {
            add(cancellationReasons(hotelsWithCancellationReasons))
        }

        val hotelsWithPolicyRules = booking.hotels.filter { it.cancellationPolicyRules.isNotEmpty() }
        if (hotelsWithPolicyRules.isNotEmpty()) {
            add(policySchedules(hotelsWithPolicyRules))
            add(cancelPolicyConfigs(hotelsWithPolicyRules))
        }

        val hotelsWithPromotionCodes = booking.hotels.filter { it.promotionCodes.isNotEmpty() }
        if (hotelsWithPromotionCodes.isNotEmpty()) {
            add(promotionCodes(hotelsWithPromotionCodes))
        }

        val hotelsWithOnSaleStatus = booking.hotels.filter { it.onSaleStatus != null }
        if (hotelsWithOnSaleStatus.isNotEmpty()) {
            add(hotelDetailsStatus(hotelsWithOnSaleStatus))
        }

        val hotelsWithPreferenceGroups = booking.hotels.filter { it.preferenceGroups.isNotEmpty() }
        if (hotelsWithPreferenceGroups.isNotEmpty()) {
            add(hotelPreferences(hotelsWithPreferenceGroups))
        }

        val hotelsWithPhysicalRooms = booking.hotels.filter { it.physicalRooms.isNotEmpty() }
        if (hotelsWithPhysicalRooms.isNotEmpty()) {
            add(vacantRooms(hotelsWithPhysicalRooms))
            add(housekeepingOverview(hotelsWithPhysicalRooms))
        }

        if (hasStay && booking.hotels.any { it.restrictions.isNotEmpty() }) {
            add(restrictionsByDateRange(booking))
        }

        if (
            hotel != null &&
            hasStay &&
            (hasMultiHotelAvailabilityData || booking.hotels.any { it.availableRoomTypes.isNotEmpty() })
        ) {
            add(hotelInventory(booking))
        }

        if (hasStay && booking.hotels.any { it.itemInventory != null }) {
            add(hotelItemInventory(booking))
        }

        if (hotel != null && (hasAvailabilityData || hasReservationAvailabilityData)) {
            add(hotelAvailability(booking))
        }

        if (hotel != null && hasMultiHotelAvailabilityData) {
            val ratesWithoutPlanSet =
                booking.hotels
                    .flatMap { it.availableRates }
                    .filter { rate ->
                        rate.promotionCode == null && rate.ratePlanSet.isNullOrBlank()
                    }
            require(ratesWithoutPlanSet.isEmpty()) {
                "Availability rates must declare ratePlanSet: " +
                    ratesWithoutPlanSet.joinToString { rate -> rate.ratePlan }
            }
            add(multiHotelAvailability(booking))
            if (
                booking.companies.isNotEmpty() &&
                booking.hotels.any { h -> h.availableRates.any { it.ratePlanSet == "NEGOTIATED" } }
            ) {
                add(multiHotelNegotiatedAvailability(booking))
            }
            add(minimumRateAvailability(booking))
            if (booking.hotels.any { it.availableRates.isNotEmpty() }) {
                add(multiRoomRateAvailability(booking))
            }
        }

        val hotelsWithRates = booking.hotels.filter { it.availableRates.isNotEmpty() }
        if (
            hotelsWithRates.isNotEmpty() &&
            (hasReservationData || hasMultiHotelAvailabilityData)
        ) {
            add(rateInfoForAllHotels(booking))
        }
        if (hotelsWithRates.isNotEmpty()) {
            add(ratePlans(hotelsWithRates))
        }
        if (hotel != null && promotionalRates.isNotEmpty()) {
            add(ratePlanInfo(hotel, promotionalRates))
        }

        if (hotel != null && hasReservationData) {
            add(createReservation(booking))
        }

        val packages = hotel?.packageCatalogue?.packages.orEmpty()
        val donationPackages = hotel?.packageCatalogue?.donationPackages.orEmpty()
        val hasSelectedPackages = requestableRooms.any { room -> room.selectedPackages.isNotEmpty() }
        val hasPackageCatalogue = hotel?.packageCatalogue != null && requestableRooms.isNotEmpty()
        val hasPackageGroups = packages.isNotEmpty() || hasSelectedPackages

        if (hotel != null && hasPackageCatalogue) {
            add(hotelRestaurants(hotel))
        }
        if (hasPackageGroups) {
            add(packageGroups(booking))
        }
        if (hasPackageCatalogue) {
            add(packagesList(booking, requestableRooms))
        }
        if (hotel != null && donationPackages.isNotEmpty()) {
            add(donationPackagesDetails(hotel))
        }

        if (hotel != null && reservationRooms.isNotEmpty()) {
            val reservationReadUpdates = reservationReadUpdateStubs(booking, reservationRooms)
            add(reservationReadUpdates.reads)
            add(reservationReadUpdates.updates)
            add(reservationAmounts(booking, reservationRooms))
            add(reservationFolios(booking, reservationRooms))
            add(depositFolios(booking, reservationRooms))
            add(reservationDeposits(booking, reservationRooms))
            add(activityLog(booking, reservationRooms))
            add(addFileAttachment(booking, reservationRooms))
            add(roomAssignment(booking, reservationRooms))
            val preCheckInRooms = reservationRooms.filter { room -> room.preCheckInAvailable }
            if (preCheckInRooms.isNotEmpty()) {
                add(preCheckInStatus(booking, preCheckInRooms))
            }
            val assignedRoomRooms = reservationRooms.filter { room -> room.assignedRoomId != null }
            if (assignedRoomRooms.isNotEmpty()) {
                add(checkIn(booking, assignedRoomRooms))
            }
        }

        if (hotel != null && roomsWithReservationIds.isNotEmpty()) {
            add(cancelReservation(booking, roomsWithReservationIds))
            add(deleteReservation(booking, roomsWithReservationIds))
            val regCardRooms =
                roomsWithReservationIds.filter { room ->
                    room.preRegistration?.regCardAttachmentId != null
                }
            if (regCardRooms.isNotEmpty()) {
                add(deleteReservationAttachment(booking, regCardRooms))
            }
            val routedInstructionRooms =
                roomsWithReservationIds.filter { room ->
                    room.routingInstructions.any { it.instructions.isNotEmpty() }
                }
            if (routedInstructionRooms.isNotEmpty()) {
                add(deleteRoutingInstructions(booking, routedInstructionRooms))
            }
        }

        if (hotel != null && reservationRooms.isNotEmpty()) {
            add(deleteCancellationPolicies(booking, reservationRooms))
            add(createCancellationPolicies(booking, reservationRooms))
            val cityTaxRooms =
                reservationRooms.filter { room ->
                    room.selectedPackages.any { selected -> selected.code == "CITYTAX" }
                }
            if (cityTaxRooms.isNotEmpty()) {
                add(cityTaxRateInfo(booking, cityTaxRooms))
            }
            val cardRooms = reservationRooms.filter { room -> room.operaPaymentCard != null }
            if (cardRooms.isNotEmpty()) {
                add(creditCardInfo(booking, cardRooms))
            }
        }

        if (booking.bookingReference != null || booking.cdhBookingReference != null) {
            add(reservationsByExternalReference(booking, reservationRooms))
        }

        val roomsWithProfiles =
            booking.rooms.filter { it.guestProfile != null || it.accompanyingGuestProfile != null }
        if (roomsWithProfiles.isNotEmpty()) {
            add(profiles(roomsWithProfiles))
        }
        // The profile-create capability also covers the company creates, so it installs for a
        // Booking that knows companies even when no room states a guest profile.
        val roomsWithOwnGuestProfiles = roomsWithProfiles.filter { it.guestProfile != null }
        if (hotel != null && (roomsWithOwnGuestProfiles.isNotEmpty() || booking.companies.isNotEmpty())) {
            add(createProfiles(booking, roomsWithOwnGuestProfiles))
        }
        // The profile-amend capability also covers company profiles, so it installs for a
        // Booking that knows companies even when no room states a guest profile.
        if (roomsWithProfiles.isNotEmpty() || booking.companies.isNotEmpty()) {
            add(updateProfiles(booking, roomsWithProfiles))
        }

        if (booking.companies.isNotEmpty()) {
            add(companyProfiles(booking.companies))
            add(companyProfilesById(booking.companies))
            if (hotel != null) {
                add(companiesProfileSearch(booking))
            }
            add(companyNegotiatedRates(booking.companies))
        }
    }
