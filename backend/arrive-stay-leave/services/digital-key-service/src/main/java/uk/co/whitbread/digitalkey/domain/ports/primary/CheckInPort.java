package uk.co.whitbread.digitalkey.domain.ports.primary;

import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.CheckInResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.ReservationByIdDto;

public interface CheckInPort {

  ReservationByIdDto getReservation(String hotelId, String reservationId);

  String allocateRoom(String hotelId, String reservationId, String roomType, String roomId);

  CheckInResponseDto checkIn(String reservationId, String hotelId, String roomId);

}