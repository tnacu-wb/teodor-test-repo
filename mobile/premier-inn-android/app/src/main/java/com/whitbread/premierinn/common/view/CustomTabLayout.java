package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.tabs.TabLayout;
import com.whitbread.premierinn.R;

public class CustomTabLayout extends TabLayout implements TabLayout.OnTabSelectedListener {

    private int selectedStyle;
    private int unselectedStyle;

    public CustomTabLayout(Context context) {
        super(context);
        init();
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        selectedStyle = R.style.BodySemiBold;
        unselectedStyle = R.style.BodySemiBold;
    }

    @Override
    public void addTab(@NonNull Tab tab) {
        super.addTab(tab);
        AppCompatTextView textView =
                (AppCompatTextView) LayoutInflater.from(getContext()).inflate(R.layout.tab_custom_view, this, false);
        textView.setText(tab.getText());
        textView.setTextAppearance(this.getContext(), tab.getPosition() == 0 ? selectedStyle : unselectedStyle);
        textView.setTextColor(this.getContext().getResources().getColor(R.color.grey_dark));
        tab.setCustomView(textView);
        addOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(Tab tab) {
        ((AppCompatTextView) tab.getCustomView()).setTextAppearance(this.getContext(), selectedStyle);
        ((AppCompatTextView) tab.getCustomView()).setTextColor(this.getContext().getResources().getColor(R.color.grey_dark));

    }

    @Override
    public void onTabUnselected(Tab tab) {
        ((AppCompatTextView) tab.getCustomView()).setTextAppearance(this.getContext(), unselectedStyle);
        ((AppCompatTextView) tab.getCustomView()).setTextColor(this.getContext().getResources().getColor(R.color.grey_dark));
    }

    @Override
    public void onTabReselected(Tab tab) {

    }

    public void setTabTextColour(@ColorInt int color) {
        for (int i = 0; i < getTabCount(); i++) {
            AppCompatTextView tabTextView = (AppCompatTextView) getTabAt(i).getCustomView();
            tabTextView.setTextColor(color);
        }
    }
}
