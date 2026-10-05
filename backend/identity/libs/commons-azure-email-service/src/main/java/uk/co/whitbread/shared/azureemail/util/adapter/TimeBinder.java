package uk.co.whitbread.shared.azureemail.util.adapter;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by KrakenDevTeam on 30/11/2016.
 */
public class TimeBinder {

    private static final String FORMAT_PATTERN = "HH:mm:ss";

    public static LocalTime parseTime(String timeAsString)
    {
        if (StringUtils.isNotBlank(timeAsString))
        {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(FORMAT_PATTERN);
            return LocalTime.parse(timeAsString, fmt);
        }
        return null;
    }

    public static String printTime(LocalTime time)
    {
        if (time != null)
        {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(FORMAT_PATTERN);
            return time.format(fmt);
        }
        return null;
    }
}
