package com.whitbread.premierinn.amend

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelablePromotionsInformationDomain(
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
    val promoBookingInfo: ParcelablePromoBookingInfoDomain?,
    val promoBox: ParcelablePromoBoxDomain?,
    val promoKind: String?,
    val promoBoxStatus: String?,
    val promoBoxMessageKey: String?
) : Parcelable


@Parcelize
data class ParcelablePromoBookingInfoDomain(
    val ratePlanCode: String?,
    val promotionCode: String?
) : Parcelable

@Parcelize
data class ParcelablePromoBoxDomain(
    val title: String?,
    val button: String?,
    val whenInvalid: String?,
    val whenMultipleRedeem: String?,
    val whenSuccess: String?,
    val whenEmpty: String?,
    val whenCodeAlreadyApplied: String?,
    val whenUnavailable: String?,
    val whenCodeExpired: String?
) : Parcelable
