package com.whitbread.premierinn.calendar.dialogcalendar;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

@AutoValue
public abstract class OutsideThisYearDecorator implements DayViewDecorator {

    public abstract CalendarDay today();
    public abstract CalendarDay oneYearFromYesterday();

    public static OutsideThisYearDecorator create(@NonNull CalendarDay today, @NonNull CalendarDay oneYearFromYesterday) {
        return new AutoValue_OutsideThisYearDecorator(today, oneYearFromYesterday);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.isBefore(today()) || day.isAfter(oneYearFromYesterday());
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setDaysDisabled(true);
    }
}
