package uk.co.whitbread.ohip.infrastructure.rest.controller.preferences;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ohip.domain.ports.primary.PreferenceInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.preferences.mapper.HotelPreferencesResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.preferences.model.out.HotelPreferencesResponseDto;

@RestController
@RequestMapping("/v1/preference")
@RequiredArgsConstructor
public class PreferenceController {

  private final PreferenceInPort preferenceInPort;
  private final HotelPreferencesResponseMapper hotelPreferencesMapper;

  @Operation(summary = "Get all preferences from a group")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = HotelPreferencesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/hotels/{hotelId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public HotelPreferencesResponseDto getPreferencesForGroup(@PathVariable String hotelId,
      @RequestParam String preferenceGroupsCodes) {

    var hotelPreferenceModel = preferenceInPort.getPreferencesForGroup(hotelId,
        preferenceGroupsCodes);
    return hotelPreferencesMapper.toResponseDto(hotelPreferenceModel);
  }

}
