package com.whitbread.premierinn.hoteldetails.viewholder

import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.databinding.ViewHotelDetailsFacilitiesBinding
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.event.ReadMoreClickEvent
import com.whitbread.premierinn.hoteldetails.uimodel.FacilitiesUiModel

class FacilitiesViewHolder(
    private val binding: ViewHotelDetailsFacilitiesBinding,
    private val relay: PublishRelay<Any>,
) : BaseRecyclerViewHolder<FacilitiesUiModel>(binding.root) {
    override fun bind(item: FacilitiesUiModel) {
        with(binding) {
            readMoreButton.clicks()
                .subscribe {
                    relay.accept(ReadMoreClickEvent(adapterPosition))
                }
            hotelDetails.text = item.hotelDescription()
            hotelDetailsFacilities.showFacilities(item.facilities())
        }
    }
}
