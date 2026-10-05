package com.whitbread.premierinn.amend.amendreview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewAmendBalanceItem
import com.whitbread.premierinn.databinding.ViewReviewAmendBalanceBinding

class ReviewBalanceTextDelegate : AbsListItemAdapterDelegate<ReviewAmendBalanceItem, Diffable, ReviewBalanceTextDelegate.ViewHolder>() {

    private lateinit var binding : ViewReviewAmendBalanceBinding

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        binding = ViewReviewAmendBalanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun isForViewType(
            item: Diffable,
            items: MutableList<Diffable>,
            position: Int
    ): Boolean {
        return items[position] is ReviewAmendBalanceItem
    }

    override fun onBindViewHolder(
            item: ReviewAmendBalanceItem,
            holder: ViewHolder,
            payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class ViewHolder(private val binding: ViewReviewAmendBalanceBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item : ReviewAmendBalanceItem) {
            binding.amendBalanceTitle.text = item.title
            binding.amendBalanceDescription.text = item.description
            binding.amendBalanceAmount.text = item.balance
        }
    }
}