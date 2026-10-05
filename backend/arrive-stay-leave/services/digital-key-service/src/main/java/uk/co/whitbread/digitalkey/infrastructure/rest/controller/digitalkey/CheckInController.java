package uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey;

import static uk.co.whitbread.digitalkey.domain.logic.CommonConstants.DK_ISSUED;
import static uk.co.whitbread.digitalkey.domain.logic.CommonConstants.INHOUSE;
import static uk.co.whitbread.digitalkey.domain.logic.CommonConstants.SUCCESS;
import static uk.co.whitbread.digitalkey.domain.utils.SanitizingUtils.sanitize;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.digitalkey.domain.ports.primary.CharacterUdfInPort;
import uk.co.whitbread.digitalkey.domain.ports.primary.CheckInPort;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.in.CheckInRequestDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.CheckInResponseDto;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.ReservationByIdDto;

/**
 * Controller responsible for handling digital key check-in operations.
 * Provides endpoints to initiate check-in, allocate rooms, and validate reservation status.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Slf4j
public class CheckInController {

  private final CheckInPort checkInPort;
  private final CharacterUdfInPort characterUdfInPort;

  /**
   * Endpoint to initiate the check-in process. This method retrieves reservation details, attempts room allocation,
   * and performs check-in based on the reservation status.
   */
  @PostMapping(value = "/digital-key/checkIn", produces = MediaType.APPLICATION_JSON_VALUE)
  public CheckInResponseDto checkInEndPoint(@Valid @RequestBody CheckInRequestDto checkInRequestDto) {

    String hotelId = checkInRequestDto.getHotelId();
    String reservationId = checkInRequestDto.getReservationId();

    ReservationByIdDto reservation = checkInPort.getReservation(hotelId, reservationId);
    String reservationStatus = reservation.getReservationStatus();
    String roomType = reservation.getRoomStay().getRoomType();
    String roomId = reservation.getRoomStay().getRoomNumber();
    log.info("log_check_in : roomType: {}, reservationStatus: {}, roomId: {}", roomType, reservationStatus, roomId);

    if (INHOUSE.equalsIgnoreCase(reservationStatus)) {
      log.info("log_check_in : InHouse for Reservation = {}, HotelId = {}", sanitize(reservationId), sanitize(hotelId));
      characterUdfInPort.updateUdfc20(reservationId, hotelId, DK_ISSUED);
      return new CheckInResponseDto(roomId, SUCCESS);
    }

    String allocateRoomId = checkInPort.allocateRoom(hotelId, reservationId, roomType, roomId);

    return checkInPort.checkIn(reservationId, hotelId, allocateRoomId);
  }

}