package com.whitbread.premierinn.common.view.gallery.finite.implementations

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.view.gallery.BadgedImageView
import com.whitbread.premierinn.common.view.gallery.Image
import com.whitbread.premierinn.common.view.gallery.ItemClickListener
import com.whitbread.premierinn.common.view.gallery.finite.FiniteGallery
import com.whitbread.premierinn.common.view.gallery.finite.PhotoGalleryAdapter

/**
 * This is a horizontal gallery of images that might have a badge on top
 * The input values are urls of images with information about the badge wrapped in [Image] object
 * There is beginning and the end
 */
class BadgedFiniteGallery @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FiniteGallery<Image, BadgedFiniteGallery.ViewHolder>(context, attrs, defStyleAttr) {

    private var clickListener: ItemClickListener? = null

    override fun createAdapter(): PhotoGalleryAdapter<Image, ViewHolder> {
        return object : PhotoGalleryAdapter<Image, ViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = BadgedImageView(parent.context)
                return ViewHolder(view)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.bindView(getItem(position))
            }
        }
    }

    fun setItemClickListener(clickListener: ItemClickListener) {
        this.clickListener = clickListener
    }

    inner class ViewHolder(private val view: BadgedImageView) : RecyclerView.ViewHolder(view) {

        fun bindView(badgedImage: Image) {
            view.apply {
                loadImage(badgedImage.imageUrl, R.drawable.image_no_hotel)
                badgedImage.badge?.let {
                    putBadge(it.text, it.bgColor)
                } ?: run {
                    removeBadge()
                }
            }
            view.setOnClickListener { clickListener?.onClick(adapterPosition) }
        }
    }
}