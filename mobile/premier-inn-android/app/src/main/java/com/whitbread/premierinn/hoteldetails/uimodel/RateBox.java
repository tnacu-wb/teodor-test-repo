package com.whitbread.premierinn.hoteldetails.uimodel;

import androidx.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.UiModelListItem;

@AutoValue
public abstract class RateBox implements UiModelListItem<RateBox> {

    public static final int LAYOUT_TYPE = R.layout.view_hotel_details_rate_plan;

    @Nullable
    public abstract Type type();

    @Nullable
    public abstract String title();

    @Nullable
    public abstract String description();

    @Nullable
    public abstract String price();

    public abstract int order();

    public abstract State state();

    public abstract boolean isAlternativeRoom();

    @Override
    public void bind(BaseRecyclerViewHolder<RateBox> viewHolder) {
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
        return new AutoValue_RateBox.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder order(int order);

        public abstract Builder type(Type type);

        public abstract Builder title(String title);

        public abstract Builder description(String description);

        public abstract Builder price(String price);

        public abstract Builder state(State state);

        public abstract Builder isAlternativeRoom(boolean isAlternativeRoom);

        public abstract RateBox build();
    }

    public enum Type {
        FLEX, SAVER
    }

    public enum State {
        LOADING, READY
    }
}