package uk.co.whitbread.marketing.utils;

import org.springframework.util.StringUtils;

public final class BartResponseValidatorUtils {

    public static final String ERROR_EMPTY_RESPONSE = "Bart returned empty response.";

    private BartResponseValidatorUtils() {
    }

    public static String checkErrorMessage(String errorDetail) {
        return StringUtils.hasLength(errorDetail) ? errorDetail : null;
    }
}
