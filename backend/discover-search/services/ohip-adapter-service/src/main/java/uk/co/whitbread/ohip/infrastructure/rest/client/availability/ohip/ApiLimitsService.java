package uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.OfferTotalType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.SearchPropertyResponseType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.SearchPropertyRoomStayType;
import uk.co.whitbread.ohip.domain.model.availability.in.ItemInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeRoomInfoCriteria;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.AmountTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.DetailDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelAvailabilityDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelAvailabilityDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.ItemInventoryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.PriceBreakdownDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RatesTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RoomRateTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RoomStayTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.SummaryDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.TotalTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.InventoryAvailabilityDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.ItemInventoryDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.ItemInventoryResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.MultiHotelAvailabilityRequestV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.properties.AvailabilityOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.DateUtils;

@Component
@Slf4j
public class ApiLimitsService {

  private static final String YYYY_MM_DD = "yyyy-MM-dd";
  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);

  private final AvailabilityOhipProperties availabilityProperties;

  private final OhipAvailabilityClient ohipAvailabilityClient;

  private final Executor apiLimitsExecutor;

  public ApiLimitsService(
      AvailabilityOhipProperties availabilityProperties,
      OhipAvailabilityClient ohipAvailabilityClient,
      @Qualifier("apiLimitsExecutor") Executor apiLimitsExecutor) {
    this.availabilityProperties = availabilityProperties;
    this.ohipAvailabilityClient = ohipAvailabilityClient;
    this.apiLimitsExecutor = apiLimitsExecutor;
  }

  @SneakyThrows
  public ItemInventoryResponseDto getItemInventoryResponses(
      ItemInventoryRequest itemInventoryRequest) {
    List<Pair<LocalDate, LocalDate>> intervals = DateUtils.splitDateRange(
        itemInventoryRequest.getStartDate(), itemInventoryRequest.getEndDate(),
        availabilityProperties.getMaxRequestedDays(), YYYY_MM_DD);

    try {
      Instant startTime = Instant.now();

      // Single interval - execute synchronously
      if (intervals.size() == 1) {
        Pair<LocalDate, LocalDate> interval = intervals.get(0);
        ItemInventoryRequestDto request = ItemInventoryRequestDto.builder()
            .hotelId(itemInventoryRequest.getHotelId())
            .startDate(interval.getLeft().toString())
            .endDate(interval.getRight().toString())
            .build();

        var result = ohipAvailabilityClient.getItemsInventory(request);
        Instant endTime = Instant.now();
        log.info("Completed synchronous call for items inventory for 1 interval in {} milliseconds.",
            Duration.between(startTime, endTime).toMillis());
        return result;
      }

      // Multiple intervals - execute asynchronously
      var futureList = getItemInventoryList(itemInventoryRequest.getHotelId(), intervals);

      Instant endTime = Instant.now();
      var message = String.format(
          "Completed calls for items inventory for %s intervals in %s milliseconds.",
          intervals.size(), Duration.between(startTime, endTime).toMillis());
      log.info(message);

      Map<ItemInventoryDto, List<InventoryAvailabilityDto>> resultMap = futureList.stream()
          .map(CompletableFuture::join).toList().stream()
          .map(ItemInventoryResponseDto::getItemsInventory)
          .flatMap(Collection::stream)
          .reduce(
              new HashMap<>(), (map, element) -> {
                ItemInventoryDto key = ItemInventoryDto.builder()
                    .name(element.getName())
                    .code(element.getCode())
                    .description(element.getDescription())
                    .build();
                if (Objects.isNull(map.get(key))) {
                  map.put(key, element.getInventories());
                } else {
                  List<InventoryAvailabilityDto> list = new ArrayList<>();
                  list.addAll(map.get(key));
                  list.addAll(element.getInventories());
                  map.put(key, list);
                }
                return map;
              },
              (map1, map2) -> {
                map1.putAll(map2);
                return map1;
              }
          );

      return ItemInventoryResponseDto.builder()
          .itemsInventory(resultMap.keySet().stream().map(key -> ItemInventoryDto.builder()
                  .name(key.getName())
                  .code(key.getCode())
                  .description(key.getDescription())
                  .inventories(resultMap.get(key))
                  .build())
              .toList())
          .build();
    } catch (CompletionException ex) {
      throw ex.getCause();
    }
  }

  @SneakyThrows
  public HotelAvailabilityDetailsDto getHotelAvailabilityResponses(
      AvailabilityRequestDto availabilityRequestDto) {
    var startDate = LocalDate.parse(availabilityRequestDto.getRoomStayStartDate(), formatter);
    List<Pair<LocalDate, LocalDate>> intervals = DateUtils.splitDateRange(
        availabilityRequestDto.getRoomStayStartDate(),
        availabilityRequestDto.getRoomStayEndDate(),
        availabilityProperties.getMaxRequestedDays() - 1, YYYY_MM_DD);
    var newIntervals = intervals.stream()
        .map(p -> {
              if (startDate.compareTo(p.getLeft()) != 0) {
                return Pair.of(p.getLeft().minusDays(1), p.getRight());
              }
              return p;
            }
        ).toList();

    try {
      Instant startTime = Instant.now();

      // Single interval - execute synchronously
      if (newIntervals.size() == 1) {
        Pair<LocalDate, LocalDate> interval = newIntervals.get(0);
        AvailabilityRequestDto request = AvailabilityRequestDto.builder()
            .hotelId(availabilityRequestDto.getHotelId())
            .roomTypes(availabilityRequestDto.getRoomTypes())
            .children(availabilityRequestDto.getChildren())
            .adults(availabilityRequestDto.getAdults())
            .roomStayQuantity(availabilityRequestDto.getRoomStayQuantity())
            .roomStayStartDate(interval.getLeft().toString())
            .roomStayEndDate(interval.getRight().toString())
            .cotsRequired(availabilityRequestDto.getCotsRequired())
            .promotionCode(availabilityRequestDto.getPromotionCode())
            .ratePlanCode(availabilityRequestDto.getRatePlanCode())
            .roomSubstitutions(availabilityRequestDto.getRoomSubstitutions())
            .ratePlanSet(availabilityRequestDto.getRatePlanSet())
            .companyId(availabilityRequestDto.getCompanyId())
            .channel(availabilityRequestDto.getChannel())
            .subchannel(availabilityRequestDto.getSubchannel())
            .language(availabilityRequestDto.getLanguage())
            .build();

        var result = ohipAvailabilityClient.getHotelAvailabilityRequest(request).block();
        Instant endTime = Instant.now();
        log.info("Completed synchronous call for hotel availability for 1 interval in {} milliseconds.",
            Duration.between(startTime, endTime).toMillis());
        return result;
      }

      // Multiple intervals - execute asynchronously
      var futureList = getHotelAvailabilityList(availabilityRequestDto, newIntervals);
      Instant endTime = Instant.now();
      var message = String.format(
          "Completed calls for the hotel availability list for %s intervals in %s milliseconds.",
          newIntervals.size(), Duration.between(startTime, endTime).toMillis());
      log.info(message);

      if (eachIntervalHasAvailability(futureList, newIntervals.size())) {
        var roomTypes = getRoomTypes(mergeIntervalAvailability(futureList));
        var hotelAvailabilityDto = futureList.stream()
            .map(CompletableFuture::join).toList().stream()
            .map(HotelAvailabilityDetailsDto::getHotelAvailability)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .findFirst();

        RoomStayTypeDto roomStays = RoomStayTypeDto.builder().roomRates(roomTypes).build();
        var availability = HotelAvailabilityDto.builder()
            .hotelId(availabilityRequestDto.getHotelId())
            .roomStays(List.of(roomStays))
            .build();
        if (hotelAvailabilityDto.isPresent()) {
          availability.setHotelId(hotelAvailabilityDto.get().getHotelId());
          availability.setRedemption(hotelAvailabilityDto.get().isRedemption());
          availability.setHasMore(hotelAvailabilityDto.get().isHasMore());
          availability.setClosed(hotelAvailabilityDto.get().isClosed());
          availability.setRatePlanSet(hotelAvailabilityDto.get().getRatePlanSet());
        }
        return HotelAvailabilityDetailsDto.builder()
            .hotelAvailability(List.of(availability))
            .build();
      }

      return HotelAvailabilityDetailsDto.builder()
          .hotelAvailability(List.of(HotelAvailabilityDto.builder()
              .hotelId(availabilityRequestDto.getHotelId())
              .roomStays(
                  List.of(RoomStayTypeDto.builder().roomRates(Collections.emptyList()).build()))
              .build()))
          .build();
    } catch (CompletionException ex) {
      throw ex.getCause();
    }
  }


  @SneakyThrows
  public PriceBreakdownDto getRateInfoResponse(RateCodeCriteria rateCodeCriteria,
      RateCodeRoomInfoCriteria roomInfoCriteria) {
    var startDate = LocalDate.parse(rateCodeCriteria.getArrivalDate(), formatter);
    List<Pair<LocalDate, LocalDate>> intervals = DateUtils.splitDateRange(
        rateCodeCriteria.getArrivalDate(),
        rateCodeCriteria.getDepartureDate(),
        availabilityProperties.getMaxRequestedDaysRateInfo() - 1, YYYY_MM_DD);
    var newIntervals = intervals.stream()
        .map(p -> {
              if (startDate.compareTo(p.getLeft()) != 0) {
                return Pair.of(p.getLeft().minusDays(1), p.getRight());
              }
              return p;
            }
        ).toList();

    try {
      Instant startTime = Instant.now();

      // Single interval - execute synchronously
      if (newIntervals.size() == 1) {
        Pair<LocalDate, LocalDate> interval = newIntervals.get(0);
        RateCodeCriteria requestCriteria = RateCodeCriteria.builder()
            .hotelId(rateCodeCriteria.getHotelId())
            .arrivalDate(interval.getLeft().toString())
            .departureDate(interval.getRight().toString())
            .ratePlanCode(rateCodeCriteria.getRatePlanCode())
            .build();
        RateCodeRoomInfoCriteria requestRoomInfo = RateCodeRoomInfoCriteria.builder()
            .adultsNo(roomInfoCriteria.getAdultsNo())
            .childrenNo(roomInfoCriteria.getChildrenNo())
            .roomType(roomInfoCriteria.getRoomType())
            .build();

        var result = ohipAvailabilityClient.getRateCodePricing(requestCriteria, requestRoomInfo);
        Instant endTime = Instant.now();
        log.info("Completed synchronous call for get rate info hotelId={} for 1 interval in {} milliseconds.",
            rateCodeCriteria.getHotelId(), Duration.between(startTime, endTime).toMillis());
        return result;
      }

      // Multiple intervals - execute asynchronously
      var futureList = getRateInfoResponseList(rateCodeCriteria, roomInfoCriteria, newIntervals);

      Instant endTime = Instant.now();
      var message = String.format(
          "Completed calls for get rate info hotelId=%s for %s intervals in %s milliseconds.",
          rateCodeCriteria.getHotelId(), newIntervals.size(),
          Duration.between(startTime, endTime).toMillis());
      log.info(message);

      SummaryDto summary = getSummaryDto(rateCodeCriteria.getArrivalDate(),
          rateCodeCriteria.getDepartureDate(), futureList);

      return PriceBreakdownDto.builder()
          .summary(summary)
          .build();
    } catch (CompletionException ex) {
      throw ex.getCause();
    }
  }

  @SneakyThrows
  public PriceBreakdownDto getRateInfoResponse(AvailabilityRequestDto availabilityRequest) {
    var startDate = LocalDate.parse(availabilityRequest.getRoomStayStartDate(), formatter);
    List<Pair<LocalDate, LocalDate>> intervals = DateUtils.splitDateRange(
        availabilityRequest.getRoomStayStartDate(),
        availabilityRequest.getRoomStayEndDate(),
        availabilityProperties.getMaxRequestedDaysRateInfo() - 1, YYYY_MM_DD);
    var newIntervals = intervals.stream()
        .map(p -> {
              if (startDate.compareTo(p.getLeft()) != 0) {
                return Pair.of(p.getLeft().minusDays(1), p.getRight());
              }
              return p;
            }
        ).toList();

    try {
      Instant startTime = Instant.now();

      // Single interval - execute synchronously
      if (newIntervals.size() == 1) {
        Pair<LocalDate, LocalDate> interval = newIntervals.get(0);
        AvailabilityRequestDto request = AvailabilityRequestDto.builder()
            .hotelId(availabilityRequest.getHotelId())
            .roomTypes(availabilityRequest.getRoomTypes())
            .children(availabilityRequest.getChildren())
            .adults(availabilityRequest.getAdults())
            .channel(availabilityRequest.getChannel())
            .subchannel(availabilityRequest.getSubchannel())
            .roomSubstitutions(availabilityRequest.getRoomSubstitutions())
            .companyId(availabilityRequest.getCompanyId())
            .promotionCode(availabilityRequest.getPromotionCode())
            .language(availabilityRequest.getLanguage())
            .cotsRequired(availabilityRequest.getCotsRequired())
            .ratePlanSet(availabilityRequest.getRatePlanSet())
            .ratePlanCode(availabilityRequest.getRatePlanCode())
            .roomStayQuantity(availabilityRequest.getRoomStayQuantity())
            .roomStayStartDate(interval.getLeft().toString())
            .roomStayEndDate(interval.getRight().toString())
            .build();

        var result = ohipAvailabilityClient.getPriceBreakdownPerNight(request).block();
        Instant endTime = Instant.now();
        log.info("Completed synchronous call for get rate info hotelId={} for 1 interval in {} milliseconds.",
            availabilityRequest.getHotelId(), Duration.between(startTime, endTime).toMillis());
        return result;
      }

      // Multiple intervals - execute asynchronously
      var futureList = getRateInfoResponseList(availabilityRequest, newIntervals);

      SummaryDto summary = getSummaryDto(availabilityRequest.getRoomStayStartDate(),
          availabilityRequest.getRoomStayEndDate(), futureList);
      Instant endTime = Instant.now();
      var message = String.format(
          "Completed calls for get rate info hotelId=%s for %s intervals in %s milliseconds.",
          availabilityRequest.getHotelId(), newIntervals.size(),
          Duration.between(startTime, endTime).toMillis());
      log.info(message);
      return PriceBreakdownDto.builder()
          .summary(summary)
          .build();
    } catch (CompletionException ex) {
      throw ex.getCause();
    }
  }

  @SneakyThrows
  public SearchPropertyResponseType getMultiHotelAvailabilityRequestV2(
      MultiHotelAvailabilityRequestV2Dto requestDto) {
    var startDate = requestDto.getArrivalDate();
    List<Pair<LocalDate, LocalDate>> intervals = DateUtils.splitDateRange(
        requestDto.getArrivalDate().toString(),
        requestDto.getDepartureDate().toString(),
        availabilityProperties.getMaxRequestedDays() - 1, YYYY_MM_DD);
    var newIntervals = intervals.stream()
        .map(p -> {
              if (startDate.compareTo(p.getLeft()) != 0) {
                return Pair.of(p.getLeft().minusDays(1), p.getRight());
              }
              return p;
            }
        ).toList();
    try {
      Instant startTime = Instant.now();

      // Single interval - execute synchronously
      if (newIntervals.size() == 1) {
        Pair<LocalDate, LocalDate> interval = newIntervals.get(0);
        MultiHotelAvailabilityRequestV2Dto request = MultiHotelAvailabilityRequestV2Dto.builder()
            .hotelIds(requestDto.getHotelIds())
            .arrivalDate(interval.getLeft())
            .departureDate(interval.getRight())
            .rooms(requestDto.getRooms())
            .minRate(requestDto.getMinRate())
            .limit(requestDto.getLimit())
            .accountId(requestDto.getAccountId())
            .offset(requestDto.getOffset())
            .includePublicRates(requestDto.getIncludePublicRates())
            .sortBy(requestDto.getSortBy())
            .build();

        var result = ohipAvailabilityClient.getMultiHotelAvailabilityRequestV2(request).block();
        Instant endTime = Instant.now();
        var logMessage = String.format(
            "Completed synchronous call for multi-hotel availability hotelIds=%s for 1 interval "
            + "in %s milliseconds.",
            requestDto.getHotelIds(), Duration.between(startTime, endTime).toMillis());
        log.info(logMessage);
        return result;
      }

      // Multiple intervals - execute asynchronously
      var futureList = getMultiHotelAvailabilityRequestV2ResponseList(requestDto, newIntervals);
      Instant endTime = Instant.now();
      var message = String.format(
          "Completed calls for get rate info hotelIds=%s for %s intervals in %s milliseconds.",
          requestDto.getHotelIds(), newIntervals.size(),
          Duration.between(startTime, endTime).toMillis());
      log.info(message);
      return getSearchPropertyResponseType(futureList, intervals);
    } catch (CompletionException ex) {
      throw ex.getCause();
    }
  }

  private SearchPropertyResponseType getSearchPropertyResponseType(
      List<CompletableFuture<SearchPropertyResponseType>> futureList,
      List<Pair<LocalDate, LocalDate>> intervals) {

    var result = mergeResults(futureList);
    SearchPropertyResponseType response = result.getLeft();
    Map<String, List<SearchPropertyRoomStayType>> resultMap = result.getRight();

    List<SearchPropertyRoomStayType> roomStays = new ArrayList<>();
    resultMap.forEach((key, value) -> {
      if (!value.isEmpty() && value.size() == intervals.size()) {
        SearchPropertyRoomStayType roomStayType = buildSearchPropertyRoomStayType(value);
        roomStays.add(roomStayType);
      }
    });
    response.setRoomStays(roomStays);
    return response;
  }

  private SearchPropertyRoomStayType buildSearchPropertyRoomStayType(
      List<SearchPropertyRoomStayType> value) {
    SearchPropertyRoomStayType roomStayType = new SearchPropertyRoomStayType();
    roomStayType.setAvailability(value.get(0).getAvailability());
    roomStayType.setPropertyInfo(value.get(0).getPropertyInfo());
    roomStayType.setRoomClass(value.get(0).getRoomClass());
    roomStayType.setRoomTags(value.get(0).getRoomTags());

    var amountAfterTax = value.stream().map(SearchPropertyRoomStayType::getMinimumRate)
        .filter(Objects::nonNull)
        .map(OfferTotalType::getAmountAfterTax)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    OfferTotalType total = new OfferTotalType();
    total.setAmountAfterTax(amountAfterTax);
    if (Objects.nonNull(value.get(0).getMinimumRate())) {
      total.setCurrencyCode(value.get(0).getMinimumRate().getCurrencyCode());
    }
    roomStayType.setMinimumRate(total);
    return roomStayType;
  }

  private Pair<SearchPropertyResponseType, Map<String, List<SearchPropertyRoomStayType>>> mergeResults(
      List<CompletableFuture<SearchPropertyResponseType>> futureList) {
    SearchPropertyResponseType response = new SearchPropertyResponseType();
    Map<String, List<SearchPropertyRoomStayType>> resultMap = futureList.stream()
        .filter(Objects::nonNull)
        .map(CompletableFuture::join).toList()
        .stream()
        .map(el -> {
          response.setLimit(el.getLimit());
          response.setHasMore(el.getHasMore());
          response.setOffset(el.getOffset());
          return el.getRoomStays();
        })
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .reduce(
            new HashMap<>(), (map, element) -> {
              String key = element.getPropertyInfo().getHotelCode();
              if (Objects.isNull(map.get(key))) {
                map.put(key, List.of(element));
              } else {
                List<SearchPropertyRoomStayType> list = new ArrayList<>();
                list.addAll(map.get(key));
                list.add(element);
                map.put(key, list);
              }
              return map;
            },
            (map1, map2) -> {
              map1.putAll(map2);
              return map1;
            }
        );
    return Pair.of(response, resultMap);
  }

  private List<CompletableFuture<SearchPropertyResponseType>> getMultiHotelAvailabilityRequestV2ResponseList(
      MultiHotelAvailabilityRequestV2Dto requestDto, List<Pair<LocalDate, LocalDate>> intervals) {
    return intervals
        .stream()
        .map(dateRangeSearchCriteria -> MultiHotelAvailabilityRequestV2Dto.builder()
            .hotelIds(requestDto.getHotelIds())
            .arrivalDate(dateRangeSearchCriteria.getLeft())
            .departureDate(dateRangeSearchCriteria.getRight())
            .rooms(requestDto.getRooms())
            .minRate(requestDto.getMinRate())
            .limit(requestDto.getLimit())
            .accountId(requestDto.getAccountId())
            .offset(requestDto.getOffset())
            .includePublicRates(requestDto.getIncludePublicRates())
            .sortBy(requestDto.getSortBy())
            .build())
        .map(request -> CompletableFuture.supplyAsync(() -> {
          try {
            Instant requestStartTime = Instant.now();
            var result = ohipAvailabilityClient.getMultiHotelAvailabilityRequestV2(request).block();
            Instant requestEndTime = Instant.now();
            var message = String.format(
                "Request for minimumRateAvailability, hotelIds=%s, interval=[%s,%s] took %s milliseconds "
                    + "and ended at %s.",
                request.getHotelIds(), request.getArrivalDate(),
                request.getDepartureDate(),
                Duration.between(requestStartTime, requestEndTime).toMillis(),
                requestEndTime);
            log.info(message);
            return result;
          } catch (Exception ex) {
            var message = String.format(
                "Request for minimumRateAvailability, hotelIds=%s, interval=[%s,%s]  fails with error: %s",
                request.getHotelIds(), request.getArrivalDate(),
                request.getDepartureDate(), ex.getMessage());
            log.info(message);
            throw ex;
          }
        }, apiLimitsExecutor))
        .toList();
  }

  private static SummaryDto getSummaryDto(String startDate, String endDate,
      List<CompletableFuture<PriceBreakdownDto>> futureList) {
    List<SummaryDto> summaryStream = futureList.stream()
        .filter(Objects::nonNull)
        .map(CompletableFuture::join).toList()
        .stream()
        .map(PriceBreakdownDto::getSummary)
        .toList();

    List<DetailDto> details = summaryStream.stream().map(SummaryDto::getDetails)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .toList();

    var net = summaryStream.stream().map(SummaryDto::getNet)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var gross = summaryStream.stream().map(SummaryDto::getGross)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    List<SummaryDto> list = summaryStream.stream().toList();
    var currencyCode = !list.isEmpty() ? list.get(0).getCurrencyCode() : null;
    boolean hasSuppressedRate = false;
    if (!list.isEmpty()) {
      hasSuppressedRate = list.get(0).isHasSuppressedRate();
    }

    return SummaryDto.builder()
        .start(startDate)
        .end(endDate)
        .details(details)
        .net(net)
        .gross(gross)
        .currencyCode(currencyCode)
        .hasSuppressedRate(hasSuppressedRate)
        .build();
  }

  @SneakyThrows
  private List<CompletableFuture<PriceBreakdownDto>> getRateInfoResponseList(
      AvailabilityRequestDto availabilityRequest,
      List<Pair<LocalDate, LocalDate>> intervals) {
    return intervals
        .stream()
        .map(dateRangeSearchCriteria -> AvailabilityRequestDto.builder()
            .hotelId(availabilityRequest.getHotelId())
            .roomTypes(availabilityRequest.getRoomTypes())
            .children(availabilityRequest.getChildren())
            .adults(availabilityRequest.getAdults())
            .channel(availabilityRequest.getChannel())
            .subchannel(availabilityRequest.getSubchannel())
            .roomSubstitutions(availabilityRequest.getRoomSubstitutions())
            .companyId(availabilityRequest.getCompanyId())
            .promotionCode(availabilityRequest.getPromotionCode())
            .language(availabilityRequest.getLanguage())
            .cotsRequired(availabilityRequest.getCotsRequired())
            .ratePlanSet(availabilityRequest.getRatePlanSet())
            .ratePlanCode(availabilityRequest.getRatePlanCode())
            .roomStayQuantity(availabilityRequest.getRoomStayQuantity())
            .roomStayStartDate(dateRangeSearchCriteria.getLeft().toString())
            .roomStayEndDate(dateRangeSearchCriteria.getRight().toString())
            .build())
        .map(request -> CompletableFuture.supplyAsync(() -> {
          try {
            Instant requestStartTime = Instant.now();
            var result = ohipAvailabilityClient.getPriceBreakdownPerNight(request).block();
            Instant requestEndTime = Instant.now();
            var message = String.format(
                "Request for get rate info, hotelId=%s, interval=[%s,%s] took %s milliseconds and ended at %s.",
                request.getHotelId(), request.getRoomStayStartDate(),
                request.getRoomStayEndDate(),
                Duration.between(requestStartTime, requestEndTime).toMillis(),
                requestEndTime);
            log.info(message);
            return result;
          } catch (Exception ex) {
            var message = String.format(
                "Request for get rate info, hotelId=%s, interval=[%s,%s]  fails with error: %s",
                request.getHotelId(), request.getRoomStayStartDate(),
                request.getRoomStayEndDate(), ex.getMessage());
            log.info(message);
            throw ex;
          }
        }, apiLimitsExecutor))
        .toList();
  }

  @SneakyThrows
  private List<CompletableFuture<PriceBreakdownDto>> getRateInfoResponseList(
      RateCodeCriteria rateCodeCriteria, RateCodeRoomInfoCriteria roomInfoCriteria,
      List<Pair<LocalDate, LocalDate>> intervals) {
    return intervals
        .stream()
        .map(dateRangeSearchCriteria -> Pair.of(RateCodeCriteria.builder()
                .hotelId(rateCodeCriteria.getHotelId())
                .arrivalDate(dateRangeSearchCriteria.getLeft().toString())
                .departureDate(dateRangeSearchCriteria.getRight().toString())
                .ratePlanCode(rateCodeCriteria.getRatePlanCode())
                .build(),
            RateCodeRoomInfoCriteria.builder()
                .adultsNo(roomInfoCriteria.getAdultsNo())
                .childrenNo(roomInfoCriteria.getChildrenNo())
                .roomType(roomInfoCriteria.getRoomType()).build()))
        .map(pair -> CompletableFuture.supplyAsync(() -> {
          try {
            Instant requestStartTime = Instant.now();
            var result = ohipAvailabilityClient.getRateCodePricing(pair.getLeft(), pair.getRight());
            Instant requestEndTime = Instant.now();
            var message = String.format(
                "Request for get rate info, hotelId=%s, interval=[%s,%s] took %s milliseconds and ended at %s.",
                pair.getLeft().getHotelId(), pair.getLeft().getArrivalDate(),
                pair.getLeft().getDepartureDate(),
                Duration.between(requestStartTime, requestEndTime).toMillis(),
                requestEndTime);
            log.info(message);
            return result;
          } catch (Exception ex) {
            var message = String.format(
                "Request for get rate info, hotelId=%s, interval=[%s,%s]  fails with error: %s",
                pair.getLeft().getHotelId(), pair.getLeft().getArrivalDate(),
                pair.getLeft().getDepartureDate(), ex.getMessage());
            log.info(message);
            throw ex;
          }
        }, apiLimitsExecutor))
        .toList();
  }

  private List<RoomRateTypeDto> getRoomTypes(Map<RoomRateTypeDto, List<RatesTypeDto>> resultMap) {
    return resultMap.keySet().stream().map(key ->
        RoomRateTypeDto.builder()
            .start(key.getStart())
            .end(key.getEnd())
            .suppressRate(key.getSuppressRate())
            .ratePlanCode(key.getRatePlanCode())
            .ratePlanSet(key.getRatePlanSet())
            .roomType(key.getRoomType())
            .marketCode(key.getMarketCode())
            .promotionCode(key.getPromotionCode())
            .numberOfUnits(key.getNumberOfUnits())
            .total(TotalTypeDto.builder()
                .decimalPlaces(
                    Objects.nonNull(key.getTotal()) ? key.getTotal().getDecimalPlaces() : null)
                .currencyCode(
                    Objects.nonNull(key.getTotal()) ? key.getTotal().getCurrencyCode() : null)
                .currencySymbol(
                    Objects.nonNull(key.getTotal()) ? key.getTotal().getCurrencySymbol() : null)
                .rateOverride(
                    Objects.nonNull(key.getTotal()) ? key.getTotal().getRateOverride() : null)
                .amountBeforeTax((resultMap.get(key).stream()
                    .map(RatesTypeDto::getRate)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream))
                    .map(AmountTypeDto::getTotal)
                    .filter(Objects::nonNull)
                    .map(TotalTypeDto::getAmountBeforeTax)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                .amountAfterTax((resultMap.get(key).stream()
                    .map(RatesTypeDto::getRate)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream))
                    .filter(Objects::nonNull)
                    .map(AmountTypeDto::getTotal)
                    .filter(Objects::nonNull)
                    .map(TotalTypeDto::getAmountAfterTax)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                .decimalPlaces(
                    Objects.nonNull(key.getTotal()) ? key.getTotal().getDecimalPlaces() : null)
                .description(
                    Objects.nonNull(key.getTotal()) ? key.getTotal().getDescription() : null)
                .build())
            .rates(RatesTypeDto.builder()
                .rate(resultMap.get(key).stream()
                    .map(RatesTypeDto::getRate)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .toList())
                .build())
            .build()).toList();
  }

  private List<CompletableFuture<ItemInventoryResponseDto>> getItemInventoryList(
      String hotelId, List<Pair<LocalDate, LocalDate>> intervals) {
    return intervals
        .stream()
        .map(dateRangeSearchCriteria -> (
            ItemInventoryRequestDto.builder()
                .hotelId(hotelId)
                .startDate(dateRangeSearchCriteria.getLeft().toString())
                .endDate(dateRangeSearchCriteria.getRight().toString())
                .build()
        ))
        .map(request -> CompletableFuture.supplyAsync(() -> {
          try {
            Instant requestStartTime = Instant.now();
            var result = ohipAvailabilityClient.getItemsInventory(request);
            Instant requestEndTime = Instant.now();
            var message = String.format(
                "Request for hotel=%s, interval=[%s,%s] took %s milliseconds and ended at %s.",
                hotelId, request.getStartDate(), request.getEndDate(),
                Duration.between(requestStartTime, requestEndTime).toMillis(),
                requestEndTime);
            log.info(message);
            return result;
          } catch (Exception ex) {
            var message = String.format(
                "Request for hotel=%s, interval=[%s,%s] fails with error: %s",
                hotelId, request.getStartDate(), request.getEndDate(),
                ex.getMessage());
            log.info(message);
            throw ex;
          }
        }, apiLimitsExecutor))
        .toList();
  }

  private List<CompletableFuture<HotelAvailabilityDetailsDto>> getHotelAvailabilityList(
      AvailabilityRequestDto availabilityRequestDto, List<Pair<LocalDate, LocalDate>> intervals) {
    return intervals
        .stream()
        .map(dateRangeSearchCriteria -> (
            AvailabilityRequestDto.builder()
                .hotelId(availabilityRequestDto.getHotelId())
                .roomTypes(availabilityRequestDto.getRoomTypes())
                .children(availabilityRequestDto.getChildren())
                .adults(availabilityRequestDto.getAdults())
                .roomStayQuantity(availabilityRequestDto.getRoomStayQuantity())
                .roomStayStartDate(dateRangeSearchCriteria.getLeft().toString())
                .roomStayEndDate(dateRangeSearchCriteria.getRight().toString())
                .cotsRequired(availabilityRequestDto.getCotsRequired())
                .promotionCode(availabilityRequestDto.getPromotionCode())
                .ratePlanCode(availabilityRequestDto.getRatePlanCode())
                .roomSubstitutions(availabilityRequestDto.getRoomSubstitutions())
                .ratePlanSet(availabilityRequestDto.getRatePlanSet())
                .companyId(availabilityRequestDto.getCompanyId())
                .channel(availabilityRequestDto.getChannel())
                .subchannel(availabilityRequestDto.getSubchannel())
                .language(availabilityRequestDto.getLanguage())
                .build()
        ))
        .map(request -> CompletableFuture.supplyAsync(() -> {
          try {
            Instant requestStartTime = Instant.now();
            var result = ohipAvailabilityClient.getHotelAvailabilityRequest(request);
            Instant requestEndTime = Instant.now();
            var message = String.format(
                "Request for hotel=%s, interval=[%s,%s] took %s milliseconds and ended at %s.",
                availabilityRequestDto.getHotelId(), request.getRoomStayStartDate(),
                request.getRoomStayEndDate(),
                Duration.between(requestStartTime, requestEndTime).toMillis(),
                requestEndTime);
            log.info(message);
            return result.block();
          } catch (Exception ex) {
            var message = String.format(
                "Request for hotel=%s, interval=[%s,%s] fails with error: %s",
                availabilityRequestDto.getHotelId(), request.getRoomStayStartDate(),
                request.getRoomStayEndDate(),
                ex.getMessage());
            log.info(message);
            throw ex;
          }
        }, apiLimitsExecutor))
        .toList();
  }

  private boolean eachIntervalHasAvailability(
      List<CompletableFuture<HotelAvailabilityDetailsDto>> futureList,
      int intervalNumber) {
    return futureList.stream()
        .map(CompletableFuture::join).toList().stream()
        .map(HotelAvailabilityDetailsDto::getHotelAvailability)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(HotelAvailabilityDto::getRoomStays)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(RoomStayTypeDto::getRoomRates)
        .filter(el -> !el.isEmpty())
        .toList().size() == intervalNumber;
  }

  private Map<RoomRateTypeDto, List<RatesTypeDto>> mergeIntervalAvailability(
      List<CompletableFuture<HotelAvailabilityDetailsDto>> futureList) {
    return futureList.stream()
        .map(CompletableFuture::join).toList().stream()
        .map(HotelAvailabilityDetailsDto::getHotelAvailability)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(HotelAvailabilityDto::getRoomStays)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(RoomStayTypeDto::getRoomRates)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .reduce(
            new HashMap<>(), (map, element) -> {
              RoomRateTypeDto key = RoomRateTypeDto.builder()
                  .start(element.getStart())
                  .end(element.getEnd())
                  .suppressRate(element.getSuppressRate())
                  .ratePlanCode(element.getRatePlanCode())
                  .ratePlanSet(element.getRatePlanSet())
                  .marketCode(element.getMarketCode())
                  .promotionCode(element.getPromotionCode())
                  .numberOfUnits(element.getNumberOfUnits())
                  .roomType(element.getRoomType())
                  .build();
              if (Objects.isNull(map.get(key))) {
                map.put(key, List.of(element.getRates()));
              } else {
                List<RatesTypeDto> list = new ArrayList<>();
                list.addAll(map.get(key));
                list.add(element.getRates());
                map.put(key, list);
              }
              return map;
            },
            (map1, map2) -> {
              map1.putAll(map2);
              return map1;
            }
        );
  }

}
