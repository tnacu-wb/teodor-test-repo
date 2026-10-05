package com.whitbread.premierinn.calendar.dialogcalendar;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.whitbread.premierinn.common.format.DateFormat;

public class ShortTitleFormatter implements TitleFormatter {
    @Override
    public CharSequence format(CalendarDay day) {
        return DateFormat.SHORT_MONTH_YEAR.format(day.getDate());
    }
}
