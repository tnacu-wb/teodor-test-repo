package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.GlideApp;
import com.whitbread.premierinn.common.view.svg.SvgSoftwareLayerSetter;

public class NetworkImageView extends AppCompatImageView {
    public NetworkImageView(Context context) {
        super(context);
    }

    public NetworkImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void load(String url) {
        GlideApp.with(getContext())
                .load(url)
                .into(this);
    }

    public void load(String url, @DrawableRes int errorImage) {
        GlideApp.with(getContext())
                .load(url)
                .placeholder(R.drawable.image_no_hotel)
                .error(errorImage)
                .into(this);
    }

    public void loadNoAnimation(String url, @DrawableRes int placeHolder) {
        GlideApp.with(getContext())
                .load(url)
                .dontAnimate()
                .dontTransform()
                .error(placeHolder)
                .into(this);
    }

    public void loadSvg(String url) {
        GlideApp.with(getContext())
                .as(Drawable.class)
                .addListener(new SvgSoftwareLayerSetter())
                .load(url)
                .into(this);
    }
}
