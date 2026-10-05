package uk.co.whitbread.shared.azureemail.util.adapter;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by KrakenDevTeam on 30/11/2016.
 */
public class DateBinder {
    private static final String FORMAT_PATTERN = "yyyy-MM-dd";

    public static LocalDate parseDate(String dateAsString)
    {
        if (StringUtils.isNotBlank(dateAsString))
        {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(FORMAT_PATTERN);
            return LocalDate.parse(dateAsString, fmt);
        }
        return null;
    }

    public static String printDate(LocalDate date)
    {
        if (date != null)
        {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(FORMAT_PATTERN);
            return date.format(fmt);
        }
        return null;
    }
}
