package uk.co.whitbread.ondemandrefreshservice.infrastructure.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@UtilityClass
@Slf4j
public class SanitizingUtils {

    private static final String CHARACTER_REGEX = "[\\r\\n]";
    private static final String CONTROL_CHARACTER_REGEX = "\\p{Cntrl}";

    public static String sanitize(final Object input) {
        if (!Objects.isNull(input)) {
            try {
                return input.toString().replaceAll(CHARACTER_REGEX, "")
                        .replaceAll(CONTROL_CHARACTER_REGEX, "");
            } catch (Exception ex) {
                log.debug("Error sanitizing input: {}", ex.getMessage());
            }
        }
        return "";
    }
}
