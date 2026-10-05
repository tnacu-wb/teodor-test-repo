package com.whitbread.premierinn.hoteldetails.uimodel;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.UiModelListItem;

@AutoValue
public abstract class ImportantInfoUiModel implements UiModelListItem<ImportantInfoUiModel> {

    public static final int LAYOUT_TYPE = R.layout.view_hotel_details_important_info_button;

    public abstract int order();
    public abstract int count();

    @Override
    public void bind(BaseRecyclerViewHolder<ImportantInfoUiModel> viewHolder) {
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
        return new AutoValue_ImportantInfoUiModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder order(int order);
        public abstract Builder count(int count);

        public abstract ImportantInfoUiModel build();
    }

}