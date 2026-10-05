package com.whitbread.premierinn.amend.amendreview.adapter

import androidx.recyclerview.widget.DiffUtil

class DiffableCallback(
        private val oldList: List<Diffable>?,
        private val newList: List<Diffable>?
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return newList?.size ?: 0
    }

    //Identifier is being used to find a match in the old and new list, so the DiffUtil can
    //build an animation to move that item.
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val type1 = oldList?.get(oldItemPosition)?.identifier
        return type1 != null && type1 == newList!![newItemPosition].identifier
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList!![oldItemPosition] == newList!![newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList!![oldItemPosition]
        return newList!![newItemPosition].diff(oldItem)
    }
}