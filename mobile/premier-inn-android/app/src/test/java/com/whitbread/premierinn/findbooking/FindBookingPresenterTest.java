package com.whitbread.premierinn.findbooking;

import static com.whitbread.premierinn.common.utils.DateUtils.date6MonthsInPast;
import static com.whitbread.premierinn.domain.booking.usecase.ImportBookingKt.KEY_BOOKING_EXISTS;
import static com.whitbread.premierinn.domain.booking.usecase.ImportBookingKt.KEY_BOOKING_NOT_FOUND;
import static com.whitbread.premierinn.domain.booking.usecase.ImportBookingKt.KEY_PAST_BOOKING;
import static com.whitbread.premierinn.findbooking.analytics.FindBookingAnalyticsData.ERROR_BOOKING_ALREADY_IMPORTED;
import static com.whitbread.premierinn.findbooking.analytics.FindBookingAnalyticsData.ERROR_BUSINESS_BOOKING_IMPORT;
import static com.whitbread.premierinn.findbooking.analytics.FindBookingAnalyticsData.ERROR_FETCHING_BOOKING;
import static com.whitbread.premierinn.findbooking.analytics.FindBookingAnalyticsData.ERROR_PAST_BOOKING_IMPORT;
import static com.whitbread.premierinn.findbooking.analytics.FindBookingAnalyticsData.ERROR_UNABLE_TO_RETRIEVE_BOOKING;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.whitbread.premierinn.api.response.ErrorBody;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.CampaignDataModel;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.booking.usecase.BusinessBookingImportError;
import com.whitbread.premierinn.domain.booking.usecase.ImportBooking;
import com.whitbread.premierinn.domain.booking.usecase.ImportBookingException;
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer;
import com.whitbread.premierinn.domain.graphql.findBooking.entity.FindBookingDomain;
import com.whitbread.premierinn.findbooking.analytics.FindBookingAnalyticsData;
import com.whitbread.premierinn.utils.RxJavaTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.LocalDate;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

@RunWith(MockitoJUnitRunner.class)
public class FindBookingPresenterTest {
    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FindBookingPresenter presenter;
    private FindBookingInput findBookingInput = null;

    @Mock
    ImportBooking importBooking;
    @Mock
    FindBookingPresenter.View view;
    @Mock
    TrackingAnalytics analytics;
    @Mock
    DeviceLocaleProvider deviceLocaleProvider;
    @Mock
    LogService crashlyticsLogger;
    @Mock
    GetCustomer getCustomer;

    @Before
    public void onSetup() {
        presenter = new FindBookingPresenter(
                importBooking,
                compositeDisposable,
                analytics,
                deviceLocaleProvider,
                crashlyticsLogger,
                getCustomer
        );
        presenter.initParams(findBookingInput);
        when(view.onClickArrivalDate()).thenReturn(Observable.never());
        when(view.onArrivalDateSelected()).thenReturn(Observable.never());
        when(view.onReferenceBookingChanged()).thenReturn(Observable.never());
        when(view.onLastNameChanged()).thenReturn(Observable.never());
        when(view.onFindBookingClick()).thenReturn(Observable.never());
        when(deviceLocaleProvider.getDeviceLanguage()).thenReturn("en");
        when(deviceLocaleProvider.getCountryIfRegion(any())).thenReturn("gb");
    }

    @Test
    public void testLifecycle() {
        assertFalse(presenter.isViewAttached());
        assertEquals(0, compositeDisposable.size());
        presenter.attachView(view);
        verify(analytics).track(AnalyticsConstants.ScreenState.FIND_BOOKING, new FindBookingAnalyticsData(new CampaignDataModel()));
        assertTrue(presenter.isViewAttached());
        assertEquals(5, compositeDisposable.size());
        presenter.detachView();
        assertFalse(presenter.isViewAttached());
        assertEquals(0, compositeDisposable.size());
    }

    @Test
    public void onArrivalDateSelectedTest() {
        LocalDate localDate = LocalDate.of(2017, 4, 1);
        when(view.onArrivalDateSelected()).thenReturn(Observable.just(localDate));

        presenter.attachView(view);

        verify(view).showArrivalDateError(false);
        verify(view).showArrivalDate(localDate);
    }

    @Test
    public void onClickArrivalDateTest() {
        when(view.onClickArrivalDate()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);
        verify(view).startCalendarActivity(date6MonthsInPast(), LocalDate.now());
    }

    @Test
    public void onReferenceBookingChanged() {
        String refBooking = "refBooking";
        when(view.onReferenceBookingChanged()).thenReturn(Observable.just(refBooking));
        presenter.attachView(view);
        verify(view).showBookingRefError(false);
    }

    @Test
    public void onLastNameChanged() {
        String lastName = "lastName";
        when(view.onLastNameChanged()).thenReturn(Observable.just(lastName));
        presenter.attachView(view);
        verify(view).showLastNameError(false);
    }

    @Test
    public void onFindBookingClick() {
        when(view.onFindBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        String refBooking = "refBooking";
        when(view.onReferenceBookingChanged()).thenReturn(Observable.just(refBooking));
        String lastName = "lastName";
        when(view.onLastNameChanged()).thenReturn(Observable.just(lastName));
        LocalDate calendarDay = LocalDate.of(2017, 4, 1);
        when(view.onArrivalDateSelected()).thenReturn(Observable.just(calendarDay));

        FindBookingDomain findBookingDomain = new FindBookingDomain("OPERA", "PI123456",
                "uuid123", "token123", "LONHO", false);
        when(importBooking.execute(refBooking, lastName, calendarDay, "en", "gb"))
                .thenReturn(Single.just(findBookingDomain));

        presenter.attachView(view);

        verify(view).showLoading(true);
        verify(view).showLoading(false);

        verify(view).goToBookingDetailsScreen(refBooking, lastName, calendarDay, "uuid123", "token123", false);
    }

    @Test
    public void onFindBookingClickImportBookingAlreadyExistError() {
        when(view.onFindBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        String refBooking = "refBooking";
        when(view.onReferenceBookingChanged()).thenReturn(Observable.just(refBooking));
        String lastName = "lastName";
        when(view.onLastNameChanged()).thenReturn(Observable.just(lastName));
        LocalDate calendarDay = LocalDate.of(2017, 4, 1);
        when(view.onArrivalDateSelected()).thenReturn(Observable.just(calendarDay));
        when(importBooking.execute(refBooking, lastName, calendarDay, "en", "gb"))
                .thenReturn(Single.error(new ImportBookingException(KEY_BOOKING_EXISTS)));

        presenter.attachView(view);

        verify(view).showLoading(true);
        verify(view).showLoading(false);
        verify(view).showErrorAlreadyImportedBooking();
        verify(analytics).trackError(ErrorBody.create(-1, ERROR_BOOKING_ALREADY_IMPORTED));
    }

    @Test
    public void onFindBookingClickImportPastBookingError() {
        when(view.onFindBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        String refBooking = "refBooking";
        when(view.onReferenceBookingChanged()).thenReturn(Observable.just(refBooking));
        String lastName = "lastName";
        when(view.onLastNameChanged()).thenReturn(Observable.just(lastName));
        LocalDate calendarDay = LocalDate.of(2017, 4, 1);
        when(view.onArrivalDateSelected()).thenReturn(Observable.just(calendarDay));

        when(importBooking.execute(refBooking, lastName, calendarDay, "en", "gb"))
                .thenReturn(Single.error(new ImportBookingException(KEY_PAST_BOOKING)));

        presenter.attachView(view);

        verify(view).showLoading(true);
        verify(view).showLoading(false);
        verify(view).showErrorPastBookingImportAttempt();
        verify(analytics).trackError(ErrorBody.create(-1, ERROR_PAST_BOOKING_IMPORT));
    }

    @Test
    public void onFindBookingClickUnableToRetrieveBookingError() {

        when(view.onFindBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        String refBooking = "refBooking";
        when(view.onReferenceBookingChanged()).thenReturn(Observable.just(refBooking));
        String lastName = "lastName";
        when(view.onLastNameChanged()).thenReturn(Observable.just(lastName));
        LocalDate calendarDay = LocalDate.of(2017, 4, 1);
        when(view.onArrivalDateSelected()).thenReturn(Observable.just(calendarDay));
        when(importBooking.execute(refBooking, lastName, calendarDay, "en", "gb"))
                .thenReturn(Single.error(new ImportBookingException(KEY_BOOKING_NOT_FOUND)));

        presenter.attachView(view);

        verify(view).showLoading(true);
        verify(view).showLoading(false);
        verify(view).showErrorUnableToRetrieveBooking();
        verify(analytics).trackError(ErrorBody.create(-1, ERROR_UNABLE_TO_RETRIEVE_BOOKING));
    }

    @Test
    public void onBusinessBookingImportError() {

        when(view.onFindBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        String refBooking = "refBooking";
        when(view.onReferenceBookingChanged()).thenReturn(Observable.just(refBooking));
        String lastName = "lastName";
        when(view.onLastNameChanged()).thenReturn(Observable.just(lastName));
        LocalDate calendarDay = LocalDate.of(2017, 4, 1);
        when(view.onArrivalDateSelected()).thenReturn(Observable.just(calendarDay));

        when(importBooking.execute(refBooking, lastName, calendarDay, "en", "gb"))
                .thenReturn(Single.error(BusinessBookingImportError.INSTANCE));

        presenter.attachView(view);

        verify(view).showLoading(true);
        verify(view).showLoading(false);
        verify(view).showBusinessBookingImportError();
        verify(analytics).trackError(ErrorBody.create(-1, ERROR_BUSINESS_BOOKING_IMPORT));
    }

    @Test
    public void onFindBookingClickError() {

        when(view.onFindBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        String refBooking = "refBooking";
        when(view.onReferenceBookingChanged()).thenReturn(Observable.just(refBooking));
        String lastName = "lastName";
        when(view.onLastNameChanged()).thenReturn(Observable.just(lastName));
        LocalDate calendarDay = LocalDate.of(2017, 4, 1);
        when(view.onArrivalDateSelected()).thenReturn(Observable.just(calendarDay));

        when(importBooking.execute(refBooking, lastName, calendarDay, "en", "gb"))
                .thenReturn(Single.error(new Exception()));

        presenter.attachView(view);

        verify(view).showLoading(true);
        verify(view).showLoading(false);
        verify(view).showErrorFetchingBooking();
        verify(analytics).trackError(ErrorBody.create(-1, ERROR_FETCHING_BOOKING));
    }

    @Test
    public void onFindBookingClickInvalidFields() {
        when(view.onFindBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        String refBooking = "";
        when(view.onReferenceBookingChanged()).thenReturn(Observable.just(refBooking));
        String lastName = "";
        when(view.onLastNameChanged()).thenReturn(Observable.just(lastName));
        when(view.onArrivalDateSelected()).thenReturn(Observable.never());

        presenter.attachView(view);

        verify(view).showBookingRefError(true);
        verify(view).showLastNameError(true);
        verify(view).showArrivalDateError(true);
    }
}
