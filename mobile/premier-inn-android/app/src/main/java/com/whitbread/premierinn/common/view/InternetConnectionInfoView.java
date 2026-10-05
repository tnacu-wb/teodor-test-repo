package com.whitbread.premierinn.common.view;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.databinding.LayoutNetworkConnectionInfoBarBinding;

public class InternetConnectionInfoView extends LinearLayout {

    private int height;
    private LayoutNetworkConnectionInfoBarBinding binding;

    public InternetConnectionInfoView(Context context) {
        super(context);
        init(context);
    }

    public InternetConnectionInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InternetConnectionInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void showConnectionSuccessful(boolean isOnline) {
        if (isOnline && getVisibility() == VISIBLE) {
            binding.tvNetworkConnectionConnection.setText(R.string.online_message);
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal_light));
            animateOffScreen();
        } else if (!isOnline && getVisibility() == GONE) {
            setAlpha(0f);
            setVisibility(VISIBLE);
            binding.tvNetworkConnectionConnection.setText(R.string.offline_message);

            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
            animateOntoScreen();
        }
    }

    private void animateOffScreen() {
        animate()
                .alpha(0f)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setVisibility(GONE);
                    }
                })
                .start();
    }

    private void animateOntoScreen() {
        setTranslationY(-height);
        animate()
                .alpha(1f)
                .translationYBy(height)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setVisibility(VISIBLE);
                    }
                })
                .start();
    }

    private void init(@NonNull Context context) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = LayoutNetworkConnectionInfoBarBinding.inflate(inflater, this);

        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER);
        setVisibility(GONE);

        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        height = getMeasuredHeight();
    }
}
