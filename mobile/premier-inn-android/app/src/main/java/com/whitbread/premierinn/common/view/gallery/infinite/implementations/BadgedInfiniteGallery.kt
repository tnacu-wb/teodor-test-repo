package com.whitbread.premierinn.common.view.gallery.infinite.implementations

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.view.gallery.BadgedImageView
import com.whitbread.premierinn.common.view.gallery.Image
import com.whitbread.premierinn.common.view.gallery.infinite.InfiniteGallery
import com.whitbread.premierinn.common.view.gallery.infinite.InfiniteViewPagerAdapter

/**
 * This is a horizontal gallery of images that might have a badge on top
 * The input values are urls of images with information about the badge wrapped in [Image] object
 * There is no beginning or end, it is circular
 */
class BadgedInfiniteGallery @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InfiniteGallery<Image>(context, attrs, defStyleAttr) {

    override fun createAdapter(images: List<Image>): InfiniteViewPagerAdapter<Image> {
        return object : InfiniteViewPagerAdapter<Image>(images) {

            override fun getView(position: Int, pager: ViewPager?): View {
                val view = BadgedImageView(context)
                val image = getItem(position)
                view.loadImage(image.imageUrl, R.drawable.image_no_hotel)
                image.badge?.let { view.putBadge(it.text, it.bgColor) } ?: run { view.removeBadge() }
                return view
            }

        }
    }
}