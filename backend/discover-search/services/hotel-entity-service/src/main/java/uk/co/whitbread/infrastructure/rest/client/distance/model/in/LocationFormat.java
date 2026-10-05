package uk.co.whitbread.infrastructure.rest.client.distance.model.in;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.infrastructure.rest.client.distance.exception.HotelDistanceException;

@Slf4j
public enum LocationFormat {

  LAT_LONG("latlong"),
  PLACE_ID("placeId"),
  MANAGED_PLACE_ID("managedPlaceId");

  private final String value;

  LocationFormat(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static LocationFormat retrieveLocationFormat(String locationFormat) {
    return Arrays
        .stream(values())
        .filter(t -> t.getValue().equalsIgnoreCase(locationFormat)).findFirst()
        .orElseThrow(() -> {
          var message = "Invalid location format!";
          var exception = new HotelDistanceException(ErrorCode.DIGITAL_INVALID_LOCATION_EXCEPTION,
                  message);
          ExceptionLogger.log(log, exception);
          return exception;
        });

  }
}