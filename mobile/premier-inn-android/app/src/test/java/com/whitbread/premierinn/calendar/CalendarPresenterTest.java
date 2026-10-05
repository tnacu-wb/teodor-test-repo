package com.whitbread.premierinn.calendar;

import static com.whitbread.premierinn.data.common.Constants.DEFAULT_MAX_ARRIVAL_DATE;
import static com.whitbread.premierinn.data.common.Constants.EMPTY_STRING;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager;
import com.whitbread.premierinn.calendar.dialogcalendar.CalendarDialogInput;
import com.whitbread.premierinn.calendar.dialogcalendar.CalendarPresenter;
import com.whitbread.premierinn.calendar.dialogcalendar.CalendarViewContainer;
import com.whitbread.premierinn.calendar.dialogcalendar.OutsideThisYearDecorator;
import com.whitbread.premierinn.calendar.dialogcalendar.ShortTitleFormatter;
import com.whitbread.premierinn.calendar.dialogcalendar.ShortWeekDayFormatter;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.LocalDate;

import java.util.GregorianCalendar;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

@RunWith(MockitoJUnitRunner.class)
public class CalendarPresenterTest {

    @Mock
    CalendarViewContainer calendarViewContainerMock;
    @Mock
    CalendarPresenter.View viewMock;
    @Mock
    private Drawable leftArrowMaskMock;
    @Mock
    private Drawable rightArrowMaskMock;
    @Mock
    private TrackingAnalytics trackingAnalyticsMock;
    @Mock
    private SimplePersistenceManager simplePersistenceManager;
    @Mock
    private BusinessPersistenceManager businessPersistenceManager;

    private LocalDate selectedDay;
    private CalendarDay selectedCalendarDay;
    private LocalDate startOfThisMonth;
    private LocalDate endOfLastMonth;
    private LocalDate oneYearFromYesterday;

    private CalendarPresenter calendarPresenter;
    private CompositeDisposable viewCompositeDisposable = new CompositeDisposable();
    private CalendarDialogInput calendarDialogInput;
    private LocalDate rangeStartDate;
    private CalendarDay rangeStartCalendarDate;

    @Before
    public void onSetup() {
        when(simplePersistenceManager.getMaxArrivalDateLeisure()).thenReturn(DEFAULT_MAX_ARRIVAL_DATE);

        selectedDay = LocalDate.of(LocalDate.now().getYear(), 12, 15);
        rangeStartDate = LocalDate.of(LocalDate.now().getYear(), 9, 5);

        selectedCalendarDay = LocalDateUtilsKt.toCalendarDay(selectedDay);
        rangeStartCalendarDate = LocalDateUtilsKt.toCalendarDay(rangeStartDate);
        startOfThisMonth = rangeStartDate.withDayOfMonth(1);
        oneYearFromYesterday = LocalDate.now().minusDays(1).plusDays(365);
        endOfLastMonth = oneYearFromYesterday.withDayOfMonth(oneYearFromYesterday.lengthOfMonth());

        calendarDialogInput = CalendarDialogInput.create(rangeStartDate, selectedDay);
        when(businessPersistenceManager.getBusinessCustomerEmail()).thenReturn(EMPTY_STRING);
        calendarPresenter = new CalendarPresenter(
                trackingAnalyticsMock,
                viewCompositeDisposable,
                simplePersistenceManager,
                businessPersistenceManager);
        calendarPresenter.initParams(calendarDialogInput);
        when(viewMock.onTodayClick()).thenReturn(Observable.never());
        when(viewMock.onCancelCalendar()).thenReturn(Observable.never());
        when(viewMock.onMonthChanged()).thenReturn(Observable.never());
        when(viewMock.onDateSelected()).thenReturn(Observable.never());
        when(viewMock.getCurrentDate()).thenReturn(selectedCalendarDay);
    }

    @Test
    public void testInit() {
        calendarPresenter.attachView(viewMock);
        verify(viewMock).setMasks();
        verify(viewMock).setCalendarRanges(LocalDateUtilsKt.toCalendarDay(startOfThisMonth),
                LocalDateUtilsKt.toCalendarDay(endOfLastMonth));
        verify(viewMock).addDecorator(OutsideThisYearDecorator.create(rangeStartCalendarDate,
                LocalDateUtilsKt.toCalendarDay(oneYearFromYesterday)));
        verify(viewMock).setTitleFormatter(any(ShortTitleFormatter.class));
        verify(viewMock).setWeekDayFormatter(any(ShortWeekDayFormatter.class));
    }

    @Test
    public void testSetTodayLabelInvisibleForCurrentMonth() {
        when(viewMock.onMonthChanged()).thenReturn(Observable.just(CalendarDay.today()));

        calendarPresenter.attachView(viewMock);

        if (CalendarDay.today().getMonth() == selectedCalendarDay.getMonth()
                && CalendarDay.today().getYear() == selectedCalendarDay.getYear()) {
            verify(viewMock, times(2)).setTodayLabelVisibility(false);
        } else {
            verify(viewMock, times(1)).setTodayLabelVisibility(false);
        }
    }

    @Test
    public void testSetTodayLabelVisible() {
        when(viewMock.onMonthChanged()).thenReturn(Observable.just(
                CalendarDay.from(selectedDay.getYear() - 1, selectedDay.getMonthValue(), selectedDay.getDayOfMonth())));

        calendarPresenter.attachView(viewMock);

        if (CalendarDay.today().getMonth() != selectedCalendarDay.getMonth()) {
            verify(viewMock, times(2)).setTodayLabelVisibility(true);
        } else {
            verify(viewMock, times(1)).setTodayLabelVisibility(true);
        }
    }

    @Test
    public void testNavigateToCorrectMonthAhead() {
        when(viewMock.getCurrentDate()).thenReturn(CalendarDay.from(new GregorianCalendar(LocalDate.now().minusYears(1).getYear(), 11, 1)));
        calendarPresenter.attachView(viewMock);

        verify(viewMock, times(12)).goToNextMonth();
    }

    @Test
    public void testNavigateToCorrectMonthPrevious() {
        when(viewMock.getCurrentDate()).thenReturn(CalendarDay.from(new GregorianCalendar(LocalDate.now().plusYears(1).getYear(), 11, 1)));
        calendarPresenter.attachView(viewMock);

        verify(viewMock, times(12)).goToPreviousMonth();
    }

    @Test
    public void testOnCancelCalendar() {
        when(viewMock.onCancelCalendar()).thenReturn(Observable.just(Unit.INSTANCE));
        calendarPresenter.attachView(viewMock);
        verify(viewMock).close();
    }


    @Test
    public void testLifeCycle() {
        assertFalse(calendarPresenter.isViewAttached());
        assertEquals(0, viewCompositeDisposable.size());
        calendarPresenter.attachView(viewMock);
        assertTrue(calendarPresenter.isViewAttached());
        assertEquals(4, viewCompositeDisposable.size());
        calendarPresenter.detachView();
        assertFalse(calendarPresenter.isViewAttached());
        assertEquals(0, viewCompositeDisposable.size());
        calendarPresenter.destroy();
    }
}
