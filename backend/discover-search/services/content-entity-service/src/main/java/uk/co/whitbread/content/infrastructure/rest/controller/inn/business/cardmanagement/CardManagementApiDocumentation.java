package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.in.CardManagementRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.CardManagementResponseDto;

public interface CardManagementApiDocumentation {

  @Operation(summary = "Retrieves Card Management Info")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = CardManagementResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<CardManagementResponseDto> getCardManagementInfo(
      @Valid @ParameterObject CardManagementRequestDto cardManagementRequestDto);

}
