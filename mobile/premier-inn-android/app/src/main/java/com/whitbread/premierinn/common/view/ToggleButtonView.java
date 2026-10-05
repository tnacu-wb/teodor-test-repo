package com.whitbread.premierinn.common.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import com.jakewharton.rxbinding3.view.RxView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.span.CustomTypefaceSpan;
import com.whitbread.premierinn.databinding.ViewToggleButtonBinding;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;


public class ToggleButtonView extends LinearLayout {

    public static final int ANIMATION_DURATION_ON_ELEVATION_PROPERTY = 500;
    public static final int COMPONENT_ELEVATION = 4;

    ViewToggleButtonBinding binding;

    private boolean startSelected = false;
    private boolean needElevation = true;
    private static float componentElevation;
    private Disposable disposable;

    public enum State {
        LEFT,
        RIGHT,
        NONE
    }

    public ToggleButtonView(Context context) {
        super(context);
        init(context, null);
    }

    public ToggleButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ToggleButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        componentElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, COMPONENT_ELEVATION,
                context.getResources().getDisplayMetrics());

        binding = ViewToggleButtonBinding.inflate(LayoutInflater.from(getContext()), this);
        disposable = getClick().subscribe();

        if (attrs != null) {
            Typeface premierInnBold = ResourcesCompat.getFont(context, R.font.proxima_nova_semibold);
            TypedArray attributesArray = getContext().obtainStyledAttributes(attrs, R.styleable.ToggleButtonView);
            binding.bToggleButtonLeft.setText(setTextToTypeface(
                    attributesArray.getString(R.styleable.ToggleButtonView_tbv_text_left), premierInnBold));
            binding.bToggleButtonRight.setText(setTextToTypeface(
                    attributesArray.getString(R.styleable.ToggleButtonView_tbv_text_right), premierInnBold));
            startSelected = attributesArray.getBoolean(R.styleable.ToggleButtonView_tbv_start_selected, false);
            attributesArray.recycle();
        }

        if (startSelected) {
            setState(State.LEFT);
            needElevation = false;
            ViewCompat.setElevation(this, componentElevation);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDetachedFromWindow();
    }

    private SpannableString setTextToTypeface(String string, Typeface typeface) {
        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new CustomTypefaceSpan("", typeface), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private void addElevation() {
        if (needElevation) {
            needElevation = false;
            ObjectAnimator animator = ObjectAnimator.ofFloat(this, "elevation", 0f, componentElevation);
            animator.setDuration(ANIMATION_DURATION_ON_ELEVATION_PROPERTY);
            animator.start();
        }
    }

    public Observable<State> getClick() {
        return Observable.merge(onClickLeft(), onCLickRight())
                .doOnEach(stateNotification -> setState(stateNotification.getValue()));
    }

    /**
     * @return A stream of the button state starting with the current state when subscribed.
     */
    public Observable<State> getState() {
        return getCurrentState().mergeWith(getClick());
    }

    /**
     * @return A stream of only the current button state. Does not send future state changes.
     */
    public Observable<State> getCurrentState() {
        if (binding.bToggleButtonLeft.isSelected()) {
            return Observable.just(State.LEFT);
        }
        if (binding.bToggleButtonRight.isSelected()) {
            return Observable.just(State.RIGHT);
        }
        return Observable.just(State.NONE);
    }

    public void setState(@NonNull State state) {
        addElevation();
        binding.bToggleButtonLeft.setSelected(state == State.LEFT);
        binding.bToggleButtonRight.setSelected(state == State.RIGHT);
    }

    public void performClick(@NonNull State state) {
        if (state == State.LEFT) {
            binding.bToggleButtonLeft.performClick();
        } else {
            binding.bToggleButtonRight.performClick();
        }
    }

    public void setEnabled(boolean enabled) {
        binding.bToggleButtonLeft.setEnabled(enabled);
        binding.bToggleButtonRight.setEnabled(enabled);
    }

    public void performSelectedClick() {
        if (binding.bToggleButtonLeft.isSelected()) {
            binding.bToggleButtonLeft.performClick();
        } else {
            binding.bToggleButtonRight.performClick();
        }
    }

    private Observable<State> onClickLeft() {
        return RxView.clicks(binding.bToggleButtonLeft)
                .map(o -> State.LEFT);
    }

    private Observable<State> onCLickRight() {
        return RxView.clicks(binding.bToggleButtonRight)
                .map(o -> State.RIGHT);
    }
}
