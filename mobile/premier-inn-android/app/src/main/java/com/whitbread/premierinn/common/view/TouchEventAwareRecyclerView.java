package com.whitbread.premierinn.common.view;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Overridden recyclerview implementation in order to intercept various touch events and especially swipe/left right gestures on children
 * views for smoother scroll experience. This is mainly ideal when this recyclerview (vertical) hosts nested recyclerviews with horizontal
 * layoutmanager.
 * for info : http://nerds.headout.com/fix-horizontal-scrolling-in-your-android-app/
 */
public class TouchEventAwareRecyclerView extends RecyclerView {
    private static final int INVALID_POINTER = -1;
    private int scrollPointerId = INVALID_POINTER;
    private int initialTouchX = 0;
    private int initialTouchY = 0;
    private int touchSlop;

    public TouchEventAwareRecyclerView(Context context) {
        super(context);
    }

    public TouchEventAwareRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchEventAwareRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ViewConfiguration vc = ViewConfiguration.get(context);
        touchSlop = vc.getScaledTouchSlop();
    }

    @Override
    public void setScrollingTouchSlop(final int slopConstant) {
        super.setScrollingTouchSlop(slopConstant);
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        switch (slopConstant) {
            case TOUCH_SLOP_DEFAULT:
                touchSlop = vc.getScaledTouchSlop();
                break;
            case TOUCH_SLOP_PAGING:
                touchSlop = vc.getScaledPagingTouchSlop();
                break;
            default:
        }
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent e) {
        if (e == null) {
            return false;
        }
        int action = MotionEventCompat.getActionMasked(e);
        int actionIndex = MotionEventCompat.getActionIndex(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                scrollPointerId = e.getPointerId(0);
                initialTouchX = Math.round(e.getX() + 0.5f);
                initialTouchY = Math.round(e.getY() + 0.5f);
                return super.onInterceptTouchEvent(e);
            case MotionEvent.ACTION_POINTER_DOWN:
                scrollPointerId = e.getPointerId(actionIndex);
                initialTouchX = Math.round(e.getX(actionIndex) + 0.5f);
                initialTouchY = Math.round(e.getY(actionIndex) + 0.5f);
                return super.onInterceptTouchEvent(e);
            case MotionEvent.ACTION_MOVE:
                int index = e.findPointerIndex(scrollPointerId);
                if (index < 0) {
                    return false;
                }
                int x = Math.round(e.getX(index) + 0.5f);
                int y = Math.round(e.getY(index) + 0.5f);
                if (getScrollState() != SCROLL_STATE_DRAGGING) {
                    int dx = x - initialTouchX;
                    int dy = y - initialTouchY;
                    boolean startScroll = false;
                    if (getLayoutManager().canScrollHorizontally() && Math.abs(dx) > touchSlop
                            && (getLayoutManager().canScrollVertically() || Math.abs(dx) > Math.abs(dy))) {
                        startScroll = true;
                    }

                    if (getLayoutManager().canScrollVertically() && Math.abs(dy) > touchSlop
                            && (getLayoutManager().canScrollHorizontally() || Math.abs(dy) > Math.abs(dx))) {
                        startScroll = true;
                    }
                    return startScroll && super.onInterceptTouchEvent(e);
                }
                return super.onInterceptTouchEvent(e);
            default:
                return super.onInterceptTouchEvent(e);
        }
    }
}
