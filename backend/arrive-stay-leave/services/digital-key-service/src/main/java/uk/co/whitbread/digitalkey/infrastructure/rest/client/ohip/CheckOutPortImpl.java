package uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.digitalkey.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.digitalkey.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.digitalkey.domain.ports.secondary.CheckOutPort;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.model.AllocationResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.model.RoomAllocationRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.service.KioskAdapterClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.mapper.KioskCheckInRequestMapper;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.ReservationByBasketRefResponseDto;

@RequiredArgsConstructor
@Slf4j
public class CheckOutPortImpl implements CheckOutPort {

  private final KioskCheckInRequestMapper kioskCheckInRequestMapper;
  private final OhipAdapterClient ohipAdapterClient;
  private final KioskAdapterClient kioskAdapterClient;

  @Override
  public ReservationByBasketRefResponseDto getReservation(String hotelId, String reservationId) {
    return ohipAdapterClient.getReservationDetails(hotelId, reservationId);
  }

  @Override
  public AllocationResponseDto allocateRoom(RoomAllocationRequestDto roomAllocationRequestDto) {
    return kioskAdapterClient.allocateRoom(roomAllocationRequestDto);
  }

  @Override
  public CheckInResponse doCheckIn(CheckInRequest checkInRequest) {
    final var kioskCheckInRequest = kioskCheckInRequestMapper.toKioskCheckInRequestModel(checkInRequest);
    return ohipAdapterClient.doCheckIn(kioskCheckInRequest);
  }

}