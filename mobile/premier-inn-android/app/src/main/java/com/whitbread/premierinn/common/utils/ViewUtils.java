package com.whitbread.premierinn.common.utils;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import io.reactivex.functions.Predicate;

public class ViewUtils {

    public static void hideKeyboard(@NonNull Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void showKeyboard(@NonNull Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public static int getToolbarHeight(@NonNull Activity activity) {
        TypedValue tv = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
        } else {
            return 0;
        }
    }

    @NonNull
    public static Predicate<Integer> allowIfValidPosition() {
        return position -> position != NO_POSITION;
    }

    public static float convertPixelsToDp(float px, Context context) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (px * density);
    }
}
