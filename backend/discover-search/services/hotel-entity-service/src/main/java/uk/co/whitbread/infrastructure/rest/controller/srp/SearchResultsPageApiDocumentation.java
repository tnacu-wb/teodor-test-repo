package uk.co.whitbread.infrastructure.rest.controller.srp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import uk.co.whitbread.infrastructure.rest.client.hotelsearch.exception.HotelSearchGenericException;
import uk.co.whitbread.infrastructure.rest.controller.srp.model.in.HotelAvailabilitiesRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.srp.model.out.HotelAvailabilitiesResponseDto;


public interface SearchResultsPageApiDocumentation {

  @Operation(summary = "Retrieves Hotels Availabilities near Hotel location search Result")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilitiesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelSearchGenericException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelSearchGenericException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelSearchGenericException.class))})
  HotelAvailabilitiesResponseDto getSrpHotelAvailabilities(
      @Valid @ParameterObject HotelAvailabilitiesRequestDto hotelAvailabilitiesRequestDto);
}
