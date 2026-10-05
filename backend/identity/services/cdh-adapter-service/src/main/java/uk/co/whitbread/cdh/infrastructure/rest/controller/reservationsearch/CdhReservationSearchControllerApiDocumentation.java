package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.in.CdhReservationSearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out.CdhReservationSearchDto;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

public interface CdhReservationSearchControllerApiDocumentation {

  @Operation(summary = "Search bookings from CDH Booking Services API V2")
  @ApiResponse(responseCode = "200", description = "Success",
          content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = CdhReservationSearchDto.class)))
  @ApiResponse(responseCode = "404", description = "Not found",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "400", description = "Bad request",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "401", description = "Unauthorized",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "Internal Server Error",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  CdhReservationSearchDto getReservationSearch(
          @Valid @RequestBody CdhReservationSearchCriteriaDto cdhReservationSearchCriteriaDto);
}
