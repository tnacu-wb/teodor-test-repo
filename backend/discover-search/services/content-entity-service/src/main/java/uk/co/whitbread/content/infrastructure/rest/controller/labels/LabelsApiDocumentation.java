package uk.co.whitbread.content.infrastructure.rest.controller.labels;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.Map;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in.LabelsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in.MultipleLabelsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.out.ExtrasLabelDto;
import uk.co.whitbread.content.infrastructure.rest.controller.model.in.LocalizationDto;


public interface LabelsApiDocumentation {

  @Operation(summary = "Retrieves Label Info by Category")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = Map.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  Map<String, String> getLabels(
      @Valid @ParameterObject LabelsRequestDto labelsRequestDto);


  @Operation(summary = "Retrieves Label Info")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = Map.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  Map<String, Map<String, String>> getLabels(
      @Valid @ParameterObject MultipleLabelsRequestDto multipleLabelsRequestDto);

  @Operation(summary = "Retrieves Extras Info")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ExtrasLabelDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<ExtrasLabelDto> getExtras(@Valid @ParameterObject LocalizationDto localization);
}
