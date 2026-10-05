package com.whitbread.premierinn.ciol

import com.whitbread.premierinn.ciol.analytics.CiolCompletionAnalyticsModel
import com.whitbread.premierinn.domain.booking.entity.HotelPreferenceDomain
import com.whitbread.premierinn.domain.booking.entity.LeadBookerAddress
import com.whitbread.premierinn.domain.booking.entity.LeadBookerDetails
import com.whitbread.premierinn.domain.booking.entity.PreStayDetails
import com.whitbread.premierinn.domain.booking.entity.PreStayHeaderInfo
import com.whitbread.premierinn.domain.booking.entity.PreStayModel
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CardDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentMethodGQDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentOptionDomain
import org.threeten.bp.LocalDate

val preStayModel = PreStayModel(
    preStayDetails = PreStayDetails(
        bookingFlowId = "booking-ct-a1",
        hotelId = "FRAMTI",
        startDate = LocalDate.of(2025, 1, 18),
        endDate = LocalDate.of(2025, 1, 19),
        bookerDetails = LeadBookerDetails(
            leadBookerTitle = "Mr",
            leadBookerFirstName = "Tzinedi",
            leadBookerLastName = "Tzidane",
            leadBookerEmail = "aaa.bbb@ccc.com",
            leadBookerPhone = "042042145525",
            address = LeadBookerAddress(
                addressLine1 = "Line 1",
                addressLine2 = "Line 2",
                addressLine3 = "Line 3",
                addressLine4 = "Line 4",
                postalCode = "SW1W 0NY",
                cityName = "Berlin",
                country = "Germany"
            )
        ),
        guestRooms = emptyList(),
        rooms = emptyList(),
        reservationGuests = emptyList(),
        preferences = emptyList()
    ),
    preStayHeaderInfo = PreStayHeaderInfo(hotelName = "Frankfurt Messe", hotelImage = null, hotelBrand = "brand", hotelAddress = "address"),
    outstandingBalance = null,
    basketReference = "aaabbb111",
    isOpera = true,
    bookingReference = "aaabbbcccddd111222",
    rateCode = "FLEXRATE",
    rateDescription = "Description for rate",
    rateName = "Flex",
    isBusinessBooking = false,
    isThirdPartyBooking = false,
    paymentOption = "PIBA_CNP",
    upsellsAddonsEnabled = false
)

val paymentMethod = PaymentMethodGQDomain(
    clientToken = null,
    enabled = true,
    name = "CARD",
    order = 1,
    logoSrc = null,
    paymentOptions = listOf(
        PaymentOptionDomain(
            enabled = true,
            order = 1,
            type = "PAY_NOW"
        )
    ),
    reasons = emptyList(),
    type = "SAVED_CARD",
    subType = null,
    acceptedCardTypes = null,
    card = CardDomain(
        token = "4216333880397891103",
        expiryMonth = "12",
        expiryYear = "29",
        type = "VI",
        logoSrc = "/content/dam/global/booking/VC.jpg",
        cardType = "LEISURE_STORED_CARD",
        cardHolderName = "Test",
        cnpRequired = false
    ),
    cnpOptionAvailable = false,
    cnpPreSelected = false
)

val hotelPreferences = HotelPreferenceDomain(
    code = "ANNV",
    preferenceGroup = "EVENTS",
    label = "Anniversary"
)

val analyticsModel = CiolCompletionAnalyticsModel()
