package uk.co.whitbread.hotel.account.utils.account.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public final class StringToLocalDate {

    private static final Pattern PATTERN1 = Pattern.compile("^([0-2][0-9]||3[0-1])\\s+(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s+\\d{4}$");
    private static final Pattern PATTERN2 = Pattern.compile("^([0-2][0-9]||3[0-1])\\s+(January|February|March|April|June|July|August|September|October|November|December)\\s+\\d{4}$");

    public static LocalDate convert(Object destination, Object source) {
        if (source == null) {
            return null;
        }
        if (source instanceof String) {
            if (PATTERN1.matcher((String) source).matches()) {
                return LocalDate.parse((String) source, DateTimeFormatter.ofPattern("d MMM yyyy"));
            }

            if (PATTERN2.matcher((String) source).matches()) {
                return LocalDate.parse((String) source, DateTimeFormatter.ofPattern("d MMMM yyyy"));
            }
        }

        throw new IllegalArgumentException(String.format("Converter StringToLocalDate "
                + "used incorrectly. Arguments passed in were: %s and %s . \n" +
                "Expected types [LocalDate] and [String.class]", destination, source));
    }
}
