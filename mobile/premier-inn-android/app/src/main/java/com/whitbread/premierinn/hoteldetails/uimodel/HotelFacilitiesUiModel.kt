package com.whitbread.premierinn.hoteldetails.uimodel

import com.whitbread.premierinn.R
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.UiModelListItem

class HotelFacilitiesUiModel(private val listOrder: Int
) : UiModelListItem<HotelFacilitiesUiModel> {

    override fun bind(viewHolder: BaseRecyclerViewHolder<HotelFacilitiesUiModel>) {
        viewHolder.bind(this)
    }

    override fun getLayout() = LAYOUT_TYPE

    override fun listOrder() = listOrder

    companion object {
        const val LAYOUT_TYPE: Int = R.layout.view_hotel_facilities_section
    }
}
