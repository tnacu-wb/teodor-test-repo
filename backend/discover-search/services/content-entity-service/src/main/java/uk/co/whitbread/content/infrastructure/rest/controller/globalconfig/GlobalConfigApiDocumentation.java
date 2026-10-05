package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.in.GlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.GlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.RoomClassConfigDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.SearchRulesDto;
import uk.co.whitbread.content.infrastructure.rest.controller.promoconfig.model.in.PromoConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.promoconfig.model.out.PromotionsInformationResponseDto;

public interface GlobalConfigApiDocumentation {

  @Operation(summary = "Retrieves Search Rules")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = SearchRulesDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<SearchRulesDto> getSearchRules(
      @Valid @ParameterObject GlobalConfigRequestDto globalConfigRequestDto);

  @Operation(summary = "Retrieves the room class config from AEM")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = RoomClassConfigDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<RoomClassConfigDto> getRoomClassConfig(
      @Valid @ParameterObject GlobalConfigRequestDto globalConfigRequestDto);

  @Operation(summary = "Retrieves global config from AEM")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = GlobalConfigDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<GlobalConfigDto> getGlobalConfig(
      @Valid @ParameterObject GlobalConfigRequestDto globalConfigRequestDto);

  @Operation(summary = "Retrieves promo config from AEM")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = PromotionsInformationResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<PromotionsInformationResponseDto> getPromoConfig(
      @Valid @ParameterObject GlobalConfigRequestDto globalConfigRequestDto,
      @Valid @ParameterObject PromoConfigRequestDto promoConfigRequestDto);
}
