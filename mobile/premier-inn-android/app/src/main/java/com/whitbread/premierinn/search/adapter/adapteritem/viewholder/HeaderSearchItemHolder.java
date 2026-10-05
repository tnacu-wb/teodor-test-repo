package com.whitbread.premierinn.search.adapter.adapteritem.viewholder;


import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.databinding.ItemSearchHeaderListBinding;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.search.adapter.adapteritem.HeaderUiModel;

public class HeaderSearchItemHolder extends BaseRecyclerViewHolder<HeaderUiModel> {

    private final ItemSearchHeaderListBinding binding;

    public HeaderSearchItemHolder(ItemSearchHeaderListBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull HeaderUiModel model) {
        Context context = binding.tvSearchHeader.getContext();
        String headerText = "";
        switch (model.headerType()) {
            case HOTELS:
                headerText = context.getString(R.string.search_activity_hotel);
                break;
            case PLACES:
                headerText = context.getString(R.string.search_activity_places);
                break;
            case RECENT_SEARCHES:
                headerText = context.getString(R.string.search_activity_recent_searches);
                break;
            case TOP_DESTINATIONS:
                headerText = context.getString(R.string.search_activity_suggestions);
                break;
            default:
                break;
        }
        binding.tvSearchHeader.setText(headerText);
        binding.tvSearchHeaderClear.setVisibility(
                model.headerType() == HeaderUiModel.HeaderType.RECENT_SEARCHES ? View.VISIBLE : View.GONE
        );
    }
}
