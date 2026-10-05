package uk.co.whitbread.bart.util.adapter;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by KrakenDevTeam on 30/11/2016.
 */
public class DateTimeBinder {

    private static final String FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static LocalDateTime parseDateTime(String dateAsString) {
        if (StringUtils.isNotBlank(dateAsString)) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(FORMAT_PATTERN);
            return LocalDateTime.parse(dateAsString, fmt);
        }
        return null;
    }

    public static String printDateTime(LocalDateTime datetime) {
        if (datetime != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(FORMAT_PATTERN);
            return datetime.format(fmt);
        }
        return null;
    }
}
