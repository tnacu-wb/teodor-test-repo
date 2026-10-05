package uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.rules.agent.domain.model.out.OccupancySupplementResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MultiOccupancySupplementRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.OccupancySupplementRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MultiOccupancySupplementResponseDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.OccupancySupplementResponseDto;


public interface OccupancySupplementApiDocumentation {

  @Operation(summary = "Retrieves Occupancy Supplement Pricing")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OccupancySupplementResponse.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  OccupancySupplementResponseDto getOccupancySupplementPricing(OccupancySupplementRequestDto amendmentRuleRequestDto);

  @Operation(summary = "Retrieves Occupancy Supplement Pricing")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = MultiOccupancySupplementResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  MultiOccupancySupplementResponseDto getMultiOccupancySupplementPricing(
      MultiOccupancySupplementRequestDto occupancySupplementRequestDto, boolean dictionary);
}
