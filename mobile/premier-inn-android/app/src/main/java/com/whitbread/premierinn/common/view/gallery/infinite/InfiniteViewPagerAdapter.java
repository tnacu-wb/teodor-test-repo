package com.whitbread.premierinn.common.view.gallery.infinite;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * ViewPagerAdapter with a bi-direction carousel behaviour. The next picture
 * after the last one is the first picture.
 * <p>
 */
public abstract class InfiniteViewPagerAdapter<T> extends PagerAdapter {

    private static final int BIG_ROUNDED_NUMBER = 1000;
    private final List<T> collection;

    public InfiniteViewPagerAdapter(List<T> collection) {
        this.collection = collection;
    }

    protected T getItem(int position) {
        return collection.get(position);
    }

    public abstract View getView(int position, ViewPager pager);

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewPager pager = (ViewPager) container;
        int realPosition = position % collection.size();
        View view = getView(realPosition, pager);
        pager.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return collection.size() * BIG_ROUNDED_NUMBER;
    }

    public int getFirstRealPosition() {
        return getCount() / 2;
    }

    public List<T> getItems() {
        return collection;
    }
}
