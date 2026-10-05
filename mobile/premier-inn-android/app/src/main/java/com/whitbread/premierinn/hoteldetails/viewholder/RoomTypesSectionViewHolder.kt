package com.whitbread.premierinn.hoteldetails.viewholder

import android.annotation.SuppressLint
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.databinding.ViewRoomTypesSectionBinding
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.event.RoomTypesClickEvent
import com.whitbread.premierinn.hoteldetails.uimodel.RoomTypesSectionUiModel

class RoomTypesSectionViewHolder(private val binding: ViewRoomTypesSectionBinding,
                                 private val relay: PublishRelay<Any>
) : BaseRecyclerViewHolder<RoomTypesSectionUiModel>(binding.root) {

    @SuppressLint("CheckResult")
    override fun bind(item: RoomTypesSectionUiModel) {
        binding.roomTypesLayout.clicks().subscribe {
            relay.accept(RoomTypesClickEvent)
        }
    }
}
