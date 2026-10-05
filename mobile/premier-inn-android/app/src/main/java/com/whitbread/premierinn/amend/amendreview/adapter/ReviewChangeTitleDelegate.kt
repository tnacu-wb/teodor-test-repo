package com.whitbread.premierinn.amend.amendreview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewChangesTitleItem
import com.whitbread.premierinn.databinding.ViewReviewAmendChangesHeaderBinding

class ReviewChangeTitleDelegate : AbsListItemAdapterDelegate<ReviewChangesTitleItem, Diffable, ReviewChangeTitleDelegate.ViewHolder>() {

    private lateinit var binding : ViewReviewAmendChangesHeaderBinding

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        binding = ViewReviewAmendChangesHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun isForViewType(
        item: Diffable,
        items: MutableList<Diffable>,
        position: Int
    ): Boolean {
        return items[position] is ReviewChangesTitleItem    }

    override fun onBindViewHolder(
        item: ReviewChangesTitleItem,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class ViewHolder(private val binding: ViewReviewAmendChangesHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reviewChangesTitleItem : ReviewChangesTitleItem) {
            binding.reviewAmendChangesHeader.text = reviewChangesTitleItem.title
        }
    }
}