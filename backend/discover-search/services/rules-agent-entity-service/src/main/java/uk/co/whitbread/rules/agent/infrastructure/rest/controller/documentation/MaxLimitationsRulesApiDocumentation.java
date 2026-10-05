package uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxArrivalDateRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxNightsRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxRoomOccupancyRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxRoomsRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxArrivalDateRuleResponseDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxNightsRuleResponseDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxRoomOccupancyResponseDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxRoomsRuleResponseDto;


public interface MaxLimitationsRulesApiDocumentation {

  @Operation(summary = "Retrieves Max Nights Rule")
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
  MaxNightsRuleResponseDto getMaxNightsRule(
      @Valid @ParameterObject MaxNightsRuleRequestDto maxNightsRuleRequestDto);

  @Operation(summary = "Retrieves Max Rooms Rule")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = MaxRoomsRuleResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  MaxRoomsRuleResponseDto getMaxRoomsRule(
      @Valid @ParameterObject MaxRoomsRuleRequestDto maxRoomsRuleRequestDto);

  @Operation(summary = "Retrieves Room Occupancy Rule")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = MaxRoomOccupancyResponseDto.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  MaxRoomOccupancyResponseDto getMaxRoomOccupancyRule(
      @Valid @ParameterObject MaxRoomOccupancyRequestDto roomOccupancyRequestDto);

  @Operation(summary = "Retrieves Max Arrival Date Rule")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = MaxArrivalDateRuleResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  MaxArrivalDateRuleResponseDto getMaxArrivalDateRule(
      @Valid @ParameterObject MaxArrivalDateRuleRequestDto maxArrivalDateRequestDto);
}
