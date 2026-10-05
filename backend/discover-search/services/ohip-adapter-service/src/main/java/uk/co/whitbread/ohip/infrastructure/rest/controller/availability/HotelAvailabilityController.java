package uk.co.whitbread.ohip.infrastructure.rest.controller.availability;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.ohip.domain.ports.primary.HotelAvailabilityInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipNotFoundException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.exceptions.RateCodePricingException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.AvailabilityByIdsResponseDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.AvailabilityByIdsSearchCriteriaDomainMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.AvailabilityResponseDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.AvailabilitySearchCriteriaDomainMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.HotelInventoryMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.ItemInventoryMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.MultiAvailabilityResponseDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.MultiAvailabilitySearchCriteriaMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.RateCodeCriteriaDomainMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.RateCodeResponseDtoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.RestrictionsByDateRangeMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.RoomPriceBreakdownRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.RoomPriceBreakdownResultMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.AvailabilityByIdsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.AvailabilityByIdsRequestV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.AvailabilityByIdsRequestV3Dto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.AvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.HotelInventoryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.ItemInventoryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.MultiAvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.MultiAvailabilityRequestV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.RateCodePricingRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.RestrictionsByDateRangeRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.RoomPriceBreakdownRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.AvailabilityByIdsResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.AvailabilityByIdsResponseV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.AvailabilityResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.HotelInventoryRoomTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.ItemInventoryResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.MultiAvailabilityResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.MultiAvailabilityResponseV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.RateCodePricingRsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.RestrictionsByDateRangeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.RoomPriceBreakdownResultDto;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HotelAvailabilityController {

  private final HotelAvailabilityInPort hotelAvailabilityPort;
  private final AvailabilityResponseDtoMapper availabilityResponseMapper;
  private final AvailabilityByIdsResponseDtoMapper availabilityByIdsResponseMapper;
  private final AvailabilitySearchCriteriaDomainMapper hotelAvailabilitySearchCriteriaMapper;
  private final AvailabilityByIdsSearchCriteriaDomainMapper availabilityByIdsSearchCriteriaMapper;
  private final RateCodeCriteriaDomainMapper rateCodeCriteriaDomainMapper;
  private final RateCodeResponseDtoMapper rateCodeResponseDtoMapper;
  private final HotelInventoryMapper hotelInventoryMapper;
  private final MultiAvailabilitySearchCriteriaMapper multiAvailabilitySearchCriteriaMapper;
  private final MultiAvailabilityResponseDtoMapper multiAvailabilityResponseMapper;
  private final RoomPriceBreakdownRequestMapper roomPriceBreakdownRequestMapper;
  private final RoomPriceBreakdownResultMapper roomPriceBreakdownResultMapper;
  private final ItemInventoryMapper itemInventoryMapper;
  private final RestrictionsByDateRangeMapper restrictionsByDateRangeMapper;

  @Operation(summary = "Retrieves Hotel Availability")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = AvailabilityResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(
      value = "/hotels/{hotelId}/availabilities", produces = MediaType.APPLICATION_JSON_VALUE)
  public AvailabilityResponseDto getHotelAvailability(
      @PathVariable("hotelId") String hotelId,
      @Valid @ParameterObject AvailabilityRequestDto availabilityRequest) {

    final var domainHotelAvailabilitySearchCriteria =
        hotelAvailabilitySearchCriteriaMapper.toDomainModel(hotelId, availabilityRequest);
    final var domainHotelAvailability =
        hotelAvailabilityPort.getHotelAvailability(domainHotelAvailabilitySearchCriteria);

    return availabilityResponseMapper.toDto(domainHotelAvailability);
  }

  @Operation(summary = "Retrieves Hotel Availability for a list of hotels")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = AvailabilityByIdsResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(
      value = "/hotels/availabilities/distr", produces = MediaType.APPLICATION_JSON_VALUE)
  public AvailabilityByIdsResponseDto getHotelAvailabilityByIds(
      @Valid @ParameterObject AvailabilityByIdsRequestDto availabilityRequest) {

    final var domainHotelAvailabilityByIdsSearchCriteria =
        availabilityByIdsSearchCriteriaMapper.toDomainModel(availabilityRequest);
    final var availabilityByIds =
        hotelAvailabilityPort.getHotelAvailabilityByIds(domainHotelAvailabilityByIdsSearchCriteria);

    return availabilityByIdsResponseMapper.toDto(availabilityByIds);
  }

  @Operation(summary = "Retrieves Rate Price Breakdown")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = RoomPriceBreakdownResultDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(
      value = "/hotels/{hotelId}/price-breakdown", produces = MediaType.APPLICATION_JSON_VALUE)
  public RoomPriceBreakdownResultDto getRoomPriceBreakdown(
      @PathVariable("hotelId") String hotelId,
      @Valid @ParameterObject RoomPriceBreakdownRequestDto roomPriceBreakdownRequestDto) {

    final var roomPriceBreakdownRequest =
        roomPriceBreakdownRequestMapper.toDomainModel(roomPriceBreakdownRequestDto);
    final var roomPriceBreakdown =
        hotelAvailabilityPort.getHotelMultiRoomsPriceBreakdown(hotelId, roomPriceBreakdownRequest);

    return roomPriceBreakdownResultMapper.toDto(roomPriceBreakdown);
  }

  @Operation(summary = "Retrieves the pricing information for a rate code")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = AvailabilityResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(
      value = "/hotels/{hotelId}/rate-code-pricing", produces = MediaType.APPLICATION_JSON_VALUE)
  public RateCodePricingRsDto getRateCodePricing(
      @PathVariable("hotelId") String hotelId,
      @Valid @ParameterObject RateCodePricingRequestDto rateCodePricingRq) {

    try {
      final var rateCodeCriteria =
          rateCodeCriteriaDomainMapper.toDomainModel(hotelId, rateCodePricingRq);
      final var rateCodePricingResponse =
          hotelAvailabilityPort.getRateCodePricing(rateCodeCriteria);
      return rateCodeResponseDtoMapper.toDto(rateCodePricingResponse);
    } catch (RateCodePricingException exception) {
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  @Operation(summary = "Retrieves Hotel Inventory")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = HotelInventoryRoomTypeDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(
      value = "/hotels/{hotelId}/hotelInventory", produces = MediaType.APPLICATION_JSON_VALUE)
  public HotelInventoryRoomTypeDto getHotelRoomsInventory(
      @PathVariable("hotelId") String hotelId,
      @Valid @ParameterObject HotelInventoryRequestDto hotelInventoryRequestDto) {

    final var hotelInventoryRequest =
        hotelInventoryMapper.toDomainModel(hotelId, hotelInventoryRequestDto);
    final var hotelInventory =
        hotelAvailabilityPort.getHotelRoomsInventory(hotelInventoryRequest);

    return hotelInventoryMapper.toDto(hotelInventory);
  }

  @Operation(summary = "Retrieves Multiple Hotels Availability")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = MultiAvailabilityResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(
      value = "/hotels/availabilities", produces = MediaType.APPLICATION_JSON_VALUE)
  public MultiAvailabilityResponseDto getMultiHotelAvailability(
      @Valid @ParameterObject MultiAvailabilityRequestDto multiAvailabilityRequest) {

    final var domainMultiAvailabilitySearchCriteria =
        multiAvailabilitySearchCriteriaMapper.toRequestModel(multiAvailabilityRequest);
    final var domainHotelAvailability =
        hotelAvailabilityPort.getMultiHotelAvailability(domainMultiAvailabilitySearchCriteria);

    return multiAvailabilityResponseMapper.toResponseDto(domainHotelAvailability);
  }

  @Operation(summary = "Retrieves Multiple Hotels Availability using minimum rate API")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = MultiAvailabilityResponseV2Dto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(
      value = "/v2/hotels/availabilities", produces = MediaType.APPLICATION_JSON_VALUE)
  public MultiAvailabilityResponseV2Dto getMultiHotelAvailabilityV2(
      @Valid @RequestBody MultiAvailabilityRequestV2Dto multiAvailabilityRequest) {

    final var multiHotelAvailabilityRequest =
        multiAvailabilitySearchCriteriaMapper.toRequestV2Model(multiAvailabilityRequest);
    final var multiHotelAvailabilityResult =
        hotelAvailabilityPort.getMultiHotelAvailabilityV2(multiHotelAvailabilityRequest);

    return multiAvailabilityResponseMapper.toV2ResponseDto(multiHotelAvailabilityResult);
  }

  @Operation(summary = "Retrieves Hotel Availability for a list of hotels "
      + "using multi room rate availability API")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = AvailabilityByIdsResponseV2Dto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(
      value = "/v2/hotels/availabilities/distr", produces = MediaType.APPLICATION_JSON_VALUE)
  public AvailabilityByIdsResponseV2Dto getHotelAvailabilityByIdsV2(
      @Valid @RequestBody AvailabilityByIdsRequestV2Dto availabilityRequest) {

    final var availabilityByIdsSearchCriteria =
        availabilityByIdsSearchCriteriaMapper.toV2DomainModel(availabilityRequest);
    final var availabilityByIdsResult =
        hotelAvailabilityPort.getHotelAvailabilityByIdsV2(availabilityByIdsSearchCriteria);

    return availabilityByIdsResponseMapper.toV2Dto(availabilityByIdsResult);
  }

  @Operation(summary = "Retrieves Hotel Availability for a list of hotels "
      + "using multi room rate availability API")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = AvailabilityByIdsResponseV2Dto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(
      value = "/v3/hotels/availabilities/distr", produces = MediaType.APPLICATION_JSON_VALUE)
  public AvailabilityByIdsResponseV2Dto getHotelAvailabilityByIdsV3(
      @Valid @RequestBody AvailabilityByIdsRequestV3Dto availabilityRequest) {

    final var availabilityByIdsSearchCriteria =
        availabilityByIdsSearchCriteriaMapper.toV3DomainModel(availabilityRequest);
    final var availabilityByIdsResult =
        hotelAvailabilityPort.getHotelAvailabilityByIdsV2(availabilityByIdsSearchCriteria);

    return availabilityByIdsResponseMapper.toV2Dto(availabilityByIdsResult);
  }

  @Operation(summary = "Retrieves Item Inventory")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ItemInventoryResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
          content = {@Content(mediaType = "application/json",
                  schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
                  schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
                  schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(
          value = "/hotels/{hotelId}/itemInventory", produces = MediaType.APPLICATION_JSON_VALUE)
  public ItemInventoryResponseDto getItemInventory(
          @PathVariable("hotelId") String hotelId,
          @Valid @ParameterObject ItemInventoryRequestDto itemInventoryRequestDto) {

    final var itemInventoryRequest = itemInventoryMapper.toDomainModel(hotelId, itemInventoryRequestDto);
    final var itemInventory = hotelAvailabilityPort.getHotelItemsInventory(itemInventoryRequest);

    return itemInventoryMapper.toDto(itemInventory);
  }

  @Operation(summary = "Get hotel restrictions by date range")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = RestrictionsByDateRangeDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/hotels/{hotelId}/restrictions", produces = MediaType.APPLICATION_JSON_VALUE)
  public RestrictionsByDateRangeDto getRestrictionsByDateRange(
      @PathVariable("hotelId") String hotelId,
      @Valid @ParameterObject RestrictionsByDateRangeRequestDto restrictionsByDateRangeRequest) {

    var restrictionRequest = restrictionsByDateRangeMapper
        .toDomainModel(hotelId, restrictionsByDateRangeRequest);
    var restrictionResult = hotelAvailabilityPort.getRestrictionsByDateRange(restrictionRequest);

    return restrictionsByDateRangeMapper.toDto(restrictionResult);
  }

  @Operation(summary = "Get hotel restrictions by date range for a list of hotels")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = List.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/hotels/restrictions", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<RestrictionsByDateRangeDto> getMultiHotelRestrictionsByDateRange(
      @RequestParam("hotelIds") List<String> hotelIds,
      @Valid @ParameterObject RestrictionsByDateRangeRequestDto restrictionsByDateRangeRequest) {

    var mappedRequests = hotelIds.stream()
        .map(hotelId ->
            restrictionsByDateRangeMapper.toDomainModel(hotelId, restrictionsByDateRangeRequest))
        .toList();
    return hotelAvailabilityPort.getMultiHotelRestrictionsByDateRange(mappedRequests)
        .stream()
        .map(restrictionsByDateRangeMapper::toDto)
        .toList();
  }

}
