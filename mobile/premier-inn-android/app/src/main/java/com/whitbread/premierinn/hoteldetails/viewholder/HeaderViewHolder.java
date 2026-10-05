package com.whitbread.premierinn.hoteldetails.viewholder;

import android.content.res.Resources;
import android.view.View;

import com.whitbread.premierinn.databinding.ViewHotelDetailsHeaderBinding;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.uimodel.HeaderUiModel;


class HeaderViewHolder extends BaseRecyclerViewHolder<HeaderUiModel> {
    private final ViewHotelDetailsHeaderBinding binding;

    HeaderViewHolder(ViewHotelDetailsHeaderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(HeaderUiModel item) {
        Resources resources = binding.getRoot().getContext().getResources();
        String text = item.heading() != null ? item.heading() : resources.getString(item.headingStringRes());
        binding.hotelDetailsHeading.setText(text);

        if (item.disclaimer() != null && !item.disclaimer().isEmpty()) {
            binding.disclaimerContainer.setVisibility(View.VISIBLE);
            binding.disclaimerText.setText(item.disclaimer());
        }
    }
}