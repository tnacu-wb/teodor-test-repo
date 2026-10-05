package uk.co.whitbread.content.infrastructure.rest.controller.note.business;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.in.BusinessNotesRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.out.BusinessNotesResponseDto;

public interface BusinessNoteApiDocumentation {

  @Operation(summary = "Retrieves texts for business notes")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = BusinessNotesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/content/businessNotes", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<BusinessNotesResponseDto> getBusinessNotes(
      @Valid @ParameterObject BusinessNotesRequestDto businessNotesRequestDto);
}
