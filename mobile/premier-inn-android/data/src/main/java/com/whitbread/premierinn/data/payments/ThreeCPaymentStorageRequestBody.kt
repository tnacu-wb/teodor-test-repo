package com.whitbread.premierinn.data.payments

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.Breakfast
import com.whitbread.premierinn.domain.common.BusinessAccount
import com.whitbread.premierinn.domain.common.GBP_LABEL
import com.whitbread.premierinn.domain.common.GuestDetailsPayment
import com.whitbread.premierinn.domain.common.PaymentCardEntity
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.payment.entity.ThreeCpPaymentStorageDetailsEntity
import java.util.*


data class ThreeCPaymentStorageRequestBody(
        @SerializedName("arrivalTime")
        val arrivalTime: String? = null,
        @SerializedName("booker")
        val booker: BookerDetailsPayment,
        @SerializedName("breakfasts")
        val breakfasts: List<BreakfastDetails>,
        @SerializedName("business")
        val business: Boolean,
        @SerializedName("dinners")
        val dinners: List<DinnerDetails>? = null,
        @SerializedName("cbtAnswers")
        val cbtAnswers: List<CbtAnswer>? = emptyList(),
        @SerializedName("donation")
        val donation: DonationDetails,
        @SerializedName("guaranteeMethod")
        val guaranteeMethod: String? = null,
        @SerializedName("guests")
        val guests: List<GuestDetailsPaymentData>,
        @SerializedName("paymentCard")
        val paymentCard: PaymentCardDetails,
        @SerializedName("paymentId")
        val paymentId: String,
        @SerializedName("referrer")
        val referrer: String? = null,
        @SerializedName("sms")
        val sms: Boolean? = false,
        @SerializedName("specialRequirements")
        val specialRequirements: String? = null,
        @SerializedName("upsellItems")
        val upsellItems: List<UpsellItemPayment>? = emptyList()
) {
    companion object {
        fun createPaymentStorageRequest(
                threeCpPaymentStorageDetailsEntity: ThreeCpPaymentStorageDetailsEntity,
        ): ThreeCPaymentStorageRequestBody {
            return ThreeCPaymentStorageRequestBody(
                    booker = BookerDetailsPayment.createBookerPaymentRequest(threeCpPaymentStorageDetailsEntity),
                    breakfasts = BreakfastDetails.createBreakfast(threeCpPaymentStorageDetailsEntity.breakfast),
                    business = threeCpPaymentStorageDetailsEntity.business,
                    donation = DonationDetails.createDonationRequest(threeCpPaymentStorageDetailsEntity.donation),
                    guests = GuestDetailsPaymentData.createGuestRequest(threeCpPaymentStorageDetailsEntity.guests),
                    paymentCard = PaymentCardDetails.createPaymentDataRequest(threeCpPaymentStorageDetailsEntity.paymentCard),
                    paymentId = threeCpPaymentStorageDetailsEntity.paymentId)
        }
    }
}

data class BookerDetailsPayment(
        @SerializedName("address")
        val address: AddressDetailsPayment,
        @SerializedName("companyId")
        val companyId: String? = null,
        @SerializedName("companyName")
        val companyName: String? = null,
        @SerializedName("emailAddress")
        val emailAddress: String,
        @SerializedName("employeeId")
        val employeeId: String? = null,
        @SerializedName("firstName")
        val firstName: String,
        @SerializedName("guestHistoryNumber")
        val guestHistoryNumber: String,
        @SerializedName("lastName")
        val lastName: String,
        @SerializedName("mobileNumber")
        val mobileNumber: String,
        @SerializedName("telephoneNumber")
        val telephoneNumber: String,
        @SerializedName("title")
        val title: String
) {
    companion object {
        fun createBookerPaymentRequest(
                threeCpPaymentStorageDetailsEntity: ThreeCpPaymentStorageDetailsEntity
        ): BookerDetailsPayment {
            return BookerDetailsPayment(
                    address = AddressDetailsPayment.createAddressRequest(threeCpPaymentStorageDetailsEntity.booker.address),
                    companyName = threeCpPaymentStorageDetailsEntity.booker.address.companyName,
                    emailAddress = threeCpPaymentStorageDetailsEntity.booker.emailAddress,
                    firstName = threeCpPaymentStorageDetailsEntity.booker.firstName,
                    guestHistoryNumber = threeCpPaymentStorageDetailsEntity.booker.guestHistoryNumber,
                    lastName = threeCpPaymentStorageDetailsEntity.booker.lastName,
                    mobileNumber = threeCpPaymentStorageDetailsEntity.booker.telephoneNumber,
                    telephoneNumber = threeCpPaymentStorageDetailsEntity.booker.telephoneNumber,
                    title = threeCpPaymentStorageDetailsEntity.booker.title
            )
        }
    }
}
data class AddressDetailsPayment(
        @SerializedName("companyName")
        val companyName: String? = null,
        @SerializedName("countryCode")
        val countryCode: String? = null,
        @SerializedName("line1")
        val line1: String? = null,
        @SerializedName("line2")
        val line2: String? = null,
        @SerializedName("line3")
        val line3: String? = null,
        @SerializedName("line4")
        val line4: String?= null,
        @SerializedName("line5")
        val line5: String? = null,
        @SerializedName("postcode")
        val postcode: String? = null
) {
    companion object {
        fun createAddressRequest(
                address: Address
        ): AddressDetailsPayment {
            return AddressDetailsPayment(
                    companyName = address.companyName,
                    countryCode = address.countryCode,
                    line1 = address.line1,
                    line2 = address.line2,
                    postcode = address.postCode
            )
        }
    }
}


data class BreakfastDetails(
        @SerializedName("adults")
        val adults: Int,
        @SerializedName("children")
        val children: Int,
        @SerializedName("code")
        val code: String,
        @SerializedName("roomNumber")
        val roomNumber: Int
) {
    companion object {
        fun createBreakfast(breakfastList: List<Breakfast>): List<BreakfastDetails> {
            val listOfBreakfast = mutableListOf<BreakfastDetails>()
            breakfastList.forEach { breakfast ->
                listOfBreakfast.add(
                        BreakfastDetails(breakfast.adults, breakfast.children, breakfast.code, breakfast.roomNumber))
            }

            return listOfBreakfast
        }
    }
}

data class DinnerDetails(
        @SerializedName("date")
        val date: String,
        @SerializedName("diners")
        val diners: Int,
        @SerializedName("time")
        val time: String
)

data class DonationDetails(
        @SerializedName("amount")
        val amount: Float,
        @SerializedName("currency")
        val currency: String
) {
    companion object {
        fun createDonationRequest(priceDomain: PriceDomain): DonationDetails {
            return DonationDetails(priceDomain.amount, priceDomain.currency)
        }
    }
}

data class GuestDetailsPaymentData(
        @SerializedName("firstName")
        val firstName: String,
        @SerializedName("guestHistoryNumber")
        val guestHistoryNumber: String? = null,
        @SerializedName("guestId")
        val guestId: Int? = null,
        @SerializedName("lastName")
        val lastName: String,
        @SerializedName("roomNumber")
        val roomNumber: Int,
        @SerializedName("title")
        val title: String
) {
    companion object {
        fun createGuestRequest(guestList: List<GuestDetailsPayment>): List<GuestDetailsPaymentData> {
            val listOfGuestData = mutableListOf<GuestDetailsPaymentData>()
            guestList.forEach { guest ->
                listOfGuestData.add(GuestDetailsPaymentData(
                        firstName = guest.firstName,
                        lastName = guest.lastName,
                        roomNumber = guest.roomNumber,
                        title = guest.title
                ))
            }
            return listOfGuestData
        }
    }
}

data class PaymentCardDetails(
        @SerializedName("billingAddress")
        val billingAddress: BillingAddressPayment,
        @SerializedName("businessAccount")
        val businessAccount: BusinessAccountPayment? = null,
        @SerializedName("cardNumber")
        val cardNumber: String? = null,
        @SerializedName("cardSecurityCode")
        val cardSecurityCode: String? = null,
        @SerializedName("cardType")
        val cardType: String? = null,
        @SerializedName("cardholderName")
        val cardholderName: String? = null,
        @SerializedName("cbtCentralCardId")
        val cbtCentralCardId: String? = null,
        @SerializedName("cbtEmployeeCardId")
        val cbtEmployeeCardId: String? = null,
        @SerializedName("expiryDate")
        val expiryDate: String? = null,
        @SerializedName("issueNumber")
        val issueNumber: String? = null,
        @SerializedName("paymentAuthenticationResponse")
        val paymentAuthenticationResponse: String? = null,
        @SerializedName("prepaymentRequired")
        val prepaymentRequired: Boolean? = null,
        @SerializedName("startDate")
        val startDate: String? = null,
        @SerializedName("useExistingCard")
        val useExistingCard: Boolean
) {
    companion object {
        fun createPaymentDataRequest(paymentCardEntity: PaymentCardEntity): PaymentCardDetails {
            return PaymentCardDetails(
                    billingAddress = BillingAddressPayment.createBillingAddress(paymentCardEntity.billingAddress),
                    businessAccount = BusinessAccountPayment.createBusinessAccountRequest(paymentCardEntity.businessAccount),
                    cardNumber = paymentCardEntity.cardNumber,
                    cardSecurityCode = paymentCardEntity.cardSecurityCode,
                    cardType = paymentCardEntity.cardType,
                    cardholderName = paymentCardEntity.cardholderName,
                    expiryDate = paymentCardEntity.expiryDate,
                    issueNumber = paymentCardEntity.issueNumber,
                    paymentAuthenticationResponse = paymentCardEntity.paymentAuthenticationResponse,
                    prepaymentRequired = paymentCardEntity.prepaymentRequired,
                    startDate = paymentCardEntity.startDate,
                    useExistingCard = paymentCardEntity.useExistingCard
            )
        }

    }

    data class BillingAddressPayment(
            @SerializedName("countryCode")
            val countryCode: String? = null,
            @SerializedName("line1")
            val line1: String,
            @SerializedName("line2")
            val line2: String? = EMPTY_STRING,
            @SerializedName("line3")
            val line3: String? = EMPTY_STRING,
            @SerializedName("line4")
            val line4: String? = EMPTY_STRING,
            @SerializedName("line5")
            val line5: String? = EMPTY_STRING,
            @SerializedName("postcode")
            val postcode: String? = null
    ) {
        companion object {
            fun createBillingAddress(address: Address): BillingAddressPayment {
                return BillingAddressPayment(
                        line1 = address.line1,
                        line2 = address.line2,
                        postcode = address.postCode,
                        countryCode = address.countryCode)
            }
        }
    }

    data class BusinessAccountPayment(
            @SerializedName("alcoholAllowed")
            val alcoholAllowed: Boolean,
            @SerializedName("atosPassword")
            val atosPassword: String? = null,
            @SerializedName("atosUsername")
            val atosUsername: String? = null,
            @SerializedName("breakfastCode")
            val breakfastCode: Int? = null,
            @SerializedName("carParkingAllowed")
            val carParkingAllowed: Boolean,
            @SerializedName("cardNotPresentAuth")
            val cardNotPresentAuth: Boolean,
            @SerializedName("customerReference")
            val customerReference: String? = null,
            @SerializedName("dinnerAllowance")
            val dinnerAllowance: DinnerAllowancePayment?,
            @SerializedName("otherChargesAllowed")
            val otherChargesAllowed: Boolean? = null,
            @SerializedName("purchaseOrder")
            val purchaseOrder: String? = null,
            @SerializedName("wifiAccessAllowed")
            val wifiAccessAllowed: Boolean
    ) {
        companion object {
            fun createBusinessAccountRequest(businessAccount: BusinessAccount?): BusinessAccountPayment? {
                businessAccount?.let {
                    return BusinessAccountPayment(
                            alcoholAllowed = businessAccount.alcoholAllowed,
                            atosPassword = businessAccount.atosPassword,
                            atosUsername = businessAccount.atosUsername,
                            breakfastCode = businessAccount.breakfastCode,
                            carParkingAllowed = businessAccount.carParkingAllowed,
                            cardNotPresentAuth = businessAccount.cardNotPresentAuth,
                            customerReference = businessAccount.customerReference,
                            dinnerAllowance = DinnerAllowancePayment.createDinnerAllowanceRequest(businessAccount.dinnerAllowance),
                            otherChargesAllowed = businessAccount.otherChargesAllowed,
                            purchaseOrder = businessAccount.purchaseOrder,
                            wifiAccessAllowed = businessAccount.wifiAccessAllowed
                    )
                }
                return null
            }
        }
    }

    data class DinnerAllowancePayment(
            @SerializedName("amount")
            val amount: Float,
            @SerializedName("currency")
            val currency: String
    ) {
        companion object {
            fun createDinnerAllowanceRequest(priceDomain: PriceDomain?): DinnerAllowancePayment {
                priceDomain?.let {
                    return DinnerAllowancePayment(priceDomain.amount, priceDomain.currency)
                } ?: return DinnerAllowancePayment(0f, GBP_LABEL)
            }
        }
    }
}

data class UpsellItemPayment(
        @SerializedName("category")
        val category: String,
        @SerializedName("code")
        val code: String,
        @SerializedName("legend")
        val legend: String,
        @SerializedName("postingDate")
        val postingDate: String,
        @SerializedName("quantity")
        val quantity: Int,
        @SerializedName("roomId")
        val roomId: String,
        @SerializedName("roomNumber")
        val roomNumber: String,
        @SerializedName("subtotal")
        val subtotal: PriceDomain,
        @SerializedName("unitCost")
        val unitCost: PriceDomain
)

data class CbtAnswer(
        @SerializedName("answer")
        val answer: String,
        @SerializedName("questionId")
        val questionId: String
)


