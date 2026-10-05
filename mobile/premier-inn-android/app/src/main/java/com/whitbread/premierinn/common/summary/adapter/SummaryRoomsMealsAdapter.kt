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
import com.whitbread.premierinn.common.summary.model.SummaryMealItem
import com.whitbread.premierinn.common.summary.model.SummaryRoomItem
import com.whitbread.premierinn.databinding.ViewSummaryRoomMealsItemBinding
import com.whitbread.premierinn.summary.setViewVisibility

class SummaryRoomsMealsAdapter(
    private val numberOfNights: Int,
    private val onIncrement: (Int, Int) -> Unit,
    private val onDecrement: (Int, Int) -> Unit
) : ListAdapter<SummaryRoomItem, SummaryRoomsMealsAdapter.RoomItemsViewHolder>(DiffCallback) {
    var currentlyOpenPosition: Int = 0

    init {
        notifyItemChanged(currentlyOpenPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomItemsViewHolder {
        return RoomItemsViewHolder(
            ViewSummaryRoomMealsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RoomItemsViewHolder, position: Int) {
        holder.bind(currentList.size > 1, currentList[position],
            this, position, currentList[position].meals)
    }

    inner class RoomItemsViewHolder(private val binding: ViewSummaryRoomMealsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            isMultiRoom: Boolean,
            roomItem: SummaryRoomItem,
            adapter: SummaryRoomsMealsAdapter,
            position: Int,
            mealItems: List<SummaryMealItem>,
        ) {

            val isMealsVisible = isMultiRoom && position == adapter.currentlyOpenPosition

            with(binding) {

                rvSummaryMeals.layoutManager = LinearLayoutManager(itemView.context)
                rvSummaryMeals.adapter =
                    SummaryMealsAdapter( roomItem, position, numberOfNights, onIncrement, onDecrement).apply { submitList(mealItems) }

                if (isMultiRoom) {
                    tvRoomMeals.text = itemView.context.getString(R.string.summary_room_name, roomItem.roomNumber)
                    tvRoomMeals.visibility = View.VISIBLE

                    tvRoomMealsType.text = roomDescription(roomItem)
                    tvRoomMealsType.visibility = View.VISIBLE

                    viewDivider.visibility = View.VISIBLE

                    tvSelectedMeal.text = displaySelectedMeal(roomItem, btnAddMeals, btnChangeMeals, ivTickCircle)

                    tvSelectedMeal.setViewVisibility(!isMealsVisible)
                    rvSummaryMeals.setViewVisibility(isMealsVisible)
                    llChosenMealInfoWrapper.setViewVisibility(!isMealsVisible)
                    flChosenMealButtonWrapper.setViewVisibility(!isMealsVisible)
                    flTickCircleWrapper.setViewVisibility(!isMealsVisible)

                    flChosenMealButtonWrapper.setOnClickListener {
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

        private fun displaySelectedMeal(
            roomItem: SummaryRoomItem,
            btnAddMeals: TextView,
            btnChangeMeals: TextView,
            ivTickCircle: ImageView,
            ): String {
            var selectedMeals = roomItem.meals
                .filter { it.counter > 0 }
                .map {
                    if (it.price.amount == 0f)
                        itemView.context.getString(R.string.summary_room_free_meals, it.name)
                    else
                        itemView.context.getString(R.string.summary_room_chosen_meals, it.counter, it.name, it.price.formattedPrice)
                }
                .joinToString("\n")

            if (roomItem.children > 0 && roomItem.meals.any { it.counter != 0 && it.kidsEatFree }) {
                selectedMeals += "\n${itemView.context.getString(R.string.summary_kids_eat_breakfast_free)}"
            }

            val selectedMealsWithPositivePricePresent = roomItem.meals.any { it.counter > 0 && it.price.amount > 0 }

            btnAddMeals.setViewVisibility(selectedMeals.isEmpty())
            btnChangeMeals.setViewVisibility(selectedMeals.isNotEmpty())
            btnChangeMeals.text = if (selectedMealsWithPositivePricePresent)
                itemView.context.getString(R.string.summary_change_selection)
            else
                itemView.context.getString(R.string.summary_view_meals)
            ivTickCircle.setViewVisibility(selectedMeals.isNotEmpty() && selectedMealsWithPositivePricePresent)

            return selectedMeals.ifEmpty {
                itemView.context.getString(R.string.summary_room_no_meals_chosen)
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




