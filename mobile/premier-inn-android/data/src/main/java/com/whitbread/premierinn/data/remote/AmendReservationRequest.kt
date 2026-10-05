package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate

interface AmendReservationRequest {

    data class ReservationRequest(@SerializedName("arrival") val arrivalDate: LocalDate,
                                  @SerializedName("departure") val departureDate: LocalDate,
                                  @SerializedName("rooms") val rooms: Int?,
                                  @SerializedName("roomRequests") val roomRequests: List<RoomRequests>?,
                                  @SerializedName("guests") val guests: List<Guest>?,
                                  @SerializedName("upsells") val upsells: List<UpsellItem>?,
                                  @SerializedName("breakfasts") val breakfasts: List<BreakfastRequest>?,
                                  @SerializedName("paymentCard") val paymentCard: PaymentCard?)

    data class BreakfastRequest(@SerializedName("adults") val adults: Int,
                                @SerializedName("children") val children: Int,
                                @SerializedName("code") val code: String,
                                @SerializedName("roomNumber") val roomNumber: Int)

    data class RoomRequests(@SerializedName("roomType") val roomType: String?,
                            @SerializedName("roomId") val roomId: String?,
                            @SerializedName("adults") val adults: Int?,
                            @SerializedName("cot") val cot: Boolean?,
                            @SerializedName("children") val children: Int?)


    data class Guest(@SerializedName("title") val title: String,
                     @SerializedName("firstName") val firstName: String,
                     @SerializedName("lastName") val lastName: String,
                     @SerializedName("guestHistoryNumber") val guestHistoryNumber: String?,
                     @SerializedName("roomNumber") val roomNumber: Int?)

    data class UpsellItem(@SerializedName("postingDate") val postingDate: LocalDate,
                          @SerializedName("quantity") val quantity: Int,
                          @SerializedName("code") val code: String,
                          @SerializedName("legend") val legend: String?,
                          @SerializedName("roomId") val roomId: String,
                          @SerializedName("roomNumber") val roomNumber: String,
                          @SerializedName("category") val category: String?,
                          @SerializedName("unitCost") val unitCost: ApiCommon.Price,
                          @SerializedName("subtotal") val subTotal: ApiCommon.Price)

    data class PaymentCard(@SerializedName("billingAddress") val billingAddress: ApiCommon.Address?,
                           @SerializedName("cardType")val cardType: String?,
                           @SerializedName("cardNumber")val cardNumber: String?,
                           @SerializedName("cardSecurityCode")val cardSecurityCode: String?,
                           @SerializedName("holdersFullName")val holdersFullName: String?,
                           @SerializedName("expiryDate")val expiryDate: String?,
                           @SerializedName("issueNumber")val issueNumber: String?,
                           @SerializedName("startDate")val startDate: String?,
                           @SerializedName("prepaymentRequired")val prepaymentRequired: Boolean,
                           @SerializedName("useExistingCard")val useExistingCard: Boolean)

    data class CompletePendingAmendRequest(@SerializedName("paymentAuthenticationResponse") val pares: String)
}