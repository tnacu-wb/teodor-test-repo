package com.whitbread.premierinn.common.utils;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.TextStyle;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;

public final class DateUtils {

    public static String getWeekDay(@NonNull LocalDate date) {
        return date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.UK);
    }

    /**
     * @return Amount of days between booking and arrival
     */
    public static int getLeadDays(@NonNull LocalDate arrivalDate, @NonNull LocalDate bookingDate) {
        return (int) ChronoUnit.DAYS.between(bookingDate, arrivalDate);
    }

    public static Date resetDayToFirstOfTheMonth(long millis) {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(millis);

        currentDate.set(Calendar.DAY_OF_MONTH, 1);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        return currentDate.getTime();
    }

    public static LocalDate date6MonthsInPast() {
        return LocalDate.now().minusMonths(6);
    }
}