package com.whitbread.premierinn.common.view.gallery.infinite

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.view.ZoomOutViewPagerTransformer
import com.whitbread.premierinn.common.view.gallery.Gallery

abstract class InfiniteGallery<T> @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), Gallery<T> {

    private val middlePosition = 0
    private val viewPager: ViewPager = ViewPager(context)
    private lateinit var adapter: InfiniteViewPagerAdapter<T>

    init {
        viewPager.id = R.id.infinite_gallery_view_pager_id
        this.addView(viewPager, LayoutParams(MATCH_PARENT, MATCH_PARENT))

        if (attrs != null) {
            val attributesArray = context.obtainStyledAttributes(attrs, R.styleable.InfiniteGallery)
            if (attributesArray.getBoolean(R.styleable.InfiniteGallery_ivp_zoomOutPageTransformer, false)) {
                viewPager.setPageTransformer(true, ZoomOutViewPagerTransformer())
            }
            val pageMarginDp = attributesArray.getFloat(R.styleable.InfiniteGallery_ivp_pageMarginInDp, 0f)
            val pageMarginPx = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    pageMarginDp,
                    resources.displayMetrics)
            viewPager.pageMargin = pageMarginPx.toInt()
            attributesArray.recycle()
        }
        viewPager.offscreenPageLimit = NUMBER_VIEWS_RETAINED_ON_BOTH_SIDES
    }

    override fun images(images: List<T>) {
        adapter = createAdapter(images)
        viewPager.adapter = adapter
        val middlePosition = adapter.firstRealPosition
        viewPager.currentItem = middlePosition
    }

    override fun currentItem() = viewPager.currentItem

    override fun setCurrentItem(position: Int) {
        viewPager.currentItem = position
    }

    abstract protected fun createAdapter(images: List<T>): InfiniteViewPagerAdapter<T>

    fun firstRealPosition() : Int {
        return adapter.firstRealPosition
    }

    fun realCurrentItem() : Int {
        return viewPager.currentItem % adapter.items.size
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    companion object {
        private const val NUMBER_VIEWS_RETAINED_ON_BOTH_SIDES = 1
    }
}