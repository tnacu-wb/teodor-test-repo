package com.whitbread.premierinn.common.mvp;

import androidx.annotation.NonNull;

import com.whitbread.premierinn.common.activity.BaseActivity;

public abstract class ViewContainer {

    private BaseActivity activity;

    public ViewContainer(@NonNull BaseActivity activity) {
        this.activity = activity;
    }

    public BaseActivity getActivity() {
        return activity;
    }
}
