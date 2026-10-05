package com.whitbread.premierinn.reviewbooking.analytics;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_LETTING_TYPE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_RATE_CODE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.SCREEN_TYPE;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Value.EMPLOYEE_RATE_CODE;
import static com.whitbread.premierinn.common.format.DateFormat.SLASHED_DAY_MONTH_YEAR;
import static com.whitbread.premierinn.common.format.DateFormat.WEEKDAY_DAY_MONTH;
import static com.whitbread.premierinn.common.utils.StringUtils.toColonSeparatedString;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.format;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.tripTypeLabel;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_ADULTS;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_IN_DATE;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_IN_DAY;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_IN_OUT_DAY;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_OUT_DATE;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHECK_OUT_DAY;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_CHILDREN;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_NIGHTS;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_RATE_DESCRIPTION;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.KEY_ROOMS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.contentsquare.android.api.model.CustomVar;
import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.AnalyticsData;
import com.whitbread.premierinn.common.format.FormatExtensionsKt;
import com.whitbread.premierinn.common.utils.AppExtensions;
import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.hoteldetails.SelectedRate;
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Pair;


/**
 * This class encapsulates all the analytics data that needs to be sent to Adobe for Add Extras (SummaryView) in Booking Flow
 * https://whitbreadis.atlassian.net/wiki/spaces/~Tom.Pinney/pages/126321085/Android+Review+and+Book
 */
@AutoValue
public abstract class ReviewBookingAnalyticsData implements AnalyticsData {

    public static final String KEY_RATE_NAME = "analyticsData.bf.rateName";
    public static final String KEY_ADDED_EXTRAS_DESCRIPTION = "analyticsData.bf.extrasSelectedDescription";
    static final String KEY_CUSTOMER_TYPE = "analyticsData.bf.customerType";

    public abstract String hotelCode();

    public abstract int nights();

    public abstract int rooms();

    public abstract int adults();

    public abstract int children();

    public abstract LocalDate checkInDate();

    public abstract String lettingType();

    public abstract PriceDomain roomPrice();

    @Nullable
    public abstract Pair<UpsellItem, List<ParcelableExtrasItem>> addedExtras();

    @Nullable
    public abstract Pair<List<UpsellItem>, List<ParcelableExtrasItem>> selectedExtras();

    public abstract String screenType();

    public abstract SelectedRate selectedRate();

    public abstract List<RoomBooking> selectedRooms();

    public abstract boolean businessUser();

    public abstract boolean isEmployeeRateSelected();

    @Nullable
    public abstract String promoCode();

    @Nullable
    public abstract String promoName();

    @Nullable
    public abstract String rateTag();

    public static ReviewBookingAnalyticsData.Builder builder() {
        return new AutoValue_ReviewBookingAnalyticsData.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder hotelCode(String hotelCode);

        public abstract Builder selectedRate(SelectedRate selectedRate);

        public abstract Builder lettingType(String lettingType);

        public abstract Builder nights(int nights);

        public abstract Builder rooms(int rooms);

        public abstract Builder adults(int adults);

        public abstract Builder children(int children);

        public abstract Builder checkInDate(LocalDate bookingAvailability);

        public abstract Builder addedExtras(Pair<UpsellItem, List<ParcelableExtrasItem>> addedExtras);

        public abstract Builder selectedExtras(Pair<List<UpsellItem>, List<ParcelableExtrasItem>> addedExtras);

        public abstract Builder screenType(String screenType);

        public abstract Builder roomPrice(PriceDomain roomPrice);

        public abstract Builder selectedRooms(List<RoomBooking> rooms);

        public abstract Builder businessUser(boolean businessUser);

        public abstract Builder isEmployeeRateSelected(boolean isEmployeeCodeSelected);

        public abstract Builder promoCode(String pushToken);

        public abstract Builder promoName(String pushToken);

        public abstract Builder rateTag(String pushToken);

        public abstract ReviewBookingAnalyticsData build();
    }

    @Override
    public Map<String, String> contextData() {
        final Map<String, String> contextData = new HashMap<>();

        contextData.put(KEY_NIGHTS, Integer.toString(nights()));
        contextData.put(KEY_ROOMS, Integer.toString(rooms()));
        contextData.put(KEY_ADULTS, Integer.toString(adults()));
        contextData.put(KEY_CHILDREN, Integer.toString(children()));
        contextData.put(KEY_CHECK_IN_DATE, formattedArrivalDate(checkInDate()));
        contextData.put(KEY_CHECK_OUT_DATE, formattedDepartureDate(checkInDate(), nights()));
        contextData.put(KEY_CHECK_IN_DAY, formattedCheckInDay(checkInDate()));
        contextData.put(KEY_CHECK_OUT_DAY, formattedCheckOutDay(checkInDate(), nights()));
        contextData.put(KEY_CHECK_IN_OUT_DAY, formattedCheckInOutDay(checkInDate(), nights()));
        contextData.put(KEY_RATE_DESCRIPTION, selectedRate().description());
        contextData.put(KEY_RATE_NAME, selectedRate().rateName());
        contextData.put(AnalyticsConstants.Key.PRODUCTS, ";" + hotelCode());
        contextData.put(AnalyticsConstants.Key.EVENTS, "scCheckout");

        if (selectedExtras() != null) {
            contextData.put(KEY_ADDED_EXTRAS_DESCRIPTION,  AppExtensions.toAnalyticsDataString(selectedExtras()));
        }

        contextData.put(
                KEY_RATE_CODE,
                isEmployeeRateSelected() ? EMPLOYEE_RATE_CODE : TrackingAnalyticsUtils.formatRateCode(selectedRate().code())
        );

        contextData.put(SCREEN_TYPE, format(screenType()));

        if (promoCode() != null) {
            contextData.put(AnalyticsConstants.Key.PROMO_CODE, promoCode());
            contextData.put(AnalyticsConstants.Key.PROMO_NAME, promoName());
            contextData.put(AnalyticsConstants.Key.ALL_RATE_TAGS, rateTag());
            contextData.put(AnalyticsConstants.Key.ALL_DISCOUNT_CODE_APPLIED, "true");
        }

        return contextData;
    }

    @Override
    public List<CustomVar> customCSQVars() {
        return Arrays.asList(
                new CustomVar(7, KEY_RATE_CODE, TrackingAnalyticsUtils.formatRateCode(selectedRate().code())),
                new CustomVar(19, KEY_LETTING_TYPE, toColonSeparatedString(bookedLettingTypes())),
                new CustomVar(14, KEY_CUSTOMER_TYPE, tripTypeLabel(businessUser()))
        );
    }

    public String formattedAddedExtras(@NonNull Pair<UpsellItem, List<ParcelableExtrasItem>> addedExtras) {
        StringBuilder formattedExtras = new StringBuilder();
        if (addedExtras.getFirst() != null && !addedExtras.getFirst().legend().isEmpty()) {
            formattedExtras.append(String.format("%s x %s ", (adults() + children()) * nights(),
                    addedExtras.getFirst().legend()));
        }
        for (ParcelableExtrasItem extrasItemDomain : addedExtras.getSecond()) {
            formattedExtras.append(String.format("%s x %s ", rooms(), extrasItemDomain.getName()));
        }
        return formattedExtras.toString();
    }

    public static String formattedAddedExtrasLegend(@NonNull Pair<UpsellItem, List<ParcelableExtrasItem>> addedExtras) {
        ArrayList<String> listOfLegends = new ArrayList<>();
        if (addedExtras.getFirst() != null && !addedExtras.getFirst().legend().isEmpty()) {
            listOfLegends.add(addedExtras.getFirst().legend());
        }

        for (int index = 0; index < addedExtras.getSecond().size(); index++) {
            listOfLegends.add(addedExtras.getSecond().get(index).getName());
        }
        return StringUtils.toCommaSeparatedString(listOfLegends);
    }

    public static String formattedArrivalDate(@NonNull LocalDate arrivalDate) {
        return FormatExtensionsKt.format(arrivalDate, SLASHED_DAY_MONTH_YEAR);
    }

    public static String formattedDepartureDate(@NonNull LocalDate arrivalDate, int nights) {
        final LocalDate checkOutDate = arrivalDate.plusDays(nights);
        return FormatExtensionsKt.format(checkOutDate, SLASHED_DAY_MONTH_YEAR);
    }

    public static String formattedCheckInDay(@NonNull LocalDate arrivalDate) {
        return FormatExtensionsKt.format(arrivalDate, WEEKDAY_DAY_MONTH).substring(0, 3);
    }

    public static String formattedCheckOutDay(@NonNull LocalDate arrivalDate, int nights) {
        final LocalDate checkOutDate = arrivalDate.plusDays(nights);
        return FormatExtensionsKt.format(checkOutDate, WEEKDAY_DAY_MONTH).substring(0, 3);
    }

    public static String formattedCheckInOutDay(@NonNull LocalDate arrivalDate, int nights) {
        final String formattedCheckInDate = formattedCheckInDay(arrivalDate);
        final String formattedCheckOutDate = formattedCheckOutDay(arrivalDate, nights);

        return String.format("%s-%s", formattedCheckInDate, formattedCheckOutDate);
    }

    private List<String> bookedLettingTypes() {
        List<String> roomLettingTypes = new ArrayList<>();
        for (RoomBooking bookingRoom : selectedRooms()) {
            roomLettingTypes.add(bookingRoom.getLettingCode());
        }
        return roomLettingTypes;
    }
}
