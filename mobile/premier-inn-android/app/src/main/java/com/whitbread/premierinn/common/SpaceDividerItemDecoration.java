package com.whitbread.premierinn.common;

import android.graphics.Rect;
import androidx.annotation.IntDef;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Note:
 * Investigate https://gist.github.com/alexfu/f7b8278009f3119f523a
 * https://blog.fossasia.org/set-spacing-in-recyclerview-items-by-custom-item-decorator-in-phimpme-android-app/
 */
public class SpaceDividerItemDecoration extends RecyclerView.ItemDecoration {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HORIZONTAL, VERTICAL})
    public @interface Orientation { }
    private static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    private static final int VERTICAL = LinearLayout.VERTICAL;

    private final int spaceDp;
    @Orientation
    private final int orientation;
    private final boolean shouldExcludeLastElement;

    public SpaceDividerItemDecoration(int spaceDp) {
        this.spaceDp = spaceDp;
        this.orientation = RecyclerView.VERTICAL;
        this.shouldExcludeLastElement = false;
    }

    public SpaceDividerItemDecoration(int spaceDp, boolean shouldExcludeLastElement) {
        this.spaceDp = spaceDp;
        this.orientation = RecyclerView.VERTICAL;
        this.shouldExcludeLastElement = shouldExcludeLastElement;
    }

    public SpaceDividerItemDecoration(int spaceDp, @Orientation int orientation) {
        this.spaceDp = spaceDp;
        this.orientation = orientation;
        this.shouldExcludeLastElement = false;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spaceDp,
                parent.getContext().getResources().getDisplayMetrics());

        if (shouldExcludeLastElement) {
            if (parent.getAdapter() != null && parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = dimension;
            }
            return;
        }

        if (orientation == VERTICAL) {
            outRect.bottom = dimension;
        } else {
            outRect.right = dimension;
        }
    }
}
