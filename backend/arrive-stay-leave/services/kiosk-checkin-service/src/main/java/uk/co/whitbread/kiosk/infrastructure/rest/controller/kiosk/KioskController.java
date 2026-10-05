package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk;

import static uk.co.whitbread.kiosk.infrastructure.rest.client.config.KioskConstants.UDFC07;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.kiosk.domain.model.checkin.out.CharacterUDFs;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.HouseKeepingResponse;
import uk.co.whitbread.kiosk.domain.ports.primary.KioskInPort;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.mapper.AllocationResponseMapper;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.mapper.CheckInRequestMapper;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in.CheckInRequestDto;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in.RoomAllocationRequestDto;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.out.AllocationResponseDto;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.out.CheckInResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Slf4j
public class KioskController {

  private final KioskInPort kioskInPort;
  private final AllocationResponseMapper allocationResponseMapper;
  private final CheckInRequestMapper checkInRequestMapper;

  @PostMapping(value = "/kiosk/allocate", produces = MediaType.APPLICATION_JSON_VALUE)
  public AllocationResponseDto allocateController(
      @RequestBody RoomAllocationRequestDto request) {
    AllocationResponseDto allocationResponseDto = new AllocationResponseDto();
    if (null != request.getRoomId() && !request.getRoomId().isEmpty()) {
      HouseKeepingResponse houseKeepingResponse = kioskInPort.fetchHouseKeepingStatus(
          request.getHotelId(), request.getRoomId());
      allocationResponseDto.setRoomId(houseKeepingResponse.getRoomId());
      allocationResponseDto.setStatus(houseKeepingResponse.getStatus());
    } else {
      final var vacantRooms = kioskInPort.getVacantRooms(request.getHotelId(),
          request.getRoomType());
      final var reservationPreferencesResponse = kioskInPort.fetchReservationPreferences(
          request.getHotelId(), request.getReservationId());
      final var roomAllocationResponse = kioskInPort
          .allocateRooms(request.getHotelId(), request.getReservationId(), vacantRooms,
              request.getRoomType(), reservationPreferencesResponse);
      allocationResponseDto = allocationResponseMapper.toDto(roomAllocationResponse);
      allocationResponseDto.setStatus("Clean");
    }
    return allocationResponseDto;
  }

  @PostMapping(value = "/kiosk/checkIn", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public CheckInResponseDto checkInController(
      @RequestBody CheckInRequestDto checkInRequestDto) {
    final var checkInRequest = checkInRequestMapper.toCheckInRequestModel(checkInRequestDto);
    final var checkInResponse = kioskInPort.getCheckInResponse(checkInRequest);
    CheckInResponseDto checkInResponseDto = new CheckInResponseDto();
    checkInResponseDto.setRoomNumber(
        checkInResponse.getReservation().get(0).getRoomStay().getCurrentRoomInfo().getRoomId());
    checkInResponseDto.setNumberOfKeys(2);
    if (null != checkInResponse.getReservation().get(0)
        .getUserDefinedFields()) {
      List<CharacterUDFs> characterUDFs = checkInResponse.getReservation().get(0)
          .getUserDefinedFields().getCharacterUDFs();
      CharacterUDFs udfc07 = characterUDFs.stream()
          .filter(characterUDF -> characterUDF.getName().equals(UDFC07))
          .findFirst().orElse(new CharacterUDFs());
      checkInResponseDto.setWifiAccessCode(udfc07.getValue());
    }
    return checkInResponseDto;

  }

}
