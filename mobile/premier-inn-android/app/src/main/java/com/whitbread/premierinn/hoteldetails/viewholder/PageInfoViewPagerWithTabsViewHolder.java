package com.whitbread.premierinn.hoteldetails.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.GlideApp;
import com.whitbread.premierinn.databinding.ViewHotelDetailsTabbedContentBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsTextviewContentBinding;
import com.whitbread.premierinn.databinding.ViewHotelDetailsTextviewImageContentBinding;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.SpannableContentFactory;
import com.whitbread.premierinn.hoteldetails.uimodel.TabbedContentUiModel;

import java.util.ArrayList;
import java.util.List;

public class PageInfoViewPagerWithTabsViewHolder extends BaseRecyclerViewHolder<TabbedContentUiModel> {
    private final ViewHotelDetailsTabbedContentBinding binding;

    private static final int POSITION_FOOD_DESCRIPTION = 0;
    private static final int POSITION_RESTAURANT_DESCRIPTION = 1;

    PageInfoViewPagerWithTabsViewHolder(ViewHotelDetailsTabbedContentBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(TabbedContentUiModel model) {
        Context context = binding.getRoot().getContext();
        List<CharSequence> pages = new ArrayList<>(model.tabContentList().size());

        for (String title : model.tabTitles()) {
            if (binding.hotelDetailsTabs.getTabCount() != model.tabTitles().size()) {
                binding.hotelDetailsTabs.addTab(binding.hotelDetailsTabs.newTab().setText(title));
            }
        }
        for (int i = 0; i < model.tabContentList().size(); i++) {
            CharSequence pageContent = SpannableContentFactory.createContent(context, model.tabContentList().get(i));
            pages.add(pageContent);
        }

        if (model.hubStyle()) {
            binding.hotelDetailsTabs.setTabTextColour(ContextCompat.getColor(context, R.color.white));
            binding.hotelDetailsTabs.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_dark));
            binding.hotelDetailsTabs.setSelectedTabIndicatorColor(ContextCompat.getColor(context, R.color.green));
        }

        binding.hotelDetailsContentPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return model.tabTitles().size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ViewPager pager = (ViewPager) container;
                LayoutInflater layoutInflater = LayoutInflater.from(binding.hotelDetailsTabs.getContext());

                ViewBinding viewBinding = null;

                if (position == POSITION_FOOD_DESCRIPTION) {
                    ViewHotelDetailsTextviewContentBinding layout =
                            ViewHotelDetailsTextviewContentBinding.inflate(layoutInflater, pager, false);
                    viewBinding = layout;
                    layout.spannableTextContainer.setText(pages.get(position));
                }

                if (position == POSITION_RESTAURANT_DESCRIPTION) {
                    ViewHotelDetailsTextviewImageContentBinding inflate =
                            ViewHotelDetailsTextviewImageContentBinding.inflate(layoutInflater, pager, false);
                    viewBinding = inflate;

                    inflate.spannableTextContainer.setText(pages.get(position));
                    if (model.imageUrl() != null) {
                        inflate.restaurantImage.setVisibility(View.VISIBLE);
                        GlideApp.with(inflate.restaurantImage)
                                .asBitmap()
                                .centerInside()
                                .load(model.imageUrl())
                                .into(inflate.restaurantImage);
                    }
                }

                pager.addView(viewBinding.getRoot());
                return viewBinding.getRoot();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });

        binding.hotelDetailsTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.hotelDetailsContentPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.hotelDetailsContentPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.hotelDetailsTabs));
    }
}
