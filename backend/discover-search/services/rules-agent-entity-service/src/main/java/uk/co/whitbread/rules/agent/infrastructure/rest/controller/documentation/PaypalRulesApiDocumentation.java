package uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.PaypalRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxNightsRuleResponseDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.PaypalRuleResponseDto;


public interface PaypalRulesApiDocumentation {

  @Operation(summary = "Retrieves Paypal Rule")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = MaxNightsRuleResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  PaypalRuleResponseDto getPaypalRule(
      @Valid @ParameterObject PaypalRuleRequestDto paypalRuleRequestDto);

}
