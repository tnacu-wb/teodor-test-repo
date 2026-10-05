package com.whitbread.premierinn.summarybreakdown;

import static com.whitbread.premierinn.data.common.Constants.BRAND_PI_GERMANY;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;

import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.common.CheckInCheckOutStringProvider;
import com.whitbread.premierinn.common.ParcelablePrice;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.AnalyticsData;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.common.RoomType;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository;
import com.whitbread.premierinn.hoteldetails.DailyRateInput;
import com.whitbread.premierinn.hoteldetails.SelectedHotel;
import com.whitbread.premierinn.hoteldetails.SelectedRate;
import com.whitbread.premierinn.summary.SummaryInput;
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem;
import com.whitbread.premierinn.summarybreakdown.analytics.SummaryBreakdownAnalyticsData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

@RunWith(MockitoJUnitRunner.class)
public class SummaryBreakdownPresenterTest {

    @Mock SummaryBreakdownPresenter.View view;
    @Mock private SummaryInput summaryInputMock;
    @Mock private RoomBooking roomMock;
    @Mock private DailyRateInput dailyRateMock;
    @Mock private PriceDomain totalCostMock;
    @Mock private ParcelablePrice parcelableTotalCostMock;
    @Mock TrackingAnalytics trackingAnalytics;
    @Mock List<UpsellItem> selectedUpsell;
    @Mock SelectedHotel selectedHotel;
    @Mock SelectedRate selectedRate;
    @Mock private ContentManagedResourceRepository contentRepository;
    @Mock private CompositeDisposable compositeDisposable;
    @Mock private LogService logService;
    @Mock private DeviceLocaleProvider deviceLocaleProviderMock;
    @Mock private CheckInCheckOutStringProvider checkInCheckOutStringProvider;

    @Captor ArgumentCaptor<List<SummaryBreakdownRoom>> summaryBreakdownRoomsCaptor;

    private final String hotelImageReference = "/some/url/stuff.jpg";
    private final String hotelName = "PI Newcastle City Centre";
    private final String hotelAddressString = "PI Newcastle City Centre";
    private final LocalDate date = LocalDate.of(2024, 3, 7);

    private final boolean cot = true;
    private final int adults = 1;
    private final int children = 1;

    private final float amount = 345.07f;
    private final String currency = "EUR";
    private final int totalNights = 1;

    private SummaryBreakdownPresenter presenter;
    private SummaryBreakdownInput summaryBreakdownInput;
    private String hotelCode = "hotelCode";
    private final List<ParcelableExtrasItem> selectedExtras = Collections.emptyList();

    @Before
    public void onSetup() {
        when(deviceLocaleProviderMock.getDeviceLocale()).thenReturn(Locale.UK);
        selectedUpsell = new ArrayList<>();
        summaryBreakdownInput = SummaryBreakdownInput.create(summaryInputMock, 0f, selectedUpsell,
                selectedExtras, parcelableTotalCostMock, false);
        presenter = new SummaryBreakdownPresenter(
                trackingAnalytics,
                contentRepository,
                compositeDisposable,
                logService,
                deviceLocaleProviderMock,
                checkInCheckOutStringProvider
        );
        presenter.initParams(summaryBreakdownInput);
        when(summaryInputMock.hotel()).thenReturn(selectedHotel);
        when(selectedHotel.imageReference()).thenReturn(hotelImageReference);
        when(selectedHotel.name()).thenReturn(hotelName);
        when(selectedHotel.address()).thenReturn(hotelAddressString);
        when(selectedHotel.code()).thenReturn(hotelCode);
        when(summaryInputMock.rate()).thenReturn(selectedRate);
        when(selectedRate.code()).thenReturn("123");
        when(selectedRate.description()).thenReturn("Description");
        when(selectedRate.rateName()).thenReturn("Name");
        when(summaryInputMock.roomBookings()).thenReturn(Collections.singletonList(roomMock));
        when(roomMock.getDailyRates()).thenReturn(Collections.singletonList(dailyRateMock));
        when(summaryInputMock.arrivalDate()).thenReturn(date);
        when(dailyRateMock.getDate()).thenReturn(date);
        when(roomMock.getCot()).thenReturn(cot);
        when(roomMock.getAdults()).thenReturn(adults);
        when(roomMock.getChildren()).thenReturn(children);
        when(roomMock.totalRoomPrice(false)).thenReturn(totalCostMock);
        when(roomMock.getType()).thenReturn(RoomType.DOUBLE.name());
        when(totalCostMock.getAmount()).thenReturn(amount);
        when(totalCostMock.getCurrency()).thenReturn(currency);
        when(parcelableTotalCostMock.getAmount()).thenReturn(amount);
        when(parcelableTotalCostMock.getCurrency()).thenReturn(currency);
        when(summaryInputMock.totalGuests()).thenReturn(adults + children);
        when(summaryInputMock.totalNights()).thenReturn(totalNights);
        when(summaryInputMock.hotelBrand()).thenReturn(BRAND_PI_GERMANY);
        when(contentRepository.getStringSingle(ContentManagedResourceRepository.Key.ALL_CHECK_IN_CHECK_OUT_TIMES.getValue()))
                .thenReturn(Single.just(
                        "{\"ukCheckInTimes\":{\"bookingDetailsCheckInInfo\": \"from 3pm\",\"bookingDetailsCheckOutInfo\": \"before 12pm\","
                                + "\"summaryOrPaymentBreakdownCheckInInfo\": \"Check-in at 3:00pm\","
                                + "\"summaryOrPaymentBreakdownCheckOutInfo\": \"Check-out by 12:00pm\"},"
                                + "\"germanyCheckInTimes\": {\"bookingDetailsCheckInInfo\": \"after 6pm\","
                                + "\"bookingDetailsCheckOutInfo\": \"before 3pm\","
                                + "\"summaryOrPaymentBreakdownCheckInInfo\": \"Check-in at 6:00pm\","
                                + "\"summaryOrPaymentBreakdownCheckOutInfo\": \"Check-out by 3:00pm\"}}"));
    }


    @Test
    public void testAttachView() {
        presenter.attachView(view);

        verify(view).setToolBar();

        verify(view).setPriceBreakdown(anyInt(), anyInt(), anyString(), anyString(),
                summaryBreakdownRoomsCaptor.capture(), anyString(), anyString(), any());

        verify(view).setPriceBreakdown(adults + children, totalNights, "Thu 7 Mar, 2024", "Fri 8 Mar, 2024",
                summaryBreakdownRoomsCaptor.getValue(), "Check-in at 6:00pm", "Check-out by 3:00pm", deviceLocaleProviderMock);

//        verify(trackingAnalytics).track(AnalyticsConstants.ScreenState.SUMMARY_EXTRAS_FULL_SUMMARY,
//                createAnalyticsDataObject(summaryBreakdownInput));

        SummaryBreakdownRoom summaryBreakdownRoom = summaryBreakdownRoomsCaptor.getValue().get(0);

        assertEquals(adults, summaryBreakdownRoom.adults());
        assertEquals(children, summaryBreakdownRoom.children());
        assertEquals(cot, summaryBreakdownRoom.cot());
        assertEquals("7 Mar", summaryBreakdownRoom.formattedArrivalDate());
        assertEquals("8 Mar", summaryBreakdownRoom.formattedDepartureDate());
        assertEquals(RoomType.DOUBLE.name(), summaryBreakdownRoom.roomType());
        assertTrue(Arrays.asList("€345.07", "EUR345.07").contains(summaryBreakdownRoom.currencyAndPrice()));
    }

    @Test
    public void alwaysHidePriceIncludesTaxesAndFeesMessage() {
        presenter.attachView(view);

        verify(view, times(1)).showPriceIncludesTaxesAndFeesMessage(false);
    }

    @Test
    public void testLifecycle() {
        assertFalse(presenter.isViewAttached());
        presenter.attachView(view);
        assertTrue(presenter.isViewAttached());
        presenter.detachView();
        assertFalse(presenter.isViewAttached());
    }

    private AnalyticsData createAnalyticsDataObject(@NonNull SummaryBreakdownInput summaryBreakdownInput) {
        LocalDate departureDate = summaryBreakdownInput.summaryInput().arrivalDate()
                .plusDays(summaryBreakdownInput.summaryInput().totalNights());
        int adults = summaryBreakdownInput.summaryInput().totalAdults();
        int children = summaryBreakdownInput.summaryInput().totalChildren();

        return SummaryBreakdownAnalyticsData.builder()
                .screenType(AnalyticsConstants.Type.BOOKING_FLOW)
                .hotelCode(summaryBreakdownInput.summaryInput().hotel().code())
                .rateCode(summaryBreakdownInput.summaryInput().rate().code())
                .rateDescription(summaryBreakdownInput.summaryInput().rate().description())
                .rateName(summaryBreakdownInput.summaryInput().rate().rateName())
                .arrivalDate(summaryBreakdownInput.summaryInput().arrivalDate())
                .departureDate(departureDate)
                .nights(summaryBreakdownInput.summaryInput().totalNights())
                .rooms(summaryBreakdownInput.summaryInput().roomBookings().size())
                .adults(adults)
                .children(children)
                .build();
    }
}