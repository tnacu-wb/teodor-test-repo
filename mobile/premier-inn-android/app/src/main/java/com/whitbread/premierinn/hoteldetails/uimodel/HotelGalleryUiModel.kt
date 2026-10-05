package com.whitbread.premierinn.hoteldetails.uimodel

import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.view.gallery.Image
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.UiModelListItem

class HotelGalleryUiModel(
    val order: Int,
    val images: List<Image>,
    val hotelName: String,
    val hotelBrand: Hotel.Brand
) : UiModelListItem<HotelGalleryUiModel> {

    override fun bind(viewHolder: BaseRecyclerViewHolder<HotelGalleryUiModel>) {
        viewHolder.bind(this)
    }

    override fun getLayout() = LAYOUT_TYPE

    override fun listOrder() = order

    companion object {
        const val LAYOUT_TYPE = R.layout.view_hotel_details_gallery
    }
}
