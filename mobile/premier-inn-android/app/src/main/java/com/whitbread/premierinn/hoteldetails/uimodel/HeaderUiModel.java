package com.whitbread.premierinn.hoteldetails.uimodel;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.UiModelListItem;

@AutoValue
public abstract class HeaderUiModel implements UiModelListItem<HeaderUiModel> {

    public static final int LAYOUT_TYPE = R.layout.view_hotel_details_header;

    @StringRes
    public abstract int headingStringRes();

    @Nullable
    public abstract String heading();

    public abstract int order();

    @Nullable
    public abstract String disclaimer();

    @Override
    public void bind(BaseRecyclerViewHolder<HeaderUiModel> viewHolder) {
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
        return new AutoValue_HeaderUiModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder headingStringRes(int headingStringRes);

        public abstract Builder heading(String heading);

        public abstract Builder order(int order);

        public abstract Builder disclaimer(String disclaimer);

        public abstract HeaderUiModel build();
    }
}