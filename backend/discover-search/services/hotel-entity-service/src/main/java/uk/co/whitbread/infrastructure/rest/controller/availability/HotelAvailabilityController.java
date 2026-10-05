package uk.co.whitbread.infrastructure.rest.controller.availability;

import static uk.co.whitbread.infrastructure.rest.client.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.infrastructure.rest.client.utils.SanitizingUtils.sanitizeForLog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.domain.ports.primary.HotelAvailabilityInPort;
import uk.co.whitbread.infrastructure.rest.client.availability.exception.HotelAvailabilityException;
import uk.co.whitbread.infrastructure.rest.controller.availability.mapper.HotelAvailabilitiesByIdsRequestDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.availability.mapper.HotelAvailabilityByIdsDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.availability.mapper.HotelAvailabilityDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.availability.mapper.HotelAvailabilityRequestDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.availability.mapper.HotelInventoryDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.availability.mapper.RateCodeCriteriaDomainMapper;
import uk.co.whitbread.infrastructure.rest.controller.availability.mapper.RateCodeResponseDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilitiesByIdsRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilitiesByIdsRequestV2Dto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilitiesByIdsRequestV3Dto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilityRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelInventoryRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.RateCodePricingRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityByIdsDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityByIdsV2Dto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityResponseDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelInventoryRoomTypeDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.RateCodePricingRsDto;

/**
 * Hotel Availability Entity REST Controller. Used to forward Experience requests for availabilities
 * of hotel rooms to OHIP Adapter.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class HotelAvailabilityController {

  private final HotelAvailabilityInPort hotelAvailabilityInboundPort;
  private final HotelAvailabilityDtoMapper hotelAvailabilityAdapter;
  private final HotelAvailabilityByIdsDtoMapper hotelAvailabilityByIdsAdapter;
  private final HotelAvailabilityRequestDtoMapper requestAdapter;
  private final HotelAvailabilitiesByIdsRequestDtoMapper hotelAvailabilityByIdsRequestDtoMapper;
  private final RateCodeCriteriaDomainMapper rateCodeCriteriaDomainMapper;
  private final RateCodeResponseDtoMapper rateCodeResponseDtoMapper;
  private final HotelInventoryDtoMapper hotelInventoryDtoMapper;

  @Operation(summary = "Retrieves Hotels Availability Result")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @GetMapping(
      value = "/v1/hotels/{hotelId}/availabilities", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@availabilityPermissionEvaluator.hasAccess(#hotelAvailabilityRequestDto)")
  @SuppressWarnings("squid:S6856")
  public HotelAvailabilityResponseDto getHotelAvailabilities(
      @Valid @ParameterObject HotelAvailabilityRequestDto hotelAvailabilityRequestDto) {

    var request = requestAdapter.toModel(hotelAvailabilityRequestDto);
    var hotelAvailability = hotelAvailabilityInboundPort.getHotelAvailability(request);

    return hotelAvailabilityAdapter.toDto(hotelAvailability);
  }

  @Operation(summary = "Retrieves Hotel Availabilities for the specified hotel IDs")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityByIdsDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @GetMapping(
      value = "/v1/hotels/availabilities/distr", produces = MediaType.APPLICATION_JSON_VALUE)
  public HotelAvailabilityByIdsDto getHotelAvailabilitiesByIds(
      @Valid @ParameterObject
      HotelAvailabilitiesByIdsRequestDto requestDto) {

    var request = hotelAvailabilityByIdsRequestDtoMapper.toModel(requestDto);
    var hotelAvailabilityByIds = hotelAvailabilityInboundPort.getHotelAvailabilityByIds(request);

    return hotelAvailabilityByIdsAdapter.toDto(hotelAvailabilityByIds);
  }

  @Operation(summary = "Retrieves the pricing information for a rate code")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = RateCodePricingRsDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @GetMapping(
      value = "/v1/hotels/{hotelId}/rate-code-pricing", produces = MediaType.APPLICATION_JSON_VALUE)
  public RateCodePricingRsDto getRateCodePricing(@PathVariable("hotelId") String hotelId,
                                                 @Valid @ParameterObject
                                                 RateCodePricingRequestDto rateCodePricingRq) {

    final var rateCodeCriteria =
        rateCodeCriteriaDomainMapper.toDomainModel(hotelId, rateCodePricingRq);
    log.debug("Successfully converted {} to {}", sanitizeForLog(rateCodePricingRq), sanitizeForLog(rateCodeCriteria));
    final var rateCodePricingResponse =
        hotelAvailabilityInboundPort.getRateCodePricing(rateCodeCriteria);
    return rateCodeResponseDtoMapper.toDto(rateCodePricingResponse);
  }

  @Operation(summary = "Retrieves Hotel Inventory")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelInventoryRoomTypeDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @GetMapping(
      value = "/v1/hotels/{hotelId}/hotelInventory", produces = MediaType.APPLICATION_JSON_VALUE)
  public HotelInventoryRoomTypeDto getHotelRoomsInventory(@PathVariable("hotelId") String hotelId,
                                                          @Valid @ParameterObject
                                                          HotelInventoryRequestDto hotelInventoryRequestDto) {

    log.debug("Request to get hotel inventory for: {} started", sanitize(hotelId));

    final var hotelInventoryRequest =
        hotelInventoryDtoMapper.toDomainModel(hotelId, hotelInventoryRequestDto);
    final var hotelInventory =
        hotelAvailabilityInboundPort.getHotelRoomsInventory(hotelInventoryRequest);

    return hotelInventoryDtoMapper.toDto(hotelInventory);
  }

  @Operation(summary = "Retrieves Hotel Availabilities for the specified hotel IDs based on dedicated Opera APIs")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityByIdsV2Dto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @PostMapping(
      value = "/v2/hotels/availabilities/distr", produces = MediaType.APPLICATION_JSON_VALUE)
  public HotelAvailabilityByIdsV2Dto getHotelAvailabilitiesByIdsV2(
      @Valid @RequestBody
      HotelAvailabilitiesByIdsRequestV2Dto requestDto) {

    var request = hotelAvailabilityByIdsRequestDtoMapper.toV2Model(requestDto);
    var hotelAvailabilityByIds = hotelAvailabilityInboundPort.getHotelAvailabilityByIdsV2(request);

    return hotelAvailabilityByIdsAdapter.toV2Dto(hotelAvailabilityByIds);
  }

  @Operation(summary = "Retrieves Hotel Availabilities for the specified hotel IDs based on dedicated Opera APIs")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityByIdsV2Dto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelAvailabilityException.class))})
  @PostMapping(
      value = "/v3/hotels/availabilities/distr", produces = MediaType.APPLICATION_JSON_VALUE)
  public HotelAvailabilityByIdsV2Dto getHotelAvailabilitiesByIdsV3(
      @Valid @RequestBody
      HotelAvailabilitiesByIdsRequestV3Dto requestDto) {

    var request = hotelAvailabilityByIdsRequestDtoMapper.toV3Model(requestDto);
    var hotelAvailabilityByIds = hotelAvailabilityInboundPort.getHotelAvailabilityByIdsV2(request);

    return hotelAvailabilityByIdsAdapter.toV2Dto(hotelAvailabilityByIds);
  }
}
