package uk.co.whitbread.ohip.infrastructure.rest.client.availability;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.minBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static reactor.core.publisher.Mono.zip;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.DISTRIBUTION_CHANNEL;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.tuple.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.inventory.StatisticCodeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.inventory.StatisticType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.HotelAvailability;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.RatePlanShortInfoType;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchCriteriaV2;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityRoomSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.CorporateRate;
import uk.co.whitbread.ohip.domain.model.availability.in.HotelInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.ItemInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequestV2;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeRoomInfoCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RateV2;
import uk.co.whitbread.ohip.domain.model.availability.in.RestrictionsByDateRangeSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.Room;
import uk.co.whitbread.ohip.domain.model.availability.in.RoomMatrix;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityByIdsResult;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityByIdsResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityDailyPrice;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRequest;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoom;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomPriceBreakdown;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomRate;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomType;
import uk.co.whitbread.ohip.domain.model.availability.out.HotelAvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.HotelInventoryRoomType;
import uk.co.whitbread.ohip.domain.model.availability.out.ItemInventoryResponse;
import uk.co.whitbread.ohip.domain.model.availability.out.MultiAvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.MultiAvailabilityResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.RateCodePricingResult;
import uk.co.whitbread.ohip.domain.model.availability.out.RateCodePricingResult.RateCodePricingResultBuilder;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomLevelInventory;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomRateInfo;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomStay;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomType;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomTypeV2;
import uk.co.whitbread.ohip.domain.model.availability.out.StatisticsDateItem;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingChannel;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitution;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.exceptions.HotelAvailabilityException;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.AvailabilityByIdsRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.AvailabilityRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.HotelInventoryStatisticsMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.HotelRoomInventoryMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.ItemsInventoryMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.MultiAvailabilityRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper.PriceBreakdownMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelAvailabilityDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelAvailabilityDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelInventoryDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelInventoryTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.InventoryCountsTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.InventoryLevelCountsListTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.LinksDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.PriceBreakdownDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RoomRateTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RoomStayTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityByIdsSearchCriteriaV2Dto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.HotelInventoryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.ItemInventoryResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.MultiHotelAvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomByIdsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomTypeInfoDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomTypesDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomTypesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.ApiLimitsService;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.OhipAvailabilityClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.properties.AvailabilityOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.ohip.OhipProfileClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.ohip.OhipRatePlansClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.RulesAgentClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.RoomSubstitutionDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.RoomSubstitutionRuleResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.DateUtils;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@RequiredArgsConstructor
@Slf4j
public class HotelAvailabilityOutPortImpl implements HotelAvailabilityOutPort {

  public static final String NEGOTIATED_RATE_PLAN_SET = "NEGOTIATED";
  public static final String OHIP_ERROR_MESSAGE = "An error was returned by OHIP!";
  public static final String HOTEL_ROOM_CODE = "HotelRoomCode";
  private static final String PBN_RATE_PLAN_SET = "PBN";
  private static final String PBF_RATE_PLAN_SET = "PBF";
  private static final String NO_RATE_PLANS_AVAILABLE = "Currently, the hotel has no rate plans available.";
  private static final String NO_COMPANY_PROFILE_ID = "The ordering company has no profile ID.";
  private static final String ACCESSIBLE_ROOM = "DIS";
  private static final String TWIN_ROOM = "TWIN";
  private static final int MAX_RANGE_DAYS = 90;
  private static final String PROFILE_ID_TYPE = "Profile";
  private static final String DEFAULT_NUMBER_OF_ROOMS = "1";
  private static final String YYYY_MM_DD = "yyyy-MM-dd";
  private static final JsonMapper OBJECT_MAPPER = JsonMapper.builder()
      .findAndAddModules()
      .build();
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);

  private final AvailabilityRequestMapper availabilityRequestMapper;

  private final AvailabilityByIdsRequestMapper availabilityByIdsRequestMapper;

  private final OhipAvailabilityClient ohipAvailabilityClient;

  private final OhipProfileClient ohipProfileClient;

  private final OhipRatePlansClient ohipRatePlansClient;

  private final RulesAgentClient rulesAgentClient;

  private final PriceBreakdownMapper priceBreakdownMapper;

  private final ItemsInventoryMapper itemsInventoryMapper;

  private final HotelRoomInventoryMapper hotelRoomInventoryMapper;

  private final MultiAvailabilityRequestMapper multiAvailabilityRequestMapper;

  private final AvailabilityOhipProperties availabilityProperties;

  private final HotelInventoryStatisticsMapper hotelInventoryStatisticsMapper;

  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  private final ApiLimitsService apiLimitsService;

  private final ConcurrentTracer concurrentTracer;

  private static final int BATCH_SIZE = 10;

  /**
   * Split the startDate and endDate in multiple chunks containing smaller intervals.
   *
   * @param startDate initial date
   * @param totalDays number of days between the startDate and the endDate
   * @return a map which contains the chunks as startDate and endDate
   */
  public static Map<LocalDate, LocalDate> splitRange(LocalDate startDate,
      Integer totalDays) {
    int numChunks = (totalDays + MAX_RANGE_DAYS - 1) / MAX_RANGE_DAYS;

    // Split the date range into chunks of up to 90 days each
    return IntStream.range(0, numChunks)
        .mapToObj(i -> {
          LocalDate chunkStartDate = startDate.plusDays((long) i * MAX_RANGE_DAYS);
          LocalDate chunkEndDate = chunkStartDate.plusDays(
              Math.min(MAX_RANGE_DAYS - 1, totalDays - i * MAX_RANGE_DAYS - 1));
          return Map.entry(chunkStartDate, chunkEndDate);
        })
        .collect(toMap(Entry::getKey, Entry::getValue));
  }

  @Override
  public AvailabilityResult getHotelAvailability(AvailabilityRequest availabilityRequest) {

    log.debug("Entered getHotelAvailability for hotelId={}", availabilityRequest.getHotelId());

    var availabilityRequestDto =
        availabilityRequestMapper.toHotelAvailabilityRequestDto(availabilityRequest);

    // Retrieve room type information (including room classes)
    var pmsRoomTypes = getRoomTypeInfoList(
        ohipAvailabilityClient.getRoomTypes(availabilityRequest.getHotelId()));

    List<String> availableSubstitutionRoomTypes = convertWbRoomTypesToPmsRoomTypes(
        availabilityRequest.getRoomSubstitutions(), pmsRoomTypes);

    // Retrieve hotel inventory for all hotel
    Mono<List<HotelInventoryDto>> hotelInventoryDtos =
        getHotelInventory(availabilityRequest.getHotelId(),
            availabilityRequest.getRoomStayStartDate(),
            availabilityRequest.getRoomStayEndDate(),
            availabilityRequestDto.getAdults().size(), availableSubstitutionRoomTypes);

    Mono<List<HotelAvailabilityDetailsDto>> hotelAvailabilities;

    availabilityRequestDto.setRoomTypes(availableSubstitutionRoomTypes);

    if (StringUtils.isEmpty(availabilityRequest.getRatePlanCode())
        && StringUtils.isBlank(availabilityRequest.getPromotionCode())) {
      // Rate plan sets to be used for the availability call
      var ratePlanSets =
          getRatePlanSets(availabilityRequest.getChannel(), availabilityRequest.getSubchannel(),
              availabilityRequest.getLanguage(), availabilityRequest.getCompanyId());

      // Retrieve hotel availability using substitution room types
      hotelAvailabilities =
          findHotelAvailabilitiesByRatePlanSets(ratePlanSets, availabilityRequestDto);
    } else {
      // Retrieve hotel availability using substitution room types
      hotelAvailabilities =
          findHotelAvailabilitiesByRatePlanCode(availabilityRequestDto);
    }

    return zip(hotelInventoryDtos, hotelAvailabilities)
        .map(tuple -> createAvailabilityResult(tuple.getT1(), availabilityRequestDto.getHotelId(),
            tuple.getT2(), availabilityRequestDto.getRoomSubstitutions(), pmsRoomTypes,
            availabilityRequest.getChannel()))
        .block();
  }

  private Mono<List<HotelInventoryDto>> mockHotelInventory(String roomStayStartDate,
      String roomStayEndDate, List<RoomTypeInfoDto>
      roomTypeInfoDtos) {

    LocalDate startDate = LocalDate.parse(roomStayStartDate);
    LocalDate endDate = LocalDate.parse(roomStayEndDate);
    InventoryCountsTypeDto inventoryCount = InventoryCountsTypeDto.builder().availableCount(1)
        .available(true).startDate(startDate).endDate(endDate).build();

    List<InventoryLevelCountsListTypeDto> inventoryLevelCount = new ArrayList<>();
    var sequence = 1;
    for (RoomTypeInfoDto roomType : roomTypeInfoDtos) {
      InventoryLevelCountsListTypeDto levelCountsListTypeDto = new InventoryLevelCountsListTypeDto();
      levelCountsListTypeDto.setCode(roomType.getRoomType());
      levelCountsListTypeDto.setInventoryCounts(Collections.singletonList(inventoryCount));
      levelCountsListTypeDto.setSequence(sequence++);
      inventoryLevelCount.add(levelCountsListTypeDto);
    }
    InventoryCountsTypeDto houseInventory = InventoryCountsTypeDto.builder().available(true)
        .startDate(startDate).endDate(endDate)
        .build();

    HotelInventoryTypeDto hotelInventoryTypeDto = new HotelInventoryTypeDto(
        Collections.singletonList(houseInventory), inventoryLevelCount);

    HotelInventoryDto hotelInventoryDto = new HotelInventoryDto(
        Collections.singletonList(hotelInventoryTypeDto));

    return Mono.just(Collections.singletonList(hotelInventoryDto));
  }

  @Override
  public AvailabilityByIdsResult getHotelAvailabilityByIds(
      AvailabilityByIdsSearchRequest availabilitySearch) {
    log.debug("Entered getHotelAvailability for hotelIds={}", availabilitySearch.getHotelIds());

    var availabilityRequest = availabilityByIdsRequestMapper.toRequestDto(availabilitySearch);

    // Retrieve room type information (including room classes)
    var roomTypes = availabilitySearch.getHotelIds().stream()
        .collect(toMap(Function.identity(), ohipAvailabilityClient::getRoomTypes));

    // Retrieve hotel inventory for all hotels
    var hotelInventoryDto = Flux.fromIterable(availabilitySearch.getHotelIds()).flatMap(
            hotelId -> sendGetHotelInventory(hotelId, availabilityRequest.getRoomStayStartDate(),
                availabilityRequest.getRoomStayEndDate(),
                availabilityRequest.getRoomSubstitutions().size()),
            availabilityProperties.getMaxAvailabilityConcurrency())
        .collectList();

    // Retrieve hotel availability using substitution room types
    var hotelAvailabilityCompanyId = Mono.just(HotelAvailabilityDetailsDto.builder().build());

    if (StringUtils.isNotEmpty(availabilitySearch.getGlobalCompanyId())) {
      // Get Opera profile ID from the corporate id (globalCompanyId)
      var companyProfile = ohipProfileClient.getCompanyByCorporateId(
          availabilitySearch.getGlobalCompanyId());

      var operaCompanyId = companyProfile.getCompanyIdList().stream()
          .filter(c -> PROFILE_ID_TYPE.equals(c.getType()))
          .findFirst()
          .orElseThrow(() -> {
            var ex = new HotelAvailabilityException(ErrorCode.DIGITAL_NO_PROFILE,
                NO_COMPANY_PROFILE_ID);
            ExceptionLogger.log(log, ex);
            throw ex;
          }).getId();

      availabilityRequest.setCompanyId(operaCompanyId);

      // Find negotiated rates and filter by display set
      hotelAvailabilityCompanyId = findMultiHotelAvailabilityByCompanyId(availabilityRequest)
          .map(availability -> {

            // No filtering needed
            if (availabilitySearch.getNegotiatedRateDisplaySets() == null
                || availabilitySearch.getNegotiatedRateDisplaySets().isEmpty()) {
              return Mono.just(availability);
            }

            // Get all distinct rates from the availability response
            var negotiatedRatePlanCodes = availability.getHotelAvailability()
                .stream().collect(
                    toMap(HotelAvailabilityDto::getHotelId, a ->
                        a.getRoomStays().stream().flatMap(r -> r.getRoomRates().stream())
                            .map(RoomRateTypeDto::getRatePlanCode)
                            .distinct()
                            .toList()));

            negotiatedRatePlanCodes = negotiatedRatePlanCodes.entrySet().stream().filter(e -> !e.getValue().isEmpty())
                .collect(toMap(Entry::getKey, Entry::getValue));

            // Get rate summary (which will contain the rate plan set) for each rate from the response
            return Flux.fromIterable(negotiatedRatePlanCodes.entrySet())
                .flatMap(e -> ohipRatePlansClient.getRatePlans(e.getValue(), e.getKey()))
                .collectList()
                .map(ratePlansSummaryList -> {

                  // Filter negotiated rates by display set
                  availability.getHotelAvailability().forEach(a ->
                      a.getRoomStays().forEach(roomStay -> {
                        if (!roomStay.getRoomRates().isEmpty()) {
                          // Extract rate summary (which contains the display set)
                          var rateSummary = ratePlansSummaryList.stream()
                              .filter(rs -> Optional.ofNullable(rs.getRatePlanShortInfoList()
                                      .getRatePlanShortInfo())
                                  .orElseThrow(
                                      () -> {
                                        var ex = new HotelAvailabilityException(
                                            ErrorCode.DIGTAL_NO_RATE_PLANS_EXCEPTION,
                                            NO_RATE_PLANS_AVAILABLE);
                                        ExceptionLogger.log(log, ex);
                                        return ex;
                                      })
                                  .getFirst()
                                  .getHotelId()
                                  .equals(a.getHotelId()))
                              .findFirst()
                              .orElseThrow(() -> {
                                var ex = new HotelAvailabilityException(
                                    ErrorCode.DIGTAL_OHIP_ERROR_MESSAGE,
                                    OHIP_ERROR_MESSAGE);
                                ExceptionLogger.log(log, ex);
                                return ex;
                              });

                          var negotiatedRatesDisplaySetMap = rateSummary.getRatePlanShortInfoList()
                              .getRatePlanShortInfo().stream()
                              .collect(toMap(
                                  RatePlanShortInfoType::getRatePlanCode,
                                  ratePlan -> ratePlan.getClassifications().getDisplaySet()));

                          roomStay.getRoomRates().removeIf(roomRate ->
                              !availabilitySearch.getNegotiatedRateDisplaySets()
                                  .contains(negotiatedRatesDisplaySetMap
                                      .getOrDefault(roomRate.getRatePlanCode(), "N/A")));
                        }
                      }));
                  return availability;
                });
          }).block();
    }

    // Fetch the public rates if they are specified in the ratePlanCodes param
    var hotelAvailabilityRatePlanCode =
        availabilityRequest.getRatePlanCodes() == null || availabilityRequest.getRatePlanCodes()
            .isEmpty()
            ? Mono.just(HotelAvailabilityDetailsDto.builder().build())
            : findMultiHotelAvailabilityByRatePlanCodes(availabilityRequest);

    // Combine the results and format the response
    return zip(hotelInventoryDto, hotelAvailabilityCompanyId, hotelAvailabilityRatePlanCode)
        .map(tuple -> createAvailabilityByIdsResult(tuple.getT1(), availabilitySearch.getHotelIds(),
            tuple.getT2(), tuple.getT3(), availabilityRequest.getRoomSubstitutions(), roomTypes,
            availabilitySearch.getChannel()))
        .block();
  }

  private Mono<HotelAvailabilityDetailsDto> findMultiHotelAvailabilityByCompanyId(
      MultiHotelAvailabilityRequestDto availabilityRequestDto) {
    var ratePlanCodes = availabilityRequestDto.getRatePlanCodes();
    availabilityRequestDto.setRatePlanCodes(null);

    var result = getHotelAvailabilityByIdsRequestResponses(availabilityRequestDto);
    availabilityRequestDto.setRatePlanCodes(ratePlanCodes);

    return result;
  }

  private Mono<HotelAvailabilityDetailsDto> findMultiHotelAvailabilityByRatePlanCodes(
      MultiHotelAvailabilityRequestDto availabilityRequestDto) {
    var companyId = availabilityRequestDto.getCompanyId();
    availabilityRequestDto.setCompanyId(null);

    var result = getHotelAvailabilityByIdsRequestResponses(availabilityRequestDto);
    availabilityRequestDto.setCompanyId(companyId);

    return result;
  }

  @SneakyThrows
  private Mono<HotelAvailabilityDetailsDto> getHotelAvailabilityByIdsRequestResponses(
      MultiHotelAvailabilityRequestDto availabilityRequestDto) {
    List<Pair<LocalDate, LocalDate>> intervals = DateUtils.splitDateRange(
        availabilityRequestDto.getRoomStayStartDate(),
        availabilityRequestDto.getRoomStayEndDate(),
        availabilityProperties.getMaxRequestedDays(), YYYY_MM_DD);

    try {
      Instant startTime = Instant.now();

      var futureList = getHotelAvailabilityByIdsList(availabilityRequestDto, intervals);

      List<HotelAvailabilityDto> list = futureList.stream()
          .map(CompletableFuture::join).toList().stream()
          .map(HotelAvailabilityDetailsDto::getHotelAvailability)
          .flatMap(Collection::stream).distinct().toList();

      LinksDto links = new LinksDto();
      if (!futureList.isEmpty() && !Objects.isNull(futureList.getFirst())) {
        futureList.getFirst().get().getLinks();
      }
      Instant endTime = Instant.now();
      var message = String.format(
          "Completed calls for get hotels availability hotelIds=%s, companyId=%s for %s intervals in %s milliseconds.",
          availabilityRequestDto.getHotelIds(), availabilityRequestDto.getCompanyId(), intervals,
          Duration.between(startTime, endTime).toMillis());
      log.info(message);
      return Mono.just(HotelAvailabilityDetailsDto.builder()
          .hotelAvailability(list)
          .links(links)
          .build());
    } catch (CompletionException ex) {
      throw ex.getCause();
    }
  }

  @SneakyThrows
  private List<CompletableFuture<HotelAvailabilityDetailsDto>> getHotelAvailabilityByIdsList(
      MultiHotelAvailabilityRequestDto availabilityRequestDto,
      List<Pair<LocalDate, LocalDate>> intervals) {
    return intervals
        .stream()
        .map(dateRangeSearchCriteria -> MultiHotelAvailabilityRequestDto.builder()
            .hotelIds(availabilityRequestDto.getHotelIds())
            .roomTypes(availabilityRequestDto.getRoomTypes())
            .children(availabilityRequestDto.getChildren())
            .adults(availabilityRequestDto.getAdults())
            .channel(availabilityRequestDto.getChannel())
            .subchannel(availabilityRequestDto.getSubchannel())
            .roomSubstitutions(availabilityRequestDto.getRoomSubstitutions())
            .companyId(availabilityRequestDto.getCompanyId())
            .language(availabilityRequestDto.getLanguage())
            .cotsRequired(availabilityRequestDto.getCotsRequired())
            .ratePlanSet(availabilityRequestDto.getRatePlanSet())
            .ratePlanCodes(availabilityRequestDto.getRatePlanCodes())
            .roomMatrix(availabilityRequestDto.getRoomMatrix())
            .roomStayQuantity(availabilityRequestDto.getRoomStayQuantity())
            .roomStayStartDate(dateRangeSearchCriteria.getLeft().toString())
            .roomStayEndDate(dateRangeSearchCriteria.getRight().toString())
            .build())
        .map(request -> CompletableFuture.supplyAsync(() -> {
          try {
            Instant requestStartTime = Instant.now();
            var result = ohipAvailabilityClient.getHotelAvailabilityByIdsRequest(request).block();
            Instant requestEndTime = Instant.now();
            var message = String.format(
                "Request for hotelIds=%s, companyId=%s, interval=[%s,%s] took %s milliseconds and ended at %s.",
                request.getHotelIds(), request.getCompanyId(), request.getRoomStayStartDate(),
                request.getRoomStayEndDate(),
                Duration.between(requestStartTime, requestEndTime).toMillis(),
                requestEndTime);
            log.info(message);
            return result;
          } catch (Exception ex) {
            var message = String.format(
                "Request for hotelIds=%s, companyId=%s, interval=[%s,%s]  fails with error: %s",
                request.getHotelIds(), request.getCompanyId(), request.getRoomStayStartDate(),
                request.getRoomStayEndDate(), ex.getMessage());
            log.info(message);
            throw ex;
          }
        }))
        .toList();
  }

  private Mono<Tuple2<String, List<HotelInventoryDto>>> sendGetHotelInventory(
      String hotelId, String getRoomStayStartDate, String getRoomStayEndDate,
      Integer size) {
    return Mono.just(hotelId).zipWith(getHotelInventory(hotelId,
        getRoomStayStartDate, getRoomStayEndDate, size));
  }

  private HotelAvailabilityDetailsDto updateRequestAndGetHotelAvailability(
      AvailabilityRequestDto availabilityRequest, String ratePlanSet) {
    AvailabilityRequestDto request = availabilityRequest.toBuilder()
        .ratePlanSet(ratePlanSet)
        .build();
    return apiLimitsService.getHotelAvailabilityResponses(request);
  }

  /**
   * Retrieve the list of rate plan sets to be used in the availability call to Opera.
   *
   * @param channel    The channel.
   * @param subchannel The subchannel.
   * @param language   The language.
   * @param companyId  The companyId.
   * @return A list of rate plan sets.
   */
  private List<String> getRatePlanSets(
      String channel, String subchannel, String language,
      String companyId) {
    var determinedLanguage = channel.equalsIgnoreCase("DISTR") ? "N/A" : language;
    var ratePlanSets = rulesAgentClient.getBookingChannelInfo(BookingChannel.builder()
            .channel(channel)
            .subchannel(subchannel)
            .language(determinedLanguage).build())
        .getRatePlanSets();
    if (StringUtils.isNotEmpty(companyId)) {
      ratePlanSets.add(NEGOTIATED_RATE_PLAN_SET);
    }
    return ratePlanSets;
  }

  /**
   * Gives a list containing multiple HotelInventory Dtos for specific Room Types based on the
   * startDate and endDate If the range between the params is greater than 90, split them in
   * multiple chunks and send multiple requests.
   *
   * @param hotelId   the Hotel ID
   * @param startDate the Start Date
   * @param endDate   the End Date
   * @param size      needed for size limit
   * @param roomTypes room Types requested
   * @return a list of hotel inventories
   */
  private Mono<List<HotelInventoryDto>> getHotelInventory(
      String hotelId, String startDate, String endDate,
      Integer size, List<String> roomTypes) {

    var totalDays =
        (int) ChronoUnit.DAYS
            .between(LocalDate.parse(startDate), LocalDate.parse(endDate).plusDays(1));
    return totalDays > 90
        ? getMultipleChunkHotelInventoryList(hotelId, startDate, totalDays, size, roomTypes) :
        getSingleChunkHotelInventoryList(hotelId, startDate, endDate, size, roomTypes);
  }

  /**
   * Gives a list containing multiple HotelInventory Dtos based on the startDate and endDate
   * If the range between the params is greater than 90, split them in multiple chunks and send
   * multiple requests.
   *
   * @param hotelId   the Hotel ID
   * @param startDate the Start Date
   * @param endDate   the End Date
   * @param size      needed for size limit
   * @return a list of hotel inventories
   */
  private Mono<List<HotelInventoryDto>> getHotelInventory(String hotelId, String startDate,
      String endDate, Integer size) {
    return getHotelInventory(hotelId, startDate, endDate, size, null);
  }

  /**
   * Gets a List containing a single item of the hotelInventory.
   *
   * @param hotelId   The hotel id
   * @param startDate The start date
   * @param endDate   The end date
   * @param size      limit of the response
   * @param roomTypes room Types requested
   * @return a list of hotel inventories
   */
  private Mono<List<HotelInventoryDto>> getSingleChunkHotelInventoryList(
      String hotelId, String startDate, String endDate, Integer size,
      List<String> roomTypes) {
    return Flux.just(1)
        .flatMap(entry -> ohipAvailabilityClient.getHotelInventory(
            hotelId, startDate, endDate, size, roomTypes))
        .collectList();
  }

  /**
   * Based on the range between startDate and endDate, retrieve the list of inventories across
   * multiple chunks of maximum 90 days per inventory.
   *
   * @return list of hotel inventories.
   */
  private Mono<List<HotelInventoryDto>> getMultipleChunkHotelInventoryList(String hotelId,
      String startDate,
      Integer totalDays,
      Integer size, List<String> roomTypes) {

    return Flux.fromIterable(splitRange(LocalDate.parse(startDate),
            totalDays).entrySet())
        .flatMap(entry -> ohipAvailabilityClient.getHotelInventory(hotelId,
            entry.getKey().toString(),
            entry.getValue().toString(), size, roomTypes))
        .collectList();
  }

  private List<RoomTypeInfoDto> getRoomTypeInfoList(RoomTypesResponseDto roomTypesResponseDto) {
    return roomTypesResponseDto.getRoomTypesSummary()
        .stream().map(RoomTypesDto::getRoomTypeSummary).flatMap(Collection::stream).toList();
  }

  private String extractEndFromLastDate(
      List<HotelAvailabilityDetailsDto> hotelAvailabilityDetailsDtos) {
    return hotelAvailabilityDetailsDtos.stream()
        .map(HotelAvailabilityDetailsDto::getHotelAvailability)
        .flatMap(Collection::stream)
        .filter(this::isAvailabilityResponseValid)
        .map(HotelAvailabilityDto::getRoomStays)
        .flatMap(Collection::stream)
        .map(RoomStayTypeDto::getRoomRates)
        .flatMap(Collection::stream)
        .map(roomRateTypeDto -> LocalDate.parse(roomRateTypeDto.getEnd(), formatter))
        .max(LocalDate::compareTo)
        .map(LocalDate::toString)
        .orElse("");
  }

  private String extractStartFromFirstDate(
      List<HotelAvailabilityDetailsDto> hotelAvailabilityDetailsDtos) {
    return hotelAvailabilityDetailsDtos.stream()
        .map(HotelAvailabilityDetailsDto::getHotelAvailability)
        .flatMap(Collection::stream)
        .filter(this::isAvailabilityResponseValid)
        .map(HotelAvailabilityDto::getRoomStays)
        .flatMap(Collection::stream)
        .map(RoomStayTypeDto::getRoomRates)
        .flatMap(Collection::stream)
        .map(roomRateTypeDto -> LocalDate.parse(roomRateTypeDto.getStart(), formatter))
        .min(LocalDate::compareTo)
        .map(LocalDate::toString)
        .orElse("");
  }

  private boolean isAvailabilityResponseValid(HotelAvailabilityDto hotelAvailabilityDto) {
    var roomStays = hotelAvailabilityDto.getRoomStays();
    var roomRates = roomStays.getFirst().getRoomRates();
    return !roomStays.isEmpty() && roomRates != null && !roomRates.isEmpty();
  }

  private Mono<List<HotelAvailabilityDetailsDto>> findHotelAvailabilitiesByRatePlanSets(
      List<String> ratePlanSets,
      AvailabilityRequestDto availabilityRequestDto) {
    List<CompletableFuture<HotelAvailabilityDetailsDto>> futures = ratePlanSets.stream()
        .map(ratePlanSet -> CompletableFuture.supplyAsync(
            concurrentTracer.wrap((Supplier<HotelAvailabilityDetailsDto>)
                () -> updateRequestAndGetHotelAvailability(availabilityRequestDto, ratePlanSet)))
        )
        .toList();
    CompletableFuture<List<HotelAvailabilityDetailsDto>> allFutures =
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .toList()
            );
    return Mono.fromFuture(allFutures);
  }

  private Mono<List<HotelAvailabilityDetailsDto>> findHotelAvailabilitiesByRatePlanCode(
      AvailabilityRequestDto availabilityRequest) {
    return Mono.just(apiLimitsService.getHotelAvailabilityResponses(availabilityRequest)).map(List::of);
  }

  /**
   * Find the required rooms and build the availability result.
   *
   * @param hotelInventoryDto            The hotel room inventory.
   * @param hotelId                      The hotel id.
   * @param hotelAvailabilityDetailsDtos A list of available rates for the requested rooms from
   *                                     Opera.
   * @param roomSubstitutions            A list of acceptable room substitution for every requested
   *                                     room.
   * @param roomTypeInfoDtos             A list of information (i.e. room class) for each Opera room
   *                                     type.
   * @return The availability result.
   */
  private AvailabilityResult createAvailabilityResult(List<HotelInventoryDto> hotelInventoryDto,
      String hotelId,
      List<HotelAvailabilityDetailsDto> hotelAvailabilityDetailsDtos,
      List<RoomSubstitutionRuleResponseDto> roomSubstitutions,
      List<RoomTypeInfoDto> roomTypeInfoDtos, String channel) {

    var start = extractStartFromFirstDate(hotelAvailabilityDetailsDtos);
    var end = extractEndFromLastDate(hotelAvailabilityDetailsDtos);

    // Extract the room rates from Opera
    var roomStays = getRoomStays(hotelAvailabilityDetailsDtos);

    var hasHouseInventoryAvailability = hotelInventoryDto.stream()
        .map(HotelInventoryDto::getHotelInventories)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(HotelInventoryTypeDto::getHouseInventory)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(InventoryCountsTypeDto::getAvailable)
        .filter(Objects::nonNull)
        .reduce(Boolean.TRUE, Boolean::logicalAnd);

    var availabilityResultBuilder = AvailabilityResult.builder()
        .hotelId(hotelId)
        .startDate(start)
        .endDate(end)
        .available(hasHouseInventoryAvailability); // True if hotel has ANY room available.

    // Extract the room counts from Opera
    var roomTypeAvailabilityCount = getRoomTypeAvailabilityCount(hotelInventoryDto);

    // For each ratePlanCode there should be at least one rate for every room, else the ratePlanCode is excluded
    // For each room, a substitution rule was requested and a substitution list was received
    // Iterate through the substitution list (i.e. an ordered list of Opera room types) and pick a room
    for (var r : getRoomRateByRatePlanCode(roomStays).entrySet()) {
      var ratePlanCode = r.getKey();
      var roomRates = r.getValue();

      // Copy the room counts from Opera
      var roomTypeAvailability = new HashMap<>(roomTypeAvailabilityCount);

      // Attempt to find a suitable room for every requested room for the current rate plan code
      var availabilityRoomTypes = getAvailabilityForRatePlanCode(
          roomSubstitutions, roomTypeInfoDtos, roomRates, roomTypeAvailability);

      // If all rooms have availability
      if (!availabilityRoomTypes.isEmpty()) {
        removeInconsistentRoomClassesRates(availabilityRoomTypes, channel);
        var promotionCode = roomRates.stream()
            .map(RoomRateTypeDto::getPromotionCode)
            .map(Optional::ofNullable)
            .findFirst()
            .flatMap(Function.identity())
            .orElse(null);
        availabilityResultBuilder.roomRate(AvailabilityRoomRate.builder()
            .roomTypes(availabilityRoomTypes)
            .ratePlanCode(ratePlanCode)
            .promotionCode(promotionCode)
            .build());
      }
    }

    availabilityResultBuilder.timestamp(Instant.now());

    var availabilityResult = availabilityResultBuilder.build();

    updateNoOfRoomsAvailable(availabilityResult, hotelInventoryDto, roomTypeInfoDtos, roomSubstitutions);

    return availabilityResult;
  }

  /**
   * Update the availability result with the number of rooms available for each room type.
   *
   * @param hotelAvailabilityResult   The availability result to update.
   * @param hotelInventoryDtos        A list of hotel inventory for the requested hotel.
   * @param pmsRoomTypes              A list of information (i.e. room class) for each Opera room type.
   * @param roomSubstitutions         A list of acceptable room substitution for every requested room type
   */
  private void updateNoOfRoomsAvailable(AvailabilityResult hotelAvailabilityResult,
      List<HotelInventoryDto> hotelInventoryDtos, List<RoomTypeInfoDto> pmsRoomTypes,
      List<RoomSubstitutionRuleResponseDto> roomSubstitutions) {

    Map<RoomTypeWithOccupants, Set<String>> roomTypeWithOccupantsSubstitutions =
        getRoomTypeWithOccupantsSubstitutions(roomSubstitutions);
    Map<RoomTypeWithOccupants, Map<String, Set<String>>> roomTypeToRoomClassToSubstitutionList =
        getRoomTypeWithOccupantsSubstitutionsPerRoomClass(hotelAvailabilityResult, pmsRoomTypes,
            roomTypeWithOccupantsSubstitutions);

    Map<String, Integer> roomTypeInventoryMap = getRoomTypeAvailabilityCount(hotelInventoryDtos);

    hotelAvailabilityResult.getRoomRates()
        .forEach(roomRate -> roomRate.getRoomTypes().forEach(roomTypeInfo -> {
          Map<String, Set<String>> roomClassToSubstitutionList = roomTypeToRoomClassToSubstitutionList.get(
              new RoomTypeWithOccupants(
                  roomTypeInfo.getRoomType(), roomTypeInfo.getAdults(),
                  roomTypeInfo.getChildren(), Boolean.TRUE.equals(roomTypeInfo.getCotRequested())));
          roomTypeInfo.getRooms().forEach(room -> {
            // calculate inventory count based on the room class, substitution list and roomTypeInventoryMap
            int inventoryCount = roomClassToSubstitutionList.get(room.getRoomClass()).stream()
                .map(substitution -> roomTypeInventoryMap.getOrDefault(substitution, 0))
                .filter(value -> value > 0)
                .reduce(0, Integer::sum);
            log.debug("RoomType: {}, RoomClass: {}, InventoryCount: {}",
                roomTypeInfo.getRoomType(), room.getRoomClass(), inventoryCount);
            room.setNumberOfRoomsAvailable(inventoryCount);
          });
        }));
  }

  /**
   * Return a map of room type with occupants to a map of room class to substitution list.
   * The substitution list contains the room types that can be substituted for the requested room class
   * based on the room class and substitution rules.
   *
   * @param availability                          The availability result
   * @param pmsRoomTypes                          A list of information (i.e. room class) for each
   *                                              Opera room type.
   * @param roomTypeWithOccupantsSubstitutions    A map of room type with occupants to a set of room types
   *                                              that can be substituted for the requested room type
   * @return  A map of room type with occupants to a map of room class to substitution list
   */
  private Map<RoomTypeWithOccupants, Map<String, Set<String>>> getRoomTypeWithOccupantsSubstitutionsPerRoomClass(
      AvailabilityResult availability, List<RoomTypeInfoDto> pmsRoomTypes,
      Map<RoomTypeWithOccupants, Set<String>> roomTypeWithOccupantsSubstitutions) {
    // create a map of room type (DB, FAM etc.) with occupants to a map of room class (e.g. ST, SE, PP)
    // to pmsRoomType (e.g. DBLWIN, PPLDBL)
    Map<RoomTypeWithOccupants, Map<String, String>> roomTypeToRoomClassToPmsRoomType =
        availability.getRoomRates()
            .stream()
            .flatMap(roomRate -> roomRate.getRoomTypes().stream())
            .collect(toMap(
                roomTypeInfo -> new RoomTypeWithOccupants(
                    roomTypeInfo.getRoomType(), roomTypeInfo.getAdults(),
                    roomTypeInfo.getChildren(),
                    Boolean.TRUE.equals(roomTypeInfo.getCotRequested())),
                roomTypeInfo -> roomTypeInfo.getRooms().stream()
                    .collect(toMap(AvailabilityRoom::getRoomClass,
                        AvailabilityRoom::getPmsRoomType,
                        (existing, replacement) -> existing)),
                (existing, replacement) -> {
                  existing.putAll(replacement);
                  return existing;
                }));

    Map<String, Set<String>> roomClassToRoomTypes = getRoomTypesPerClass(pmsRoomTypes);

    // create a map of room type to a map of room class to substitution list; the substitution list
    // contains the room types that can be substituted for the requested room type based on the
    // room class (retrieved from roomTypesInfo) and substitution rules retrieved from rules agent
    return roomTypeToRoomClassToPmsRoomType.entrySet().stream()
        .collect(toMap(Entry::getKey,
            typeToClassEntry -> typeToClassEntry.getValue().entrySet().stream()
                .collect(toMap(Entry::getKey,
                    classToPmsRoomTypeEntry -> {
                      Set<String> filteredSubstitutions =
                          roomTypeWithOccupantsSubstitutions.get(typeToClassEntry.getKey()).stream()
                              .filter(substitution -> roomClassToRoomTypes.getOrDefault(
                                      classToPmsRoomTypeEntry.getKey(), Set.of())
                                  .contains(substitution))
                              .collect(toSet());
                      filteredSubstitutions.add(classToPmsRoomTypeEntry.getValue());
                      return filteredSubstitutions;
                    }))));
  }

  /**
   * Return a map of room class to a set of room types.
   *
   * @param pmsRoomTypes A list of information (i.e. room class) for each Opera room type.
   * @return  A map of room class to a set of room types
   */
  private Map<String, Set<String>> getRoomTypesPerClass(List<RoomTypeInfoDto> pmsRoomTypes) {
    if (pmsRoomTypes == null) {
      return new HashMap<>();
    }

    // create a map of room class to set of room types from the roomTypesInfo; group room types by room class
    return pmsRoomTypes.stream()
        .collect(
            groupingBy(RoomTypeInfoDto::getRoomClass,
                mapping(RoomTypeInfoDto::getRoomType, toSet()))
        );
  }

  /**
   * Return a map of room type with occupants to a set of room types that can be substituted for the
   * requested room type.
   *
   * @param roomSubstitutions A list of acceptable room substitution for every requested room type
   * @return  A map of room type with occupants to a set of room types that can be substituted for the
   *      requested room type
   */
  private Map<RoomTypeWithOccupants, Set<String>> getRoomTypeWithOccupantsSubstitutions(
      List<RoomSubstitutionRuleResponseDto> roomSubstitutions) {
    return roomSubstitutions.stream()
        .collect(toMap(roomSubstitutionRuleResponseDto -> new RoomTypeWithOccupants(
                roomSubstitutionRuleResponseDto.getRequestDetails().getRoomType(),
                roomSubstitutionRuleResponseDto.getRequestDetails().getAdults(),
                roomSubstitutionRuleResponseDto.getRequestDetails().getChildren(),
                Boolean.TRUE.equals(roomSubstitutionRuleResponseDto.getRequestDetails().getCotRequired())),
            roomSubstitutionRuleResponseDto -> roomSubstitutionRuleResponseDto.getSubstitutionList()
                .stream()
                .map(RoomSubstitutionDto::getType)
                .collect(toSet()), (existing, replacement) -> existing));
  }

  record RoomTypeWithOccupants(String roomType, int adults, int children, boolean cotsRequired) {

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      RoomTypeWithOccupants that = (RoomTypeWithOccupants) o;
      return adults == that.adults && children == that.children && cotsRequired == that.cotsRequired
          && roomType.equals(that.roomType);
    }

    @Override
    public int hashCode() {
      int result = roomType.hashCode();
      result = 31 * result + adults;
      result = 31 * result + children;
      result = 31 * result + (cotsRequired ? 1 : 0);
      return result;
    }
  }

  private AvailabilityByIdsResult createAvailabilityByIdsResult(
      List<Tuple2<String, List<HotelInventoryDto>>> hotelInventoryDtos,
      List<String> hotelIds,
      HotelAvailabilityDetailsDto hotelAvailabilityCompanyId,
      HotelAvailabilityDetailsDto hotelAvailabilityRatePlanCode,
      List<RoomSubstitutionRuleResponseDto> roomSubstitutions,
      Map<String, RoomTypesResponseDto> roomTypeInfoMap,
      String channel) {

    // Only add valid results (that contain actual availability data)
    List<HotelAvailabilityDetailsDto> hotelAvailabilityDetailsDtos = new ArrayList<>();

    if (hotelAvailabilityCompanyId != null
        && hotelAvailabilityCompanyId.getHotelAvailability() != null) {
      hotelAvailabilityDetailsDtos.add(hotelAvailabilityCompanyId);
    }

    if (hotelAvailabilityRatePlanCode != null
        && hotelAvailabilityRatePlanCode.getHotelAvailability() != null) {
      hotelAvailabilityDetailsDtos.add(hotelAvailabilityRatePlanCode);
    }

    // No results were found, return
    if (hotelAvailabilityDetailsDtos.isEmpty()) {
      return AvailabilityByIdsResult.builder().build();
    }

    Map<String, List<HotelInventoryDto>> hotelInventoryMap = hotelInventoryDtos.stream()
        .collect(toMap(Tuple2::getT1, Tuple2::getT2));

    var start = extractStartFromFirstDate(hotelAvailabilityDetailsDtos);
    var end = extractEndFromLastDate(hotelAvailabilityDetailsDtos);

    List<AvailabilityResult> result = new ArrayList<>();

    for (String hotelId : hotelIds) {
      // Extract the room rates from Opera
      var roomStays = getRoomStays(hotelAvailabilityDetailsDtos, hotelId);
      List<HotelInventoryDto> hotelInventoryDto = hotelInventoryMap.get(hotelId);
      RoomTypesResponseDto roomTypeInfoDto = roomTypeInfoMap.get(hotelId);

      boolean houseLevelAvailability = hotelInventoryDto.getFirst().getHotelInventories().getFirst().getHouseInventory()
          .getFirst().getAvailable();
      var availabilityResultBuilder = AvailabilityResult.builder()
          .hotelId(hotelId)
          .startDate(start)
          .endDate(end)
          .available(houseLevelAvailability); // True if hotel has ANY room available.

      if (houseLevelAvailability) {
        // Extract the room counts from Opera
        var roomTypeAvailabilityCount = getRoomTypeAvailabilityCount(hotelInventoryDto);

        // For each ratePlanCode there should be at least one rate for every room, else the ratePlanCode is excluded
        // For each room, a substitution rule was requested and a substitution list was received
        // Iterate through the substitution list (i.e. an ordered list of Opera room types) and pick a room
        for (var r : getRoomRateByRatePlanCode(roomStays).entrySet()) {
          var ratePlanCode = r.getKey();
          var roomRates = r.getValue();

          // Copy the room counts from Opera
          var roomTypeAvailability = new HashMap<>(roomTypeAvailabilityCount);

          // Attempt to find a suitable room for every requested room for the current rate plan code
          var availabilityRoomTypes = getAvailabilityForRatePlanCode(
              roomSubstitutions, getRoomTypeInfoList(roomTypeInfoDto), roomRates,
              roomTypeAvailability);

          // If all rooms have availability
          if (!availabilityRoomTypes.isEmpty()) {
            removeInconsistentRoomClassesRates(availabilityRoomTypes, channel);
            availabilityResultBuilder.roomRate(AvailabilityRoomRate.builder()
                .roomTypes(availabilityRoomTypes)
                .ratePlanCode(ratePlanCode)
                .build());
          }
        }

        availabilityResultBuilder.timestamp(Instant.now());
        result.add(availabilityResultBuilder.build());
      }
    }

    return AvailabilityByIdsResult.builder().hotelAvailability(result).build();
  }

  /**
   * Extracts a list of suitable rooms for the current rate plan.
   *
   * @param roomSubstitutions    A list of acceptable room substitution for the current processed
   *                             room.
   * @param roomTypeInfoDtos     A list containing room class information.
   * @param roomRates            A list of rates for the current processed rate plan.
   * @param roomTypeAvailability A map of room count per room type.
   * @return A list of rooms or an empty list if one of the requested rooms has no availability.
   */
  private List<AvailabilityRoomType> getAvailabilityForRatePlanCode(
      List<RoomSubstitutionRuleResponseDto> roomSubstitutions,
      List<RoomTypeInfoDto> roomTypeInfoDtos, List<RoomRateTypeDto> roomRates,
      Map<String, Integer> roomTypeAvailability) {
    var availabilityRoomTypes = new ArrayList<AvailabilityRoomType>();

    // For each requested room
    for (var roomSubstitution : roomSubstitutions) {
      var availabilityRoomTypeBuilder = AvailabilityRoomType.builder()
              .roomType(roomSubstitution.getRequestDetails().getRoomType())
              .adults(roomSubstitution.getRequestDetails().getAdults())
              .children(roomSubstitution.getRequestDetails().getChildren())
              .cotRequested(roomSubstitution.getRequestDetails().getCotRequired());

      // For each room class
      createPmsRoomTypesByRoomClassMap(roomSubstitution.getSubstitutionList(), roomTypeInfoDtos)
              .forEach((roomClass, roomTypeInfoDtosList) -> {
                var availabilityRooms =
                        createRoomForAvailabilityRoomType(roomSubstitution, roomRates,
                                roomTypeAvailability, roomTypeInfoDtosList, roomClass);
                availabilityRoomTypeBuilder.rooms(availabilityRooms);
              });

      var availabilityRoomType = availabilityRoomTypeBuilder.build();

      // One of the requested rooms has no availability for the current rate
      if (availabilityRoomType.getRooms().isEmpty()) {
        return List.of();
      }
      availabilityRoomTypes.add(availabilityRoomType);
    }

    return availabilityRoomTypes;
  }

  /**
   * Extract the rates from the Opera response.
   *
   * @param hotelAvailabilityDetailsDtos The availability response from Opera.
   * @return A list of rates for each requested room type.
   */
  private List<RoomStayTypeDto> getRoomStays(
      List<HotelAvailabilityDetailsDto> hotelAvailabilityDetailsDtos) {
    return hotelAvailabilityDetailsDtos.stream().map(
            hotelAvailabilityDetailsDto -> {
              var hotelAvailability = hotelAvailabilityDetailsDto.getHotelAvailability().getFirst();
              var ratePlanSet = hotelAvailability.getRatePlanSet();
              var roomStays = hotelAvailability.getRoomStays();
              roomStays.forEach(roomStayTypeDto -> roomStayTypeDto.getRoomRates()
                  .forEach(roomRateTypeDto -> roomRateTypeDto.setRatePlanSet(ratePlanSet)));
              return roomStays;
            }).flatMap(Collection::stream)
        .toList();
  }

  /**
   * Extract the rates from the Opera response.
   *
   * @param hotelAvailabilityDetailsDtos The availability response from Opera.
   * @param hotelId                      The hotel ID
   * @return A list of rates for each requested room type.
   */
  private List<RoomStayTypeDto> getRoomStays(
      List<HotelAvailabilityDetailsDto> hotelAvailabilityDetailsDtos,
      String hotelId) {
    return hotelAvailabilityDetailsDtos.stream().map(
            hotelAvailabilityDetailsDto -> hotelAvailabilityDetailsDto.getHotelAvailability().stream()
                .filter(x -> x.getHotelId().equalsIgnoreCase(hotelId))
                .findFirst()
                .map(HotelAvailabilityDto::getRoomStays)
                .orElse(Collections.emptyList()))
        .flatMap(Collection::stream).toList();
  }

  /**
   * Remove room options if their class is not available for each requested room.
   *
   * @param availabilityRoomTypes Found availabilities for the current ratePlanCode
   */
  private void removeInconsistentRoomClassesRates(List<AvailabilityRoomType> availabilityRoomTypes, String channel) {

    var roomClassOccurrence = getRoomClassOccurrence(availabilityRoomTypes);
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getAvailabilityFromDifferentRoomClasses())) {
      log.info(
          "No room class restriction applied since FF release_availability_from_different_room_classes is enabled");
    } else {
      if (!DISTRIBUTION_CHANNEL.equalsIgnoreCase(channel)) {
        availabilityRoomTypes.forEach(r -> {
          var updatedRooms = r.getRooms().stream()
              .filter(rr -> roomClassOccurrence.get(rr.getRoomClass()) >= availabilityRoomTypes.size())
              .toList();
          r.setRooms(updatedRooms);
        });
      }
    }
  }

  /**
   * Create a MAP to keep track of the number of occurrences of roomClasses across a LIST of
   * roomTypes e.g. ST -> 2, PP -> 1 etc.
   *
   * @param availabilityRoomTypes The list of roomTypes from which we'll count the occurrences of
   *                              roomClasses
   * @return A MAP which will hold a key value pair of roomClasses and their occurrences across the
   *         roomTypes etc.
   */
  private Map<String, Long> getRoomClassOccurrence(
      List<AvailabilityRoomType> availabilityRoomTypes) {

    return availabilityRoomTypes
        .stream()
        .map(AvailabilityRoomType::getRooms)
        .flatMap(List::stream)
        .map(AvailabilityRoom::getRoomClass)
        .collect(groupingBy(e -> e, Collectors.counting()));
  }


  private List<AvailabilityRoom> createRoomForAvailabilityRoomType(
      RoomSubstitutionRuleResponseDto roomSubstitutionResponse,
      List<RoomRateTypeDto> roomRates,
      Map<String, Integer> roomTypeAvailability,
      List<String> roomTypeInfoDtosList,
      String roomClass) {

    // Find substitution rooms that are available both in the inventory response and in the availability response
    var classScopedSubstitutions = roomSubstitutionResponse.getSubstitutionList().stream()
        .filter(s -> roomTypeInfoDtosList.contains(s.getType()))
        .toList();

    var preferredPmsRoomType = classScopedSubstitutions.isEmpty()
        ? null
        : classScopedSubstitutions.getFirst().getType();

    var substitutions = classScopedSubstitutions.stream()
        .filter(s -> roomRates.stream().anyMatch(r -> r.getRoomType().equals(s.getType())
            && roomTypeAvailability.getOrDefault(s.getType(), 0) > 0))
        .toList();

    var rooms = new ArrayList<AvailabilityRoom>();

    for (var roomSubstitution : substitutions) {

      // Accessible rooms can have multiple variants
      if (ACCESSIBLE_ROOM.equals(roomSubstitutionResponse.getRequestDetails().getRoomType())) {
        var specialAccessibleRoom = rooms.stream()
            .filter(r -> r.getSpecialRequests() != null
                && roomSubstitution.getAccessibleSpecialRequest() != null
                && r.getSpecialRequests().contains(roomSubstitution.getAccessibleSpecialRequest()))
            .findFirst()
            .isEmpty();

        // Only add if the existing room has different accessible special requests
        // otherwise it means room was already found
        if (specialAccessibleRoom) {
          // Subtract 1 available unit from the roomTypeAvailability list
          // ONLY if this is the first room
          // Otherwise this rooms will be used as an alternative
          if (rooms.isEmpty()) {
            roomTypeAvailability.merge(roomSubstitution.getType(), 0,
                (x, y) -> x - 1);
          }
          // Add room
          roomRates.stream()
              .filter(r -> r.getRoomType().equals(roomSubstitution.getType()))
              .findFirst()
              .ifPresent(roomRate -> rooms.add(
                  createAvailabilityRoom(roomSubstitution, roomRate, roomClass,
                      roomSubstitutionResponse.getRequestDetails().getAdults(),
                      preferredPmsRoomType)));
        }
      } else {
        // Subtract 1 available unit from the roomTypeAvailability list
        roomTypeAvailability.merge(roomSubstitution.getType(), 0,
            (x, y) -> x - 1);
        // Mapping all the PMS room type for TWIN Room if it matches with substitution Rule(without braking the loop)
        if (TWIN_ROOM.equals(roomSubstitutionResponse.getRequestDetails().getRoomType())) {
          roomRates.stream()
              .filter(r -> r.getRoomType().equals(roomSubstitution.getType()))
              .findFirst()
              .ifPresent(roomRate -> rooms.add(
                  createAvailabilityRoom(roomSubstitution, roomRate, roomClass,
                      roomSubstitutionResponse.getRequestDetails().getAdults(),
                      preferredPmsRoomType)));
        } else {
          // Add room and exit, since no other option should be visible
          roomRates.stream()
              .filter(r -> r.getRoomType().equals(roomSubstitution.getType()))
              .findFirst()
              .ifPresent(roomRate -> rooms.add(
                  createAvailabilityRoom(roomSubstitution, roomRate, roomClass,
                      roomSubstitutionResponse.getRequestDetails().getAdults(),
                      preferredPmsRoomType)));
          break;
        }
      }
    }

    return rooms;

  }

  /**
   * Creates a map of rates by ratePlanCode.
   *
   * @param roomStays The rates received from Opera.
   * @return A map of rates indexed by ratePlanCode.
   */
  private Map<String, List<RoomRateTypeDto>> getRoomRateByRatePlanCode(
      List<RoomStayTypeDto> roomStays) {
    return roomStays.stream()
        .map(RoomStayTypeDto::getRoomRates)
        .flatMap(List::stream)
        .collect(groupingBy(RoomRateTypeDto::getRatePlanCode));
  }


  /**
   * Creates a map of room count by Opera room type. As input, we provide a list of
   * hotelInventoryDtos and the map is created with the lowest values found for the availability
   * counts inside the list if there are multiple entries.
   *
   * @param hotelInventoryDtos Inventory received from Opera.
   * @return A map of room count by Opera room type.
   */
  private Map<String, Integer> getRoomTypeAvailabilityCount(
      List<HotelInventoryDto> hotelInventoryDtos) {
    return hotelInventoryDtos.size() == 1 ? hotelInventoryDtos.getFirst().getHotelInventories().getFirst()
        .getRoomTypeInventories().stream()
        .collect(toMap(InventoryLevelCountsListTypeDto::getCode,
            i -> i.getInventoryCounts().getFirst().getAvailableCount())) :
        getMinimumRoomTypeCount(hotelInventoryDtos);
  }

  /**
   * Provides a map containing the key as the pms room type and the value as the availability count
   * for it. Give the fact that the input is a list, the actual value for each key is going to be
   * the smallest value found on the list
   *
   * @param hotelInventoryDtos the list of hotel inventories which will contain numerous values for
   *                           the available pms rooms
   * @return the map containing the smallest value of the availability count for each pms room type
   */
  private Map<String, Integer> getMinimumRoomTypeCount(
      List<HotelInventoryDto> hotelInventoryDtos) {
    return hotelInventoryDtos.stream()
        .map(hotelInventory -> hotelInventory.getHotelInventories().getFirst().getRoomTypeInventories())
        .flatMap(List::stream)
        .collect(groupingBy(
            InventoryLevelCountsListTypeDto::getCode,
            Collectors.collectingAndThen(
                toCollection(ArrayList::new),
                counts -> counts.stream()
                    .flatMap(count -> count.getInventoryCounts().stream())
                    .mapToInt(InventoryCountsTypeDto::getAvailableCount)
                    .min()
                    .orElse(0)
            )
        ));
  }

  /**
   * Create a list of acceptable Opera room types by using the substitution rules. Removes
   * duplicates and rooms without inventory to avoid unnecessary OHIP query parameters
   *
   * @param roomSubstitutions The list of room substitution rules from the Rules Engine.
   * @param roomTypes         List of room types with quantity
   * @return A list of Opera room types to be requested availability for (e.g. "FMTRPL", "FMQUAD",
   *         etc.)
   */
  private List<String> convertWbRoomTypesToPmsRoomTypes(
      List<RoomSubstitutionRuleResponse> roomSubstitutions,
      List<RoomTypeInfoDto> roomTypes) {
    Set<String> availableRoomTypes = roomTypes.stream()
        .map(RoomTypeInfoDto::getRoomType)
        .collect(toSet());
    return roomSubstitutions.stream()
        .map(RoomSubstitutionRuleResponse::getSubstitutionList)
        .flatMap(List::stream)
        .map(RoomSubstitution::getType)
        .distinct()
        .filter(availableRoomTypes::contains)
        .toList();
  }

  private Map<String, List<String>> createPmsRoomTypesByRoomClassMap(
      List<RoomSubstitutionDto> roomSubstitutions,
      List<RoomTypeInfoDto> roomTypeInfoDtos) {

    return roomTypeInfoDtos.stream()
        .filter(roomTypeInfoDto -> roomSubstitutions
            .stream().map(RoomSubstitutionDto::getType)
            .anyMatch(roomType -> roomType.equals(roomTypeInfoDto.getRoomType())))
        .collect(groupingBy(RoomTypeInfoDto::getRoomClass,
            mapping(RoomTypeInfoDto::getRoomType, toCollection(ArrayList::new))));
  }


  private AvailabilityRoom createAvailabilityRoom(RoomSubstitutionDto roomSubstitutionDto,
      RoomRateTypeDto roomRateTypeDto,
      String roomClass,
      Integer adults,
      String preferredPmsRoomType) {
    var isSubstitution = !StringUtils.equals(preferredPmsRoomType, roomSubstitutionDto.getType());
    var availabilityRoomBuilder = AvailabilityRoom.builder()
        .pmsRoomType(roomRateTypeDto.getRoomType())
        .silentSubstitution(roomSubstitutionDto.getSilent())
        .isSubstitution(isSubstitution)
        .substitution(isSubstitution ? preferredPmsRoomType : null)
        .specialRequests(createAccessibleRequestsList(roomSubstitutionDto))
        .cotAvailable(Boolean.FALSE)
        .roomClass(roomClass)
        .ratePlanSet(roomRateTypeDto.getRatePlanSet());

    // Extract and set effective rates if available
    var priceBreakdown = extractPriceBreakdownWithEffectiveRate(roomRateTypeDto, adults);
    if (priceBreakdown != null) {
      availabilityRoomBuilder.roomPriceBreakdown(priceBreakdown);
    }

    return availabilityRoomBuilder.build();
  }

  private List<String> createAccessibleRequestsList(RoomSubstitutionDto roomSubstitutionDto) {
    return roomSubstitutionDto.getAccessibleSpecialRequest() != null
        ? List.of(roomSubstitutionDto.getSpecialRequest(),
        roomSubstitutionDto.getAccessibleSpecialRequest())
        : List.of(roomSubstitutionDto.getSpecialRequest());
  }

  /**
   * Extracts price breakdown with effective rate from RoomRateTypeDto.
   * This method preserves the effectiveRate information from the availability response.
   *
   * @param roomRateTypeDto The room rate DTO containing rate information
   * @return AvailabilityRoomPriceBreakdown with daily prices including effectiveRate, or null if no rate data
   */
  private AvailabilityRoomPriceBreakdown extractPriceBreakdownWithEffectiveRate(
      RoomRateTypeDto roomRateTypeDto, Integer adults) {
    if (roomRateTypeDto.getRates() == null || roomRateTypeDto.getRates().getRate() == null
        || roomRateTypeDto.getRates().getRate().isEmpty()) {
      return null;
    }

    var rates = roomRateTypeDto.getRates().getRate();
    var dailyPrices = new ArrayList<AvailabilityDailyPrice>();

    for (var amountTypeDto : rates) {

      if (amountTypeDto.getEffectiveRate() != null) {

        var startDate = LocalDate.parse(amountTypeDto.getStart());
        var endDate = LocalDate.parse(amountTypeDto.getEnd());
        var rate = amountTypeDto.getEffectiveRate().getAmountBeforeTax();
        if (adults.equals(2)) {
          rate = rate.add(rate).subtract(amountTypeDto.getBase().getAmountBeforeTax());
        }

        // Add daily prices for the date range
        addDailyPrices(dailyPrices, startDate, endDate, rate);
      }
    }

    // Only create price breakdown if we have effective rates
    if (dailyPrices.isEmpty()) {
      return null;
    }

    return AvailabilityRoomPriceBreakdown.builder()
        .dailyPrices(dailyPrices)
        .build();
  }

  private void addDailyPrices(List<AvailabilityDailyPrice> dailyPrices, LocalDate startDate, LocalDate endDate,
      BigDecimal rate) {

    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
      dailyPrices.add(AvailabilityDailyPrice.builder()
          .date(date.toString())
          .effectiveRate(rate)
          .build());
    }
  }

  @Override
  public AvailabilityRoomPriceBreakdown getHotelRoomPriceBreakdown(
      AvailabilityRoomSearchCriteria roomAvailabilitySearch) {
    log.debug("Entered getHotelRoomPriceBreakdown for hotelId={}",
        roomAvailabilitySearch.getHotelId());
    AvailabilityRequestDto availabilityRequest =
        availabilityRequestMapper.toAvailabilityRequestDto(roomAvailabilitySearch);

    var response = apiLimitsService.getRateInfoResponse(availabilityRequest);

    var priceBreakdown = priceBreakdownMapper.toDomainModel(response.getSummary());
    priceBreakdown.setTotalTaxAmount(
        response.getSummary().getNet().subtract(response.getSummary().getGross()));
    return priceBreakdown;
  }

  @Override
  public List<AvailabilityRoomPriceBreakdown> getRatesInfo(
      List<AvailabilityRoomSearchCriteria> rooms) {
    if (rooms == null) {
      log.debug("Entered getRatesInfo for 0 rooms");
      return Collections.emptyList();
    }
    log.debug("Entered getRatesInfo for {} rooms", rooms.size());
    return rooms
        .stream()
        .map(availabilityRequestMapper::toAvailabilityRequestDto)
        .map(request -> priceBreakdownMapper.toDomainModel(
            apiLimitsService.getRateInfoResponse(request).getSummary()))
        .toList();
  }

  @Override
  public ItemInventoryResponse getHotelItemsInventory(ItemInventoryRequest itemInventoryRequest) {
    log.debug("Entered getHotelItemsInventory for hotelId={}", itemInventoryRequest.getHotelId());
    ItemInventoryResponseDto response = apiLimitsService.getItemInventoryResponses(
        itemInventoryRequest);
    return itemsInventoryMapper.toDomainModel(response);
  }

  @Override
  public HotelInventoryRoomType getHotelRoomsInventory(
      HotelInventoryRequest hotelInventoryRequest) {
    HotelInventoryRequestDto hotelInventoryRequestDto = hotelRoomInventoryMapper.toDto(
        hotelInventoryRequest);

    List<HotelInventoryDto> hotelInventoryDtos = getHotelInventory(
        hotelInventoryRequestDto.getHotelId(),
        hotelInventoryRequestDto.getDateRangeStart(),
        hotelInventoryRequestDto.getDateRangeEnd(), 1)
        .block();

    if (hotelInventoryDtos == null) {
      var ex = new HotelReservationException(ErrorCode.DIGITAL_NO_HOTEL_ROOM_EXCEPTION,
          String.format("No hotel inventory with hotel id id:%s, start=%s, end=%s",
              hotelInventoryRequestDto.getHotelId(), hotelInventoryRequestDto.getDateRangeStart(),
              hotelInventoryRequestDto.getDateRangeEnd()));
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    // Extract the room counts from Opera and filter the available rooms
    var roomLevelInventory = getRoomTypeAvailabilityCount(hotelInventoryDtos)
        .entrySet().stream().filter(room -> room.getValue() != 0)
        .map(room -> RoomLevelInventory.builder().code(room.getKey())
            .availableCount(room.getValue()).build()
        )
        .toList();

    return HotelInventoryRoomType.builder().roomTypeInventories(roomLevelInventory).build();
  }

  @Override
  public RateCodePricingResult getRateCodePricing(RateCodeCriteria rateCodeCriteria) {
    return calculateRateCodePricingRs(rateCodeCriteria);
  }

  @Override
  public List<HotelAvailabilityResult> getMultiHotelAvailabilities(
      MultiHotelAvailabilityRequest availabilityRequest, List<RoomMatrix> roomTypes) {

    List<String> ratePlanSets = Arrays.asList(PBN_RATE_PLAN_SET, PBF_RATE_PLAN_SET);
    List<HotelAvailabilityResult> multiAvailabilityResult = new LinkedList<>();
    List<MultiHotelAvailabilityRequestDto> splitHotelsList = new LinkedList<>();
    for (RoomMatrix room : roomTypes) {
      splitRequest(splitHotelsList, availabilityRequest, ratePlanSets, room);
    }

    Flux.fromIterable(splitHotelsList)
        .flatMap(this::sendGetMultiHotelAvailability,
            availabilityProperties.getMaxAvailabilityConcurrency())
        .doOnNext(result -> mergeAvailabilityResults(availabilityRequest, multiAvailabilityResult,
            result.getT1(), multiAvailabilityRequestMapper.toResultModel(result.getT2())))
        .collectList()
        .block();

    calculateFinalPrices(availabilityRequest, multiAvailabilityResult);

    return multiAvailabilityResult;
  }

  private Mono<Tuple2<MultiHotelAvailabilityRequestDto, HotelAvailability>> sendGetMultiHotelAvailability(
      MultiHotelAvailabilityRequestDto requestDto) {
    return Mono.just(requestDto)
        .zipWith(ohipAvailabilityClient.getMultiHotelAvailabilityRequest(requestDto));
  }

  private void mergeAvailabilityResults(MultiHotelAvailabilityRequest request,
      List<HotelAvailabilityResult> multiAvailabilityResult,
      MultiHotelAvailabilityRequestDto requestDto,
      MultiAvailabilityResult result) {

    var roomMatrix = requestDto.getRoomMatrix();

    for (String hotelId : requestDto.getHotelIds()) {
      var roomRateInfoList = result.getHotelAvailabilityResults().stream().filter(hotel ->
          hotelId.equals(hotel.getHotelId())).findFirst();
      roomRateInfoList.ifPresent(hotelAvailabilityResult ->
          multiAvailabilityResult.stream()
              .filter(hotel -> hotelId.equals(hotel.getHotelId()))
              .findFirst().ifPresentOrElse(
                  hotelFound -> hotelFound.getRoomTypes().stream().filter(
                          room -> room.getRoomType().equals(roomMatrix.getOriginalRoomType().getFirst()))
                      .findFirst().ifPresentOrElse(
                          roomFound -> {
                            var roomRates = hotelAvailabilityResult.getRoomTypes().getFirst().getRoomRates();
                            if (roomRates != null && !roomRates.isEmpty()) {
                              roomFound.getRoomRates().addAll(roomRates);
                              hotelFound.setAvailable(true);
                            } else {
                              hotelFound.setAvailable(false);
                              roomFound.setRoomRates(new LinkedList<>());
                            }
                          },
                          () -> {
                            var roomToBeAdded = RoomType.builder()
                                .roomType(roomMatrix.getOriginalRoomType().getFirst())
                                .numberOfRooms(roomMatrix.getQuantity())
                                .adults(StringUtils.join(roomMatrix.getAdults(), ", "))
                                .children(StringUtils.join(roomMatrix.getChildren(), ", "))
                                .cotRequested(StringUtils.join(roomMatrix.getCotRequired(), ", "))
                                .roomRates(new LinkedList<>())
                                .build();
                            if (Boolean.TRUE.equals(hotelFound.getAvailable())) {
                              roomToBeAdded.getRoomRates().addAll(
                                  hotelAvailabilityResult.getRoomTypes().getFirst().getRoomRates());
                            } else {
                              roomToBeAdded.setRoomRates(new LinkedList<>());
                            }
                            hotelFound.getRoomTypes().add(roomToBeAdded);
                          }),
                  () -> {
                    var hotelToBeAdded = HotelAvailabilityResult.builder()
                        .hotelId(hotelId)
                        .available(hotelAvailabilityResult.getRoomTypes().getFirst().getRoomRates().isEmpty()
                            ? Boolean.FALSE : Boolean.TRUE)
                        .arrivalDate(request.getArrivalDate())
                        .departureDate(request.getDepartureDate())
                        .roomTypes(new LinkedList<>())
                        .build();
                    var roomToBeAdded = RoomType.builder()
                        .roomType(roomMatrix.getOriginalRoomType().getFirst())
                        .numberOfRooms(roomMatrix.getQuantity())
                        .adults(StringUtils.join(roomMatrix.getAdults(), ", "))
                        .children(StringUtils.join(roomMatrix.getChildren(), ", "))
                        .cotRequested(StringUtils.join(roomMatrix.getCotRequired(), ", "))
                        .roomRates(new LinkedList<>())
                        .build();
                    if (Boolean.TRUE.equals(hotelToBeAdded.getAvailable())) {
                      roomToBeAdded.getRoomRates()
                          .addAll(hotelAvailabilityResult.getRoomTypes().getFirst().getRoomRates());
                    } else {
                      roomToBeAdded.setRoomRates(new LinkedList<>());
                    }
                    hotelToBeAdded.getRoomTypes().add(roomToBeAdded);
                    multiAvailabilityResult.add(hotelToBeAdded);
                  }));
    }
  }

  /**
   * Gets the available room types from Opera hotel inventory within a date range.
   * When the range is greater than MAX_RANGE_DAYS, it is split in multiple intervals and passed in concurrent requests.
   * If the results from different threads contain entries for the same room type,
   * the one with the lowest available count is kept.
   * The room type is finally filtered out if the availability is 0.
   * e.g. if we have 2 ranges [2025-10-01, 2025-10-05] and [2025-10-06, 2025-10-10],
   * the first with room type A with availabilityCount = 2
   * and the second with room type A with availabilityCount = 0, the final availability is 0
   * and room type A will be filtered out,
   * because it is not available for the fully combined date range [2023-10-01, 2023-10-10]
   **/
  @SneakyThrows
  @Override
  public List<String> getRoomTypesFromHotelInventory(String hotelId, String arrivalDate, String departureDate,
      int roomCountRequested) {

    List<Pair<LocalDate, LocalDate>> dateIntervals =
        DateUtils.splitDateRange(arrivalDate, departureDate, MAX_RANGE_DAYS, YYYY_MM_DD);

    List<String> roomTypes;
    try {
      Instant startTime = Instant.now();

      roomTypes = getHotelIventoryList(hotelId, dateIntervals, roomCountRequested).stream()
          .map(CompletableFuture::join)
          .filter(dto -> CollectionUtils.isNotEmpty(dto.getHotelInventories()))
          .map(dto -> dto.getHotelInventories().getFirst())
          .flatMap(inventory -> inventory.getRoomTypeInventories().stream())
          .collect(groupingBy(InventoryLevelCountsListTypeDto::getCode,
             minBy(Comparator.comparingInt(
                 roomTypeInventory -> roomTypeInventory.getInventoryCounts().getFirst().getAvailableCount()))))
          .values().stream()
          .filter(Optional::isPresent).map(Optional::get)
          .filter(roomTypeInventory -> roomTypeInventory.getInventoryCounts().getFirst().getAvailableCount() > 0)
          .map(InventoryLevelCountsListTypeDto::getCode)
          .distinct().toList();
      log.debug("GetRoomTypesFromHotelInventory for ohip request response roomTypes={}",
          roomTypes);

      log.info("Completed calls for hotel inventory in intervals {} in {} milliseconds.", dateIntervals,
          Duration.between(startTime, Instant.now()).toMillis());
    } catch (CompletionException ex) {
      throw ex.getCause();
    }

    return roomTypes;
  }

  private List<CompletableFuture<HotelInventoryDto>> getHotelIventoryList(
      String hotelId, List<Pair<LocalDate, LocalDate>> intervals, int roomCountRequested) {
    return intervals.stream()
        .map(interval -> CompletableFuture.supplyAsync(() -> {
          String startDate = interval.getLeft().toString();
          String endDate = interval.getRight().toString();
          try {
            HotelInventoryDto hotelInventoryDto =
                ohipAvailabilityClient.getHotelInventory(hotelId, startDate, endDate, roomCountRequested)
                    .block();
            log.info("Request completed for interval {}", interval);
            return hotelInventoryDto;
          } catch (Exception ex) {
            log.info("Request for hotel={}, interval=[{},{}] fails with error {}", hotelId, startDate, endDate,
                ex.getMessage());
            throw ex;
          }
        }))
        .toList();
  }

  @Override
  public AvailabilityByIdsResultV2 getHotelAvailabilityByIdsV2(
      AvailabilityByIdsSearchCriteriaV2 availabilityByIdsSearchCriteria) {
    log.debug("Entered getHotelAvailabilityByIdsV2 for hotelIds={}",
            availabilityByIdsSearchCriteria.getHotelIds());
    AvailabilityByIdsResultV2 noAvailabilityByIdsResultV2 = AvailabilityByIdsResultV2.builder().build();
    List<AvailabilityByIdsSearchCriteriaV2Dto> requestDtoList = buildRequestDtoList(availabilityByIdsSearchCriteria);

    if (requestDtoList.isEmpty() || CollectionUtils.isEmpty(requestDtoList.stream()
            .map(AvailabilityByIdsSearchCriteriaV2Dto::getHotelIds).toList())
            || CollectionUtils.isEmpty(requestDtoList.stream()
                    .map(AvailabilityByIdsSearchCriteriaV2Dto::getHotelIds).toList().stream()
                    .findAny().orElse(Collections.emptyList()))) {
      return noAvailabilityByIdsResultV2;
    }

    List<AvailabilityByIdsResultV2> responses =
        Objects.requireNonNullElse(fetchAvailabilityInParallel(requestDtoList).block(), List.of());

    if (isAnyResponseWithoutAvailability(responses, requestDtoList.size())) {
      return noAvailabilityByIdsResultV2;
    }

    String channel = availabilityByIdsSearchCriteria.getBookingChannel().getChannel();
    if (DISTRIBUTION_CHANNEL.equals(channel)) {
      return resolveDistributionAvailability(availabilityByIdsSearchCriteria, requestDtoList,
          responses, noAvailabilityByIdsResultV2);
    }
    return mergeResponses(responses, channel, requestDtoList.size());
  }

  /**
   * DISTR-channel resolution: inventory-filters parallel responses;
   * falls back to a combined OPERA request if the filtered merge yields no availability.
   */
  private AvailabilityByIdsResultV2 resolveDistributionAvailability(
      AvailabilityByIdsSearchCriteriaV2 criteria,
      List<AvailabilityByIdsSearchCriteriaV2Dto> requestDtoList,
      List<AvailabilityByIdsResultV2> responses,
      AvailabilityByIdsResultV2 noAvailability) {

    List<AvailabilityByIdsResultV2> inventoryFilteredResponses =
        checkInventoryAndComputeResponse(criteria, requestDtoList, responses);

    AvailabilityByIdsResultV2 mergedResult =
        mergeResponses(inventoryFilteredResponses, criteria.getBookingChannel().getChannel(),
            requestDtoList.size());

    if (CollectionUtils.isEmpty(mergedResult.getHotelAvailability())) {
      // All individual per-room calls returned availability, but our inventory check detected
      // room-type duplication across the parallel responses.
      // Retry with a single combined OPERA request.
      log.info("Inventory conflict detected for hotelIds={}. Retrying with combined request.",
          criteria.getHotelIds());
      return retryWithCombinedRequest(criteria, noAvailability);
    }
    return mergedResult;
  }

  /**
   * Sends all rooms in one combined OPERA request. Called when per-room split responses
   * individually had availability but merged to no-availability due to room-type duplication.
   * Skips {@link #checkInventoryAndComputeResponse} — OPERA handles allocation in combined
   * requests; re-filtering would double-count room types.
   * {@link #fetchAvailabilityRaw} skips {@link #mapRequestToResponse}, so occupancy is restored
   * by {@link #enrichCombinedResponseWithOccupancy} and {@code globalCompanyId} by
   * {@link #applyGlobalCompanyId}.
   */
  private AvailabilityByIdsResultV2 retryWithCombinedRequest(
      AvailabilityByIdsSearchCriteriaV2 criteria,
      AvailabilityByIdsResultV2 noAvailability) {

    log.debug("Retrying availability with combined room request for DISTR channel, hotelIds={}",
        criteria.getHotelIds());

    var combinedRequestDtoList = buildCombinedRequestDtoList(criteria);

    // Build parallel lists: one Mono and one corporateId per batch.
    // All batches from the same DTO share the same corporateId.
    List<Pair<Mono<AvailabilityByIdsResultV2>, String>> combinedPairs = combinedRequestDtoList.stream()
        .flatMap(dto -> {
          String corpId = dto.getRates() != null && dto.getRates().getCorporateRates() != null
              ? dto.getRates().getCorporateRates().getCorporateId() : null;
          return createBatchRequestsRaw(dto).map(mono -> Pair.of(mono, corpId));
        })
        .toList();

    var combinedMonos = combinedPairs.stream().map(Pair::getLeft).toList();
    var combinedCorpIds = combinedPairs.stream().map(Pair::getRight).toList();

    List<AvailabilityByIdsResultV2> combinedResponses =
        Objects.requireNonNullElse(
            zip(combinedMonos, results -> Flux.fromArray(results)
                .cast(AvailabilityByIdsResultV2.class)
                .collectList())
                .flatMap(mono -> mono)
                .block(),
            List.of());

    // Fail fast only on missing responses (a Mono failed) or null hotelAvailability (OPERA error).
    // Empty hotelAvailability is valid — that batch simply had no available hotels; mergeResponses
    // drops them naturally. Checking against combinedMonos.size() (not combinedRequestDtoList.size())
    // is correct: batching can produce more Monos than DTOs.
    if (combinedResponses.size() < combinedMonos.size()
        || combinedResponses.stream().anyMatch(r -> r.getHotelAvailability() == null)) {
      log.debug("Combined retry: incomplete or error responses for hotelIds={} — returning no availability.",
          criteria.getHotelIds());
      return noAvailability;
    }

    // Enrich with occupancy (adults/children/rooms/tag) so insertSpecialRequests can resolve substitutions.
    enrichCombinedResponseWithOccupancy(combinedResponses, combinedRequestDtoList);

    // Restore globalCompanyId on room rates — skipped by fetchAvailabilityRaw for corporate-rate requests.
    for (int i = 0; i < combinedResponses.size(); i++) {
      applyGlobalCompanyId(combinedResponses.get(i),
          i < combinedCorpIds.size() ? combinedCorpIds.get(i) : null);
    }

    return mergeResponses(combinedResponses, criteria.getBookingChannel().getChannel(),
        combinedRequestDtoList.size());
  }

  /**
   * Sets adults, children, numberOfRooms and tag on each {@link RoomTypeV2} in the combined
   * response by matching it to the corresponding request room.
   * Phase 1: match by roomType candidate list (each request room consumed at most once).
   * Phase 2: positional fallback for response rooms not in any candidate list.
   */
  private void enrichCombinedResponseWithOccupancy(List<AvailabilityByIdsResultV2> responses,
      List<AvailabilityByIdsSearchCriteriaV2Dto> combinedRequestDtoList) {

    if (CollectionUtils.isEmpty(combinedRequestDtoList)) {
      return;
    }

    List<RoomByIdsDto> requestRooms = combinedRequestDtoList.getFirst().getRooms();
    if (CollectionUtils.isEmpty(requestRooms)) {
      return;
    }

    List<RoomTypeV2> allResponseRooms = responses.stream()
        .flatMap(r -> Optional.ofNullable(r.getHotelAvailability()).orElse(Collections.emptyList()).stream())
        .flatMap(ha -> Optional.ofNullable(ha.getRoomStays()).orElse(Collections.emptyList()).stream())
        .flatMap(rs -> Optional.ofNullable(rs.getRoomTypes()).orElse(Collections.emptyList()).stream())
        .toList();

    // Phase 1: exact roomType match — each request room is consumed at most once
    List<RoomByIdsDto> remainingRequestRooms = new ArrayList<>(requestRooms);
    List<RoomTypeV2> unmatchedResponseRooms = new ArrayList<>();

    for (RoomTypeV2 responseRoom : allResponseRooms) {
      boolean matched = false;
      for (Iterator<RoomByIdsDto> it = remainingRequestRooms.iterator(); it.hasNext(); ) {
        RoomByIdsDto requestRoom = it.next();
        if (CollectionUtils.isNotEmpty(requestRoom.getRoomTypes())
            && requestRoom.getRoomTypes().contains(responseRoom.getRoomType())) {
          applyOccupancy(responseRoom, requestRoom);
          it.remove();
          matched = true;
          break;
        }
      }
      if (!matched) {
        unmatchedResponseRooms.add(responseRoom);
      }
    }

    // Phase 2: positional fallback for response rooms whose type wasn't in any candidate list
    int fallbackCount = Math.min(unmatchedResponseRooms.size(), remainingRequestRooms.size());
    for (int i = 0; i < fallbackCount; i++) {
      RoomTypeV2 responseRoom = unmatchedResponseRooms.get(i);
      RoomByIdsDto requestRoom = remainingRequestRooms.get(i);
      applyOccupancy(responseRoom, requestRoom);
      log.warn("Combined retry: roomType={} not in any candidate list — positional fallback to tag={}",
          responseRoom.getRoomType(), requestRoom.getTag());
    }

    int orphanCount = unmatchedResponseRooms.size() - fallbackCount;
    if (orphanCount > 0) {
      log.warn("Combined retry: {} response room(s) could not be matched to any request room "
          + "and will carry no occupancy data.", orphanCount);
    }
  }

  /** Builds one OPERA request containing all rooms combined (fallback when split requests yield no availability). */
  private List<AvailabilityByIdsSearchCriteriaV2Dto> buildCombinedRequestDtoList(
          AvailabilityByIdsSearchCriteriaV2 availabilityByIdsSearchCriteria) {
    List<AvailabilityByIdsSearchCriteriaV2Dto> requests = new ArrayList<>();
    addCorporateRateRequestsForRooms(
            availabilityByIdsSearchCriteria.getRooms(),
            availabilityByIdsSearchCriteria,
            requests);
    return requests;
  }

  /**
   * Copies adults, children, numberOfRooms and tag onto a response room.
   * Tag is required by {@code insertSpecialRequests} to key into roomSubstitutionMap
   * ({@code "${adults}-${children}-${tag}"}).
   */
  private void applyOccupancy(RoomTypeV2 responseRoom, RoomByIdsDto requestRoom) {
    responseRoom.setAdults(Optional.ofNullable(requestRoom.getAdults()).map(Object::toString).orElse(null));
    responseRoom.setChildren(Optional.ofNullable(requestRoom.getChildren()).map(Object::toString).orElse(null));
    responseRoom.setNumberOfRooms(Optional.ofNullable(requestRoom.getNumberOfRooms())
        .map(Object::toString).orElse(DEFAULT_NUMBER_OF_ROOMS));
    responseRoom.setTag(requestRoom.getTag());
  }

  /**
   * Sets {@code globalCompanyId} on every room rate in the response.
   * Called in the combined retry path because {@link #fetchAvailabilityRaw} skips
   * {@link #mapRequestToResponse}, which is where {@code globalCompanyId} is normally applied
   * for corporate-rate requests.
   */
  private void applyGlobalCompanyId(AvailabilityByIdsResultV2 response, String corporateId) {
    if (corporateId == null) {
      return;
    }
    Optional.ofNullable(response.getHotelAvailability()).orElse(Collections.emptyList()).stream()
        .flatMap(ha -> Optional.ofNullable(ha.getRoomStays()).orElse(Collections.emptyList()).stream())
        .flatMap(rs -> Optional.ofNullable(rs.getRoomTypes()).orElse(Collections.emptyList()).stream())
        .flatMap(rt -> Optional.ofNullable(rt.getRoomRates()).orElse(Collections.emptyList()).stream())
        .forEach(roomRate -> roomRate.setGlobalCompanyId(corporateId));
  }

  private Map<String, Map<String, Integer>> roomTypesCount(
      List<AvailabilityByIdsResultV2> responses, String hotelId) {
    Map<String, Map<String, Integer>> roomTypesNrByHotelId = new HashMap<>();
    Map<String, Integer> value = new HashMap<>();
    for (AvailabilityByIdsResultV2 response : responses) {
      List<AvailabilityResultV2> hotelAvailability = response.getHotelAvailability();
      if (hotelAvailability != null) {
        for (AvailabilityResultV2 hotel : hotelAvailability) {
          if (hotelId.equals(hotel.getHotelId())) {
            List<RoomStay> roomStays = hotel.getRoomStays();
            for (RoomStay roomStay : roomStays) {
              List<RoomTypeV2> roomTypes = roomStay.getRoomTypes();
              for (RoomTypeV2 roomType : roomTypes) {
                value.put(roomType.getRoomType(), value.getOrDefault(roomType.getRoomType(),
                        0) + 1);
                roomTypesNrByHotelId.put(hotelId, value);
              }
            }
          }
        }
      }
    }
    return roomTypesNrByHotelId;
  }

  private List<AvailabilityByIdsResultV2> checkInventoryAndComputeResponse(
          AvailabilityByIdsSearchCriteriaV2 availabilityByIdsSearchCriteria,
          List<AvailabilityByIdsSearchCriteriaV2Dto> requestDtoList,
          List<AvailabilityByIdsResultV2> responses) {

    //check opera inventory for given request
    Map<String, List<HotelInventoryDto>> hotelInventoryMap = checkAndGetHotelInventory(
            availabilityByIdsSearchCriteria, requestDtoList, responses);
    Map<String, Map<String, Integer>> roomTypesResponseMap = new HashMap<>();

    //compute a map for response -> key: hotelId, value : map of roomType as key and nr of available rooms
    hotelInventoryMap.keySet().forEach(key -> roomTypesResponseMap.putAll(roomTypesCount(responses, key)));

    //verify if opera inventory has enough rooms for every roomType from response
    Map<String, List<InventoryLevelCountsListTypeDto>> roomTypeInventories = new HashMap<>();
    List<AvailabilityByIdsResultV2> computedResponse = new ArrayList<>();
    hotelInventoryMap.forEach((key, hotelInventoryDtos) -> hotelInventoryDtos.stream()
            .filter(Objects::nonNull)
            .forEach(hotelInventoryDto -> {
              List<HotelInventoryTypeDto> hotelInventories = hotelInventoryDto.getHotelInventories();
              hotelInventories.stream()
                      .filter(Objects::nonNull)
                      .forEach(hotelInventory ->
                              roomTypeInventories.put(key, hotelInventory.getRoomTypeInventories()));
            }));
    roomTypeInventories.forEach((key, inventoryTypeDtoCount) -> inventoryTypeDtoCount
            .stream().filter(Objects::nonNull)
            .forEach(inventoryTypeDto -> {
              InventoryCountsTypeDto inventoryTypeCount = inventoryTypeDto.getInventoryCounts().stream()
                        .findFirst().orElse(null);
              Integer inventoryAvailableTypeCount = Objects.nonNull(inventoryTypeCount)
                        ? inventoryTypeCount.getAvailableCount() : Integer.valueOf(0);
              String inventoryCode = inventoryTypeDto.getCode();

              if (roomTypesResponseMap.get(key).containsKey(inventoryCode)) {
                Map<String, Integer> roomTypeResponseCount = roomTypesResponseMap.get(key);

                //if there is enough availability in opera, then compute response
                if (roomTypeResponseCount.get(inventoryCode) <= inventoryAvailableTypeCount) {
                  List<AvailabilityByIdsResultV2> availabilityByIdsResultV2s = filterRoomStays(
                          responses, inventoryCode, key);
                  computedResponse.addAll(availabilityByIdsResultV2s);
                }
              }
            }));

    return computedResponse;
  }

  public static List<AvailabilityByIdsResultV2> getAvailabilityResponsesDeepCopy(
          List<AvailabilityByIdsResultV2> responses) {
    return OBJECT_MAPPER.readValue(
        OBJECT_MAPPER.writeValueAsString(responses), new TypeReference<>() {});
  }

  public static List<AvailabilityByIdsResultV2> filterRoomStays(List<AvailabilityByIdsResultV2> responses, String rt,
                                                                String hotelId) {
    List<AvailabilityByIdsResultV2> results = new ArrayList<>();
    List<AvailabilityByIdsResultV2> responsesCopy = getAvailabilityResponsesDeepCopy(responses);
    for (AvailabilityByIdsResultV2 response : responsesCopy) {
      List<AvailabilityResultV2> hotelAvailability = response.getHotelAvailability();
      for (AvailabilityResultV2 availability : hotelAvailability) {
        if (hotelId.equals(availability.getHotelId())) {
          List<RoomStay> filteredRoomStays = availability.getRoomStays().stream()
                  .filter(roomStay -> roomStay.getRoomTypes().stream()
                          .anyMatch(roomType -> rt.equals(roomType.getRoomType())))
                  .toList();
          availability.setHotelId(hotelId);
          if (!filteredRoomStays.isEmpty()) {
            availability.setRoomStays(filteredRoomStays);
            AvailabilityByIdsResultV2 res = new AvailabilityByIdsResultV2();
            res.setHotelAvailability(List.of(availability));
            results.add(res);
          }
        }
      }
    }
    return results;
  }

  public Mono<List<AvailabilityByIdsResultV2>> fetchAvailabilityInParallel(
      List<AvailabilityByIdsSearchCriteriaV2Dto> requestDtoList) {

    List<Mono<AvailabilityByIdsResultV2>> responseList = requestDtoList.stream()
        .flatMap(this::createBatchRequests)
        .toList();

    return zip(responseList, results -> Flux.fromArray(results)
        .cast(AvailabilityByIdsResultV2.class)
        .collectList())
        .flatMap(mono -> mono);
  }

  private Stream<Mono<AvailabilityByIdsResultV2>> createBatchRequests(AvailabilityByIdsSearchCriteriaV2Dto request) {
    return createBatchedMonos(request, this::fetchAvailability);
  }

  /** Like {@link #createBatchRequests} but skips {@link #mapRequestToResponse} occupancy mapping. */
  private Stream<Mono<AvailabilityByIdsResultV2>> createBatchRequestsRaw(
      AvailabilityByIdsSearchCriteriaV2Dto request) {
    return createBatchedMonos(request, this::fetchAvailabilityRaw);
  }

  /**
   * Splits {@code request} into hotel-ID batches of at most {@link #BATCH_SIZE} and maps each
   * batch to a {@link Mono} using the supplied {@code fetcher}. When the hotel list fits in a
   * single batch the stream contains exactly one element.
   */
  private Stream<Mono<AvailabilityByIdsResultV2>> createBatchedMonos(
          AvailabilityByIdsSearchCriteriaV2Dto request,
          Function<AvailabilityByIdsSearchCriteriaV2Dto, Mono<AvailabilityByIdsResultV2>> fetcher) {

    List<String> hotelIds = request.getHotelIds();
    if (CollectionUtils.isNotEmpty(hotelIds) && hotelIds.size() > BATCH_SIZE) {
      int size = hotelIds.size();
      int batchCount = (size + BATCH_SIZE - 1) / BATCH_SIZE;
      return IntStream.range(0, batchCount)
          .mapToObj(batchIndex -> {
            int fromIndex = batchIndex * BATCH_SIZE;
            int toIndex = Math.min(fromIndex + BATCH_SIZE, size);
            AvailabilityByIdsSearchCriteriaV2Dto batchRequest = request.toBuilder()
                .hotelIds(List.copyOf(hotelIds.subList(fromIndex, toIndex)))
                .build();
            return fetcher.apply(batchRequest);
          });
    }
    return Stream.of(fetcher.apply(request));
  }

  private Mono<AvailabilityByIdsResultV2> fetchAvailability(AvailabilityByIdsSearchCriteriaV2Dto request) {
    return ohipAvailabilityClient
        .getHotelAvailabilityByIdsRequestV2(request)
        .map(result -> {
          AvailabilityByIdsResultV2 response = availabilityByIdsRequestMapper.toResultV2Model(result);
          mapRequestToResponse(request, response);
          return response;
        });
  }

  /**
   * Fetches and maps an OPERA availability response; skips {@link #mapRequestToResponse}
   * (tag-based occupancy is unreliable for multi-room combined requests).
   * Callers must compensate: occupancy via {@link #enrichCombinedResponseWithOccupancy},
   * {@code globalCompanyId} via {@link #applyGlobalCompanyId}.
   */
  private Mono<AvailabilityByIdsResultV2> fetchAvailabilityRaw(AvailabilityByIdsSearchCriteriaV2Dto request) {
    return ohipAvailabilityClient
        .getHotelAvailabilityByIdsRequestV2(request)
        .map(availabilityByIdsRequestMapper::toResultV2Model);
  }

  /**
   * Enhances Opera responses with details from the request: number of adults, number of children and number of rooms.
   * Currently, it is assumed that the number of rooms will always be equal to 1.
   */
  private void mapRequestToResponse(AvailabilityByIdsSearchCriteriaV2Dto request,
                                    AvailabilityByIdsResultV2 response) {
    String companyProfileId =
        (null != request.getRates() && null != request.getRates().getCorporateRates())
            ? request.getRates().getCorporateRates().getCorporateId()
            : null;
    Optional.ofNullable(response.getHotelAvailability()).orElse(Collections.emptyList())
        .stream()
        .map(multiRoomRateAvailabilityType -> Optional.of(multiRoomRateAvailabilityType.getRoomStays())
            .orElse(Collections.emptyList()))
        .flatMap(List::stream)
        .map(multiRoomRateStayType -> Optional.of(multiRoomRateStayType.getRoomTypes())
            .orElse(Collections.emptyList()))
        .flatMap(Collection::stream)
        .forEach(roomTypeV2 -> {
          for (RoomByIdsDto room : request.getRooms()) {
            if (Strings.CI.equals(room.getTag(), roomTypeV2.getTag())) {
              mapRoomValues(roomTypeV2, room);
              // Set companyProfileId on each RoomRateTypeDto
              if (companyProfileId != null && roomTypeV2.getRoomRates() != null) {
                roomTypeV2.getRoomRates().forEach(roomRate ->
                    roomRate.setGlobalCompanyId(companyProfileId)
                );
              }
              break;
            }
          }
        });
  }

  private void mapRoomValues(RoomTypeV2 roomTypeV2, RoomByIdsDto room) {
    roomTypeV2.setAdults(room.getAdults() == null ? null : room.getAdults().toString());
    roomTypeV2.setChildren(room.getChildren() == null ? null : room.getChildren().toString());
    roomTypeV2.setNumberOfRooms(room.getNumberOfRooms() == null ? DEFAULT_NUMBER_OF_ROOMS
            : room.getNumberOfRooms().toString());
  }

  private AvailabilityByIdsResultV2 mergeResponses(List<AvailabilityByIdsResultV2> responses, String channel,
                                                   int requestRoomNr) {
    AvailabilityByIdsResultV2 finalResult = AvailabilityByIdsResultV2.builder()
            .hotelAvailability(new ArrayList<>()).build();
    responses.stream()
            .map(AvailabilityByIdsResultV2::getHotelAvailability)
            .flatMap(Collection::stream)
            .collect(groupingBy(AvailabilityResultV2::getHotelId))
            .entrySet()
            .stream()
            .filter(entry -> isHotelAvailableForAllResponses(responses, entry.getKey(), requestRoomNr))
            .forEach(roomsByHotel -> finalResult.getHotelAvailability()
                    .add(mergeRoomStaysForHotel(roomsByHotel, channel)));
    return finalResult;
  }

  private boolean isHotelAvailableForAllResponses(List<AvailabilityByIdsResultV2> responses,
                                                  String hotelId, int requestRoomsNr) {
    Map<String, List<AvailabilityByIdsResultV2>> groupResponsesByHotelId = responses.stream()
            .flatMap(response -> response.getHotelAvailability().stream()
                    .map(availability -> Map.entry(availability.getHotelId(), response)))
            .collect(groupingBy(Entry::getKey,
                    mapping(Entry::getValue, toCollection(ArrayList::new))
            ));

    if (requestRoomsNr > groupResponsesByHotelId.get(hotelId).size()) {
      return false;
    }

    List<AvailabilityByIdsResultV2> responsesByHotelId = groupResponsesByHotelId.get(hotelId);
    return responsesByHotelId.stream()
            .map(AvailabilityByIdsResultV2::getHotelAvailability)
            .allMatch(availabilities -> availabilities.stream()
                    .anyMatch(availability -> availability.getHotelId().equals(hotelId)));
  }

  private boolean isAnyResponseWithoutAvailability(List<AvailabilityByIdsResultV2> responses,
                                                        int requestsNumber) {
    return responses == null
            || responses.size() < requestsNumber
            || responses.stream().anyMatch(response -> response.getHotelAvailability() == null
            || response.getHotelAvailability().isEmpty());

  }

  private AvailabilityResultV2 mergeRoomStaysForHotel(Entry<String, List<AvailabilityResultV2>> resultsByHotel,
                                                      String channel) {
    List<RoomStay> roomStayList;
    if (DISTRIBUTION_CHANNEL.equals(channel)) {
      roomStayList = resultsByHotel.getValue().stream().map(AvailabilityResultV2::getRoomStays)
              .filter(Objects::nonNull)
              .flatMap(Collection::stream)
              .collect(groupingBy(RoomStay::getRoomClass))
              .entrySet().stream()
              .map(entry -> {
                List<RoomTypeV2> roomTypeV2sList = entry.getValue().stream().filter(Objects::nonNull)
                        .map(RoomStay::getRoomTypes).filter(Objects::nonNull).flatMap(Collection::stream).toList();
                return RoomStay.builder().roomClass(entry.getKey()).roomTypes(roomTypeV2sList).build();
              })
              .toList();
    } else {
      roomStayList = resultsByHotel.getValue().stream().map(AvailabilityResultV2::getRoomStays)
              .filter(Objects::nonNull)
              .flatMap(Collection::stream)
              .collect(groupingBy(RoomStay::getRoomClass))
              .entrySet().stream()
              .filter(entry -> isRoomClassPresentInAllResponses(entry, resultsByHotel))
              .map(entry -> {
                List<RoomTypeV2> roomTypeV2sList = entry.getValue().stream().filter(Objects::nonNull)
                        .map(RoomStay::getRoomTypes).filter(Objects::nonNull).flatMap(Collection::stream).toList();
                return RoomStay.builder().roomClass(entry.getKey()).roomTypes(roomTypeV2sList).build();
              })
              .toList();
    }

    return AvailabilityResultV2.builder()
            .hotelId(resultsByHotel.getKey())
            .roomStays(roomStayList).build();
  }

  private boolean isRoomClassPresentInAllResponses(Entry<String, List<RoomStay>> roomStaysByRoomClass,
                                                   Entry<String, List<AvailabilityResultV2>> resultsByHotel) {
    return roomStaysByRoomClass.getValue().size() == resultsByHotel.getValue().size();
  }

  private List<AvailabilityByIdsSearchCriteriaV2Dto> buildRequestDtoList(
          AvailabilityByIdsSearchCriteriaV2 availabilityByIdsSearchCriteria) {
    if (availabilityByIdsSearchCriteria == null) {
      return Collections.emptyList();
    }
    if (CollectionUtils.isEmpty(availabilityByIdsSearchCriteria.getRooms())) {
      return List.of(availabilityByIdsRequestMapper.toRequestV2Dto(
              availabilityByIdsSearchCriteria));
    }
    List<AvailabilityByIdsSearchCriteriaV2Dto> requests = new ArrayList<>();

    if (DISTRIBUTION_CHANNEL.equals(availabilityByIdsSearchCriteria.getBookingChannel().getChannel())) {
      availabilityByIdsSearchCriteria.getRooms().forEach(room ->
          addCorporateRateRequestsForRooms(List.of(room), availabilityByIdsSearchCriteria, requests));
    } else {
      Map<Pair<Integer, Integer>, List<Room>> roomsByNrOfAdultAndChildren =
          availabilityByIdsSearchCriteria.getRooms()
              .stream()
              .collect(groupingBy(room -> Pair.of(room.getAdults(), room.getChildren())));
      roomsByNrOfAdultAndChildren.forEach((key, value) ->
          addCorporateRateRequestsForRooms(roomsByNrOfAdultAndChildren.get(key),
              availabilityByIdsSearchCriteria, requests));
    }
    return requests;
  }

  private AvailabilityByIdsSearchCriteriaV2 buildRequest(
          List<Room> rooms,
          AvailabilityByIdsSearchCriteriaV2 availabilityByIdsSearchCriteria,
          CorporateRate corporateRate) {
    return AvailabilityByIdsSearchCriteriaV2.builder()
        .bookingChannel(availabilityByIdsSearchCriteria.getBookingChannel())
        .hotelIds(availabilityByIdsSearchCriteria.getHotelIds())
        .arrivalDate(availabilityByIdsSearchCriteria.getArrivalDate())
        .departureDate(availabilityByIdsSearchCriteria.getDepartureDate())
        .rooms(rooms)
        .rates(RateV2.builder().corporateRates(null != corporateRate ? List.of(corporateRate)
                : Collections.emptyList()).ratePlanCodes(availabilityByIdsSearchCriteria.getRates().getRatePlanCodes())
            .build()).build();

  }

  private Map<String, List<HotelInventoryDto>> checkAndGetHotelInventory(AvailabilityByIdsSearchCriteriaV2
                                                                                 availabilityByIdsSearchCriteria,
                                                                         List<AvailabilityByIdsSearchCriteriaV2Dto>
                                                                                 requestDtoList,
                                                                         List<AvailabilityByIdsResultV2> responses) {
    int totalNumberOfRooms = availabilityByIdsSearchCriteria.getRooms()
            .stream().mapToInt(Room::getNumberOfRooms)
            .sum();

    Set<String> hotelIdsSet = responses.stream()
            .filter(Objects::nonNull)
            .flatMap(response -> response.getHotelAvailability() != null
                ? response.getHotelAvailability().stream()
                    .filter(Objects::nonNull)
                    .map(AvailabilityResultV2::getHotelId)
                : Stream.empty())
            .collect(toSet());

    var hotelInventoryDto = Flux.fromIterable(hotelIdsSet).flatMap(
            hotelId -> sendGetHotelInventory(hotelId, availabilityByIdsSearchCriteria.getArrivalDate().toString(),
                    availabilityByIdsSearchCriteria.getDepartureDate().toString(), totalNumberOfRooms),
                    availabilityProperties.getMaxAvailabilityConcurrency())
            .collectList();

    List<Tuple2<String, List<HotelInventoryDto>>> hotelInventoryList = Objects.requireNonNull(
            hotelInventoryDto.block());

    Map<String, List<HotelInventoryDto>> hotelInventoryMap = hotelInventoryList.stream()
            .collect(toMap(Tuple2::getT1, Tuple2::getT2));
    for (String hotelId : availabilityByIdsSearchCriteria.getHotelIds()) {
      List<HotelInventoryDto> hotelInventory = hotelInventoryMap.get(hotelId);
      if (hotelInventory != null) {
        int houseLevelAvailability = hotelInventory.getFirst().getHotelInventories().getFirst().getHouseInventory()
                .getFirst().getAvailableCount();
        if (totalNumberOfRooms > houseLevelAvailability) {
          requestDtoList.forEach(request -> {
            var hotelIds = new ArrayList<>(request.getHotelIds());
            hotelIds.remove(hotelId);
            request.setHotelIds(hotelIds);
          });
        }
      }
    }
    return hotelInventoryMap;
  }

  @Override
  public MultiAvailabilityResultV2 getMultiHotelAvailabilitiesV2(
      MultiHotelAvailabilityRequestV2 multiHotelAvailabilityRequest) {
    log.debug("Entered getMultiHotelAvailabilitiesV2 for hotelIds={}",
        multiHotelAvailabilityRequest.getHotelIds());
    var requestDto = multiAvailabilityRequestMapper.toRequestV2Dto(
        multiHotelAvailabilityRequest);
    return multiAvailabilityRequestMapper.toResultV2Model(
        apiLimitsService.getMultiHotelAvailabilityRequestV2(requestDto));
  }

  private RateCodePricingResult calculateRateCodePricingRs(RateCodeCriteria rateCodeCriteria) {

    log.debug("Begin calculating the total net cost for rate plan {} between {} and {} for hotel {}",
        rateCodeCriteria.getRatePlanCode(), rateCodeCriteria.getArrivalDate(),
        rateCodeCriteria.getDepartureDate(), rateCodeCriteria.getHotelId());

    RateCodePricingResultBuilder rateCodePricingRsBuilder = RateCodePricingResult.builder()
        .ratePlanCode(rateCodeCriteria.getRatePlanCode());
    BigDecimal totalNetAmount = new BigDecimal(0);
    for (RateCodeRoomInfoCriteria roomInfoCriteria : rateCodeCriteria.getRoomInfoCriteriaList()) {
      PriceBreakdownDto rateCodePricing =  apiLimitsService.getRateInfoResponse(rateCodeCriteria, roomInfoCriteria);
      rateCodePricingRsBuilder.currencyCode(rateCodePricing.getSummary().getCurrencyCode());
      totalNetAmount = totalNetAmount.add(rateCodePricing.getSummary().getNet());
    }
    rateCodePricingRsBuilder.totalNetAmount(totalNetAmount);

    log.debug("Finished calculating total net cost {}", totalNetAmount);

    return rateCodePricingRsBuilder.build();
  }

  private void splitRequest(
      List<MultiHotelAvailabilityRequestDto> splitHotelsList,
      MultiHotelAvailabilityRequest availabilityRequest,
      List<String> ratePlanSets, RoomMatrix room) {

    int i = 0;
    int j = 5;
    var intervals = splitRequestedPeriod(availabilityRequest.getArrivalDate(),
        availabilityRequest.getDepartureDate());

    while (i < availabilityRequest.getHotelIds().size()) {
      if (j > availabilityRequest.getHotelIds().size()) {
        j = availabilityRequest.getHotelIds().size();
      }
      var startIndex = i;
      var endIndex = j;
      for (String ratePlan : ratePlanSets) {
        intervals.forEach((key, value) -> {
          var multiHotelRequest = MultiHotelAvailabilityRequestDto.builder()
              .hotelIds(availabilityRequest.getHotelIds().subList(startIndex, endIndex))
              .roomStayStartDate(key)
              .roomStayEndDate(value)
              .roomTypes(room.getRoomsSubstitutionList())
              .roomStayQuantity(room.getQuantity())
              .ratePlanSet(ratePlan)
              .roomMatrix(room)
              .build();
          splitHotelsList.add(multiHotelRequest);
        });
      }
      i += 5;
      j += 5;
    }
  }

  private Map<String, String> splitRequestedPeriod(String arrivalDate,
      String departureDate) {
    var numberOfDays = ChronoUnit.DAYS.between(
        LocalDate.parse(arrivalDate, formatter),
        LocalDate.parse(departureDate, formatter));
    var divider = numberOfDays / availabilityProperties.getMaxRequestedDays() + 1;
    Map<String, String> intervals = new HashMap<>();
    var doRun = true;

    do {
      var tempArrivalDate = arrivalDate;
      var tempDepartureDate = LocalDate.parse(tempArrivalDate, formatter)
          .plusDays(numberOfDays / divider + 1);
      if (tempDepartureDate.compareTo(LocalDate.parse(departureDate, formatter)) >= 0) {
        tempDepartureDate = LocalDate.parse(departureDate, formatter);
        doRun = false;
      }
      intervals.put(tempArrivalDate, tempDepartureDate.toString());
      arrivalDate = tempDepartureDate.toString();
    } while (doRun);

    return intervals;
  }

  private void calculateFinalPrices(MultiHotelAvailabilityRequest availabilityRequest,
      List<HotelAvailabilityResult> multiAvailabilityResult) {

    var numberOfDays = ChronoUnit.DAYS.between(
        LocalDate.parse(availabilityRequest.getArrivalDate(), formatter),
        LocalDate.parse(availabilityRequest.getDepartureDate(), formatter));
    var divider = numberOfDays / availabilityProperties.getMaxRequestedDays() + 1;

    multiAvailabilityResult.forEach(hotel ->
        hotel.getRoomTypes().forEach(roomType -> {
          if (!roomType.getRoomRates().isEmpty()) {
            List<RoomRateInfo> finalRoomRates = new LinkedList<>();
            for (int i = 0; i < roomType.getRoomRates().size(); i++) {
              int occurrences = 0;
              for (int j = i; j < roomType.getRoomRates().size(); j++) {
                if (roomType.getRoomRates().get(i).getRoomType()
                    .equals(roomType.getRoomRates().get(j).getRoomType())
                    && roomType.getRoomRates().get(i).getRatePlan()
                    .equals(roomType.getRoomRates().get(j).getRatePlan())) {
                  if (i != j) {
                    roomType.getRoomRates().get(i).setTotalPrice(
                        roomType.getRoomRates().get(i).getTotalPrice()
                            .add(roomType.getRoomRates().get(j).getTotalPrice()));
                  }
                  occurrences++;
                }
              }
              if (occurrences == divider) {
                finalRoomRates.add(roomType.getRoomRates().get(i));
              }
            }
            roomType.setRoomRates(finalRoomRates);
          }
        }));
  }

  public List<StatisticsDateItem> getHotelInventoryStatistics(
      AvailabilityRequest availabilityRequest) {
    log.debug("Entered getHotelInventoryStatistics for hotelId={}",
        availabilityRequest.getHotelId());

    var availabilityRequestDto =
        availabilityRequestMapper.toHotelAvailabilityRequestDto(availabilityRequest);

    var operaInventoryStatistics = ohipAvailabilityClient.getHotelInventoryStatistics(
        availabilityRequestDto);
    return operaInventoryStatistics.stream().map(StatisticType::getStatistics)
      .filter(Objects::nonNull)
      .flatMap(Collection::stream)
      .filter(statisticItem -> HOTEL_ROOM_CODE.equals(statisticItem.getStatCategoryCode())).map(
        StatisticCodeType::getStatisticDate).filter(Objects::nonNull).flatMap(Collection::stream)
      .map(hotelInventoryStatisticsMapper::toStatisticsDateModel).toList();
  }

  @SneakyThrows
  @Override
  public RestrictionsByDateRangeResult getRestrictionsByDateRange(
      RestrictionsByDateRangeSearchCriteria restrictionsByDateRangeSearchCriteria) {
    List<Pair<LocalDate, LocalDate>> dateIntervals =
        DateUtils.splitDateRange(restrictionsByDateRangeSearchCriteria.getStartDate(),
            restrictionsByDateRangeSearchCriteria.getEndDate(), MAX_RANGE_DAYS, YYYY_MM_DD);
    RestrictionsByDateRangeResult result;
    try {
      Instant startTime = Instant.now();

      result = getRestrictionsByDateRange(restrictionsByDateRangeSearchCriteria.getHotelId(), dateIntervals)
          .stream().map(CompletableFuture::join)
          .reduce((result1, result2) -> {
            if (null == result1.getRestrictionsByDateRange()
                || null == result1.getRestrictionsByDateRange().getRestrictionsByDateRange()) {
              return result2;
            }
            if (null != result2.getRestrictionsByDateRange()
                && null != result2.getRestrictionsByDateRange().getRestrictionsByDateRange()) {
              result1.getRestrictionsByDateRange().getRestrictionsByDateRange().getRestrictionSets().addAll(
                  result2.getRestrictionsByDateRange().getRestrictionsByDateRange().getRestrictionSets());
            }
            return result1;
          }).orElse(new RestrictionsByDateRangeResult());

      log.debug("GetRestrictionsByDateRange for ohip request response restrictions={}", result);
      log.info("Completed calls for restrictions in intervals {} in {} milliseconds.", dateIntervals,
          Duration.between(startTime, Instant.now()).toMillis());
    } catch (CompletionException ex) {
      throw ex.getCause();
    }

    return result;
  }

  private List<CompletableFuture<RestrictionsByDateRangeResult>> getRestrictionsByDateRange(String hotelId,
      List<Pair<LocalDate, LocalDate>> intervals) {
    return intervals.stream()
        .map(interval -> CompletableFuture.supplyAsync(() -> {
          String startDate = interval.getLeft().toString();
          String endDate = interval.getRight().toString();
          try {
            RestrictionsByDateRangeResult result =
                ohipAvailabilityClient.getRestrictionsByDateRange(RestrictionsByDateRangeSearchCriteria.builder()
                    .hotelId(hotelId).startDate(startDate).endDate(endDate).build());
            log.info("Restrictions by date range request completed for interval {}", interval);
            return result;
          } catch (Exception ex) {
            log.info("Restrictions by date range request for hotel={}, interval=[{},{}] fails with error {}",
                hotelId, startDate, endDate,
                ex.getMessage());
            throw ex;
          }
        }))
        .toList();
  }

  private void addCorporateRateRequestsForRooms(List<Room> rooms,
                                                AvailabilityByIdsSearchCriteriaV2 availabilityByIdsSearchCriteriaV2,
                                                List<AvailabilityByIdsSearchCriteriaV2Dto> requests) {
    if (Objects.nonNull(availabilityByIdsSearchCriteriaV2.getRates())
        && CollectionUtils.isNotEmpty(availabilityByIdsSearchCriteriaV2.getRates().getCorporateRates())) {

      for (CorporateRate corporateRate : availabilityByIdsSearchCriteriaV2.getRates().getCorporateRates()) {
        var request = buildRequest(rooms, availabilityByIdsSearchCriteriaV2, corporateRate);
        requests.add(availabilityByIdsRequestMapper.toRequestV2Dto(request));
      }

    } else {
      var request = buildRequest(rooms, availabilityByIdsSearchCriteriaV2, null);
      requests.add(availabilityByIdsRequestMapper.toRequestV2Dto(request));
    }
  }
}
