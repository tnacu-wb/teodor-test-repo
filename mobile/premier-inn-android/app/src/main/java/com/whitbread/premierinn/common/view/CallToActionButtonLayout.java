package com.whitbread.premierinn.common.view;

import static com.whitbread.premierinn.common.utils.AppExtensions.dpToPx;

import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.jakewharton.rxbinding3.view.RxView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.utils.NetworkUtils;
import com.whitbread.premierinn.databinding.ViewCallToActionButtonBinding;

import io.reactivex.Observable;
import kotlin.Unit;


public class CallToActionButtonLayout extends RelativeLayout {

    private static final int MAX_ALPHA_VALUE_DRAWABLE = 255;
    private static final int DISABLED_STATE_VALUE_DRAWABLE = MAX_ALPHA_VALUE_DRAWABLE / 4;
    private static final float MAX_ALPHA_VALUE_VIEW = 1.0f;
    private static final float DISABLED_STATE_VALUE_VIEW = MAX_ALPHA_VALUE_VIEW / 4;
    private static final float DEFAULT_ELEVATION_DP = 2;
    private static final float PRESSED_ELEVATION_DP = 4;

    private float defaultElevation;
    private float elevationPressed;
    private Drawable drawableBackground;

    ViewCallToActionButtonBinding binding;

    public CallToActionButtonLayout(Context context) {
        super(context);
        init(context, null);
    }

    public CallToActionButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CallToActionButtonLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CallToActionButtonLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        binding = ViewCallToActionButtonBinding.inflate(LayoutInflater.from(context), this);

        defaultElevation = dpToPx(getContext(), DEFAULT_ELEVATION_DP);
        elevationPressed = dpToPx(getContext(), PRESSED_ELEVATION_DP);
        drawableBackground = ContextCompat.getDrawable(context, R.drawable.call_to_action_button_background);
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{androidx.appcompat.R.attr.colorAccent});
        int accentColor = a.getColor(0, 0);
        a.recycle();
        drawableBackground.setColorFilter(accentColor, PorterDuff.Mode.SRC);
        setBackground(drawableBackground);

        setClickable(true);
        setElevation(dpToPx(getContext(), 2));
        setStateListAnimator(AnimatorInflater.loadStateListAnimator(context, R.animator.button_elevation));

        if (attrs != null) {
            TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.CallToActionButton);
            String text = attributesArray.getString(R.styleable.CallToActionButton_android_text);
            defaultElevation = attributesArray
                    .getDimensionPixelSize(R.styleable.CallToActionButton_elevation, (int) defaultElevation);

            elevationPressed = attributesArray
                    .getDimensionPixelSize(R.styleable.CallToActionButton_elevation, (int) elevationPressed);

            if (text != null && text.length() > 0) {
                binding.tvCallToActionButtonText.setText(text);
            }
            if (attributesArray.getBoolean(R.styleable.CallToActionButton_cab_padlock, false)) {
                binding.ivCallToActionPadlock.setVisibility(VISIBLE);
            }

            int bgColor = attributesArray.getColor(R.styleable.CallToActionButton_cab_background_color, accentColor);

            if (attributesArray.getBoolean(R.styleable.CallToActionButton_cab_is_secondary, false)) {
                Drawable frameBg = ContextCompat.getDrawable(context, R.drawable.call_to_action_button_secondary_background);
                if (bgColor != accentColor) {
                    frameBg.setTint(bgColor);
                }
                setBackground(frameBg);
            } else {
                if (bgColor != accentColor) {
                    setButtonBackgroundColor(bgColor);
                }
            }

            int textColor = attributesArray.getColor(R.styleable.CallToActionButton_cab_text_color, accentColor);
            if (textColor != accentColor) {
                setButtonTextColor(textColor);
            }

            float textSize = attributesArray.getDimensionPixelSize(R.styleable.CallToActionButton_cab_text_size, 0);
            if (textSize != 0) {
                binding.tvCallToActionButtonText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            if (attributesArray.hasValue(R.styleable.CallToActionButton_cab_spinner_color)) {
                int spinnerColor = attributesArray.getColor(R.styleable.CallToActionButton_cab_spinner_color, 0);
                binding.pbTvCallToActionButtonLoading.setIndeterminateTintList(ColorStateList.valueOf(spinnerColor));
            }

            int padlockColor = attributesArray.getColor(R.styleable.CallToActionButton_cab_padlock_color, accentColor);
            if (padlockColor != accentColor) {
                setPadlockColour(padlockColor);
            }
            attributesArray.recycle();
        }
    }

    public void setLoadingState(boolean loadingState) {
        binding.tvCallToActionButtonText.setVisibility(loadingState ? View.GONE : View.VISIBLE);
        binding.pbTvCallToActionButtonLoading.setVisibility(loadingState ? View.VISIBLE : View.GONE);
        ViewCompat.setElevation(this, loadingState ? elevationPressed : defaultElevation);
        super.setEnabled(!loadingState);
    }

    public void setText(@Nullable String string) {
        binding.tvCallToActionButtonText.setText(string);
    }

    public void setButtonTextGravity(int gravity) {
        binding.tvCallToActionButtonText.setGravity(gravity);
    }

    @Override
    public void setEnabled(boolean enableState) {
        super.setEnabled(enableState);
        getBackground().setAlpha(enableState ? MAX_ALPHA_VALUE_DRAWABLE : DISABLED_STATE_VALUE_DRAWABLE);
        binding.tvCallToActionButtonText.setAlpha(enableState ? MAX_ALPHA_VALUE_VIEW : DISABLED_STATE_VALUE_VIEW);
    }

    public void setButtonBackgroundColor(@ColorInt int color) {
        drawableBackground.setColorFilter(color, PorterDuff.Mode.SRC);
        setBackground(drawableBackground);
    }

    public void setButtonTextColor(@ColorInt int color) {
        binding.tvCallToActionButtonText.setTextColor(color);
    }

    /**
     * This method will return an observable which emits on view click events
     * in case there is a successful internet connection
     * @return
     */
    public Observable<Unit> onClickOnInternetAvailable() {
        return RxView.clicks(this)
                .filter(__ -> {
                    boolean isInternetConnected = NetworkUtils.hasInternetConnection(getContext());
                    if (!isInternetConnected) {
                        Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                    return isInternetConnected;
                });
    }


    public void setButtonAs(String buttonText) {
        switch (buttonText) {
            case "update":
                setText(getContext().getString(R.string.button_text_update));
                break;
            case "availability":
                setText(getContext().getString(R.string.amend_button_text_availability));
                break;
            case "continue":
                setText(getContext().getString(R.string.button_text_continue));
                break;
            default:
                break;
        }

        setButtonBackgroundColor(ContextCompat.getColor(getContext(), R.color.premier_inn_purple));
        setButtonTextColor(ContextCompat.getColor(getContext(), android.R.color.white));

        Drawable progressDrawable = binding.pbTvCallToActionButtonLoading.getIndeterminateDrawable().mutate();
        progressDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        binding.pbTvCallToActionButtonLoading.setProgressDrawable(progressDrawable);
    }

    public void setButtonAsUpdate() {
        setButtonAs("update");
    }

    public void setPadlockEnabled(Boolean state) {
        binding.ivCallToActionPadlock.setAlpha(state ? MAX_ALPHA_VALUE_VIEW : DISABLED_STATE_VALUE_VIEW);
    }

    public void setPadlockVisibility(Boolean visibility) {
        binding.ivCallToActionPadlock.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    private void setPadlockColour(@ColorInt int color) {
        binding.ivCallToActionPadlock.setColorFilter(color);
    }
}