package uk.co.whitbread.basket.infrastructure.rest.controller.basket;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.BackgroundChargeRequestDto;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

/**
 * API documentation interface for BackgroundChargeController.
 */
public interface BackgroundChargeControllerApiDocumentation {

  /**
   * Processes background charge for a basket.
   *
   * @param backgroundChargeRequestDto the background charge request containing basketReference and token
   * @return ResponseEntity with 204 No Content status
   */
  @Operation(summary = "Processes background charge for a basket")
  @ApiResponse(responseCode = "204", description = "No Content")
  @ApiResponse(responseCode = "400", description = "Error Occurred", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/background-charge", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> backgroundCharge(
      @RequestBody @Valid BackgroundChargeRequestDto backgroundChargeRequestDto);
}

