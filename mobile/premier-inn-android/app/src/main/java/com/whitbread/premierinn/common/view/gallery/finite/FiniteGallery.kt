package com.whitbread.premierinn.common.view.gallery.finite

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.SpaceDividerItemDecoration
import com.whitbread.premierinn.common.utils.getSnapPosition
import com.whitbread.premierinn.common.view.gallery.Gallery

private const val DIVIDER_SIZE_DP = 4

abstract class FiniteGallery<T, VH : RecyclerView.ViewHolder> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), Gallery<T> {

    private val recyclerView = RecyclerView(context)
    private lateinit var adapter: PhotoGalleryAdapter<T, VH>
    private val pagerSnapHelper = PagerSnapHelper()
    private lateinit var scrolledListener: ScrolledListener

    init {
        recyclerView.id = R.id.finite_gallery_recycler_view_id
        recyclerView.setHasFixedSize(true)

        val linearLayoutManager = object : LinearLayoutManager(context, HORIZONTAL, false) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                lp.width = (width * 0.85).toInt()
                return true
            }
        }
        recyclerView.layoutManager = linearLayoutManager
        pagerSnapHelper.attachToRecyclerView(recyclerView)

        val decor = SpaceDividerItemDecoration(
            DIVIDER_SIZE_DP,
            linearLayoutManager.orientation
        )
        recyclerView.addItemDecoration(decor)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val position = pagerSnapHelper.getSnapPosition(recyclerView)
                if (position != RecyclerView.NO_POSITION) {
                    updateCounter(position)
                }

                super.onScrolled(recyclerView, dx, dy)
            }
        })

        this.addView(recyclerView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }

    private fun updateCounter(currentPosition: Int) {
        scrolledListener.scrolled(currentPosition, adapter.items.size)
    }

    fun setScrolled(scrolledListener: ScrolledListener) {
        this.scrolledListener = scrolledListener
    }

    override fun images(images: List<T>) {
        adapter = createAdapter()
        adapter.items = images
        recyclerView.adapter = adapter
        updateCounter(0)
    }

    override fun currentItem(): Int {
        return (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    override fun setCurrentItem(position: Int) {
        recyclerView.scrollToPosition(position)
    }

    protected abstract fun createAdapter(): PhotoGalleryAdapter<T, VH>

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    interface ScrolledListener {
        fun scrolled(position: Int, size: Int)
    }
}