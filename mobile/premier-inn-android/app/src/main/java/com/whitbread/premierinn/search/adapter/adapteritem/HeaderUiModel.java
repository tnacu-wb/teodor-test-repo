package com.whitbread.premierinn.search.adapter.adapteritem;


import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;

@AutoValue
public abstract class HeaderUiModel implements SearchAdapterItem<HeaderUiModel> {

    public abstract HeaderType headerType();

    @LayoutRes
    public static final int LAYOUT_ID = R.layout.item_search_header_list;

    public enum HeaderType {
        HOTELS, PLACES, TOP_DESTINATIONS, RECENT_SEARCHES
    }

    public static HeaderUiModel create(HeaderUiModel.HeaderType headerType) {
        return new AutoValue_HeaderUiModel(headerType);
    }

    @LayoutRes
    @Override
    public int getLayoutId() {
        return LAYOUT_ID;
    }

    @Override
    public void onBind(@NonNull BaseRecyclerViewHolder<HeaderUiModel> holder) {
        holder.bind(this);
    }
}
