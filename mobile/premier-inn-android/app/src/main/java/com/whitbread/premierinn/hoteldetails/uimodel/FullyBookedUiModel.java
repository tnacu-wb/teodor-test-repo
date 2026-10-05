package com.whitbread.premierinn.hoteldetails.uimodel;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.UiModelListItem;

@AutoValue
public abstract class FullyBookedUiModel implements UiModelListItem<FullyBookedUiModel> {

    public static final int LAYOUT_TYPE = R.layout.view_hotel_details_fully_booked;

    public abstract int order();

    public abstract Type type();

    @Override
    public void bind(BaseRecyclerViewHolder<FullyBookedUiModel> viewHolder) {
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

    public static FullyBookedUiModel create(int order, Type type) {
        return new AutoValue_FullyBookedUiModel(order, type);
    }

    public enum Type {
        FULLY_BOOKED, CHECK_AVAILABILITY
    }
}