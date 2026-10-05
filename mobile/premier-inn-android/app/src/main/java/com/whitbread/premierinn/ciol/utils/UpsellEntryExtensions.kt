package com.whitbread.premierinn.ciol.utils

import com.whitbread.premierinn.ciol.entity.upsells.BreakfastUiModel
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.domain.ciol.entity.MealType
import com.whitbread.premierinn.domain.ciol.entity.isMealType

/**
 * This method is disabling multi meal selection if the max selections for a meal or breakfast
 * was reached.
 *
 * @param totalRoomsAdults -> sum of all adults from all rooms
 */
fun MutableList<UpsellEntry>.disableMultiMealSelection(totalRoomsAdults: Int) =
    this.let { upsellEntries ->
        if (totalRoomsAdults == -1) {
            return@let upsellEntries
        }

        val shouldDisableMealDealSelection = upsellEntries.isBreakfastSelectionLimitReached(totalRoomsAdults)
        val shouldDisableBreakfastSelection = upsellEntries.isMealDealSelectionLimitReached(totalRoomsAdults)

        (upsellEntries.firstOrNull { it is MealUiModel && !it.id.isKidsMeal() } as? MealUiModel)?.let { mealDeal ->
            mealDeal.displayAsEnabled = !shouldDisableMealDealSelection
        }

        (upsellEntries.firstOrNull { it is BreakfastUiModel } as? BreakfastUiModel)?.let { breakfast ->
            breakfast.displayAsEnabled = !shouldDisableBreakfastSelection
        }

        upsellEntries
    }

fun MutableList<UpsellEntry>.isMealDealSelectionLimitReached(totalRoomsAdults: Int) =
    this.filterIsInstance<MealUiModel>().filter { upsell ->
        upsell.id.getMealType() == MealType.MEAL_DEAL && !upsell.id.isKidsMeal()
    }.let { mealDeals ->
        mealDeals.sumOf { it.noOfSelections } == totalRoomsAdults ||
            mealDeals.sumOf { it.preselectedNoOfSelections } == totalRoomsAdults
    }

fun MutableList<UpsellEntry>.isBreakfastSelectionLimitReached(totalRoomsAdults: Int) =
    (this.firstOrNull { it is BreakfastUiModel } as? BreakfastUiModel)?.meals
        ?.let { breakfasts ->
            breakfasts.sumOf { it.noOfSelections } == totalRoomsAdults ||
                breakfasts.sumOf { it.preselectedNoOfSelections } == totalRoomsAdults
        } ?: false

fun UpsellEntry.isOtherMealTypeAddedForAllGuests(roomSelection: RoomSelection, preselectedRoomSelections: RoomSelection?, totalRoomAdults: Int): Boolean =
    roomSelection.selectedUpsells.filter {
            it.getId().isMealType() && !it.getId().isKidsMeal() && it.getId().getUpsellType() != this.getId().getUpsellType()
                    && it.getNumberOfSelections() > 0
        }
        .sumOf { it.getNumberOfSelections() } == totalRoomAdults

        || preselectedRoomSelections?.selectedUpsells?.any {
            it.getId().isMealType() && !it.getId().isKidsMeal() && it.getId()
                .getUpsellType() != this.getId().getUpsellType() && it.isPreselected()
        } ?: false
