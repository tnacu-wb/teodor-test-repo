package com.whitbread.premierinn.hoteldetails.uimodel;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.availability.Coordinates;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelCoordinates;
import com.whitbread.premierinn.domain.hotel.entity.Hotel;
import com.whitbread.premierinn.hoteldetails.BaseRecyclerViewHolder;
import com.whitbread.premierinn.hoteldetails.UiModelListItem;



@AutoValue
public abstract class MapUiModel implements UiModelListItem<MapUiModel> {

    public static final int LAYOUT_TYPE = R.layout.view_hotel_details_map_component;

    public abstract int order();

    @Nullable
    public abstract Coordinates hotelLocation();

    @Nullable
    public abstract String hotelAddress();

    @Nullable
    public abstract HotelCoordinates hotelLocationGQ();

    public abstract Hotel.Brand hotelBrand();

    @Nullable
    public abstract String searchedLocationTerm();

    @Nullable
    public abstract Coordinates searchedLocation();

    public abstract float distance();

    public abstract String shortDirections();

    @Override
    public void bind(BaseRecyclerViewHolder<MapUiModel> viewHolder) {
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
        return new AutoValue_MapUiModel.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder order(int order);

        public abstract Builder hotelLocation(Coordinates hotelLocation);

        public abstract Builder hotelAddress(String hotelAddress);

        public abstract Builder hotelLocationGQ(HotelCoordinates hotelCoordinates);

        public abstract Builder searchedLocation(Coordinates searchedLocation);

        public abstract Builder searchedLocationTerm(String searchedLocationTerm);

        public abstract Builder shortDirections(String shortDirections);

        public abstract Builder distance(float distance);

        public abstract Builder hotelBrand(Hotel.Brand hotelBrand);

        public abstract MapUiModel build();
    }
}
