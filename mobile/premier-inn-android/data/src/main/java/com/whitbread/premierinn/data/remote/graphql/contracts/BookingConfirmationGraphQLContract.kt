package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.common.toLocalDate
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase
import com.whitbread.premierinn.data.remote.graphql.contracts.AmendSummaryGraphQLContract.AmendSummary
import org.threeten.bp.temporal.ChronoUnit

interface BookingConfirmationGraphQLContract{

    data class BookingConfirmationData(
            @SerializedName("data") val data: Data,
            @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?)

    data class Data (
        @SerializedName("bookingConfirmation") val bookingConfirmation: BookingConfirmation?,
        @SerializedName("manageBooking") val manageBooking: ManageBooking?,
        @SerializedName("packages") val packages: PackagesGraphQLContract.Packages?,
        @SerializedName("amendSummary") val amendSummary: AmendSummary?
    )

    data class BookingConfirmation (
            @SerializedName("balanceOutstanding") val balanceOutstanding: Float? = null,
            @SerializedName("bookingFlowId") val bookingFlowID: String,
            @SerializedName("currencyCode") val currencyCode: String,
            @SerializedName("bookingReference") val bookingReference: String,
            @SerializedName("hotelId") val hotelId: String,
            @SerializedName("infoMessages") val infoMessages: List<String>,
            @SerializedName("newTotal") val newTotal: Float? = null,
            @SerializedName("policyCode") val policyCode: String,
            @SerializedName("previousTotal") val previousTotal: Float? = null,
            @SerializedName("reservationByIdList") val reservationByIdList: List<ReservationByIdConfirmation>,
            @SerializedName("totalCost") val totalCost: Float,
            @SerializedName("upgradeToFlex") val upgradeToFlex: UpgradeToFlex,
            @SerializedName("basketStatus") val basketStatus: String?,
            @SerializedName("isThirdPartyBooking") val isThirdPartyBooking: Boolean,
            @SerializedName("paymentOption") val paymentOption: String?,
            @SerializedName("upsellsAddonsEnabled") val upsellsAddonsEnabled: Boolean
    ) {
        val totalNumberOfAdults: Int
            get() {
                var totalAdults = 0
                reservationByIdList.forEach { room ->
                    totalAdults += room.roomStay.adultsNumber.toInt()
                }
                return totalAdults
            }

        val totalNumberOfChildren: Int
            get() {
                var totalChildren = 0
                reservationByIdList.forEach { room ->
                    totalChildren += room.roomStay.childrenNumber.toInt()
                }
                return totalChildren
            }
    }

    data class UpgradeToFlex(
            @SerializedName("amount") val amount: Float,
            @SerializedName("currencyCode") val currencyCode: String? = null,
            @SerializedName("flexRateCode") val flexRateCode: String
    )

    data class ReservationByIdConfirmation (
            @SerializedName("reservationId") val reservationId: String,
            @SerializedName("reservationStatus") val reservationStatus: String,
            @SerializedName("reservationGuestList") val reservationGuestList : List<GuestInfo>,
            @SerializedName("roomStay") val roomStay: RoomStayConfirmation,
            @SerializedName("depositPolicies") val depositPolicies: List<DepositPolicy>,
            @SerializedName("billing") val billing: BillingResponse?,
            @SerializedName("reservationPackageList") val  reservationPackageList: List<ReservationPackagesDetails>,
            @SerializedName("preferences") val preferences: List<HotelPreference>?,
            @SerializedName("preCheckInStatus") val preCheckInStatus: Boolean?,
            @SerializedName("additionalGuestInfo") val additionalGuestInfo: AdditionalGuestInfo?
    )

    data class HotelPreference(
        @SerializedName("code") val code: String,
        @SerializedName("preferenceType") val preferenceCode: String
    )

    data class AdditionalGuestInfo(
        @SerializedName("purposeOfStay") val purposeOfStay: String
    )

    data class RoomStayConfirmation (
        @SerializedName("adultsNumber") val adultsNumber: Long,
        @SerializedName("childrenNumber") val childrenNumber: Long,
        @SerializedName("cot") val cot: Boolean,
        @SerializedName("roomType") val roomType: String,
        @SerializedName("ratePlanCode") val ratePlanCode: String,
        @SerializedName("arrivalDate") val arrivalDate: String,
        @SerializedName("departureDate") val departureDate: String,
        @SerializedName("rateExtraInfo") val rateExtraInfo: RateExtraInfo?,
        @SerializedName("roomExtraInfo") val roomExtraInfo: RoomExtraInfo,
        @SerializedName("accessibleRoom") val accessibleRoom : AccessibleRoomInfo,
        @SerializedName("roomPrice") val roomPrice : Float,
        @SerializedName("ratesPerNight") val ratesPerNight : List<RatePerNightInfo>
    ) {
        val numberOfNights: Int
            get() {
                return ChronoUnit.DAYS.between(arrivalDate.toLocalDate(), departureDate.toLocalDate()).toInt()
            }

        }

    data class GuestInfo (
            @SerializedName("nameTitle") val title: String?,
            @SerializedName("givenName") val givenName: String,
            @SerializedName("surName") val surName: String,
            @SerializedName("email") val email: String?,
            @SerializedName("additionalDetails") val additionalDetails: GuestInfoAdditionalDetails?,
            @SerializedName("isAccompanyingGuest") val isAccompanyingGuest: Boolean,
            @SerializedName("profileId") val profileId: String?,
            @SerializedName("address") val address: Address?
    )

    data class GuestInfoAdditionalDetails(
        @SerializedName("dob") val dob: String?,
        @SerializedName("passportNumber") val passportNumber: String?,
        @SerializedName("nationality") val nationality: String?,
    )

    data class RateExtraInfo (
            @SerializedName("rateClassification") val rateClassification: String?,
            @SerializedName("rateDescription") val rateDescription: String?,
            @SerializedName("rateLongDescription") val rateLongDescription: String?,
            @SerializedName("rateName") val rateName: String?,
            @SerializedName("rateNotes") val rateNotes: String?,
            @SerializedName("rateOrder") val rateOrder: String?
    )

    data class RoomExtraInfo (
            @SerializedName("roomDescription") val roomDescription: String,
            @SerializedName("roomName") val roomName: String,
            @SerializedName("roomType") val roomType: String
    )

    data class AccessibleRoomInfo (
            @SerializedName("phoneNumber") val phoneNumber: String,
            @SerializedName("isAccessible") val isAccessible : Boolean
    )

    data class RatePerNightInfo (
            @SerializedName("startDate") val startDate: String,
            @SerializedName("pricePerNight ") val pricePerNight : Float
    )

    data class DepositPolicy (
            @SerializedName("amountDue") val amountDue: Amount,
            @SerializedName("amountPaid") val amountPaid: Amount,
            @SerializedName("policyCode") val policyCode: String
    )

    data class Amount (
            @SerializedName("amount") val amount: Float,
            @SerializedName("currencyCode") val currencyCode: String
    )

    data class BillingResponse (
            @SerializedName("email") val email: String,
            @SerializedName("address") val address: Address?,
            @SerializedName("firstName") val firstName: String,
            @SerializedName("lastName") val lastName: String,
            @SerializedName("telephone") val telephone: String?,
            @SerializedName("title") val title: String?
    )

    data class ReservationPackagesDetails (
            @SerializedName("description") val description: String,
            @SerializedName("packageCode") val packageCode: String,
            @SerializedName("unitPrice") val unitPrice: Float,
            @SerializedName("totalQuantity") val totalQuantity: Int,
            @SerializedName("computedPrice") val computedPrice: Float
    )

    data class Address (
            @SerializedName("addressLine1") val addressLine1: String?,
            @SerializedName("addressLine2") val addressLine2: String?,
            @SerializedName("addressLine3") val addressLine3: String?,
            @SerializedName("addressLine4") val addressLine4: String? = null,
            @SerializedName("cityName") val cityName: String?,
            @SerializedName("country") val country: String?,
            @SerializedName("countryCode") val countryCode: String?,
            @SerializedName("postalCode") val postalCode: String?
    )

    data class ManageBooking(
        @SerializedName("isAmendable") val isAmendable: Boolean,
        @SerializedName("isCancellable") val isCancellable: Boolean,
        @SerializedName("isCheckInOnlineAvailable") val isCheckInOnlineAvailable: Boolean,
        @SerializedName("isCheckOutOnlineAvailable") val isCheckOutOnlineAvailable: Boolean
    )
}


