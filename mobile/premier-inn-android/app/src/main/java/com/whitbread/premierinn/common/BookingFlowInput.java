package com.whitbread.premierinn.common;

import static com.whitbread.premierinn.data.common.Constants.EMPTY_STRING;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.request.booking.Breakfast;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.hoteldetails.SelectedRate;
import com.whitbread.premierinn.summary.SummaryInput;
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

@AutoValue
public abstract class BookingFlowInput implements Parcelable {

    public static BookingFlowInput create(SummaryInput summaryInput,
                                          @NonNull List<Breakfast> breakfasts, @Nullable UpsellItem selectedUpsellItem,
                                          @Nullable List<UpsellItem> selectedUpsellItems,
                                          float totalUpsellAmount, @Nullable List<ParcelableExtrasItem> selectedExtras) {

        PaymentRatePlan paymentRatePlan = PaymentRatePlan.create(summaryInput.prepaymentAllowed(), summaryInput.rate().prepaymentRequired(),
                summaryInput.rate().guaranteeRequired());

        boolean cityTaxForLeisure = summaryInput.cityTaxForLeisure() != null && summaryInput.cityTaxForLeisure();
        boolean cityTaxForBusiness = summaryInput.cityTaxForBusiness() != null && summaryInput.cityTaxForBusiness();

        return BookingFlowInput.builder()
                .numAdults(summaryInput.totalAdults())
                .numChildren(summaryInput.totalChildren())
                .numNights(summaryInput.totalNights())
                .hotelBrand(summaryInput.hotelBrand())
                .basketReference(summaryInput.basketReference())
                .hotelCode(summaryInput.hotel().code())
                .chosenRate(summaryInput.rate())
                .hotelImageReference(summaryInput.hotel().imageReference())
                .hotelName(summaryInput.hotel().name())
                .hotelLocation(summaryInput.hotel().address() == null ? EMPTY_STRING
                        : summaryInput.hotel().address())
                .arrivalDate(summaryInput.arrivalDate())
                .isHub(summaryInput.hotel().isHub())
                .breakfasts(breakfasts)
                .selectedUpsellItem(selectedUpsellItem)
                .selectedUpsellItems(selectedUpsellItems)
                .selectedExtras(selectedExtras)
                .totalUpsellAmount(totalUpsellAmount)
                .paymentRatePlan(paymentRatePlan)
                .roomBookings(summaryInput.roomBookings())
                .accessibleRoomBookings(summaryInput.accessibleRoomBookings())
                .twinRoomBookings(summaryInput.twinRoomBookings())
                .cityTaxForLeisure(cityTaxForLeisure)
                .cityTaxForBusiness(cityTaxForBusiness)
                .isDinnerAvailable(summaryInput.isDinnerAvailablePIBA())
                .isDinnerAvailableNonBa(summaryInput.isDinnerAvailableNotPIBA())
                .dinnerBudget(summaryInput.dinnerAllowance())
                .formattedBaseRate(summaryInput.formattedBaseRate())
                .promotionCode(summaryInput.promotionCode())
                .promotionTag(summaryInput.promotionTag())
                .rateTag(summaryInput.rateTag())
                .isEmployeeRateSelected(summaryInput.isEmployeeRateSelected())
                .build();
    }

    public static BookingFlowInput.Builder builder() {
        return new AutoValue_BookingFlowInput.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder numAdults(int numAdults);

        public abstract Builder numChildren(int numChildren);

        public abstract Builder numNights(int numNights);

        public abstract Builder basketReference(String basketReference);

        public abstract Builder hotelCode(String hotelCode);

        public abstract Builder hotelBrand(String hotelBrand);

        public abstract Builder chosenRate(SelectedRate selectedRate);

        public abstract Builder hotelImageReference(String imageReference);

        public abstract Builder hotelName(String hotelName);

        public abstract Builder hotelLocation(String hotelLocation);

        public abstract Builder arrivalDate(LocalDate arrivalDate);

        public abstract Builder isHub(boolean isHub);

        public abstract Builder breakfasts(List<Breakfast> breakfasts);

        public abstract Builder selectedUpsellItem(UpsellItem upsellItem);

        public abstract Builder selectedUpsellItems(List<UpsellItem> upsellItems);

        public abstract Builder selectedExtras(List<ParcelableExtrasItem> selectedExtras);

        public abstract Builder totalUpsellAmount(float upsellAmount);

        public abstract Builder paymentRatePlan(PaymentRatePlan paymentRatePlan);

        public abstract Builder roomBookings(List<RoomBooking> roomBookings);
        public abstract Builder accessibleRoomBookings(List<RoomBooking> roomBookings);
        public abstract Builder twinRoomBookings(List<RoomBooking> roomBookings);

        public abstract Builder cityTaxForLeisure(boolean cityTaxForLeisure);

        public abstract Builder cityTaxForBusiness(boolean cityTaxForBusiness);

        public abstract Builder isDinnerAvailable(boolean isDinnerAvailable);

        public abstract Builder isDinnerAvailableNonBa(boolean isDinnerAvailableNonBa);

        public abstract Builder dinnerBudget(float dinnerBudget);

        public abstract Builder formattedBaseRate(String formattedBaseRate);

        public abstract Builder promotionCode(String promotionCode);

        public abstract Builder promotionTag(String promotionTag);

        public abstract Builder isEmployeeRateSelected(boolean isEmployeeRateEnabled);

        public abstract BookingFlowInput build();

        public abstract Builder rateTag(String rateTag);
    }

    public abstract int numAdults();

    public abstract int numChildren();

    public abstract int numNights();

    public abstract String basketReference();

    public abstract String hotelCode();

    public abstract SelectedRate chosenRate();

    public abstract String hotelImageReference();

    public abstract String hotelName();

    public abstract String hotelLocation();

    public abstract String hotelBrand();

    public abstract LocalDate arrivalDate();

    public abstract boolean isHub();

    public abstract List<Breakfast> breakfasts();

    @Nullable
    public abstract UpsellItem selectedUpsellItem();

    @Nullable
    public abstract List<UpsellItem> selectedUpsellItems();

    public abstract float totalUpsellAmount();
    @Nullable
    public abstract List<ParcelableExtrasItem> selectedExtras();

    public abstract PaymentRatePlan paymentRatePlan();

    public abstract List<RoomBooking> roomBookings();
    @Nullable
    public abstract List<RoomBooking> accessibleRoomBookings();
    public abstract List<RoomBooking> twinRoomBookings();

    public abstract boolean cityTaxForLeisure();

    public abstract boolean cityTaxForBusiness();

    public abstract boolean isDinnerAvailable();

    public abstract boolean isDinnerAvailableNonBa();

    public abstract float dinnerBudget();
    @Nullable
    public abstract String formattedBaseRate();

    @Nullable
    public abstract String promotionCode();

    @Nullable
    public abstract String promotionTag();

    @Nullable
    public abstract String rateTag();

    public abstract boolean isEmployeeRateSelected();

    public int numGuests() {
        return numAdults() + numChildren();
    }

    public PriceDomain totalRoomsCost(boolean taxExempt) {
        float amount = 0f;
        String currency = null;

        List<RoomBooking> allRoomBookings = new ArrayList<>(roomBookings());

        if (accessibleRoomBookings() != null) {
            allRoomBookings.addAll(accessibleRoomBookings());
        }

        if (twinRoomBookings() != null) {
            allRoomBookings.addAll(twinRoomBookings());
        }

        if (allRoomBookings.isEmpty()) {
            return PriceDomain.Companion.createDefault();
        }

        for (RoomBooking roomBooking : allRoomBookings) {
            amount += roomBooking.totalRoomPrice(taxExempt).getAmount();
            if (currency == null) {
                currency = roomBooking.totalRoomPrice(taxExempt).getCurrency();
            }
        }

        return new PriceDomain(amount, currency);
    }

    public PriceDomain totalStayPrice(boolean taxExempt) {
        PriceDomain totalRoomsCost = totalRoomsCost(taxExempt);

        float upsellTotals = selectedUpsellItem() != null
                ? selectedUpsellItem().calculateTotalUpsellCostForStay(numAdults(), numNights())
                : selectedUpsellItems() != null
                ? totalUpsellAmount() : 0;

        return new PriceDomain(totalRoomsCost.getAmount() + upsellTotals,
                totalRoomsCost.getCurrency());
    }
}