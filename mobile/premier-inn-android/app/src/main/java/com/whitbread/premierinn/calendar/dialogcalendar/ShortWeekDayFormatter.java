package com.whitbread.premierinn.calendar.dialogcalendar;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;
import com.whitbread.premierinn.common.format.DateFormat;

import java.util.Calendar;
import java.util.Date;

public class ShortWeekDayFormatter implements WeekDayFormatter {

    @Override
    public CharSequence format(int dayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        Date date = CalendarDay.from(calendar).getDate();
        return DateFormat.SHORT_WEEKDAY.format(date);
    }
}
