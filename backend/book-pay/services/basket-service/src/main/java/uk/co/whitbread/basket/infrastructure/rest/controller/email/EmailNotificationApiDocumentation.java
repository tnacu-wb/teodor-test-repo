package uk.co.whitbread.basket.infrastructure.rest.controller.email;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.basket.infrastructure.rest.controller.email.model.in.EmailRequestDto;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

public interface EmailNotificationApiDocumentation {

  @Operation(summary = "Initiate email trigger notification process.")
  @ApiResponse(responseCode = "201", description = "Created", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<Void> triggerEmailNotificationProcess(
      @Valid @RequestBody final EmailRequestDto emailRequestDto);

}
