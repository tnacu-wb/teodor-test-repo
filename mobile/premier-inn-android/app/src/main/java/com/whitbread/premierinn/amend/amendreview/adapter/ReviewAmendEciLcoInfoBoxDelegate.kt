package com.whitbread.premierinn.amend.amendreview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewAmendEciLcoInfoBoxItem
import com.whitbread.premierinn.databinding.ViewReviewAmendEciLcoInfoBinding

class ReviewAmendEciLcoInfoBoxDelegate(private val isEciLcoBooking: Boolean) :
    AbsListItemAdapterDelegate<ReviewAmendEciLcoInfoBoxItem, Diffable, ReviewAmendEciLcoInfoBoxDelegate.ViewHolder>() {

    private lateinit var binding : ViewReviewAmendEciLcoInfoBinding

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        binding = ViewReviewAmendEciLcoInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, isEciLcoBooking)
    }

    override fun isForViewType(
        item: Diffable,
        items: MutableList<Diffable>,
        position: Int
    ): Boolean {
        return items[position] is ReviewAmendEciLcoInfoBoxItem
    }

    override fun onBindViewHolder(
        item: ReviewAmendEciLcoInfoBoxItem,
        holder: ViewHolder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class ViewHolder(private val binding: ViewReviewAmendEciLcoInfoBinding, private val isEciLcoBooking: Boolean) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReviewAmendEciLcoInfoBoxItem) {
            if (isEciLcoBooking) {
                binding.reviewAmendEciLcoInfobox.setText(item.message!!)
                binding.reviewAmendEciLcoInfobox.visibility = View.VISIBLE
            }
        }
    }
}