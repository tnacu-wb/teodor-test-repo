package com.whitbread.premierinn.amend.amendreview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewAmendTextItem
import com.whitbread.premierinn.databinding.ViewAmendReviewRoomChangedBinding

class ReviewAmendTextDelegate :  AbsListItemAdapterDelegate<ReviewAmendTextItem, Diffable, ReviewAmendTextDelegate.ViewHolder>() {

    private lateinit var binding : ViewAmendReviewRoomChangedBinding

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        binding = ViewAmendReviewRoomChangedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun isForViewType(
        item: Diffable,
        items: MutableList<Diffable>,
        position: Int
    ): Boolean {
        return items[position] is ReviewAmendTextItem
    }

    override fun onBindViewHolder(
        item: ReviewAmendTextItem,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class ViewHolder(private val binding: ViewAmendReviewRoomChangedBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reviewAmendTextItem: ReviewAmendTextItem) {
            reviewAmendTextItem.let {
                if (!it.title.isNullOrEmpty()) {
                    binding.reviewAmendTitle.visibility = View.VISIBLE
                    binding.reviewAmendTitle.text = it.title
                }

                if (!it.description.isNullOrEmpty()) {
                    binding.reviewAmendRoomDescription.visibility = View.VISIBLE
                    binding.reviewAmendRoomDescription.text = it.description
                }

                if (!it.priceChange.isNullOrEmpty()) {
                    binding.reviewAmendPriceChange.visibility = View.VISIBLE
                    binding.reviewAmendPriceChange.text = it.priceChange
                }
            }
        }
    }
}