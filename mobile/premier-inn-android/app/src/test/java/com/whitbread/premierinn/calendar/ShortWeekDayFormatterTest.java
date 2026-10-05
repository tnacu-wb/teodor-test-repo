package com.whitbread.premierinn.calendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;

import com.whitbread.premierinn.calendar.dialogcalendar.ShortWeekDayFormatter;

@RunWith(MockitoJUnitRunner.class)
public class ShortWeekDayFormatterTest {

    private ShortWeekDayFormatter shortWeekDayFormatter;

    @Before
    public void onSetup() {
        shortWeekDayFormatter = new ShortWeekDayFormatter();
    }

    @Test
    public void testFormat() {
        Locale.setDefault(new Locale("en", "GB"));
        List<String> daysOfWeek = new ArrayList<>();

        // Get the days of the week starting from Sunday
        for (int i = 1; i < 8; i++) {
            daysOfWeek.add(shortWeekDayFormatter.format(i).toString());
        }

        List<String> expectedDays = Arrays.asList("S", "M", "T", "W", "T", "F", "S");
        for (int i = 0; i < 7; i++) {
            // "EEEEE" on JVM doesn't take the first letter (as it does on Android device)
            // so getting the first character using the substring method
            assertEquals(expectedDays.get(i), daysOfWeek.get(i).substring(0, 1));
        }
    }
}



