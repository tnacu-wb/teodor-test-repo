package com.whitbread.premierinn.search.adapter.adapteritem;


import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;

@AutoValue
public abstract class CurrentLocationSearchItemUiModel implements SearchAdapterItem {

    @LayoutRes
    public static final int LAYOUT_ID = R.layout.item_location_search_list;

    public static CurrentLocationSearchItemUiModel create() {
        return new AutoValue_CurrentLocationSearchItemUiModel();
    }

    @Override
    @LayoutRes
    public int getLayoutId() {
        return LAYOUT_ID;
    }

    @Override
    public void onBind(@NonNull BaseRecyclerViewHolder holder) {
        //void
    }
}