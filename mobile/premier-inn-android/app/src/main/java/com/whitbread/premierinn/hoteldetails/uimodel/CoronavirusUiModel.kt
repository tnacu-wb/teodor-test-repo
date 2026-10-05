package com.whitbread.premierinn.hoteldetails.uimodel

import com.whitbread.premierinn.R
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.UiModelListItem

class CoronavirusUiModel(
        private val listOrder: Int,
        val message: String
) : UiModelListItem<CoronavirusUiModel> {

    companion object {
        const val LAYOUT_TYPE = R.layout.view_coronavirus_layout
    }

    override fun bind(viewHolder: BaseRecyclerViewHolder<CoronavirusUiModel>) {
        viewHolder.bind(this)
    }

    override fun getLayout(): Int {
        return LAYOUT_TYPE
    }

    override fun listOrder(): Int {
        return listOrder
    }
}