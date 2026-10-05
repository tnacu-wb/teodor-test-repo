package uk.co.whitbread.booking.infrastructure.rest.client.reservation.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationResponseDto;

@UtilityClass
public class ReservationUtils {

  public static boolean isPastBooking(ReservationResponseDto response) {

    return response.getReservationByIdList().stream().map(reservation -> {
      var departureDate = reservation.getRoomStay().getDepartureDate();
      var checkOutTime = reservation.getRoomStay().getCheckOutTime();
      var departureDateTime = departureDate + " " + checkOutTime;
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
      LocalDateTime departureDatetime = LocalDateTime.parse(departureDateTime, formatter);

      return departureDatetime.isBefore(LocalDateTime.now());
    }).findFirst().orElseThrow();
  }
}
