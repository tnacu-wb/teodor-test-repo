package com.whitbread.premierinn.hoteldetails.viewholder

import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.whitbread.premierinn.common.view.gallery.Image
import com.whitbread.premierinn.common.view.gallery.ItemClickListener
import com.whitbread.premierinn.common.view.gallery.finite.FiniteGallery
import com.whitbread.premierinn.databinding.ViewHotelDetailsGalleryBinding
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.HotelDetailsActivity
import com.whitbread.premierinn.hoteldetails.hotelimagesfullscreen.FullScreenGalleryActivity
import com.whitbread.premierinn.hoteldetails.uimodel.HotelGalleryUiModel

class HotelGalleryViewHolder(val binding: ViewHotelDetailsGalleryBinding) :
    BaseRecyclerViewHolder<HotelGalleryUiModel>(binding.root) {

    init {
        binding.hotelDetailsTopGallery.setScrolled(object : FiniteGallery.ScrolledListener {
            override fun scrolled(position: Int, size: Int) {
                binding.counter.text = "${position + 1} / $size"
            }
        })
    }

    override fun bind(item: HotelGalleryUiModel) {
        binding.hotelDetailsTopGallery.images(item.images)
        binding.hotelDetailsTopGallery.setItemClickListener(object : ItemClickListener {
            override fun onClick(position: Int) {
                ActivityCompat.startActivityForResult(
                    (itemView.context as Activity),
                    FullScreenGalleryActivity.createBadgedGalleryIntent(
                        itemView.context,
                        ArrayList<Image>(item.images),
                        position
                    ),
                    HotelDetailsActivity.RESULT_REQUEST_CODE,
                    FullScreenGalleryActivity.createAnimationBundle(
                        (itemView.context as Activity),
                        binding.hotelDetailsTopGallery
                    )
                )
            }
        })

        binding.hotelDetailsHotelName.text = item.hotelName
        binding.hotelDetailsHubLabel.isVisible = item.hotelBrand == Hotel.Brand.HUB
        binding.hotelDetailsZipLabel.isVisible = item.hotelBrand == Hotel.Brand.ZIP
    }
}
