package com.whitbread.premierinn.paymentbreakdown;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Value.EMPLOYEE_RATE_CODE;
import static com.whitbread.premierinn.data.common.Constants.BRAND_PI;
import static com.whitbread.premierinn.data.common.Constants.BRAND_PI_GERMANY;
import static com.whitbread.premierinn.data.common.Constants.EMPTY_STRING;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.AnalyticsData;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.format.DateFormat;
import com.whitbread.premierinn.common.format.FormatExtensionsKt;
import com.whitbread.premierinn.common.mapper.CommonMappersKt;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.common.DomainMappers;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.data.remote.AllCheckInTimesInfo;
import com.whitbread.premierinn.domain.common.AllCheckInCheckoutTimesInfoDomain;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import com.whitbread.premierinn.paymentbreakdown.analytics.PaymentBreakdownAnalyticsData;
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownRoom;
import org.threeten.bp.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@ActivityRetainedScoped
public class PaymentBreakdownPresenter extends Presenter<PaymentBreakdownPresenter.View> {

    private PaymentBreakdownInput paymentBreakdownInput;
    private final TrackingAnalytics analytics;
    private final CompositeDisposable compositeDisposable;
    private final LogService logService;
    private final GetStringResource getStringResource;
    private final IsFeatureOn isFeatureOn;
    private AllCheckInCheckoutTimesInfoDomain hotelCheckInCheckInAllInfo;
    private Disposable checkInCheckoutInfoDisposable;
    private final ContentManagedResourceRepository contentRepository;
    private final DeviceLocaleProvider deviceLocaleProvider;
    private String donationPledgeString;

    @Inject
    public PaymentBreakdownPresenter(
                                     @NonNull TrackingAnalytics analytics,
                                     @NonNull ContentManagedResourceRepository contentRepository,
                                     @NonNull CompositeDisposable compositeDisposable,
                                     @NonNull LogService logService,
                                     @NonNull DeviceLocaleProvider deviceLocaleProvider,
                                     @NonNull IsFeatureOn isFeatureOn,
                                     @NonNull GetStringResource getStringResource) {
        this.analytics = analytics;
        this.contentRepository = contentRepository;
        this.compositeDisposable = compositeDisposable;
        this.logService = logService;
        this.deviceLocaleProvider = deviceLocaleProvider;
        this.getStringResource = getStringResource;
        this.isFeatureOn = isFeatureOn;
    }

    public void initParams(@NonNull PaymentBreakdownInput paymentBreakdownInput) {
        this.paymentBreakdownInput = paymentBreakdownInput;
    }

    @Override
    public void onAttachView(View view) {
        String formattedArrivalDate = FormatExtensionsKt.format(paymentBreakdownInput.arrivalDate(), DateFormat.DAY_DATE_MONTH_YEAR);
        LocalDate checkoutDay = paymentBreakdownInput.arrivalDate().plusDays(paymentBreakdownInput.totalNights());

        String formattedDepartureDate = FormatExtensionsKt.format(checkoutDay, DateFormat.DAY_DATE_MONTH_YEAR);
        PriceDomain totalPrice = CommonMappersKt.toPriceDomain(paymentBreakdownInput.totalPrice());
        PriceDomain donation = CommonMappersKt.toPriceDomain(paymentBreakdownInput.donation());

        boolean shouldShowDonation = isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_DONATION);
        donationPledgeString = shouldShowDonation ? getStringResource.invoke(ContentManagedResourceRepository.Key.DONATION_PLEDGE)
                : EMPTY_STRING;
        retrieveAndSetCheckInCheckOutInfoFromFirebase();

        setFields(view, formattedArrivalDate, formattedDepartureDate, totalPrice, donation);

        view.showPriceIncludesTaxesAndFeesMessage(true);

        view.setOtherExtras(CommonMappersKt.toExtrasItemDomain(paymentBreakdownInput.selectedExtras(), deviceLocaleProvider),
                paymentBreakdownInput.summaryBreakdownRooms().size(),
                deviceLocaleProvider);

        if (paymentBreakdownInput.selectedUpsells() != null) {
            List<UpsellItem> selectedUpsellList = paymentBreakdownInput.selectedUpsells();
            List<UpsellItem> foodUpsells = new ArrayList<>();
            boolean hasChildrenBreakfast = paymentBreakdownInput.totalChildrenBreakfast() > 0;
            int nights = paymentBreakdownInput.totalNights();

            for (UpsellItem selectedUpSellItem : selectedUpsellList) {
                if (selectedUpSellItem.foodUpsell()) {
                    foodUpsells.add(selectedUpSellItem);
                }
            }
            if (!foodUpsells.isEmpty()) {
                view.setMealExtra(nights, deviceLocaleProvider, selectedUpsellList, hasChildrenBreakfast);
            }

            if (hasChildrenBreakfast) {
                view.setChildrenBreakfast(paymentBreakdownInput.totalChildrenBreakfast(), nights);
            }
        }
        analytics.track(AnalyticsConstants.ScreenState.PAYMENT_DETAILS_SUMMARY, createAnalyticsDataObject(paymentBreakdownInput));
    }

    private void setFields(View view, String formattedArrivalDate, String formattedDepartureDate,
                           PriceDomain totalPrice, PriceDomain donation) {
        String hotelBrand = paymentBreakdownInput.hotelBrand();

        switch (hotelBrand) {
            case BRAND_PI:
            default:
                view.setFields(paymentBreakdownInput.totalGuests(), paymentBreakdownInput.totalNights(),
                        formattedArrivalDate, formattedDepartureDate, totalPrice,
                        paymentBreakdownInput.chosenRateName(), paymentBreakdownInput.summaryBreakdownRooms(),
                        donation, hotelCheckInCheckInAllInfo.getUkCheckInTimes().getSummaryOrPaymentBreakdownCheckInInfo(),
                        hotelCheckInCheckInAllInfo.getUkCheckInTimes().getSummaryOrPaymentBreakdownCheckOutInfo(),
                        deviceLocaleProvider, donationPledgeString);
                break;
            case BRAND_PI_GERMANY:
                view.setFields(paymentBreakdownInput.totalGuests(), paymentBreakdownInput.totalNights(),
                        formattedArrivalDate, formattedDepartureDate, totalPrice,
                        paymentBreakdownInput.chosenRateName(), paymentBreakdownInput.summaryBreakdownRooms(),
                        donation, hotelCheckInCheckInAllInfo.getGermanyCheckInTimes().getSummaryOrPaymentBreakdownCheckInInfo(),
                        hotelCheckInCheckInAllInfo.getGermanyCheckInTimes().getSummaryOrPaymentBreakdownCheckOutInfo(),
                        deviceLocaleProvider, donationPledgeString);
        }
    }

    private void retrieveAndSetCheckInCheckOutInfoFromFirebase() {
        checkInCheckoutInfoDisposable = contentRepository
                .getStringSingle(ContentManagedResourceRepository.Key.ALL_CHECK_IN_CHECK_OUT_TIMES.getValue())
                .map(it -> new Gson().fromJson(it, AllCheckInTimesInfo.class)).toObservable().firstOrError()
                .subscribe(it -> hotelCheckInCheckInAllInfo = DomainMappers.toAllCheckInCheckoutTimesInfoDomain(it),
                        onError -> logService.logException(onError, "Retrieval of check in/ check out from firebase failed"));
        compositeDisposable.add(checkInCheckoutInfoDisposable);
    }

    @Override
    protected void onDetachView() {
        compositeDisposable.clear();
    }

    private AnalyticsData createAnalyticsDataObject(@NonNull PaymentBreakdownInput paymentBreakdownInput) {

        String rateCode = paymentBreakdownInput.isEmployeeRateSelected()
                ? EMPLOYEE_RATE_CODE
                : paymentBreakdownInput.chosenRateCode();

        return PaymentBreakdownAnalyticsData.builder()
                .screenType(AnalyticsConstants.Type.BOOKING_FLOW)
                .rateCode(rateCode)
                .rateDescription(paymentBreakdownInput.chosenRateDescription())
                .rateName(paymentBreakdownInput.chosenRateName())
                .build();
    }

    public interface View extends PresenterView {

        void setFields(int guests, int nights, @NonNull String formattedArrivalDate, @NonNull String formattedDepartureDate,
                       @NonNull PriceDomain totalBooking, @Nullable String rateValue, @NonNull List<SummaryBreakdownRoom> rooms,
                       @NonNull PriceDomain donation, @NonNull String checkInInfo, @NonNull String checkOutInfo,
                       @NonNull DeviceLocaleProvider deviceLocaleProvider, String donationPledgeString);

        void setCCFee(@NonNull String label, @NonNull String amount); //TODO: Remove

        void setNonMealExtra(String title, PriceDomain price, DeviceLocaleProvider deviceLocaleProvider);

        void setMealExtra(int nights, DeviceLocaleProvider deviceLocaleProvider,
                          List<UpsellItem> selectedUpSell, boolean hasChildrenBreakfast);

        void setChildrenBreakfast(int children, int nights);

        void showPriceIncludesTaxesAndFeesMessage(boolean show);

        void setOtherExtras(List<ExtrasItemDomain> selectedExtras, int totalRooms, DeviceLocaleProvider deviceLocaleProvider);
    }
}