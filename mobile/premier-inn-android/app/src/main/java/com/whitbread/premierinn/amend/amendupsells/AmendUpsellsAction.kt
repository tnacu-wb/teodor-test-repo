package com.whitbread.premierinn.amend.amendupsells


sealed interface AmendUpsellsAction {
    data object BackClicked : AmendUpsellsAction
    data object ContinueClicked : AmendUpsellsAction

    data class MenuAndAllergyInfoClicked(val isAllergyInfo: Boolean) : AmendUpsellsAction

    data class MealIncrement(val roomIndex: Int, val mealIndex: Int) : AmendUpsellsAction
    data class MealDecrement(val roomIndex: Int, val mealIndex: Int) : AmendUpsellsAction

    data class ExtraToggled(val roomIndex: Int, val extraIndex: Int, val enabled: Boolean) : AmendUpsellsAction
}