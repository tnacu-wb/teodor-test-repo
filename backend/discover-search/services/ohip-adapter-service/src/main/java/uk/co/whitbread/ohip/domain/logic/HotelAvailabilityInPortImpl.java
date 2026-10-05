package uk.co.whitbread.ohip.domain.logic;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.BUSINESS_BOOKER_CHANNEL;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.CCUI_CHANNEL;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.DISTRIBUTION_CHANNEL;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.PI_CHANNEL;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.exceptions.ParameterMismatchException;
import uk.co.whitbread.ohip.domain.exceptions.UnavailableRatesException;
import uk.co.whitbread.ohip.domain.logic.utils.RoomPriceBreakdownUtils;
import uk.co.whitbread.ohip.domain.logic.utils.RulesAgentUtils;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchCriteriaV2;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityByIdsSearchRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityRoomSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilitySearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.HotelInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.ItemInventoryRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.MultiHotelAvailabilityRequestV2;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RestrictionsByDateRangeSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.Room;
import uk.co.whitbread.ohip.domain.model.availability.in.RoomMatrix;
import uk.co.whitbread.ohip.domain.model.availability.in.RoomPriceBreakdownRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.RoomPriceBreakdownWrapper;
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
import uk.co.whitbread.ohip.domain.model.availability.out.HotelInventoryRoomType;
import uk.co.whitbread.ohip.domain.model.availability.out.InventoryAvailability;
import uk.co.whitbread.ohip.domain.model.availability.out.ItemInventory;
import uk.co.whitbread.ohip.domain.model.availability.out.ItemInventoryResponse;
import uk.co.whitbread.ohip.domain.model.availability.out.MealsIncluded;
import uk.co.whitbread.ohip.domain.model.availability.out.MultiAvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.MultiAvailabilityResultV2;
import uk.co.whitbread.ohip.domain.model.availability.out.RateCodePricingResult;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomPriceBreakdownResult;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomStay;
import uk.co.whitbread.ohip.domain.model.availability.out.StatisticsDateItem;
import uk.co.whitbread.ohip.domain.model.availability.out.StatisticsInventoryItem;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlanInfoResponse;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitution;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitutionRequestDetails;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.ohip.domain.ports.primary.HotelAvailabilityInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.RatePlansOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Slf4j
@RequiredArgsConstructor
public class HotelAvailabilityInPortImpl implements HotelAvailabilityInPort {

  private static final String COT_NAME = "Cot";
  public static final List<String> DISTRIBUTION_SUBCHANNELS =
      List.of("AGENCY", "BOOKING", "AMADEUS", "TRAVELPORT");
  public static final String PHYSICAL_ROOMS = "PhysicalRooms";
  public static final String AVAILABLE_ROOMS = "AvailableRooms";
  public static final String SING_SPECIAL_REQUEST = "SING";
  public static final String BUSIFLEX = "BUSIFLEX";

  private final HotelAvailabilityOutPort hotelAvailabilityOutPort;
  private final RulesAgentOutPort rulesAgentOutPort;
  private final MealsIncludedConfigProperties mealsConfig;
  private final AvailabilityConfigProperties availabilityProperties;
  private final ConcurrentTracer concurrentTracer;
  private final ExecutorService restrictionsExecutorService;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final RatePlansOutPort ratePlansOutPort;
  private final Executor availabilityExecutor;

  @SneakyThrows
  @Override
  public AvailabilityResult getHotelAvailability(AvailabilitySearchCriteria availabilitySearch) {

    log.debug("Entered getHotelAvailability for hotelId={}", availabilitySearch.getHotelId());
    var availabilityRequest = AvailabilityRequest.builder()
        .roomStayStartDate(availabilitySearch.getArrivalDate())
        .roomStayEndDate(availabilitySearch.getDepartureDate())
        .roomStayQuantity(1)
        .roomTypes(availabilitySearch.getRoomTypes())
        .hotelId(availabilitySearch.getHotelId())
        .ratePlanCode(availabilitySearch.getRatePlanCode())
        .adults(availabilitySearch.getAdults())
        .children(availabilitySearch.getChildren())
        .cotsRequired(availabilitySearch.getCotsRequired())
        .channel(availabilitySearch.getChannel())
        .subchannel(availabilitySearch.getSubchannel())
        .language(availabilitySearch.getLanguage())
        .companyId(availabilitySearch.getCompanyId()).build();

    // Retrieve room substitutions for each requested <roomType, adults, children> combination
    var roomSubstitutions = getRoomSubstitutions(
        availabilitySearch.getRoomTypes(), availabilitySearch.getAdults(),
        availabilitySearch.getChildren(), availabilitySearch.getCotsRequired(),
        availabilitySearch.getChannel());
    availabilityRequest.setRoomSubstitutions(roomSubstitutions);

    AvailabilityResult hotelAvailabilityResult;
    var hotelAvailabilityPromotionResult = AvailabilityResult.builder().roomRates(new ArrayList<>()).build();
    // When there's no promo code in the request, make the availability call in the request thread ...
    // ... instead of using another parallel task
    if (!hasPromotionCode(availabilitySearch)) {
      hotelAvailabilityResult = getAvailabilityResult(availabilityRequest);
    } else {
      // Promo code is present, proceed with asynchronous calls using configurable Executor ...
      // ... instead of relying on the default ForkJoinPool
      var hotelAvailabilityResultFuture = CompletableFuture.supplyAsync(
          concurrentTracer.wrap((Supplier<AvailabilityResult>) () -> getAvailabilityResult(
              availabilityRequest)), availabilityExecutor);
      var hotelAvailabilityPromotionResultFuture = CompletableFuture.supplyAsync(
          concurrentTracer.wrap((Supplier<AvailabilityResult>) () -> getPromotionRates(
              availabilitySearch, availabilityRequest)), availabilityExecutor);
      CompletableFuture<List<AvailabilityResult>> combinedFuture = hotelAvailabilityResultFuture
          .thenCombine(hotelAvailabilityPromotionResultFuture, List::of);
      var results = combinedFuture.get();
      hotelAvailabilityResult = !results.isEmpty()
          ? results.get(0) : AvailabilityResult.builder().roomRates(new ArrayList<>()).build();
      hotelAvailabilityPromotionResult = results.size() > 1
          ? results.get(1) : AvailabilityResult.builder().roomRates(new ArrayList<>()).build();
    }
    hotelAvailabilityResult.getRoomRates().addAll(hotelAvailabilityPromotionResult.getRoomRates());

    //FIXME Temporarily disabled - see https://whitbreadis.atlassian.net/browse/OB-899
    //    var hotelInventoryStatistics = hotelAvailabilityOutPort.getHotelInventoryStatistics(availabilityRequest);
    //    var limitedAvailabilityFlag = calculateLimitedAvailabilityFlag(hotelInventoryStatistics);
    var limitedAvailabilityFlag = false;
    hotelAvailabilityResult.setLimitedAvailability(limitedAvailabilityFlag);

    addPriceBreakdownToHotelAvailabilityResult(
        availabilitySearch.getArrivalDate(),
        availabilitySearch.getDepartureDate(),
        hotelAvailabilityResult);

    if (BUSINESS_BOOKER_CHANNEL.equals(availabilitySearch.getChannel())
        && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getFlexRateStrikethroughBB())) {
      setStrikeThroughBaseRateForBb(availabilitySearch, hotelAvailabilityResult);
    }
    String basePromoRateCode = getBasePromoRateCode(availabilitySearch,
        hotelAvailabilityPromotionResult);
    if (StringUtils.isNotEmpty(basePromoRateCode)) {
      setPromotionalBaseRate(availabilitySearch, hotelAvailabilityResult,
          hotelAvailabilityPromotionResult, basePromoRateCode);
    }

    if (!hotelAvailabilityPromotionResult.getRoomRates().isEmpty() && StringUtils.isNotEmpty(
        basePromoRateCode)) {
      hotelAvailabilityResult.setRoomRates(
          removeBaseRate(hotelAvailabilityResult.getRoomRates(), basePromoRateCode));
    }

    checkForCotsAvailability(availabilitySearch.getCotsRequired(), hotelAvailabilityResult);

    checkMealsIncluded(availabilitySearch, hotelAvailabilityResult);
    List<RoomSubstitution> substitutionList = roomSubstitutions.stream()
        .flatMap(substitutionRuleResponse -> substitutionRuleResponse.getSubstitutionList().stream()).toList();
    hotelAvailabilityResult.setSubstitutionList(substitutionList);
    return hotelAvailabilityResult;
  }

  private AvailabilityResult getAvailabilityResult(
      AvailabilityRequest availabilityRequest) {
    var availability = hotelAvailabilityOutPort.getHotelAvailability(availabilityRequest);
    var roomRates = availability.getRoomRates();
    availability.setRoomRates(
        roomRates != null ? new ArrayList<>(roomRates) : new ArrayList<>());
    return availability;
  }

  private void setPromotionalBaseRate(AvailabilitySearchCriteria availabilitySearch,
      AvailabilityResult hotelAvailabilityResult,
      AvailabilityResult hotelAvailabilityPromotionResult, String basePromoRateCode) {
    if (!hasPromotionCode(availabilitySearch) || hotelAvailabilityResult.getRoomRates().isEmpty()) {
      return;
    }

    var basePromoRate = hotelAvailabilityResult.getRoomRates().stream()
        .filter(roomRate -> roomRate.getRatePlanCode().equals(basePromoRateCode))
        .findFirst();
    if (basePromoRateCode == null || basePromoRate.isEmpty()) {
      var message = String.format("No base promo rate (%s) found for hotelId=%s, ratePlanCode=%s",
          basePromoRateCode, availabilitySearch.getHotelId(), availabilitySearch.getRatePlanCode());
      throw new UnavailableRatesException(ErrorCode.DIGITAL_NO_BASE_PROMO_RATE_EXCEPTION, message);
    }

    var promoRates = hotelAvailabilityPromotionResult.getRoomRates().stream()
        .map(AvailabilityRoomRate::getRatePlanCode).collect(Collectors.toSet());
    var roomTypesBaseRates = basePromoRate.get().getRoomTypes().stream()
        .map(roomType -> roomType.getRooms().stream()
            .collect(Collectors.toMap(AvailabilityRoom::getPmsRoomType,
                room -> room.getRoomPriceBreakdown().getTotalNetAmount(),
                (existing, replacement) -> replacement)))
        .toList();

    hotelAvailabilityResult.getRoomRates().forEach(roomRate -> {
      if (promoRates.contains(roomRate.getRatePlanCode())) {
        for (int i = 0; i < roomRate.getRoomTypes().size(); i++) {
          var roomType = roomRate.getRoomTypes().get(i);
          var roomRates = roomTypesBaseRates.get(i);
          roomType.getRooms().forEach(room -> setRoomBaseRate(room, roomRates));
        }
      }
    });
  }

  public String getBasePromoRateCode(AvailabilitySearchCriteria availabilitySearch,
      AvailabilityResult hotelAvailabilityPromotionResult) {

    if (StringUtils.isEmpty(availabilitySearch.getPromotionCode())) {
      return null;
    }

    String promoRatePlanCode = hotelAvailabilityPromotionResult.getRoomRates().stream()
        .filter(
            roomRate -> availabilitySearch.getPromotionCode().equals(roomRate.getPromotionCode()))
        .map(AvailabilityRoomRate::getRatePlanCode)
        .findFirst()
        .orElse(null);

    if (StringUtils.isEmpty(promoRatePlanCode)) {
      return null;
    }

    RatePlanInfoResponse infoResponse = ratePlansOutPort.getRatePlanInfo(promoRatePlanCode,
        availabilitySearch.getHotelId());
    if (infoResponse == null || infoResponse.getRatePlanInfo() == null) {
      return null;
    }

    return infoResponse.getRatePlanInfo().stream()
        .filter(ratePlan -> ObjectUtils.isNotEmpty(ratePlan.getRatePlanBasedOnRates()))
        .flatMap(ratePlan -> ratePlan.getRatePlanBasedOnRates().stream())
        .map(rate -> rate.getDynamicBaseRate().getDynamicBasedOnRatePlan())
        .findFirst()
        .orElse(null);
  }

  private void setStrikeThroughBaseRateForBb(AvailabilitySearchCriteria availabilitySearchCriteria,
                                             AvailabilityResult hotelAvailabilityResult) {
    if (hotelAvailabilityResult.getRoomRates().isEmpty()) {
      return;
    }
      
    var baseRateCodeFromRules = rulesAgentOutPort.getBaseRate(BUSIFLEX).getBaseRate();
    
    var strikeThroughRates = hotelAvailabilityResult.getRoomRates().stream()
        .filter(roomRate -> roomRate.getRatePlanCode().equals(baseRateCodeFromRules))
        .findFirst();
    if (strikeThroughRates.isEmpty()) {
      log.info("No strike through rate (%s) found for hotelId=%s, ratePlanCode=%s",
          baseRateCodeFromRules, availabilitySearchCriteria.getHotelId(), availabilitySearchCriteria.getRatePlanCode());
      return;
    }
    
    var promoRates = BUSIFLEX;
    var roomTypesBaseRates = strikeThroughRates.get().getRoomTypes().stream()
        .map(roomType -> roomType.getRooms().stream()
            .collect(Collectors.toMap(AvailabilityRoom::getPmsRoomType,
                room -> room.getRoomPriceBreakdown().getTotalNetAmount(),
                (existing, replacement) -> replacement)))
        .toList();
    
    hotelAvailabilityResult.getRoomRates().forEach(roomRate -> {
      if (promoRates.equals(roomRate.getRatePlanCode())) {
        for (int i = 0; i < roomRate.getRoomTypes().size(); i++) {
          var roomType = roomRate.getRoomTypes().get(i);
          var roomRates = roomTypesBaseRates.get(i);
          roomType.getRooms().forEach(room -> {
            setRoomBaseRate(room, roomRates);
          });
        }
      }
    });
  }

  private static void setRoomBaseRate(AvailabilityRoom room,
      Map<String, BigDecimal> roomRates) {
    var roomTotalNetAmount = room.getRoomPriceBreakdown() != null
        ? room.getRoomPriceBreakdown().getTotalNetAmount() : null;
    var baseRateAmount = roomRates.getOrDefault(room.getPmsRoomType(), null);
    if (roomTotalNetAmount != null && baseRateAmount != null
        && roomTotalNetAmount.compareTo(baseRateAmount) < 0) {
      room.getRoomPriceBreakdown().setBaseRateAmount(baseRateAmount);
    }
  }

  private static ArrayList<AvailabilityRoomRate> removeBaseRate(
      List<AvailabilityRoomRate> roomRates, String basePromoRateCode) {
    var excludedRates = List.of(basePromoRateCode);
    return new ArrayList<>(roomRates.stream()
        .filter(roomRate -> !excludedRates.contains(roomRate.getRatePlanCode()))
        .toList());
  }

  @SneakyThrows
  @Override
  public AvailabilityByIdsResult getHotelAvailabilityByIds(
      AvailabilityByIdsSearchCriteria availabilitySearch) {
    log.debug("Entered getHotelAvailabilityByIds for hotelIds={}",
        availabilitySearch.getHotelIds());
    validateRequest(availabilitySearch);

    List<String> roomTypesToSearch;
    List<RoomSubstitutionRuleResponse> roomSubstitutions;

    if (availabilitySearch.getPmsRoomTypes() == null || availabilitySearch.getPmsRoomTypes().isEmpty()) {
      // Retrieve room substitutions for each requested <roomType, adults, children> combination
      roomSubstitutions = getRoomSubstitutions(
          availabilitySearch.getRoomTypes(), availabilitySearch.getAdults(),
          availabilitySearch.getChildren(), availabilitySearch.getCotsRequired(),
          availabilitySearch.getChannel());
      roomTypesToSearch = RulesAgentUtils.convertWbRoomTypesToPmsRoomTypes(roomSubstitutions);

    } else {
      roomSubstitutions = createRoomSubstitutions(availabilitySearch.getRoomTypes(), availabilitySearch.getAdults(),
          availabilitySearch.getChildren(), availabilitySearch.getCotsRequired(), availabilitySearch.getPmsRoomTypes());
      roomTypesToSearch = availabilitySearch.getPmsRoomTypes();
    }

    var availabilityByIdsSearchRequest =
        AvailabilityByIdsSearchRequest.builder()
            .hotelIds(availabilitySearch.getHotelIds())
            .arrivalDate(availabilitySearch.getArrivalDate())
            .departureDate(availabilitySearch.getDepartureDate())
            .numberOfRooms(1)
            .roomTypes(roomTypesToSearch)
            .adults(availabilitySearch.getAdults())
            .children(availabilitySearch.getChildren())
            .cotsRequired(availabilitySearch.getCotsRequired())
            .ratePlanCodes(availabilitySearch.getRatePlanCodes())
            .channel(availabilitySearch.getChannel())
            .subchannel(availabilitySearch.getSubchannel())
            .language(availabilitySearch.getLanguage())
            .globalCompanyId(availabilitySearch.getGlobalCompanyId())
            .negotiatedRateDisplaySets(availabilitySearch.getNegotiatedRateDisplaySets())
            .roomSubstitutions(roomSubstitutions)
            .build();

    var hotelAvailabilityResult =
        hotelAvailabilityOutPort.getHotelAvailabilityByIds(availabilityByIdsSearchRequest);

    for (AvailabilityResult availability : hotelAvailabilityResult.getHotelAvailability()) {
      if (availability.getRoomRates().isEmpty()) {
        availability.setAvailable(Boolean.FALSE);
      }
      addPriceBreakdownToHotelAvailabilityResult(availabilitySearch.getArrivalDate(),
          availabilitySearch.getDepartureDate(), availability);
      checkForCotsAvailability(availabilitySearch.getCotsRequired(), availability);
    }

    return hotelAvailabilityResult;
  }

  private List<RoomSubstitutionRuleResponse> createRoomSubstitutions(List<String> roomTypesToSearch,
                                                                     List<Integer> adults,
                                                                     List<Integer> children,
                                                                     List<Boolean> cotsRequiredList,
                                                                     List<String> pmsTypesToSearch) {
    List<RoomSubstitutionRuleResponse> roomSubstitutions = new ArrayList<>();

    var roomTypes = roomTypesToSearch.iterator();
    var adultsNumber = adults.iterator();
    var childrenNumber = children.iterator();
    var cotsRequired = cotsRequiredList.iterator();
    var pmsTypes = pmsTypesToSearch.iterator();

    while (roomTypes.hasNext() && adultsNumber.hasNext() && childrenNumber.hasNext()
        && cotsRequired.hasNext()) {

      var nextPmsRoomType = pmsTypes.next();
      RoomSubstitutionRequestDetails requestDetails = new RoomSubstitutionRequestDetails(adultsNumber.next(),
          childrenNumber.next(), roomTypes.next(), cotsRequired.next(), nextPmsRoomType);
      RoomSubstitution roomSubstitution = new RoomSubstitution(nextPmsRoomType, Boolean.FALSE,
          SING_SPECIAL_REQUEST, null, null);
      var roomSubstitutionRule = new RoomSubstitutionRuleResponse(requestDetails, new Date(),
          Arrays.asList(roomSubstitution));
      roomSubstitutions.add(roomSubstitutionRule);
    }
    return roomSubstitutions;
  }

  @Override
  public RoomPriceBreakdownResult getHotelMultiRoomsPriceBreakdown(String hotelId,
      RoomPriceBreakdownRequest roomPriceBreakdownRequest) {
    return RoomPriceBreakdownResult.builder()
        .priceBreakdown(hotelAvailabilityOutPort.getRatesInfo(
            mapAvailabilityRoomSearchCriteria(hotelId, roomPriceBreakdownRequest)))
        .build();
  }

  private List<AvailabilityRoomSearchCriteria> mapAvailabilityRoomSearchCriteria(String hotelId,
      RoomPriceBreakdownRequest roomPriceBreakdownRequest) {
    List<AvailabilityRoomSearchCriteria> searchCriterias = new ArrayList<>();
    for (int i = 0; i < roomPriceBreakdownRequest.getRoomTypes().size(); i++) {
      searchCriterias.add(AvailabilityRoomSearchCriteria.builder()
          .hotelId(hotelId)
          .arrivalDate(roomPriceBreakdownRequest.getArrivalDate())
          .departureDate(roomPriceBreakdownRequest.getDepartureDate())
          .roomType(roomPriceBreakdownRequest.getRoomTypes().get(i))
          .adults(roomPriceBreakdownRequest.getAdultsNo().get(i))
          .children(roomPriceBreakdownRequest.getChildrenNo().get(i))
          .ratePlanCode(roomPriceBreakdownRequest.getRatePlanCode())
          .build());
    }

    return searchCriterias;
  }

  private void validateRequest(AvailabilityByIdsSearchCriteria request) {
    if (request.getRoomTypes().size() != request.getAdults().size()
        || request.getAdults().size() != request.getChildren().size()
        || request.getChildren().size() != request.getCotsRequired().size()) {
      var exception = new ParameterMismatchException(ErrorCode.DIGITAL_INVALID_PARAMS_EXCEPTION,
          "RoomTypes, Adults, Children, CotsRequired parameters must have the same number of values.");
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    if (StringUtils.isEmpty(request.getGlobalCompanyId())
        && (request.getRatePlanCodes() == null || request.getRatePlanCodes().isEmpty())) {
      var exception = new ParameterMismatchException(
          ErrorCode.DIGITAL_NO_GOLBAL_COMPANY_ID_EXCEPTION,
          "No globalCompanyId nor ratePlanCodes provided");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  @Override
  public HotelInventoryRoomType getHotelRoomsInventory(
      HotelInventoryRequest hotelInventoryRequest) {
    return hotelAvailabilityOutPort.getHotelRoomsInventory(hotelInventoryRequest);
  }

  @Override
  public MultiAvailabilityResult getMultiHotelAvailability(
      MultiHotelAvailabilityRequest availabilityRequest) {

    var roomMatrix = new LinkedList<RoomMatrix>();
    for (int i = 0; i < availabilityRequest.getRoomTypes().size(); i++) {
      generateRoomMatrix(availabilityRequest, roomMatrix, i);
    }

    roomMatrix.forEach(roomType -> {
      roomType.setRoomsSubstitutionList(
          RulesAgentUtils.convertWbRoomTypesToPmsRoomTypes(getRoomSubstitutions(
              availabilityRequest.getRoomTypes(), availabilityRequest.getAdults(),
              availabilityRequest.getChildren(), availabilityRequest.getCotsRequired(),
              availabilityRequest.getChannel())));
    });

    var multiHotelAvailability =
        hotelAvailabilityOutPort.getMultiHotelAvailabilities(availabilityRequest, roomMatrix);
    multiHotelAvailability.stream().forEach(hotel -> {
      hotel.getRoomTypes().stream().forEach(roomType -> {
        if (roomType.getRoomRates().isEmpty()) {
          hotel.setAvailable(false);
        }
      });
    });
    return MultiAvailabilityResult.builder()
        .timestamp(Instant.now())
        .hotelAvailabilityResults(multiHotelAvailability)
        .build();
  }

  @Override
  public RateCodePricingResult getRateCodePricing(RateCodeCriteria rateCodeCriteria) {
    log.debug("Entered getRateCodePricing for hotelId={}", rateCodeCriteria.getHotelId());
    return hotelAvailabilityOutPort.getRateCodePricing(rateCodeCriteria);
  }

  @SneakyThrows
  @Override
  public ItemInventoryResponse getHotelItemsInventory(ItemInventoryRequest itemInventoryRequest) {
    ItemInventoryResponse itemInventoryResponse = hotelAvailabilityOutPort.getHotelItemsInventory(itemInventoryRequest);
    if (Objects.isNull(itemInventoryRequest.getItemCodes()) || itemInventoryRequest.getItemCodes().isEmpty()) {
      return itemInventoryResponse;
    }
    List<ItemInventory> itemsInventory = itemInventoryResponse.getItemsInventory().stream()
            .filter(item -> itemInventoryRequest.getItemCodes().contains(item.getCode())).toList();
    return ItemInventoryResponse.builder().itemsInventory(itemsInventory).build();
  }

  @Override
  public RestrictionsByDateRangeResult getRestrictionsByDateRange(
      RestrictionsByDateRangeSearchCriteria restrictionsByDateRangeSearchCriteria) {
    return hotelAvailabilityOutPort.getRestrictionsByDateRange(restrictionsByDateRangeSearchCriteria);
  }

  @Override
  public List<RestrictionsByDateRangeResult> getMultiHotelRestrictionsByDateRange(
      List<RestrictionsByDateRangeSearchCriteria> restrictionsByDateRangeSearchCriteriaList) {
    log.debug("Starting mlos restriction calls for {} hotels", restrictionsByDateRangeSearchCriteriaList.size());
    Instant startTime = Instant.now();

    List<CompletableFuture<RestrictionsByDateRangeResult>> futureList = restrictionsByDateRangeSearchCriteriaList
        .stream()
        .map(restrictionsByDateRangeSearchCriteria -> CompletableFuture.supplyAsync(
            () -> {
              Instant requestStartTime = Instant.now();
              RestrictionsByDateRangeResult result =
                  hotelAvailabilityOutPort.getRestrictionsByDateRange(restrictionsByDateRangeSearchCriteria);
              Instant requestEndTime = Instant.now();
              log.debug("Request for hotel {} took {} milliseconds and ended at {}.",
                  restrictionsByDateRangeSearchCriteria.getHotelId(),
                  Duration.between(requestStartTime, requestEndTime).toMillis(), requestEndTime);
              return result;
            }, restrictionsExecutorService))
        .toList();

    // Wait for all futures to complete
    List<RestrictionsByDateRangeResult> results = futureList.stream().map(CompletableFuture::join).toList();
    logExecutionTime(startTime, restrictionsByDateRangeSearchCriteriaList.size());
    return results;
  }

  private void logExecutionTime(Instant startTime, int hotelCount) {
    Instant endTime = Instant.now();
    log.info("Completed calls for mlos restrictions for {} hotels in {} milliseconds.",
        hotelCount, Duration.between(startTime, endTime).toMillis());
  }

  private void checkForCotsAvailability(List<Boolean> cotsRequired,
      AvailabilityResult availabilityResult) {

    var requestedNumberOfCots =
        cotsRequired != null ? cotsRequired.stream().filter(Boolean.TRUE::equals).count() : 0;

    if (requestedNumberOfCots > 0 && !CollectionUtils.isEmpty(availabilityResult.getRoomRates())) {
      var availableCots = hotelAvailabilityOutPort.getHotelItemsInventory(
              ItemInventoryRequest.builder()
                  .hotelId(availabilityResult.getHotelId())
                  .startDate(availabilityResult.getStartDate())
                  .endDate(availabilityResult.getEndDate())
                  .build()
          ).getItemsInventory().stream()
          .filter(itemInventory -> itemInventory.getName().contains(COT_NAME))
          .findFirst();

      var minimumNumberOfCotsAvailable =
          availableCots.map(itemInventory -> getLeastAvailableCotNumber(
              itemInventory.getInventories())).orElse(0);

      if (minimumNumberOfCotsAvailable >= requestedNumberOfCots) {
        setAvailableCotsForRooms(availabilityResult);
      }
    }
  }

  private void setAvailableCotsForRooms(AvailabilityResult availabilityResult) {
    availabilityResult.getRoomRates().stream()
        .map(AvailabilityRoomRate::getRoomTypes)
        .flatMap(Collection::stream)
        .filter(AvailabilityRoomType::getCotRequested)
        .map(AvailabilityRoomType::getRooms)
        .flatMap(Collection::stream)
        .forEach(availabilityRoom -> {
          if (availabilityRoom != null) {
            availabilityRoom.setCotAvailable(Boolean.TRUE);
          }
        });
  }

  private Integer getLeastAvailableCotNumber(List<InventoryAvailability> inventoryAvailabilities) {
    var list = inventoryAvailabilities.stream()
        .map(InventoryAvailability::getAvailable)
        .sorted().toList();
    return list.get(0);
  }

  private void addPriceBreakdownToHotelAvailabilityResult(
      String arrivalDate,
      String departureDate,
      AvailabilityResult hotelAvailabilityResult) {

    List<RoomPriceBreakdownWrapper> roomPriceBreakdownWrappers = RoomPriceBreakdownUtils
        .wrapAllRoomPriceBreakdownRequests(
            arrivalDate, departureDate, hotelAvailabilityResult);

    List<CompletableFuture> futures = new ArrayList<>();
    for (RoomPriceBreakdownWrapper roomPriceBreakdownWrapper : roomPriceBreakdownWrappers) {
      futures.add(CompletableFuture.runAsync(concurrentTracer.wrap(() ->
          addRoomPriceBreakdown(roomPriceBreakdownWrapper))));
    }
    futures.forEach(CompletableFuture::join);
  }

  private void addRoomPriceBreakdown(RoomPriceBreakdownWrapper roomPriceBreakdownWrapper) {
    var roomPriceBreakdown = hotelAvailabilityOutPort.getHotelRoomPriceBreakdown(
        createRoomAvailabilityCriteria(roomPriceBreakdownWrapper.getHotelId(),
            roomPriceBreakdownWrapper.getRatePlanCode(),
            roomPriceBreakdownWrapper.getRoom().getPmsRoomType(),
            roomPriceBreakdownWrapper.getArrivalDate(),
            roomPriceBreakdownWrapper.getDepartureDate(),
            roomPriceBreakdownWrapper.getAdults(), roomPriceBreakdownWrapper.getChildren()));

    log.debug("Price total net amount for rate plan code {} and room type {}: {}",
        roomPriceBreakdownWrapper.getRatePlanCode(),
        roomPriceBreakdownWrapper.getRoom().getPmsRoomType(),
        roomPriceBreakdown.getTotalNetAmount());

    // Preserve effectiveRate from existing price breakdown if available
    var existingPriceBreakdown = roomPriceBreakdownWrapper.getRoom().getRoomPriceBreakdown();
    if (existingPriceBreakdown != null && existingPriceBreakdown.getDailyPrices() != null) {
      mergeEffectiveRatesIntoPriceBreakdown(roomPriceBreakdown, existingPriceBreakdown);
    }

    roomPriceBreakdownWrapper.getRoom().setRoomPriceBreakdown(roomPriceBreakdown);
  }

  /**
   * Merges the effectiveRate from existing daily prices into the new price breakdown.
   * This preserves the effectiveRate information from the availability response while adding
   * net/gross prices from the rate info API call.
   *
   * @param newPriceBreakdown The new price breakdown from the rate info API
   * @param existingPriceBreakdown The existing price breakdown containing effectiveRate
   */
  private void mergeEffectiveRatesIntoPriceBreakdown(
      AvailabilityRoomPriceBreakdown newPriceBreakdown,
      AvailabilityRoomPriceBreakdown existingPriceBreakdown) {

    if (newPriceBreakdown.getDailyPrices() == null || existingPriceBreakdown.getDailyPrices() == null) {
      return;
    }

    // Create a map of date -> effectiveRate from existing breakdown
    var effectiveRatesByDate = existingPriceBreakdown.getDailyPrices().stream()
        .filter(dailyPrice -> dailyPrice.getEffectiveRate() != null)
        .collect(Collectors.toMap(
            AvailabilityDailyPrice::getDate,
            AvailabilityDailyPrice::getEffectiveRate,
            (existing, replacement) -> existing
        ));

    // Merge effectiveRate into new daily prices by matching date
    newPriceBreakdown.getDailyPrices().forEach(dailyPrice -> {
      if (dailyPrice.getDate() != null && effectiveRatesByDate.containsKey(dailyPrice.getDate())) {
        dailyPrice.setEffectiveRate(effectiveRatesByDate.get(dailyPrice.getDate()));
      }
    });
    newPriceBreakdown.setEffectiveRateAmount(newPriceBreakdown.getDailyPrices().stream()
        .filter(dp -> dp.getEffectiveRate() != null)
        .map(AvailabilityDailyPrice::getEffectiveRate)
        .reduce(BigDecimal.ZERO, BigDecimal::add));
  }

  private AvailabilityRoomSearchCriteria createRoomAvailabilityCriteria(String hotelId,
      String ratePlanCode,
      String roomType,
      String arrivalDate,
      String departureDate,
      int adults, int children) {

    return AvailabilityRoomSearchCriteria.builder()
        .hotelId(hotelId)
        .ratePlanCode(ratePlanCode)
        .roomType(roomType)
        .arrivalDate(arrivalDate)
        .departureDate(departureDate)
        .adults(adults)
        .children(children)
        .build();
  }

  private void generateRoomMatrix(MultiHotelAvailabilityRequest availabilityRequest,
      LinkedList<RoomMatrix> roomMatrix, int index) {
    roomMatrix.stream().filter(room -> availabilityRequest.getRoomTypes().get(index)
            .equals(room.getOriginalRoomType().get(0))).findFirst()
        .ifPresentOrElse(
            roomFound -> {
              roomFound.setQuantity(
                  roomFound.getQuantity() + availabilityRequest.getNumberOfRooms().get(index));
              roomFound.getOriginalRoomType().add(availabilityRequest.getRoomTypes().get(index));
              roomFound.getAdults().add(availabilityRequest.getAdults().get(index));
              roomFound.getChildren().add(availabilityRequest.getChildren().get(index));
              roomFound.getCotRequired().add(availabilityRequest.getCotsRequired().get(index));
            },
            () -> {
              var roomMatrixObject = RoomMatrix.builder()
                  .originalRoomType(new LinkedList<>(List.of(
                      availabilityRequest.getRoomTypes().get(index))))
                  .quantity(availabilityRequest.getNumberOfRooms().get(index))
                  .adults(new LinkedList<>(List.of(availabilityRequest.getAdults().get(index))))
                  .children(new LinkedList<>(List.of(availabilityRequest.getChildren().get(index))))
                  .cotRequired(new LinkedList<>(List.of(
                      availabilityRequest.getCotsRequired().get(index))))
                  .roomsSubstitutionList(new LinkedList<>())
                  .build();
              roomMatrix.add(roomMatrixObject);
            });
  }


  private void checkMealsIncluded(AvailabilitySearchCriteria availabilitySearch,
      AvailabilityResult hotelAvailabilityResult) {
    //only if the request comes from Distribution, fetch the meals included config
    if (DISTRIBUTION_CHANNEL.equalsIgnoreCase(availabilitySearch.getChannel())
        && DISTRIBUTION_SUBCHANNELS.contains(availabilitySearch.getSubchannel())) {
      hotelAvailabilityResult.getRoomRates().stream()
          .flatMap(roomRate -> roomRate.getRoomTypes().stream())
          .flatMap(roomType -> roomType.getRooms().stream())
          .forEach(room -> {
            MealsIncluded mealConfig = mealsConfig.getMealConfig(room.getRatePlanSet());
            room.setMealsIncluded(
                StringUtils.isEmpty(mealConfig.getMealName()) ? null : mealConfig);
          });
    }
  }

  @Override
  public AvailabilityByIdsResultV2 getHotelAvailabilityByIdsV2(
      AvailabilityByIdsSearchCriteriaV2 availabilityByIdsSearchCriteria) {

    var roomSubstitutionMap = addSubstitutedRoomTypes(availabilityByIdsSearchCriteria.getRooms(),
        availabilityByIdsSearchCriteria.getBookingChannel().getChannel());
    var response = hotelAvailabilityOutPort.getHotelAvailabilityByIdsV2(
        availabilityByIdsSearchCriteria);
    insertSpecialRequests(response, roomSubstitutionMap);
    return response;
  }

  private void insertSpecialRequests(AvailabilityByIdsResultV2 hotelAvailabilityResult,
      Map<String, List<RoomSubstitution>> roomSubstitutionMap) {

    if (Objects.nonNull(hotelAvailabilityResult)) {
      Optional.ofNullable(hotelAvailabilityResult.getHotelAvailability()).orElse(Collections.emptyList())
              .stream()
              .map(AvailabilityResultV2::getRoomStays)
              .flatMap(List::stream)
              .map(RoomStay::getRoomTypes)
              .flatMap(Collection::stream)
              .forEach(roomTypeV2 -> roomTypeV2.setSpecialRequests(
                      insertSpecialRequests(roomSubstitutionMap.getOrDefault(
                              buildRoomSubstitutionsKey(roomTypeV2.getAdults(), roomTypeV2.getChildren(),
                                      roomTypeV2.getTag()), Collections.emptyList()),
                              roomTypeV2.getRoomType())));
    }
  }

  private List<String> insertSpecialRequests(List<RoomSubstitution> roomSubstitutions,
      String roomType) {
    return roomSubstitutions.stream()
        .filter(roomSubstitution -> roomSubstitution.getType().equals(roomType))
        .findFirst()
        .map(roomSubstitution -> createSpecialRequestsList(roomSubstitution))
        .orElse(Collections.EMPTY_LIST);

  }

  private List<String> createSpecialRequestsList(RoomSubstitution roomSubstitution) {
    return Stream.of(roomSubstitution.getSpecialRequest(),
            roomSubstitution.getAccessibleSpecialRequest())
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public MultiAvailabilityResultV2 getMultiHotelAvailabilityV2(
      MultiHotelAvailabilityRequestV2 multiHotelAvailabilityRequest) {

    addSubstitutedRoomTypes(multiHotelAvailabilityRequest.getRooms(),
        multiHotelAvailabilityRequest.getBookingChannel().getChannel());
    var multiHotelAvailability =
        hotelAvailabilityOutPort.getMultiHotelAvailabilitiesV2(multiHotelAvailabilityRequest);
    return multiHotelAvailability;
  }

  private Map<String, List<RoomSubstitution>> addSubstitutedRoomTypes(List<Room> rooms,
      String channel) {
    var specialRequestMap = new HashMap<String, List<RoomSubstitution>>();
    List<CompletableFuture<Void>> roomSubstitutionCalls = rooms.stream()
        .map(r -> CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
          executeRoomSubstitutionCall(channel, r, specialRequestMap);
        }))).toList();
    roomSubstitutionCalls.forEach(CompletableFuture::join);
    return specialRequestMap;
  }

  private void executeRoomSubstitutionCall(
      String channel, Room room, Map<String, List<RoomSubstitution>> specialRequestMap) {

    if (room.getRoomTypes() != null
        && !room.getRoomTypes().isEmpty()
        && room.getRoomTypes().get(0) != null
        && !room.getRoomTypes().get(0).isBlank()) {
      setPmsRoomTypesSubstitutions(room, specialRequestMap);
    } else {
      setAllRoomTypesSubstitutions(channel, room, specialRequestMap);
    }
  }

  private void setPmsRoomTypesSubstitutions(Room room, Map<String, List<RoomSubstitution>> specialRequestMap) {
    var roomSubstitutionKey = buildRoomSubstitutionsKey(room.getAdults().toString(),
        room.getChildren().toString(), room.getTag());
    var roomSubstitutions = createRoomSubstitutions(List.of(room.getTag()),
        List.of(room.getAdults()),
        List.of(room.getChildren()),
        List.of(false),
        List.of(room.getRoomTypes().get(0)));
    setRoomSubstitutionsToMap(specialRequestMap, roomSubstitutionKey, roomSubstitutions);
  }

  private void setAllRoomTypesSubstitutions(
      String channel, Room room, Map<String, List<RoomSubstitution>> specialRequestMap) {
    var roomSubstitutions = getRoomSubstitutions(
        List.of(room.getTag()),
        List.of(room.getAdults()), List.of(room.getChildren()), List.of(false),
        channel);
    List<String> roomTypes = RulesAgentUtils.convertWbRoomTypesToPmsRoomTypes(roomSubstitutions);
    room.setRoomTypes(roomTypes);
    var requestDetails = roomSubstitutions.get(0).getRequestDetails();
    var roomSubstitutionKey = buildRoomSubstitutionsKey(requestDetails.getAdults().toString(),
        requestDetails.getChildren().toString(),
        requestDetails.getRoomType());
    setRoomSubstitutionsToMap(specialRequestMap, roomSubstitutionKey, roomSubstitutions);
  }

  private static void setRoomSubstitutionsToMap(
      Map<String, List<RoomSubstitution>> specialRequestMap, String roomSubstitutionKey,
      List<RoomSubstitutionRuleResponse> roomSubstitutions) {
    if (specialRequestMap.get(roomSubstitutionKey) == null) {
      specialRequestMap.putIfAbsent(roomSubstitutionKey, roomSubstitutions.get(0).getSubstitutionList());
    } else {
      Set<RoomSubstitution> roomSubstitutionSet = new HashSet<>(specialRequestMap.get(roomSubstitutionKey));
      roomSubstitutionSet.addAll(roomSubstitutions.get(0).getSubstitutionList());
      specialRequestMap.put(roomSubstitutionKey, new ArrayList<>(roomSubstitutionSet));
    }
  }

  private String buildRoomSubstitutionsKey(String adults, String children, String roomType) {
    var stringJoiner = new StringJoiner("-");
    stringJoiner.add(adults).add(children)
        .add(roomType);
    return stringJoiner.toString();

  }

  private List<RoomSubstitutionRuleResponse> getRoomSubstitutions(List<String> roomTypeList,
      List<Integer> adults,
      List<Integer> children,
      List<Boolean> cotsRequiredList, String channel) {

    List<RoomSubstitutionRuleResponse> roomSubstitutions = new ArrayList<>();

    var roomTypes = roomTypeList.iterator();
    var adultsNumber = adults.iterator();
    var childrenNumber = children.iterator();
    var cotsRequired = cotsRequiredList.iterator();

    while (roomTypes.hasNext() && adultsNumber.hasNext() && childrenNumber.hasNext()
        && cotsRequired.hasNext()) {

      var roomSubstitution = rulesAgentOutPort.getRoomSubstitution(roomTypes.next(),
          adultsNumber.next(), childrenNumber.next(), channel);
      roomSubstitution.getRequestDetails().setCotRequired(cotsRequired.next());
      roomSubstitutions.add(roomSubstitution);

    }
    return roomSubstitutions;

  }

  private boolean calculateLimitedAvailabilityFlag(
      List<StatisticsDateItem> hotelInventoryStatistics) {

    if (org.apache.commons.collections.CollectionUtils.isNotEmpty(hotelInventoryStatistics)) {
      var limitedAvailability =
          hotelInventoryStatistics.stream().filter(statisticSet -> {
            var totalNumberOfRooms =
                statisticSet.getInventoryItemList().stream()
                    .filter(inventory -> PHYSICAL_ROOMS.equals(inventory.getCode()))
                    .map(StatisticsInventoryItem::getValue).findFirst().orElse(BigDecimal.ZERO);
            if (totalNumberOfRooms.compareTo(BigDecimal.ZERO) > 0) {
              var availableRooms =
                  statisticSet.getInventoryItemList().stream().filter(inventory ->
                          AVAILABLE_ROOMS.equals(inventory.getCode()))
                      .map(StatisticsInventoryItem::getValue)
                      .findFirst().orElse(BigDecimal.ZERO);
              var availabilityPercent =
                  availableRooms.multiply(new BigDecimal(100))
                      .divide(totalNumberOfRooms, MathContext.DECIMAL128);
              return availabilityPercent.compareTo(
                  new BigDecimal(availabilityProperties.getMinimumAvailability())) <= 0;
            } else {
              return false;
            }

          }).findAny().orElse(null);

      return ObjectUtils.isNotEmpty(limitedAvailability);

    } else {
      return true;
    }

  }

  private static final List<String> PROMO_ALLOWED_CHANNELS = List.of(
      PI_CHANNEL,
      CCUI_CHANNEL,
      BUSINESS_BOOKER_CHANNEL
  );

  private boolean hasPromotionCode(AvailabilitySearchCriteria availabilitySearch) {
    return StringUtils.isNotBlank(availabilitySearch.getPromotionCode())
        && StringUtils.isNotBlank(availabilitySearch.getChannel())
        && PROMO_ALLOWED_CHANNELS.contains(availabilitySearch.getChannel());
  }

  private AvailabilityResult getPromotionRates(
      AvailabilitySearchCriteria availabilitySearch, AvailabilityRequest availabilityRequest) {
    var availabilityPromotionRequest = availabilityRequest.toBuilder()
        .promotionCode(availabilitySearch.getPromotionCode())
        .build();
    var availability = hotelAvailabilityOutPort.getHotelAvailability(
        availabilityPromotionRequest);
    var roomRates = availability.getRoomRates();
    availability.setRoomRates(roomRates != null ? roomRates : new ArrayList<>());
    return availability;
  }
}
