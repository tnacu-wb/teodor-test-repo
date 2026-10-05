package com.whitbread.premierinn.searchresults;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.analytics.CampaignDataModel;
import com.whitbread.premierinn.domain.common.RoomType;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.whitbread.premierinn.common.utils.StringUtils.toCommaSeparatedString;

import androidx.annotation.Nullable;

@AutoValue
public abstract class SearchResultsInput implements Parcelable {

    public abstract String placeName();

    public abstract float latitude();

    public abstract float longitude();

    public abstract LocalDate arrivalDate();

    public abstract LocalDate departureDate();

    public abstract int numRooms();

    public abstract List<Integer> adults();

    public abstract List<Integer> children();

    public abstract List<Integer> infants();

    public abstract List<Boolean> cots();

    public abstract List<String> roomTypeCodes();

    @Nullable
    public abstract CampaignDataModel campaignModel();

    @Nullable
    public abstract String trackingCode();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_SearchResultsInput.Builder();
    }

    public static Builder builderWithDefaults() {
        return new AutoValue_SearchResultsInput.Builder()
                .numRooms(1)
                .adults(new ArrayList<>(Collections.singletonList(1)))
                .children(new ArrayList<>(Collections.singletonList(0)))
                .infants(new ArrayList<>(Collections.singleton(0)))
                .cots(new ArrayList<>(Collections.singletonList(false)))
                .roomTypeCodes(new ArrayList<>(Collections.singletonList(RoomType.DOUBLE.getCode())));
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder placeName(String placeName);

        public abstract Builder latitude(float latitude);

        public abstract Builder longitude(float longitude);

        public abstract Builder arrivalDate(LocalDate arrivalDate);

        public abstract Builder departureDate(LocalDate departureDate);

        public abstract Builder numRooms(int numRooms);

        public abstract Builder adults(List<Integer> adults);

        public abstract Builder children(List<Integer> children);

        public abstract Builder infants(List<Integer> infants);

        public abstract Builder cots(List<Boolean> cots);

        public abstract Builder roomTypeCodes(List<String> roomTypeCodes);

        public abstract Builder campaignModel(CampaignDataModel campaignModel);

        public abstract Builder trackingCode(String trackingCode);

        public abstract SearchResultsInput build();
    }

    // Helper Methods
    public int totalNumOfGuests() {
        int numGuests = 0;
        for (int guest : adults()) {
            numGuests += guest;
        }
        for (int guest : children()) {
            numGuests += guest;
        }
        return numGuests;
    }

    public String adultsCommaSeparatedList() {
        return toCommaSeparatedString(adults());
    }

    public String childrenCommaSeparatedList() {
        return toCommaSeparatedString(children());
    }

    public String cotsCommaSeparatedList() {
        return toCommaSeparatedString(cots());
    }

    public String roomTypeCodeCommaSeparatedList() {
        return toCommaSeparatedString(roomTypeCodes());
    }

    public int nights() {
        return (int) ChronoUnit.DAYS.between(arrivalDate(), departureDate());
    }

    public int numAdults() {
        int adults = 0;
        for (int adultsPerRoom : adults()) {
            adults += adultsPerRoom;
        }
        return adults;
    }

    public int numChildren() {
        int children = 0;
        for (int chldrenPerRoom : children()) {
            children += chldrenPerRoom;
        }
        return children;
    }

    public boolean hasAccessibleRoom() {
        return roomTypeCodes() != null && roomTypeCodes().contains(RoomType.ACCESSIBLE.getCode());
    }

    public boolean hasTwinRoom() {
        return roomTypeCodes() != null && roomTypeCodes().contains(RoomType.TWIN.getCode());
    }
}

