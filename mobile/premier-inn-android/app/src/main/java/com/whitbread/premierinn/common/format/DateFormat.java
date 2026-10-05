package com.whitbread.premierinn.common.format;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class DateFormat {

    private DateFormat() {
    }

    public static final String DASHED_YEAR_MONTH_DAY = "yyyy-MM-dd";
    public static final String WEEKDAY_DAY_MONTH = "EEE d MMM";
    public static final String DAY_DATE_MONTH_YEAR = "EEE d MMM, yyyy";
    public static final String DAY_DATE_MONTH_FULL_NAME = "EEE d MMMM";
    public static final String SLASHED_DAY_MONTH_YEAR = "dd/MM/yyyy";
    public static final String SLASHED_YEAR_MONTH_DAY = "yyyy/MM/dd";
    public static final String SHORT_DATE_MONTH = "d MMM";
    public static final String DAY_DATE_MONTH = "EEE dd MMM";
    public static final String MONTH_YEAR_FORMAT = "MMMM yyyy";
    public static final String DAY_MONTH_FORMAT = "d MMM";
    public static final String DATE_TIME_WITH_OFFSET = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final SimpleDateFormat SHORT_MONTH_YEAR = new SimpleDateFormat("LLL yyyy", Locale.getDefault());
    public static final SimpleDateFormat SHORT_WEEKDAY = new SimpleDateFormat("EEEEE", Locale.getDefault());
    public static final SimpleDateFormat MONTH_YEAR_DIGITS = new SimpleDateFormat("MMyy", Locale.getDefault());
    public static final SimpleDateFormat HOUR_AND_MINUTES = new SimpleDateFormat("HH:mm", Locale.getDefault());
    public static final SimpleDateFormat HOUR_AND_MINUTES_AND_SECONDS = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat MONTH_YEAR = new SimpleDateFormat("MM/yy", Locale.getDefault());

    static {
        // Stops months > 12 being accepted (default behaviour is to subtract 12 and add a year)
        MONTH_YEAR_DIGITS.setLenient(false);
        MONTH_YEAR.setLenient(false);
    }
}
