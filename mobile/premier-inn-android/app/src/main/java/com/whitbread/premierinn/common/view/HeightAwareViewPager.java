package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.HashMap;
import java.util.Map;

/**
 * Special thanks to Phate P for his response
 * (https://stackoverflow.com/a/51136278)
 */
public class HeightAwareViewPager extends ViewPager {

    private Map<Integer, Integer> childHeights;
    private int minHeight = 0;
    private int currentPosition = 0;

    public HeightAwareViewPager(Context context) {
        super(context);
        init();
    }

    public HeightAwareViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        obtainMinHeightAttribute(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setCurrentItem(currentPosition, true);
        measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //calculate child views
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h < minHeight) {
                h = minHeight;
            }

            childHeights.putIfAbsent(i, h);
        }

        if (childHeights.size() - 1 >= currentPosition) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeights.get(currentPosition), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init() {
        childHeights = new HashMap<>();
        setOnPageChangeListener();
    }

    private void obtainMinHeightAttribute(@NonNull Context context, @Nullable AttributeSet attrs) {
        int[] heightAttr = new int[]{android.R.attr.minHeight};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, heightAttr);
        minHeight = typedArray.getDimensionPixelOffset(0, -666);
        typedArray.recycle();
    }

    private void setOnPageChangeListener() {
        this.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;

                ViewGroup.LayoutParams layoutParams = HeightAwareViewPager.this.getLayoutParams();
                layoutParams.height = childHeights.get(position);
                HeightAwareViewPager.this.setLayoutParams(layoutParams);
                HeightAwareViewPager.this.invalidate();
            }
        });
    }
}
