package com.whitbread.premierinn.paymentbreakdown;

import static com.whitbread.premierinn.data.common.Constants.BRAND_PI;
import static com.whitbread.premierinn.domain.common.Constants.GBP;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.whitbread.premierinn.api.response.booking.BookingPrice;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.mapper.CommonMappersKt;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import com.whitbread.premierinn.paymentbreakdown.analytics.PaymentBreakdownAnalyticsData;
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownRoom;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;

import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

@RunWith(MockitoJUnitRunner.class)
public class PaymentBreakdownPresenterTest {
    @Mock private PaymentBreakdownPresenter.View view;
    @Mock private TrackingAnalytics analytics;
    @Mock private PaymentBreakdownInput paymentBreakdownInput;
    @Mock private SummaryBreakdownRoom summaryBreakdownRoom;
    @Mock private ContentManagedResourceRepository contentRepository;
    @Mock private CompositeDisposable compositeDisposable;
    @Mock private LogService logService;
    @Mock private DeviceLocaleProvider deviceLocaleProviderMock;
    @Mock private IsFeatureOn isFeatureOn;
    @Mock private GetStringResource getStringResource;


    private PaymentBreakdownPresenter paymentBreakdownPresenter;

    private List<SummaryBreakdownRoom> rooms;

    private PriceDomain totalPrice;
    private BookingPrice parcelableTotalPrice;
    private String arrivalDateFormatted;
    private String formattedDepartureDate;

    private String rateClassification = "A";
    private String rateCode = "FLEXRATE";
    private String rateDescription = "Rate Description";
    private int numGuests = 2;
    private int numNights = 2;

    @Before
    public void setup() {
        paymentBreakdownPresenter = new PaymentBreakdownPresenter(
                analytics,
                contentRepository,
                compositeDisposable,
                logService,
                deviceLocaleProviderMock,
                isFeatureOn,
                getStringResource
        );
        paymentBreakdownPresenter.initParams(paymentBreakdownInput);
        LocalDate arrivalDate = LocalDate.of(2017, Month.JUNE, 9);
        arrivalDateFormatted = "Fri 9 Jun, 2017";
        formattedDepartureDate = "Sun 11 Jun, 2017";

        int priceValue = 100;
        totalPrice = PriceDomain.Companion.createWithGBPCurrency(priceValue);
        parcelableTotalPrice = CommonMappersKt.toBookingPrice(totalPrice);
        rooms = Collections.singletonList(summaryBreakdownRoom);

        when(paymentBreakdownInput.totalGuests()).thenReturn(numGuests);
        when(paymentBreakdownInput.totalNights()).thenReturn(numNights);
        when(paymentBreakdownInput.arrivalDate()).thenReturn(arrivalDate);
        when(paymentBreakdownInput.totalPrice()).thenReturn(parcelableTotalPrice);
        when(paymentBreakdownInput.chosenRateName()).thenReturn(rateClassification);
        when(paymentBreakdownInput.chosenRateCode()).thenReturn(rateCode);
        when(paymentBreakdownInput.chosenRateDescription()).thenReturn(rateDescription);
        when(paymentBreakdownInput.summaryBreakdownRooms()).thenReturn(rooms);
        when(paymentBreakdownInput.hotelBrand()).thenReturn(BRAND_PI);
        when(paymentBreakdownInput.donation()).thenReturn(new BookingPrice(0f, GBP));
        when(contentRepository.getStringSingle(ContentManagedResourceRepository.Key.ALL_CHECK_IN_CHECK_OUT_TIMES.getValue()))
                .thenReturn(Single.just(
                        "{\"ukCheckInTimes\":{\"bookingDetailsCheckInInfo\": \"after 3pm\",\"bookingDetailsCheckOutInfo\": \"before 12pm\","
                                + "\"summaryOrPaymentBreakdownCheckInInfo\": \"Check-in at 3:00pm\","
                                + "\"summaryOrPaymentBreakdownCheckOutInfo\": \"Check-out by 12:00pm\"},"
                                + "\"germanyCheckInTimes\": {\"bookingDetailsCheckInInfo\": \"after 6pm\","
                                + "\"bookingDetailsCheckOutInfo\": \"before 3pm\","
                                + "\"summaryOrPaymentBreakdownCheckInInfo\": \"Check-in at 6:00pm\","
                                + "\"summaryOrPaymentBreakdownCheckOutInfo\": \"Check-out by 3:00pm\"}}"));
        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_DONATION)).thenReturn(true);
        when(getStringResource.invoke(ContentManagedResourceRepository.Key.DONATION_PLEDGE)).thenReturn("any pledge");

    }

    @Test
    public void testLifeCycle() {
        assertFalse(paymentBreakdownPresenter.isViewAttached());
        paymentBreakdownPresenter.attachView(view);
        assertTrue(paymentBreakdownPresenter.isViewAttached());
        paymentBreakdownPresenter.detachView();
        assertFalse(paymentBreakdownPresenter.isViewAttached());
    }

    @Test
    public void testAttachView() {
        paymentBreakdownPresenter.attachView(view);
        verify(view).setFields(numGuests, numNights, arrivalDateFormatted, formattedDepartureDate, totalPrice, rateClassification,
                rooms, PriceDomain.Companion.createDefault(), "Check-in at 3:00pm", "Check-out by 12:00pm",
                deviceLocaleProviderMock, "any pledge");
        verify(analytics).track(eq(AnalyticsConstants.ScreenState.PAYMENT_DETAILS_SUMMARY), any(PaymentBreakdownAnalyticsData.class));
    }

    @Test
    public void showPriceIncludesTaxesAndFeesMessage() {
        paymentBreakdownPresenter.attachView(view);

        verify(view, times(1)).showPriceIncludesTaxesAndFeesMessage(true);
    }
}