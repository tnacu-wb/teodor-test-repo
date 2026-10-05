package com.whitbread.premierinn.summary;

import static com.whitbread.premierinn.common.utils.AppExtensions.getFirstRateTag;
import static com.whitbread.premierinn.common.utils.StringUtils.EMPTY_STRING;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.common.mapper.CommonMappersKt;
import com.whitbread.premierinn.domain.common.OperaCommonExtensionsKt;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.common.RatePlanOpera;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.AncillaryCloseOutItem;
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain;
import com.whitbread.premierinn.hoteldetails.SelectedHotel;
import com.whitbread.premierinn.hoteldetails.SelectedRate;
import com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.HDPExtensionsKt;
import com.whitbread.premierinn.summary.models.ParcelableAncillariesCloseOutItem;
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem;
import com.whitbread.premierinn.summary.models.ParcelableMenuAndAllergyInfo;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class SummaryInput implements Parcelable {
    public static SummaryInput create(@Nullable HotelAvailabilityDomain hotelAvailabilityDomain,
                                      List<Integer> infantCountInRoom,
                                      SelectedHotel selectedHotel, @NonNull SelectedRate selectedRate,
                                      String pmsRoomTypeSelected,
                                      boolean alternativeRoomChosen, String hotelBrand,
                                      LocalDate arrivalDate, LocalDate departureDate, int nights,
                                      Boolean hasAccessibleRooms,
                                      Boolean hasTwinRoom,
                                      List<AncillaryCloseOutItem> ancillaryCloseOutItems,
                                      boolean hasCustomerLoggedIn, boolean isAlternativeRoomChosen,
                                      Boolean isBusinessUser, Boolean isEmployeeRateSelected,
                                      @Nullable List<String> upsellItemsAllowed, float dinnerAllowance,
                                      @Nullable String formattedBaseRate,
                                      @Nullable String promotionCode, @Nullable String promoKind,
                                      @Nullable String promoTag,
                                      String selectedRoomClass) {
        String rateTag;
        rateTag = (promotionCode != null) ? getFirstRateTag(hotelAvailabilityDomain.getListOfRatesClassification()) : EMPTY_STRING;

        List<UpsellItem> upsellItems = new ArrayList<>();
        List<ParcelableExtrasItem> parcelableExtrasItems = new ArrayList<>();
        List<String> listOfUpsellImageSources = new ArrayList<>();

        List<RatePlanOpera> ratePlanOperas = OperaCommonExtensionsKt
                .convertToRatePlansOpera(hotelAvailabilityDomain);

        List<RoomBooking> roomBookings = RoomBooking.createListGQ(ratePlanOperas,
                infantCountInRoom, selectedRate.rateType(), alternativeRoomChosen, pmsRoomTypeSelected);

        List<RoomBooking> accessibleRoomBookings = RoomBooking.createAccessibleListGQ(ratePlanOperas,
                selectedRate.rateType(), isAlternativeRoomChosen);

        List<RoomBooking> twinRoomBookings = RoomBooking.createTwinRoomListGQ(ratePlanOperas, selectedRate.rateType());

        List<RoomBooking> twinRoomBookingsAccessibleFlow = RoomBooking.createTwinRoomListGQ(OperaCommonExtensionsKt
                .convertToRatePlansOpera(hotelAvailabilityDomain, true), selectedRate.rateType());

        List<List<String>> specialRequestsList = SummaryExtensionsKt.getSpecialRequestsList(
                hotelAvailabilityDomain, selectedRate.rateType(), alternativeRoomChosen);

        List<String> packageCode = SummaryExtensionsKt.getPackageCode(
                hotelAvailabilityDomain, selectedRate.rateType(), alternativeRoomChosen);

        List<Double> packageAmount = SummaryExtensionsKt.getPackageAmount(
                hotelAvailabilityDomain, selectedRate.rateType(), alternativeRoomChosen);

        if (hasAccessibleRooms) {

            List<RoomBooking> stdAndTwinRoomBookings = new ArrayList<>(roomBookings);
            stdAndTwinRoomBookings.addAll(twinRoomBookingsAccessibleFlow);

            return SummaryInput.builder()
                    .bookingId("")
                    .hotel(selectedHotel)
                    .hotelBrand(hotelBrand)
                    .prepaymentAllowed(false)
                    .arrivalDateGQ(arrivalDate.toString())
                    .departureDateGQ(departureDate.toString())
                    .totalNights(nights)
                    .rate(selectedRate)
                    .specialRequests(specialRequestsList)
                    .packageCode(packageCode)
                    .packageAmount(packageAmount)
                    .roomBookings(stdAndTwinRoomBookings)
                    .accessibleRoomBookings(accessibleRoomBookings)
                    .twinRoomBookings(Collections.emptyList())
                    .upsellItems(upsellItems)
                    .ancillaryCloseOutItems(CommonMappersKt.toParcelableAncillariesCloseout(ancillaryCloseOutItems))
                    .extrasItems(parcelableExtrasItems)
                    .isDinnerAvailableNotPIBA(false)
                    .isDinnerAvailablePIBA(false)
                    .upsellsImages(listOfUpsellImageSources)
                    .isAlternativeRoom(alternativeRoomChosen)
                    .isLoggedIn(hasCustomerLoggedIn)
                    .isBusinessUser(isBusinessUser)
                    .isEmployeeRateSelected(isEmployeeRateSelected)
                    .dinnerAllowance(dinnerAllowance)
                    .formattedBaseRate(formattedBaseRate)
                    .promotionCode(promotionCode)
                    .promoKind(promoKind)
                    .promotionTag(promoTag)
                    .selectedRoomClass(selectedRoomClass)
                    .rateTag(rateTag)
                    .build();
        } else if (hasTwinRoom) {
            return SummaryInput.builder()
                    .bookingId("")
                    .hotel(selectedHotel)
                    .hotelBrand(hotelBrand)
                    .prepaymentAllowed(false)
                    .arrivalDateGQ(arrivalDate.toString())
                    .departureDateGQ(departureDate.toString())
                    .totalNights(nights)
                    .rate(selectedRate)
                    .specialRequests(specialRequestsList)
                    .packageCode(packageCode)
                    .packageAmount(packageAmount)
                    .roomBookings(roomBookings)
                    .accessibleRoomBookings(accessibleRoomBookings)
                    .twinRoomBookings(twinRoomBookings)
                    .upsellItems(upsellItems)
                    .ancillaryCloseOutItems(CommonMappersKt.toParcelableAncillariesCloseout(ancillaryCloseOutItems))
                    .extrasItems(parcelableExtrasItems)
                    .isDinnerAvailableNotPIBA(false)
                    .isDinnerAvailablePIBA(false)
                    .upsellsImages(listOfUpsellImageSources)
                    .isAlternativeRoom(alternativeRoomChosen)
                    .isLoggedIn(hasCustomerLoggedIn)
                    .isBusinessUser(isBusinessUser)
                    .isEmployeeRateSelected(isEmployeeRateSelected)
                    .dinnerAllowance(dinnerAllowance)
                    .formattedBaseRate(formattedBaseRate)
                    .promotionCode(promotionCode)
                    .promoKind(promoKind)
                    .promotionTag(promoTag)
                    .rateTag(rateTag)
                    .selectedRoomClass(selectedRoomClass)
                    .build();
        } else {
            return SummaryInput.builder()
                    .bookingId("")
                    .hotel(selectedHotel)
                    .hotelBrand(hotelBrand)
                    .prepaymentAllowed(false)
                    .arrivalDateGQ(arrivalDate.toString())
                    .departureDateGQ(departureDate.toString())
                    .totalNights(nights)
                    .rate(selectedRate)
                    .specialRequests(specialRequestsList)
                    .roomBookings(roomBookings)
                    .accessibleRoomBookings(accessibleRoomBookings)
                    .twinRoomBookings(twinRoomBookings)
                    .upsellItems(upsellItems)
                    .ancillaryCloseOutItems(CommonMappersKt.toParcelableAncillariesCloseout(ancillaryCloseOutItems))
                    .extrasItems(parcelableExtrasItems)
                    .isDinnerAvailableNotPIBA(false)
                    .isDinnerAvailablePIBA(false)
                    .upsellsImages(listOfUpsellImageSources)
                    .isAlternativeRoom(alternativeRoomChosen)
                    .isLoggedIn(hasCustomerLoggedIn)
                    .isBusinessUser(isBusinessUser)
                    .isEmployeeRateSelected(isEmployeeRateSelected)
                    .dinnerAllowance(dinnerAllowance)
                    .formattedBaseRate(formattedBaseRate)
                    .promotionCode(promotionCode)
                    .promoKind(promoKind)
                    .promotionTag(promoTag)
                    .rateTag(rateTag)
                    .selectedRoomClass(selectedRoomClass)
                    .build();
        }
    }

    public abstract String bookingId();
    public abstract SelectedHotel hotel();
    public abstract String hotelBrand();
    public abstract boolean prepaymentAllowed();
    @Nullable
    public abstract Boolean cityTaxForLeisure();
    @Nullable
    public abstract Boolean cityTaxForBusiness();
    public abstract String arrivalDateGQ();
    public abstract String departureDateGQ();
    @Nullable
    public abstract List<List<String>> specialRequests();
    @Nullable
    public abstract List<String> packageCode();
    @Nullable
    public abstract List<Double> packageAmount();
    public abstract int totalNights();
    @Nullable
    public abstract List<ParcelableMenuAndAllergyInfo> menus();
    @Nullable
    public abstract List<ParcelableMenuAndAllergyInfo> allergyInfo();
    public abstract SelectedRate rate();
    @Nullable
    public abstract List<RoomBooking> roomBookings();
    @Nullable
    public abstract List<RoomBooking> accessibleRoomBookings();
    @Nullable
    public abstract List<RoomBooking> twinRoomBookings();
    public abstract List<UpsellItem> upsellItems();
    public abstract List<ParcelableAncillariesCloseOutItem> ancillaryCloseOutItems();
    @Nullable
    public abstract List<ParcelableExtrasItem> extrasItems();
    public abstract boolean isAlternativeRoom();
    public abstract boolean isDinnerAvailablePIBA();
    public abstract boolean isDinnerAvailableNotPIBA();
    public abstract boolean isLoggedIn();
    public abstract Boolean isBusinessUser();
    public abstract Boolean isEmployeeRateSelected();
    @Nullable
    public abstract List<String> upsellsImages();
    public abstract float dinnerAllowance();
    @Nullable
    public abstract String formattedBaseRate();
    @Nullable
    public abstract String promotionCode();
    @Nullable
    public abstract String promoKind();
    @Nullable
    public abstract String promotionTag();
    @Nullable
    public abstract String basketReference();
    @Nullable
    public abstract List<String> upsellItemsAllowed();
    public abstract String selectedRoomClass();
    @Nullable
    public abstract String rateTag();


    public abstract Builder toBuilder();

    public static SummaryInput.Builder builder() {
        return new AutoValue_SummaryInput.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder bookingId(String bookingId);
        public abstract Builder hotel(SelectedHotel selectedHotel);
        public abstract Builder hotelBrand(String hotelBrand);
        public abstract Builder prepaymentAllowed(boolean prepaymentAllowed);
        public abstract Builder cityTaxForLeisure(Boolean cityTaxForLeisure);
        public abstract Builder cityTaxForBusiness(Boolean cityTaxForBusiness);
        public abstract Builder arrivalDateGQ(String arrivalDate);
        public abstract Builder departureDateGQ(String departureDate);
        public abstract Builder specialRequests(List<List<String>> specialRequests);
        public abstract Builder packageCode(List<String> packageCode);
        public abstract Builder packageAmount(List<Double> packageAmount);
        public abstract Builder totalNights(int totalNights);
        public abstract Builder menus(List<ParcelableMenuAndAllergyInfo> menus);
        public abstract Builder allergyInfo(List<ParcelableMenuAndAllergyInfo> allergyInfo);
        public abstract Builder rate(SelectedRate selectedRate);
        public abstract Builder roomBookings(List<RoomBooking> roomBookings);
        public abstract Builder accessibleRoomBookings(List<RoomBooking> accessibleRoomBookings);
        public abstract Builder twinRoomBookings(List<RoomBooking> twinRoomBookings);
        public abstract Builder upsellItems(List<UpsellItem> upsellItems);
        public abstract Builder ancillaryCloseOutItems(List<ParcelableAncillariesCloseOutItem> ancillaryCloseOutItems);
        public abstract Builder extrasItems(List<ParcelableExtrasItem> extrasItems);
        public abstract Builder isAlternativeRoom(boolean isAlternativeRoom);
        public abstract Builder isDinnerAvailablePIBA(boolean isDinnerAvailable);
        public abstract Builder isDinnerAvailableNotPIBA(boolean isDinnerAvailableNonBa);
        public abstract Builder isLoggedIn(boolean isLoggedIn);
        public abstract Builder isBusinessUser(Boolean isBusinessUser);
        public abstract Builder isEmployeeRateSelected(Boolean isEmployeeRateSelected);
        public abstract Builder upsellsImages(List<String> upsellImageSources);
        public abstract Builder dinnerAllowance(float dinnerAllowance);
        public abstract Builder formattedBaseRate(String formattedBaseRate);
        public abstract Builder promotionCode(String promoCode);
        public abstract Builder promoKind(String promoKind);
        public abstract Builder rateTag(String rateTag);
        public abstract Builder promotionTag(String promoTag);
        public abstract Builder selectedRoomClass(String selectedRoomClass);
        public abstract Builder basketReference(String basketReference);
        public abstract Builder upsellItemsAllowed(List<String> upsellItemsAllowed);

        public abstract SummaryInput build();
    }

    public int totalAdults() {
        int adults = 0;
        for (RoomBooking roomBooking : roomBookings()) {
            adults += roomBooking.getAdults();
        }

        for (RoomBooking roomBooking : accessibleRoomBookings()) {
            adults += roomBooking.getAdults();
        }

        for (RoomBooking roomBooking : twinRoomBookings()) {
            adults += roomBooking.getAdults();
        }
        return adults;
    }

    public int totalAdultsAccessible(int roomNumber) {
        return HDPExtensionsKt.totalAdults(accessibleRoomBookings(), roomNumber);
    }

    public int totalChildrenAccessible(int roomNumber) {
        return HDPExtensionsKt.totalChildrenOpera(accessibleRoomBookings(), roomNumber);
    }

    public int totalChildren() {
        int children = 0;
        for (RoomBooking roomBooking : roomBookings()) {
            children += roomBooking.getChildren();
        }

        for (RoomBooking roomBooking : accessibleRoomBookings()) {
            children += roomBooking.getChildren();
        }

        for (RoomBooking roomBooking : twinRoomBookings()) {
            children += roomBooking.getChildren();
        }
        return children;
    }

    public PriceDomain totalStayPrice() { // Fix This Too Complicated
        float price = 0f;
        if (!roomBookings().isEmpty()) {
            if (!accessibleRoomBookings().isEmpty() && twinRoomBookings().isEmpty()) {
                // When opera both std and accessible are added - no twin
                price = getPriceFromListOfRoomBookings(roomBookings(), price);
                price = getPriceFromListOfRoomBookings(accessibleRoomBookings(), price);

                return new PriceDomain(price, roomBookings().get(0).totalRoomPrice(false).getCurrency());
            } else if (!twinRoomBookings().isEmpty() && accessibleRoomBookings().isEmpty()) {
                // When opera both std and twin are added - no accessible
                price = getPriceFromListOfRoomBookings(roomBookings(), price);
                price = getPriceFromListOfRoomBookings(twinRoomBookings(), price);

                return new PriceDomain(price, roomBookings().get(0).totalRoomPrice(false).getCurrency());
            } else if (!twinRoomBookings().isEmpty() && !accessibleRoomBookings().isEmpty()) {
                // When opera all std, twin and accessible are added
                price = getPriceFromListOfRoomBookings(roomBookings(), price);
                price = getPriceFromListOfRoomBookings(accessibleRoomBookings(), price);
                price = getPriceFromListOfRoomBookings(twinRoomBookings(), price);
                return new PriceDomain(price, roomBookings().get(0).totalRoomPrice(false).getCurrency());
            } else {
                price = getPriceFromListOfRoomBookings(roomBookings(), price);

                return new PriceDomain(price, roomBookings().get(0).totalRoomPrice(false).getCurrency());
            }
        } else if (!accessibleRoomBookings().isEmpty() && twinRoomBookings().isEmpty()) {
            // When opera and only accessible added
            price = getPriceFromListOfRoomBookings(accessibleRoomBookings(), price);
            return new PriceDomain(price, accessibleRoomBookings().get(0).totalRoomPrice(false).getCurrency());
        } else if (!twinRoomBookings().isEmpty() && accessibleRoomBookings().isEmpty()) {
            // When opera and only twin added
            price = getPriceFromListOfRoomBookings(twinRoomBookings(), price);
            return new PriceDomain(price, twinRoomBookings().get(0).totalRoomPrice(false).getCurrency());
        }
        // When opera, twin & accessible added
        price = getPriceFromListOfRoomBookings(accessibleRoomBookings(), price);
        price = getPriceFromListOfRoomBookings(twinRoomBookings(), price);
        return new PriceDomain(price, twinRoomBookings().get(0).totalRoomPrice(false).getCurrency());
    }

    private float getPriceFromListOfRoomBookings(List<RoomBooking> roomBookings, float price) {
        for (RoomBooking roomBooking : roomBookings) {
            price += roomBooking.totalRoomPrice(false).getAmount();
        }
        return price;
    }

    public int totalGuests() {
        return totalAdults() + totalChildren();
    }
    public LocalDate arrivalDate() {
        if (roomBookings().size() > 0) {
            return roomBookings().get(0).getDailyRates().get(0).getDate();
        } else if (accessibleRoomBookings().size() > 0) {
            return accessibleRoomBookings().get(0).getDailyRates().get(0).getDate();
        } else {
            return twinRoomBookings().get(0).getDailyRates().get(0).getDate();
        }
    }
}
