package com.whitbread.premierinn.hoteldetails.uimodel;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.UiModelListItem;

import java.util.List;

@AutoValue
public abstract class RoomRatesUiModel implements UiModelListItem<RoomRatesUiModel> {

    public static final int LAYOUT_TYPE = R.layout.view_hotel_details_rate_plan;

    @Nullable
    public abstract List<List<RateBoxUiModel>> roomRates();

    public abstract int order();

    public abstract boolean showCityTaxMessage();

    @Nullable
    public abstract String invalidDiscountCodeMessage();

    @Override
    public void bind(BaseRecyclerViewHolder<RoomRatesUiModel> viewHolder) {
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

    public static RoomRatesUiModel.Builder builder() {
        return new AutoValue_RoomRatesUiModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder order(int order);
        public abstract Builder roomRates(List<List<RateBoxUiModel>> roomRates);
        public abstract Builder showCityTaxMessage(boolean show);
        public abstract Builder invalidDiscountCodeMessage(@Nullable String message);
        public abstract RoomRatesUiModel build();
    }
}
