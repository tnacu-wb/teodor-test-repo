package com.whitbread.premierinn.search.adapter.adapteritem.viewholder;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.databinding.ItemSearchListBinding;
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.search.adapter.adapteritem.SearchItemUiModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import static android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;
import static com.whitbread.premierinn.common.utils.StringUtils.isBlank;

public class SearchItemViewHolder extends BaseRecyclerViewHolder<SearchItemUiModel> {
    private final ItemSearchListBinding binding;
    Context context;

    public SearchItemViewHolder(ItemSearchListBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        this.context = binding.getRoot().getContext();
    }

    @Override
    public void bind(SearchItemUiModel model) {
        binding.recentSearchIcon.setImageDrawable(ContextCompat.getDrawable(context, getSearchIconDrawable(model.searchItem().getType())));
        if (showHotelLabel(model.searchItem().getType())) {
            binding.hotelLabel.setVisibility(View.VISIBLE);
        } else {
            binding.hotelLabel.setVisibility(View.GONE);
        }
        setSearchItemNameAndHighlightQueryTextIfMatched(model.searchItem().getName(), model.query());
    }

    private boolean showHotelLabel(@NonNull SearchSuggetionItem.Type type) {
        switch (type) {
            case PI_HOTEL:
            case PI_GERMAN_HOTEL:
            case HUB_HOTEL:
            case ZIP_HOTEL:
                return true;
            default:
                return false;
        }
    }

    private int getSearchIconDrawable(@NonNull SearchSuggetionItem.Type type) {
        switch (type) {
            case PI_HOTEL:
            case PI_GERMAN_HOTEL:
                return R.drawable.ic_hotel_purple;
            case LOCATION:
            case GOOGLE_PLACE:
                return R.drawable.ic_destination;
            case HUB_HOTEL:
                return R.drawable.ic_hub_hotel;
            case ZIP_HOTEL:
                return R.drawable.ic_zip_logo;
            default:
                return R.drawable.ic_search;
        }
    }

    private void setSearchItemNameAndHighlightQueryTextIfMatched(@NonNull String searchItemName, @Nullable String query) {
        SpannableString spannableString = new SpannableString(searchItemName);
        if (!isBlank(query)) {
            binding.placeName.setTypeface(ResourcesCompat.getFont(context, R.font.proxima_nova_regular));
            int startIndex = searchItemName.toLowerCase().indexOf(query.toLowerCase());
            int endIndex = startIndex + query.length();
            if (startIndex != -1) {
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            binding.placeName.setTypeface(ResourcesCompat.getFont(context, R.font.proxima_nova_semibold));
        }
        binding.placeName.setText(spannableString);
    }
}