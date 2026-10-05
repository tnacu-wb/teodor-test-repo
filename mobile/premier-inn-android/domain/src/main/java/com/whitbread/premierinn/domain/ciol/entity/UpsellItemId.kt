package com.whitbread.premierinn.domain.ciol.entity

import com.whitbread.premierinn.domain.ciol.entity.UpsellItemId.*

enum class UpsellItemId(val id: String) {
    PREMIER_INN_BREAKFAST("BFADBF"),
    PREMIER_INN_BREAKFAST_DE("BBIB"),
    CONTINENTAL_BREAKFAST("BFADCT"),
    BREAKFAST_ROLL_BUNDLE("BFGROL"),
    BREAKFAST_LIGHTER_BUNDLE("BFGLGH"),
    BREAKFAST_CONTINENTAL_BUNDLE("BFGCON"),
    FREE_CHILD_BREAKFAST("BFCHDF"),
    FREE_CHILD_BREAKFAST_BUNDLE("BFGCHD"),
    MEAL_DEAL("MDP"),
    ULTIMATE_WIFI_24_HRS("FI24HR"),
    ULTIMATE_WIFI_7_DAYS("FI7DAY"),
    EARLY_CHECK_IN("HSCKIN"),
    LATE_CHECK_OUT("HSCOU2")
}

fun String.isMealType() = this == PREMIER_INN_BREAKFAST.id || this == PREMIER_INN_BREAKFAST_DE.id
        || this == CONTINENTAL_BREAKFAST.id || this == FREE_CHILD_BREAKFAST.id
        || this == MEAL_DEAL.id || this == BREAKFAST_ROLL_BUNDLE.id
        || this == BREAKFAST_LIGHTER_BUNDLE.id || this == BREAKFAST_CONTINENTAL_BUNDLE.id || this == FREE_CHILD_BREAKFAST_BUNDLE.id

fun String.isMealWithChildBreakfast() = this == PREMIER_INN_BREAKFAST.id || this == PREMIER_INN_BREAKFAST_DE.id
        || this == MEAL_DEAL.id  || this == BREAKFAST_ROLL_BUNDLE.id

enum class MealType {
    BREAKFAST,
    MEAL_DEAL
}
