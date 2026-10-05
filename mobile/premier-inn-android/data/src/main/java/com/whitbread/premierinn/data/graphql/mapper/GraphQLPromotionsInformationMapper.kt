package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.PromotionsInformationGraphQLContract
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromoBookingInfoDomain
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromoBoxDomain

fun PromotionsInformationGraphQLContract.PromotionsInformationData.mapToPromotionsInformationDomain(): PromotionsInformationDomain {
    val info = this.data?.promotionsInformation
    return PromotionsInformationDomain(
        showPromo = info?.showPromo,
        isWithinPromoWindow = info?.isWithinPromoWindow,
        promotionCode = info?.promotionCode,
        landingPage = info?.landingPage,
        promoBannerColour = info?.promoBannerColour,
        promoBannerIcon = info?.promoBannerIcon,
        termsLink = info?.termsLink,
        appPromoBannerTitle = info?.appPromoBannerTitle,
        appPromoBannerSubtitle = info?.appPromoBannerSubtitle,
        appPromoInvalidMessage = info?.appPromoInvalidMessage,
        appPromoExpiredMessage = info?.appPromoExpiredMessage,
        appPromoAmendMessage = info?.appPromoAmendMessage,
        promoBookingInfo = info?.promoBookingInfo?.let {
            PromoBookingInfoDomain(
                ratePlanCode = it.ratePlanCode,
                promotionCode = it.promotionCode
            )
        },
        promoBox = info?.promoBox?.let {
            PromoBoxDomain(
                title = it.title,
                button = it.button,
                whenInvalid = it.whenInvalid,
                whenMultipleRedeem = it.whenMultipleRedeem,
                whenSuccess = it.whenSuccess,
                whenEmpty = it.whenEmpty,
                whenCodeAlreadyApplied = it.whenCodeAlreadyApplied,
                whenUnavailable = it.whenUnavailable,
                whenCodeExpired = it.whenCodeExpired
            )
        },
        promoKind = info?.promoKind,
        promoBoxStatus = info?.promoBoxStatus,
        promoBoxMessageKey = info?.promoBoxMessageKey
    )
}