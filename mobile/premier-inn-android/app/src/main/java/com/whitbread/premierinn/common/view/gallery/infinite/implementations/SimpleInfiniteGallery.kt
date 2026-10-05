package com.whitbread.premierinn.common.view.gallery.infinite.implementations

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.view.NetworkImageView
import com.whitbread.premierinn.common.view.gallery.infinite.InfiniteGallery
import com.whitbread.premierinn.common.view.gallery.infinite.InfiniteViewPagerAdapter

/**
 * This is horizontal gallery of images
 * The input values are urls of images
 * There is no beginning or end, it is circular
 */
class SimpleInfiniteGallery @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InfiniteGallery<String>(context, attrs, defStyleAttr) {

    override fun createAdapter(images: List<String>): InfiniteViewPagerAdapter<String> {
        return object : InfiniteViewPagerAdapter<String>(images) {

            override fun getView(position: Int, pager: ViewPager?): View {
                val view = LayoutInflater.from(context).inflate(R.layout.view_item_image, pager, false)
                val url = getItem(position)
                view.findViewById<NetworkImageView>(R.id.image).load(url, R.drawable.image_no_hotel)
                return view
            }
        }
    }
}