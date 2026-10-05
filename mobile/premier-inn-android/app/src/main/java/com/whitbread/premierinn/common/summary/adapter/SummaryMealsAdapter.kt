package com.whitbread.premierinn.common.summary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.summary.model.SummaryMealItem
import com.whitbread.premierinn.common.summary.model.SummaryRoomItem
import com.whitbread.premierinn.common.utils.HtmlUtils
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.databinding.ViewSummaryMealItemBinding


class SummaryMealsAdapter(
    private val roomItem: SummaryRoomItem,
    private val roomIndex: Int,
    private val numberOfNights: Int,
    private val onIncrement: (Int, Int) -> Unit,
    private val onDecrement: (Int, Int) -> Unit
) :
    ListAdapter<SummaryMealItem, SummaryMealsAdapter.MealItemsViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealItemsViewHolder {
        return MealItemsViewHolder(
            ViewSummaryMealItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MealItemsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)

        holder.binding.itemDivider.visibility = if (position == itemCount - 1) View.GONE else View.VISIBLE
    }

    inner class MealItemsViewHolder(val binding: ViewSummaryMealItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: SummaryMealItem,
            position: Int,
        ) {
            binding.apply {
                val isOffer = item.originalPrice != null

                tvMealsLegend.tag = item.id
                tvMealsLegend.text = item.name

                if (item.price.formattedPrice.isEmpty()) {
                    tvMealsPrice.visibility = View.GONE
                } else {
                    tvMealsPrice.visibility = View.VISIBLE
                    tvMealsPrice.text = if (isOffer)
                        HtmlUtils.parseTags(itemView.context.getString(R.string.summary_breakfast_per_adult_per_day, item.originalPrice?.formattedPrice, item.price.formattedPrice)) else
                        itemView.context.getString(R.string.summary_breakfast_per_day, item.price.formattedPrice)
                }
                if (StringUtils.isBlank(item.description)) {
                    tvMealsDescription.visibility = View.GONE
                } else {
                    tvMealsDescription.visibility = View.VISIBLE
                    tvMealsDescription.text = HtmlUtils.parseTags(item.description)
                }

                if (roomItem.children != 0 && item.kidsEatFree) {
                    tvMealsKidsEatFree.visibility = View.VISIBLE
                    tvMealsKidsEatFree.text = itemView.context.getString(R.string.summary_kids_eat_breakfast_free)
                }

                tvAdultsNumber.text = item.counter.toString()

                btnAdultsIncrement.isActivated = roomItem.meals.sumOf { it.counter } < roomItem.adults
                btnAdultsDecrement.isActivated = item.counter > 0


                if (isOffer) {
                    btnAdultsIncrement.visibility = View.INVISIBLE
                    btnAdultsDecrement.visibility = View.INVISIBLE

                    tvOfferTag.text = item.offerTag
                    tvOfferTag.visibility = View.VISIBLE

                    tvPromoPriceForNights.text = itemView.context.resources.getQuantityString(
                        R.plurals.price_for_nights_in_parenthesis,
                        numberOfNights,
                        item.price.formattedPrice,
                        numberOfNights
                    )
                    tvPromoPriceForNights.visibility = View.VISIBLE
                } else {
                    btnAdultsIncrement.visibility = View.VISIBLE
                    btnAdultsIncrement.setOnClickListener {
                        onIncrement(roomIndex, position)
                        notifyItemRangeChanged(0, roomItem.meals.size)
                    }

                    btnAdultsDecrement.visibility = View.VISIBLE
                    btnAdultsDecrement.setOnClickListener {
                        onDecrement(roomIndex, position)
                        notifyItemRangeChanged(0, roomItem.meals.size)
                    }

                    tvOfferTag.visibility = View.GONE
                    tvPromoPriceForNights.visibility = View.GONE
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<SummaryMealItem>() {

        override fun areItemsTheSame(oldItem: SummaryMealItem, newItem: SummaryMealItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: SummaryMealItem, newItem: SummaryMealItem): Boolean {
            return oldItem == newItem
        }
    }
}