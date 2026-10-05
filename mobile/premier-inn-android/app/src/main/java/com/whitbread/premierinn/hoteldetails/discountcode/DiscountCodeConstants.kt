package com.whitbread.premierinn.hoteldetails.discountcode

import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain

object DiscountCodeConstants {

    // Promo Box Status values
    const val SUCCESS: String = "SUCCESS"
    const val INVALID: String = "INVALID"
    const val CODE_EXPIRED: String = "CODE_EXPIRED"
    const val UNAVAILABLE: String = "UNAVAILABLE"
    const val CODE_ALREADY_APPLIED: String = "CODE_ALREADY_APPLIED"
    const val MULTIPLE_REDEEM: String = "MULTIPLE_REDEEM"

    // Promo Box Message Keys
    private const val KEY_WHEN_INVALID = "whenInvalid"
    private const val KEY_WHEN_CODE_EXPIRED = "whenCodeExpired"
    private const val KEY_WHEN_UNAVAILABLE = "whenUnavailable"
    private const val KEY_WHEN_CODE_ALREADY_APPLIED = "whenCodeAlreadyApplied"
    private const val KEY_WHEN_MULTIPLE_REDEEM = "whenMultipleRedeem"
    private const val KEY_WHEN_SUCCESS = "whenSuccess"
    private const val KEY_WHEN_EMPTY = "whenEmpty"

    fun getMessageKeyForStatus(status: String): String? {
        return when (status) {
            INVALID -> KEY_WHEN_INVALID
            CODE_EXPIRED -> KEY_WHEN_CODE_EXPIRED
            UNAVAILABLE -> KEY_WHEN_UNAVAILABLE
            CODE_ALREADY_APPLIED -> KEY_WHEN_CODE_ALREADY_APPLIED
            MULTIPLE_REDEEM -> KEY_WHEN_MULTIPLE_REDEEM
            SUCCESS -> KEY_WHEN_SUCCESS
            else -> null
        }
    }

    fun resolvePromoBoxMessage(
        promoResponse: PromotionsInformationDomain,
        messageKey: String?
    ): String? {
        val promoBox = promoResponse.promoBox ?: return null
        return when (messageKey) {
            KEY_WHEN_INVALID -> promoBox.whenInvalid
            KEY_WHEN_MULTIPLE_REDEEM -> promoBox.whenMultipleRedeem
            KEY_WHEN_SUCCESS -> promoBox.whenSuccess
            KEY_WHEN_EMPTY -> promoBox.whenEmpty
            KEY_WHEN_CODE_ALREADY_APPLIED -> promoBox.whenCodeAlreadyApplied
            KEY_WHEN_UNAVAILABLE -> promoBox.whenUnavailable
            KEY_WHEN_CODE_EXPIRED -> promoBox.whenCodeExpired
            else -> null
        }
    }

    object BundleKeys {
        const val INPUT_KEY = "discount_code_input"
        const val FRAGMENT_TAG = "DiscountCodeBottomSheetComposeFragment"
    }
}