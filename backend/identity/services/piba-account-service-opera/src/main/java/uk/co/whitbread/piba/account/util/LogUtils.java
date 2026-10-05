package uk.co.whitbread.piba.account.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogUtils {

    public static String sanitisedStringWithMaxLengthLimit(final String stringToBeSanitised, final int limit) {

        if (stringToBeSanitised == null) {
            return null;
        }

        // Allow only alphanumeric characters and a limited set of safe symbols.
        String sanitisedString = stringToBeSanitised.replaceAll("[^a-zA-Z0-9 .,_-]", "");
        return sanitisedString.substring(0, Math.min(limit, sanitisedString.length()));
    }
}
