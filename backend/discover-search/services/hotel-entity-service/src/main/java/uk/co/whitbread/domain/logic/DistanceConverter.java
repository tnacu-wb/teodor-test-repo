package uk.co.whitbread.domain.logic;

import java.text.DecimalFormat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class DistanceConverter {

  private static final double METERS_TO_MILES = 0.000621371;
  private static final double METERS_TO_KM = 0.001;
  private static final String MILES = "mi";
  private static final String KILOMETERS = "km";
  private static final DecimalFormat df = new DecimalFormat("#.##");

  public static double convertDistance(int distance, String radiusUnit) {
    log.debug("Entered convertDistance with distance={}, radiusUnit={}",
        distance, radiusUnit);

    double convertedDistance = 0;

    if (KILOMETERS.equals(radiusUnit)) {
      log.trace("Converting {} meters to kilometers", distance);
      convertedDistance = distance * METERS_TO_KM;
    } else if (MILES.equals(radiusUnit)) {
      log.trace("Converting {} meters to miles", distance);
      convertedDistance = distance * METERS_TO_MILES;
    }
    var result = Double.parseDouble(df.format(convertedDistance));

    log.debug("Converted {} meters to {} {}", distance, result, radiusUnit);
    return result;
  }
}
