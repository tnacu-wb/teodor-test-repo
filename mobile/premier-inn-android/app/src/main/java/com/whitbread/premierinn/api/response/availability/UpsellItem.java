package com.whitbread.premierinn.api.response.availability;

import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.whitbread.premierinn.api.response.booking.BookingPrice;

import java.util.List;

import static com.whitbread.premierinn.api.response.availability.File.ALLERGY_INFO_LABEL;

@AutoValue
public abstract class UpsellItem implements Parcelable {

    @NonNull
    public static TypeAdapter<UpsellItem> typeAdapter(Gson gson) {
        return new AutoValue_UpsellItem.GsonTypeAdapter(gson);
    }

    @SerializedName("code")
    public abstract String code();

    @SerializedName("legend")
    public abstract String legend();

    @SerializedName("price")
    public abstract BookingPrice price();

    @SerializedName("foodUpsell")
    public abstract boolean foodUpsell();

    @SerializedName("availableForChildren")
    public abstract boolean availableForChildren();

    @SerializedName("freeBreakfastOption")
    public abstract boolean freeBreakfastOption();

    @SerializedName("freeBreakfastTrigger")
    public abstract boolean freeBreakfastTrigger();

    @SerializedName("freeBreakfastCode")
    @Nullable
    public abstract String freeBreakfastCode();

    // Note : contains html
    @SerializedName("description")
    @Nullable
    public abstract String description();

    // Note : contains html
    @SerializedName("shortDescription")
    @Nullable
    public abstract String shortDescription();

    @SerializedName("files")
    @Nullable
    public abstract List<File> files();

    /**
     * Calculates the total cost for this upsell for the stay
     * Assumes this upsell has a cost per night.
     * @param adults
     * @param nights
     * @return The total price of breakfast for the stay
     */
    public float calculateTotalUpsellCostForStay(int adults, int nights) {
        if (price().getAmount() > 0) {
            return nights * adults * price().getAmount();
        } else {
            return 0;
        }
    }
    public boolean isFreeForChildren() {
        return !(freeBreakfastCode() == null) && !freeBreakfastCode().isEmpty();
    }

    public String allergyInfoPath() {
        if (files() != null) {
            for (File file : files()) {
                if (file.label().equalsIgnoreCase(ALLERGY_INFO_LABEL)) {
                    return file.path();
                }
            }
        }
        return null;
    }

    public static Builder builder() {
        return new AutoValue_UpsellItem.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder code(String code);

        public abstract Builder legend(String legend);

        public abstract Builder price(BookingPrice price);

        public abstract Builder foodUpsell(boolean foodUpsell);

        public abstract Builder availableForChildren(boolean availableForChildren);

        public abstract Builder freeBreakfastOption(boolean freeBreakfastOption);

        public abstract Builder freeBreakfastTrigger(boolean freeBreakfastTigger);

        public abstract Builder freeBreakfastCode(String freeBreakfastCode);

        public abstract Builder description(String description);

        public abstract Builder shortDescription(String shortDescription);

        public abstract Builder files(List<File> files);

        public abstract UpsellItem build();
    }
}
