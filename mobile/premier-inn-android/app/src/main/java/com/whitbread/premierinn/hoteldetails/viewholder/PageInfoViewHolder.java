package com.whitbread.premierinn.hoteldetails.viewholder;

import static com.whitbread.premierinn.hoteldetails.SpannableContentFactory.createContent;

import android.view.Gravity;
import android.view.View;
import com.whitbread.premierinn.common.GlideApp;
import com.whitbread.premierinn.databinding.ViewHotelDetailsTextviewImageContentBinding;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.uimodel.PageInfoUiModel;

class PageInfoViewHolder extends BaseRecyclerViewHolder<PageInfoUiModel> {
    public final ViewHotelDetailsTextviewImageContentBinding binding;

    PageInfoViewHolder(ViewHotelDetailsTextviewImageContentBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(PageInfoUiModel item) {
        if (PageInfoUiModel.Type.CENTER_ALIGN.equals(item.textAlignment())) {
            binding.spannableTextContainer.setGravity(Gravity.CENTER);
        }
        binding.spannableTextContainer.setText(createContent(binding.getRoot().getContext(), item.contents()));

        if (item.imageUrl() != null) {
            binding.restaurantImage.setVisibility(View.VISIBLE);
            GlideApp.with(binding.restaurantImage)
                    .asBitmap()
                    .centerInside()
                    .load(item.imageUrl())
                    .into(binding.restaurantImage);
        }
    }
}