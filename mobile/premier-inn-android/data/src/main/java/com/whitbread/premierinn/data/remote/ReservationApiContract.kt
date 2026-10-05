package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate

/**
 *
 */
interface ReservationApiContract {

    data class ReservationResponse(@SerializedName("sessionId") val sessionId: String?,
                                   @SerializedName("reservationDetails") val details: ReservationDetails?,
                                   @SerializedName("upsellItemsAvailable") val upsellItemsAvailable : List<UpsellItemsAvailable>)

    data class UpsellItemsAvailable(@SerializedName("description") val description: String?,
                                    @SerializedName("foodUpsell") val foodUpsell: Boolean,
                                    @SerializedName("freeBreakfastTrigger") val freeBreakfastTrigger: Boolean,
                                    @SerializedName("availableForChildren") val availableForChildren: Boolean,
                                    @SerializedName("code") val code: String,
                                    @SerializedName("freeBreakfastCode") val freeBreakfastCode: String?,
                                    @SerializedName("legend") val legend: String,
                                    @SerializedName("unitCost") val unitCost: ApiCommon.Price,
                                    @SerializedName("freeBreakfastOption") val freeBreakfastOption: Boolean,
                                    @SerializedName("attachments") val attachments: List<Attachments>?)

    data class Attachments(@SerializedName("path") val path: String?,
                           @SerializedName("label") val label: String?)

    data class ReservationDetails(@SerializedName("confirmationNumber") val confirmationNumber: String?,
                                  @SerializedName("sessionId") val sessionId: String?,
                                  @SerializedName("hotelCode") val hotelCode: String?,
                                  @SerializedName("arrivalDate") val arrivalDate: LocalDate,
                                  @SerializedName("departureDate") val departureDate: LocalDate,
                                  @SerializedName("rooms") val rooms: List<Room?>?,
                                  @SerializedName("rateClass") val rateClass: String,
                                  @SerializedName("rateText") val rateText: String?,
                                  @SerializedName("totalCost") val totalCost: ApiCommon.Price?,
                                  @SerializedName("cityTax") val cityTax: ApiCommon.Price?,
                                  @SerializedName("booker") val booker: CheckInGuest,
                                  @SerializedName("upsellBreakdown") val upsellBreakdown: UpsellBreakdown?,
                                  @SerializedName("roomBreakdown") val roomBreakdown: List<RoomBreakdown>?,
                                  @SerializedName("amendable") val amendable: Boolean?,
                                  @SerializedName("cancelable") val cancelable: Boolean?,
                                  @SerializedName("cancelableText") val cancelableText: String?,
                                  @SerializedName("cardFeeApplies") val cardFeeApplies: Boolean,
                                  @SerializedName("business") val business: Boolean?,
                                  @SerializedName("changeCard") val changeCard: Boolean?,
                                  @SerializedName("payment") val paymentDetails: PaymentDetails?,
                                  @SerializedName("prepaidAmount") val prepaidAmount: ApiCommon.Price?,
                                  @SerializedName("amendRestrictions") val amendRestrictions: AmendRestriction?)

    data class Room(@SerializedName("roomType") val roomType: String?,
                    @SerializedName("roomId") val roomId: String?,
                    @SerializedName("checkInGuest") val guest: CheckInGuest?,
                    @SerializedName("adults") val adults: Int?,
                    @SerializedName("cot") val cot: Boolean?,
                    @SerializedName("children") val children: Int?)

    data class CheckInGuest(@SerializedName("roomId") val roomId: String?,
                            @SerializedName("guestId") val guestId: String?,
                            @SerializedName("title") val title: String,
                            @SerializedName("firstName") val firstName: String,
                            @SerializedName("lastName") val lastName: String,
                            @SerializedName("emailAddress") val emailAddress: String?,
                            @SerializedName("nationality") val nationalityCountryCode: String?,
                            @SerializedName("address") val address: ApiCommon.Address?,
                            @SerializedName("guestHistoryNumber") val guestHistoryNumber: String?,
                            @SerializedName("passportNumber") val passportNumber: String?,
                            @SerializedName("nextDestination") val nextDestination: String?,
                            @SerializedName("placeOfIssue") val placeOfIssue: String?,
                            @SerializedName("mobileNumber") val mobileNumber: String?,
                            @SerializedName("telephoneNumber") val telephoneNumber: String?,
                            @SerializedName("carRegistration") val carRegistration: String?,
                            @SerializedName("additionalGuests") val additionalGuests: List<AdditionalGuest>?)

    data class AdditionalGuest(@SerializedName("title") val title: String?,
                               @SerializedName("firstName") val firstName: String?,
                               @SerializedName("lastName") val lastName: String?,
                               @SerializedName("carRegistration") val carRegistration: String?)

    data class UpsellBreakdown(@SerializedName("upsellItems") val upsells: List<ApiCommon.UpsellItem>?)

    data class RoomBreakdown(@SerializedName("totalRoomCost") val totalRoomCost: ApiCommon.Price?,
                             @SerializedName("roomId") val roomId: String?)

    data class PaymentDetails(@SerializedName("billingAddress") val billingAddress: ApiCommon.Address?,
                              @SerializedName("cardType") val cardType: String?,
                              @SerializedName("cardNumber") val cardNumber: String?,
                              @SerializedName("cardSecurityCode") val cardSecurityCode: String?,
                              @SerializedName("cardholderName") val cardholderName: String?,
                              @SerializedName("expiryDate") val expiryDate: String?,
                              @SerializedName("startDate") val startDate: String?,
                              @SerializedName("issueNumber") val issueNumber: String?,
                              @SerializedName("prepaymentRequired") val prepaymentRequired: Boolean,
                              @SerializedName("useExistingCard") val useExistingCard: Boolean)

    data class AmendRestriction(@SerializedName("nights") val nights: Boolean,
                                @SerializedName("rooms") val rooms: Boolean,
                                @SerializedName("guestNames") val guestNames: Boolean,
                                @SerializedName("upsell") val upsell: Boolean,
                                @SerializedName("restricted") val restricted: Boolean)
}