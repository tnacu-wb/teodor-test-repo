package com.whitbread.premierinn.ciol.uilogic

import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpsellItem
import com.whitbread.premierinn.ciol.entity.upsells.details.MealSelectionRules
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.ciol.utils.isKidsMeal
import javax.inject.Inject

/**
 * Class which builds a model used to display meal selection rules
 */
class ComputeMealSelectionRules @Inject constructor() {

    /**
     * @param currentUpsell -> Upsell for which the rules are computed
     * @param availableUpsells -> all upsells available for selection in the bottom sheet
     * @param roomSelection -> contains all the selected upsells for a specific room
     * @param roomAdults -> Number of adults in the selected room
     * @param roomChildren -> Number of children in the selected room
     *
     * @return all rules applicable to a meal entry in the Bottom sheet
     */
    operator fun invoke(
        currentUpsell: MealUiModel,
        availableUpsells: List<UpsellItem>,
        roomSelection: RoomSelection,
        roomAdults: Int,
        roomChildren: Int
    ): MealSelectionRules {
        val bottomSheetSelectedMealsNumberForAdults = getBottomSheetSelectedMealsForAdults(availableUpsells).sumOf { it.noOfSelections }
        val roomSelectionSelectedMealsNumberForAdults = getAllSelectedMealsExceptingKids(roomSelection).sumOf { it.noOfSelections }

        val isKidsMenuSelectionEnabled = bottomSheetSelectedMealsNumberForAdults > 0

        val canIncrementForAdults = roomSelectionSelectedMealsNumberForAdults < roomAdults

        val canDecrementForAdults = currentUpsell.noOfSelections > 0

        val bottomSheetSelectedMealsNumberForChildren = getBottomSheetSelectedMealsForChildren(availableUpsells).sumOf { it.noOfSelections }
        val totalSelectedMealsNumberForChildren = getTotalSelectedMealsNumberForChildren(roomSelection)

        val canIncrementForChildren = bottomSheetSelectedMealsNumberForChildren < roomChildren &&
            bottomSheetSelectedMealsNumberForAdults > 0 &&
            totalSelectedMealsNumberForChildren < roomChildren

        val canDecrementForChildren = bottomSheetSelectedMealsNumberForChildren > 0

        return MealSelectionRules(
            isMealUnavailableForSelection = false,
            isKidsMenuSelectionEnabled = isKidsMenuSelectionEnabled,
            canIncrementForAdults = canIncrementForAdults,
            canDecrementForAdults = canDecrementForAdults,
            canIncrementForChildren = canIncrementForChildren,
            canDecrementForChildren = canDecrementForChildren,
        )
    }

    private fun getBottomSheetSelectedMealsForAdults(availableUpsells: List<UpsellItem>) =
        availableUpsells
            .filterIsInstance<MealUiModel>()
            .filter { it.isSelected() && !it.id.isKidsMeal() }

    private fun getBottomSheetSelectedMealsForChildren(availableUpsells: List<UpsellItem>) =
        availableUpsells
            .filterIsInstance<MealUiModel>()
            .filter { it.isSelected() && it.id.isKidsMeal() }

    private fun getTotalSelectedMealsNumberForChildren(roomSelection: RoomSelection) =
        roomSelection.selectedUpsells
            .filterIsInstance<MealUiModel>()
            .filter { it.isSelected() && it.freeBreakfastOption == true }
            .sumOf { it.freeBreakfastSelections }

    private fun getAllSelectedMealsExceptingKids(roomSelection: RoomSelection) =
        roomSelection.selectedUpsells
            .filterIsInstance<MealUiModel>()
            .filter { it.isSelected() && !it.id.isKidsMeal() }
}
