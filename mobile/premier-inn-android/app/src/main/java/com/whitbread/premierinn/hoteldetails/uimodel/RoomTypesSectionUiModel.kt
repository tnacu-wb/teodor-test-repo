package com.whitbread.premierinn.hoteldetails.uimodel

import com.whitbread.premierinn.R
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.UiModelListItem

class RoomTypesSectionUiModel(private val listOrder: Int
) : UiModelListItem<RoomTypesSectionUiModel> {

    override fun bind(viewHolder: BaseRecyclerViewHolder<RoomTypesSectionUiModel>) {
        viewHolder.bind(this)
    }

    override fun getLayout() = LAYOUT_TYPE

    override fun listOrder() = listOrder

    companion object {
        const val LAYOUT_TYPE: Int = R.layout.view_room_types_section
    }
}
