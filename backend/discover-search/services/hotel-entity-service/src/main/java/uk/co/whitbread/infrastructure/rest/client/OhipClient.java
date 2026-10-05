package uk.co.whitbread.infrastructure.rest.client;

import static uk.co.whitbread.infrastructure.rest.client.utils.WebClientUtils.logErrorResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.exceptions.HotelAvailabilityBadReqException;
import uk.co.whitbread.domain.model.availability.in.MultiHotelRestrictionsByDateRangeRequest;
import uk.co.whitbread.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.domain.model.availability.in.RestrictionsByDateRangeRequest;
import uk.co.whitbread.domain.model.availability.in.RoomPriceBreakdownRequest;
import uk.co.whitbread.domain.model.availability.out.RateCodePricingResult;
import uk.co.whitbread.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.domain.model.availability.out.RoomPriceBreakdownResult;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AvailabilityByIdsResponseV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancellationReasonsResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DonationPackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelInventoryRoomTypeDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelPreferencesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelStatusDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ItemInventoryResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.KioskReservationPreferencesDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MultiAvailabilityRequestV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MultiAvailabilityResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MultiAvailabilityResponseV2Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlanInfoResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlansResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByBasketRefResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationLightweightResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomTypesInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.VacantRoomResponseDto;
import uk.co.whitbread.infrastructure.config.OhipProperties;
import uk.co.whitbread.infrastructure.rest.client.availability.exception.HotelAvailabilityException;
import uk.co.whitbread.infrastructure.rest.client.availability.model.HotelAvailabilityByIdsRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.HotelAvailabilityByIdsRequestOhipV2Dto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.HotelAvailabilityByIdsRequestOhipV3Dto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.HotelAvailabilityRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.HotelInventoryRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.ItemInventoryRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.MultiHotelAvaSearchCriteriaOhip;
import uk.co.whitbread.infrastructure.rest.client.ohip.exceptions.OhipClientException;
import uk.co.whitbread.infrastructure.rest.client.packages.model.DonationPackagesRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.packages.model.PackagesRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.utils.WebClientUtils;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityByIdsDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityDto;


@Slf4j
@RequiredArgsConstructor
public class OhipClient {

  public static final String ARRIVAL_DATE_QUERY_PARAM = "arrivalDate";
  public static final String DEPARTURE_DATE_QUERY_PARAM = "departureDate";
  public static final String ROOM_TYPES_QUERY_PARAM = "roomTypes";
  public static final String PMS_ROOM_TYPES_QUERY_PARAM = "pmsRoomTypes";
  public static final String ADULTS_QUERY_PARAM = "adults";
  public static final String CHILDREN_QUERY_PARAM = "children";
  public static final String COTS_REQUIRED_QUERY_PARAM = "cotsRequired";
  public static final String CHANNEL_QUERY_PARAM = "channel";
  public static final String HOTEL_ID = "hotelId";
  public static final String RATE_PLAN_CODES = "ratePlanCodes";
  public static final String RESERVATIONS_IDS = "reservationIds";
  public static final String START_DATE_PARAM = "startDate";
  public static final String END_DATE_PARAM = "endDate";
  public static final String ITEM_CODES_PARAM = "itemCodes";
  public static final String RATE_PLAN_CODE_PARAM = "ratePlanCode";
  public static final String PREFERENCES_GROUP_CODE = "preferenceGroupsCodes";
  private static final String HOTEL_IDS_PARAM = "hotelIds";
  private static final String MEAL_INCLUSIVE_RATE_PARAM = "mealInclusiveRate";
  private static final String ROOM_TYPE = "roomType";
  private static final String RESERVATION_ID = "reservationId";
  private static final String RESERVATION_IDS = "reservationIds";
  private static final String PRICE_BREAKDOWN_NEEDED = "priceBreakdownNeeded";
  private static final String RATE_INFO_NEEDED = "rateInfoNeeded";
  private static final String OPERA_UI_CREATED_RSV = "operaUiCreatedRsv";

  private final WebClient ohipWebClient;
  private final OhipProperties ohipProperties;

  public HotelAvailabilityDto getHotelAvailability(
      HotelAvailabilityRequestOhipDto hotelAvailabilityRequestOhipDto) {
    log.debug("Entered getHotelAvailability with hotelAvailabilityRequestOhipDto={}",
        hotelAvailabilityRequestOhipDto);

    return ohipWebClient.get().uri(uriBuilder -> uriBuilder
            .path(ohipProperties.getAvailabilitiesEndpoint())
            .queryParam(ARRIVAL_DATE_QUERY_PARAM,
                hotelAvailabilityRequestOhipDto.getArrivalDate())
            .queryParam(DEPARTURE_DATE_QUERY_PARAM,
                hotelAvailabilityRequestOhipDto.getDepartureDate())
            .queryParam(ROOM_TYPES_QUERY_PARAM,
                String.join(",", hotelAvailabilityRequestOhipDto.getRoomTypes()))
            .queryParam(ADULTS_QUERY_PARAM, String.join(",",
                hotelAvailabilityRequestOhipDto.getAdultsNumber().stream().map(Object::toString)
                    .toList()))
            .queryParam(CHILDREN_QUERY_PARAM,
                hotelAvailabilityRequestOhipDto.getChildrenNumber() != null ? String.join(",",
                    hotelAvailabilityRequestOhipDto.getChildrenNumber().stream().map(Object::toString)
                        .toList())
                    : String.join(",",
                    Collections.nCopies(hotelAvailabilityRequestOhipDto.getRoomTypes().size(), "0")))
            .queryParam(COTS_REQUIRED_QUERY_PARAM,
                hotelAvailabilityRequestOhipDto.getCotsRequired() != null ? String.join(",",
                    hotelAvailabilityRequestOhipDto.getCotsRequired().stream().map(Object::toString)
                        .toList())
                    : String.join(",",
                    Collections.nCopies(hotelAvailabilityRequestOhipDto.getRoomTypes().size(), "false")))
            .queryParam(CHANNEL_QUERY_PARAM, hotelAvailabilityRequestOhipDto.getChannel())
            .queryParam("subchannel", hotelAvailabilityRequestOhipDto.getSubchannel())
            .queryParam("language", hotelAvailabilityRequestOhipDto.getLanguage())
            .queryParamIfPresent("companyId",
                Optional.ofNullable(hotelAvailabilityRequestOhipDto.getCompanyId()))
            .queryParamIfPresent("promotionCode", Optional.ofNullable(
                hotelAvailabilityRequestOhipDto.getPromotionCode()))
            .build(hotelAvailabilityRequestOhipDto.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(HotelAvailabilityBadReqException.class);
        })
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(HotelAvailabilityDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to create hotel availability for hotelAvailabilityRequestOhipDto=%s",
            hotelAvailabilityRequestOhipDto)))
        .block();
  }

  public HotelAvailabilityByIdsDto getHotelAvailabilityByIds(
      HotelAvailabilityByIdsRequestOhipDto requestDto) {
    log.debug("Entered getHotelAvailabilityByIds with hotelAvailabilityByIdsRequestOhipDto={}",
        requestDto);

    return ohipWebClient.get().uri(uriBuilder -> uriBuilder
            .path(ohipProperties.getAvailabilityByIdsEndpoint())
            .queryParam(HOTEL_IDS_PARAM, String.join(",", requestDto.getHotelIds()))
            .queryParam(ARRIVAL_DATE_QUERY_PARAM, requestDto.getArrivalDate())
            .queryParam(DEPARTURE_DATE_QUERY_PARAM, requestDto.getDepartureDate())
            .queryParam(ROOM_TYPES_QUERY_PARAM, String.join(",", requestDto.getRoomTypes()))
            .queryParam(PMS_ROOM_TYPES_QUERY_PARAM,
                requestDto.getPmsRoomTypes() != null ? String.join(",",
                    requestDto.getPmsRoomTypes().stream().map(Object::toString).toList()) : null)
            .queryParam(ADULTS_QUERY_PARAM, String.join(",",
                requestDto.getAdultsNumber().stream().map(Object::toString).toList()))
            .queryParam(CHILDREN_QUERY_PARAM,
                requestDto.getChildrenNumber() != null ? String.join(",",
                    requestDto.getChildrenNumber().stream()
                        .map(Object::toString).toList()) : String.join(",",
                    Collections.nCopies(requestDto.getRoomTypes().size(), "0")))
            .queryParam(COTS_REQUIRED_QUERY_PARAM,
                requestDto.getCotsRequired() != null ? String.join(",",
                    requestDto.getCotsRequired().stream().map(Object::toString)
                        .toList()) : String.join(",",
                    Collections.nCopies(requestDto.getRoomTypes().size(),
                        "false")))
            .queryParamIfPresent(RATE_PLAN_CODES, Optional.ofNullable(
                requestDto.getRatePlanCodes() != null
                    ? String.join(",", requestDto.getRatePlanCodes()) : null))
            .queryParamIfPresent("globalCompanyId", Optional.ofNullable(requestDto.getGlobalCompanyId()))
            .queryParamIfPresent("negotiatedRateDisplaySets",
                Optional.ofNullable(requestDto.getNegotiatedRateDisplaySets()))
            .queryParam(CHANNEL_QUERY_PARAM, requestDto.getChannel())
            .queryParam("subchannel", requestDto.getSubchannel())
            .queryParam("language", requestDto.getLanguage())
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(HotelAvailabilityByIdsDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to create HotelAvailabilityByIds for hotelAvailabilityByIdsRequestOhipDto=%s",
            requestDto)))
        .block();
  }

  public Mono<RoomPriceBreakdownResult> getRoomPriceBreakdown(String hotelId,
      RoomPriceBreakdownRequest roomPriceBreakdownRequest) {
    return ohipWebClient.get().uri(uriBuilder -> uriBuilder.path(
                ohipProperties.getRoomPriceBreakdownEndpoint())
            .queryParam(ARRIVAL_DATE_QUERY_PARAM,
                roomPriceBreakdownRequest.getArrivalDate())
            .queryParam(DEPARTURE_DATE_QUERY_PARAM,
                roomPriceBreakdownRequest.getDepartureDate())
            .queryParam(ROOM_TYPES_QUERY_PARAM, roomPriceBreakdownRequest.getRoomTypes())
            .queryParam(RATE_PLAN_CODE_PARAM, roomPriceBreakdownRequest.getRatePlanCode())
            .queryParam("adultsNo", String.join(",",
                roomPriceBreakdownRequest.getAdultsNo().stream().map(Object::toString)
                    .toList()))
            .queryParam("childrenNo",
                roomPriceBreakdownRequest.getChildrenNo() != null ? String.join(",",
                    roomPriceBreakdownRequest.getChildrenNo().stream().map(Object::toString)
                        .toList()) : 0)
            .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(RoomPriceBreakdownResult.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to get room price breakdown!"));
  }

  public PackagesResponseDto getPackages(
      PackagesRequestOhipDto packagesRequestOhipDto) {
    log.debug(
        "Entered getPackages with packagesRequestOhipDto={}", packagesRequestOhipDto);

    return ohipWebClient.get().uri(uriBuilder -> uriBuilder
            .path(ohipProperties.getPackagesEndpoint())
            .queryParam(START_DATE_PARAM,
                packagesRequestOhipDto.getStartDate())
            .queryParam(END_DATE_PARAM,
                packagesRequestOhipDto.getEndDate())
            .queryParam(ADULTS_QUERY_PARAM,
                packagesRequestOhipDto.getAdultsNumber())
            .queryParamIfPresent(CHILDREN_QUERY_PARAM,
                Optional.of(packagesRequestOhipDto.getChildrenNumber()))
            .queryParam("nrNights",
                Optional.of(packagesRequestOhipDto.getNightsNumber()))
            .queryParamIfPresent(RATE_PLAN_CODE_PARAM,
                Optional.ofNullable(packagesRequestOhipDto.getRatePlanCode()))
            .queryParamIfPresent(MEAL_INCLUSIVE_RATE_PARAM,
                Optional.ofNullable(packagesRequestOhipDto.getMealInclusiveRate()))
        .build(packagesRequestOhipDto.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(PackagesResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to create package response for packagesRequestOhipDto=%s",
            packagesRequestOhipDto)))
        .block();
  }

  public DonationPackagesResponseDto getHotelCharityPackagesDetails(
      DonationPackagesRequestOhipDto donationPackagesRequestOhipDto) {
    log.debug(
        "Entered getDonationPackageDetails with donationPackagesRequestOhipDto={}",
        donationPackagesRequestOhipDto);

    return ohipWebClient.get().uri(uriBuilder -> uriBuilder
            .path(ohipProperties.getDonationPackagesEndpoint())
            .queryParam("packageCodes",
                String.join(",", donationPackagesRequestOhipDto.getPackageCodes()))
            .build(donationPackagesRequestOhipDto.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(DonationPackagesResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to get donation packages response "
                + "for donationPackagesRequestOhipDto=%s", donationPackagesRequestOhipDto)))
        .block();
  }

  public HotelInventoryRoomTypeDto getHotelRoomsInventory(
      HotelInventoryRequestOhipDto hotelInventoryRequestOhipDto) {
    log.debug(
        "Entered getHotelRoomsInventory with hotelInventoryRequestOhipDto={}",
        hotelInventoryRequestOhipDto);

    return ohipWebClient.get().uri(uriBuilder -> uriBuilder
            .path(ohipProperties.getHotelInventoryEndpoint())
            .queryParam("dateRangeStart",
                hotelInventoryRequestOhipDto.getDateRangeStart())
            .queryParam("dateRangeEnd",
                hotelInventoryRequestOhipDto.getDateRangeEnd())
            .build(hotelInventoryRequestOhipDto.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(HotelInventoryRoomTypeDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to get hotel inventory response for hotelInventoryRequestOhipDto=%s",
            hotelInventoryRequestOhipDto)))
        .block();

  }

  public ItemInventoryResponseDto getItemInventory(
            ItemInventoryRequestOhipDto itemInventoryRequestOhipDto) {
    log.debug(
                "Entered getItemInventory with itemInventoryRequestOhipDto={}",
                itemInventoryRequestOhipDto);

    return ohipWebClient.get().uri(uriBuilder -> uriBuilder
                        .path(ohipProperties.getItemInventoryEndpoint())
                        .queryParam(START_DATE_PARAM,
                                itemInventoryRequestOhipDto.getStartDate())
                        .queryParam(END_DATE_PARAM,
                                itemInventoryRequestOhipDto.getEndDate())
                        .queryParam(ITEM_CODES_PARAM, itemInventoryRequestOhipDto.getItemCodes())
                        .build(itemInventoryRequestOhipDto.getHotelId()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                  WebClientUtils.logErrorHeader(log, clientResponse);
                  return clientResponse.bodyToMono(OhipClientException.class);
                })
                .bodyToMono(ItemInventoryResponseDto.class)
                .doOnError(e -> log.warn(
                        "Error while trying to get item inventory response for itemInventoryRequestOhipDto={}",
                        itemInventoryRequestOhipDto, e))
                .block();

  }

  public RateCodePricingResult getRateCodePricing(RateCodeCriteria rateCodeCriteria) {
    log.debug(
        "Entered getRateCodePricing with rateCodeCriteria={}",
        rateCodeCriteria);

    return ohipWebClient.get().uri(uriBuilder -> uriBuilder
            .path(ohipProperties.getRateCodePricingEndpoint())
            .queryParam(ARRIVAL_DATE_QUERY_PARAM,
                rateCodeCriteria.getArrivalDate())
            .queryParam(DEPARTURE_DATE_QUERY_PARAM,
                rateCodeCriteria.getDepartureDate())
            .queryParam(RATE_PLAN_CODE_PARAM, rateCodeCriteria.getRatePlanCode())
            .queryParam(ROOM_TYPES_QUERY_PARAM,
                String.join(",", rateCodeCriteria.getRoomTypes()))
            .queryParam("adultsNo", String.join(",",
                rateCodeCriteria.getAdultsNo().stream().map(Object::toString)
                    .toList()))
            .queryParam("childrenNo",
                rateCodeCriteria.getChildrenNo() != null ? String.join(",",
                    rateCodeCriteria.getChildrenNo().stream().map(Object::toString)
                        .toList()) : 0)
            .build(rateCodeCriteria.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(RateCodePricingResult.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to get rate code pricing response for rateCodeCriteria=%s",
            rateCodeCriteria)))
        .block();
  }

  public HotelInfoDto getHotelInfo(String hotelId) {
    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(ohipProperties.getHotelInfoEndpoint())
                .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(HotelAvailabilityException.class);
        })
        .bodyToMono(HotelInfoDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to get hotel information!"))
        .block();
  }

  /**
   * Method that caches the wrapper Mono which also caches the response in order to avoid trips.
   *
   * @param hotelId The hotelId
   * @return RoomTypeInfo Mono
   */
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager12Hours",
      value = "OperaRoomTypesInfoCache", key = "#hotelId")
  public RoomTypesInfoDto getRoomTypesInfo(String hotelId) {
    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(ohipProperties.getRoomTypesInfoEndpoint())
                .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(RoomTypesInfoDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
                "Error while trying to get room types info from OHIP"))
        .block();
  }

  public MultiAvailabilityResponseDto getMultiHotelAvailability(
      MultiHotelAvaSearchCriteriaOhip multiHotelAvaSearchCriteriaOhip) {

    log.debug(
        "Entered getMultiHotelAvailability with multiHotelAvaSearchCriteriaOhip={}",
        multiHotelAvaSearchCriteriaOhip);

    return ohipWebClient.get().uri(uriBuilder -> uriBuilder.path(
                ohipProperties.getMultiHotelAvailabilitiesEndpoint())
            .queryParam(HOTEL_IDS_PARAM, String.join(",", multiHotelAvaSearchCriteriaOhip.getHotelIds()))
            .queryParam(ARRIVAL_DATE_QUERY_PARAM, multiHotelAvaSearchCriteriaOhip.getArrivalDate())
            .queryParam(DEPARTURE_DATE_QUERY_PARAM, multiHotelAvaSearchCriteriaOhip.getDepartureDate())
            .queryParam(CHANNEL_QUERY_PARAM, multiHotelAvaSearchCriteriaOhip.getChannel())
            .queryParam("numberOfRooms", String.join(",", multiHotelAvaSearchCriteriaOhip
                .getNumberOfRooms().stream().map(Object::toString).toList()))
            .queryParam(ROOM_TYPES_QUERY_PARAM, String.join(",", multiHotelAvaSearchCriteriaOhip.getRoomTypes()))
            .queryParam(ADULTS_QUERY_PARAM, String.join(",", multiHotelAvaSearchCriteriaOhip
                .getAdults().stream().map(Object::toString).toList()))
            .queryParam(CHILDREN_QUERY_PARAM,
                multiHotelAvaSearchCriteriaOhip.getChildren() != null ? String.join(",",
                    multiHotelAvaSearchCriteriaOhip.getChildren().stream().map(Object::toString)
                        .toList()) : 0)
            .queryParam(COTS_REQUIRED_QUERY_PARAM, multiHotelAvaSearchCriteriaOhip.getClass() != null
                ? String.join(",", multiHotelAvaSearchCriteriaOhip.getCotsRequired().stream()
                .map(Object::toString).toList()) : Boolean.FALSE)
            .queryParamIfPresent("companyId",
                Optional.ofNullable(multiHotelAvaSearchCriteriaOhip.getCompanyId()))
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(MultiAvailabilityResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
                "Error while trying to get multi hotel availabilities from OHIP"))
        .block();
  }

  public CancellationReasonsResponseDto getListOfCancellationReasons(String hotelId) {
    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(ohipProperties.getCancellationReasonsEndpoint())
                .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(CancellationReasonsResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
                "Error while trying to get list of cancellation reasons from OHIP"))
        .block();
  }

  public MultiAvailabilityResponseV2Dto getPostMultiHotelAvailability(
      MultiAvailabilityRequestV2Dto postMultiAvaNew) {
    return ohipWebClient
        .post()
        .uri(ohipProperties.getMultiHotelAvailabilitiesEndpoint2())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(postMultiAvaNew), MultiAvailabilityRequestV2Dto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(MultiAvailabilityResponseV2Dto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
                "Error while trying to create reservation guests"))
        .block();
  }

  public AvailabilityByIdsResponseV2Dto getHotelAvailabilityByIdsV2(
      HotelAvailabilityByIdsRequestOhipV2Dto ohipRequest) {
    return ohipWebClient
        .post()
        .uri(ohipProperties.getAvailabilityByIdsEndpointV2())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(ohipRequest), HotelAvailabilityByIdsRequestOhipV2Dto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(AvailabilityByIdsResponseV2Dto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
                "Error while trying to get availability by ids v2"))
        .block();
  }

  public AvailabilityByIdsResponseV2Dto getHotelAvailabilityByIdsV3(
      HotelAvailabilityByIdsRequestOhipV3Dto ohipRequest) {
    return ohipWebClient
        .post()
        .uri(ohipProperties.getAvailabilityByIdsEndpointV3())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(ohipRequest), HotelAvailabilityByIdsRequestOhipV3Dto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(AvailabilityByIdsResponseV2Dto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to get availability by ids v3"))
        .block();
  }

  public Mono<RatePlansResponseDto> getRatePlans(List<String> ratePlanCodes, String hotelId) {

    return ohipWebClient.get()
        .uri(
            uriBuilder -> uriBuilder.path(
                    ohipProperties.getRatePlansEndpoint())
                .queryParam(RATE_PLAN_CODES, ratePlanCodes)
                .queryParam(HOTEL_ID, hotelId)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(RatePlansResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to get rate plans!"));
  }

  public RatePlanInfoResponseDto getRatePlanInfo(String ratePlanCode, String hotelId) {
    return ohipWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(ohipProperties.getRatePlanInfoEndpoint())
            .queryParam(RATE_PLAN_CODE_PARAM, ratePlanCode)
            .queryParam(HOTEL_ID, hotelId)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(RatePlanInfoResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format("Error while trying to get rate plan info for ratePlanCode=%s, hotelId=%s",
                ratePlanCode, hotelId)))
        .block();
  }

  public HotelPreferencesResponseDto getPreferencesForGroup(String hotelId, String groupCode) {
    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(ohipProperties.getPreferencesEndpoint())
                .queryParam(PREFERENCES_GROUP_CODE, groupCode).build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        }).bodyToMono(HotelPreferencesResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public RestrictionsByDateRangeResult getRestrictionsByDateRange(
        RestrictionsByDateRangeRequest restrictionsByDateRangeRequest) {

    return ohipWebClient
          .get()
          .uri(uriBuilder ->
                uriBuilder.path(ohipProperties.getRestrictionsByDateRangeEndpoint())
                .queryParam(START_DATE_PARAM, restrictionsByDateRangeRequest.getStartDate())
                .queryParam(END_DATE_PARAM, restrictionsByDateRangeRequest.getEndDate())
                .build(restrictionsByDateRangeRequest.getHotelId()))
          .retrieve()
          .onStatus(HttpStatusCode::isError, clientResponse -> {
            WebClientUtils.logErrorHeader(log, clientResponse);
            return clientResponse.bodyToMono(OhipClientException.class);
          })
          .bodyToMono(RestrictionsByDateRangeResult.class)
          .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
                "Error while trying to get restrictions by date range from OHIP, "
                      + "hotelId=%s, startDate=%s, endDate=%s.",
                restrictionsByDateRangeRequest.getHotelId(),
                restrictionsByDateRangeRequest.getStartDate(),
                restrictionsByDateRangeRequest.getEndDate())))
          .block();
  }

  public List<RestrictionsByDateRangeResult> getMultiHotelRestrictionsByDateRange(
      MultiHotelRestrictionsByDateRangeRequest multiHotelRestrictionsByDateRangeRequest) {

    return ohipWebClient
        .get()
        .uri(uriBuilder ->
            uriBuilder.path(ohipProperties.getMultiHotelRestrictionsByDateRangeEndpoint())
                .queryParam(START_DATE_PARAM, multiHotelRestrictionsByDateRangeRequest.getStartDate())
                .queryParam(END_DATE_PARAM, multiHotelRestrictionsByDateRangeRequest.getEndDate())
                .queryParam(HOTEL_IDS_PARAM, multiHotelRestrictionsByDateRangeRequest.getHotelIds())
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(new ParameterizedTypeReference<List<RestrictionsByDateRangeResult>>() {})
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to get restrictions by date range from OHIP for a list of hotelIds, "
                + "hotelId=%s, startDate=%s, endDate=%s.",
            multiHotelRestrictionsByDateRangeRequest.getHotelIds(),
            multiHotelRestrictionsByDateRangeRequest.getStartDate(),
            multiHotelRestrictionsByDateRangeRequest.getEndDate())))
        .block();
  }

  public Mono<ReservationLightweightResponseDto> getLightweightReservations(String hotelId,
      Set<String> reservationIds) {

    return ohipWebClient.get()
        .uri(
            uriBuilder -> uriBuilder.path(
                    ohipProperties.getLightweightReservationsEndpoint())
                .queryParam(HOTEL_ID, hotelId)
                .queryParam(RESERVATIONS_IDS, reservationIds)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(ReservationLightweightResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to get lightweight reservation!"));
  }

  public List<HotelStatusDto> getOnSaleFlagFromOpera(List<String> hotelIds) {
    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(ohipProperties.getOnSaleFlagEndpoint())
                .queryParam(HOTEL_IDS_PARAM, String.join(",", hotelIds))
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(HotelAvailabilityBadReqException.class);
        })
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(HotelAvailabilityException.class);
        })
        .bodyToMono(new ParameterizedTypeReference<List<HotelStatusDto>>() {})
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to get OnSale Flag!"))
        .block();
  }


  public KioskReservationPreferencesDto fetchReservationPreferences(String hotelId,
      String reservationId) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(HOTEL_ID, hotelId);
    queryParams.add(RESERVATION_ID, reservationId);
    return ohipWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(ohipProperties.getReservationPreferences())
            .queryParams(queryParams)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return response.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(KioskReservationPreferencesDto.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e, String.format(
                "Error while trying to fetch Preferences for hotelId = %s and reservationId = %s",
                hotelId, reservationId)))
        .block();
  }

  public VacantRoomResponseDto getVacantRooms(String hotelId, String roomType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(HOTEL_ID, hotelId);
    queryParams.add(ROOM_TYPE, roomType);
    return ohipWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(ohipProperties.getGetVacantRoomsEndpoint())
            .queryParams(queryParams)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return response.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(VacantRoomResponseDto.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e,
                String.format("Error while trying to get vacant rooms for hotelId = %s", hotelId)))
        .block();
  }

  public ReservationByBasketRefResponseDto getReservationInfo(
      String hotelId,
      List<String> reservationsIds,
      Boolean priceBreakdownNeeded,
      Boolean operaUiCreated,
      Boolean rateInfoNeeded) {
    return ohipWebClient
        .get()
        .uri(uriBuilder ->
            uriBuilder.path(ohipProperties.getReservationInfo())
                .queryParam(RESERVATION_IDS,
                    String.join(",", reservationsIds.stream().map(Object::toString).toList()))
                .queryParam(HOTEL_ID, hotelId)
                .queryParam(PRICE_BREAKDOWN_NEEDED, priceBreakdownNeeded)
                .queryParam(OPERA_UI_CREATED_RSV, operaUiCreated)
                .queryParam(RATE_INFO_NEEDED, rateInfoNeeded)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return response.bodyToMono(OhipClientException.class);
        })
        .bodyToMono(ReservationByBasketRefResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to get reservations by ids for hotelId=%s, reservationsIds=%s, "
                + "priceBreakdownNeeded=%s, operaUiCreated=%s, rateInfoNeeded=%s.",
            hotelId, reservationsIds, priceBreakdownNeeded, operaUiCreated, rateInfoNeeded)))
        .block();
  }
}
