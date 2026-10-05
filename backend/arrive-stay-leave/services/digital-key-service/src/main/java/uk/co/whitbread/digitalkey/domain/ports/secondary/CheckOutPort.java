package uk.co.whitbread.digitalkey.domain.ports.secondary;

import uk.co.whitbread.digitalkey.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.digitalkey.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.model.AllocationResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.model.RoomAllocationRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.ReservationByBasketRefResponseDto;

public interface CheckOutPort {

  ReservationByBasketRefResponseDto getReservation(String hotelId, String reservationId);

  AllocationResponseDto allocateRoom(RoomAllocationRequestDto roomAllocationRequestDto);

  CheckInResponse doCheckIn(CheckInRequest checkInRequest);

}