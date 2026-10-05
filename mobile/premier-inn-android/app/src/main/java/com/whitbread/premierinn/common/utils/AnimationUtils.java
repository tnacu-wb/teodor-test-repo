package com.whitbread.premierinn.common.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import androidx.annotation.NonNull;
import android.view.View;

public class AnimationUtils {

    private AnimationUtils() {
        throw new AssertionError("no instances allowed");
    }

    public static ObjectAnimator alphaInfiniteRepeat(@NonNull View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setDuration(1000);
        return animator;
    }
}
