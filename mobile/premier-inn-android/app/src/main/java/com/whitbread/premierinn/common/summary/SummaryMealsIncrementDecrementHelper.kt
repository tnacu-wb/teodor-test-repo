package com.whitbread.premierinn.common.summary

import com.whitbread.premierinn.common.summary.model.SummaryRoomItem
import javax.inject.Inject

class SummaryMealsIncrementDecrementHelper  @Inject constructor (){

    // Checks if the increment is allowed for a given RoomItem.
    private fun canIncrement(roomItem: SummaryRoomItem): Boolean {
        val totalCounter = roomItem.meals.sumOf { it.counter }
        return totalCounter < roomItem.adults
    }

    // Increments a MealItem counter if allowed.
    fun incrementMealItem(roomItem: SummaryRoomItem, mealIndex: Int): SummaryRoomItem {
        if (!canIncrement(roomItem)) return roomItem

        return roomItem.copy(
            meals = roomItem.meals.toMutableList().apply {
                this[mealIndex] = this[mealIndex].copy(
                    counter = this[mealIndex].counter + 1
                )
            }
        )
    }

    // Decrements a MealItem counter.
    fun decrementMealItem(roomItem: SummaryRoomItem, mealIndex: Int): SummaryRoomItem {
        val currentCounter = roomItem.meals[mealIndex].counter
        if (currentCounter <= 0) return roomItem

        return roomItem.copy(
            meals = roomItem.meals.toMutableList().apply {
                this[mealIndex] = this[mealIndex].copy(
                    counter = currentCounter - 1
                )
            }
        )
    }

}