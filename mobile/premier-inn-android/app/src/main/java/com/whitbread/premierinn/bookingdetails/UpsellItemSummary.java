package com.whitbread.premierinn.bookingdetails;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.response.booking.BookingPrice;

import java.util.HashMap;
import java.util.Map;

@AutoValue
public abstract class UpsellItemSummary implements Parcelable {

    public abstract UpsellItemType itemType();

    public abstract BookingPrice price();

    public static UpsellItemSummary create(@NonNull UpsellItemType upsellItemType, @NonNull BookingPrice price) {
        return new AutoValue_UpsellItemSummary(upsellItemType, price);
    }

    public enum UpsellItemType {
        NO_PREFERENCE("0"),
        PI_BREAKFAST("11"),
        OPERA_PI_BREAKFAST("BFADBF"),
        CONTINENTAL_BREAKFAST("12"),
        OPERA_CONTINENTAL_BREAKFAST("BFADCT"),
        BREAKFAST_BOX("14"),
        OPERA_BREAKFAST_BOX("OBFBOX"),
        FREE_CHILD_BREAKFAST("15"),
        OPERA_FREE_CHILD_BREAKFAST("BFCHDF"),
        MEAL_DEAL("17"),
        OPERA_MEAL_DEAL("MDP"),
        HUB_BREAKFAST("18"),
        CARD_PROCESSING_FEE("99"),
        EARLY_CHECK_IN("HSCKIN"),
        LATE_CHECK_OUT("HSCOU2");

        private String code;

        private static Map<String, UpsellItemType> map = new HashMap<>();

        static {
            for (UpsellItemType upsellItemType : UpsellItemType.values()) {
                map.put(upsellItemType.code, upsellItemType);
            }
        }

        UpsellItemType(String code) {
            this.code = code;
        }

        @Nullable
        public static UpsellItemType getByCode(String code) {
            return map.get(code);
        }

        public String code() {
            return code;
        }
    }
}