package uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip;

import static java.util.Collections.singletonList;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ADULTS_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ATTACHED_PROFILE_ID;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.CHILDREN_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.CRITERIA_END_DATE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.CRITERIA_START_DATE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.DAILY_INVENTORY;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.DATE_RANGE_END;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.DATE_RANGE_START;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.END_DATE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOTEL_IDS_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOTEL_ID_HEADER;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOUSE_LEVEL;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.LIMIT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.PARAMETER_NAME;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.PARAMETER_VALUE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.PROMOTION_CODE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RATE_PLAN_CODE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RATE_PLAN_SET;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.REPORT_CODE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RESERVATION_GUEST_ID;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RESERVATION_GUEST_ID_TYPE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RESERVATION_PROFILE_TYPE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RESTRICTIONS_BY_DATE_END_DATE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RESTRICTIONS_BY_DATE_START_DATE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ROOMS_AVAILABILITY_SUMMARY;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ROOM_AVAIL_ROOMS_YN;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ROOM_COUNT_REQUESTED;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ROOM_PHYSICAL_ROOMS_YN;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ROOM_STAY_END_DATE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ROOM_STAY_QUANTITY;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ROOM_STAY_START_DATE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ROOM_TYPE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ROOM_TYPES;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ROOM_TYPE_WILD_CARD_LIST;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.SELL_IN_RESERVATION;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.START_DATE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.WELCOME_OFFER;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.YES;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.logErrorResponse;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.util.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.inventory.StatisticType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.HotelAvailability;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.MultiRoomRateAvailabilityResponseType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.SearchPropertyResponseType;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeRoomInfoCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RestrictionsByDateRangeSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.exceptions.HotelAvailabilityException;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HoldItemInventoryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelAvailabilityDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelInventoryDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.ItemInventoryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.PriceBreakdownDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityByIdsSearchCriteriaV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.ItemInventoryResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.MultiHotelAvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.MultiHotelAvailabilityRequestV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomTypesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.properties.AvailabilityOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils;


@RequiredArgsConstructor
@Slf4j
@Component
public class OhipAvailabilityClient {

  private static final String AVAILABILITY_LIMIT = "20";
  private static final String ROOM_TYPES_LIMIT = "50";
  private static final String PROFILE = "Profile";
  private static final String COMPANY = "Company";
  public static final String SUMMARY_INFO = "summaryInfo";

  private final WebClient ohipWebClient;
  private final AvailabilityOhipProperties availabilityOhipProperties;

  public Mono<HotelAvailabilityDetailsDto> getHotelAvailabilityRequest(
      AvailabilityRequestDto availabilityRequest) {

    String message = "Error while trying to get hotel availability for hotelId=%s";
    return ohipWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(availabilityOhipProperties.getAvailabilitiesEndpoint())
            .queryParam(ROOM_STAY_START_DATE, availabilityRequest.getRoomStayStartDate())
            .queryParam(ROOM_STAY_END_DATE, availabilityRequest.getRoomStayEndDate())
            .queryParam(ROOM_STAY_QUANTITY, availabilityRequest.getRoomStayQuantity())
            .queryParam(ROOM_TYPE, String.join(",", availabilityRequest.getRoomTypes()))
            .queryParamIfPresent(RATE_PLAN_SET,
                Optional.ofNullable(availabilityRequest.getRatePlanSet()))
            .queryParam(LIMIT,
                availabilityRequest.getLimit() != null ? availabilityRequest.getLimit().toString()
                    : AVAILABILITY_LIMIT)
            .queryParamIfPresent(RATE_PLAN_CODE,
                Optional.ofNullable(availabilityRequest.getRatePlanCode()))
            .queryParamIfPresent(RESERVATION_GUEST_ID,
                Optional.ofNullable(availabilityRequest.getCompanyId()))
            .queryParam(RESERVATION_GUEST_ID_TYPE, PROFILE)
            .queryParamIfPresent(PROMOTION_CODE,
                Optional.ofNullable(availabilityRequest.getPromotionCode()))
            .build(availabilityRequest.getHotelId()))
        .headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, availabilityRequest.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new OhipBadRequestException(ErrorCode.OHIP_RETRIEVE_AVAILABILITY_EXCEPTION,
              String.format(message, availabilityRequest.getHotelId())));
        })
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelAvailabilityException(
              ErrorCode.OHIP_RETRIEVE_AVAILABILITY_EXCEPTION,
              String.format(message, availabilityRequest.getHotelId())
          ));
        })
        .bodyToMono(HotelAvailabilityDetailsDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  public Mono<PriceBreakdownDto> getPriceBreakdownPerNight(
      AvailabilityRequestDto availabilityRequest) {

    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(CRITERIA_START_DATE, singletonList(availabilityRequest.getRoomStayStartDate()));
    params.put(CRITERIA_END_DATE, singletonList(availabilityRequest.getRoomStayEndDate()));
    params.put(ADULTS_PARAM, singletonList(availabilityRequest.getAdults().get(0).toString()));
    params.put(CHILDREN_PARAM, singletonList(availabilityRequest.getChildren().get(0).toString()));
    params.put(RATE_PLAN_CODE, singletonList(availabilityRequest.getRatePlanCode()));
    params.put(ROOM_TYPE, singletonList(availabilityRequest.getRoomTypes().get(0)));
    params.put(SUMMARY_INFO, singletonList("true"));

    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(availabilityOhipProperties.getRateInfoEndpoint())
                .queryParams(params).build(availabilityRequest.getHotelId()))
        .headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, availabilityRequest.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelAvailabilityException(
              ErrorCode.OHIP_PRICE_BREAKDOWN_PERNIGHT_EXCEPTION,
              String.format(
                  "Error while trying to get price breakdown per night for hotelId=%s",
                  availabilityRequest.getHotelId())));
        })
        .bodyToMono(PriceBreakdownDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  public Mono<HotelInventoryDto> getHotelInventory(String hotelId, String startDate, String endDate,
      int roomCountRequested) {
    return getHotelInventory(hotelId, startDate, endDate, roomCountRequested, null);
  }

  public Mono<HotelInventoryDto> getHotelInventory(String hotelId, String startDate, String endDate,
      int roomCountRequested, List<String> roomTypes) {

    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(availabilityOhipProperties.getHotelInventoryEndpoint())
                .queryParam(DATE_RANGE_START, startDate)
                .queryParam(DATE_RANGE_END, endDate)
                .queryParam(ROOM_COUNT_REQUESTED, roomCountRequested)
                .queryParam(DAILY_INVENTORY, false)
                .queryParam(HOUSE_LEVEL, true)
                .queryParamIfPresent(ROOM_TYPES, Optional.ofNullable(roomTypes)) //roomTypes can be null
                .build(hotelId))
        .headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_HOTEL_INVENTORY_EXCEPTION,
              String.format(
                  "Error while trying to get hotel inventory for hotelId=%s, roomCountRequested=%s",
                  hotelId, roomCountRequested)));
        })
        .bodyToMono(HotelInventoryDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  public ItemInventoryResponseDto getItemsInventory(
      ItemInventoryRequestDto itemInventoryRequestDto) {
    return ohipWebClient.get()
        .uri(
            uriBuilder -> uriBuilder.path(availabilityOhipProperties.getItemsInventoryEndpoint())
                .queryParam(WELCOME_OFFER, Boolean.FALSE)
                .queryParam(SELL_IN_RESERVATION, Boolean.TRUE)
                .queryParam(START_DATE_PARAM, itemInventoryRequestDto.getStartDate())
                .queryParam(END_DATE_PARAM, itemInventoryRequestDto.getEndDate())
                .build(itemInventoryRequestDto.getHotelId())
        ).headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, itemInventoryRequestDto.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_HOTEL_ITEMS_INVENTORY_EXCEPTION,
              String.format("Error while trying to get items inventory for hotelId=%s",
                  itemInventoryRequestDto.getHotelId())
          ));
        })
        .bodyToMono(ItemInventoryResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager7Days",
      value = "OperaRoomTypesCache", key = "#hotelId")
  public RoomTypesResponseDto getRoomTypes(String hotelId) {
    String message = "Error while trying to get room types for hotelId=%s";
    return ohipWebClient.get()
        .uri(
            uriBuilder -> uriBuilder.path(availabilityOhipProperties.getRoomTypesEndpoint())
                .queryParam(OhipConstants.ROOM_TYPES_SUMMARY_INFO, Boolean.TRUE)
                .queryParam(OhipConstants.LIMIT, ROOM_TYPES_LIMIT)
                .queryParam(OhipConstants.PHYSICAL, Boolean.TRUE)
                .build(hotelId)
        ).headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return Mono.error(new OhipBadRequestException(ErrorCode.OHIP_ROOM_TYPES_EXCEPTIONS,
              String.format(message, hotelId)));
        })
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(ErrorCode.OHIP_ROOM_TYPES_EXCEPTIONS,
              String.format(message, hotelId)));
        })
        .bodyToMono(RoomTypesResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public PriceBreakdownDto getRateCodePricing(RateCodeCriteria rateCodeCriteria,
      RateCodeRoomInfoCriteria roomInfoCriteria) {

    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(CRITERIA_START_DATE, singletonList(rateCodeCriteria.getArrivalDate()));
    params.put(CRITERIA_END_DATE, singletonList(rateCodeCriteria.getDepartureDate()));
    params.put(RATE_PLAN_CODE, singletonList(rateCodeCriteria.getRatePlanCode()));
    params.put(ROOM_TYPE, singletonList(roomInfoCriteria.getRoomType()));
    params.put(ADULTS_PARAM, singletonList(roomInfoCriteria.getAdultsNo().toString()));
    params.put(CHILDREN_PARAM, singletonList(roomInfoCriteria.getChildrenNo().toString()));

    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(availabilityOhipProperties.getRateInfoEndpoint())
                .queryParams(params).build(rateCodeCriteria.getHotelId()))
        .headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, rateCodeCriteria.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(ErrorCode.OHIP_RATECODE_INFO_EXCEPTION,
              String.format("Error while trying to get rate code pricing for hotelId=%s",
                  rateCodeCriteria.getHotelId())));
        })
        .bodyToMono(PriceBreakdownDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public Mono<ResponseEntity<Void>> itemInventoryHold(
      HoldItemInventoryRequestDto holdItemInventoryRequestDto) {
    var hotelId = holdItemInventoryRequestDto.getHoldItemInfo().getHotelId();

    return ohipWebClient
        .post()
        .uri(uriBuilder ->
            uriBuilder.path(availabilityOhipProperties.getItemInventoryHoldEndpoint())
                .build(hotelId))
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, hotelId))
        .body(Mono.just(holdItemInventoryRequestDto), HoldItemInventoryRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelAvailabilityException(ErrorCode.OHIP_HOLD_ITEM_EXCEPTION,
              String.format(
                  "Error while trying to hold item for hotelId=%s",
                  holdItemInventoryRequestDto.getHoldItemInfo().getHotelId())));
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  public Mono<HotelAvailabilityDetailsDto> getHotelAvailabilityByIdsRequest(
      MultiHotelAvailabilityRequestDto availabilityRequest) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    for (String hotelId : availabilityRequest.getHotelIds()) {
      queryParams.add(HOTEL_IDS_PARAM, (hotelId));
      queryParams.add(LIMIT, AVAILABILITY_LIMIT);
    }
    queryParams.add(ROOM_STAY_START_DATE, availabilityRequest.getRoomStayStartDate());
    queryParams.add(ROOM_STAY_END_DATE, availabilityRequest.getRoomStayEndDate());
    if (!availabilityRequest.getRoomTypes().isEmpty()) {
      queryParams.add(ROOM_TYPE, String.join(",", availabilityRequest.getRoomTypes()));
    }
    queryParams.add(ROOM_STAY_QUANTITY, availabilityRequest.getRoomStayQuantity().toString());
    if (!StringUtils.isEmpty(availabilityRequest.getCompanyId())) {
      queryParams.add(RESERVATION_PROFILE_TYPE, COMPANY);
      queryParams.add(ATTACHED_PROFILE_ID, availabilityRequest.getCompanyId());
    }
    if (availabilityRequest.getRatePlanCodes() != null
        && !availabilityRequest.getRatePlanCodes().isEmpty()) {
      queryParams.add(RATE_PLAN_CODE, String.join(",", availabilityRequest.getRatePlanCodes()));
    }

    return ohipWebClient.get().uri(uriBuilder -> uriBuilder
            .path(availabilityOhipProperties.getMultiHotelAvaEndpoint())
            .queryParams(queryParams)
            .build())
        .headers(httpHeaders -> httpHeaders.add(OhipConstants.HUB_ID_HEADER,
            availabilityOhipProperties.getHubId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelAvailabilityException(
              ErrorCode.OHIP_RETRIVE_AVAILABILITY_BY_IDS_EXCEPTION,
              String.format("Error while trying to get availabilityByIds for hotelIds=%s",
                  availabilityRequest.getHotelIds())));
        })
        .bodyToMono(HotelAvailabilityDetailsDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  public Mono<HotelAvailability> getMultiHotelAvailabilityRequest(
      MultiHotelAvailabilityRequestDto availabilityRequest) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    for (String hotelId : availabilityRequest.getHotelIds()) {
      queryParams.add(HOTEL_IDS_PARAM, (hotelId));
      queryParams.add(RATE_PLAN_SET, availabilityRequest.getRatePlanSet());
      queryParams.add(LIMIT, AVAILABILITY_LIMIT);
    }
    queryParams.add(ROOM_STAY_START_DATE, availabilityRequest.getRoomStayStartDate());
    queryParams.add(ROOM_STAY_END_DATE, availabilityRequest.getRoomStayEndDate());
    if (!availabilityRequest.getRoomTypes().isEmpty()) {
      queryParams.add(ROOM_TYPE, String.join(",", availabilityRequest.getRoomTypes()));
    }
    queryParams.add(ROOM_STAY_QUANTITY, availabilityRequest.getRoomStayQuantity().toString());
    if (!StringUtils.isEmpty(availabilityRequest.getCompanyId())) {
      queryParams.add(RESERVATION_PROFILE_TYPE, COMPANY);
      queryParams.add(ATTACHED_PROFILE_ID, availabilityRequest.getCompanyId());
    }
    return ohipWebClient.get().uri(uriBuilder -> uriBuilder
            .path(availabilityOhipProperties.getMultiHotelAvaEndpoint())
            .queryParams(queryParams)
            .build())
        .headers(httpHeaders -> httpHeaders.add(OhipConstants.HUB_ID_HEADER,
            availabilityOhipProperties.getHubId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelAvailabilityException(
              ErrorCode.OHIP_MULTIHOTELS_AVAILABILITY_EXCEPTION,
              String.format(
                  "Error while trying to get multi hotel availabilities for hotelIds=%s",
                  availabilityRequest.getHotelIds().get(0))));
        })
        .bodyToMono(HotelAvailability.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  public Mono<MultiRoomRateAvailabilityResponseType> getHotelAvailabilityByIdsRequestV2(
      AvailabilityByIdsSearchCriteriaV2Dto requestDto) {
    return ohipWebClient
        .post()
        .uri(availabilityOhipProperties.getMultiRoomRateAvaEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, requestDto.getHotelIds().get(0)))
        .body(Mono.just(requestDto), AvailabilityByIdsSearchCriteriaV2Dto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelAvailabilityException(
              ErrorCode.OHIP_GET_MULTIROOM_RATE_AVAILABILITY_EXCEPTION,
              "Error while fetching multi room rate availability response"));
        })
        .bodyToMono(MultiRoomRateAvailabilityResponseType.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  public Mono<SearchPropertyResponseType> getMultiHotelAvailabilityRequestV2(
      MultiHotelAvailabilityRequestV2Dto requestDto) {
    return ohipWebClient
        .post()
        .uri(availabilityOhipProperties.getMinimumRateAvaEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .headers(httpHeaders -> httpHeaders.add(HOTEL_ID_HEADER, requestDto.getHotelIds().get(0)))
        .body(Mono.just(requestDto), MultiHotelAvailabilityRequestV2Dto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          log.error("Error while fetching multi room rate availability response");
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_MULTIHOTEL_AVAILABILITY_EXCEPTION,
              "Unable to get multi room rate availability response"));
        })
        .bodyToMono(SearchPropertyResponseType.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception));
  }

  public List<StatisticType> getHotelInventoryStatistics(
      AvailabilityRequestDto availabilityRequest) {

    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(
                    availabilityOhipProperties.getHotelInventoryStatisticsEndpoint())
                .queryParam(DATE_RANGE_START, availabilityRequest.getRoomStayStartDate())
                .queryParam(DATE_RANGE_END, availabilityRequest.getRoomStayEndDate())
                .queryParam(REPORT_CODE, ROOMS_AVAILABILITY_SUMMARY)
                .queryParam(PARAMETER_NAME, ROOM_TYPE_WILD_CARD_LIST)
                .queryParam(PARAMETER_VALUE, String.join(",", availabilityRequest.getRoomTypes()))
                .queryParam(PARAMETER_NAME, ROOM_PHYSICAL_ROOMS_YN)
                .queryParam(PARAMETER_VALUE, YES)
                .queryParam(PARAMETER_NAME, ROOM_AVAIL_ROOMS_YN)
                .queryParam(PARAMETER_VALUE, YES)
                .build(availabilityRequest.getHotelId()))
        .headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, availabilityRequest.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_INVENTORY_STATISTIC_EXCEPTION,
              String.format(
                  "Error while trying to get hotel inventory statistics for hotelId=%s",
                  availabilityRequest.getHotelId())));
        })
        .bodyToMono(new ParameterizedTypeReference<List<StatisticType>>() {
        })
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public RestrictionsByDateRangeResult getRestrictionsByDateRange(
      RestrictionsByDateRangeSearchCriteria restrictionsByDateRangeSearchCriteria) {

    return ohipWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(availabilityOhipProperties.getRestrictionsByDateRangeEndpoint())
            .queryParam(RESTRICTIONS_BY_DATE_START_DATE, restrictionsByDateRangeSearchCriteria.getStartDate())
            .queryParam(RESTRICTIONS_BY_DATE_END_DATE, restrictionsByDateRangeSearchCriteria.getEndDate())
            .build(restrictionsByDateRangeSearchCriteria.getHotelId()))
        .headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, restrictionsByDateRangeSearchCriteria.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelAvailabilityException(
              ErrorCode.OHIP_GET_RESTRICTIONS_BY_DATE_RANGE_EXCEPTION,
              String.format("Error while trying to get hotel restrictions by date range for "
                      + "hotelId=%s, start=%s, end=%s.",
                  restrictionsByDateRangeSearchCriteria.getHotelId(),
                  restrictionsByDateRangeSearchCriteria.getStartDate(),
                  restrictionsByDateRangeSearchCriteria.getEndDate())
          ));
        })
        .bodyToMono(RestrictionsByDateRangeResult.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}