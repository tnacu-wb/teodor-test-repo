package com.whitbread.premierinn.ciol.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.details.BookingConfirmationUiModel
import com.whitbread.premierinn.ciol.entity.upsells.details.ReservationByIdUiModel
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType
import com.whitbread.premierinn.ciol.utils.getAllUpsellsNames
import com.whitbread.premierinn.ciol.utils.getFoodUpsellNameFromCode
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.getNumberOfSelections
import com.whitbread.premierinn.ciol.utils.getUpsellType
import com.whitbread.premierinn.ciol.utils.isKidsMeal
import com.whitbread.premierinn.ciol.utils.isOtherMealTypeAddedForAllGuests
import com.whitbread.premierinn.common.utils.StringUtils.LINE_BREAK
import com.whitbread.premierinn.databinding.ItemRoomSelectionBinding
import com.whitbread.premierinn.domain.ciol.entity.UpsellItemId

class RoomSelectionItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    private val binding = ItemRoomSelectionBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setState(
        upsellEntry: UpsellEntry,
        preselectedRoomSelection: RoomSelection?,
        roomSelection: RoomSelection,
        position: Int,
        bookingConfirmation: BookingConfirmationUiModel
    ) {
        setupView(upsellEntry, preselectedRoomSelection, roomSelection, position, bookingConfirmation)
    }

    private fun setupView(
        upsellEntry: UpsellEntry,
        preselectedRoomSelection: RoomSelection?,
        roomSelection: RoomSelection,
        position: Int,
        bookingConfirmation: BookingConfirmationUiModel
    ) {
        binding.itemRoomName.text = String.format(context.getString(R.string.room_number), position)
        val currentRoom = bookingConfirmation.reservationByIdList
            .find { roomSelection.reservationId == it.reservationId }
        val adultNames = currentRoom?.reservationGuestList
            ?.map { guest -> guest.firstName }
        binding.itemGuestName.text = adultNames?.joinToString(LINE_BREAK)

        val childrenNumber = currentRoom?.roomStay?.childrenNumber?.toInt()
        binding.itemChildren.isVisible = childrenNumber != 0
        if (binding.itemChildren.isVisible) {
            binding.itemChildren.text = context.resources.getQuantityString(R.plurals.number_of_children, childrenNumber!!, childrenNumber)
        }

        binding.itemUpsells.isVisible =
            roomSelection.selectedUpsells.sumOf { it.getNumberOfSelections() } > 0 ||
                (preselectedRoomSelection != null && preselectedRoomSelection.selectedUpsells
                    .sumOf { it.getNumberOfSelections() } > 0)
        if (binding.itemUpsells.isVisible) {
            populateItemUpsellsText(preselectedRoomSelection, roomSelection)
        }

        setupDisabledView(upsellEntry, currentRoom, roomSelection, preselectedRoomSelection)
    }

    private fun setupDisabledView(
        upsellEntry: UpsellEntry,
        currentRoom: ReservationByIdUiModel?,
        roomSelection: RoomSelection,
        preselectedRoomSelection: RoomSelection?
    ) {
        val isOtherMealAddedForAllGuests = currentRoom?.roomStay?.adultsNumber?.toInt()?.let {
            upsellEntry.isOtherMealTypeAddedForAllGuests(
                roomSelection,
                preselectedRoomSelection,
                it
            )
        }

        val isMealType = arrayOf(UpsellType.BREAKFAST, UpsellType.MEAL_DEAL).contains(upsellEntry.getId().getUpsellType())
        binding.itemRoomUpsellInfo.isVisible = isMealType && isOtherMealAddedForAllGuests == true

        when {
            upsellEntry.getId() == UpsellItemId.MEAL_DEAL.id -> R.string.upsells_multi_room_meal_deals_disabled
            upsellEntry.getId().getUpsellType() == UpsellType.BREAKFAST -> R.string.upsells_multi_room_breakfasts_disabled

            else -> {
                null
            }
        }?.let {
            binding.itemRoomUpsellInfo.text = context.getText(it)
        }

        binding.apply {
            if (isMealType && isOtherMealAddedForAllGuests == true) {
                itemRoomName.setTextColor(ContextCompat.getColor(context, R.color.dark_grey_2))
                itemGuestName.setTextColor(ContextCompat.getColor(context, R.color.grey_40_transparency))
                itemChildren.setTextColor(ContextCompat.getColor(context, R.color.grey_40_transparency))
                itemUpsells.setTextColor(ContextCompat.getColor(context, R.color.grey_40_transparency))
            } else {
                itemRoomName.setTextColor(ContextCompat.getColor(context, R.color.grey_dark))
                itemGuestName.setTextColor(ContextCompat.getColor(context, R.color.dark_grey_2))
                itemChildren.setTextColor(ContextCompat.getColor(context, R.color.dark_grey_2))
                itemUpsells.setTextColor(ContextCompat.getColor(context, R.color.dark_grey_2))
            }
        }
    }

    private fun populateItemUpsellsText(preselectedRoomSelection: RoomSelection?, roomSelection: RoomSelection) {
        val preselectedRoomSelectionFreeBfs = preselectedRoomSelection?.selectedUpsells
            ?.filter { upsell -> upsell.getId().isKidsMeal()}
        val numberOfFreePreselectedBfs = preselectedRoomSelectionFreeBfs?.firstOrNull()?.getNumberOfSelections() ?: 0

        binding.itemUpsells.text = buildString {
            if (preselectedRoomSelection != null && preselectedRoomSelection.getAllUpsellsNames(context).trim().isNotEmpty()) {
                append(preselectedRoomSelection.getAllUpsellsNames(context))
                append(LINE_BREAK)
            }
            if (numberOfFreePreselectedBfs > 0) {
                append(
                    UpsellItemId.FREE_CHILD_BREAKFAST.id.getFoodUpsellNameFromCode(context, numberOfFreePreselectedBfs)
                )
                append(LINE_BREAK)
            }
            append(roomSelection.getAllUpsellsNames(context))
        }
    }
}
