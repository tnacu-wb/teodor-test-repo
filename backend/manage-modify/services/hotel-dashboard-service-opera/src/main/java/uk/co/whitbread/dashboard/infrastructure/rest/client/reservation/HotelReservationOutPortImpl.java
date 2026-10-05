package uk.co.whitbread.dashboard.infrastructure.rest.client.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.dashboard.domain.model.in.FindBookingResponse;
import uk.co.whitbread.dashboard.domain.model.in.ManageBookingResponse;
import uk.co.whitbread.dashboard.domain.ports.secondary.HotelReservationsOutPort;
import uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.mapper.FindBookingResponseMapper;
import uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.mapper.ManageBookingInfoMapper;
import uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.service.HotelReservationClient;

@Component
@RequiredArgsConstructor
public class HotelReservationOutPortImpl implements HotelReservationsOutPort {

  private final HotelReservationClient hotelReservationClient;
  private final FindBookingResponseMapper findBookingResponseMapper;
  private final ManageBookingInfoMapper manageBookingInfoMapper;

  @Override
  public FindBookingResponse findBooking(String lastName, LocalDate arrivalDate,
      String bookingReference, String language) {
    return findBookingResponseMapper.toModel(
        hotelReservationClient.findBooking(lastName, arrivalDate, bookingReference, language));
  }

  @Override
  public ManageBookingResponse getManageBookingInfo(String basketReference, String hotelId,
      String token, String language, LocalDateTime userDateTime) {
    return manageBookingInfoMapper.toModel(
        hotelReservationClient.getManageBookingInfo(basketReference, hotelId, token, language, userDateTime));
  }
}
