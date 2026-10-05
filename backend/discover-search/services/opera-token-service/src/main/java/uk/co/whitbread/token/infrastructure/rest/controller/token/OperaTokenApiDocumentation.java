package uk.co.whitbread.token.infrastructure.rest.controller.token;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.token.infrastructure.rest.controller.token.model.out.AuthTokenDto;
import uk.co.whitbread.token.infrastructure.validation.ValidProviderId;

public interface OperaTokenApiDocumentation {

  @Operation(summary = "Get Opera access token", description = "Returns token, type, lifetime, and issue time")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = AuthTokenDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<AuthTokenDto> getToken(
      @PathVariable("providerId") @ValidProviderId
      @Parameter(description = "The provider for which to fetch the access token. "
          + "Must be alphanumeric with hyphens and underscores only.",
          required = true, example = "ohip") String providerId);

}
