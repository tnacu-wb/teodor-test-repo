package com.whitbread.premierinn.hoteldetails.uimodel;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.UiModelListItem;

@AutoValue
public abstract class DiscountCodeUiModel implements UiModelListItem<DiscountCodeUiModel> {

    public static final int LAYOUT_TYPE = R.layout.view_hotel_details_discount_code_compose;

    public abstract int order();

    public abstract String appliedDiscountCode();

    public abstract boolean visible();

    @Override
    public void bind(BaseRecyclerViewHolder<DiscountCodeUiModel> viewHolder) {
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
        return new AutoValue_DiscountCodeUiModel.Builder()
                .appliedDiscountCode("")
                .visible(false);
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder order(int order);

        public abstract Builder appliedDiscountCode(String code);

        public abstract Builder visible(boolean visible);

        public abstract DiscountCodeUiModel build();
    }
}