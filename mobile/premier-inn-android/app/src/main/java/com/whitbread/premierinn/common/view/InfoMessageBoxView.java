package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.databinding.ViewInfoMessageBoxBinding;

public class InfoMessageBoxView extends LinearLayout {

    ViewInfoMessageBoxBinding binding;

    public InfoMessageBoxView(Context context) {
        super(context);
        init(null);
    }

    public InfoMessageBoxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public InfoMessageBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public InfoMessageBoxView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet) {
        inflateLayout();
        bindCustomAttributes(attributeSet);
    }

    private void inflateLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = ViewInfoMessageBoxBinding.inflate(inflater, this);
    }

    private void bindCustomAttributes(@Nullable AttributeSet attributeSet) {
        if (attributeSet == null) {
            return;
        }

        final TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.InfoMessageBoxView);

        int backgroundRes = typedArray.getResourceId(R.styleable.InfoMessageBoxView_android_background, 0);

        if (backgroundRes != 0) {
            setBackgroundResource(backgroundRes);
        }

        int iconRes = typedArray.getResourceId(R.styleable.InfoMessageBoxView_android_src, 0);
        if (iconRes != 0) {
            setIconResource(iconRes);
        }

        int iconColorRes = typedArray.getResourceId(R.styleable.InfoMessageBoxView_iconTintColor, 0);
        if (iconColorRes != 0) {
            setIconColorFilter(iconColorRes);
        }

        int textIdRes = typedArray.getResourceId(R.styleable.InfoMessageBoxView_android_id, 0);
        if (textIdRes != 0) {
            binding.text.setId(textIdRes);
        }

        int textRes = typedArray.getResourceId(R.styleable.InfoMessageBoxView_android_text, 0);
        if (textRes != 0) {
            binding.text.setText(textRes);
        }

        int textColorRes = typedArray.getResourceId(R.styleable.InfoMessageBoxView_android_textColor, 0);

        if (textColorRes != 0) {
            setTextColorResource(textColorRes);
        }

        int textSize = typedArray.getDimensionPixelSize(R.styleable.InfoMessageBoxView_android_textSize, 0);
        if (textSize > 0) {
            binding.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }

        int minLines = typedArray.getInt(R.styleable.InfoMessageBoxView_android_minLines, 0);
        if (minLines > 0) {
            binding.text.setMinLines(minLines);
        }

        if (textColorRes != 0) {
            binding.text.setTextColor(ContextCompat.getColor(getContext(), textColorRes));
        }

        boolean alignTextCenter = typedArray.getBoolean(R.styleable.InfoMessageBoxView_alignTextCenter, false);
        if (alignTextCenter) {
            binding.text.setGravity(Gravity.CENTER);
        }

        typedArray.recycle();
    }

    public void setHtmlText(@NonNull String htmlContent) {
        binding.text.setText(Html.fromHtml(htmlContent));
        binding.text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setIconResource(@DrawableRes int iconResource) {
        binding.icon.setVisibility(VISIBLE);
        binding.icon.setImageResource(iconResource);
    }

    public void setTextColorResource(@ColorRes int textColorResource) {
        binding.text.setTextColor(ContextCompat.getColor(getContext(), textColorResource));
    }

    public void setText(@NonNull String textContent) {
        binding.text.setText(textContent);
    }

    /**
     * Set text with resource id in order to properly format any HTML tags the text may have.
     */
    public void setText(int textResId) {
        binding.text.setText(textResId);
    }

    public void setText(SpannableString spannable) {
        binding.text.setText(spannable);
    }

    public void setTextSize(@DimenRes int textSizeDimen) {
        binding.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(textSizeDimen));
    }

    public void setIconColorFilter(@ColorRes int iconColorFilter) {
        binding.icon.setColorFilter(ContextCompat.getColor(getContext(), iconColorFilter), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public void setFontFamily(@FontRes int fontResource) {
        binding.text.setTypeface(ResourcesCompat.getFont(getContext(), fontResource));
    }
}
