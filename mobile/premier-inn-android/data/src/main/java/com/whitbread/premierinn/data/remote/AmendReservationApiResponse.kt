package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName

interface AmendReservationApiResponse {

    data class AmendReservationResponse(@SerializedName("sessionId") val sessionId: String?,
                                        @SerializedName("confirmationNumber") val confirmationNumber: String?,
                                        @SerializedName("checkInOnline") val checkInOnline: Boolean?,
                                        @SerializedName("totalCost") val totalCost: ApiCommon.Price?,
                                        @SerializedName("cityTax") val cityTax: ApiCommon.Price?,
                                        @SerializedName("vatRate") val vatRate: Float?,
                                        @SerializedName("carData") val carData: CarData?,
                                        @SerializedName("prepaymentSuccess") val prepaymentSuccess: Boolean,
                                        @SerializedName("payOnArrivalSuccess") val payOnArrivalSuccess: Boolean,
                                        @SerializedName("prepaymentText") val prepaymentText: String?,
                                        @SerializedName("pendingAmendId") val pendingAmendId: String?)

    data class CarData(@SerializedName("required") val title: String?,
                       @SerializedName("carParkOperator") val carParkOperator: String?,
                       @SerializedName("hotelCode") val hotelCode: String?,
                       @SerializedName("stayLength") val stayLength: Int?)

}
