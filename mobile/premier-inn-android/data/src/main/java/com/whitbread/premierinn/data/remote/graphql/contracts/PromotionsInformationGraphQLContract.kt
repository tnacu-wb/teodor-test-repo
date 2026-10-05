package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface PromotionsInformationGraphQLContract {
    data class PromotionsInformationData(
        @SerializedName("data") val data: Data?,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("promotionsInformation") val promotionsInformation: PromotionsInformation?
    )

    data class PromotionsInformation(
        @SerializedName("showPromo") val showPromo: Boolean?,
        @SerializedName("isWithinPromoWindow") val isWithinPromoWindow: Boolean?,
        @SerializedName("promotionCode") val promotionCode: String?,
        @SerializedName("landingPage") val landingPage: String?,
        @SerializedName("promoBannerColour") val promoBannerColour: String?,
        @SerializedName("promoBannerIcon") val promoBannerIcon: String?,
        @SerializedName("termsLink") val termsLink: String?,
        @SerializedName("appPromoBannerTitle") val appPromoBannerTitle: String?,
        @SerializedName("appPromoBannerSubtitle") val appPromoBannerSubtitle: String?,
        @SerializedName("appPromoInvalidMessage") val appPromoInvalidMessage: String?,
        @SerializedName("appPromoExpiredMessage") val appPromoExpiredMessage: String?,
        @SerializedName("appPromoAmendMessage") val appPromoAmendMessage: String?,
        @SerializedName("promoBookingInfo") val promoBookingInfo: PromoBookingInfo?,
        @SerializedName("promoBox") val promoBox: PromoBox?,
        @SerializedName("promoKind") val promoKind: String?,
        @SerializedName("promoBoxStatus") val promoBoxStatus: String?,
        @SerializedName("promoBoxMessageKey") val promoBoxMessageKey: String?
    )

    data class PromoBookingInfo(
        @SerializedName("ratePlanCode") val ratePlanCode: String?,
        @SerializedName("promotionCode") val promotionCode: String?
    )

    data class PromoBox(
        @SerializedName("title") val title: String?,
        @SerializedName("button") val button: String?,
        @SerializedName("whenInvalid") val whenInvalid: String?,
        @SerializedName("whenMultipleRedeem") val whenMultipleRedeem: String?,
        @SerializedName("whenSuccess") val whenSuccess: String?,
        @SerializedName("whenEmpty") val whenEmpty: String?,
        @SerializedName("whenCodeAlreadyApplied") val whenCodeAlreadyApplied: String?,
        @SerializedName("whenUnavailable") val whenUnavailable: String?,
        @SerializedName("whenCodeExpired") val whenCodeExpired: String?
    )
}