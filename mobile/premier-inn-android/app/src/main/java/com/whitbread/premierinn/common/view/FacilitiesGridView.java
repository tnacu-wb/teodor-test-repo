package com.whitbread.premierinn.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.gridlayout.widget.GridLayout;
import android.util.AttributeSet;
import android.view.Gravity;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.availability.Facility;

import java.util.Iterator;
import java.util.List;

public class FacilitiesGridView extends GridLayout {

    private static final int NUM_COLUMNS = 1;
    private static final float COLUMN_WEIGHT = 1f;
    private int facilityMarginBottomDp;

    public FacilitiesGridView(Context context) {
        super(context);
        init(context, null);
    }

    public FacilitiesGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FacilitiesGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FacilitiesGridView);
            try {
                facilityMarginBottomDp = ta.getLayoutDimension(R.styleable.FacilitiesGridView_fgv_bottomMargin, 0);
            } finally {
                ta.recycle();
            }
        }
    }

    public void showFacilities(List<Facility> facilities) {
        setOrientation(HORIZONTAL);
        if (facilities != null) {
            removeAllViews();
            int numFacilities = facilities.size();
            int numRows = (int) Math.ceil(numFacilities / (float) NUM_COLUMNS);
            setColumnCount(NUM_COLUMNS);
            setRowCount(numRows);
            Iterator<Facility> facilityIterator = facilities.iterator();
            for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
                for (int columnIndex = 0; columnIndex < NUM_COLUMNS; columnIndex++) {
                    if (facilityIterator.hasNext()) {
                        FacilityView facilityView = new FacilityView(getContext());
                        facilityView.setFacility(facilityIterator.next());
                        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                        layoutParams.height = LayoutParams.WRAP_CONTENT;
                        layoutParams.width = getWidth() / 2;
                        layoutParams.bottomMargin = facilityMarginBottomDp;
                        layoutParams.setGravity(Gravity.CENTER);
                        layoutParams.columnSpec = GridLayout.spec(columnIndex, COLUMN_WEIGHT);
                        layoutParams.rowSpec = GridLayout.spec(rowIndex);
                        facilityView.setLayoutParams(layoutParams);
                        addView(facilityView);
                    }
                }
            }
        }
    }
}
