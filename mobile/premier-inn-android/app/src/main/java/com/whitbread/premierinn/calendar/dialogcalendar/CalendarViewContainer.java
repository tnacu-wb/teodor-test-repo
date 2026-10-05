package com.whitbread.premierinn.calendar.dialogcalendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;

import com.jakewharton.rxbinding3.view.RxView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.whitbread.premierinn.calendar.LocalDateUtilsKt;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.format.DateFormat;
import com.whitbread.premierinn.common.format.FormatExtensionsKt;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.databinding.DialogCalendarBinding;

import org.threeten.bp.LocalDate;

import io.reactivex.Observable;
import kotlin.Unit;

public class CalendarViewContainer extends ViewContainer implements CalendarPresenter.View {

    private final DialogCalendarBinding binding;
    private final Drawable leftArrowMask;
    private final Drawable rightArrowMask;


    public CalendarViewContainer(@NonNull BaseActivity activity, @NonNull Drawable leftArrowMask,
                                 @NonNull Drawable rightArrowMask,  DialogCalendarBinding binding) {
        super(activity);
        this.leftArrowMask = leftArrowMask;
        this.rightArrowMask = rightArrowMask;
        this.binding = binding;
    }

    @Override
    public void setMasks() {
        binding.viewCalendar.setLeftArrowMask(leftArrowMask);
        binding.viewCalendar.setRightArrowMask(rightArrowMask);
    }

    @Override
    public void setCalendarRanges(@NonNull CalendarDay startOfThisMonth, @NonNull CalendarDay endOfLastMonth) {
        binding.viewCalendar.state().edit()
                .setMinimumDate(startOfThisMonth)
                .setMaximumDate(endOfLastMonth)
                .commit();
    }

    @Override
    public void setSelectedDate(@NonNull CalendarDay day) {
        binding.viewCalendar.setSelectedDate(day);
        binding.labelTodayDate.setText(FormatExtensionsKt.format(day, DateFormat.WEEKDAY_DAY_MONTH));
    }

    @Override
    public void addDecorator(@NonNull OutsideThisYearDecorator outsideThisYearDecorator) {
        binding.viewCalendar.addDecorator(outsideThisYearDecorator);
    }

    @Override
    public void setTitleFormatter(@NonNull ShortTitleFormatter shortTitleFormatter) {
        binding.viewCalendar.setTitleFormatter(shortTitleFormatter);
    }

    @Override
    public void setWeekDayFormatter(@NonNull ShortWeekDayFormatter shortWeekDayFormatter) {
        binding.viewCalendar.setWeekDayFormatter(shortWeekDayFormatter);
    }

    @Override
    public Observable<CalendarDay> onDateSelected() {
        return Observable.create(emitter ->
                binding.viewCalendar.setOnDateChangedListener((widget, date, selected) -> emitter.onNext(date)));
    }

    @Override
    public void setReturnedDate(@NonNull CalendarDay calendarDay) {
        LocalDate localDate = LocalDateUtilsKt.toLocalDate(calendarDay);
        getActivity().setResult(Activity.RESULT_OK, new Intent().putExtra(CalendarDialogActivity.CHOSEN_DATE, localDate));
        getActivity().finish();
    }

    @Override
    public Observable<CalendarDay> onMonthChanged() {
        return Observable.create(emitter -> {
            // TODO create a disposable that remove this listener
            binding.viewCalendar.setOnMonthChangedListener(((widget, date) -> emitter.onNext(date)));
        });
    }

    @Override
    public void setTodayLabelVisibility(boolean visible) {
        binding.textviewCalendarTodayButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void goToPreviousMonth() {
        binding.viewCalendar.goToPrevious();
    }

    @Override
    public void goToNextMonth() {
        binding.viewCalendar.goToNext();
    }

    @Override
    public CalendarDay getCurrentDate() {
        return binding.viewCalendar.getCurrentDate();
    }

    @Override
    public void close() {
        getActivity().finish();
    }

    @Override
    public Observable<Unit> onTodayClick() {
        return RxView.clicks(binding.textviewCalendarTodayButton);
    }

    @Override
    public Observable<Unit> onCancelCalendar() {
        return RxView.clicks(binding.viewCancelCalendar);
    }
}
