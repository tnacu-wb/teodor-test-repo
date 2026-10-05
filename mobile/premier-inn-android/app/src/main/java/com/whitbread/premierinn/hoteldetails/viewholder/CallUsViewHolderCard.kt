package com.whitbread.premierinn.hoteldetails.viewholder

import com.whitbread.premierinn.databinding.ViewHotelDetailsCallUsBinding
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.uimodel.CallUsUiModel

class CallUsViewHolderCard(private val binding: ViewHotelDetailsCallUsBinding) :
    BaseRecyclerViewHolder<CallUsUiModel>(binding.root) {

    override fun bind(item: CallUsUiModel) = with(binding.callUsView) {
        setTelephoneNumber(item.telNumber())
        setLabel(item.label())
        setDescription(item.telCostInfo())
    }
}
