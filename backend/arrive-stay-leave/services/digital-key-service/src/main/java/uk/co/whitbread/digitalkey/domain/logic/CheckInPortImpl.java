package uk.co.whitbread.digitalkey.domain.logic;

import static uk.co.whitbread.digitalkey.ErrorCode.Constants.RESERVATION_NOT_FOUND;
import static uk.co.whitbread.digitalkey.domain.logic.CommonConstants.CLEAN;
import static uk.co.whitbread.digitalkey.domain.logic.CommonConstants.DK_ISSUED;
import static uk.co.whitbread.digitalkey.domain.logic.CommonConstants.SUCCESS;
import static uk.co.whitbread.digitalkey.domain.utils.SanitizingUtils.sanitize;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.digitalkey.ErrorCode;
import uk.co.whitbread.digitalkey.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.digitalkey.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.digitalkey.domain.ports.primary.CharacterUdfInPort;
import uk.co.whitbread.digitalkey.domain.ports.primary.CheckInPort;
import uk.co.whitbread.digitalkey.domain.ports.secondary.CheckOutPort;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.model.AllocationResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.model.RoomAllocationRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.exceptions.CheckInRequestException;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.CheckInResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.ReservationByBasketRefResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.ReservationByIdDto;

@AllArgsConstructor
@Slf4j
public class CheckInPortImpl implements CheckInPort {

  private final CheckOutPort checkOutPort;
  private final CharacterUdfInPort characterUdfInPort;

  @Override
  public ReservationByIdDto getReservation(String hotelId, String reservationId) {
    log.info("log_check_in : Processing getReservation Call for ReservationId = {}, HotelId = {}",
              sanitize(reservationId), sanitize(hotelId));
    ReservationByBasketRefResponseDto basketResponse = checkOutPort.getReservation(hotelId, reservationId);

    if (basketResponse == null) {
      log.error("log_check_in : No Reservation found for ReservationId = {}, HotelId = {}",
              sanitize(reservationId), sanitize(hotelId));
      throw new CheckInRequestException(ErrorCode.RESERVATION_NOT_FOUND, RESERVATION_NOT_FOUND);
    }

    if (BigDecimal.ZERO.compareTo(basketResponse.getBalanceOutstanding()) != 0) {
      log.info("log_check_in : Outstanding balance is not 0 for ReservationId = {}, HotelId = {}",
                sanitize(reservationId), sanitize(hotelId));
      throw new CheckInRequestException(ErrorCode.CHECKIN_FAILED, "Outstanding balance is not 0");
    }

    return basketResponse.getReservationByIdList().stream()
        .filter(dto -> reservationId.equalsIgnoreCase(dto.getReservationId()) && dto.getRoomStay() != null)
        .findFirst()
        .orElseThrow(() -> {
          log.error(RESERVATION_NOT_FOUND);
          return new CheckInRequestException(ErrorCode.RESERVATION_NOT_FOUND, RESERVATION_NOT_FOUND);
        });
  }

  @Override
  public String allocateRoom(String hotelId, String reservationId, String roomType, String roomId) {

    RoomAllocationRequestDto roomAllocationRequestDto = new RoomAllocationRequestDto();
    roomAllocationRequestDto.setHotelId(hotelId);
    roomAllocationRequestDto.setReservationId(reservationId);
    roomAllocationRequestDto.setRoomType(roomType);
    roomAllocationRequestDto.setRoomId(roomId);

    log.info("log_check_in : Processing allocateRoom call for ReservationId = {}, HotelId = {}",
            sanitize(reservationId), sanitize(hotelId));
    AllocationResponseDto allocationResponse  = checkOutPort.allocateRoom(roomAllocationRequestDto);

    if (allocationResponse == null || isNullOrBlank(allocationResponse.getRoomId())
            || isNullOrBlank(allocationResponse.getStatus())) {
      log.error("log_check_in : Allocation response is invalid for ReservationId = {}, HotelId = {}",
                sanitize(reservationId), sanitize(hotelId));
      throw new CheckInRequestException(ErrorCode.NO_ROOMS_AVAILABLE, "Allocation response is invalid.");
    }

    if (!CLEAN.equalsIgnoreCase(allocationResponse.getStatus())) {
      log.error("log_check_in : Pre allocated room is not Clean for ReservationId = {}, HotelId = {}",
                sanitize(reservationId), sanitize(hotelId));
      throw new CheckInRequestException(ErrorCode.NO_ROOMS_AVAILABLE, "Pre allocated room is not ready.");
    }

    characterUdfInPort.updateUdfc20(reservationId, hotelId, DK_ISSUED);

    log.info("log_check_in : Allocate Room call processed successfully for ReservationId = {}, HotelId = {}",
            sanitize(reservationId), sanitize(hotelId));
    return allocationResponse.getRoomId();
  }

  @Override
  public CheckInResponseDto checkIn(String reservationId, String hotelId, String roomId) {
    log.info("log_check_in : Processing CheckIn call for ReservationId = {}, HotelId = {}, RoomId = {}",
            sanitize(reservationId), sanitize(hotelId), sanitize(roomId));
    CheckInRequest checkInRequest = new CheckInRequest();
    checkInRequest.setHotelId(hotelId);
    checkInRequest.setReservationNumber(reservationId);
    checkInRequest.setRoomId(roomId);
    CheckInResponse checkInResponse = checkOutPort.doCheckIn(checkInRequest);
    CheckInResponseDto checkInResponseDto = new CheckInResponseDto();
    checkInResponseDto.setRoomNumber(
            checkInResponse.getReservation().get(0).getRoomStay().getCurrentRoomInfo().getRoomId());
    checkInResponseDto.setCheckInStatus(SUCCESS);
    log.info("log_check_in : CheckIn call Completed Successfully for ReservationId = {}, HotelId = {}, RoomId = {}",
            sanitize(reservationId), sanitize(hotelId), sanitize(roomId));
    return checkInResponseDto;
  }

  public static boolean isNullOrBlank(String str) {
    return str == null || str.isBlank();
  }

}