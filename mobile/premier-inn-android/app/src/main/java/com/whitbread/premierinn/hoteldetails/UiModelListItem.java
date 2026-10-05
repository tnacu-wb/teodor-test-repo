package com.whitbread.premierinn.hoteldetails;

import androidx.annotation.LayoutRes;

public interface UiModelListItem<T> {

    void bind(BaseRecyclerViewHolder<T> viewHolder);

    @LayoutRes
    int getLayout();

    int listOrder();
}
