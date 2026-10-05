package com.whitbread.premierinn.mealpreferences;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.FrameLayout;

import com.whitbread.premierinn.common.utils.HtmlUtils;
import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.databinding.ViewBreakfastRadioButtonBinding;

public class BreakfastRadioButtonView extends FrameLayout implements Checkable {

    private BreakfastRadioButtonInput input;
    private ViewBreakfastRadioButtonBinding binding;

    public BreakfastRadioButtonView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public BreakfastRadioButtonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BreakfastRadioButtonView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        binding = ViewBreakfastRadioButtonBinding.inflate(LayoutInflater.from(context), this);
        ViewGroup.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);

        setClickable(true);

        float oneDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.0f, getResources().getDisplayMetrics());

        setBackgroundColor(Color.WHITE);
        ViewCompat.setElevation(this, oneDp);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
        binding.rbBreakfastRadioButton.setOnClickListener(l);
    }

    @Override
    public boolean isChecked() {
        return binding.rbBreakfastRadioButton.isChecked();
    }

    @Override
    public void setChecked(boolean b) {
        binding.rbBreakfastRadioButton.setChecked(b);
    }

    @Override
    public void toggle() {
        binding.rbBreakfastRadioButton.toggle();
    }

    public void setContent(@NonNull BreakfastRadioButtonInput content) {
        input = content;
        binding.tvBreakfastLegend.setText(content.legend());
        if (StringUtils.EMPTY_STRING.contentEquals(content.price())) {
            binding.tvBreakfastPrice.setVisibility(GONE);
        } else {
            binding.tvBreakfastPrice.setVisibility(VISIBLE);
            binding.tvBreakfastPrice.setText(content.price());
        }
        if (StringUtils.isBlank(content.description())) {
            binding.tvBreakfastDescription.setVisibility(GONE);
        } else {
            binding.tvBreakfastDescription.setVisibility(VISIBLE);
            binding.tvBreakfastDescription.setText(HtmlUtils.parseTags(content.description()));
        }
    }

    public void setAddedMessage(@NonNull String message) {
        binding.llBreakfastAdded.setVisibility("".equals(message) ? GONE : VISIBLE);
        binding.tvBreakfastValidationMessage.setText(message);
    }

    public BreakfastRadioButtonInput getInput() {
        return input;
    }
}
