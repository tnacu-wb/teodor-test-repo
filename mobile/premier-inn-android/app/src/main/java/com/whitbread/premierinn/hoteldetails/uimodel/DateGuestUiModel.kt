package com.whitbread.premierinn.hoteldetails.uimodel

import com.whitbread.premierinn.R
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.UiModelListItem

class DateGuestUiModel(
    val order: Int,
    val editDateLabel: String,
    val editGuestLabel: String
) : UiModelListItem<DateGuestUiModel> {

    override fun bind(viewHolder: BaseRecyclerViewHolder<DateGuestUiModel>) {
        viewHolder.bind(this)
    }

    override fun getLayout() = LAYOUT_TYPE

    override fun listOrder() = order

    companion object {
        const val LAYOUT_TYPE: Int = R.layout.view_hotel_details_date_guest
    }
}
