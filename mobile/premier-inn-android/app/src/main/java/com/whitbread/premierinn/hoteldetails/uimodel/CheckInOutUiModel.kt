package com.whitbread.premierinn.hoteldetails.uimodel

import com.whitbread.premierinn.R
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.UiModelListItem

class CheckInOutUiModel(val order: Int, val checkIn: String, val checkOut: String) : UiModelListItem<CheckInOutUiModel> {

    override fun bind(viewHolder: BaseRecyclerViewHolder<CheckInOutUiModel>) = viewHolder.bind(this)

    override fun getLayout(): Int = LAYOUT_TYPE

    override fun listOrder(): Int = order

    companion object {
        const val LAYOUT_TYPE: Int = R.layout.view_hotel_details_check_in_out
    }
}
