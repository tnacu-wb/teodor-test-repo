package uk.co.whitbread.review.infrastructure.rest.controller.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.out.ReviewResponseDto;

public interface HotelReviewControllerApiDocumentation {

  @Operation(summary = "Retrieves Sample Hotels getReviewsForSingleHotel Result")
  @ApiResponse(responseCode = "200", description = "Success", content = {
    @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ReviewResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
    @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
    @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
    @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reviews/{hotelCode}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<ReviewResponseDto> getReviewsForSingleHotel(
      @PathVariable(name = "hotelCode") @NotNull String hotelCode,
      @Valid @RequestParam(name = "lang", defaultValue = "en_US") String lang,
      @RequestParam(name = "limit", defaultValue = "5") Integer limit);
}
