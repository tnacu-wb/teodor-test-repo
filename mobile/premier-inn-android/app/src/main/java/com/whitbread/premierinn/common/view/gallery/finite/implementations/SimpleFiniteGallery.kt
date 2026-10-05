package com.whitbread.premierinn.common.view.gallery.finite.implementations

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.view.NetworkImageView
import com.whitbread.premierinn.common.view.gallery.ItemClickListener
import com.whitbread.premierinn.common.view.gallery.finite.FiniteGallery
import com.whitbread.premierinn.common.view.gallery.finite.PhotoGalleryAdapter

/**
 * This is horizontal gallery of images
 * The input values are urls of images
 * There is beginning and the end
 */
class SimpleFiniteGallery @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FiniteGallery<String, SimpleFiniteGallery.ViewHolder>(context, attrs, defStyleAttr) {

    private var clickListener: ItemClickListener? = null

    override fun createAdapter(): PhotoGalleryAdapter<String, ViewHolder> {
        return object : PhotoGalleryAdapter<String, ViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item_image, parent, false) as NetworkImageView
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

    inner class ViewHolder(private val view: NetworkImageView) : RecyclerView.ViewHolder(view) {

        fun bindView(imageUrl: String) {
            view.loadNoAnimation(imageUrl, R.drawable.image_no_hotel)
            view.setOnClickListener { clickListener?.onClick(adapterPosition) }
        }
    }
}