package com.whitbread.premierinn.api.response.availability;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.whitbread.premierinn.api.response.AcceptedCreditCard;
import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.data.hotel.mapper.HotelMappersKt;
import com.whitbread.premierinn.domain.hotel.entity.Hotel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.functions.Predicate;

import static com.whitbread.premierinn.api.response.availability.HotelInfo.HotelRating.ALL_AVAILABLE;
import static com.whitbread.premierinn.api.response.availability.HotelInfo.HotelRating.AVAILABLE_NO_IMAGE;
import static com.whitbread.premierinn.api.response.availability.HotelInfo.HotelRating.AVAILABLE_NO_NUMBER;
import static com.whitbread.premierinn.api.response.availability.HotelInfo.HotelRating.UNAVAILABLE;

@AutoValue
public abstract class HotelInfo implements Parcelable {

    // This is BAD BAD BAD - If any tags needs to be modified or added removed etc we need to update the app. API should handle that
    private static final List<String> TAGS_ROOM;
    private static final List<String> TAGS_RESTAURANT;
    private static final List<String> TAGS_PARKING;

    static {
        TAGS_ROOM = Arrays.asList("accessible-bathroom", "standard-bathroom", "bath", "shower", "double-room", "family-room", "ID2", "ID3",
                "ID4", "pti", "single-room", "bedroom", "twin-room", "hub-accessible-room", "hub-bigger-room", "hub-standard-room",
                "hub-bathroom");
        TAGS_RESTAURANT = Arrays.asList("bar", "beefeater", "brewers-fayre", "coffee-shop", "food", "orange-cow", "restaurant",
                "tgi-fridays", "table-table", "thyme", "whitbread-inn", "breakfast", "family");

        TAGS_PARKING = Collections.singletonList("parking");
    }

    public static final Predicate<Pair<HotelImage, String>> FILTER_ROOM_IMAGES_BY_TAG = pair -> TAGS_ROOM.contains(pair.second);
    public static final Predicate<Pair<HotelImage, String>> FILTER_RESTAURANT_IMAGES_BY_TAG = pair -> TAGS_RESTAURANT.contains(pair.second);
    public static final Predicate<Pair<HotelImage, String>> FILTER_PARKINGIMAGES_BY_TAG = pair -> TAGS_PARKING.contains(pair.second);

    @NonNull
    public static TypeAdapter<HotelInfo> typeAdapter(Gson gson) {
        return new AutoValue_HotelInfo.GsonTypeAdapter(gson);
    }

    @SerializedName("code")
    public abstract String code();

    @SerializedName("name")
    public abstract String name();

    @SerializedName("facilities")
    @Nullable
    public abstract List<Facility> facilities();

    @SerializedName("tripAdvisor")
    @Nullable
    public abstract TripAdvisor tripAdvisor();

    @SerializedName("images")
    @Nullable
    public abstract List<HotelImage> images();

    @SerializedName("map")
    public abstract Coordinates map();

    @SerializedName("messagingFlag")
    @Nullable
    public abstract MessagingFlag messagingFlag();

    @SerializedName("address")
    public abstract AvailabilityAddress address();

    @SerializedName("hotelDescription")
    @Nullable
    public abstract String hotelDescription();

    @SerializedName("hotelDirections")
    @Nullable
    public abstract String hotelDirections();

    @SerializedName("parkingDescription")
    @Nullable
    public abstract String parkingDescription();

    @SerializedName("contactDetails")
    @Nullable
    public abstract ContactDetails contactDetails();

    @SerializedName("acceptedCreditCards")
    @Nullable
    public abstract List<AcceptedCreditCard> acceptedCreditCards();

    @SerializedName("restaurant")
    @Nullable
    public abstract Restaurant restaurant();

    @SerializedName("announcement")
    @Nullable
    public abstract Announcement announcement();

    @SerializedName("authenticationRequired")
    @Nullable
    public abstract Boolean isAuthenticationRequired();

    public abstract String brand();

    public Hotel.Brand hotelBrand() {
        return HotelMappersKt.mapToBrand(brand());
    }

    public boolean isMessagingAvailable() {
        return messagingFlag() != null && messagingFlag().flagText() != null;
    }

    public boolean hasRestaurant() {
        return restaurant() != null && !StringUtils.isBlank(restaurant().getName());
    }

    @Nullable
    public String firstImageFileReference() {
        return images() != null && images().get(0) != null ? images().get(0).fileReference() : null;
    }

    public enum HotelRating {
        UNAVAILABLE,
        ALL_AVAILABLE,
        AVAILABLE_NO_IMAGE,
        AVAILABLE_NO_NUMBER
    }

    public HotelRating getRatingState() {
        if (tripAdvisor() == null || !tripAdvisor().hasData()) {
            return UNAVAILABLE;
        } else if (tripAdvisor().ratingImageUrl() == null) {
            return AVAILABLE_NO_IMAGE;
        } else if (tripAdvisor().sampleSize() == null) {
            return AVAILABLE_NO_NUMBER;
        } else {
            return ALL_AVAILABLE;
        }
    }

    @Nullable
    public Facility parkingFacility() {
        for (Facility facility : facilities()) {
            if (isParkingFacility(facility)) {
                return facility;
            }
        }
        return null;
    }

    private boolean isParkingFacility(@NonNull Facility facility) {
        return (Facility.Codes.FREE_PARKING.equals(facility.code())
                || Facility.Codes.CHARGEABLE_PARKING.equals(facility.code())
                || Facility.Codes.CHARGEABLE_ONSITE_PARKING.equals(facility.code()));
    }

    public abstract HotelInfo.Builder toBuilder();

    public static HotelInfo.Builder builder() {
        return new AutoValue_HotelInfo.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder code(String code);
        public abstract Builder name(String name);
        public abstract Builder facilities(List<Facility> facilities);
        public abstract Builder tripAdvisor(TripAdvisor tripAdvisor);
        public abstract Builder images(List<HotelImage> images);
        public abstract Builder map(Coordinates coordinates);
        public abstract Builder messagingFlag(MessagingFlag messagingFlag);
        public abstract Builder address(AvailabilityAddress address);
        public abstract Builder hotelDescription(String hotelDescription);
        public abstract Builder hotelDirections(String hotelDirections);
        public abstract Builder parkingDescription(String parkingDescription);
        public abstract Builder contactDetails(ContactDetails contactDetails);
        public abstract Builder acceptedCreditCards(List<AcceptedCreditCard> cards);
        public abstract Builder restaurant(Restaurant restaurant);
        public abstract Builder brand(String brand);
        public abstract Builder announcement(Announcement announcement);
        public abstract Builder isAuthenticationRequired(Boolean authenticationRequired);
        public abstract HotelInfo build();
    }
}
