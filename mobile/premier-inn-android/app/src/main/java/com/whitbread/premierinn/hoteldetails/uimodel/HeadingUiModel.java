package com.whitbread.premierinn.hoteldetails.uimodel;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.availability.Facility;
import com.whitbread.premierinn.api.response.availability.HotelInfo;
import com.whitbread.premierinn.api.response.availability.TripAdvisor;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.UiModelListItem;

import java.util.List;

@AutoValue
public abstract class HeadingUiModel implements UiModelListItem<HeadingUiModel> {

    public static final int LAYOUT_TYPE = R.layout.view_hotel_details_heading;

    public abstract List<Facility> facilities();

    public abstract boolean isPremierPlus();

    public abstract String extraMessage();

    public abstract int order();

    public abstract float distance();

    public abstract String address();

    public abstract boolean limitedAvailability();

    @Nullable
    public abstract TripAdvisor tripAdvisor();

    @Nullable
    public abstract HotelInfo.HotelRating tripAdvisorRating();

    @Override
    public void bind(BaseRecyclerViewHolder<HeadingUiModel> viewHolder) {
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
        return new AutoValue_HeadingUiModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder facilities(List<Facility> facilities);

        public abstract Builder distance(float distance);

        public abstract Builder address(String address);

        public abstract Builder tripAdvisor(TripAdvisor tripAdvisor);

        public abstract Builder tripAdvisorRating(HotelInfo.HotelRating tripAdvisorRating);

        public abstract Builder extraMessage(String message);

        public abstract Builder isPremierPlus(boolean isPremierPlus);

        public abstract Builder limitedAvailability(boolean limitedAvailability);

        public abstract Builder order(int order);

        public abstract HeadingUiModel build();
    }
}