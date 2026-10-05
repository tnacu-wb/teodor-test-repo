package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.databinding.ViewInfoBannerBinding;

public class InfoBannerView extends FrameLayout {

    private ViewInfoBannerBinding binding;

    public InfoBannerView(Context context) {
        super(context);
        init(context, null);
    }

    public InfoBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public InfoBannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = ViewInfoBannerBinding.inflate(inflater, this, true);
        if (attrs != null) {
            TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.InfoBannerView);
            CharSequence textAttr = attributesArray.getText(R.styleable.InfoBannerView_android_text);
            binding.bannerText.setText(textAttr);
            attributesArray.recycle();
        }
    }
}