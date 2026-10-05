package com.whitbread.premierinn.domain

import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.entity.BookingConfirmation
import com.whitbread.premierinn.domain.booking.entity.ReservationById
import com.whitbread.premierinn.domain.booking.entity.RoomStay
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.AmendRestrictions
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.TripAdvisorRating
import com.whitbread.premierinn.domain.graphql.common.GraphQLErrorDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.DailyPriceDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.PackagesPackagesDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RateClassificationsDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RestaurantDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomOptionsDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomPriceBreakdownDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomRateDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomTypeDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomTypeInfoDomain
import com.whitbread.premierinn.domain.hotel.entity.AcceptedCreditCard
import com.whitbread.premierinn.domain.hotel.entity.CityTax
import com.whitbread.premierinn.domain.hotel.entity.Facility2
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.domain.search.entity.Location
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem
import org.threeten.bp.LocalDate
import java.util.Collections

fun createBooking(bookingReference: String = "ref",
                  leadGuestSurname: String = "Smith",
                  arrivalDate: LocalDate = LocalDate.of(2018, 11, 6),
                  departureDate: LocalDate = LocalDate.of(2018, 11, 7),
                  hotelCode: String = "LONMON",
                  hotelName: String = "London Something",
                  numberOfRooms: Int = 1,
                  leadGuestFullName: String = "Mr John Smith",
                  rateType: String = "Flex",
                  totalCost: PriceDomain? = null,
                  balanceOutstanding: PriceDomain? = null,
                  prepaidAmount: PriceDomain? = null,
                  cardFeeApplies: Boolean = false,
                  amendable: Boolean = false,
                  isCancelled: Boolean = false,
                  isLinkedToAccount: Boolean = false,
                  details: Booking.Details? = null): Booking {
    return Booking(
            bookingReference = bookingReference,
            leadGuestSurname = leadGuestSurname,
            arrivalDate = arrivalDate,
            departureDate = departureDate,
            hotelCode = hotelCode,
            hotelName = hotelName,
            numberOfRooms = numberOfRooms,
            leadGuestFullName = leadGuestFullName,
            rateType = rateType,
            totalCost = totalCost,
            balanceOutstanding = balanceOutstanding,
            prepaidAmount = prepaidAmount,
            cardFeeApplies = cardFeeApplies,
            amendable = amendable,
            isCancelled = isCancelled,
            isLinkedToAccount = isLinkedToAccount,
            details = details,
            amendRestrictions = AmendRestrictions.createWithDefaults()
    )
}

fun createBookingConfirmation(bookingReference: String) = BookingConfirmation(
    bookingReference = bookingReference,
    balanceOutstanding = 123.0f,
    bookingFlowID = "abcd123",
    currencyCode = "GBP",
    hotelId = "LONEUS",
    infoMessages = listOf("Free cancellation up to 1pm on the day of arrival"),
    newTotal = 123.0f,
    policyCode = "policy code",
    previousTotal = 0.0f,
    reservationByIdList = listOf(
        ReservationById(
            reservationId = "132435",
            reservationStatus = "Reserved",
            reservationGuestList = listOf(),
            reservationPackageList = listOf(),
            purposeOfStay = "LEI",
            roomStay = RoomStay(
                adultsNumber = 1,
                childrenNumber = 0,
                cot = false,
                roomType = "FMTRPL",
                ratePlanCode = "FLEXRATE",
                arrivalDate = "2024-12-12",
                departureDate = "2024-12-13",
                roomPrice = 123.0f
            )
        )
    ),
    totalCost = 123.0f,
    basketStatus = "basketStatus"
)

fun createSearchItem(name: String = "London", type: SearchSuggetionItem.Type = SearchSuggetionItem.Type.PI_HOTEL, hotelId: String? = null,
                     location: Location = createLocation()): SearchSuggetionItem {
    return SearchSuggetionItem(name = name, type = type, code = hotelId, location = location)
}

fun createLocation(lat: Double = 1.0, lon: Double = 1.0): Location {
    return Location(latitude = lat, longitude = lon)
}

fun createHotel(
        code: String = "LONMON",
        name: String = "London Something",
        brand: Hotel.Brand = Hotel.Brand.PI,
        address: Address = Address("Oxford Street"),
        location: Location = Location(1.0, 1.0),
        isHub: Boolean = false,
        flag: Hotel.Flag? = null,
        cityTax: CityTax? = null,
        rating: TripAdvisorRating? = null,
        photoPath: String? = null,
        nationalPhone: String? = null,
        chargeablePhone: String? = null,
        facilities: List<Facility2> = emptyList(),
        acceptedCreditCards: List<AcceptedCreditCard> = emptyList()
): Hotel {
    val contacts = if (nationalPhone != null && chargeablePhone != null) Hotel.ContactDetails(freePhone = nationalPhone, chargeablePhone = chargeablePhone) else null
    return Hotel(
            code = code,
            name = name,
            brand = brand,
            address = address,
            location = location,
            flag = flag,
            cityTax = cityTax,
            contactDetails = contacts,
            facilities = facilities,
            rating = rating,
            photoPath = photoPath,
            roomVariantDetails = emptyList(),
            acceptedCreditCards = acceptedCreditCards
    )
}

fun mockSuccessfulHotelAvailability() : HotelAvailabilityDomain {
    val roomDomainOne = RoomOptionsDomain(cotAvailable = false, pmsRoomType = "PPLDBL",
        roomPriceBreakdownDomain = RoomPriceBreakdownDomain(currencyCode = "GBP",
            dailyPricesDomainList = Collections.singletonList(DailyPriceDomain(date = "2023-11-09", netPrice = 999.0)),
            totalNetAmount = 999.0, packageCode = null, packageAmount = null, baseRateAmount = null), roomClass = "PP", silentSubstitution = true, specialRequests = Collections.singletonList("DBLE"))
    val roomDomainTwo = RoomOptionsDomain(cotAvailable = false, pmsRoomType = "DOUBLE",
        roomPriceBreakdownDomain = RoomPriceBreakdownDomain(currencyCode = "GBP",
            dailyPricesDomainList = Collections.singletonList(DailyPriceDomain(date = "2023-11-09", netPrice = 999.0)),
            totalNetAmount = 999.0, packageCode = null, packageAmount = null, baseRateAmount = null), roomClass = "ST", silentSubstitution = true, specialRequests = Collections.singletonList("DBLE"))
    val listOfRoomTypes: MutableList<RoomTypeDomain> = Collections.singletonList(
        RoomTypeDomain(adults = 2, children = 0, cotRequested = false, roomType = "DB",
        roomOptionsDomainList = listOf(roomDomainOne, roomDomainTwo))
    )
     val listOfRoomRate: MutableList<RoomRateDomain> = Collections.singletonList(
        RoomRateDomain(promotionCode = "SSSS", ratePlanCode = "FLEXRATE", roomTypesDomainList = listOfRoomTypes, cellCode = EMPTY_STRING_DOMAIN)
    )
     val roomTypeInfoDomainTwin = RoomTypeInfoDomain(
        roomTypeCode = listOf("TWINRM", "DBLDBL"),
        roomCategory = "Twin",
        roomLabel = "Twin room",
        roomDescription = "Twin rooms description",
        roomImage = "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-twin-room.jpg")
     val roomTypeInfoDomainSingle = RoomTypeInfoDomain(
        roomTypeCode = listOf("SINGLE"),
        roomCategory = "Standard",
        roomLabel = "Standard room",
        roomDescription = "Standard rooms description",
        roomImage = "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4%201.jpg")

    val rateClassificationsDomainOne = RateClassificationsDomain(
        rateClassification = "FLEXRATE",
        rateOrder = "10",
        rateName = "Flex Rate",
        rateDescription = "Pay now or on arrival, fully refundable",
        rateLongDescription = EMPTY_STRING_DOMAIN,
        rateNotes = "<p>Flex: Amend or cancel up to 1pm on arrival day</p>",
        rateTags = emptyList()
    )

     val rateClassificationsDomainTwo = RateClassificationsDomain(
        rateClassification = "SEMIFLEX",
        rateOrder = "20",
        rateName = "Semi-Flex Rate",
        rateDescription = "Pay now, fully refundable conditions apply",
        rateLongDescription = EMPTY_STRING_DOMAIN,
        rateNotes = "<p>Semi-Flex: Amend or cancel up to three full days before arrival date. Your arrival date can be amended up to 1pm on the day you’re due to arrive.</p>",
        rateTags = emptyList()
    )
    return HotelAvailabilityDomain.createDefaultAvailability().copy(
        available = true,
        hotelId = "BANBRI",
        endDate = "2023-11-09",
        startDate = "2023-11-09",
        roomRateDomainList = listOfRoomRate,
        packages = null,
        listOfRatesClassification = listOf(rateClassificationsDomainOne, rateClassificationsDomainTwo),
        listOfRoomTypeInfo = listOf(roomTypeInfoDomainSingle, roomTypeInfoDomainTwin),
        error = null

    )
}

fun mockErrorHotelNoAvailabilityOpera(): HotelAvailabilityDomain {
    return HotelAvailabilityDomain.createDefaultAvailability().copy(available = false)
}

fun mockErrorHotelAvailabilityOpera(): HotelAvailabilityDomain {
    val mockError = GraphQLErrorDomain(path = listOf("path1"), errorType = "Network timeout", message = "500")
    return HotelAvailabilityDomain.createDefaultAvailability().copy(error = listOf(mockError))
}

fun createPackagesPackagesDomain() = PackagesPackagesDomain(
    meals = listOf(premierInnBreakfast, continentalBreakfast),
    mealsKids = listOf(mealForKids),
    extrasItems = listOf(ultimateWiFi),
    roomSelection = listOf(roomSelection)
)

fun createDataPackagesDomain() = DataPackagesDomain(
    restaurant = RestaurantDomain(
        noMealsFound = false,
        messageDescription = null,
        restaurantNotFound = false,
        menus = emptyList()
    ),
    packages = createPackagesPackagesDomain(),
    hotelHasCityTaxForLeisure = false,
    hotelHasCityTaxForBusiness = false
)