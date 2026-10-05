package uk.co.whitbread.domain.logic;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static uk.co.whitbread.domain.constants.HotelEntityConstants.BUSIFLEX;
import static uk.co.whitbread.domain.constants.HotelEntityConstants.FLEXRATE;
import static uk.co.whitbread.domain.constants.HotelEntityConstants.TW2S;
import static uk.co.whitbread.domain.constants.HotelEntityConstants.TWDS;
import static uk.co.whitbread.domain.constants.HotelEntityConstants.TWIN;
import static uk.co.whitbread.domain.model.availability.in.BookingChannel.BB_BOOKING_CHANNEL;
import static uk.co.whitbread.domain.model.availability.in.BookingChannel.CCUI_BOOKING_CHANNEL;
import static uk.co.whitbread.domain.model.availability.in.BookingChannel.DISTR_BOOKING_CHANNEL;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.constants.HotelEntityConstants;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.domain.exceptions.HotelAvailabilityBadReqException;
import uk.co.whitbread.domain.exceptions.RulesAgentBadRequestException;
import uk.co.whitbread.domain.model.availability.in.CorporateRate;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.domain.model.availability.in.HotelInventoryRequest;
import uk.co.whitbread.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.domain.model.availability.in.RoomPriceBreakdownRequest;
import uk.co.whitbread.domain.model.availability.out.HotelAvailability;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIds;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityResultV2;
import uk.co.whitbread.domain.model.availability.out.HotelInventoryRoomType;
import uk.co.whitbread.domain.model.availability.out.PriceInfo;
import uk.co.whitbread.domain.model.availability.out.RateCodePricingResult;
import uk.co.whitbread.domain.model.availability.out.Room;
import uk.co.whitbread.domain.model.availability.out.RoomPriceBreakdown;
import uk.co.whitbread.domain.model.availability.out.RoomRate;
import uk.co.whitbread.domain.model.availability.out.RoomRateV2;
import uk.co.whitbread.domain.model.availability.out.RoomTypeInfo;
import uk.co.whitbread.domain.model.cdh.CdhSearchCompaniesRequest;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.model.migrationstatus.in.HotelsMigrationStatusRequest;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelMigrationStatusResponse;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelsMigrationStatusResponse;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.domain.model.packages.out.Meal;
import uk.co.whitbread.domain.model.packages.out.MealsInfoResponse;
import uk.co.whitbread.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.domain.model.promotion.in.PromoKindRequest;
import uk.co.whitbread.domain.model.promotion.out.PromoKindResponse;
import uk.co.whitbread.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomOccupancyData;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomOccupancyResponse;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitution;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.domain.ports.primary.HotelAvailabilityInPort;
import uk.co.whitbread.domain.ports.primary.PackagesInPort;
import uk.co.whitbread.domain.ports.primary.RulesAgentInPort;
import uk.co.whitbread.domain.ports.secondary.AvailabilityCacheV1SearchOutPort;
import uk.co.whitbread.domain.ports.secondary.BasketServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.CdhAdapterOutPort;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.domain.ports.secondary.OnSaleFlagOutPort;
import uk.co.whitbread.domain.ports.secondary.PromotionOutPort;
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.infrastructure.config.CompanyProperties;
import uk.co.whitbread.infrastructure.config.PromotionProperties;
import uk.co.whitbread.infrastructure.config.RateSuppressionProperties;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.exception.NoAvailabilityException;
import uk.co.whitbread.infrastructure.rest.client.companyentity.CompanyEntityServiceOutPortImpl;
import uk.co.whitbread.infrastructure.rest.client.promotion.exceptions.InvalidPromotionException;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.PromoKind;
import uk.co.whitbread.promo.generated.models.promotion.PromoCodeStatus;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Slf4j
@RequiredArgsConstructor
public class HotelAvailabilityInPortImpl implements HotelAvailabilityInPort {

  private static final String NO_AVAILABILITY = "No availability";
  private static final String DATE_PATTERN = "yyyy-MM-dd";
  private static final boolean PRICE_FROM_OPERA = true;
  private static final boolean MULTIPLY_FOR_NO_NIGHTS = true;

  private final HotelAvailabilityOutPort availabilityOhipPort;
  private final AvailabilityCacheV1SearchOutPort availabilityCacheV1SearchOutPort;

  private final RulesAgentInPort rulesAgentInPort;

  private final OnSaleFlagOutPort onSaleFlagOutPortImpl;

  private final BasketServiceOutPort basketServiceOutPort;

  private volatile LocalDateTime suppressionRulesExpiryDate;

  private CopyOnWriteArrayList<String> cachedRateSuppressionList;

  private final CompanyProperties companyProperties;

  private final PackagesInPort packagesInPort;

  private final HotelAvailabilityCheckRules hotelAvailabilityCheckRules;

  private final ConcurrentTracer concurrentTracer;

  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  private final RulesAgentOutPort rulesAgentOutPort;

  private final ContentServiceOutPort contentServiceOutPort;

  private final PromotionProperties promotionProperties;

  private final MlosCommonLogic mlosCommonLogic;

  private final RateSuppressionProperties rateSuppressionProperties;

  private final SoftBundlesAndRatesLogic softBundlesAndRatesLogic;

  private final PromotionOutPort promotionOutPort;

  private final CdhAdapterOutPort cdhAdapterOutPort;

  private final CompanyEntityServiceOutPortImpl companyEntityServiceOutPort;

  public HotelAvailability getHotelAvailability(HotelAvailabilityRequest request) {
    log.debug("Entered getHotelAvailability with hotelAvailabilityRequest={}", request);
    HotelAvailability availability;
    boolean isEmptyRoomType = CollectionUtils.isEmpty(request.getRoomTypes())
            || request.getRoomTypes().stream().anyMatch(StringUtils::isBlank);

    List<List<String>> roomTypesVariants = new ArrayList<>();
    if (!isEmptyRoomType) {
      hotelAvailabilityCheckRules.validateRoomOccupancyRule(request);
    } else {
      roomTypesVariants = retrieveRoomTypesVariants(
              request, rulesAgentInPort.getMaxRoomOccupancyRule(request.getChannel()));
      // set first variant of roomTypes for each room in the initial request
      request = request.toBuilder().roomTypes(roomTypesVariants.get(0)).build();
    }

    final String clientPromoCode = request.getPromotionCode();
    final String resolvedPromoCode = applyUniquePromoIfNeeded(request);

    if (resolvedPromoCode != null) {
      request = request.toBuilder()
          .promotionCode(resolvedPromoCode)
          .build();
    }

    HotelsMigrationStatusResponse migrationStatusResponse =
            onSaleFlagOutPortImpl.getOnSaleFlag(
                    HotelsMigrationStatusRequest.builder()
                            .hotelIds(Collections.singletonList(request.getHotelId()))
                            .build());
    var hotelToCheckStatus = migrationStatusResponse.getMigrationStatusList().get(0);
    var channel = request.getChannel();
    if (hotelToCheckStatus.getOnSale().equals(true) && hotelAvailabilityCheckRules.fulfillHubRules(request)) {
      boolean empRateCodeAvailable = Optional.ofNullable(request.getRatePlanCodes()).orElse(Collections.emptyList())
              .stream().filter(Objects::nonNull)
              .anyMatch(ratePlanCode ->
                      StringUtils.equalsAnyIgnoreCase(HotelEntityConstants.EMPLOYEE_RATE_PLAN_CODE, ratePlanCode));
      if (empRateCodeAvailable) {
        request.setCompanyId(companyProperties.getCompanyId());
      }
      availability = processAvailabilityRequest(
              request, isEmptyRoomType, roomTypesVariants, empRateCodeAvailable, channel);
      List<String> companySuppressedRates = applyCompanyRateSuppression(availability, request.getCompanyId(), channel);
      applyRateSuppressionRules(availability, getRateSuppressionList());
      if (!CCUI_BOOKING_CHANNEL.equals(channel)) {
        applyFlexRateExclusionWhenBusiFlex(availability, companySuppressedRates);
      }
      applyExclusion(availability);
      fillAvailabilityResponse(request, availability);
      applySoftBundlesIfEligible(request, availability);

    } else {
      availability = buildEmptyRatesResponse(
              request.getHotelId(), request.getArrivalDate(), request.getDepartureDate());
    }
    if (mlosCommonLogic.isMlosEnabled(channel)) {
      boolean hasMlosRestriction = mlosCommonLogic.hasMlosRestriction(request.getHotelId(),
              request.getArrivalDate(), request.getDepartureDate(), availability.isAvailable(),
              availability.getRoomRates());
      availability.setMlos(hasMlosRestriction);
    }

    restoreClientPromoIfNeeded(availability, resolvedPromoCode, clientPromoCode);

    return availability;
  }

  private String applyUniquePromoIfNeeded(HotelAvailabilityRequest request) {

    if (request.getPromoKind() != PromoKind.UNIQUE
            || StringUtils.isBlank(request.getPromotionCode())) {
      return null;
    }

    PromoKindRequest promoKindRequest = PromoKindRequest.builder()
            .promoCode(request.getPromotionCode())
            .country(request.getCountry())
            .channel(request.getChannel())
            .subChannel(request.getSubchannel())
            .build();

    return resolveUniquePromo(promoKindRequest);
  }

  private void restoreClientPromoIfNeeded(HotelAvailability availability,
      String resolvedPromoCode, String clientPromoCode) {
    if (resolvedPromoCode == null) {
      return;
    }

    availability.getRoomRates().forEach(rate -> {
      if (resolvedPromoCode.equals(rate.getPromotionCode())) {
        rate.setPromotionCode(clientPromoCode);
      }
    });
  }

  private String resolveUniquePromo(PromoKindRequest request) {
    PromoKindResponse promoResponse = promotionOutPort.getPromoKind(request);
    String promoCode = request.getPromoCode();
    if (promoResponse == null) {
      log.warn("No promo response received for promoCode={}", promoCode);
      return promoCode;
    }

    PromoCodeStatus status = promoResponse.getUniquePromoCodeStatus();

    if (status == null) {
      return null;
    }
    
    if (status == PromoCodeStatus.REDEEMED) {
      throwAndLogPromotionException(
          ErrorCode.DIGITAL_PROMOTION_ALREADY_USED_EXCEPTION,
          String.format("The promotion code '%s' has already been used.", promoCode)
      );
    }

    if (status == PromoCodeStatus.EXPIRED) {
      throwAndLogPromotionException(
          ErrorCode.DIGITAL_PROMOTION_EXPIRED_EXCEPTION,
          String.format("The promotion code '%s' has expired.", promoCode)
      );
    }

    return Optional.ofNullable(promoResponse.getOperaPromoCode())
        .filter(StringUtils::isNotBlank)
        .orElseGet(() -> {
          log.warn(
              "UNIQUE promo kind but no opera promo code returned for promoCode={}",
                  promoCode
          );
          return promoCode;
        });
  }

  private void throwAndLogPromotionException(ErrorCode errorCode, String message) {
    InvalidPromotionException exception =
        new InvalidPromotionException(errorCode, message);
    ExceptionLogger.log(log, exception);
    throw exception;
  }

  private void applySoftBundlesIfEligible(HotelAvailabilityRequest request, HotelAvailability availability) {
    if (Objects.nonNull(request.getSoftBundle())
            && request.getAdultsNumber().size() == 1
            && request.getChildrenNumber().stream().allMatch(c -> c != null && c == 0)) {
      applySoftBundlesAndRates(request, availability);
    }
  }

  private void applySoftBundlesAndRates(HotelAvailabilityRequest request,
      HotelAvailability response) {
    // Get Opera packages for the hotel and date range
    PackagesResponse packagesResponse = packagesInPort.getPackages(buildPackagesRequest(request));
    if (packagesResponse == null) {
      log.info("No packages response from Opera for hotelId: {}", request.getHotelId());
      return;
    }

    // Get meals AEM
    MealsInfoResponse upsellItemsAndSoftBundles = packagesInPort.getUpsellItemsAndSoftBundles(
        request.getHotelId(), request.getCountry().toLowerCase(),
        request.getLanguage().toLowerCase());

    if (upsellItemsAndSoftBundles == null
            || upsellItemsAndSoftBundles.getSoftBundles() == null
            || upsellItemsAndSoftBundles.getSoftBundles().isEmpty()) {
      log.info("No soft bundles found in AEM for hotelId: {}", request.getHotelId());
      return;
    }

    var ancillariesContent = contentServiceOutPort.getExtrasLabels(
        request.getCountry().toLowerCase(),
        request.getLanguage().toLowerCase());

    // Update availability response with soft bundle info
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(request, response,
            packagesResponse, upsellItemsAndSoftBundles, ancillariesContent);

  }

  private static PackagesRequest buildPackagesRequest(HotelAvailabilityRequest request) {
    return PackagesRequest.builder()
        .hotelId(request.getHotelId())
        .startDate(request.getArrivalDate())
        .endDate(request.getDepartureDate())
        .nightsNumber(Math.toIntExact(ChronoUnit.DAYS.between(
            LocalDate.parse(request.getArrivalDate()),
            LocalDate.parse(request.getDepartureDate()))))
        .adultsNumber(request.getAdultsNumber().get(0))
        .childrenNumber(request.getChildrenNumber().get(0))
        .channel(request.getChannel())
        .country(request.getCountry().toLowerCase())
        .language(request.getLanguage().toLowerCase())
        .build();
  }

  private HotelAvailability processAvailabilityRequest(
          HotelAvailabilityRequest request, boolean isEmptyRoomType,
          List<List<String>> roomTypesVariants, boolean empRateCodeAvailable, String channel) {
    HotelAvailability availability;
    if (!isEmptyRoomType) {
      availability = availabilityOhipPort.getHotelAvailability(request);

      // Availability call is made on amend booking
      if (StringUtils.isNotBlank(request.getOriginalBasketReference())) {
        CityTaxUtils.setPriceWithoutCityTaxForAmendIfApplicable(availability, request.getOriginalBasketReference(),
                contentServiceOutPort,  availabilityOhipPort, basketServiceOutPort, unleashWrapper);
      } else {
        CityTaxUtils.setPriceWithoutCityTaxIfApplicable(availability, channel,
                request.getArrivalDate(), contentServiceOutPort, unleashWrapper);
      }
      updateEmployeeRateInFlexRate(empRateCodeAvailable, availability);
      updateTwinRoomAvailability(request, availability);
    } else {
      availability = processMultipleRoomTypeVariants(request, roomTypesVariants, empRateCodeAvailable, channel);
    }
    mapCityTaxAmount(availability, AvailabilitiesResponseUtils.getNumberOfNights(request));
    return availability;
  }

  private void mapCityTaxAmount(HotelAvailability availability, int numberOfNights) {
    if (Objects.isNull(availability) || Objects.isNull(availability.getRoomRates())) {
      return;
    }
    availability.getRoomRates().stream()
        .filter(Objects::nonNull)
        .forEach(roomRate -> processRoomRate(roomRate, numberOfNights));
  }

  private void processRoomRate(RoomRate roomRate, int numberOfNights) {
    Optional.ofNullable(roomRate.getRoomTypes())
        .orElse(Collections.emptyList())
        .stream()
        .filter(Objects::nonNull)
        .forEach(roomTypeInfo -> processRoomTypeInfo(roomRate, roomTypeInfo, numberOfNights));
  }

  private void processRoomTypeInfo(RoomRate roomRate, RoomTypeInfo roomTypeInfo, int numberOfNights) {
    Optional.ofNullable(roomTypeInfo.getRooms())
        .orElse(Collections.emptyList())
        .stream()
        .filter(Objects::nonNull)
        .forEach(room -> processRoom(roomRate, roomTypeInfo, room, numberOfNights));
  }

  private void processRoom(RoomRate roomRate, RoomTypeInfo roomTypeInfo, Room room, int numberOfNights) {
    RoomPriceBreakdown breakdown = room.getRoomPriceBreakdown();
    if (Objects.isNull(breakdown)) {
      return;
    }
    initializeRoomNetAmounts(breakdown);

    if (TWIN.equals(roomTypeInfo.getRoomType()) && roomRate.isTwinRoomTypeAvailability()) {
      processTwinRoomBreakdown(breakdown, numberOfNights);
    } else {
      processDefaultBreakdown(breakdown);
    }
    room.setRoomPriceBreakdown(breakdown);
  }

  private void processTwinRoomBreakdown(RoomPriceBreakdown breakdown, int numberOfNights) {
    breakdown.setTotalCityTaxAmount(getCityTaxAmount(breakdown, numberOfNights));
    Optional.ofNullable(breakdown.getDailyPrices())
        .orElse(Collections.emptyList())
        .forEach(dailyPrice -> {
          BigDecimal effectiveRate = Optional.ofNullable(dailyPrice.getEffectiveRate()).orElse(BigDecimal.ZERO);
          BigDecimal packageAmt = Optional.ofNullable(breakdown.getPackageAmount()).orElse(BigDecimal.ZERO);
          dailyPrice.setEffectiveRate(effectiveRate.add(packageAmt));
        });
    BigDecimal totalEffectiveRate = Optional.ofNullable(breakdown.getDailyPrices())
        .orElse(Collections.emptyList())
        .stream()
        .map(dailyPrice -> Optional.ofNullable(dailyPrice.getEffectiveRate()).orElse(BigDecimal.ZERO))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    breakdown.setEffectiveRateAmount(totalEffectiveRate);
  }

  private void processDefaultBreakdown(RoomPriceBreakdown breakdown) {
    BigDecimal totalNetAmount = Optional.ofNullable(breakdown.getTotalNetAmount()).orElse(BigDecimal.ZERO);
    BigDecimal totalEffectiveRateAmount = Optional.ofNullable(breakdown.getEffectiveRateAmount())
        .orElse(BigDecimal.ZERO);
    BigDecimal totalCityTaxAmount = totalNetAmount.subtract(totalEffectiveRateAmount);
    breakdown.setTotalCityTaxAmount(totalCityTaxAmount);
  }

  private BigDecimal getCityTaxAmount(RoomPriceBreakdown breakdown, int numberOfNights) {
    BigDecimal packageAmount = Optional.ofNullable(breakdown.getPackageAmount())
        .orElse(BigDecimal.ZERO)
        .multiply(BigDecimal.valueOf(numberOfNights));
    BigDecimal totalNetAmount = Optional.ofNullable(breakdown.getTotalNetAmount()).orElse(BigDecimal.ZERO);
    return (totalNetAmount.subtract(breakdown.getEffectiveRateAmount())).subtract(packageAmount);
  }

  private void initializeRoomNetAmounts(RoomPriceBreakdown breakdown) {
    if (breakdown.getTotalRoomNetAmount() == null) {
      breakdown.setTotalRoomNetAmount(breakdown.getTotalNetAmount());
    }

    Optional.ofNullable(breakdown.getDailyPrices())
        .orElse(Collections.emptyList())
        .forEach(dailyPrice -> {
          if (dailyPrice.getRoomNetPrice() == null) {
            dailyPrice.setRoomNetPrice(dailyPrice.getNetPrice());
          }
        });
  }

  private HotelAvailability processMultipleRoomTypeVariants(
          HotelAvailabilityRequest request,
          List<List<String>> roomTypesVariants, boolean empRateCodeAvailable, String channel) {
    List<HotelAvailability> availabilityListForAllRooms = new ArrayList<>();

    for (int i = 0; i < roomTypesVariants.size(); i++) {
      request = request.toBuilder().roomTypes(roomTypesVariants.get(i)).build();
      HotelAvailability availability = availabilityOhipPort.getHotelAvailability(request);
      CityTaxUtils.setPriceWithoutCityTaxIfApplicable(availability, channel,
              request.getArrivalDate(), contentServiceOutPort, unleashWrapper);

      updateEmployeeRateInFlexRate(empRateCodeAvailable, availability);
      updateTwinRoomAvailability(request, availability);
      availabilityListForAllRooms.add(availability);
    }

    return aggregateAvailabilityResponses(availabilityListForAllRooms);
  }

  /**
   * Aggregates multiple HotelAvailability objects into a single unified HotelAvailability response.
   * - RoomRates are grouped by ratePlanCode
   * - Within each RoomRate, roomNumber is assigned based on their position on RoomTypeList
   * - RoomTypeInfo objects with the same roomNumber are merged into one
   * - All Rooms belonging to the same roomNumber are appended together, avoiding duplicates
   * - roomType at RoomTypeInfo level remains null; each Room has its own roomType set
   * - twinRoomTypeAvailability is true if any input has it enabled
   */
  public HotelAvailability aggregateAvailabilityResponses(List<HotelAvailability> availabilities) {
    if (availabilities == null || availabilities.isEmpty()) {
      return null;
    }

    HotelAvailability first = availabilities.get(0);
    HotelAvailability result = HotelAvailability.builder()
            .hotelId(first.getHotelId())
            .startDate(first.getStartDate())
            .endDate(first.getEndDate())
            .available(availabilities.stream().anyMatch(HotelAvailability::isAvailable))
            .limitedAvailability(availabilities.stream().anyMatch(HotelAvailability::isLimitedAvailability))
            .mlos(availabilities.stream().anyMatch(HotelAvailability::isMlos))
            .timestamp(Instant.now())
            .build();

    Map<String, List<RoomRate>> groupedRates = availabilities.stream()
            .flatMap(hotelAvailability -> hotelAvailability.getRoomRates().stream())
            .collect(Collectors.groupingBy(RoomRate::getRatePlanCode));

    List<RoomRate> mergedRates = groupedRates.entrySet().stream()
            .map(entry -> {
              String ratePlanCode = entry.getKey();
              List<RoomRate> rates = entry.getValue();

              Map<Integer, RoomTypeInfo> roomNumberMap = rates.stream()
                      .flatMap(this::propagateRoomTypes)
                      .collect(Collectors.toMap(
                              RoomTypeInfo::getRoomNumber,
                              roomType -> roomType,
                              this::mergeRoomTypeInfo
                      ));
              return RoomRate.builder()
                      .ratePlanCode(ratePlanCode)
                      .roomTypes(new ArrayList<>(roomNumberMap.values()))
                      .twinRoomTypeAvailability(
                              rates.stream().anyMatch(RoomRate::isTwinRoomTypeAvailability)
                      )
                      .build();
            })
            .toList();
    result.setRoomRates(mergedRates);
    return result;
  }

  /**
   * Assigns sequential room numbers in each Room object.
   * Propagates the parent roomType to all child rooms
   * Clears the roomType at the RoomTypeInfo level to ensure consistency
   */
  private Stream<RoomTypeInfo> propagateRoomTypes(RoomRate rate) {
    List<RoomTypeInfo> types = rate.getRoomTypes();
    return IntStream.range(0, types.size())
            .mapToObj(i -> {
              RoomTypeInfo roomTypeInfo = types.get(i);
              roomTypeInfo.setRoomNumber(i + 1);
              if (roomTypeInfo.getRoomType() != null) {
                roomTypeInfo.getRooms().forEach(room -> {
                  if (room.getRoomType() == null) {
                    room.setRoomType(roomTypeInfo.getRoomType());
                  }
                });
              }
              roomTypeInfo.setRoomType("");
              return roomTypeInfo;
            });
  }

  /**
   * Merges two RoomTypeInfo objects that share the same room number.
   * The method combines the list of rooms from the incoming RoomTypeInfo
   * into the existing one, ensuring no duplicate rooms are added.
   * A room is considered duplicate if both its roomType and pmsRoomType
   * match an already existing room.
   */
  private RoomTypeInfo mergeRoomTypeInfo(RoomTypeInfo existing, RoomTypeInfo incoming) {
    List<Room> mergedRooms = new ArrayList<>(existing.getRooms());
    for (Room incomingRoom : incoming.getRooms()) {
      boolean alreadyExists = mergedRooms.stream()
              .anyMatch(existingRoom -> Objects.equals(existingRoom.getRoomType(), incomingRoom.getRoomType())
                      && Objects.equals(existingRoom.getPmsRoomType(), incomingRoom.getPmsRoomType()));
      if (!alreadyExists) {
        mergedRooms.add(incomingRoom);
      }
    }
    existing.setRooms(mergedRooms);
    return existing;
  }

  private void fillAvailabilityResponse(HotelAvailabilityRequest request,
      HotelAvailability availability) {
    availability.setStartDate(request.getArrivalDate());
    availability.setEndDate(request.getDepartureDate());
    var roomRates = availability.getRoomRates();
    availability.setAvailable(availability.isAvailable() && roomRates != null && !roomRates.isEmpty());
  }

  @Override
  public HotelAvailabilityByIds getHotelAvailabilityByIds(
      HotelAvailabilityByIdsRequest request) {
    log.debug("Entered getHotelAvailabilityByIds with hotelAvailabilityByIdsRequest={}", request);
    HotelAvailabilityByIds hotelAvailabilityByIds;

    hotelAvailabilityCheckRules.validateRoomOccupancyRule(request);

    HotelsMigrationStatusResponse migrationStatusResponse =
            onSaleFlagOutPortImpl.getOnSaleFlag(
                    HotelsMigrationStatusRequest.builder().hotelIds(request.getHotelIds()).build());

    List<String> invalidHotelIds = migrationStatusResponse.getMigrationStatusList().stream()
            .filter(response -> !response.getOnSale()).map(HotelMigrationStatusResponse::getHotelId)
            .toList();
    request.getHotelIds().removeIf(hotel -> invalidHotelIds.stream()
            .anyMatch(invalidHotel -> invalidHotel.equalsIgnoreCase(hotel)));

    if (!request.getHotelIds().isEmpty()) {

      boolean isPriceFromOpera = true;
      if (DISTR_BOOKING_CHANNEL.equals(request.getChannel()) && StringUtils.isEmpty(request.getGlobalCompanyId())
              && CollectionUtils.isEmpty(request.getRatePlanCodes())) {

        hotelAvailabilityByIds = availabilityCacheV1SearchOutPort
                .getHotelAvailabilityByIdsFromAvCache(request,
                        getRoomSubstitutions(request.getRoomTypes(), request.getAdultsNumber(),
                                request.getChildrenNumber(), request.getCotsRequired(),
                                DISTR_BOOKING_CHANNEL));

        log.debug("Availabilities result: {}", hotelAvailabilityByIds);

        if (!request.isVatNotRequired()) {
          addRoomPriceBreakdown(request.getArrivalDate(),
                  request.getDepartureDate(), hotelAvailabilityByIds);
        } else {
          isPriceFromOpera = false;
        }

      } else {
        hotelAvailabilityByIds = availabilityOhipPort.getHotelAvailabilityByIds(request);
        setGlobalCompanyIds(request.getGlobalCompanyId(), hotelAvailabilityByIds);
      }

      hotelAvailabilityByIds.getHotelAvailability().forEach(
          hotelAvailability ->
              CityTaxUtils.setPriceWithoutCityTaxIfApplicable(hotelAvailability, request.getChannel(),
              request.getArrivalDate(), contentServiceOutPort, unleashWrapper)
      );

      if (hotelAvailabilityByIds.getHotelAvailability() != null
          && !hotelAvailabilityByIds.getHotelAvailability().isEmpty()) {
        OccupancySupplementDistributionUtils.applyOccupancySupplement(request, hotelAvailabilityByIds,
            unleashWrapper, rulesAgentOutPort, isPriceFromOpera);
      }

      hotelAvailabilityByIds.getHotelAvailability().forEach(
              availability -> applyRateSuppressionRules(availability, getRateSuppressionList()));

    } else {
      hotelAvailabilityByIds =
              HotelAvailabilityByIds.builder().hotelAvailability(new ArrayList<>()).build();
    }
    List<HotelAvailability> emptyResponses = new ArrayList<>();
    buildEmptyResponseForNoAvailability(request, hotelAvailabilityByIds, emptyResponses, invalidHotelIds);
    hotelAvailabilityByIds.setHotelAvailability(Stream.concat(hotelAvailabilityByIds.getHotelAvailability().stream(),
            emptyResponses.stream()).toList());

    return hotelAvailabilityByIds;
  }

  private void buildEmptyResponseForNoAvailability(HotelAvailabilityByIdsRequest request,
                   HotelAvailabilityByIds hotelAvailabilityByIds, List<HotelAvailability> emptyResponses,
                   List<String> invalidHotelIds) {
    for (String hotelId : invalidHotelIds) {
      HotelAvailability hotelAvailabilityEmpty = buildEmptyRatesResponse(hotelId,
          request.getArrivalDate(), request.getDepartureDate());
      emptyResponses.add(hotelAvailabilityEmpty);
    }
    for (String hotelId : request.getHotelIds()) {
      if (Objects.isNull(hotelAvailabilityByIds)
          || CollectionUtils.isEmpty(hotelAvailabilityByIds.getHotelAvailability())) {
        HotelAvailability hotelAvailabilityEmpty = buildEmptyRatesResponse(hotelId,
            request.getArrivalDate(), request.getDepartureDate());
        emptyResponses.add(hotelAvailabilityEmpty);
      } else {
        boolean isHotelAvailable = hotelAvailabilityByIds.getHotelAvailability().stream()
            .anyMatch(avail -> org.apache.commons.lang3.StringUtils.equalsIgnoreCase(hotelId, avail.getHotelId()));
        if (!isHotelAvailable) {
          HotelAvailability hotelAvailabilityEmpty = buildEmptyRatesResponse(hotelId,
              request.getArrivalDate(), request.getDepartureDate());
          emptyResponses.add(hotelAvailabilityEmpty);
        }
      }
    }
  }

  private static void setGlobalCompanyIds(String globalCompanyID,
                                          HotelAvailabilityByIds hotelAvailabilityByIds) {
    ofNullable(hotelAvailabilityByIds)
        .map(HotelAvailabilityByIds::getHotelAvailability)
        .orElse(emptyList()).stream()
        .flatMap(avail -> ofNullable(avail.getRoomRates())
            .orElse(emptyList()).stream())
        .forEach(roomRate -> roomRate.setGlobalCompanyId(globalCompanyID));
  }

  private void updateTwinRoomAvailability(HotelAvailabilityRequest request, HotelAvailability hotelAvailability) {

    boolean isTwinRoomType = request.getRoomTypes().stream()
        .anyMatch(roomType -> org.apache.commons.lang3.StringUtils.equalsIgnoreCase(TWIN, roomType));

    if (isTwinRoomType) {
      checkTwinRoomTypeAvailability(request, hotelAvailability);
    }
  }

  private void checkTwinRoomTypeAvailability(HotelAvailabilityRequest request, HotelAvailability hotelAvailability) {

    LocalDate startDate = Optional.ofNullable(request.getArrivalDate())
        .map(LocalDate::parse).orElse(null);
    LocalDate endDate = Optional.ofNullable(request.getDepartureDate())
        .map(LocalDate::parse).orElse(null);
    long nightsNumber = (Objects.nonNull(startDate) && Objects.nonNull(endDate))
        ? ChronoUnit.DAYS.between(startDate, endDate) : 0;

    var packages = packagesInPort.getPackages(PackagesRequest.builder()
        .hotelId(request.getHotelId())
        .startDate(request.getArrivalDate())
        .endDate(request.getDepartureDate())
        .adultsNumber(request.getAdultsNumber().get(0))
        .childrenNumber(request.getChildrenNumber().get(0))
        .nightsNumber(Math.toIntExact(nightsNumber))
        .build());

    hotelAvailability.getRoomRates().stream()
        .filter(Objects::nonNull)
        .forEach(roomRate -> {
          Optional.ofNullable(roomRate.getRoomTypes()).orElse(Collections.emptyList())
              .stream()
              .filter(Objects::nonNull)
              .forEach(roomTypeInfo -> {
                List<Room> rooms = Optional.ofNullable(roomTypeInfo.getRooms()).orElse(Collections.emptyList());
                Room tw2sRoom = rooms.stream()
                    .filter(Objects::nonNull)
                    .filter(r -> Optional.ofNullable(r.getSpecialRequests())
                        .orElse(Collections.emptyList())
                        .stream().anyMatch(TW2S::equalsIgnoreCase))
                    .findFirst().orElse(null);
                Room twdsRoom = rooms.stream()
                    .filter(Objects::nonNull)
                    .filter(r -> Optional.ofNullable(r.getSpecialRequests())
                        .orElse(Collections.emptyList())
                        .stream().anyMatch(TWDS::equalsIgnoreCase))
                    .findFirst().orElse(null);

                List<Room> filteredRooms = new ArrayList<>();
                if (Objects.nonNull(tw2sRoom) && Objects.nonNull(twdsRoom)) {
                  filteredRooms.add(tw2sRoom);
                  filteredRooms.add(twdsRoom);
                  roomRate.setTwinRoomTypeAvailability(true);
                } else if (!rooms.isEmpty()) {
                  filteredRooms.add(rooms.get(0));
                }
                roomTypeInfo.setRooms(filteredRooms);
              });
          List<RoomSubstitution> substitutionList = Optional.ofNullable(hotelAvailability.getSubstitutionList())
              .orElse(Collections.emptyList());
          updateTwinRoomPackage(roomRate, substitutionList, packages, nightsNumber);
        });
  }

  private void updateTwinRoomPackage(RoomRate roomRate, List<RoomSubstitution> substitutionList,
                                     PackagesResponse packages, long nightsNumber) {

    Map<String, String> codePackageMap = substitutionList.stream()
        .distinct()
        .filter(substitution -> org.apache.commons.lang3.StringUtils.isNotEmpty(substitution.getCodePackage()))
        .collect(Collectors.toMap(
            RoomSubstitution::getType,
            RoomSubstitution::getCodePackage,
            (existing, replacement) -> {
              // in case of conflict, if the values (codePackage) are the same, we can ignore it and return either
              // if the values are different, we throw an exception
              if (!existing.equalsIgnoreCase(replacement)) {
                throw new IllegalStateException(
                    String.format("Duplicate key conflict (attempted merging values %s and %s)", existing,
                        replacement));
              }
              return existing;
            }));

    Optional.ofNullable(roomRate.getRoomTypes()).orElse(Collections.emptyList())
        .stream()
        .filter(roomTypeInfo -> org.apache.commons.lang3.StringUtils.equalsIgnoreCase(TWIN,
            roomTypeInfo.getRoomType()))
        .forEach(roomType -> Optional.ofNullable(roomType.getRooms()).orElse(Collections.emptyList())
            .forEach(room -> {
              String packageCode = codePackageMap.get(room.getPmsRoomType());
              if (!org.apache.commons.lang3.StringUtils.isBlank(packageCode)) {
                BigDecimal additionalPackageAmount = Optional.ofNullable(packages.getPackages()
                        .getMeals()).orElse(Collections.emptyList())
                    .stream()
                    .filter(meal -> meal.getId().equals(packageCode))
                    .map(Meal::getPrice)
                    .findFirst().orElse(null);
                if (Objects.nonNull(additionalPackageAmount)) {
                  room.getRoomPriceBreakdown().setPackageCode(packageCode);
                  room.getRoomPriceBreakdown().setPackageAmount(additionalPackageAmount);
                  room.getRoomPriceBreakdown().setTotalNetAmount(room.getRoomPriceBreakdown()
                      .getTotalNetAmount().add(additionalPackageAmount.multiply(BigDecimal.valueOf(nightsNumber))));
                  BigDecimal baseRate = room.getRoomPriceBreakdown().getBaseRateAmount();
                  if (baseRate != null) {
                    room.getRoomPriceBreakdown().setBaseRateAmount(
                            baseRate.add(additionalPackageAmount.multiply(BigDecimal.valueOf(nightsNumber)))
                    );
                  }
                  Optional.ofNullable(room.getRoomPriceBreakdown().getDailyPrices())
                      .orElse(Collections.emptyList())
                      .forEach(dailyPrice -> dailyPrice.setNetPrice(dailyPrice.getNetPrice()
                          .add(additionalPackageAmount)));
                }
              }
            }));
  }

  private void updateEmployeeRateInFlexRate(boolean empRateCodeAvailable, HotelAvailability hotelAvailability) {
    if (empRateCodeAvailable) {
      RoomRate empRoomRate = Optional.ofNullable(hotelAvailability.getRoomRates()).orElse(Collections.emptyList())
          .stream()
          .filter(roomRate ->
              org.apache.commons.lang3.StringUtils.equalsIgnoreCase(
                  roomRate.getRatePlanCode(), HotelEntityConstants.EMPLOYEE_RATE_CODE))
          .findFirst().orElse(null);
      hotelAvailability.setRoomRates(Optional.ofNullable(hotelAvailability.getRoomRates())
          .orElse(Collections.emptyList())
          .stream()
          .filter(roomRate ->
              !org.apache.commons.lang3.StringUtils.equalsIgnoreCase(
                  roomRate.getRatePlanCode(), HotelEntityConstants.EMPLOYEE_RATE_CODE))
          .collect(Collectors.toList()));
      Optional.ofNullable(hotelAvailability.getRoomRates()).orElse(Collections.emptyList())
          .forEach(roomRate -> {
            if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(
                roomRate.getRatePlanCode(), HotelEntityConstants.FLEXRATE)
                && Objects.nonNull(empRoomRate)) {
              roomRate.setRateDisplaySet(empRoomRate.getRateDisplaySet());
              roomRate.setRoomTypes(empRoomRate.getRoomTypes());
              roomRate.setCellCode(HotelEntityConstants.EMPLOYEE_RATE_PLAN_CODE);
            }
          });
    }
  }

  private void addRoomPriceBreakdown(String arrivalDate, String departureDate,
                                     HotelAvailabilityByIds hotelAvailabilityByIds) {

    hotelAvailabilityByIds.getHotelAvailability().parallelStream()
        .forEach(concurrentTracer.wrap((Consumer<HotelAvailability>) hotelAvailability -> Flux.fromIterable(
                hotelAvailability.getRoomRates())
            .flatMap(roomRate -> Mono.zip(Mono.just(roomRate), availabilityOhipPort
                .getHotelMultiRoomsPriceBreakdown(hotelAvailability.getHotelId(),
                    createRoomPriceBreakdownRequest(
                        roomRate,
                        arrivalDate, departureDate))))
            .collectList()
            .blockOptional()
            .orElseThrow(() -> {
              var exception = new NoAvailabilityException(ErrorCode.DIGITAL_NO_AVAILABILITY_EXCEPTION,
                  NO_AVAILABILITY);
              ExceptionLogger.log(log, exception);
              return exception;
            })
            .forEach(t -> {
              if (t.getT1().getRoomTypes().stream().mapToInt(r -> r.getRooms().size()).sum()
                  != t.getT2().getPriceBreakdown().size()) {
                var exception = new NoAvailabilityException(ErrorCode.DIGITAL_NO_AVAILABILITY_2_EXCEPTION,
                    NO_AVAILABILITY);
                ExceptionLogger.log(log, exception);
                throw exception;
              }

              var priceIterator = t.getT2().getPriceBreakdown().iterator();

              t.getT1().getRoomTypes().forEach(roomType ->
                  roomType.getRooms().forEach(room -> {
                    room.setRoomPriceBreakdown(priceIterator.next());
                    log.debug("Price set for rate {}, roomType {}, ohipRoomType {}: {}",
                        t.getT1().getRatePlanCode(),
                        roomType.getRoomType(),
                        room.getPmsRoomType(),
                        room.getRoomPriceBreakdown());
                  }));

            })));
  }

  private void addRoomPriceBreakdownV2(HotelAvailabilityByIdsV2Request request,
                                       HotelAvailabilityByIdsV2 hotelAvailabilityByIdsV2) {

    var arrivalDate = request.getArrivalDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    var departureDate = request.getDepartureDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    var guestRoomMap = request.getRooms().stream()
        .collect(Collectors.toMap(r -> buildParamsKey(r.getAdults().toString(), r.getChildren().toString(), r.getTag()),
            room -> room));
    /*
    The response will only have a single roomStay object with STANDARD room class
    https://whitbreadis.atlassian.net/wiki/spaces/DCA/pages/3927146589/Distribution+solution+for+multi-hotel+availabilities
    */
    hotelAvailabilityByIdsV2.getHotelAvailability().parallelStream()
        .forEach(concurrentTracer.wrap(
            (Consumer<HotelAvailabilityResultV2>) hotelAvailability -> Flux.fromIterable(
                    hotelAvailability.getRoomStays().get(0).getRoomTypes())
                .map(roomType -> {
                  var guestConfig = guestRoomMap.getOrDefault(
                      buildParamsKey(roomType.getAdults(), roomType.getChildren(), roomType.getTag()),
                      uk.co.whitbread.domain.model.availability.in.Room.builder().build());
                  return roomType.getRoomRates().stream()
                      .map(roomRateV2 -> Triple.of(roomRateV2, roomType.getRoomType(), guestConfig))
                      .toList();
                })
                .flatMap(Flux::fromIterable)
                .flatMap(roomRateConfig -> Mono.zip(Mono.just(roomRateConfig.getLeft()), availabilityOhipPort
                    .getHotelMultiRoomsPriceBreakdown(hotelAvailability.getHotelId(),
                        createRoomPriceBreakdownRequestV2(roomRateConfig,
                            arrivalDate, departureDate))))
                .collectList()
                .blockOptional()
                .orElseThrow(() -> {
                  var exception = new NoAvailabilityException(ErrorCode.DIGITAL_NO_AVAILABILITY_3_EXCEPTION,
                      NO_AVAILABILITY);
                  ExceptionLogger.log(log, exception);
                  return exception;
                })
                .forEach(t -> {
                  var priceBreakdown = t.getT2().getPriceBreakdown();

                  if (CollectionUtils.isEmpty(priceBreakdown)) {
                    var exception = new NoAvailabilityException(ErrorCode.DIGITAL_NO_AVAILABILITY_4_EXCEPTION,
                        NO_AVAILABILITY);
                    ExceptionLogger.log(log, exception);
                    throw exception;
                  }

                  var roomRate = t.getT1();

                  roomRate.getRoomRateInfo().setPriceInfo(
                      priceBreakdown.get(0).getDailyPrices().stream()
                          .map(dailyPrice -> PriceInfo.builder()
                              .stayDate(
                                  LocalDate.parse(dailyPrice.getDate(), DateTimeFormatter.ofPattern(DATE_PATTERN)))
                              .amountAfterTax(dailyPrice.getNetPrice())
                              .amountBeforeTax(dailyPrice.getGrossPrice())
                              .build())
                          .toList()

                  );


                })));
  }

  private RoomPriceBreakdownRequest createRoomPriceBreakdownRequest(
      RoomRate roomRate, String arrivalDate, String departureDate) {

    var roomTypes = new ArrayList<String>();
    var adults = new ArrayList<Integer>();
    var children = new ArrayList<Integer>();

    roomRate.getRoomTypes().forEach(r -> {
      var currentRoomTypes = r.getRooms().stream().map(Room::getPmsRoomType).toList();
      roomTypes.addAll(currentRoomTypes);
      adults.addAll(Collections.nCopies(currentRoomTypes.size(), r.getAdults()));
      children.addAll(Collections.nCopies(currentRoomTypes.size(), r.getChildren()));
    });

    return RoomPriceBreakdownRequest.builder()
        .arrivalDate(arrivalDate)
        .departureDate(departureDate)
        .ratePlanCode(roomRate.getRatePlanCode())
        .roomTypes(roomTypes)
        .adultsNo(adults)
        .childrenNo(children)
        .build();
  }


  private RoomPriceBreakdownRequest createRoomPriceBreakdownRequestV2(
      Triple<RoomRateV2, String, uk.co.whitbread.domain.model.availability.in.Room> roomRateConfig, String arrivalDate,
      String departureDate) {
    /*
    roomRateConfig is a Triple containing roomRateV2, roomType value and the pair of adults and children number
     */

    return RoomPriceBreakdownRequest.builder()
        .arrivalDate(arrivalDate)
        .departureDate(departureDate)
        .ratePlanCode(roomRateConfig.getLeft().getRatePlanCode())
        .roomTypes(List.of(roomRateConfig.getMiddle()))
        .adultsNo(List.of(roomRateConfig.getRight().getAdults()))
        .childrenNo(List.of(roomRateConfig.getRight().getChildren()))
        .build();
  }

  private List<RoomSubstitutionRuleResponse> getRoomSubstitutions(
      List<String> roomTypeList, List<Integer> adults, List<Integer> children,
      List<Boolean> cotsRequiredList, String channel) {

    List<RoomSubstitutionRuleResponse> roomSubstitutions = new ArrayList<>();

    var roomTypes = roomTypeList.iterator();
    var adultsNumber = adults.iterator();
    var childrenNumber = children.iterator();
    var cotsRequired = cotsRequiredList.iterator();

    while (roomTypes.hasNext() && adultsNumber.hasNext() && childrenNumber.hasNext()
        && cotsRequired.hasNext()) {
      var roomSubstitution = rulesAgentInPort.getRoomSubstitutionRule(
              RoomSubstitutionRuleRequest.builder()
                      .roomType(roomTypes.next())
                      .adults(adultsNumber.next())
                      .children(childrenNumber.next())
                      .channel(channel)
                      .pms("OP")
                      .build());
      roomSubstitution.getRequestDetails().setCotRequired(cotsRequired.next());
      roomSubstitutions.add(roomSubstitution);

    }
    return roomSubstitutions;
  }

  private void applyRateSuppressionRules(HotelAvailability availability,
      List<String> rateSuppressionList) {

    if (!CollectionUtils.isEmpty(rateSuppressionList)
        && !CollectionUtils.isEmpty(availability.getRoomRates())) {
      // Order all rates by suppression rule
      var rates = new ArrayList<>(availability.getRoomRates());
      rates.sort((r1, r2) -> rateOrderComparator(rateSuppressionList, r1, r2));

      final List<RoomRate> suppressableRates =
          rates.stream().filter(x -> rateSuppressionList.contains(x.getRatePlanCode())).toList();

      log.info("Code Fix - Start :{}", LocalDateTime.now());
      final Set<String> roomClasses = getRoomClasses();
      //filter room rates to be suppressed for each roomClass.
      var roomRatesToSuppress = filterSuppressedRoomRates(roomClasses, rates, suppressableRates);
      //Remove the rooms & eventually rates that have been suppressed
      suppressRoomRates(availability, roomRatesToSuppress, rates);
      log.info("Code Fix - End :{}", LocalDateTime.now());
    }
  }

  private List<String> applyCompanyRateSuppression(HotelAvailability availability,
      String companyId, String channel) {
    if (!unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCompanyRateSuppression())
        || StringUtils.isBlank(companyId)
        || (!BB_BOOKING_CHANNEL.equals(channel) && !CCUI_BOOKING_CHANNEL.equals(channel))) {
      return Collections.emptyList();
    }
    String cdhCompanyAccountId = getCdhCompanyAccountId(companyId, channel);

    List<String> suppressRates = cdhAdapterOutPort.getCompanySuppressRates(cdhCompanyAccountId);
    if (CollectionUtils.isEmpty(suppressRates)) {
      return Collections.emptyList();
    }
    log.info("Removing company-suppressed rates {} for companyId={}", suppressRates, companyId);
    availability.setRoomRates(
        availability.getRoomRates().stream()
            .filter(rate -> !suppressRates.contains(rate.getRatePlanCode()))
            .toList()
    );
    return suppressRates;
  }

  private String getCdhCompanyAccountId(String globalCompanyId, String bookingChannel) {
    if (!StringUtils.isNumeric(globalCompanyId.trim())) {
      return globalCompanyId;
    }

    var company = companyEntityServiceOutPort.getCompanyById(globalCompanyId);

    CdhSearchCompaniesRequest searchCompaniesRequest =
        CdhSearchCompaniesRequest.builder()
            .globalCompanyId(Integer.valueOf(company.getCorpId().trim()))
            .accessContext(bookingChannel)
            .build();
    return cdhAdapterOutPort.getCompanyAccountIdFromCdh(searchCompaniesRequest);
  }

  private Set<String> getRoomClasses() {
    final List<String> roomClassesFrmProperties = rateSuppressionProperties.getRoomClasses();
    if (null == roomClassesFrmProperties
        || CollectionUtils.isEmpty(roomClassesFrmProperties)) {
      final String errMsg = "Room classes are not configured in the properties";
      log.error(errMsg);
      throw new MissingResourceException(errMsg,
          this.getClass().getName(),
          "rate-suppression.room-classes");
    }
    return new HashSet<>(roomClassesFrmProperties);
  }

  private HashMap<String, List<Room>> filterSuppressedRoomRates(
      Set<String> roomClasses, ArrayList<RoomRate> rates,
      List<RoomRate> suppressableRates) {
    var roomRatesToSuppress = new HashMap<String, List<Room>>();
    for (final String roomClass : roomClasses) {
      //initial rate for all types of rooms will be FLEXRATE
      final List<Room> flexRoomRates = rates.get(0).getRoomTypes().stream().flatMap(
              roomTypeInfo -> roomTypeInfo.getRooms().stream())
          .filter(room -> roomClass.equals(room.getRoomClass())).toList();
      if (!CollectionUtils.isEmpty(flexRoomRates)) {
        var previousTotalPrice = calculateTotalStayPrice(flexRoomRates);
        // Starting at index 1 as the #1 in the sequence (i.e. Flex) always needs to be displayed
        compareRoomRates(suppressableRates, roomClass, previousTotalPrice, roomRatesToSuppress);
      }
    }
    return roomRatesToSuppress;
  }

  private void compareRoomRates(List<RoomRate> suppressableRates, String roomClass,
      BigDecimal previousTotalPrice, HashMap<String, List<Room>> roomRatesToSuppress) {
    for (int i = 1; i < suppressableRates.size(); i++) {
      final String ratePlanCode = suppressableRates.get(i).getRatePlanCode();
      final List<RoomTypeInfo> roomTypes = suppressableRates.get(i).getRoomTypes();
      final List<Room> rooms = roomTypes.stream().flatMap(
              roomTypeInfo -> roomTypeInfo.getRooms().stream())
          .filter(room -> roomClass.equals(room.getRoomClass())).toList();
      final BigDecimal currentTotalPrice = calculateTotalStayPrice(rooms);
      // current price > previous price - 1
      if (previousTotalPrice.subtract(currentTotalPrice).compareTo(BigDecimal.ONE) < 0) {
        log.debug(
            "Rate: {} will be suppressed for roomClass: {} as total price: {} > previous rate: {} - 1",
            ratePlanCode, roomClass, currentTotalPrice, previousTotalPrice);
        populateRoomRatesToSuppress(roomRatesToSuppress, rooms, ratePlanCode);
      }
      previousTotalPrice = currentTotalPrice;
    }
  }

  private void populateRoomRatesToSuppress(
      HashMap<String, List<Room>> roomRatesToSuppress,
      final List<Room> roomsToSuppress, final String ratePlanCode) {
    final List<Room> suppressedRoomRates = roomRatesToSuppress.get(ratePlanCode);
    if (null != suppressedRoomRates && !suppressedRoomRates.isEmpty()) {
      final List<Room> suppressedRoomRatesUpdated = new LinkedList<>(suppressedRoomRates);
      suppressedRoomRatesUpdated.addAll(roomsToSuppress);
      roomRatesToSuppress.put(ratePlanCode, suppressedRoomRatesUpdated);
    } else {
      roomRatesToSuppress.put(ratePlanCode, roomsToSuppress);
    }
  }

  private BigDecimal calculateTotalStayPrice(final List<Room> rooms) {
    return rooms.stream()
        .filter(room -> room != null && room.getRoomPriceBreakdown() != null)
        .map(room -> room.getRoomPriceBreakdown().getTotalNetAmount())
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private static void suppressRoomRates(HotelAvailability availability,
      final HashMap<String, List<Room>> roomClassToSuppress,
      List<RoomRate> rates) {
    roomClassToSuppress.forEach((ratePlanCode, roomRateClass) -> {
      log.info("Suppressing Room Rates:{} for RatePlanCode:{}", roomRateClass, ratePlanCode);
      final RoomRate roomRate = rates.stream()
          .filter(rate -> rate.getRatePlanCode().equalsIgnoreCase(ratePlanCode))
          .findAny().orElse(null);

      if (null != roomRate) {

        final List<RoomTypeInfo> roomTypeInfoLst = rates.stream()
            .filter(rate -> rate.getRatePlanCode().equalsIgnoreCase(ratePlanCode))
            .map(RoomRate::getRoomTypes)
            .flatMap(roomTypes -> roomTypes.stream())
            .collect(Collectors.toList());
        final List<RoomTypeInfo> roomTypeInfosToRemove = new ArrayList<>();
        for (final RoomTypeInfo roomTypeInfo : roomTypeInfoLst) {
          List<Room> roomsToUpdate =
              new LinkedList<>(roomTypeInfo.getRooms());
          //remove the room rate that needs to be suppressed
          roomsToUpdate.removeAll(roomRateClass);
          roomTypeInfo.setRooms(roomsToUpdate);
          if (roomsToUpdate.isEmpty()) {
            roomTypeInfosToRemove.add(roomTypeInfo);
          }
        }
        //if all room rates are suppressed,
        //then remove the roomTypeInfo from the roomTypeInfoLst
        if (!roomTypeInfosToRemove.isEmpty()) {
          roomTypeInfoLst.removeAll(roomTypeInfosToRemove);
        }
        //if all room type infos are removed,
        //then remove the rate
        if (roomTypeInfoLst.isEmpty()) {
          rates.remove(roomRate);
        }
        availability.setRoomRates(rates);
      }
    });
  }

  private int rateOrderComparator(List<String> rateSuppressionList, RoomRate roomRate,
                                  RoomRate roomRate1) {
    var rateIndex = rateSuppressionList.indexOf(roomRate.getRatePlanCode());
    var rateIndex1 = rateSuppressionList.indexOf(roomRate1.getRatePlanCode());

    if (rateIndex < 0) {
      rateIndex = Integer.MAX_VALUE;
    }

    if (rateIndex1 < 0) {
      rateIndex1 = Integer.MAX_VALUE;
    }

    return rateIndex - rateIndex1;
  }

  private List<String> getRateSuppressionList() {
    if (suppressionRulesExpiryDate == null
        || suppressionRulesExpiryDate.isBefore(LocalDateTime.now())) {
      var rateSuppressionRuleResponse = rulesAgentInPort.getRateSuppressionRule();

      this.cachedRateSuppressionList =
          new CopyOnWriteArrayList<>(rateSuppressionRuleResponse.getRateSuppressionList());
      this.suppressionRulesExpiryDate =
          rateSuppressionRuleResponse.getExpiryDate().toInstant().atZone(
              ZoneId.systemDefault()).toLocalDateTime();
    }

    return List.copyOf(cachedRateSuppressionList);
  }

  @Override
  public RateCodePricingResult getRateCodePricing(RateCodeCriteria rateCodeCriteria) {
    log.debug("Entered getRateCodePricing with rateCodeCriteria={}", rateCodeCriteria);
    if (promotionProperties.getPromotionalRate().equals(rateCodeCriteria.getReservationRatePlanCode())) {
      return new RateCodePricingResult();
    }
    return availabilityOhipPort.getRateCodePricing(rateCodeCriteria);
  }

  @Override
  public HotelInventoryRoomType getHotelRoomsInventory(
      HotelInventoryRequest hotelInventoryRequest) {
    log.debug("Requested hotel inventory for {}, from {} to {}.",
        hotelInventoryRequest.getHotelId(),
        hotelInventoryRequest.getDateRangeStart(),
        hotelInventoryRequest.getDateRangeEnd());

    return availabilityOhipPort.getHotelRoomsInventory(hotelInventoryRequest);
  }

  @Override
  public HotelAvailabilityByIdsV2 getHotelAvailabilityByIdsV2(
      HotelAvailabilityByIdsV2Request request) {

    hotelAvailabilityCheckRules.validateRoomOccupancyRule(request);
    removeInvalidHotelIds(request);

    if (request.getHotelIds().isEmpty()) {
      var message = "Request could not be processed";
      var exception = new HotelAvailabilityBadReqException(
          ErrorCode.DIGITAL_REQUEST_NOT_PROCESSED_EXCEPTION.getMessage(),
          message, null, ErrorCode.DIGITAL_REQUEST_NOT_PROCESSED_EXCEPTION.getCode());
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    if (!hasRequestOnlyPublicRates(request) && !request.isVatNotRequired()) {
      return toNonNullV2Result(fetchFromOhipV2(request));
    }

    if (DISTR_BOOKING_CHANNEL.equals(request.getBookingChannel().getChannel())) {
      return fetchFromDistributionCacheV2(request);
    }

    return HotelAvailabilityByIdsV2.builder().hotelAvailability(List.of()).build();
  }

  private void removeInvalidHotelIds(HotelAvailabilityByIdsV2Request request) {
    HotelsMigrationStatusResponse migrationStatusResponse = onSaleFlagOutPortImpl.getOnSaleFlag(
        HotelsMigrationStatusRequest.builder().hotelIds(request.getHotelIds()).build());
    List<String> invalidHotelIds = migrationStatusResponse.getMigrationStatusList().stream()
        .filter(response -> !response.getOnSale())
        .map(HotelMigrationStatusResponse::getHotelId)
        .toList();
    request.getHotelIds().removeIf(hotel -> invalidHotelIds.stream()
        .anyMatch(invalidHotel -> invalidHotel.equalsIgnoreCase(hotel)));
  }

  private HotelAvailabilityByIdsV2 fetchFromOhipV2(HotelAvailabilityByIdsV2Request request) {
    var result = availabilityOhipPort.getHotelAvailabilityByIdsV2(request);
    removeCityTaxIfApplicableForV2(request, result);
    if (result != null && result.getHotelAvailability() != null
        && !result.getHotelAvailability().isEmpty()) {
      OccupancySupplementDistributionUtils.applyOccupancySupplement(request, result,
          unleashWrapper, rulesAgentOutPort, PRICE_FROM_OPERA,
              MULTIPLY_FOR_NO_NIGHTS);
    }
    return result;
  }

  private HotelAvailabilityByIdsV2 fetchFromDistributionCacheV2(HotelAvailabilityByIdsV2Request request) {
    var roomTypes = request.getRooms().stream()
        .map(uk.co.whitbread.domain.model.availability.in.Room::getTag).toList();
    var adults = request.getRooms().stream()
        .map(uk.co.whitbread.domain.model.availability.in.Room::getAdults).toList();
    var children = request.getRooms().stream()
        .map(uk.co.whitbread.domain.model.availability.in.Room::getChildren).toList();
    var cotsRequired = new ArrayList<>(Collections.nCopies(request.getRooms().size(), false));
    var pmsRoomTypes = request.getRooms().stream()
        .map(uk.co.whitbread.domain.model.availability.in.Room::getRoomType).toList();

    var roomSubstitutions = resolveRoomSubstitutionsForV2(roomTypes, adults, children, cotsRequired, pmsRoomTypes);
    var result = availabilityCacheV1SearchOutPort.getHotelAvailabilityByIdsV2FromAvCache(request, roomSubstitutions);
    insertHotelOccupancy(request.getRooms(), result, buildRoomSubstitutionMap(roomSubstitutions));
    log.debug("Availabilities result: {}", result);

    boolean isPriceFromOpera = !request.isVatNotRequired();
    if (!request.isVatNotRequired()) {
      addRoomPriceBreakdownV2(request, result);
    }

    removeCityTaxIfApplicableForV2(request, result);
    OccupancySupplementDistributionUtils.applyOccupancySupplement(request, result,
        unleashWrapper, rulesAgentOutPort, isPriceFromOpera, false);
    return result;
  }

  private List<RoomSubstitutionRuleResponse> resolveRoomSubstitutionsForV2(
      List<String> roomTypes, List<Integer> adults, List<Integer> children,
      List<Boolean> cotsRequired, List<String> pmsRoomTypes) {
    if (pmsRoomTypes == null || pmsRoomTypes.isEmpty()) {
      return getRoomSubstitutions(roomTypes, adults, children, cotsRequired, DISTR_BOOKING_CHANNEL);
    }
    return createRoomSubstitutions(roomTypes, adults, children, cotsRequired, pmsRoomTypes, DISTR_BOOKING_CHANNEL);
  }

  private static HotelAvailabilityByIdsV2 toNonNullV2Result(HotelAvailabilityByIdsV2 result) {
    return result == null || result.getHotelAvailability() == null
        ? HotelAvailabilityByIdsV2.builder().hotelAvailability(List.of()).build()
        : result;
  }

  private void removeCityTaxIfApplicableForV2(HotelAvailabilityByIdsV2Request request,
                                              HotelAvailabilityByIdsV2 hotelAvailabilityByIdsV2) {
    if (Objects.nonNull(hotelAvailabilityByIdsV2.getHotelAvailability())
        && !hotelAvailabilityByIdsV2.getHotelAvailability().isEmpty()) {
      hotelAvailabilityByIdsV2.getHotelAvailability().forEach(
          hotelAvailability ->
              CityTaxUtils.setPriceWithoutCityTaxIfApplicableForV2(hotelAvailability,
                  request.getBookingChannel().getChannel(),
                  request.getArrivalDate(), request.getDepartureDate(),
                  contentServiceOutPort, unleashWrapper)
      );
    }
  }

  private List<RoomSubstitutionRuleResponse> createRoomSubstitutions(
      List<String> roomTypeList, List<Integer> adultsList, List<Integer> childrenList,
      List<Boolean> cotsRequiredList, List<String> pmsRoomTypeList, String channel) {
    List<RoomSubstitutionRuleResponse> roomSubstitutions = new ArrayList<>();

    var roomTypes = roomTypeList.iterator();
    var adultsNumber = adultsList.iterator();
    var childrenNumber = childrenList.iterator();
    var cotsRequired = cotsRequiredList.iterator();
    var pmsRoomTypes = pmsRoomTypeList.iterator();

    while (roomTypes.hasNext() && adultsNumber.hasNext() && childrenNumber.hasNext()
        && cotsRequired.hasNext() && pmsRoomTypes.hasNext()) {
      var pmsRoomType = pmsRoomTypes.next();
      RoomSubstitutionRuleResponse roomSubstitution;
      if (pmsRoomType == null || pmsRoomType.isBlank()) {
        roomSubstitution = rulesAgentInPort.getRoomSubstitutionRule(
                RoomSubstitutionRuleRequest.builder()
                        .roomType(roomTypes.next())
                        .adults(adultsNumber.next())
                        .children(childrenNumber.next())
                        .channel(channel)
                        .pms("OP")
                        .build());

      } else {
        roomSubstitution = rulesAgentInPort.createRoomSubstitutionRule(
                roomTypes.next(), adultsNumber.next(), childrenNumber.next(), pmsRoomType, channel);
      }
      roomSubstitution.getRequestDetails().setCotRequired(cotsRequired.next());
      roomSubstitutions.add(roomSubstitution);
    }
    return roomSubstitutions;
  }

  private Map<String, List<RoomSubstitution>> buildRoomSubstitutionMap(
      List<RoomSubstitutionRuleResponse> roomSubstitutions) {
    var specialRequestMap = new HashMap<String, List<RoomSubstitution>>();

    roomSubstitutions
        .forEach(roomSubstitution ->
            specialRequestMap.putIfAbsent(
                buildParamsKey(roomSubstitution.getRequestDetails().getAdults().toString(),
                    roomSubstitution.getRequestDetails().getChildren().toString(),
                    roomSubstitution.getRequestDetails().getRoomType()),
                roomSubstitution.getSubstitutionList()));
    return specialRequestMap;
  }

  private String buildParamsKey(String adults, String children, String roomType) {
    var stringJoiner = new StringJoiner("-");
    stringJoiner.add(adults).add(children)
        .add(roomType);
    return stringJoiner.toString();

  }

  private void insertHotelOccupancy(List<uk.co.whitbread.domain.model.availability.in.Room> rooms,
                                    HotelAvailabilityByIdsV2 hotelAvailabilityByIdsV2,
                                    Map<String, List<RoomSubstitution>> roomSubstitutionMap) {

    hotelAvailabilityByIdsV2.getHotelAvailability().stream(
        ).map(HotelAvailabilityResultV2::getRoomStays)
        .forEach(roomStays -> {
          var roomsIterator = rooms.iterator();
          var roomTypeV2Iterator = roomStays.get(0).getRoomTypes().iterator();
          while (roomsIterator.hasNext() && roomTypeV2Iterator.hasNext()) {
            var room = roomsIterator.next();
            var roomType = roomTypeV2Iterator.next();
            if (room.getTag().equals(roomType.getTag())) {
              roomType.setAdults(room.getAdults().toString());
              roomType.setChildren(room.getChildren().toString());
              roomType.setNumberOfRooms(room.getNumberOfRooms().toString());
              roomType.setSpecialRequests(
                  insertSpecialRequests(roomSubstitutionMap.getOrDefault(
                      buildParamsKey(roomType.getAdults(), roomType.getChildren(),
                          roomType.getTag()), Collections.emptyList()), roomType.getRoomType()));
            }
          }
        });
  }

  private List<String> insertSpecialRequests(List<RoomSubstitution> roomSubstitutions, String roomType) {
    return roomSubstitutions.stream()
        .filter(roomSubstitution -> roomSubstitution.getType().equals(roomType))
        .findFirst()
        .map(this::createSpecialRequestsList)
        .orElse(Collections.emptyList());

  }

  private List<String> createSpecialRequestsList(RoomSubstitution roomSubstitution) {
    return Stream.of(roomSubstitution.getSpecialRequest(), roomSubstitution.getAccessibleSpecialRequest())
        .filter(Objects::nonNull)
        .toList();
  }

  private boolean hasRequestOnlyPublicRates(HotelAvailabilityByIdsV2Request request) {
    List<CorporateRate> corporateRates = request.getRates().getCorporateRates();
    return (Objects.isNull(request.getRates())
        || CollectionUtils.isEmpty(corporateRates)
        || corporateRates.stream().allMatch(Objects::isNull)
        || corporateRates.stream().allMatch(c -> Objects.isNull(c.getRatePlanSets())
        || c.getRatePlanSets().isEmpty()))
        && (request.getRates().getRatePlanCodes() == null
        || request.getRates().getRatePlanCodes().isEmpty());
  }

  private void applyFlexRateExclusionWhenBusiFlex(HotelAvailability hotelAvailability,
      List<String> companySuppressedRates) {
    if (Objects.isNull(hotelAvailability.getRoomRates())) {
      return;
    }
    var withoutFlex = hotelAvailability.getRoomRates().stream()
        .filter(i -> !FLEXRATE.equals(i.getRatePlanCode()))
        .toList();

    var withoutBusiFlex = withoutFlex.stream()
        .filter(i -> !BUSIFLEX.equals(i.getRatePlanCode()))
        .toList();

    // Case 1: BUSIFLEX is still present → hide FLEXRATE
    if (withoutBusiFlex.size() == hotelAvailability.getRoomRates().size() - 2) {
      hotelAvailability.setRoomRates(withoutFlex);
      return;
    }

    // Case 2: BUSIFLEX suppressed; hide FLEXRATE if negotiated rates remain.
    if (!CollectionUtils.isEmpty(companySuppressedRates) && companySuppressedRates.contains(BUSIFLEX)) {
      String hotelId = hotelAvailability.getHotelId();
      List<String> uniqueRatePlanCodes = withoutFlex.stream()
          .map(RoomRate::getRatePlanCode)
          .distinct()
          .toList();
      boolean negotiatedRatesRemain;
      List<CompletableFuture<Boolean>> futures = uniqueRatePlanCodes.stream()
          .map(code -> CompletableFuture.supplyAsync(
              concurrentTracer.wrap((Supplier<Boolean>) () -> isNegotiatedRate(code, hotelId))))
          .toList();
      negotiatedRatesRemain = futures.stream()
          .anyMatch(CompletableFuture::join);
      if (negotiatedRatesRemain) {
        log.info("BUSIFLEX was CDH-suppressed but negotiated rates remain; hiding FLEXRATE");
        hotelAvailability.setRoomRates(withoutFlex);
      }
    }
  }

  private boolean isNegotiatedRate(String ratePlanCode, String hotelId) {
    var response = availabilityOhipPort.getRatePlanInfo(ratePlanCode, hotelId);
    return response != null
        && !CollectionUtils.isEmpty(response.getRatePlanInfo())
        && response.getRatePlanInfo().stream()
        .anyMatch(rp -> !CollectionUtils.isEmpty(rp.getRatePlanBasedOnRates())
            && rp.getRatePlanBasedOnRates().getFirst().getDynamicBaseRate() != null
            &&
            rp.getRatePlanBasedOnRates().getFirst().getDynamicBaseRate().getDynamicBasedOnRatePlan()
                != null
            && rp.getRatePlanBasedOnRates().getFirst().getDynamicBaseRate()
            .getDynamicBasedOnRatePlan().equals(FLEXRATE));
  }

  private void applyExclusion(HotelAvailability hotelAvailability) {
    if (Objects.isNull(hotelAvailability.getRoomRates()) || Strings.isEmpty(
        promotionProperties.getExcludedRates())) {
      return;
    }
    var excludedRates = Arrays.stream(promotionProperties.getExcludedRates().split(",")).toList()
        .stream().map(String::trim).toList();

    var withoutExclusion = hotelAvailability.getRoomRates().stream()
        .filter(roomRate -> !excludedRates.contains(roomRate.getRatePlanCode()))
        .toList();
    hotelAvailability.setRoomRates(withoutExclusion);
  }

  public HotelAvailability buildEmptyRatesResponse(
      String hotelId, String startDate, String endDate) {
    return HotelAvailability.builder()
        .timestamp(Instant.now())
        .hotelId(hotelId)
        .startDate(startDate)
        .endDate(endDate)
        .available(false)
        .roomRates(new ArrayList<>())
        .build();
  }

  public List<List<String>> retrieveRoomTypesVariants(HotelAvailabilityRequest request,
                                                      MaxRoomOccupancyResponse occupancyResponse) {
    List<List<String>> roomTypeListFromRules = retrieveRoomTypeListFromRules(request, occupancyResponse);

    var maxVariants = Math.max(1, roomTypeListFromRules.stream()
            .mapToInt(List::size).max().orElse(0));
    List<List<String>> roomTypeVariants = new ArrayList<>();

    for (int i = 0; i < maxVariants; i++) {
      final int index = i;
      List<String> variant = roomTypeListFromRules.stream()
              .map(options -> {
                if (!options.isEmpty()) {
                  return index < options.size() ? options.get(index) : options.get(0);
                } else {
                  return "";
                }
              })
              .toList();
      roomTypeVariants.add(variant);
    }
    return roomTypeVariants;
  }

  /**
   * For each room (index in request.adultsNumber) find the accepted roomTypes list from the maxRoomOccupancyResponse,
   * filter out "DIS" and return a list of per-room option lists.
   * If the roomType value at the current index is "DIS"(Accessible room) in the request,
   * the method returns a single‑option list containing only "DIS" for that room.
   */
  private List<List<String>> retrieveRoomTypeListFromRules(HotelAvailabilityRequest request,
                                                           MaxRoomOccupancyResponse occupancyResponse) {
    return IntStream.range(0, request.getAdultsNumber().size())
            .mapToObj(i -> {

              String requestedRoomType = "";
              if (request.getRoomTypes() != null
                      && i < request.getRoomTypes().size()
                      && request.getRoomTypes().get(i) != null) {
                requestedRoomType = request.getRoomTypes().get(i);
              }
              if ("DIS".equalsIgnoreCase(requestedRoomType)) {
                return List.of("DIS");
              }
              int children = request.getChildrenNumber().get(i);
              int adults = request.getAdultsNumber().get(i);
              List<String> roomTypes = occupancyResponse.getRoomOccupancies().stream()
                      .filter(ro -> ro.getAdultsNumber() == adults && ro.getChildrenNumber() == children)
                      .map(MaxRoomOccupancyData::getAcceptedRoomTypes)
                      .flatMap(Collection::stream)
                      .filter(roomType -> !"DIS".equals(roomType))
                      .toList();

              if (roomTypes.isEmpty()) {
                var message = String.format(
                        "Error while trying to retrieve Room Types from MaxRoomOccupancyResponse for "
                        + "adultsNumber=%s, childrenNumber=%s", adults, children);
                var exception = new RulesAgentBadRequestException(
                        ErrorCode.DIGITAL_ROOM_OCCUPANCY_RULE_EXCEPTION, message);
                ExceptionLogger.log(log, exception);
                throw exception;
              }
              return roomTypes;
            })
            .toList();
  }
}

