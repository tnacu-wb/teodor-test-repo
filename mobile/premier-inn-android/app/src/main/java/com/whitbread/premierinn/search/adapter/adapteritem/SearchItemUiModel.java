package com.whitbread.premierinn.search.adapter.adapteritem;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;


@AutoValue
public abstract class SearchItemUiModel implements SearchAdapterItem<SearchItemUiModel> {

    @LayoutRes
    public static final int LAYOUT_ID = R.layout.item_search_list;

    public abstract String query();

    public abstract SearchSuggetionItem searchItem();

    public static SearchItemUiModel create(@NonNull String query, @NonNull SearchSuggetionItem item) {
        return new AutoValue_SearchItemUiModel(query, item);
    }

    @Override
    @LayoutRes
    public int getLayoutId() {
        return LAYOUT_ID;
    }

    @Override
    public void onBind(@NonNull BaseRecyclerViewHolder<SearchItemUiModel> holder) {
        holder.bind(this);
    }
}
