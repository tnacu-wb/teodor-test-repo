package com.whitbread.premierinn.calendar;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.whitbread.premierinn.calendar.dialogcalendar.ShortTitleFormatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.GregorianCalendar;

import static junit.framework.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ShortTitleFormatterTest {

    private ShortTitleFormatter shortTitleFormatter;

    @Before
    public void onSetup() {
        shortTitleFormatter = new ShortTitleFormatter();
    }

    @Test
    public void testFormat() {
        CalendarDay day = CalendarDay.from(new GregorianCalendar(2040, 5, 7));

        String dateString = shortTitleFormatter.format(day).toString();

        assertEquals("Jun 2040", dateString);
    }
}
