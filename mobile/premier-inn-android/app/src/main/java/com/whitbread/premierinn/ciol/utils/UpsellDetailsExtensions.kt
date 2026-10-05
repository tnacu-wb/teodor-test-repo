package com.whitbread.premierinn.ciol.utils

import android.content.Context
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.upsells.BreakfastUiModel
import com.whitbread.premierinn.ciol.entity.upsells.ExtrasItemUiModel
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellDetailsModel
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType
import com.whitbread.premierinn.ciol.entity.upsells.details.getNumberOfNights
import com.whitbread.premierinn.ciol.entity.upsells.toBreakfastUiModel
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.common.GBP
import java.util.Locale

fun UpsellDetailsModel.getImageUri() = this.availableUpsells.firstOrNull()?.let {
    when (it) {
        is ExtrasItemUiModel -> it.imageSrc
        is MealUiModel -> it.imageSrc
        is BreakfastUiModel -> it.imageUrl
    }
}

fun UpsellDetailsModel.getTitle(context: Context) = when(this.upsellType) {
    UpsellType.BREAKFAST -> this.roomName?.let {
        it + " " + context.getString(R.string.upsells_breakfast_options).lowercase()
    } ?: context.getString(R.string.upsells_breakfast_options)

    UpsellType.MEAL_DEAL -> this.roomName?.let {
        it + " " + context.getString(R.string.upsells_details_meal_deal)
    } ?: context.getString(R.string.upsells_details_meal_deal)

    UpsellType.ECI,
    UpsellType.LCO,
    UpsellType.WIFI -> this.roomName?.let {
        it + " " + (this.availableUpsells.firstOrNull() as? ExtrasItemUiModel)?.name
    } ?: (this.availableUpsells.firstOrNull() as? ExtrasItemUiModel)?.name
}

fun UpsellDetailsModel.getMultiRoomDescription(context: Context, locale: Locale) = when(upsellType) {
    UpsellType.BREAKFAST,
    UpsellType.MEAL_DEAL,
    UpsellType.WIFI -> {
        val children = this.roomStay.first().childrenNumber.toInt()
        val adults = this.roomStay.first().adultsNumber.toInt()

        when {
            children == 0 && adults == 1 -> String.format("%s %s", context.getString(R.string.upsells_details_for), adultsNames[0])
            children == 0 && adults == 2 -> String.format("%s %s %s %s", context.getString(R.string.upsells_details_for), adultsNames[0], context.getString(R.string.upsells_details_and), adultsNames[1])
            children == 1 && adults == 1 -> String.format("%s %s %s %s", context.getString(R.string.upsells_details_for), adultsNames[0], context.getString(R.string.upsells_details_and), context.getString(R.string.upsells_details_one_child))
            children == 1 && adults == 2 -> String.format("%s %s, %s %s %s", context.getString(R.string.upsells_details_for), adultsNames[0], adultsNames[1], context.getString(R.string.upsells_details_and), context.getString(R.string.upsells_details_one_child))
            children == 2 && adults == 1 -> String.format("%s %s %s %s", context.getString(R.string.upsells_details_for), adultsNames[0], context.getString(R.string.upsells_details_and), context.getString(R.string.upsells_details_two_children))
            children == 2 && adults == 2 -> String.format("%s %s, %s %s %s", context.getString(R.string.upsells_details_for), adultsNames[0], adultsNames[1], context.getString(R.string.upsells_details_and), context.getString(R.string.upsells_details_two_children))
            // Case not possible
            else -> EMPTY_STRING
        }
    }
    UpsellType.ECI,
    UpsellType.LCO -> {
        String.format(
            locale,
            "%s %s %s",
            getDescriptionFormattedPrice(locale),
            context.getString(R.string.upsells_price_info_per),
            context.getString(R.string.upsells_room_measurement)
        )
    }
}

fun UpsellDetailsModel.getSingleRoomDescription(context: Context, locale: Locale) =
    when(this.upsellType) {
        UpsellType.BREAKFAST,
        UpsellType.MEAL_DEAL -> String.format(
            locale,
            "%s %s %s",
            getDescriptionFormattedPrice(locale),
            context.getString(R.string.upsells_price_info_per),
            getDescriptionMeasurementUnit(context)
        )

        UpsellType.ECI,
        UpsellType.LCO -> String.format(
            locale,
            "%s",
            getDescriptionFormattedPrice(locale)
        )

        UpsellType.WIFI -> String.format(
            locale,
            "%s %s %s",
            getDescriptionFormattedPrice(locale),
            context.getString(R.string.upsells_price_info_per),
            getDescriptionMeasurementUnit(context)
        )
    }


fun UpsellDetailsModel.getDescriptionFormattedPrice(locale: Locale) = when (upsellType) {
    UpsellType.BREAKFAST -> {
        val items = availableUpsells.toMutableList()
        items.removeIf { it is MealUiModel && it.id.isKidsMeal() }
        (items as? MutableList<MealUiModel>?)?.toBreakfastUiModel()?.let { breakfastUiModel ->
            formatPrice(breakfastUiModel.minPrice, breakfastUiModel.currency, locale) + " - " +
                formatPrice(breakfastUiModel.maxPrice, breakfastUiModel.currency, locale)
        } ?: EMPTY_STRING

    }
    UpsellType.MEAL_DEAL -> {
        val upsell = availableUpsells.firstOrNull()
        (upsell as? MealUiModel?)?.let { formatPrice(it.price, it.currency, locale) } ?: EMPTY_STRING
    }
    UpsellType.ECI,
    UpsellType.LCO,
    UpsellType.WIFI-> {
        val upsell = availableUpsells.firstOrNull()
        (upsell as? ExtrasItemUiModel?)?.let { formatPrice(it.price, it.currency, locale) } ?: EMPTY_STRING
    }
}

fun UpsellDetailsModel.getDescriptionMeasurementUnit(context: Context) = when(upsellType) {
    UpsellType.BREAKFAST,
    UpsellType.MEAL_DEAL -> context.getString(R.string.upsells_adult_per_day_measurement)
    UpsellType.WIFI -> context.getString(R.string.upsells_24_hrs_measurement)
    UpsellType.ECI,
    UpsellType.LCO -> EMPTY_STRING
}

/**
 * This returns the selected meals prices from a bottom sheet, not from all possible upsell meals
 */
fun UpsellDetailsModel.getSelectedMealsUpsellsPrice() = roomSelections
    .flatMap { it.selectedUpsells }
    .filter { it.getId().getUpsellType() == upsellType }
    .sumOf { it.getUpsellPrice() * it.getNumberOfSelections() }

fun UpsellDetailsModel.getButtonText(context: Context, locale: Locale): String =
    when (upsellType) {
        UpsellType.BREAKFAST,
        UpsellType.MEAL_DEAL -> {
            if (roomSelections.flatMap { it.selectedUpsells }
                    .filterIsInstance<MealUiModel>()
                    .filter { it.id.getUpsellType() == upsellType }
                    .sumOf { it.getNumberOfSelections() } != 0) {
                getMealButtonText(context, locale)
            } else {
                context.getString(R.string.pre_stay_add)
            }
        }

        UpsellType.ECI,
        UpsellType.LCO -> {
            if (roomStay.size > 1) {
                context.getString(R.string.upsells_add_to_all_rooms)
            } else {
                context.getString(R.string.pre_stay_add)
            }
        }

        UpsellType.WIFI -> context.getString(R.string.pre_stay_add)
    }

fun UpsellDetailsModel.getMealButtonText(context: Context, locale: Locale): String {
    val numberOfNights = this.roomStay.first().getNumberOfNights()
    val formattedPrice = formatPrice(
        getSelectedMealsUpsellsPrice() * numberOfNights,
        roomSelections.flatMap { it.selectedUpsells }.firstOrNull()?.getCurrency() ?: GBP,
        locale
    )

    return String.format(
        locale,
        "%s (%s %s %s)",
        context.getString(R.string.pre_stay_add),
        formattedPrice,
        context.getString(R.string.upsells_details_for),
        context.resources.getQuantityString(
            R.plurals.number_of_nights,
            numberOfNights,
            numberOfNights
        )
    )
}

// ================================= UpsellEntry Extensions =================================
fun UpsellEntry.getUpsellPrice(): Double = when(this) {
    is ExtrasItemUiModel -> price ?: 0.0
    is MealUiModel -> price ?: 0.0
    // Breakfast only added for when block to be exhaustive. Upsell details only contains MealUiModel and ExtrasItemUiModel
    is BreakfastUiModel -> 0.0
}

fun UpsellEntry.getNumberOfSelections(): Int = when(this) {
    is BreakfastUiModel -> 0
    is ExtrasItemUiModel -> noOfSelections
    is MealUiModel -> noOfSelections
}

fun UpsellEntry.getNumberOfPreselections(): Int = when(this) {
    is BreakfastUiModel -> this.meals.sumOf { it.preselectedNoOfSelections }
    is ExtrasItemUiModel -> preselectedNoOfSelections
    is MealUiModel -> preselectedNoOfSelections
}

fun UpsellEntry.getUpsellName(context: Context) = when (this) {
    is BreakfastUiModel -> EMPTY_STRING
    is MealUiModel -> this.getId().getFoodUpsellNameFromCode(context,  noOfSelections, this.name)
    is ExtrasItemUiModel -> this.getId().getExtrasUpsellNameFromCode(context)
}

fun UpsellEntry.getFreeBreakfastSelections(): Int = when(this) {
    is BreakfastUiModel -> this.meals.firstOrNull { it.freeBreakfastOption == true }?.freeBreakfastSelections ?: 0
    is MealUiModel -> this.freeBreakfastSelections
    is ExtrasItemUiModel -> 0
}

fun UpsellEntry.updateNumberOfSelections(numberOfSelections: Int) = when(this) {
    is BreakfastUiModel -> { /* no-op */ }
    is ExtrasItemUiModel -> this.noOfSelections = numberOfSelections
    is MealUiModel -> this.noOfSelections = numberOfSelections
}

fun UpsellEntry.updateNumberOfFreeBreakfastSelections(numberOfSelections: Int) = when(this) {
    is BreakfastUiModel -> { /* no-op */ }
    is ExtrasItemUiModel -> { /* no-op */ }
    is MealUiModel -> this.freeBreakfastSelections = numberOfSelections
}

fun UpsellEntry.isEnabled() = when(this) {
    is BreakfastUiModel -> displayAsEnabled
    is ExtrasItemUiModel -> true
    is MealUiModel -> displayAsEnabled
}

fun UpsellEntry.isPreselected() = when(this) {
    is BreakfastUiModel -> isPreSelected()
    is ExtrasItemUiModel -> isPreSelected()
    is MealUiModel -> isPreSelected()
}

/**
 * Returns the id of an upsell. Only Meal and ExtrasItem have ids, Breakfast is composed of Meal models
 */
fun UpsellEntry.getId() = when(this) {
    is ExtrasItemUiModel -> this.id
    is MealUiModel -> this.id
    is BreakfastUiModel -> EMPTY_STRING
}

fun UpsellEntry.getCurrency(): String = when(this) {
    is BreakfastUiModel -> currency
    is ExtrasItemUiModel -> currency
    is MealUiModel -> currency
}

fun UpsellEntry.getUpsellName(): String = when(this) {
    is BreakfastUiModel -> EMPTY_STRING
    is ExtrasItemUiModel -> name
    is MealUiModel -> name
}

/**
 * This method takes all selected upsells from all rooms in order to update upsells number of selections
 */
fun List<UpsellEntry>.addUpsellsSelectionsFromAllRooms(roomSelections: List<RoomSelection>) {
    val allRoomsUpsells = roomSelections.flatMap { it.selectedUpsells }

    this.forEach { upsellToUpdate ->
        upsellToUpdate.updateNumberOfSelections(
            allRoomsUpsells
                .filter { it.getId() == upsellToUpdate.getId() }
                .sumOf { it.getNumberOfSelections() }
        )

        if (upsellToUpdate is MealUiModel) {
            upsellToUpdate.freeBreakfastSelections = allRoomsUpsells
                .filter { it.getId() == upsellToUpdate.getId() }
                .sumOf { it.getFreeBreakfastSelections() }
        }
    }
}

fun List<UpsellEntry>.updateUpsells(roomSelection: RoomSelection) {
    // Reset the selections first and update with roomSelection numbers
    this.forEach {
        it.updateNumberOfSelections(0)
        it.updateNumberOfFreeBreakfastSelections(0)
    }

    roomSelection.selectedUpsells.forEach { roomSelectionUpsell ->
        val upsellToUpdate = this
        // Remove any BF for kids, as we'll update the selection from the parent
            .filterNot { it.getId().isKidsMeal()}
            .firstOrNull { it.getId() == roomSelectionUpsell.getId() }

        // Update a Meal deal or Breakfast number of selections
        upsellToUpdate?.updateNumberOfSelections(roomSelectionUpsell.getNumberOfSelections())

        // Update free Bf number of selections
        if (upsellToUpdate is MealUiModel) {
            if (roomSelectionUpsell.getFreeBreakfastSelections() > 0) {
                this.firstOrNull { it.getId().isKidsMeal() }
                    ?.updateNumberOfSelections(
                        roomSelectionUpsell.getFreeBreakfastSelections()
                    )

                upsellToUpdate.updateNumberOfFreeBreakfastSelections(
                    roomSelectionUpsell.getFreeBreakfastSelections()
                )
            }
        }
    }
}

fun List<RoomSelection>.updateRoomSelections(roomSelection: RoomSelection) {
    val roomSelectionToUpdate = this.first { it.reservationId == roomSelection.reservationId }
    roomSelection.selectedUpsells.forEach { roomSelectionUpsell ->
        val upsellToUpdate = roomSelectionToUpdate.selectedUpsells.firstOrNull { it.getId() == roomSelectionUpsell.getId() }

        if (upsellToUpdate != null) {
            upsellToUpdate.updateNumberOfSelections(roomSelectionUpsell.getNumberOfSelections())

            if (upsellToUpdate is MealUiModel) {
                upsellToUpdate.freeBreakfastSelections = roomSelectionUpsell.getFreeBreakfastSelections()
            }
        } else {
            roomSelectionToUpdate.selectedUpsells.add(roomSelectionUpsell)
        }
    }
}
