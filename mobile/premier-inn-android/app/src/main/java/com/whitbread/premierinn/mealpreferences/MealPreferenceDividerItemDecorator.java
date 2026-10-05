package com.whitbread.premierinn.mealpreferences;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.whitbread.premierinn.bookingdetails.UpsellItemSummary;
import com.whitbread.premierinn.common.SpaceDividerItemDecoration;

public class MealPreferenceDividerItemDecorator extends SpaceDividerItemDecoration {

    private static final int BREAKFAST_ITEM_SPACE_DP = 1;
    private static final int NO_PREFERENCE_EXTRA_SPACE_DP = 4;

    MealPreferenceDividerItemDecorator() {
        super(BREAKFAST_ITEM_SPACE_DP);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        BreakfastOptionsListAdapter adapter = (BreakfastOptionsListAdapter) parent.getAdapter();
        BreakfastRadioButtonInput breakfastRadioButtonInput = adapter.getItemAtPosition(position);

        if (breakfastRadioButtonInput.code().equals(UpsellItemSummary.UpsellItemType.NO_PREFERENCE.code())) {
            outRect.top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, NO_PREFERENCE_EXTRA_SPACE_DP,
                    parent.getContext().getResources().getDisplayMetrics());
        }

    }
}