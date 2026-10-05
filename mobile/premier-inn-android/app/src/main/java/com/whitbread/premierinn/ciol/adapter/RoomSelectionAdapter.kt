package com.whitbread.premierinn.ciol.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.UpsellItem
import com.whitbread.premierinn.ciol.entity.upsells.details.BookingConfirmationUiModel
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.getUpsellType
import com.whitbread.premierinn.ciol.utils.isOtherMealTypeAddedForAllGuests

class RoomSelectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var preselectedRoomSelections: List<RoomSelection> = mutableListOf()
    var roomSelections: List<RoomSelection> = mutableListOf()
    var bookingConfirmation: BookingConfirmationUiModel? = null
    var upsellEntry: UpsellEntry? = null
    var upsellItems: List<UpsellItem>? = emptyList()
    var onRoomClicked: ((upsellEntry: UpsellEntry, roomSelection: RoomSelection) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RoomSelectionViewHolder(RoomSelectionItemView(parent.context))
    }

    override fun getItemCount() = roomSelections.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RoomSelectionViewHolder) {
            bookingConfirmation?.let { nonNullBookingConfirmation ->
                upsellEntry?.let { upsellEntry ->
                    holder.bindItem(
                        upsellEntry,
                        preselectedRoomSelections.getOrNull(position),
                        roomSelections[position],
                        nonNullBookingConfirmation
                    )
                }
            }
        }
    }

    inner class RoomSelectionViewHolder(private val roomSelectionItemView: RoomSelectionItemView)
        : RecyclerView.ViewHolder(roomSelectionItemView) {
        fun bindItem(
            upsellItem: UpsellEntry,
            preselectedRoomSelections: RoomSelection?,
            roomSelection: RoomSelection,
            bookingConfirmation: BookingConfirmationUiModel
        ) {
            roomSelectionItemView.apply {
                setState(
                    upsellItem,
                    preselectedRoomSelections,
                    roomSelection,
                    roomSelections.indexOf(roomSelection) + 1,
                    bookingConfirmation
                )
                setOnClickListener {
                    val currentRoom = bookingConfirmation.reservationByIdList
                        .find { roomSelection.reservationId == it.reservationId }

                    val isOtherMealAddedForAllGuests =
                        currentRoom?.roomStay?.adultsNumber?.toInt()
                            ?.let { adultsNumber -> upsellItem.isOtherMealTypeAddedForAllGuests(roomSelection, preselectedRoomSelections, adultsNumber) }
                    if (!arrayOf(UpsellType.BREAKFAST, UpsellType.MEAL_DEAL).contains(upsellEntry?.getId()?.getUpsellType())
                        || isOtherMealAddedForAllGuests == false) {
                        onRoomClicked?.invoke(upsellItem, roomSelection)
                    } else {
                        null
                    }
                }
            }
        }
    }
}
