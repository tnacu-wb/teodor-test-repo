package com.whitbread.premierinn.hoteldetails.viewholder

import android.annotation.SuppressLint
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.databinding.ViewHotelFacilitiesSectionBinding
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.event.FacilitiesClickEvent
import com.whitbread.premierinn.hoteldetails.uimodel.HotelFacilitiesUiModel

class HotelFacilitiesSectionViewHolder(private val binding: ViewHotelFacilitiesSectionBinding,
                                       private val relay: PublishRelay<Any>) :
        BaseRecyclerViewHolder<HotelFacilitiesUiModel>(binding.root) {

    @SuppressLint("CheckResult")
    override fun bind(item: HotelFacilitiesUiModel) {
        binding.hotelFacilitiesLayout.clicks().subscribe {
            relay.accept(FacilitiesClickEvent())
        }
    }
}
