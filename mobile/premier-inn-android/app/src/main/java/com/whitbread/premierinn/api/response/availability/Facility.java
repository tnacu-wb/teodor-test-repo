package com.whitbread.premierinn.api.response.availability;

import android.os.Parcelable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.whitbread.premierinn.R;

@AutoValue
public abstract class Facility implements Parcelable {

    @NonNull
    public static TypeAdapter<Facility> typeAdapter(Gson gson) {
        return new AutoValue_Facility.GsonTypeAdapter(gson);
    }

    @SerializedName("code")
    @Nullable
    public abstract Codes code();

    @SerializedName("legend")
    @Nullable
    public abstract String legend();

    @SerializedName("icon")
    @Nullable
    public abstract String icon();

    public static Builder builder() {
        return new AutoValue_Facility.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder code(Codes codes);

        public abstract Builder legend(String legend);

        public abstract Builder icon(String icon);

        public abstract Facility build();

    }

    @SuppressWarnings("unused")
    public enum Codes {
        @SerializedName("CPF")FREE_PARKING(R.drawable.ic_free_parking, "CPF"),
        @SerializedName("COP")CHARGEABLE_PARKING(R.drawable.ic_chargeable_parking, "COP"),
        @SerializedName("COC")CHARGEABLE_OFFSITE_PARKING(R.drawable.ic_chargeable_parking, "COC"),
        @SerializedName("CPP")CHARGEABLE_ONSITE_PARKING(R.drawable.ic_chargeable_parking, "CPP"),
        @SerializedName("WIA")FREE_WIFI(R.drawable.ic_wifi, "WIA"),
        @SerializedName("WIA")HUB_FREE_WIFI(R.drawable.ic_wifi, "WIA"),
        @SerializedName("DIS")ACCESSIBLE_ROOM(R.drawable.ic_accessibility, "DIS"),
        @SerializedName("HAR")HUB_ACCESSIBLE_ROOM(R.drawable.ic_accessibility, "HAR"),
        @SerializedName("PRR")PREMIER_PLUS_ROOM(-1, false, "PRR"),
        @SerializedName("STE")BUSINESS_ROOM(-1, false, "STE"),
        @SerializedName("LFT")LIFT(R.drawable.ic_lift, "LFT"),
        @SerializedName("HUL")HUB_LIFT(R.drawable.ic_lift, "HUL"),
        @SerializedName("FAM")FAMILY(R.drawable.ic_family, "FAM"),
        @SerializedName("ACO")AIR_CONDITIONING(R.drawable.ic_aircon, "ACO"),
        @SerializedName("HAC")HUB_AIR_CONDITIONING(R.drawable.ic_aircon, "HAC"),
        @SerializedName("RES")RESTAURANT(R.drawable.ic_restaurant, "RES"),
        @SerializedName("HRS")HUB_RESTAURANT(R.drawable.ic_restaurant, "HRS"),
        @SerializedName("LUG")LUGGAGE_STORAGE(R.drawable.ic_luggage_storage, "LUG"),
        @SerializedName("PAF")SLEEP_PARK_FLY(R.drawable.ic_sleep_park_fly, "PAF"),
        @SerializedName("MEE")MEETING_ROOM(R.drawable.ic_meeting_room, "MEE"),
        @SerializedName("IRA")IN_ROOM_APP(R.drawable.ic_room_controls, "IRA"),
        @SerializedName("COS")COSTA(R.drawable.ic_costa, "COS"),
        @SerializedName("WET")WET_ROOMS_AVAILABLE(-1, false, "WET"),
        @SerializedName("LWB")LOWERED_BATHS_AVAILABLE(-1, false, "LWB"),
        @SerializedName("DIN")RESTAURANT_DIN(R.drawable.ic_restaurant, "DIN"),
        @SerializedName("SMA")SMART_40_TV(R.drawable.ic_smart_40_tv, "SMA"),
        @SerializedName("HUW")HUB_WIFI(R.drawable.ic_wifi, "HUW"),
        @SerializedName("HLG")LUGGAGE_FACILITY(R.drawable.ic_luggage_facilities, "HLG");
        private final int drawableId;
        private final boolean displayable;
        private final String code;

        Codes(@DrawableRes int drawableId, String code) {
            this(drawableId, true, code);
        }

        Codes(@DrawableRes int drawableId, boolean displayable, String code) {
            this.drawableId = drawableId;
            this.displayable = displayable;
            this.code = code;
        }

        public static Codes valueOfCode(String code) {
            for (Codes e : values()) {
                if (e.code.equals(code)) {
                    return e;
                }
            }
            return null;
        }
        @DrawableRes
        public int getDrawable() {
            return drawableId;
        }

        public boolean isDisplayable() {
            return displayable;
        }

        }
}
