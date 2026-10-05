package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ohip.domain.ports.primary.RoomAllocationInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipNotFoundException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper.HouseKeepingRoomStatusResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper.ReservationPreferencesResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper.RoomAllocationRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper.RoomAllocationResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper.VacantRoomResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.in.RoomAllocationRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.HouseKeepingResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.KioskReservationPreferencesDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.RoomAllocationResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.VacantRoomResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/rooms")
public class RoomAllocationController {

  private final RoomAllocationInPort roomAllocationInPort;
  private final VacantRoomResponseMapper vacantRoomResponseMapper;
  private final RoomAllocationRequestMapper roomAllocationRequestMapper;
  private final RoomAllocationResponseMapper roomAllocationResponseMapper;
  private final HouseKeepingRoomStatusResponseMapper houseKeepingRoomStatusResponseMapper;
  private final ReservationPreferencesResponseMapper reservationPreferencesResponseMapper;

  @Operation(summary = "Get Allocated Room ID")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = VacantRoomResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(value = "/getVacant", produces = MediaType.APPLICATION_JSON_VALUE)
  public VacantRoomResponseDto getVacantRooms(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("roomType") String roomType) {

    final var vacantRoomResponse = roomAllocationInPort.getVacantRoomIds(hotelId, roomType);

    return vacantRoomResponseMapper.toVacantRoomResponseDto(vacantRoomResponse);
  }


  @Operation(summary = "Allocating the roomId")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = RoomAllocationResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(value = "/allocate", produces = MediaType.APPLICATION_JSON_VALUE)
  public RoomAllocationResponseDto allocateRooms(
      @RequestBody @Valid RoomAllocationRequestDto allocateInDto) {
    final var allocationRequest = roomAllocationRequestMapper.toAllocateRequestModel(allocateInDto);

    final var roomAllocationResponse = roomAllocationInPort.allocateRoom(allocationRequest);

    return roomAllocationResponseMapper.toDto(roomAllocationResponse);
  }

  @Operation(summary = "Fetching the Housekeeping Status")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HouseKeepingResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/fetchHouseKeepingStatus", produces = MediaType.APPLICATION_JSON_VALUE)
  public HouseKeepingResponseDto fetchHouseKeepingRoomStatus(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("roomId") String roomId) {
    final var houseKeepingRoomStatusResponse = roomAllocationInPort.fetchHouseKeepingRoomStatus(
        hotelId, roomId);
    return houseKeepingRoomStatusResponseMapper.toHouseKeepingResponseDto(
        houseKeepingRoomStatusResponse, roomId);
  }

  @Operation(summary = "Fetching the Reservation Preferences")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = KioskReservationPreferencesDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/fetchReservationWithPreferences", produces = MediaType.APPLICATION_JSON_VALUE)
  public KioskReservationPreferencesDto fetchReservationWithPreference(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationId") String reservationId) {
    final var kioskReservationPreferences = roomAllocationInPort.fetchReservationWithPreference(
        hotelId, reservationId);
    return reservationPreferencesResponseMapper.toDto(kioskReservationPreferences);
  }

}
