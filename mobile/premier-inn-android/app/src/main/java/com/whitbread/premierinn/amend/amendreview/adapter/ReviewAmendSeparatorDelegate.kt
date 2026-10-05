package com.whitbread.premierinn.amend.amendreview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewSeparatorItem

class ReviewAmendSeparatorDelegate :
    AbsListItemAdapterDelegate<ReviewSeparatorItem, Diffable, ReviewAmendSeparatorDelegate.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_review_separator,
                parent,
                false
            )
        )
    }

    override fun isForViewType(
        item: Diffable,
        items: MutableList<Diffable>,
        position: Int
    ): Boolean {
        return items[position] is ReviewSeparatorItem
    }

    override fun onBindViewHolder(
        item: ReviewSeparatorItem,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        //noop
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}