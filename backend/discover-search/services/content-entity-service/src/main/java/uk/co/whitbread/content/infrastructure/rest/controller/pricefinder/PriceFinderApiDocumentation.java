package uk.co.whitbread.content.infrastructure.rest.controller.pricefinder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.in.PriceFinderGlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.out.PriceFinderGlobalConfigDto;

public interface PriceFinderApiDocumentation {

  @Operation(summary = "Retrieves the price finder config from AEM")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = PriceFinderGlobalConfigDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<PriceFinderGlobalConfigDto> getPriceFinderGlobalConfig(
      @Valid @ParameterObject PriceFinderGlobalConfigRequestDto priceFinderGlobalConfigRequestDto);

}
