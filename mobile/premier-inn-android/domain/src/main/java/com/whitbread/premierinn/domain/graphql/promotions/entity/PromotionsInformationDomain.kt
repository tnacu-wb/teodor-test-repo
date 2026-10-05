package com.whitbread.premierinn.domain.graphql.promotions.entity

data class PromotionsInformationDomain(
    val showPromo: Boolean?,
    val isWithinPromoWindow: Boolean?,
    val promotionCode: String?,
    val landingPage: String?,
    val promoBannerColour: String?,
    val promoBannerIcon: String?,
    val termsLink: String?,
    val appPromoBannerTitle: String?,
    val appPromoBannerSubtitle: String?,
    val appPromoInvalidMessage: String?,
    val appPromoExpiredMessage: String?,
    val appPromoAmendMessage: String?,
    val promoBookingInfo: PromoBookingInfoDomain?,
    val promoBox: PromoBoxDomain?,
    val promoKind: String?,
    val promoBoxStatus: String?,
    val promoBoxMessageKey: String?
) {
    companion object {
        const val PROMO_KIND_SITE_WIDE = "SITE_WIDE"
        const val PROMO_BOX_STATUS_SUCCESS = "SUCCESS"

        // PromoBox Status Constants
        const val STATUS_INVALID = "INVALID"
        const val STATUS_EXPIRED = "EXPIRED"
        const val STATUS_CODE_EXPIRED = "CODE_EXPIRED"
        const val STATUS_CODE_ALREADY_APPLIED = "CODE_ALREADY_APPLIED"
        const val STATUS_MULTIPLE_REDEEM = "MULTIPLE_REDEEM"
        const val STATUS_UNAVAILABLE = "UNAVAILABLE"

        fun resolveMessageForStatus(status: String?, promoBox: PromoBoxDomain?): String? {
            if (promoBox == null) return null

            return when (status) {
                STATUS_CODE_ALREADY_APPLIED -> promoBox.whenCodeAlreadyApplied
                STATUS_MULTIPLE_REDEEM -> promoBox.whenMultipleRedeem
                STATUS_UNAVAILABLE -> promoBox.whenUnavailable
                STATUS_CODE_EXPIRED -> promoBox.whenCodeExpired
                STATUS_INVALID, STATUS_EXPIRED -> promoBox.whenInvalid
                else -> null
            }
        }

        fun createDefault():
                PromotionsInformationDomain {
            return PromotionsInformationDomain(
                showPromo = false,
                isWithinPromoWindow = false,
                promotionCode = null,
                landingPage = null,
                promoBannerColour = null,
                promoBannerIcon = null,
                termsLink = null,
                appPromoBannerTitle = null,
                appPromoBannerSubtitle = null,
                appPromoInvalidMessage = null,
                appPromoExpiredMessage = null,
                appPromoAmendMessage = null,
                promoBookingInfo = PromoBookingInfoDomain(null, null),
                promoBox = null,
                promoKind = null,
                promoBoxStatus = null,
                promoBoxMessageKey = null
            )
        }
    }
}

data class PromoBookingInfoDomain(
    val ratePlanCode: String?,
    val promotionCode: String?
)

data class PromoBoxDomain(
    val title: String?,
    val button: String?,
    val whenInvalid: String?,
    val whenMultipleRedeem: String?,
    val whenSuccess: String?,
    val whenEmpty: String?,
    val whenCodeAlreadyApplied: String?,
    val whenUnavailable: String?,
    val whenCodeExpired: String?
)
