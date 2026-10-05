package com.whitbread.premierinn.hoteldetails.viewholder

import android.annotation.SuppressLint
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.databinding.ViewHotelParkingSectionBinding
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.event.ParkingClickEvent
import com.whitbread.premierinn.hoteldetails.uimodel.HotelParkingUiModel

class HotelParkingSectionViewHolder(private val binding: ViewHotelParkingSectionBinding,
                                       private val relay: PublishRelay<Any>) :
        BaseRecyclerViewHolder<HotelParkingUiModel>(binding.root) {

    @SuppressLint("CheckResult")
    override fun bind(item: HotelParkingUiModel) {
        binding.hotelParkingLayout.clicks().subscribe {
            relay.accept(ParkingClickEvent())
        }
    }
}
