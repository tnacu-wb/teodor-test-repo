package com.whitbread.premierinn.hoteldetails.uimodel;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.availability.Facility;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.UiModelListItem;

import java.util.List;

@AutoValue
public abstract class FacilitiesUiModel implements UiModelListItem<FacilitiesUiModel> {

    public static final int LAYOUT_TYPE = R.layout.view_hotel_details_facilities;

    public abstract List<Facility> facilities();

    public abstract int order();

    public abstract String hotelDescription();

    @Override
    public void bind(BaseRecyclerViewHolder<FacilitiesUiModel> viewHolder) {
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
        return new AutoValue_FacilitiesUiModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder order(int order);

        public abstract Builder facilities(List<Facility> facilities);

        public abstract Builder hotelDescription(String description);

        public abstract FacilitiesUiModel build();
    }

}