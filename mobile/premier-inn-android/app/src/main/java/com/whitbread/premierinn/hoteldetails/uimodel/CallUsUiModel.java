package com.whitbread.premierinn.hoteldetails.uimodel;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.UiModelListItem;

@AutoValue
public abstract class CallUsUiModel implements UiModelListItem<CallUsUiModel> {

    public static final int LAYOUT_TYPE = R.layout.view_hotel_details_call_us;

    public abstract String telCostInfo();

    public abstract String telNumber();

    public abstract String label();

    public abstract int order();

    @Override
    public void bind(BaseRecyclerViewHolder<CallUsUiModel> viewHolder) {
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
        return new AutoValue_CallUsUiModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder order(int order);

        public abstract Builder telCostInfo(String telCostInfo);

        public abstract Builder telNumber(String telNumber);

        public abstract Builder label(String value);

        public abstract CallUsUiModel build();
    }


}
