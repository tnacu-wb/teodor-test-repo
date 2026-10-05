package uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.AmendmentRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.AmendmentRuleResponseDto;


public interface AmendmentRuleApiDocumentation {

  @Operation(summary = "Retrieves Amendment Rule")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = AmendmentRuleResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  AmendmentRuleResponseDto getAmendmentRule(
      @Valid @ParameterObject AmendmentRuleRequestDto amendmentRuleRequestDto);
}
