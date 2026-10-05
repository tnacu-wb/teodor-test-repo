package com.whitbread.premierinn.reviewbooking.mappers

import com.whitbread.premierinn.api.request.booking.BookingAddress
import com.whitbread.premierinn.api.response.customer.PaymentCard
import com.whitbread.premierinn.api.response.customer.PaymentCard.BUSINESS_CARD
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.PaymentMethodType
import com.whitbread.premierinn.common.PaymentTimingChoice
import com.whitbread.premierinn.common.PaymentTimingRule
import com.whitbread.premierinn.common.RoomBooking
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.payments.BOOKING_PAYMENT_JOURNEY
import com.whitbread.premierinn.domain.booking.entity.PreStayModel
import com.whitbread.premierinn.domain.common.ANDROID_APPS_CHANNEL
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CardDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.*
import com.whitbread.premierinn.domain.payment.entity.ThreeCpBillingAddress
import com.whitbread.premierinn.hoteldetails.DonationsInput
import com.whitbread.premierinn.reviewbooking.AdditionalInformation
import com.whitbread.premierinn.reviewbooking.ParcelableCard
import com.whitbread.premierinn.reviewbooking.ParcelableDonationPackageDomain
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethod
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethodsDetailsInput
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput
import java.util.UUID

const val WHITESPACE = " "
const val ZERO_BUDGET = 0.0f

const val PAYMENT_TYPE_PAY_NOW = "PAY_NOW"
const val PAYMENT_TYPE_PAY_ON_ARRIVAL = "PAY_ON_ARRIVAL"

fun BookingAddress.toAddressDomain(): ThreeCpBillingAddress {
    return ThreeCpBillingAddress(
            line1 = this.line1(),
            line2 = this.line2() ?: EMPTY_STRING,
            state = this.city(),
            countryCode = this.countryCode(),
            postalCode = this.postcode()
    )
}

fun ParcelableCard.toDomain(): CardDomain {
    return CardDomain(
        token = this.token,
        expiryMonth = this.expiryMonth,
        expiryYear = this.expiryYear,
        cardType = this.cardType,
        cardHolderName = this.cardHolderName,
        type = this.type,
        cnpRequired = this.cnpRequired,
        logoSrc = this.logoUrl
    )
}

fun List<RoomBooking>.toOperaRoom(rateType : String, accessibleRoomList: List<RoomBooking>, twinRoomList: List<RoomBooking>) : List<Rooms>{
    val listOfOperaRooms = mutableListOf<Rooms>()
    val mergedListOfRoomBooking = listOfNotNull(this, accessibleRoomList, twinRoomList).flatten().sortedBy { it.roomNumber }

    if (rateType != EMPTY_STRING) {
        mergedListOfRoomBooking.forEach {

            listOfOperaRooms.add(Rooms(it.adults, rateType, it.lettingCode))
        }
    }
    return listOfOperaRooms
}

fun buildPaymentRequestBody(
    configuration: AppConfiguration,
    preStayModel: PreStayModel,
    selectedPaymentMethod: ParcelablePaymentMethod,
    language: String,
    paypalNonce: String?,
    paypalDeviceData: String?,
    billingAddress: Address?,
    isInnBusinessUser: Boolean?
): InitiatePaymentRequestBody {
    val departureDate = preStayModel.preStayDetails.endDate
    val arrivalDate = preStayModel.preStayDetails.startDate
    val fullName = preStayModel.preStayDetails.bookerDetails.run {
        leadBookerTitle + WHITESPACE + leadBookerFirstName + WHITESPACE + leadBookerLastName
    }
    val leadGuestDetails = LeadGuestBooking(
        fullName,
        false,
        0,
        null // Optional field
    )

    val operaRooms: List<Rooms> = preStayModel.preStayDetails.rooms
    val environment = configuration.graphQLUrl.replace("api", "www")

    val isStoredCard = if (preStayModel.isOpera) {
        selectedPaymentMethod.type == PaymentMethodType.SAVED_CARD.name
    } else {
        selectedPaymentMethod.parcelableCard?.getExpiryDate()?.isNotBlank() ?: false
    }

    val isBusinessCard = if (preStayModel.isOpera) {
        selectedPaymentMethod.type in listOf(
            PaymentCard.BUSINESS_CARD_OPERA,
            PaymentCard.BUSINESS_CARD_OPERA_EURO,
            PaymentMethodType.NEW_PIBA.name,
            PaymentMethodType.NEW_PIBA_EURO.name
        )
    } else {
        selectedPaymentMethod.type == BUSINESS_CARD
    }

    val paymentType = if (isBusinessCard) PAYMENT_TYPE_PAY_ON_ARRIVAL else PAYMENT_TYPE_PAY_NOW

    val isGooglePaySelected = selectedPaymentMethod.type == PaymentMethodType.GP.name

    var isSavedCardBusiness = false
    val storedCardDetails = if (isStoredCard) selectedPaymentMethod else null
    var storedCard : PaymentCardDetailsDomain? = null

    val address = if (billingAddress != null) {
        Address(
            country = billingAddress.countryCode,
            addressLine1 = billingAddress.line1,
            addressLine2 = billingAddress.line2,
            addressLine3 = billingAddress.line3,
            addressLine4 = billingAddress.line4,
            postalCode = billingAddress.postCode ?: EMPTY_STRING
        )
    } else {
        Address(
            country = null, // Optional field
            addressLine1 = preStayModel.preStayDetails.bookerDetails.address.addressLine1,
            addressLine2 = preStayModel.preStayDetails.bookerDetails.address.addressLine2,
            addressLine3 = preStayModel.preStayDetails.bookerDetails.address.addressLine3,
            addressLine4 = null, // Optional field
            postalCode = preStayModel.preStayDetails.bookerDetails.address.postalCode
        )
    }

    if (storedCardDetails != null && isStoredCard) {
        storedCardDetails.parcelableCard?.let { card ->
            storedCard = PaymentCardDetailsDomain(
                cardType = card.cardType,
                cardholderName = card.cardHolderName,
                cnpRequired = card.cnpRequired,
                expiryMonth = card.expiryMonth,
                expiryYear = card.expiryYear,
                logoUrl = card.logoUrl,
                token = card.token,
                type = card.type
            )
            isSavedCardBusiness = card.type == BUSINESS_CARD
        }
    }

    val booking = Booking(
        businessSite = BusinessSite(
            identifier = preStayModel.preStayDetails.hotelId,
            name = preStayModel.preStayHeaderInfo.hotelName,
            type = "HOTEL",
            location = preStayModel.preStayDetails.hotelId
        ),
        channel = ANDROID_APPS_CHANNEL,
        journey = BOOKING_PAYMENT_JOURNEY,
        language = language,
        rooms = operaRooms,
        type = paymentType,
        leadGuest = leadGuestDetails,
        arrivalDate = arrivalDate.toString(),
        departureDate = departureDate.toString()
    )

    val payment = Payment(
        billing = Billing(
            address = address,
            email = preStayModel.preStayDetails.bookerDetails.leadBookerEmail,
            firstName = preStayModel.preStayDetails.bookerDetails.leadBookerFirstName,
            lastName = preStayModel.preStayDetails.bookerDetails.leadBookerLastName,
            title = preStayModel.preStayDetails.bookerDetails.leadBookerTitle,
        ),
        environment = environment,
        subType = if (paypalNonce != null) "MIT" else "ECOMM",
        type = if (paypalNonce != null) "PAYPAL" else if (isBusinessCard) "PIBA" else if (isGooglePaySelected) "WALLET_GOOGLE" else if (isSavedCardBusiness) "PIBA" else "CARD",
        businessItems = if (isBusinessCard) {
            createBusinessItems(
                false,
                0f,
                false,
                false,
                false,
                EMPTY_STRING,
                EMPTY_STRING
            )
        } else {
            null
        },
        pibaCardPresent = !selectedPaymentMethod.cnpPreSelected,
        paypalNonce = paypalNonce,
        paypalDeviceData = paypalDeviceData,
        card = storedCard
    )

    val createPaymentCriteria = CreatePaymentCriteria(
        booking = booking,
        payment = payment,
        requestId = UUID.randomUUID().toString(),
        charityPackageCode = EMPTY_STRING, // Optional Field
        hotelId = EMPTY_STRING, // Optional Field
        isCiol = true,
        companyQuestionAndAnswerDetails = if (isInnBusinessUser == true) {
            CompanyQuestionAndAnswerDetails(emptyList())
        } else {
            null
        }
    )

    return InitiatePaymentRequestBody(
        basketReference = preStayModel.basketReference,
        createPaymentCriteria = createPaymentCriteria
    )
}

fun ReviewBookingInput.toGraphQLPaymentDetails(language: String,
                                               environment: String,
                                               bookingAddress: BookingAddress,
                                               paymentTimingRule: String,
                                               isBusinessCard: Boolean, dinnerAllowance: Boolean,
                                               alcoholAllowed:  Boolean, wifiAccessAllowed: Boolean,
                                               carParkingAllowed: Boolean, dinnerBudget: Float,
                                               cnpSelected: Boolean, charityPackageCode: String?,
                                               paypalNonce: String?, paypalDeviceData: String?,
                                               purchaseOrder: String?, customerReference: String?,
                                               isGooglePaySelected: Boolean,
                                               paymentDetailInput: ParcelablePaymentMethodsDetailsInput,
                                               isStoredCard: Boolean,
                                               selectedSavedCardHolderName: String,
                                               isInnBusinessUser: Boolean,
                                               isLoggedIn: Boolean
): InitiatePaymentRequestBody {

    val departureDate = this.paymentDetailsInput()?.bookingFlowInput()?.let {
        it.arrivalDate().plusDays(it.numNights().toLong())
    } ?: EMPTY_STRING
    val arrivalDate = this.paymentDetailsInput()?.bookingFlowInput()?.arrivalDate() ?: EMPTY_STRING
    val fullName = this.paymentDetailsInput()?.bookerDetails()?.title().toString() + WHITESPACE +
                   this.paymentDetailsInput()?.bookerDetails()?.firstName() + WHITESPACE +
                   this.paymentDetailsInput()?.bookerDetails()?.lastName()

    val leadGuestDetails = if (isLoggedIn) LeadGuestBooking(
        fullName,
        true,
        null,
        null
    ) else LeadGuestBooking(fullName, false, null,
        null)

    val paymentType = if (paymentTimingRule == PaymentTimingRule.PAY_LATER.name)  PaymentTimingRule.PAY_ON_ARRIVAL.name else paymentTimingRule
    val rateType = this.paymentDetailsInput()?.bookingFlowInput()?.chosenRate()?.rateType() ?: EMPTY_STRING

    val operaRooms: List<Rooms> = this.paymentDetailsInput()?.bookingFlowInput()?.roomBookings()?.toOperaRoom(
        rateType,
        this.paymentDetailsInput()?.bookingFlowInput()?.accessibleRoomBookings() ?: emptyList(),
        this.paymentDetailsInput()?.bookingFlowInput()?.twinRoomBookings() ?: emptyList()
    ) ?: emptyList()

    var isSavedCardBusiness = false
    val storedCardDetails = paymentDetailInput.parcelablePaymentMethods.filter { it.type == PaymentMethodType.SAVED_CARD.name }.filter { it.enabled }
    var storedCard : PaymentCardDetailsDomain? = null
    var cnpForStoredCard = false
    if (isStoredCard) {
        val findSelectedSavedCard = storedCardDetails.find { it.parcelableCard?.cardHolderName == selectedSavedCardHolderName }

        findSelectedSavedCard?.parcelableCard?.let { card ->
            storedCard = PaymentCardDetailsDomain(
                            cardType = card.cardType,
                            cardholderName = card.cardHolderName,
                            cnpRequired = card.cnpRequired,
                            expiryMonth = card.expiryMonth,
                            expiryYear = card.expiryYear,
                            logoUrl = card.logoUrl,
                            token = card.token,
                            type = card.type
            )
            isSavedCardBusiness = card.type == BUSINESS_CARD

            cnpForStoredCard = !card.cnpRequired
        }
    }
    val cnpForAuth = storedCard?.let {
        if (cnpSelected) false else cnpForStoredCard
    } ?: !cnpSelected

    return InitiatePaymentRequestBody(
            basketReference = this.paymentDetailsInput()?.bookingFlowInput()?.basketReference()!!,
            createPaymentCriteria = CreatePaymentCriteria(
                    booking = Booking(
                            businessSite = BusinessSite(
                                    identifier = this.paymentDetailsInput()?.bookingFlowInput()?.hotelCode()!!,
                                    name = this.paymentDetailsInput()?.bookingFlowInput()?.hotelName()!!,
                                    type = "HOTEL",
                                    location = this.paymentDetailsInput()?.bookingFlowInput()?.hotelCode()!!
                            ),
                            channel = ANDROID_APPS_CHANNEL,
                            journey = BOOKING_PAYMENT_JOURNEY,
                            language = language,
                            rooms = operaRooms,
                            type =  paymentType,
                            leadGuest = leadGuestDetails,
                            arrivalDate = arrivalDate.toString(),
                            departureDate = departureDate.toString()

                    ),
                    payment = Payment(
                            billing = Billing(
                                    address = Address(
                                            country = bookingAddress.countryCode(),
                                            addressLine1 = bookingAddress.toAddressDomain().line1,
                                            addressLine2 = bookingAddress.toAddressDomain().line2,
                                            addressLine3 = bookingAddress.toAddressDomain().line3,
                                            addressLine4 = bookingAddress.toAddressDomain().line4,
                                            postalCode = bookingAddress.toAddressDomain().postalCode
                                    ),
                                    email = this.paymentDetailsInput()?.bookerDetails()?.email().toString(),
                                    firstName = this.paymentDetailsInput()?.bookerDetails()?.firstName().toString(),
                                    lastName = this.paymentDetailsInput()?.bookerDetails()?.lastName().toString(),
                                    title = this.paymentDetailsInput()?.bookerDetails()?.title().toString()
                            ),
                            environment = environment,
                            subType = if (paypalNonce != null) "MIT" else "ECOMM",
                            type = if (paypalNonce != null) "PAYPAL" else if (isBusinessCard) "PIBA" else if (isGooglePaySelected) "WALLET_GOOGLE" else if (isSavedCardBusiness) "PIBA" else "CARD",
                            businessItems = if (isBusinessCard  || isInnBusinessUser == true) {
                                createBusinessItems(
                                    dinnerAllowance,
                                    dinnerBudget,
                                    alcoholAllowed,
                                    wifiAccessAllowed,
                                    carParkingAllowed,
                                    customerReference ?: EMPTY_STRING,
                                    purchaseOrder ?: EMPTY_STRING
                                )
                            } else {
                                null
                            },
                        // this is cardPresent not piba related atall - so if cnp is selected we send false
                            pibaCardPresent = if (this.paymentDetailsInput()?.paymentTimingChoice() != null &&
                                this.paymentDetailsInput()?.paymentTimingChoice() == PaymentTimingChoice.PAY_NOW) true else cnpForAuth,
                        paypalNonce = paypalNonce,
                        paypalDeviceData = paypalDeviceData,
                        card = storedCard
                    ),
                    requestId = UUID.randomUUID().toString(),
                    charityPackageCode = charityPackageCode ?: EMPTY_STRING,
                    hotelId = this.paymentDetailsInput()?.bookingFlowInput()?.hotelCode()!!,
                    isCiol = false,
                    companyQuestionAndAnswerDetails = if (isInnBusinessUser == true) {
                        CompanyQuestionAndAnswerDetails(this.additionalInformation().toUserDefinedQuestionAndAnswerList())
                    } else {
                        null
                }
            )
    )
}

fun List<AdditionalInformation>?.toUserDefinedQuestionAndAnswerList(): List<UserDefinedQuestionAndAnswer> {
    return this?.mapNotNull { info ->
        info.let {
            UserDefinedQuestionAndAnswer(
                question = it.question,
                answer = it.answer
            )
        }
    } ?: emptyList()
}

fun List<ParcelableDonationPackageDomain>.findCharityPackageCode(amount: Float): String {
    this.forEach { eachDPD ->
        if (eachDPD.unitPrice == amount) {
            return eachDPD.code
        }
    }
    return EMPTY_STRING_DOMAIN
}

fun List<ParcelableDonationPackageDomain>.getMaxAndMinDonationsInput() =
    this.map { it.unitPrice }.sorted().let { sortedList ->
        if (sortedList.size == 1) {
            DonationsInput(minDonation = sortedList.first())
        } else {
            DonationsInput(
                sortedList.last(),
                sortedList.first()
            )
        }
    }

fun createBusinessItems(
    dinnerAllowance: Boolean, dinnerBudget: Float,
    alcoholAllowed: Boolean, wifiAccessAllowed: Boolean,
    carParkingAllowed: Boolean, customerReference: String, purchaseOrder: String
): BusinessItems {
    val calculatedDinnerBudget: Float = if (dinnerAllowance && dinnerBudget != ZERO_BUDGET) {
        dinnerBudget
    } else {
        ZERO_BUDGET
    }

    return BusinessItems(
        purchaseOrderNumber = purchaseOrder,
        customReferenceNumber = customerReference,
        businessAllowances = listOf(
            BusinessAllowances(budget = calculatedDinnerBudget, allowance = "dinner", isAuthorised = dinnerAllowance),
            BusinessAllowances(budget = ZERO_BUDGET, allowance = "alcohol", isAuthorised = alcoholAllowed),
            BusinessAllowances(budget = ZERO_BUDGET, allowance = "carParking", isAuthorised = carParkingAllowed),
            BusinessAllowances(budget = ZERO_BUDGET, allowance = "ultimateWifi", isAuthorised = wifiAccessAllowed)
    ))
}