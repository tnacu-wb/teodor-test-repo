package com.whitbread.premierinn.amend.amendreview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewAmendTitleItem
import com.whitbread.premierinn.databinding.ViewReviewAmendTitleBinding

class ReviewAmendTitleDelegate : AbsListItemAdapterDelegate<ReviewAmendTitleItem, Diffable, ReviewAmendTitleDelegate.ViewHolder>() {

    private lateinit var binding : ViewReviewAmendTitleBinding

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        binding = ViewReviewAmendTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun isForViewType(
            item: Diffable,
            items: MutableList<Diffable>,
            position: Int
    ): Boolean {
        return items[position] is ReviewAmendTitleItem
    }

    override fun onBindViewHolder(
            item: ReviewAmendTitleItem,
            holder: ViewHolder,
            payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class ViewHolder(private val binding: ViewReviewAmendTitleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reviewAmendTitleItem : ReviewAmendTitleItem) {
            binding.reviewAmendTitle.text = reviewAmendTitleItem.title
            binding.reviewAmendPreviousAmount.text = reviewAmendTitleItem.total
        }
    }
}