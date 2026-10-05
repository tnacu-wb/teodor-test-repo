package uk.co.whitbread.ocd.infrastructure.rest.controller.tax;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.model.in.TaxRequestDto;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.model.out.TaxResponseDto;

public interface TaxApiDocumentation {

  @Operation(summary = "Retrieves Tax-inclusive pricing from OCD")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = TaxResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<TaxResponseDto> getTaxDetails(String hotelId, TaxRequestDto taxRequestDto);
}
