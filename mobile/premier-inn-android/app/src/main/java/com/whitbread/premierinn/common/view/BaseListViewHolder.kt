package com.whitbread.premierinn.common.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Same purpose as {@link com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder}
 */
abstract class BaseListViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: T)
}