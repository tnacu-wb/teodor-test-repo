package com.whitbread.premierinn.calendar.dialogcalendar;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type;
import static com.whitbread.premierinn.common.utils.StringUtils.EMPTY_STRING;

import androidx.annotation.NonNull;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager;
import com.whitbread.premierinn.calendar.LocalDateUtilsKt;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager;

import org.threeten.bp.LocalDate;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

@ActivityRetainedScoped
public class CalendarPresenter extends Presenter<CalendarPresenter.View> {

    private static final int MONTHS_IN_YEAR = 12;
    private final TrackingAnalytics analytics;
    private final CompositeDisposable compositeDisposableView;
    private LocalDate selectedDate;
    private LocalDate oneYearFromYesterday;
    private LocalDate startOfThisMonth;
    private LocalDate endOfLastMonth;
    private LocalDate rangeStartDay;
    private Integer maxArrivalDate;

    @Inject
    public CalendarPresenter(@NonNull TrackingAnalytics trackingAnalytics,
                             @NonNull CompositeDisposable compositeDisposable,
                             @NonNull SimplePersistenceManager simplePersistenceManager,
                             @NonNull BusinessPersistenceManager businessPersistenceManager) {
        this.analytics = trackingAnalytics;
        this.compositeDisposableView = compositeDisposable;
        boolean isInnBusiness = !businessPersistenceManager.getBusinessCustomerEmail().equals(EMPTY_STRING);
        maxArrivalDate = isInnBusiness ? businessPersistenceManager.getMaxArrivalDateInnBusiness()
                : simplePersistenceManager.getMaxArrivalDateLeisure();
    }

    public void initParams(CalendarDialogInput calendarDialogInput) {
        this.rangeStartDay = calendarDialogInput.rangeStartDate();
        this.selectedDate = calendarDialogInput.selectedDate();
        calculateCalendarRanges();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Presenter
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onAttachView(View view) {
        view.setMasks();
        view.setCalendarRanges(LocalDateUtilsKt.toCalendarDay(startOfThisMonth), LocalDateUtilsKt.toCalendarDay(endOfLastMonth));
        navigateToCorrectMonth(selectedDate, view);
        view.setSelectedDate(LocalDateUtilsKt.toCalendarDay(selectedDate));
        view.addDecorator(OutsideThisYearDecorator.create(
                LocalDateUtilsKt.toCalendarDay(rangeStartDay),
                LocalDateUtilsKt.toCalendarDay(oneYearFromYesterday)));
        view.setTitleFormatter(new ShortTitleFormatter());
        view.setWeekDayFormatter(new ShortWeekDayFormatter());
        setTodayLabel(LocalDateUtilsKt.toCalendarDay(selectedDate), view);
        analytics.track(ScreenState.CALENDAR, Type.LOOK_TO_BOOK);
        compositeDisposableView.add(view.onCancelCalendar().subscribe(__ -> view.close()));
        compositeDisposableView.add(view.onTodayClick().subscribe(__ -> navigateToCorrectMonth(LocalDate.now(), view)));
        compositeDisposableView.add(view.onMonthChanged().subscribe((calendarDay) -> setTodayLabel(calendarDay, view)));
        compositeDisposableView.add(view.onDateSelected().subscribe(view::setReturnedDate));
    }

    @Override
    public void onDetachView() {
        if (!compositeDisposableView.isDisposed()) {
            compositeDisposableView.clear();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void calculateCalendarRanges() {
        startOfThisMonth = rangeStartDay.withDayOfMonth(1);
        oneYearFromYesterday = LocalDate.now().minusDays(1).plusDays(maxArrivalDate + 1);
        endOfLastMonth = oneYearFromYesterday.withDayOfMonth(oneYearFromYesterday.lengthOfMonth());
    }

    private void setTodayLabel(@NonNull CalendarDay calendarDay, @NonNull View view) {
        CalendarDay calendarDayToday = CalendarDay.today();
        if (calendarDay.getMonth() == calendarDayToday.getMonth()
                && calendarDay.getYear() == calendarDayToday.getYear()) {
            view.setTodayLabelVisibility(false);
        } else {
            view.setTodayLabelVisibility(true);
        }
    }

    private void navigateToCorrectMonth(@NonNull LocalDate dayToNavigate, @NonNull View view) {
        CalendarDay currentCalendarDay = view.getCurrentDate();
        int currentMonth = currentCalendarDay.getMonth();
        int currentYear = currentCalendarDay.getYear();
        CalendarDay calendarDateToNavigate = LocalDateUtilsKt.toCalendarDay(dayToNavigate);
        int monthToNavigate = calendarDateToNavigate.getMonth();
        int yearToNavigate = calendarDateToNavigate.getYear();
        int deltaMonths = (yearToNavigate - currentYear) * MONTHS_IN_YEAR + (monthToNavigate - currentMonth);
        while (deltaMonths != 0) {
            if (compareCalendarDayDates(currentCalendarDay, calendarDateToNavigate)) {
                view.goToPreviousMonth();
                deltaMonths++;
            } else {
                view.goToNextMonth();
                deltaMonths--;
            }
        }
    }

    private boolean compareCalendarDayDates(@NonNull CalendarDay currentCalendarDay, @NonNull CalendarDay dayToNavigate) {
        if (currentCalendarDay.getYear() == dayToNavigate.getYear()) {
            return currentCalendarDay.getMonth() > dayToNavigate.getMonth();
        } else if (currentCalendarDay.getYear() > dayToNavigate.getYear()) {
            return true;
        }
        return false;
    }

    public interface View extends PresenterView {
        void setMasks();

        void setCalendarRanges(@NonNull CalendarDay startOfThisMonth, @NonNull CalendarDay endOfLastMonth);

        void setSelectedDate(@NonNull CalendarDay calendarDay);

        void addDecorator(@NonNull OutsideThisYearDecorator outsideThisYearDecorator);

        void setTitleFormatter(@NonNull ShortTitleFormatter titleFormatter);

        void setWeekDayFormatter(@NonNull ShortWeekDayFormatter shortWeekDayFormatter);

        void setTodayLabelVisibility(boolean visibility);

        CalendarDay getCurrentDate();

        void goToPreviousMonth();

        void goToNextMonth();

        void close();

        Observable<Unit> onTodayClick();

        Observable<Unit> onCancelCalendar();

        Observable<CalendarDay> onMonthChanged();

        Observable<CalendarDay> onDateSelected();

        void setReturnedDate(@NonNull CalendarDay calendarDay);
    }
}
