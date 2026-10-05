package com.whitbread.premierinn.summarybreakdown;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Value.EMPLOYEE_RATE_CODE;
import static com.whitbread.premierinn.common.utils.AppExtensions.getCheckInCheckoutTimes;
import static com.whitbread.premierinn.common.utils.AppExtensions.retrieveCheckInCheckoutInfoFromFirebase;
import androidx.annotation.NonNull;
import com.whitbread.premierinn.api.Urls;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.common.CheckInCheckOutStringProvider;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.AnalyticsData;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.format.DateFormat;
import com.whitbread.premierinn.common.format.FormatExtensionsKt;
import com.whitbread.premierinn.common.mapper.CommonMappersKt;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.common.AllCheckInCheckoutTimesInfoDomain;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository;
import com.whitbread.premierinn.summary.SummaryInput;
import com.whitbread.premierinn.summarybreakdown.analytics.SummaryBreakdownAnalyticsData;
import org.threeten.bp.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Pair;

@ActivityRetainedScoped
public class SummaryBreakdownPresenter extends Presenter<SummaryBreakdownPresenter.View> {

    private SummaryBreakdownInput summaryBreakdownInput;
    private final TrackingAnalytics analytics;
    private final ContentManagedResourceRepository contentRepository;
    private final CompositeDisposable compositeDisposable;
    private final LogService logService;
    private AllCheckInCheckoutTimesInfoDomain allCheckInTimesInfo;
    private DeviceLocaleProvider deviceLocaleProvider;
    private CheckInCheckOutStringProvider checkInCheckOutStringProvider;

    @Inject
    public SummaryBreakdownPresenter(
            @NonNull TrackingAnalytics trackingAnalytics,
            @NonNull ContentManagedResourceRepository contentRepository,
            @NonNull CompositeDisposable compositeDisposable,
            @NonNull LogService logService,
            @NonNull DeviceLocaleProvider deviceLocaleProvider,
            @NonNull CheckInCheckOutStringProvider checkInCheckOutStringProvider
    ) {
        this.analytics = trackingAnalytics;
        this.contentRepository = contentRepository;
        this.compositeDisposable = compositeDisposable;
        this.logService = logService;
        this.deviceLocaleProvider = deviceLocaleProvider;
        this.checkInCheckOutStringProvider = checkInCheckOutStringProvider;
    }

    public void initParams(@NonNull SummaryBreakdownInput summaryBreakdownInput) {
        this.summaryBreakdownInput = summaryBreakdownInput;
    }

    @Override
    public void onAttachView(View view) {
        view.setToolBar();

        SummaryInput summaryInput = summaryBreakdownInput.summaryInput();
        PriceDomain totalStayPrice = CommonMappersKt.toPriceDomain(summaryBreakdownInput.totalStayPrice());

        if (summaryInput.hotel().address() != null) {
            view.setBookingBanner(Urls.CONTENT_BASE_URL + summaryInput.hotel().imageReference(), summaryInput.hotel().name(),
                    summaryInput.hotel().address());
        }

        view.showPriceIncludesTaxesAndFeesMessage(false);

        LocalDate arrivalDate = summaryBreakdownInput.summaryInput().arrivalDate();
        String formattedArrivalDate = FormatExtensionsKt.format(arrivalDate, DateFormat.DAY_DATE_MONTH_YEAR);
        LocalDate checkoutDay = arrivalDate.plusDays(summaryInput.totalNights());

        allCheckInTimesInfo = retrieveCheckInCheckoutInfoFromFirebase(compositeDisposable, logService, contentRepository);

        String formattedDepartureDate = FormatExtensionsKt.format(checkoutDay, DateFormat.DAY_DATE_MONTH_YEAR);
        List<SummaryBreakdownRoom> summaryBreakdownRooms =
                SummaryBreakdownRoom.createSummaryBreakdownRooms(
                        summaryBreakdownInput.summaryInput().roomBookings(),
                        summaryBreakdownInput.summaryInput().accessibleRoomBookings(),
                        summaryBreakdownInput.summaryInput().twinRoomBookings(),
                        summaryBreakdownInput.summaryInput().arrivalDate(),
                        summaryBreakdownInput.isBusinessTrip(), deviceLocaleProvider);

        androidx.core.util.Pair<String, String> checkInOutValues =
                getCheckInCheckoutTimes(summaryInput.hotelBrand(), allCheckInTimesInfo, checkInCheckOutStringProvider, true);

        view.setPriceBreakdown(summaryInput.totalGuests(), summaryInput.totalNights(),
                formattedArrivalDate, formattedDepartureDate, summaryBreakdownRooms,
                checkInOutValues.first,
                checkInOutValues.second,
                deviceLocaleProvider);

        view.setTotalPrice(totalStayPrice, deviceLocaleProvider);
        view.setRateName(summaryInput.rate().rateName());
        view.setOtherExtras(
                CommonMappersKt.toExtrasItemDomain(summaryBreakdownInput.selectedExtras(), deviceLocaleProvider),
                deviceLocaleProvider);

        if (summaryBreakdownInput.selectedUpsells() != null) {
            List<UpsellItem> selectedUpsellList = summaryBreakdownInput.selectedUpsells();
            List<UpsellItem> foodUpsells = new ArrayList<>();
            boolean hasChildrenBreakfast = summaryBreakdownInput.totalChildrenBreakfast() > 0;
            int nights = summaryInput.totalNights();

            for (UpsellItem selectedUpSellItem : selectedUpsellList) {
                if (selectedUpSellItem.foodUpsell()) {
                    foodUpsells.add(selectedUpSellItem);
                }
            }
            if (!foodUpsells.isEmpty()) {
                view.setMealExtra(nights, deviceLocaleProvider, selectedUpsellList, hasChildrenBreakfast);
            }

            if (hasChildrenBreakfast) {
                view.setChildrenBreakfastSummary(summaryBreakdownInput.totalChildrenBreakfast(), nights);
            }
        }
        analytics.track(AnalyticsConstants.ScreenState.SUMMARY_EXTRAS_FULL_SUMMARY, createAnalyticsDataObject(summaryBreakdownInput));
    }


    @Override
    protected void onDetachView() {
        compositeDisposable.clear();
    }

    private AnalyticsData createAnalyticsDataObject(@NonNull SummaryBreakdownInput summaryBreakdownInput) {
        LocalDate arrivalDate = summaryBreakdownInput.summaryInput().arrivalDate();
        LocalDate departureDate = arrivalDate.plusDays(summaryBreakdownInput.summaryInput().totalNights());

        int totalAdults = summaryBreakdownInput.summaryInput().totalAdults();
        int totalChildren = summaryBreakdownInput.summaryInput().totalChildren();
        String rateCode = summaryBreakdownInput.summaryInput().isEmployeeRateSelected()
                ? EMPLOYEE_RATE_CODE
                : summaryBreakdownInput.summaryInput().rate().code();

        return SummaryBreakdownAnalyticsData.builder()
                .screenType(AnalyticsConstants.Type.BOOKING_FLOW)
                .selectedExtras(new Pair<>(summaryBreakdownInput.selectedUpsells(), summaryBreakdownInput.selectedExtras()))
                .hotelCode(summaryBreakdownInput.summaryInput().hotel().code())
                .rateCode(rateCode)
                .rateDescription(summaryBreakdownInput.summaryInput().rate().description())
                .rateName(summaryBreakdownInput.summaryInput().rate().rateName())
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .nights(summaryBreakdownInput.summaryInput().totalNights())
                .rooms(summaryBreakdownInput.summaryInput().roomBookings().size())
                .adults(totalAdults)
                .children(totalChildren)
                .build();
    }

    public interface View extends PresenterView {

        void setToolBar();

        void setBookingBanner(@NonNull String hotelImageReference, @NonNull String hotelName, @NonNull String commaSeparatedAddress);

        void setPriceBreakdown(int guests, int nights, @NonNull String formattedArrivalDate, @NonNull String formattedDepartureDate,
                               @NonNull List<SummaryBreakdownRoom> summaryBreakdownRooms,
                               @NonNull String checkInInfo, @NonNull String checkOutInfo,
                               @NonNull DeviceLocaleProvider deviceLocaleProvider);

        void setTotalPrice(@NonNull PriceDomain totalPrice, @NonNull DeviceLocaleProvider deviceLocaleProvider);

        void setRateName(@NonNull String rateName);

        void setMealExtra(int nights,
                          DeviceLocaleProvider deviceLocaleProvider, List<UpsellItem> selectedUpSell, boolean hasChildrenBreakfast);

        void setOtherExtras(List<ExtrasItemDomain> selectedExtras, DeviceLocaleProvider deviceLocaleProvider);

        void setChildrenBreakfastSummary(int children, int nights);

        void showPriceIncludesTaxesAndFeesMessage(boolean show);
    }
}
