package com.whitbread.premierinn.common.summary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.summary.model.SummaryExtrasItem
import com.whitbread.premierinn.common.utils.HtmlUtils
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.databinding.ViewSummaryExtrasItemBinding


class SummaryExtrasAdapter(
    private val roomIndex: Int,
    private val onToggleChanged: (roomIndex: Int, extraIndex: Int, isSelected: Boolean) -> Unit
) : ListAdapter<SummaryExtrasItem, SummaryExtrasAdapter.ExtrasItemsViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtrasItemsViewHolder {
        return ExtrasItemsViewHolder(
            ViewSummaryExtrasItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ExtrasItemsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)

        holder.binding.itemDivider.visibility = if (position == itemCount - 1) View.GONE else View.VISIBLE

    }

    inner class ExtrasItemsViewHolder(val binding: ViewSummaryExtrasItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: SummaryExtrasItem,
            position: Int,
        ) {
            binding.apply {
                tvExtrasLegend.tag = item.id
                tvExtrasLegend.text = item.name

                if (item.price.formattedPrice.isEmpty()) {
                    tvExtrasPrice.visibility = View.GONE
                } else {
                    tvExtrasPrice.visibility = View.VISIBLE
                    tvExtrasPrice.text = itemView.context.getString(R.string.summary_extras_price, item.price.formattedPrice)
                }
                if (StringUtils.isBlank(item.description)) {
                    tvExtrasDescription.visibility = View.GONE
                } else {
                    tvExtrasDescription.visibility = View.VISIBLE
                    tvExtrasDescription.text = HtmlUtils.parseTags(item.description)
                }

                scExtrasToggleButton.isChecked = item.selected

                scExtrasToggleButton.setOnCheckedChangeListener(null)

                scExtrasToggleButton.setOnCheckedChangeListener { _, isChecked ->
                    onToggleChanged(roomIndex, position, isChecked)
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<SummaryExtrasItem>() {

        override fun areItemsTheSame(oldItem: SummaryExtrasItem, newItem: SummaryExtrasItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: SummaryExtrasItem, newItem: SummaryExtrasItem): Boolean {
            return oldItem == newItem
        }
    }
}