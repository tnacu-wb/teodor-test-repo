package com.whitbread.premierinn.api.response.search;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.whitbread.premierinn.api.response.availability.Coordinates;
import com.whitbread.premierinn.common.utils.StringUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@AutoValue
public abstract class SearchItemInput implements Parcelable {

    private static final String HUB_BRAND_LEGEND = "HUB";
    private static final String ZIP_BRAND_LEGEND = "ZIP";
    private static final String PID_BRAND_LEGEND = "PID";

    public boolean isHotel() {
        return !StringUtils.isBlank(hotelId()) && !StringUtils.isBlank(brand());
    }

    public SearchType searchType() {
        SearchType searchType;
        if (isHotel()) {
            if (HUB_BRAND_LEGEND.equals(brand())) {
                searchType = SearchType.HUB_HOTEL;
            } else if (ZIP_BRAND_LEGEND.equals(brand())) {
                searchType = SearchType.ZIP_HOTEL;
            } else if (PID_BRAND_LEGEND.equals(brand())) {
                searchType = SearchType.PID_HOTEL;
            } else {
                searchType = SearchType.PI_HOTEL;
            }
        } else {
            searchType = SearchType.DESTINATION;
        }
        return searchType;
    }

    public abstract String searchText();

    public abstract Coordinates location();

    @Nullable
    public abstract String hotelId(); //or google placeId

    @Nullable
    public abstract String brand();

    public enum SearchType {
        PI_HOTEL,
        DESTINATION,
        HUB_HOTEL,
        ZIP_HOTEL,
        PID_HOTEL
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder searchText(@NonNull String searchText);

        public abstract Builder location(@NonNull Coordinates location);

        public abstract Builder hotelId(@NonNull String hotelId);

        public abstract Builder brand(@Nullable String brand);

        public abstract SearchItemInput build();
    }

    public static Builder builder() {
        return new AutoValue_SearchItemInput.Builder();
    }

    public abstract Builder toBuilder();

    public static TypeAdapter<SearchItemInput> typeAdapter(Gson gson) {
        return new AutoValue_SearchItemInput.GsonTypeAdapter(gson);
    }

}
