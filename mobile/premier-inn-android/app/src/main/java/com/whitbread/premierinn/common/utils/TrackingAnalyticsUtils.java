package com.whitbread.premierinn.common.utils;


import androidx.annotation.NonNull;

import com.whitbread.premierinn.bookingdetails.UpsellItemSummary;
import com.whitbread.premierinn.criteria.roomselector.Room;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.common.RoomType;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ACTIVITY_SECTION_MAP;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type;

public class TrackingAnalyticsUtils {

    private static final String RATES_EVAR_INTERNAL_DELIMITER = "-";

    public static String buildEvar(@NonNull String evarKey, @NonNull String value) {
        return String.format(evarKey, value);
    }

    public static String format(@NonNull String value) {
        return String.format("And:%s", value);
    }

    public static String buildEvent(@NonNull String evarKey, @NonNull String value) {
        return buildEvar(evarKey, value);
    }

    public static String tripTypeLabel(boolean isBusinessTrip) {
        return isBusinessTrip ? "Business" : "Leisure";
    }

    public static String prepayLabel(boolean isPrepay) {
        return isPrepay ? "Prepay" : "Non-Prepay";
    }

    public static String getStateType(String canonicalName) {
        String screenType = ACTIVITY_SECTION_MAP.get(canonicalName);
        return StringUtils.valueOrDefault(screenType, Type.UNKNOWN);
    }

    public static String roomTypeLabel(String roomType) {
        RoomType type = Room.typeLookUp(roomType);
        if (type == null) {
            return "Unknown room type - " + roomType;
        }
        switch (type) {
            case DOUBLE:
                return "Double";
            case TWIN:
                return "Twin";
            case SINGLE:
                return "Single";
            case FAMILY:
                return "Family";
            case ACCESSIBLE:
                return "Accessible";
            default:
                return "UNKNOWN - " + type.name();
        }
    }

    public static String formatRateCode(String rateCode) {
        return rateCode.replaceAll("[0-9]", "");
    }

    public static String formatRateInfo(String rateCode, String lettingType, PriceDomain price) {
        return formatRateCode(rateCode)
                + RATES_EVAR_INTERNAL_DELIMITER
                + lettingType
                + RATES_EVAR_INTERNAL_DELIMITER
                + StringUtils.formatWith2DecimalPlaces(price.getAmount());
    }

    public static String formatPriceInfo(PriceDomain price) {
        return StringUtils.formatWith2DecimalPlaces(price.getAmount());
    }

    public static String mealTypeLabel(int mealCode) {
        if (mealCode == 0) {
            return "";
        }
        String mealCodeString = Integer.toString(mealCode);
        UpsellItemSummary.UpsellItemType mealType = UpsellItemSummary.UpsellItemType.getByCode(mealCodeString);
        if (mealType == null) {
            return "Unknown meal code - " + mealCode;
        }
        switch (mealType) {
            case PI_BREAKFAST:
            case OPERA_PI_BREAKFAST:
                return "Premier Inn Breakfast";
            case CONTINENTAL_BREAKFAST:
            case OPERA_CONTINENTAL_BREAKFAST:
                return "Continental Breakfast";
            case MEAL_DEAL:
            case OPERA_MEAL_DEAL:
                return "Meal Deal";
            case HUB_BREAKFAST:
                return "Hub Breakfast";
            case BREAKFAST_BOX:
                return "Breakfast box";
            default:
                return "Unknown meal type - " + mealType;
        }
    }
}
