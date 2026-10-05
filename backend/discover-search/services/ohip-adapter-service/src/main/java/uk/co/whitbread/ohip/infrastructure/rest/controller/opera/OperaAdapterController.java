package uk.co.whitbread.ohip.infrastructure.rest.controller.opera;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.opera.out.HotelStatus;
import uk.co.whitbread.ohip.domain.ports.primary.HotelDetailsInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestRetryException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipNotFoundException;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.MultiHotelProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.opera.validator.HotelIdValidator;
import uk.co.whitbread.ohip.infrastructure.rest.controller.opera.mapper.HotelStatusDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.opera.model.out.HotelStatusDto;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
public class OperaAdapterController {

  private final HotelDetailsInPort hotelDetailsInPort;
  private final HotelStatusDtoMapper hotelStatusDtoMapper;
  private MultiHotelProperties multiHotelProperties;

  @Operation(summary = "Get hotel status for one or more hotels")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelStatusDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/hotels/status", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<HotelStatusDto> getOnSaleFlagFromOpera(
      @RequestParam("hotelIds") Set<String> hotelIds) {
    String sanitizer = hotelIds.toString().replace("\n", "").replace("\r", "");

    if (hotelIds.stream().anyMatch(id -> !id.matches("[a-zA-Z0-9,]+"))) {
      throw new OhipBadRequestRetryException(ErrorCode.DIGTAL_OHIP_ERROR_MESSAGE,
          "Invalid HotelId format");
    }

    log.info("Requesting HotelStatus for hotelIds: {}", sanitizer);
    String errorMessage =
        new HotelIdValidator()
            .validateMultiHotels(hotelIds, multiHotelProperties.getNoOfAllowedHotels());

    if (StringUtils.isNotEmpty(errorMessage)) {
      log.debug("Error Message : {}", errorMessage);
      throw new OhipBadRequestException(ErrorCode.OHIP_MULTIHOTEL_AVAILABILITY_EXCEPTION, errorMessage);
    }

    final List<HotelStatus> operaHotelDetails = hotelDetailsInPort.getHotelsMigrationStatus(hotelIds);

    log.info("Returning response of HotelStatus for hotelIds: {}", sanitizer);

    return operaHotelDetails.stream()
        .map(hotelStatusDtoMapper::toDto)
        .toList();
  }

}
