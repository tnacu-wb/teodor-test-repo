package com.whitbread.premierinn.utils;

import com.whitbread.premierinn.common.utils.DateUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.LocalDate;

import java.text.ParseException;

import static junit.framework.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DateUtilsTest {

    @Test
    public void leadDaysWithLateBooking() throws ParseException {
        assertEquals(2, DateUtils.getLeadDays(LocalDate.of(2017, 1, 1), LocalDate.of(2016, 12, 30)));
    }

    @Test
    public void leadDaysOnLeapYear() {
        assertEquals(3, DateUtils.getLeadDays(LocalDate.of(2020, 3, 1), LocalDate.of(2020, 2, 27)));
    }

    @Test
    public void leadDaysOnDaylightSavingsStarting() {
        assertEquals(1, DateUtils.getLeadDays(LocalDate.of(2017, 3, 27), LocalDate.of(2017, 3, 26)));
    }

    @Test
    public void leadDaysOnDaylightSavingsEnding() {
        assertEquals(0, DateUtils.getLeadDays(LocalDate.of(2017, 10, 29), LocalDate.of(2017, 10, 29)));
    }
}