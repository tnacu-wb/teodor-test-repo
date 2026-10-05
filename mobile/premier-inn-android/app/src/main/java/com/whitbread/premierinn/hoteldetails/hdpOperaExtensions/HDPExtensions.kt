package com.whitbread.premierinn.hoteldetails.hdpOperaExtensions

import com.google.gson.Gson
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.toParcelable
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.businessbooker.domain.company.PriceCapLocations
import com.whitbread.premierinn.common.RoomBooking
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.remote.AllowedBrandsInfo
import com.whitbread.premierinn.domain.common.ACCESSIBLE_ROOM_CODE
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.LOWB
import com.whitbread.premierinn.domain.common.RatePlanOpera
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.STANDARD_ROOM_CLASS_OPERA
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.common.TWIN_ROOM_CODE
import com.whitbread.premierinn.domain.common.WETR
import com.whitbread.premierinn.domain.common.hoteldetails.entity.ImportantInfoDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.TopSectionImageDomain
import com.whitbread.premierinn.domain.common.toRoomStringGQL
import com.whitbread.premierinn.domain.graphql.hdp.entity.RateClassificationsDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomTypeDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomTypeInfoDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomSearch
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.hoteldetails.ParcelableRatePlanOpera
import com.whitbread.premierinn.hoteldetails.ParcelableRoomOpera
import com.whitbread.premierinn.hoteldetails.ParcelableRoomOptions
import com.whitbread.premierinn.hoteldetails.ParcelableRoomType
import com.whitbread.premierinn.hoteldetails.RatesDisplayInfo
import com.whitbread.premierinn.hoteldetails.facilities.RoomFeaturesModel
import com.whitbread.premierinn.hoteldetails.hotelfulldescription.RoomRatePlan
import com.whitbread.premierinn.hoteldetails.uimodel.CheckInOutUiModel
import com.whitbread.premierinn.hoteldetails.uimodel.RateBoxUiModel
import com.whitbread.premierinn.searchresults.SearchResultsInput
import com.whitbread.premierinn.summary.SummaryInput
import com.whitbread.premierinn.summary.toListOfReservations
import io.reactivex.functions.Predicate
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

@JvmField
val TAGS_RESTAURANT = listOf(
    "bar", "beefeater", "brewers-fayre", "coffee-shop", "food", "orange-cow", "restaurant",
    "tgi-fridays", "table-table", "thyme", "whitbread-inn", "breakfast", "family"
)

@JvmField
val FILTER_RESTAURANT_IMAGES_BY_TAG =
    Predicate { pair: kotlin.Pair<TopSectionImageDomain?, String?> ->
        TAGS_RESTAURANT.contains(
            pair.second
        )
    }

fun String.isAccessibleRoom(): Boolean {
    return this == ACCESSIBLE_ROOM_CODE
}

fun String.isTwinRoom(): Boolean {
    return this == TWIN_ROOM_CODE
}

fun List<RoomTypeInfoDomain>.findRoomLabelFromRoomTypeInfo(pmsRoomType: String, roomClass: String) : String {
    if (roomClass == STANDARD_ROOM_CLASS_OPERA) return EMPTY_STRING_DOMAIN
    val findMatchingPmsRoomType = this.find { i -> i.roomTypeCode.contains(pmsRoomType) }

    return findMatchingPmsRoomType?.roomLabel ?: EMPTY_STRING_DOMAIN
}

fun convertToRoomSearch(size: Int, adultsList: List<Int>, childList: List<Int>,
                        cotReqd: List<Boolean>, roomTypes: List<String>) : List<RoomSearch>{
    val roomSearchList = mutableListOf<RoomSearch>()
    for ( i in 0 until size) {
        roomSearchList.add(RoomSearch(adultsList[i], childList[i], cotReqd[i], roomTypes[i]))
    }

    return roomSearchList
}

fun List<RoomCriteria>.toRoomSearchCriteria() : List<RoomSearch> {
    val roomSearchList = mutableListOf<RoomSearch>()
    for (roomCriteria in this) {
        roomSearchList.add(RoomSearch(roomCriteria.numberOfAdults, roomCriteria.numberOfChildren, roomCriteria.includeCot,
            roomCriteria.roomType.toRoomStringGQL()))
    }

    return roomSearchList
}

fun List<RoomBooking>.totalAdults(roomNumber: Int):Int {
    var adults = 0
    val findMatchingRoom = this.find { it.roomNumber == roomNumber }
    adults += findMatchingRoom?.adults ?: 0

    return adults
}

fun List<RoomBooking>.totalChildrenOpera(roomNumber: Int):Int {
    var children = 0
    val findMatchingRoom = this.find { it.roomNumber == roomNumber }
    children += findMatchingRoom?.children ?: 0

    return children
}

fun buildCheckInOutModel(order: Int = 0, checkIn: String, checkOut: String): CheckInOutUiModel = CheckInOutUiModel(order = order, checkIn = checkIn, checkOut = checkOut)

fun getPremierInnRoomFeatures(stringResourceProvider: StringResourceProvider): List<RoomFeaturesModel>  = listOf(
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.premierInnRoomFacilityHighPoweredShowers)),
    RoomFeaturesModel(R.drawable.ic_wifi, stringResourceProvider.getString(R.string.premierInnRoomFacilityFreeWiFi)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.premierInnRoomFacilityDesk)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.premierInnRoomFacilityPillows)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.premierInnRoomFacilityTeaAndCoffee)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.premierInnRoomFacilityBed)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.premierInnRoomFacilityDuvet)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.premierInnRoomFacilityFreeview)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.premierInnRoomFacilityCurtains)),
    RoomFeaturesModel(R.drawable.ic_aircon, stringResourceProvider.getString(R.string.premierInnRoomFacilityAirConditioning))
)

fun getHubFacilities(stringResourceProvider: StringResourceProvider): List<RoomFeaturesModel> = listOf(
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.hubRoomFacilityShower)),
    RoomFeaturesModel(R.drawable.ic_wifi, stringResourceProvider.getString(R.string.hubRoomFacilityWifi)),
    RoomFeaturesModel(R.drawable.ic_double_room, stringResourceProvider.getString(R.string.hubRoomFacilityBed)),
    RoomFeaturesModel(R.drawable.ic_touchscreen_lighting, stringResourceProvider.getString(R.string.hubRoomFacilityRoomControls)),
    RoomFeaturesModel(R.drawable.ic_aircon, stringResourceProvider.getString(R.string.hubRoomFacilityAirConditioning)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.hubRoomFacilityDuvet)),
    RoomFeaturesModel(R.drawable.ic_smart_40_tv, stringResourceProvider.getString(R.string.hubRoomFacilityTV)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.hubRoomFacilityDesk)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.hubRoomFacilityChargingPoints)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.hubRoomFacilityMirror)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.hubRoomFacilityUnderbedStorage)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.hubRoomFacilityHairdryer)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.hubRoomFacilityCurtains))
)

fun getZipFacilities(stringResourceProvider: StringResourceProvider): List<RoomFeaturesModel> = listOf(
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.zipRoomFacilityShower)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.zipRoomFacilityTowelAndBodywash)),
    RoomFeaturesModel(R.drawable.ic_wifi, stringResourceProvider.getString(R.string.zipRoomFacilityWifi)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.zipRoomFacilityBed)),
    RoomFeaturesModel(R.drawable.ic_aircon, stringResourceProvider.getString(R.string.zipRoomFacilityAirConditioning)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.zipRoomFacilityTV)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.zipRoomFacilityStorage)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.zipRoomFacilityMirror)),
    RoomFeaturesModel(R.drawable.ic_available, stringResourceProvider.getString(R.string.zipRoomFacilityWindow))
)

fun List<RateClassificationsDomain>.getRateNameAndRateDescPairFromRateClassification(rateType: String): Pair<String, String> {
    this.find { it.rateClassification == rateType }?.let {
        return Pair(it.rateName, it.rateDescription)
    } ?: return Pair(EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
}

fun List<RateBoxUiModel>.filterDuplicates(): List<RateBoxUiModel> {
    return this.distinct()
}

fun List<RoomBooking>.convertToMapOfRoomClassToPrice(): Map<String, Float> {
    //type is roomclass here
    return this
        .groupBy { it.type }
        .mapValues { (_, bookingsInRoomClass) ->
            // We need to group by roomnumber
            // since premier plus acc we have same roomclass and multiple options
//            1 room may return. Although ideally we were expecting each roomclass
//            to return only one option
//            Flex                    Semi flex               Std
//            ppdlow - PP - 79        ppdlow - PP - 71        ppdlow - PP - 59
//            ppdwet - pp - 79        ppdwet - pp - 71        ppdwet  - PP - 59
            bookingsInRoomClass
                .groupBy { it.roomNumber }
                .map { (_, bookingsForRoomNumber) ->
                    bookingsForRoomNumber.firstOrNull()?.totalRoomPrice(false)?.amount ?: 0.0f
                }
                .sum()
        }
}

fun List<RatesDisplayInfo>.filterAndReturnOnlyOneRateDisplay(): List<RatesDisplayInfo> {
    return this.distinctBy { Pair(it.rateCode , it.getRoomClass())}
}

fun Map<String, Float>.getPriceBasedOnPmsRoomType(pmsRoomType: String): Float {
    return this[pmsRoomType] ?: 0.0f
}

fun List<RatePlanOpera>.filterRateForSelectedPmsRoomTypeForAlternateRooms(roomClass: String, isAlternateRoomSelected: Boolean): List<RatePlanOpera> {
    val listOfUpdatedRatePlanOpera = mutableListOf<RatePlanOpera>()

    if (isAlternateRoomSelected) {
        this.forEach { ratePlanOpera ->
            // here letting type is room class
            val matchedAltRooms =
                ratePlanOpera.alternateRoomList.filter { it.lettingType == roomClass }
            val updatedRatePlan =
                ratePlanOpera.copy(roomList = matchedAltRooms, alternateRoomList = matchedAltRooms)
            listOfUpdatedRatePlanOpera.add(updatedRatePlan)
        }
    } else {
        return this
    }

    return listOfUpdatedRatePlanOpera
}

fun List<ParcelableRatePlanOpera>.addAccRoomFromStdIfAlternateRoomFlow(ratePlanCode: String,
                                                                       isAlternateRoomSelected: Boolean):  List<ParcelableRoomOpera> {
    val filterRatePlanBasedOnRateplanCode = this.filterRatePlanBasedOnRatePlanCode(ratePlanCode)
    if (isAlternateRoomSelected) {
        val filterAccRoomInStdRoomsList = filterRatePlanBasedOnRateplanCode!!.roomList.filter { it.type == RoomType.ACCESSIBLE }
        if (filterAccRoomInStdRoomsList.isNotEmpty()) {
            filterRatePlanBasedOnRateplanCode.accessibleRoomList?.let {
                return it.filterAccRoomOnlyContainingSpecialReqSupported() + filterAccRoomInStdRoomsList
            } ?: return emptyList()
        } else {
            return filterRatePlanBasedOnRateplanCode.accessibleRoomList?.filterAccRoomOnlyContainingSpecialReqSupported() ?: emptyList()
        }
    } else {
        return filterRatePlanBasedOnRateplanCode!!.accessibleRoomList?.filterAccRoomOnlyContainingSpecialReqSupported() ?: emptyList()
    }
}

private fun List<ParcelableRatePlanOpera>.filterRatePlanBasedOnRatePlanCode(
    ratePlanCode: String
): ParcelableRatePlanOpera? {
    val findSelectedRatePlan = this.find { it.code == ratePlanCode }
    return findSelectedRatePlan
}

private fun List<ParcelableRoomOpera>.filterAccRoomOnlyContainingSpecialReqSupported(): List<ParcelableRoomOpera> {
    return this.filter { room ->
        room.specialRequests?.any { it == WETR || it == LOWB } == true
    }

}

fun List<RatePlanOpera>.findIfSpecialRequestContainsWetOrLow(): Boolean {
    return this.any { rateplan ->
        rateplan.accessibleRoomList?.any { room ->
            room.specialRequests?.any { it == WETR || it == LOWB } == true
        } == true
    }
}

fun List<RatePlanOpera>.toParcelableRatePlanOpera() : List<ParcelableRatePlanOpera> {
    return this.map { it.toParcelable() }
}

fun List<ParcelableRatePlanOpera>.toStdParcelableRoom(ratePlanCode: String) : List<ParcelableRoomOpera> {
    val listOfParcelableRoom = mutableListOf<ParcelableRoomOpera>()
    val findSelectedRatePlan = filterRatePlanBasedOnRatePlanCode(ratePlanCode)

    findSelectedRatePlan?.roomList?.forEach { eachRoom ->
        if (eachRoom.type != RoomType.ACCESSIBLE) {
            listOfParcelableRoom.add(eachRoom)
        }
    }
    return listOfParcelableRoom
}

fun List<ParcelableRatePlanOpera>.toTwinParcelableRoom(ratePlanCode: String) : List<ParcelableRoomOpera> {
    val listOfTwinRoom = mutableListOf<ParcelableRoomOpera>()
    val findSelectedRatePlan = this.find { it.code == ratePlanCode }
    findSelectedRatePlan?.twinRoomList?.forEach { eachRoom ->
        if (eachRoom.type == RoomType.TWIN) {
            listOfTwinRoom.add(eachRoom)
        }
    }
    return listOfTwinRoom
}

fun List<RoomTypeDomain>.toParcelableRoomType(): List<ParcelableRoomType> {
    val listOfParcelableRoomTypes = mutableListOf<ParcelableRoomType>()

    this.forEach { eachRoomType ->
        val listOfRoomOptions = mutableListOf<ParcelableRoomOptions>()

        eachRoomType.roomOptionsDomainList.forEach { eachRoomOption ->
            listOfRoomOptions.add(
                ParcelableRoomOptions(eachRoomOption.pmsRoomType, eachRoomOption.roomClass, eachRoomOption.specialRequests,
                    eachRoomOption.roomPriceBreakdownDomain.packageCode, eachRoomOption.roomPriceBreakdownDomain.packageAmount)
            )
        }
        listOfParcelableRoomTypes.add(
            ParcelableRoomType(eachRoomType.adults, eachRoomType.children, eachRoomType.cotRequested,
                eachRoomType.roomType, listOfRoomOptions)
        )
    }
    return listOfParcelableRoomTypes
}

fun List<RoomRatePlan>.returnTotalAmountForStandardRooms(rateType: String): Float {
    val grpByRateTypeStd = this.filter { !it.isAlternativeRoomUpsell }.groupBy { it.roomRate.rateType() }
    val grpByRateTypeAlt = this.filter { it.isAlternativeRoomUpsell }.groupBy { it.roomRate.rateType() }

    val listOfStdPlanWithRateType = grpByRateTypeStd[rateType] ?: emptyList()
    val listOfAltPlanWithRateType = grpByRateTypeAlt[rateType] ?: emptyList()

    val roomNumbersToExclude = listOfAltPlanWithRateType.map { it.rooms }.flatten().map { it.roomNumber }.toSet()
    val filteredRoomsNotInAlternateList = listOfStdPlanWithRateType.map { it.rooms }.flatten()
        .filter { room ->
            room.roomNumber !in roomNumbersToExclude
        }
    val total = filteredRoomsNotInAlternateList.sumOf {
        it.totalRoomPrice(false).amount.toDouble()
    }.toFloat()

    return total
}

fun BusinessPersistenceManager.getPriceCapLocations(): PriceCapLocations? {
    return this.getCompany()
        ?.requestedCompany
        ?.bookingAllowances
        ?.maxDinnerBudgets
}

fun getDinnerAllowance(
    priceCapLocations: PriceCapLocations,
    county: String?,
    country: String
): Float {
    val modifiedCounty = county?.replace("-", " ")?.lowercase()?.trim()
    val modifiedCountry = country.lowercase().trim()

    return when {
        modifiedCounty == "greater london" -> priceCapLocations.greaterLondon?.amount ?: 0f
        "united kingdom" in modifiedCountry -> priceCapLocations.uKWide?.amount ?: 0f
        else -> priceCapLocations.ireland?.amount ?: 0f
    }
}

fun filterImportantInfoByDate(
    importantInfo: ImportantInfoDomain?, input: SearchResultsInput
): ImportantInfoDomain? {
    if (importantInfo?.infoItems.isNullOrEmpty()) return null

    val formatter = DateTimeFormatter.ofPattern(DateFormat.SLASHED_DAY_MONTH_YEAR)

    return importantInfo?.let { importantInformation ->
        importantInformation.infoItems.filter { infoItem ->
            infoItem.startDate.isNotEmpty() && infoItem.endDate.isNotEmpty() &&
                    !input.arrivalDate().isAfter(LocalDate.parse(infoItem.endDate, formatter)) &&
                    !input.departureDate().isBefore(LocalDate.parse(infoItem.startDate, formatter))
        }.takeIf { it.isNotEmpty() }?.let { ImportantInfoDomain(it) }
    }
}

fun SummaryInput.constructCreateReservation( language: String): CreateReservationRequestBody {
    return CreateReservationRequestBody(
        roomBookings()!!.toListOfReservations(
            accessibleRoomBookings(),
            twinRoomBookings(),
            hotel().code(),
            arrivalDateGQ(),
            departureDateGQ(),
            rate().rateType(),
            specialRequests(),
            packageCode(),
            packageAmount(),
            promotionCode(),
            promoKind()
        ),
        BookingChannelDetails(if (!isBusinessUser) Channel.PI.name else Channel.BB.name, SUB_CHANNEL, language)
    )
}

fun SummaryInput.createPackagesRequestBody(language: String, country: String): HotelPackagesRequestBody {
    return HotelPackagesRequestBody(
        hotelId = hotel().code(),
        startDate = arrivalDateGQ(),
        endDate = departureDateGQ(),
        adultsNumber = totalAdults(),
        childrenNumber = totalGuests() - totalAdults(),
        nightsNumber = totalNights(),
        language = language,
        country = country,
        bookingFlowId = EMPTY_STRING_DOMAIN,
        basketReferenceId = EMPTY_STRING_DOMAIN,
        channel = if (isBusinessUser) Channel.BB else Channel.PI
    )
}

fun isBrandAllowedForDiscountBox(
    brandName: String?,
    logService: LogService,
    contentRepository: ContentManagedResourceRepository
): Boolean {
    if (brandName.isNullOrEmpty()) {
        return false
    }

    return try {
        val jsonString = contentRepository.getString(ContentManagedResourceRepository.Key.HDP_DISCOUNT_BOX_ALLOWED_BRANDS.value)
        val allowedBrandsInfo = Gson().fromJson(jsonString, AllowedBrandsInfo::class.java)
        allowedBrandsInfo.allowedBrands.any { it.equals(brandName, ignoreCase = true) }
    } catch (e: Exception) {
        logService.logException(e, "Failed to parse hdp_discount_box_allowed_brands")
        false
    }
}