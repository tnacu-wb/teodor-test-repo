package com.whitbread.premierinn.common.summary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.summary.model.SummaryExtrasItem
import com.whitbread.premierinn.common.summary.model.SummaryRoomItem
import com.whitbread.premierinn.databinding.ViewSummaryRoomExtrasItemBinding
import com.whitbread.premierinn.summary.setViewVisibility

class SummaryRoomsExtrasAdapter(
    private val onToggleChanged: (roomIndex: Int, extraIndex: Int, isSelected: Boolean) -> Unit
) : ListAdapter<SummaryRoomItem, SummaryRoomsExtrasAdapter.RoomItemsViewHolder>(DiffCallback) {
    var currentlyOpenPosition: Int = 0

    init {
        notifyItemChanged(currentlyOpenPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomItemsViewHolder {
        return RoomItemsViewHolder(
            ViewSummaryRoomExtrasItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RoomItemsViewHolder, position: Int) {
        holder.bind(currentList.size > 1, currentList[position],
            this, position, currentList[position].extras)
    }

    inner class RoomItemsViewHolder(private val binding: ViewSummaryRoomExtrasItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            isMultiRoom: Boolean,
            roomItem: SummaryRoomItem,
            adapter: SummaryRoomsExtrasAdapter,
            position: Int,
            extrasItems: List<SummaryExtrasItem>,
        ) {

            val isExtrasVisible = isMultiRoom && position == adapter.currentlyOpenPosition

            with(binding) {

                rvSummaryExtras.layoutManager = LinearLayoutManager(itemView.context)
                rvSummaryExtras.adapter =
                    SummaryExtrasAdapter( position, onToggleChanged).apply { submitList(extrasItems) }

                if (isMultiRoom) {
                    tvRoomExtras.text = itemView.context.getString(R.string.summary_room_name, roomItem.roomNumber)
                    tvRoomExtras.visibility = View.VISIBLE

                    tvRoomExtrasType.text = roomDescription(roomItem)
                    tvRoomExtrasType.visibility = View.VISIBLE

                    viewDivider.visibility = View.VISIBLE

                    tvSelectedExtra.text = displaySelectedExtra(roomItem, btnAddExtras, btnChangeExtras, ivTickCircle)

                    tvSelectedExtra.setViewVisibility(!isExtrasVisible)
                    rvSummaryExtras.setViewVisibility(isExtrasVisible)
                    llChosenExtraInfoWrapper.setViewVisibility(!isExtrasVisible)
                    flChosenExtraButtonWrapper.setViewVisibility(!isExtrasVisible)
                    flTickCircleWrapper.setViewVisibility(!isExtrasVisible)

                    flChosenExtraButtonWrapper.setOnClickListener {
                        val previousOpenPosition = adapter.currentlyOpenPosition
                        adapter.currentlyOpenPosition =
                            if (position == adapter.currentlyOpenPosition) RecyclerView.NO_POSITION else position

                        setOf(previousOpenPosition, adapter.currentlyOpenPosition)
                            .filter { it in 0 until adapter.itemCount }
                            .forEach(adapter::notifyItemChanged)
                    }
                }
            }
        }

        private fun roomDescription(roomItem: SummaryRoomItem): String {
            val adultText = itemView.context.resources.getQuantityString(R.plurals.number_of_adults, roomItem.adults, roomItem.adults)
            val childText = itemView.context.resources.getQuantityString(R.plurals.number_of_children, roomItem.children, roomItem.children)

            return if (roomItem.children > 0) {
                itemView.context.getString(R.string.summary_room_details_text_with_adults_and_children, adultText, childText,
                    roomItem.roomType)
            } else {
                itemView.context.getString(R.string.summary_room_details_text_with_adults_only, adultText,
                    roomItem.roomType)
            }
        }

        private fun displaySelectedExtra(
            roomItem: SummaryRoomItem,
            btnAddExtras: TextView,
            btnChangeExtras: TextView,
            ivTickCircle: ImageView,
            ): String {

            val selectedExtras = roomItem.extras
                .filter { it.selected }
                .map { itemView.context.getString(R.string.summary_room_chosen_extras, it.name, it.price.formattedPrice) }
                .joinToString("\n")

            btnAddExtras.setViewVisibility(selectedExtras.isEmpty())
            btnChangeExtras.setViewVisibility(selectedExtras.isNotEmpty())
            ivTickCircle.setViewVisibility(selectedExtras.isNotEmpty())

            return selectedExtras.ifEmpty {
                itemView.context.getString(R.string.summary_room_no_extras_chosen)
            }
        }

    }

    companion object DiffCallback : DiffUtil.ItemCallback<SummaryRoomItem>() {

        override fun areItemsTheSame(oldItem: SummaryRoomItem, newItem: SummaryRoomItem): Boolean {
            return oldItem.roomNumber == newItem.roomNumber
        }

        override fun areContentsTheSame(oldItem: SummaryRoomItem, newItem: SummaryRoomItem): Boolean {
            return oldItem == newItem
        }
    }
}




