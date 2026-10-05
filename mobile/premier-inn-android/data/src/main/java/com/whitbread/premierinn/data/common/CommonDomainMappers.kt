@file:JvmName("DomainMappers")

package com.whitbread.premierinn.data.common

import com.whitbread.premierinn.data.booking.entity.AmendRestrictionsEntity
import com.whitbread.premierinn.data.booking.entity.PriceEntity
import com.whitbread.premierinn.data.booking.mapToAddress
import com.whitbread.premierinn.data.remote.AccountApiContract
import com.whitbread.premierinn.data.remote.AllCheckInTimesInfo
import com.whitbread.premierinn.data.remote.AmendReservationApiResponse
import com.whitbread.premierinn.data.remote.ApiCommon
import com.whitbread.premierinn.data.remote.BartDownInfo
import com.whitbread.premierinn.data.remote.HomepageBanner
import com.whitbread.premierinn.data.remote.HotelCheckInCheckoutInfo
import com.whitbread.premierinn.data.remote.OperaFallbackPopupInfo
import com.whitbread.premierinn.data.remote.PaymentsApiContract
import com.whitbread.premierinn.data.remote.PaymentsConfirmationStatusContract
import com.whitbread.premierinn.data.remote.PromoContent
import com.whitbread.premierinn.data.remote.QrKioskHotels
import com.whitbread.premierinn.data.remote.RateContentItem
import com.whitbread.premierinn.data.remote.ReservationApiContract
import com.whitbread.premierinn.data.remote.SrpBanner
import com.whitbread.premierinn.data.remote.Terms
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationGraphQLContract
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.AllCheckInCheckoutTimesInfoDomain
import com.whitbread.premierinn.domain.common.AmendRestrictions
import com.whitbread.premierinn.domain.common.BartDownInfoDomain
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.HomepageBannerDomain
import com.whitbread.premierinn.domain.common.HotelCheckInCheckoutInfoDomain
import com.whitbread.premierinn.domain.common.NewsletterPermissionDomain
import com.whitbread.premierinn.domain.common.NewsletterPreferenceDomain
import com.whitbread.premierinn.domain.common.OperaFallbackPopupInfoDomain
import com.whitbread.premierinn.domain.common.Passport
import com.whitbread.premierinn.domain.common.PaymentDetails
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.PromoContentDomain
import com.whitbread.premierinn.domain.common.QrKioskHotelsDomain
import com.whitbread.premierinn.domain.common.RateContentItemDomain
import com.whitbread.premierinn.domain.common.RoomBreakdown
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.SrpBannerDomain
import com.whitbread.premierinn.domain.common.TermsDomain
import com.whitbread.premierinn.domain.customer.entity.AccessLevel
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Business
import com.whitbread.premierinn.domain.customer.entity.Contact
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.FullName
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.payment.AmendReservationDomainDetails
import com.whitbread.premierinn.domain.payment.CarDataDetails
import com.whitbread.premierinn.domain.payment.entity.PaymentBookingStatusDomain
import com.whitbread.premierinn.domain.payment.entity.ProviderResponse
import com.whitbread.premierinn.domain.payment.entity.ThreeCPBookingConfirmationResponseEntity
import com.whitbread.premierinn.domain.payment.entity.ThreeCPaymentServiceResponseEntity
import com.whitbread.premierinn.domain.payment.entity.ThreeCResponse

fun ApiCommon.Price?.toPriceDomain(): PriceDomain? {
    return if (this != null) {
        PriceDomain(this.amount, this.currency)
    } else null
}

fun ApiCommon.Price?.toPriceEntity(): PriceEntity? {
    return if (this != null) {
        PriceEntity(this.amount, this.currency)
    } else null
}

fun PriceDomain.toPriceEntity(): PriceEntity {
    return PriceEntity(this.amount, this.currency)
}

fun PriceEntity?.mapToPrice(): PriceDomain? {
    return if (this != null) {
        PriceDomain(this.amount, this.currency)
    } else null
}

fun PriceDomain?.mapToPrice(): PriceEntity? {
    return if (this != null) {
        PriceEntity(this.amount, this.currency)
    } else null
}

fun AmendRestrictionsEntity.mapToRestriction(): AmendRestrictions {
    return AmendRestrictions(
            nights = nights,
            rooms = rooms,
            guestNames = guestNames,
            upsell = upsell,
            restricted = restricted
    )
}

fun AmendRestrictions.mapToRestriction(): AmendRestrictionsEntity {
    return AmendRestrictionsEntity(
            nights = nights,
            rooms = rooms,
            guestNames = guestNames,
            upsell = upsell,
            restricted = restricted
    )
}

fun ApiCommon.Passport?.toDomain(): Passport? {
    return this?.let {
        Passport(
                number = throwApiExceptionIfNullable(this.number),
                placeOfIssue = this.countryOfIssue
        )
    }
}

fun ApiCommon.PaymentCard?.toDomain(): PaymentCard? {
    return this?.let {
        PaymentCard(
                number = this.cardNumber ?: EMPTY_STRING_DOMAIN,
                cardType = this.cardType ?: EMPTY_STRING_DOMAIN,
                cardID = this.cardID,
                holdersFullName = this.cardHolderName ?: EMPTY_STRING_DOMAIN,
                expiryDate = this.expiryDate ?: EMPTY_STRING_DOMAIN
        )
    }
}

fun AccountApiContract.BookingPreference?.toDomain(): BookingPreferences {
    return if (this != null) {
        BookingPreferences(
                mealPreference = this.mealPreference,
                roomCriteriaPreference = if (this.roomRequirements != null) RoomCriteria(
                        numberOfAdults = this.roomRequirements.adults,
                        numberOfChildren = this.roomRequirements.children,
                        roomType = if (this.roomRequirements.type.isNotEmpty()) this.roomRequirements.type.toRoomType() else RoomType.DOUBLE,
                        includeCot = this.roomRequirements.cotRequired,
                        roomNumber = 1,
                        hotelBrand = this.roomRequirements.hotelBrand ?: EMPTY_STRING_DOMAIN
                ) else RoomCriteria.createWithDefaults(roomNumber = 1)
        )
    } else {
        BookingPreferences.EMPTY
    }
}

fun AccountApiContract.CustomerContactDetails.toContactDomain(): Contact {
    return Contact(
            email = this.email.orEmpty(),
            mobile = this.mobile,
            telephone = this.telephone
    )
}

fun AccountApiContract.CustomerContactDetails.toFullNameDomain(): FullName {
    return FullName(
            title = this.title.orEmpty(),
            firstName = this.firstName.orEmpty(),
            lastName = this.lastName.orEmpty()
    )
}

fun AccountApiContract.CustomerContactDetails.toAddressDomain(): Address {
    return this.address?.let {
        Address(
                line1 = it.line1.orEmpty(),
                line2 = it.line2,
                line3 = it.line3,
                line4 = it.line4,
                line5 = it.line5,
                postCode = it.postcode,
                companyName = it.companyName,
                countryCode = it.countryCode
        )
    } ?: Address.EMPTY
}

fun AccountApiContract.CustomerContactDetails.toPassportDomain(): Passport? {
    return this.passport.toDomain()
}


fun AccountApiContract.Business.toBusinessDomain(): Business {
    return Business(
            accessLevel = this.accessLevel.toAccessLevelEnum(),
            customerReferenceAnswer = this.customerReferenceAnswer,
            centralCard = this.centralCard,
            employeeId = this.employeeId
    )
}

fun AccountApiContract.CustomerResponse.toDomain(): Customer {
    return Customer(
            sessionId = this.sessionId ?: EMPTY_STRING_DOMAIN,
            customerAccountID = this.customerAccountID ?: EMPTY_STRING_DOMAIN,
            guestHistoryNumber = this.guestHistoryNumber ?: EMPTY_STRING_DOMAIN,
            fullName = this.contactDetail?.toFullNameDomain() ?: FullName.EMPTY,
            contact = this.contactDetail?.toContactDomain() ?: Contact.EMPTY,
            businessUse = this.businessUse ?: false,
            carRegistration = this.contactDetail?.carRegistration,
            passport = this.contactDetail?.toPassportDomain(),
            nationality = this.contactDetail?.nationality,
            bookingPreferences = this.bookingPreference?.toDomain() ?: BookingPreferences.EMPTY,
            paymentCard = this.paymentPreference?.paymentCard.toDomain(),
            address = this.contactDetail?.toAddressDomain() ?: Address.EMPTY,
            companyId = this.companyId,
            business = this.business?.toBusinessDomain(),
            guestHistoryCreation = this.guestHistoryCreation,
            totalStays = this.totalStays ?: 0
    )
}

fun ReservationApiContract.AmendRestriction?.toDomain(): AmendRestrictions {
    return if (this != null) {
        AmendRestrictions(
                nights = nights,
                rooms = rooms,
                guestNames = guestNames,
                upsell = upsell,
                restricted = restricted
        )
    } else {
        AmendRestrictions.createWithDefaults()
    }
}

fun List<ReservationApiContract.RoomBreakdown>?.toDomain(): List<RoomBreakdown?> {
    return this?.asIterable()?.map { it.toDomain() }.orEmpty()
}

fun ReservationApiContract.RoomBreakdown?.toDomain(): RoomBreakdown? {
    return if (this != null) {
        RoomBreakdown(totalRoomCost = totalRoomCost.toPriceDomain()!!, roomId = roomId!!)
    } else null
}

fun ReservationApiContract.PaymentDetails?.toDomain(): PaymentDetails? {
    return if (this != null) {
        PaymentDetails(
                billingAddress = throwApiExceptionIfNullable(this.billingAddress.mapToAddress()),
                cardNumber = throwApiExceptionIfNullable(this.cardNumber),
                cardSecurityCode = throwApiExceptionIfNullable(this.cardSecurityCode),
                cardType = throwApiExceptionIfNullable(this.cardType),
                holdersFullName = throwApiExceptionIfNullable(this.cardholderName),
                expiryDate = throwApiExceptionIfNullable(this.expiryDate),
                startDate = this.startDate,
                issueNumber = this.issueNumber,
                prepaymentRequired = throwApiExceptionIfNullable(this.prepaymentRequired),
                useExistingCard = throwApiExceptionIfNullable(this.useExistingCard)
        )
    } else null
}


fun AmendReservationApiResponse.CarData?.toDomain(): CarDataDetails? {
    return if (this != null) {
        CarDataDetails(
                title = this.title,
                carParkOperator = this.carParkOperator,
                hotelCode = this.hotelCode,
                stayLength = this.stayLength
        )
    } else null
}

fun AmendReservationApiResponse.AmendReservationResponse.toDomain(): AmendReservationDomainDetails {
    return AmendReservationDomainDetails(
            sessionId = throwApiExceptionIfNullable(this.sessionId),
            confirmationNumber = throwApiExceptionIfNullable(this.confirmationNumber),
            checkInOnline = this.checkInOnline,
            totalCost = this.totalCost.toPriceDomain(),
            cityTax = this.cityTax.toPriceDomain(),
            vatRate = this.vatRate,
            carData = this.carData.toDomain(),
            prepaymentSuccess = throwApiExceptionIfNullable(this.prepaymentSuccess),
            payOnArrivalSuccess = throwApiExceptionIfNullable(this.payOnArrivalSuccess),
            prepaymentText = this.prepaymentText,
            pendingAmendId = this.pendingAmendId
    )
}

fun PaymentsApiContract.ThreeCPaymentServiceResponse.toDomain(): ThreeCPaymentServiceResponseEntity {
    return ThreeCPaymentServiceResponseEntity(
            paymentId = this.paymentID,
            providerResponse = this.providerResponse.toDomain(),
            revisedSolution = this.revisedSolution
    )
}

fun PaymentsApiContract.ProviderResponse.toDomain(): ProviderResponse {
    return ProviderResponse(
            threeCResponse = this.threeCResponse.toDomain()
    )
}

fun PaymentsApiContract.ThreeCResponse.toDomain(): ThreeCResponse {
    return ThreeCResponse(
            sessionId = this.sessionID,
            template = this.template,
            iPageHtml = this.iPageHtml,
            iPageSessionIdForGPay = null,
            providerUrl = EMPTY_STRING
    )
}

fun PaymentsConfirmationStatusContract.PaymentsConfirmationStatusResponse.toDomain(): ThreeCPBookingConfirmationResponseEntity{
    return ThreeCPBookingConfirmationResponseEntity(
            bookingStatus = this.bookingStatus.toPaymentStatusDomain(),
            confirmationNumber = this.confirmationNumber,
            prepaymentSuccess = this.prepaymentSuccess,
            code = this.code)
}

fun PaymentsConfirmationStatusContract.PaymentBookingStatus.toPaymentStatusDomain(): PaymentBookingStatusDomain {
    return when(this) {
        PaymentsConfirmationStatusContract.PaymentBookingStatus.PENDING -> PaymentBookingStatusDomain.PENDING
        PaymentsConfirmationStatusContract.PaymentBookingStatus.FAILED -> PaymentBookingStatusDomain.FAILED
        PaymentsConfirmationStatusContract.PaymentBookingStatus.COMPLETE -> PaymentBookingStatusDomain.COMPLETE
    }
}

fun String.toAccessLevelEnum(): AccessLevel {
    return when (this) {
        "STAYER" -> AccessLevel.STAYER
        "SELF" -> AccessLevel.SELF
        "BOOKER" -> AccessLevel.BOOKER
        "SUPER" -> AccessLevel.SUPER
        else -> AccessLevel.UNKNOWN
    }
}

fun HotelCheckInCheckoutInfo.toHotelCheckInCheckoutInfoDomain(): HotelCheckInCheckoutInfoDomain{
    return HotelCheckInCheckoutInfoDomain(this.bookingDetailsCheckInInfo,this.bookingDetailsCheckOutInfo,
        this.summaryOrPaymentBreakdownCheckInInfo, this.summaryOrPaymentBreakdownCheckOutInfo)
}

fun AllCheckInTimesInfo.toAllCheckInCheckoutTimesInfoDomain(): AllCheckInCheckoutTimesInfoDomain{
    return AllCheckInCheckoutTimesInfoDomain(this.ukCheckInTimes.toHotelCheckInCheckoutInfoDomain(), this.germanyCheckInTimes.toHotelCheckInCheckoutInfoDomain())
}

fun BartDownInfo.toBartDownInfoDomain(): BartDownInfoDomain{
    return BartDownInfoDomain(this.bartDowntimeTitle, this.bartDowntimeDescription, this.bartDowntimeMakeBooking, this.bartDowntimeExistingBooking)
}

fun RateContentItem.toRateContentInfoDomain(): RateContentItemDomain {
    return RateContentItemDomain(this.classification, this.name, this.description, this.bookingTermsMessage)
}

fun OperaFallbackPopupInfo.toOperaFallbackPopupInfoDomain(): OperaFallbackPopupInfoDomain {
    return OperaFallbackPopupInfoDomain(this.operaFallbackAlertTitle, this.operaFallbackAlertMessage, this.operaFallbackAlertClose, this.operaFallbackAlertContinue)
}

fun BookingConfirmationGraphQLContract.BillingResponse.commaSeparatedAddress(): String {
    val list = listOf(this.address?.addressLine1 ?: EMPTY_STRING,
        this.address?.addressLine2 ?: EMPTY_STRING, this.address?.postalCode ?: EMPTY_STRING)

    return list.filter { it.isNotEmpty() }.joinToString()
}

fun QrKioskHotels.toQrKioskHotelsDomain(): QrKioskHotelsDomain {
    return QrKioskHotelsDomain(hotelCode = hotelCode)
}

fun AccountApiContract.NewsletterPreferenceResponse.toNewsletterPreferenceDomain(): NewsletterPreferenceDomain =
     NewsletterPreferenceDomain(
        contactChannelId= this.contactChannelId,
         newsletterPermission = this.permissions.toPermissionsDomain()
    )

private fun List<AccountApiContract.NewsletterPermission>.toPermissionsDomain(): List<NewsletterPermissionDomain> =
    this.map { NewsletterPermissionDomain(optIn = it.optIn) }



fun PromoContent.toPromoContentDomain(): PromoContentDomain {
    return PromoContentDomain(
        homepageBanner = this.homepageBanner.toHomepageBannerDomain(),
        srpBanner = this.srpBanner.toSrpBannerDomain()
    )
}

fun HomepageBanner.toHomepageBannerDomain(): HomepageBannerDomain {
    return HomepageBannerDomain(
        title = this.title,
        datePrefixText = this.datePrefixText,
        date = this.date,
        discountAmount = this.discountAmount,
        discountPercentageSign = this.discountPercentageSign,
        discountText = this.discountText,
        offerDescription = this.offerDescription,
        buttonText = this.buttonText,
        disclaimer = this.disclaimer,
        terms = this.terms.toTermsDomain()
    )
}

fun SrpBanner.toSrpBannerDomain(): SrpBannerDomain {
    return SrpBannerDomain(
        title = this.title,
        date = this.date,
        subTitle = this.subTitle,
        terms = this.terms.toTermsDomain()
    )
}

fun Terms.toTermsDomain(): TermsDomain {
    return TermsDomain(
        text = this.text,
        url = this.url
    )
}

fun PromotionsInformationDomain?.toSrpBannerPromoContentDomain(): PromoContentDomain? {
    return if (this != null) {
        val termsUrl = this.termsLink ?: EMPTY_STRING
        val normalizedUrl = if (termsUrl.startsWith(CONTENT_BASE_URL)) termsUrl else CONTENT_BASE_URL + termsUrl
        PromoContentDomain(
            homepageBanner = HomepageBannerDomain(
                title = EMPTY_STRING,
                datePrefixText = EMPTY_STRING,
                date = EMPTY_STRING,
                discountAmount = EMPTY_STRING,
                discountPercentageSign = EMPTY_STRING,
                discountText = EMPTY_STRING,
                offerDescription = EMPTY_STRING,
                buttonText = EMPTY_STRING,
                disclaimer = EMPTY_STRING,
                terms = TermsDomain(text = EMPTY_STRING, url = EMPTY_STRING)
            ),
            srpBanner = SrpBannerDomain(
                title = this.appPromoBannerTitle ?: EMPTY_STRING,
                date = EMPTY_STRING,
                subTitle = this.appPromoBannerSubtitle ?: EMPTY_STRING,
                terms = TermsDomain(
                    text = EMPTY_STRING,
                    url = normalizedUrl
                )
            )
        )
    } else null
}