package com.whitbread.premierinn.hoteldetails;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SelectedHotel implements Parcelable { // Move to HDP
    public abstract String code();
    public abstract String name();
    public abstract String imageReference();
    public abstract boolean isHub();
    @Nullable
    public abstract String address();
    @Nullable
    public abstract AccessibilityFacilities accessibilityFacilities();

    public static SelectedHotel.Builder builder() {
        return new AutoValue_SelectedHotel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder code(String code);
        public abstract Builder name(String name);
        public abstract Builder imageReference(String imageRef);
        public abstract Builder address(String address);
        public abstract Builder accessibilityFacilities(AccessibilityFacilities accessibilityFacilities);
        public abstract Builder isHub(boolean isHub);
        public abstract SelectedHotel build();
    }
}
