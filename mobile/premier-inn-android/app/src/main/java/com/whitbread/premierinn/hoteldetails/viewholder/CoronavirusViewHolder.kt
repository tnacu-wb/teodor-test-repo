package com.whitbread.premierinn.hoteldetails.viewholder

import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.databinding.ViewCoronavirusLayoutBinding
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.event.CoronavirusDismissClickEvent
import com.whitbread.premierinn.hoteldetails.uimodel.CoronavirusUiModel

class CoronavirusViewHolder(
        private val binding: ViewCoronavirusLayoutBinding,
        private val relay: PublishRelay<Any>
) : BaseRecyclerViewHolder<CoronavirusUiModel>(binding.root) {

    init {
        binding.coronavirusDismissButton.setOnClickListener {
            relay.accept(CoronavirusDismissClickEvent(adapterPosition))
        }
    }

    override fun bind(item: CoronavirusUiModel) {
        binding.coronavirusTextDescription.text = item.message
    }
}