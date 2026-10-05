package com.whitbread.premierinn.hoteldetails.viewholder

import androidx.core.view.isVisible
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.databinding.ViewHotelDetailsDateGuestBinding
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.event.EditDatesClickEvent
import com.whitbread.premierinn.hoteldetails.event.EditGuestClickEvent
import com.whitbread.premierinn.hoteldetails.uimodel.DateGuestUiModel

class DateGuestViewHolder(
    private val binding: ViewHotelDetailsDateGuestBinding,
    private val publishRelay: PublishRelay<Any>
) : BaseRecyclerViewHolder<DateGuestUiModel>(binding.root) {

    init {
        binding.dateContainer.setOnClickListener {
            publishRelay.accept(EditDatesClickEvent(adapterPosition) as Any)
        }
        binding.guestContainer.setOnClickListener {
            publishRelay.accept(EditGuestClickEvent(adapterPosition) as Any)
        }
    }

    override fun bind(item: DateGuestUiModel) = with(binding) {
        editDateButton.isVisible = item.editDateLabel.isNotEmpty()
        editDateButton.text = item.editDateLabel

        editGuestButton.isVisible = item.editGuestLabel.isNotEmpty()
        editGuestButton.text = item.editGuestLabel
    }
}
