package com.whitbread.premierinn.hoteldetails.uimodel;

import androidx.annotation.StringRes;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.UiModelListItem;

import java.util.List;

@AutoValue
public abstract class RestaurantBarUiModel implements UiModelListItem<RestaurantBarUiModel> {

    public static final int LAYOUT_TYPE = R.layout.view_hotel_details_restaurant_bar;

    public abstract List<String> imageUrls();

    @StringRes
    public abstract int headingStringRes();

    public abstract int order();

    public abstract String disclaimer();

    public abstract Type type();

    @Override
    public void bind(BaseRecyclerViewHolder<RestaurantBarUiModel> viewHolder) {
        viewHolder.bind(this);
    }

    @Override
    public int getLayout() {
        return LAYOUT_TYPE;
    }

    @Override
    public int listOrder() {
        return order();
    }

    public static Builder builder() {
        return new AutoValue_RestaurantBarUiModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder imageUrls(List<String> imageUrls);

        public abstract Builder headingStringRes(int headingStringRes);

        public abstract Builder order(int order);

        public abstract Builder type(Type type);

        public abstract Builder disclaimer(String disclaimer);

        public abstract RestaurantBarUiModel build();
    }

    public enum Type {
        RESTAURANT
    }
}