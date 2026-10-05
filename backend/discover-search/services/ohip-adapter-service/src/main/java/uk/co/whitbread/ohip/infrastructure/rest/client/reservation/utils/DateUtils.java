package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils;

import static uk.co.whitbread.ohip.ErrorCode.DIGITAL_FORMAT_DATE_RESERVATION_EXCEPTION;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;

@Slf4j
public class DateUtils {

  public static Date getDateFromString(String dateAsString, String pattern) {
    try {
      return new SimpleDateFormat(pattern).parse(dateAsString);
    } catch (ParseException e) {
      var exception = new HotelReservationException(DIGITAL_FORMAT_DATE_RESERVATION_EXCEPTION,
          String.format("Could not parse date from %s", dateAsString));
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }
}
