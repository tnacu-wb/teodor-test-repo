package com.whitbread.premierinn.search.adapter.adapteritem;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;

public interface SearchAdapterItem<T> {

    @LayoutRes
    int getLayoutId();

    void onBind(@NonNull BaseRecyclerViewHolder<T> holder);

    @Override
    boolean equals(Object other);
}
