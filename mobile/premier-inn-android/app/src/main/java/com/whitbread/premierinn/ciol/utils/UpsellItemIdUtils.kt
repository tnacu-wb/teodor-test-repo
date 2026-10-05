package com.whitbread.premierinn.ciol.utils

import android.content.Context
import android.util.Log
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType
import com.whitbread.premierinn.ciol.fragments.UPSELLS_FEATURE_TAG
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.ciol.entity.MealType
import com.whitbread.premierinn.domain.ciol.entity.UpsellItemId.*

fun String.getUpsellType() = when(this) {
    PREMIER_INN_BREAKFAST.id,
    PREMIER_INN_BREAKFAST_DE.id,
    CONTINENTAL_BREAKFAST.id,
    FREE_CHILD_BREAKFAST.id,
    BREAKFAST_CONTINENTAL_BUNDLE.id,
    BREAKFAST_LIGHTER_BUNDLE.id,
    BREAKFAST_ROLL_BUNDLE.id,
    FREE_CHILD_BREAKFAST_BUNDLE.id-> UpsellType.BREAKFAST

    MEAL_DEAL.id -> UpsellType.MEAL_DEAL

    ULTIMATE_WIFI_24_HRS.id,
    ULTIMATE_WIFI_7_DAYS.id -> UpsellType.WIFI

    EARLY_CHECK_IN.id -> UpsellType.ECI
    LATE_CHECK_OUT.id -> UpsellType.LCO

    else -> {
        Log.w(UPSELLS_FEATURE_TAG, "getUpsellType() Upsell with it $this is not existing.")
        UpsellType.BREAKFAST
    }
}

fun String.getFoodUpsellNameFromCode(context: Context, nbOfSelections: Int, upsellName: String = EMPTY_STRING) = when(this) {
    PREMIER_INN_BREAKFAST.id,
    PREMIER_INN_BREAKFAST_DE.id -> context.resources
        .getQuantityString(R.plurals.upsells_pi_breakfast_label, nbOfSelections, nbOfSelections)
    CONTINENTAL_BREAKFAST.id -> context.resources
        .getQuantityString(R.plurals.upsells_continental_breakfast_label, nbOfSelections, nbOfSelections)
    FREE_CHILD_BREAKFAST.id,
    FREE_CHILD_BREAKFAST_BUNDLE.id -> String.format("%sx %s", nbOfSelections, context.getString(R.string.kids_breakfast_label))
    MEAL_DEAL.id -> context.resources.getQuantityString(R.plurals.upsells_meal_deal_label, nbOfSelections, nbOfSelections)
    BREAKFAST_CONTINENTAL_BUNDLE.id,
    BREAKFAST_LIGHTER_BUNDLE.id,
    BREAKFAST_ROLL_BUNDLE.id-> String.format("%sx %s", nbOfSelections, upsellName)

    else -> {
        Log.w(UPSELLS_FEATURE_TAG, "getFoodUpsellNameFromCode() $this is not existing.")
        EMPTY_STRING
    }
}

fun String.getExtrasUpsellNameFromCode(context: Context) = when(this) {
    ULTIMATE_WIFI_24_HRS.id,
    ULTIMATE_WIFI_7_DAYS.id -> context.getString(R.string.review_booking_card_type_not_present_wifi_toggle_label_part_1)
    EARLY_CHECK_IN.id -> context.getString(R.string.upsells_early_check_in)
    LATE_CHECK_OUT.id -> context.getString(R.string.upsells_late_check_out)

    else -> {
        Log.w(UPSELLS_FEATURE_TAG, "getExtrasUpsellNameFromCode() $this is not existing.")
        EMPTY_STRING
    }
}

fun String.getMealType() =
    if (this == PREMIER_INN_BREAKFAST.id || this == PREMIER_INN_BREAKFAST_DE.id || this == CONTINENTAL_BREAKFAST.id
        || this == BREAKFAST_ROLL_BUNDLE.id || this == BREAKFAST_LIGHTER_BUNDLE.id || this == BREAKFAST_CONTINENTAL_BUNDLE.id)
        MealType.BREAKFAST else MealType.MEAL_DEAL

fun String.isKidsMeal() = this == FREE_CHILD_BREAKFAST.id || this == FREE_CHILD_BREAKFAST_BUNDLE.id