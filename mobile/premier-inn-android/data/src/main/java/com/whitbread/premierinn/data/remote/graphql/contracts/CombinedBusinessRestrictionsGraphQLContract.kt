package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface CombinedBusinessRestrictionsGraphQLContract {
    data class CombinedBusinessRestrictionsResponse(
        @SerializedName("data") val data: CombinedBusinessRestrictionsData?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>? = null
    )

    data class CombinedBusinessRestrictionsData(
        @SerializedName("roomsLimitationPI") val roomsLimitationPI: BusinessRulesGraphQLContract.MaxRoomsLimitation?,
        @SerializedName("arrivalDateLimitationPI") val arrivalDateLimitationPI: BusinessRulesGraphQLContract.MaxArrivalDateLimitation?,
        @SerializedName("nightsLimitationPI") val nightsLimitationPI: BusinessRulesGraphQLContract.MaxNightsLimitation?,
        @SerializedName("roomsLimitationBB") val roomsLimitationBB: BusinessRulesGraphQLContract.MaxRoomsLimitation?,
        @SerializedName("arrivalDateLimitationBB") val arrivalDateLimitationBB: BusinessRulesGraphQLContract.MaxArrivalDateLimitation?,
        @SerializedName("nightsLimitationBB") val nightsLimitationBB: BusinessRulesGraphQLContract.MaxNightsLimitation?,
        @SerializedName("roomsLimitationEMPLOYEE") val roomsLimitationEMPLOYEE: BusinessRulesGraphQLContract.MaxRoomsLimitation?,
        @SerializedName("arrivalDateLimitationEMPLOYEE") val arrivalDateLimitationEMPLOYEE: BusinessRulesGraphQLContract.MaxArrivalDateLimitation?,
        @SerializedName("nightsLimitationEMPLOYEE") val nightsLimitationEMPLOYEE: BusinessRulesGraphQLContract.MaxNightsLimitation?
    )
}