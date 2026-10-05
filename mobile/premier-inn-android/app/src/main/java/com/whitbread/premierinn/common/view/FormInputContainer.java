package com.whitbread.premierinn.common.view;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class FormInputContainer extends LinearLayout {
    public FormInputContainer(Context context) {
        super(context);
    }

    public FormInputContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FormInputContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutTransition layoutTransition = getLayoutTransition();
        if (layoutTransition != null) {
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        }
    }
}
