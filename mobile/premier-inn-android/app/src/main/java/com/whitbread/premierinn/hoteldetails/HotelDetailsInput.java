package com.whitbread.premierinn.hoteldetails;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.Urls;
import com.whitbread.premierinn.common.analytics.CampaignDataModel;
import com.whitbread.premierinn.landing.ParcelableMappersKt;
import com.whitbread.premierinn.landing.SearchPayload;
import com.whitbread.premierinn.searchresults.HotelListItem;
import com.whitbread.premierinn.searchresults.SearchResultsInput;

@AutoValue
public abstract class HotelDetailsInput implements Parcelable {

    public abstract String hotelCode();

    @Nullable
    public abstract String hotelName();

    @Nullable
    public abstract String hotelImageUrl();

    public abstract float distanceFromSearchedLocation();

    public abstract String brand();

    @Nullable
    public abstract SearchResultsInput searchResultsInput();
    @Nullable
    public abstract CampaignDataModel campaignModel();

    @Nullable
    public abstract String trackingCode();

    public abstract boolean cameFromMapView();

    public static Builder fromSearchResults(HotelListItem listItem, SearchResultsInput input, boolean cameFromMapView) {
        return builder()
                .distanceFromSearchedLocation((float) listItem.getDistance())
                .searchResultsInput(input)
                .hotelName(listItem.getName())
                .hotelImageUrl(Urls.CONTENT_BASE_URL + listItem.getImagePath())
                .cameFromMapView(cameFromMapView)
                .brand(listItem.getBrand().toString())
                .hotelCode(listItem.getCode());
    }

    public static Builder fromNotifications(String hotelCode, String brand, String trackingCode, SearchResultsInput input) {
        return builder()
                .hotelCode(hotelCode)
                .brand(brand)
                .searchResultsInput(input)
                .trackingCode(trackingCode)
                .distanceFromSearchedLocation(0f)
                .cameFromMapView(false);
    }

    public static Builder fromBookingDetails(String hotelCode, String hotelBrand) {
        return searchResultsHotelCodeFromBookingDetails(hotelCode, hotelBrand);
    }

    public static Builder searchResultsHotelCodeFromBookingDetails(String hotelCode, String hotelBrand) {
        return builder()
                .hotelCode(hotelCode)
                .brand(hotelBrand)
                .cameFromMapView(false)
                .distanceFromSearchedLocation(0f);
    }

    public static Builder fromFrequentBooking(String hotelCode) {
        return searchResultsHotelCode(hotelCode);
    }

    public static Builder searchResultsHotelCode(String hotelCode) {
        return builder()
                .hotelCode(hotelCode)
                .brand("")
                .cameFromMapView(false)
                .distanceFromSearchedLocation(0f);
    }

    public static Builder fromFrequentBookingCalendar(SearchPayload payload, String hotelCode) {
        return builder()
                .searchResultsInput(ParcelableMappersKt.toSearchResultsInput(payload))
                .hotelName(payload.getPlaceName())
                .distanceFromSearchedLocation(0f)
                .brand("")
                .cameFromMapView(false)
                .hotelCode(hotelCode);
    }

    public static Builder fromSearchPayload(SearchPayload payload, String hotelCode) {
        return builder()
                .searchResultsInput(ParcelableMappersKt.toSearchResultsInput(payload))
                .hotelName(payload.getPlaceName())
                .distanceFromSearchedLocation(0f)
                .brand("")
                .cameFromMapView(false)
                .hotelCode(hotelCode);
    }

    public static Builder builder() {
        return new AutoValue_HotelDetailsInput.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder hotelCode(String hotelCode);

        public abstract Builder hotelName(String name);

        public abstract Builder hotelImageUrl(String imageUrl);

        public abstract Builder distanceFromSearchedLocation(float distance);

        public abstract Builder brand(String brand);

        public abstract Builder searchResultsInput(SearchResultsInput searchResultsInput);

        public abstract Builder cameFromMapView(boolean b);

        public abstract Builder campaignModel(CampaignDataModel campaignModel);

        public abstract Builder trackingCode(String trackingCode);

        public abstract HotelDetailsInput build();
    }
}
