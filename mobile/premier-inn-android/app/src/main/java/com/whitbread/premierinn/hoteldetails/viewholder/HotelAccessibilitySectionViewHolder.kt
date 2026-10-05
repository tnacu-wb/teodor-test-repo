package com.whitbread.premierinn.hoteldetails.viewholder

import android.annotation.SuppressLint
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.databinding.ViewHotelAccessibilitySectionBinding
import com.whitbread.premierinn.databinding.ViewHotelParkingSectionBinding
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.event.AccesibilityClickEvent
import com.whitbread.premierinn.hoteldetails.event.ParkingClickEvent
import com.whitbread.premierinn.hoteldetails.uimodel.HotelAccessibilityUiModel
import com.whitbread.premierinn.hoteldetails.uimodel.HotelParkingUiModel

class HotelAccessibilitySectionViewHolder(private val binding: ViewHotelAccessibilitySectionBinding,
                                          private val relay: PublishRelay<Any>) :
        BaseRecyclerViewHolder<HotelAccessibilityUiModel>(binding.root) {

    @SuppressLint("CheckResult")
    override fun bind(item: HotelAccessibilityUiModel) {
        binding.hotelAccessibilityLayout.clicks().subscribe {
            relay.accept(AccesibilityClickEvent())
        }
    }
}