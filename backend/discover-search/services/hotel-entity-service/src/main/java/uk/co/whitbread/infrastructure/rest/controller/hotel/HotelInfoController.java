package uk.co.whitbread.infrastructure.rest.controller.hotel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.domain.ports.primary.HotelInfoInPort;
import uk.co.whitbread.infrastructure.rest.controller.hotel.mapper.HotelInfoDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.hotel.model.in.HotelPreferencesRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.hotel.model.out.HotelInfoDto;
import uk.co.whitbread.infrastructure.rest.controller.hotel.model.out.HotelPreferencesResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class HotelInfoController {

  private final HotelInfoInPort hotelInfoInPort;
  private final HotelInfoDtoMapper hotelInfoDtoMapper;

  @Operation(summary = "Get hotel info")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelInfoDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/hotels/{hotelId}/info", produces = MediaType.APPLICATION_JSON_VALUE)
  public HotelInfoDto getHotelInfo(@PathVariable("hotelId") String hotelId) {

    final var hotelInfo = hotelInfoInPort.getHotelInfo(hotelId);

    return hotelInfoDtoMapper.toDto(hotelInfo);
  }

  @Operation(summary = "Get hotel preferences for a group")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = HotelPreferencesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/hotels/{hotelId}/preferences", produces = MediaType.APPLICATION_JSON_VALUE)
  public HotelPreferencesResponseDto getHotelPreferences(@PathVariable @NotEmpty String hotelId,
      @ParameterObject @Valid HotelPreferencesRequestDto hotelPreferencesRequestDto) {

    var hotelPreferences = hotelInfoInPort.getHotelPreferences(hotelId,
        hotelPreferencesRequestDto.preferenceGroupsCodes(), hotelPreferencesRequestDto.language());

    return hotelInfoDtoMapper.toDto(hotelPreferences);
  }

}
