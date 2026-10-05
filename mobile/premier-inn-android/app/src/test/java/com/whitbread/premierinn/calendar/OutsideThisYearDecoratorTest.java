package com.whitbread.premierinn.calendar;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.whitbread.premierinn.calendar.dialogcalendar.OutsideThisYearDecorator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.GregorianCalendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OutsideThisYearDecoratorTest {

    @Mock
    DayViewFacade dayViewFacadeMock;
    private CalendarDay today = CalendarDay.from(new GregorianCalendar(2015, 9, 3));
    private CalendarDay oneYearFromYesterday = CalendarDay.from(new GregorianCalendar(2016, 9, 2));

    private OutsideThisYearDecorator outsideThisYearDecorator;

    @Before
    public void onSetup() {
        outsideThisYearDecorator = OutsideThisYearDecorator.create(today, oneYearFromYesterday);
    }

    @Test
    public void testShouldDecorate() {
        boolean shouldDecorate = outsideThisYearDecorator.shouldDecorate(CalendarDay.from(new GregorianCalendar(2015, 10, 3)));
        assertFalse(shouldDecorate);

        shouldDecorate = outsideThisYearDecorator.shouldDecorate(CalendarDay.from(new GregorianCalendar(2015, 1, 3)));
        assertTrue(shouldDecorate);

        shouldDecorate = outsideThisYearDecorator.shouldDecorate(CalendarDay.from(new GregorianCalendar(2016, 9, 6)));
        assertTrue(shouldDecorate);
    }

    @Test
    public void testDecorate() {
        outsideThisYearDecorator.decorate(dayViewFacadeMock);

        verify(dayViewFacadeMock).setDaysDisabled(true);
    }
}
