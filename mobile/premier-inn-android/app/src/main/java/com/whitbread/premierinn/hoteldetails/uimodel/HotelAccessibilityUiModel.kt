package com.whitbread.premierinn.hoteldetails.uimodel

import com.whitbread.premierinn.R
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.UiModelListItem

class HotelAccessibilityUiModel(private val listOrder: Int
) : UiModelListItem<HotelAccessibilityUiModel> {

    override fun bind(viewHolder: BaseRecyclerViewHolder<HotelAccessibilityUiModel>) {
        viewHolder.bind(this)
    }

    override fun getLayout() = LAYOUT_TYPE

    override fun listOrder() = listOrder

    companion object {
        const val LAYOUT_TYPE: Int = R.layout.view_hotel_accessibility_section
    }
}
