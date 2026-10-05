package com.whitbread.premierinn.ciol.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.ciol.entity.upsells.BreakfastUiModel
import com.whitbread.premierinn.ciol.entity.upsells.ExtrasItemUiModel
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType
import com.whitbread.premierinn.ciol.entity.upsells.getSelectedUpsellNamesWithKids
import com.whitbread.premierinn.ciol.utils.formatPrice
import com.whitbread.premierinn.ciol.utils.getAllUpsellsNames
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.getNumberOfPreselections
import com.whitbread.premierinn.ciol.utils.getNumberOfSelections
import com.whitbread.premierinn.ciol.utils.getUpsellName
import com.whitbread.premierinn.ciol.utils.getUpsellType
import com.whitbread.premierinn.ciol.utils.isEnabled
import com.whitbread.premierinn.ciol.utils.isKidsMeal
import com.whitbread.premierinn.ciol.utils.isPreselected
import com.whitbread.premierinn.common.utils.StringUtils.LINE_BREAK
import com.whitbread.premierinn.databinding.ItemUpsellBinding
import com.whitbread.premierinn.domain.ciol.entity.UpsellItemId
import com.whitbread.premierinn.domain.ciol.entity.isMealType
import java.util.Locale

class UpsellItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private val binding = ItemUpsellBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }

    fun setState(
        upsellItem: UpsellEntry,
        isMultiRoomBooking: Boolean,
        preselectedRoomSelections: List<RoomSelection>,
        roomSelections: List<RoomSelection>,
        totalRoomsAdults: Int,
        deviceLocale: Locale,
        shouldHideAddUpsellsSection: Boolean
    ) {

        if (shouldHideAddUpsellsSection && !upsellItem.isPreselected()){
            binding.root.isVisible = false
            return
        }

        setupUpsellInfo(upsellItem, roomSelections, totalRoomsAdults, upsellItem.isPreselected(), isMultiRoomBooking)
        setupViewsBackground(upsellItem)

        upsellItem.apply {
            when(this) {
                is BreakfastUiModel -> {
                    setupUpsellDescription(this, preselectedRoomSelections, roomSelections, isMultiRoomBooking, deviceLocale)
                    context.getString(R.string.restaurant_info_breakfast_tab_title) to imageUrl
                }
                is ExtrasItemUiModel -> {
                    setupUpsellDescription(this, preselectedRoomSelections, roomSelections, isMultiRoomBooking, deviceLocale)
                    name to imageSrc
                }
                is MealUiModel -> {
                    setupUpsellDescription(this, preselectedRoomSelections, roomSelections, isMultiRoomBooking, deviceLocale)
                    name to imageSrc
                }
            }.let {
                binding.itemUpsellName.text = it.first
                binding.itemUpsellImage.load(Urls.CONTENT_BASE_URL.plus(it.second))
            }
        }
    }

    /**
     * WIFI -> Add individually
     * ECI, LCO -> Add to all rooms
     */
    private fun setupUpsellInfo(upsellItem: UpsellEntry, roomSelections: List<RoomSelection>, totalRoomsAdults: Int, isPreselected: Boolean, isMultiRoomBooking: Boolean) {
        val numberOfSelections = when(upsellItem) {
            is BreakfastUiModel -> upsellItem.meals.sumOf { it.noOfSelections }
            is ExtrasItemUiModel -> upsellItem.noOfSelections
            is MealUiModel -> upsellItem.noOfSelections
        }

        val mealNotAddedForAllGuests = (upsellItem is MealUiModel || upsellItem is BreakfastUiModel) &&
            numberOfSelections in (1 until totalRoomsAdults) &&
            roomSelections.flatMap { it.selectedUpsells }
                .filter { it.getId().isMealType() && !it.getId().isKidsMeal() && it.getNumberOfSelections() > 0 }
                .sumOf { it.getNumberOfSelections() } < totalRoomsAdults

        binding.upsellInfoTextView.isVisible =
            !upsellItem.isEnabled() || upsellItem.isPreselected() || mealNotAddedForAllGuests

        val (stringId, backgroundColorId) = when {
            isPreselected -> R.string.upsells_need_to_amend to R.color.orange_sunrise// Yellow
            numberOfSelections in (1 until totalRoomsAdults) -> R.string.upsells_not_added_for_all_guests to R.color.orange_sunrise // Yellow

            !upsellItem.isEnabled() && upsellItem.getId() == UpsellItemId.MEAL_DEAL.id && !isMultiRoomBooking -> R.string.upsells_single_room_meal_deal_disabled to R.color.light_grey_3_30// Grey
            !upsellItem.isEnabled() && upsellItem.getId() == UpsellItemId.MEAL_DEAL.id && isMultiRoomBooking -> R.string.upsells_multi_room_meal_deal_disabled to R.color.light_grey_3_30 // Grey
            !upsellItem.isEnabled() && !isMultiRoomBooking -> R.string.upsells_single_room_breakfast_disabled to R.color.light_grey_3_30 // Grey
            !upsellItem.isEnabled() && isMultiRoomBooking -> R.string.upsells_multi_room_breakfast_disabled to R.color.light_grey_3_30 // Grey
            else -> null to null
        }

        if (stringId != null && backgroundColorId != null) {
            binding.upsellInfoTextView.apply {
                text = context.getString(stringId)
                background.setTint(ContextCompat.getColor(context, backgroundColorId))
            }
        }
    }

    private fun setupViewsBackground(upsellItem: UpsellEntry) {
        if (!upsellItem.isEnabled()) {
            binding.apply {
                itemUpsellImage.setColorFilter(ContextCompat.getColor(context, R.color.light_grey_50))
                itemUpsellName.setTextColor(ContextCompat.getColor(context, R.color.dark_grey_2))
                itemUpsellDescription.setTextColor(ContextCompat.getColor(context, R.color.grey_40_transparency))
            }
        } else {
            binding.apply {
                itemUpsellImage.setColorFilter(ContextCompat.getColor(context, R.color.transparent))
                itemUpsellName.setTextColor(ContextCompat.getColor(context, R.color.grey_dark))
                itemUpsellDescription.setTextColor(ContextCompat.getColor(context, R.color.dark_grey_2))
            }
        }
    }

    private fun setupUpsellDescription(
        upsellItem: UpsellEntry,
        preselectedRoomSelections: List<RoomSelection>,
        roomSelections: List<RoomSelection>,
        isMultiRoomBooking: Boolean,
        deviceLocale: Locale
    ) {
        val upsellItemIds = mutableListOf<String>()
        when (upsellItem) {
            is BreakfastUiModel -> upsellItemIds.addAll(upsellItem.meals.map { it.id })
            is ExtrasItemUiModel -> upsellItemIds.add(upsellItem.getId())
            is MealUiModel -> upsellItemIds.add(upsellItem.getId())
        }

        val areUpsellsPreselected = preselectedRoomSelections.flatMap { it.selectedUpsells }
            .filter { upsellItemIds.contains(it.getId()) }
            .sumOf { it.getNumberOfSelections() } > 0
        val areUpsellsSelected = roomSelections.flatMap { it.selectedUpsells }
            .filter { upsellItemIds.contains(it.getId()) }
            .sumOf { it.getNumberOfSelections() } > 0

        val numberOfRoomsWithPreSelections = preselectedRoomSelections.filter { roomSelection ->
            roomSelection.selectedUpsells.any { upsellItemIds.contains(it.getId()) && it.getNumberOfPreselections() > 0 }
        }.size

        val numberOfRoomsWithSelections = roomSelections.filter { roomSelection ->
            roomSelection.selectedUpsells.any { upsellItemIds.contains(it.getId()) && it.getNumberOfSelections() > 0 }
        }.size

        /**
         * This logic applies for both selected and preselected upsells
         * (isSelected || isPreselected) && isMultiRoomBooking -> selected for multiple rooms -> Added for multiple rooms
         *                                     selected for 1 room -> Added for one room
         *
         * (isSelected || isPreselected) && !isMultiRoomBooking -> show upsells along with number of selections
         */
        if (areUpsellsPreselected || areUpsellsSelected) {
            if (isMultiRoomBooking) {
                if (numberOfRoomsWithSelections > 1 || numberOfRoomsWithPreSelections > 1) {
                    binding.itemUpsellDescription.text = context.getString(R.string.upsells_added_for_multiple_rooms)
                } else {
                    binding.itemUpsellDescription.text = context.getString(R.string.upsells_added_for_one_room)
                }
            } else {
                binding.itemUpsellDescription.text = if (areUpsellsPreselected) {
                    buildString {
                        append(preselectedRoomSelections.first().getAllUpsellsNames(context))
                        append(LINE_BREAK)
                        append(preselectedRoomSelections.first().selectedUpsells
                            .filter { upsell -> upsell.getId().isKidsMeal() }
                            .joinToString(LINE_BREAK) { it.getUpsellName(context) })
                    }
                } else {
                    roomSelections.first().selectedUpsells.getSelectedUpsellNamesWithKids(context, upsellItem)
                }
            }
        } else {
            showDescriptionForUnselectedItem(upsellItem, deviceLocale)
        }
    }

    private fun showDescriptionForUnselectedItem(upsellItem: UpsellEntry, deviceLocale: Locale) {
        val priceMeasurementUnit = when(upsellItem) {
            is MealUiModel,
            is BreakfastUiModel -> context.getString(R.string.upsells_adult_per_day_measurement)
            is ExtrasItemUiModel -> {
                if (upsellItem.getId().getUpsellType() == UpsellType.WIFI)
                    context.getString(R.string.upsells_24_hrs_measurement)
                else context.getString(R.string.upsells_room_measurement)
            }
        }
        val formattedPrice = when (upsellItem) {
            is BreakfastUiModel ->
                formatPrice(upsellItem.minPrice, upsellItem.currency, deviceLocale) + " - " +
                        formatPrice(upsellItem.maxPrice, upsellItem.currency, deviceLocale)
            is ExtrasItemUiModel -> formatPrice(upsellItem.price, upsellItem.currency, deviceLocale)
            is MealUiModel -> formatPrice(upsellItem.price, upsellItem.currency, deviceLocale)
        }

        binding.itemUpsellDescription.text = String.format(
            deviceLocale,
            "%s %s %s",
            formattedPrice,
            context.getString(R.string.upsells_price_info_per),
            priceMeasurementUnit)
    }
}
