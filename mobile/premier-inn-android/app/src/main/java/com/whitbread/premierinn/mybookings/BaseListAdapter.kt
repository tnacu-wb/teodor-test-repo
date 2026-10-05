package com.whitbread.premierinn.mybookings

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.whitbread.premierinn.common.view.BaseListViewHolder
import com.whitbread.premierinn.common.view.ListItem

abstract class BaseListAdapter(callback: DiffUtil.ItemCallback<ListItem>?)
    : ListAdapter<ListItem, BaseListViewHolder<ListItem>>(callback ?: BaseDiffCallback()) {

    override fun onBindViewHolder(holder: BaseListViewHolder<ListItem>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type()
    }

    fun getListItem(position: Int): ListItem {
        return getItem(position)
    }

    fun getAdapterPositionById(id: String): Int {
        for (adapterPosition in 0 until itemCount) {
            if (getItem(adapterPosition).id() == id) {
                return adapterPosition
            }
        }
        return -1
    }
}

class BaseDiffCallback : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem.id() == newItem.id()
    }

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem == newItem
    }
}