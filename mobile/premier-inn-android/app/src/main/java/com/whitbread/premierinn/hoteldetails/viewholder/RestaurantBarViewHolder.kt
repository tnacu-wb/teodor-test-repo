package com.whitbread.premierinn.hoteldetails.viewholder

import android.app.Activity
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.whitbread.premierinn.common.view.gallery.ItemClickListener
import com.whitbread.premierinn.common.view.gallery.finite.FiniteGallery.ScrolledListener
import com.whitbread.premierinn.databinding.ViewHotelDetailsRestaurantBarBinding
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder
import com.whitbread.premierinn.hoteldetails.HotelDetailsActivity
import com.whitbread.premierinn.hoteldetails.hotelimagesfullscreen.FullScreenGalleryActivity
import com.whitbread.premierinn.hoteldetails.uimodel.RestaurantBarUiModel

class RestaurantBarViewHolder(val binding: ViewHotelDetailsRestaurantBarBinding) :
    BaseRecyclerViewHolder<RestaurantBarUiModel>(binding.root) {

    init {
        binding.hotelDetailsGalleryPager.setScrolled(object : ScrolledListener {
            override fun scrolled(position: Int, size: Int) {
                binding.counter.text = "${position + 1} / $size"
            }
        })
    }

    override fun bind(model: RestaurantBarUiModel) = with(binding) {
        hotelDetailsGalleryTitle.text =
            itemView.context.resources.getString(model.headingStringRes())

        hotelDetailsGalleryPager.isVisible = model.imageUrls().isNotEmpty()
        counter.isVisible = model.imageUrls().isNotEmpty()
        hotelDetailsGalleryPager.images(model.imageUrls())

        hotelDetailsGalleryPager.setItemClickListener(object : ItemClickListener {
            override fun onClick(position: Int) {
                ActivityCompat.startActivityForResult(
                    (itemView.context as Activity),
                    FullScreenGalleryActivity.createSimpleGalleryIntent(
                        itemView.context,
                        ArrayList(model.imageUrls()),
                        position
                    ),
                    HotelDetailsActivity.RESULT_REQUEST_CODE,
                    null
                )
            }
        })

        if (model.disclaimer() != null && model.disclaimer().isNotEmpty()) {
            disclaimerContainer.visibility = View.VISIBLE
            disclaimerText.text = model.disclaimer()
        }
    }
}