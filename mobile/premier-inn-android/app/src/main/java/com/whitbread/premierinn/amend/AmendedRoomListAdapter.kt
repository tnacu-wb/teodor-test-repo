package com.whitbread.premierinn.amend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.contentsquare.android.Contentsquare
import com.whitbread.premierinn.R
import com.whitbread.premierinn.account.view.ListButton
import com.whitbread.premierinn.common.utils.fullName
import com.whitbread.premierinn.common.utils.roomCriteriaSummary
import com.whitbread.premierinn.domain.common.HUB_BREAKFAST_CODE
import com.whitbread.premierinn.domain.common.Upsell

class AmendedRoomListAdapter(diffUtil: AmendedRoomDiffUtil = AmendedRoomDiffUtil(),
                             val editBooking: (AmendReservationState.AmendedRoom) -> Unit,
                             val removeBooking: (AmendReservationState.AmendedRoom, Int, TextView) -> Unit) :
        ListAdapter<AmendReservationState.AmendedRoom, AmendedRoomListAdapter.AmendedRoomViewHolder>(diffUtil) {

    lateinit var removeTextView: TextView
    var removeTextViewItemPosition: Int = -1
    var numberOfNights: Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmendedRoomViewHolder =
            AmendedRoomViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.view_amend_room_item,
                            parent,
                            false
                    )
            )

    override fun onBindViewHolder(holder: AmendedRoomViewHolder, position: Int) {
        holder.bind(getItem(position))


    }

    inner class AmendedRoomViewHolder(view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {

        private val editButton = view.findViewById<ListButton>(R.id.edit_room)
        private val roomNumberHeading = view.findViewById<TextView>(R.id.amend_room_number_heading)
        private val roomCriteriaSummary = view.findViewById<TextView>(R.id.amend_room_criteria)
        private val roomGuestFullName = view.findViewById<TextView>(R.id.amend_room_lead_guest_title)
        private val roomMealsTitle = view.findViewById<TextView>(R.id.amend_room_meals_title)
        private val roomMealsSummary = view.findViewById<TextView>(R.id.amend_room_meals)
        private val roomExtrasTitle = view.findViewById<TextView>(R.id.amend_room_extras_title)
        private val roomExtrasSummary = view.findViewById<TextView>(R.id.amend_room_extras)
        private val removeButton = view.findViewById<TextView>(R.id.amend_remove_room)

        init {
            editButton.setOnClickListener(this)
        }

        fun bind(item: AmendReservationState.AmendedRoom?) {
            item?.let {
                val context = roomNumberHeading.context
                roomNumberHeading.text = context.getString(R.string.room_number, item.position)
                // Note : We dont show the premier plus or bigger rooms here as these are alternative room
                // and they are not mapped to Premier plus / bigger room
                roomCriteriaSummary.text = item.roomCriteria.roomCriteriaSummary(context)
                roomGuestFullName.text = context.getString(R.string.lead_guest, item.roomLeadGuest.fullName())

                item.roomUpsells.filter {
                    it.category == Upsell.Category.BREAKFAST || it.code == HUB_BREAKFAST_CODE
                }.let {
                    roomMealsTitle.isVisible = it.isNotEmpty()
                    roomMealsSummary.isVisible = it.isNotEmpty()
                    if (it.isNotEmpty()) {
                        roomMealsSummary.text = item.listOfMealSummary(context, numberOfNights)
                    }
                }

                item.roomUpsells.filter {
                    it.category == Upsell.Category.OTHER
                }.let {
                    roomExtrasTitle.isVisible = it.isNotEmpty()
                    roomExtrasSummary.isVisible = it.isNotEmpty()
                    if (it.isNotEmpty()) {
                        roomExtrasSummary.text = item.listOfExtrasSummary()
                    }
                }

                removeButton.isVisible = item.isRoomRemovable && !item.isRoomRestricted

                removeButton.setOnClickListener {
                    removeTextView = removeButton
                    removeTextViewItemPosition = item.position

                    removeBooking(getItem(layoutPosition), item.position, removeButton)
                }

                editButton.visibility = when (item.isRoomRestricted && item.isGuestNamesRestricted) {
                    true -> View.GONE
                    else -> View.VISIBLE
                }
            }

            Contentsquare.mask(roomGuestFullName)
        }

        override fun onClick(v: View?) {
            editBooking(getItem(adapterPosition))
        }
    }

    fun getCurrentlySelectedRemoveTextView(): TextView = removeTextView


    fun getCurrentlySelectedRemoveTextViewPosition(): Int = removeTextViewItemPosition

    fun numberOfNights(nights: Int) {
        numberOfNights = nights
    }
}