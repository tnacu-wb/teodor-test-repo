package com.whitbread.premierinn.hoteldetails.viewholder

import com.whitbread.premierinn.databinding.ViewHotelDetailsCheckInOutBinding
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.uimodel.CheckInOutUiModel

class CheckInOutViewHolder(private val binding: ViewHotelDetailsCheckInOutBinding) :
        BaseRecyclerViewHolder<CheckInOutUiModel>(binding.root) {

    override fun bind(item: CheckInOutUiModel) {
        with(binding){
            checkInTime.text = item.checkIn
            checkOutTime.text = item.checkOut
        }
    }
}
