package uk.co.whitbread.reservation.domain.model.validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.apache.commons.lang3.StringUtils;

public class ValidationUtils {
  private static final Integer PI_MAXIMUM_RANGE = 9;
  private static final Integer BB_MAXIMUM_RANGE = 14;
  private static final Integer CCUI_MAXIMUM_RANGE = 364;
  private static final Integer DISTR_MAXIMUM_RANGE = 364;
  private static final Integer MAX_DAYS_PAST = 7;
  private static final Integer MAX_DAYS_FUTURE = 365;

  public static boolean validDates(String channel, LocalDate startDate, LocalDate endDate) {
    if (StringUtils.isNotEmpty(channel)) {
      var maxRange = switch (channel) {
        case "PI" -> PI_MAXIMUM_RANGE;
        case "BB" -> BB_MAXIMUM_RANGE;
        case "CCUI" -> CCUI_MAXIMUM_RANGE;
        case "DISTR" -> DISTR_MAXIMUM_RANGE;
        default -> 0;
      };

      long range = ChronoUnit.DAYS.between(startDate, endDate);

      return range <= maxRange
          && range >= 0
          && !startDate.isBefore(LocalDate.now().minusDays(MAX_DAYS_PAST))
          && !endDate.isAfter(LocalDate.now().plusDays(MAX_DAYS_FUTURE));
    }

    return true;
  }

}
