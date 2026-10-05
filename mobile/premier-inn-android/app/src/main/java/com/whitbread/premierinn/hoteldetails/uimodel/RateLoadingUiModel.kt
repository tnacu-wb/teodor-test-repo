package com.whitbread.premierinn.hoteldetails.uimodel

import com.whitbread.premierinn.R
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.UiModelListItem

class RateLoadingUiModel(val order: Int): UiModelListItem<RateLoadingUiModel> {

    companion object {
        const val LAYOUT_TYPE = R.layout.view_hotel_details_rate_loading

        @JvmStatic
        fun createWithOrder(order: Int): RateLoadingUiModel {
            return RateLoadingUiModel(order)
        }
    }

    override fun bind(viewHolder: BaseRecyclerViewHolder<RateLoadingUiModel>) {
        viewHolder.bind(this)
    }

    override fun getLayout(): Int {
        return LAYOUT_TYPE
    }

    override fun listOrder(): Int {
        return order
    }
}