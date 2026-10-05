package com.whitbread.premierinn.common;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.Toast;

public class ToastUtil {

    private final Context context;

    public ToastUtil(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    public void showLong(@NonNull String message) {
        // There is a (unavoidable) leak here due to Samsung's implementation of ViewConfiguration which inappropriately references the
        // activity context rather the application context. There is a pretty horrible workaround listed below... but since Toasts are
        // probably going to be replaced with something a little more custom, I think we can defer for now.
        // https://github.com/square/leakcanary/issues/1#issuecomment-100324683
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
