package uk.co.whitbread.ohip.infrastructure.rest.controller.hotel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ohip.domain.ports.primary.HotelInfoInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipNotFoundException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.mapper.HotelInfoDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.mapper.RoomTypesInfoDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.model.out.HotelInfoDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.model.out.RoomTypesInfoDto;

@RestController
@RequiredArgsConstructor
public class HotelInfoController {

  private final HotelInfoInPort hotelInfoInPort;
  private final HotelInfoDtoMapper hotelInfoDtoMapper;
  private final RoomTypesInfoDtoMapper roomTypesInfoDtoMapper;

  @Operation(summary = "Get hotel info")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelInfoDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/hotels/{hotelId}/info", produces = MediaType.APPLICATION_JSON_VALUE)
  public HotelInfoDto getHotelInfo(@PathVariable("hotelId") String hotelId) {

    final var hotelInfo = hotelInfoInPort.getHotelInfo(hotelId);

    return hotelInfoDtoMapper.toDto(hotelInfo);
  }

  @Operation(summary = "Get room types info")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = RoomTypesInfoDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/hotels/{hotelId}/roomTypes", produces = MediaType.APPLICATION_JSON_VALUE)
  public RoomTypesInfoDto getRoomTypes(@PathVariable("hotelId") String hotelId) {

    final var roomTypesInfo = hotelInfoInPort.getRoomTypesInfo(hotelId);

    return roomTypesInfoDtoMapper.toDto(roomTypesInfo);
  }

}
