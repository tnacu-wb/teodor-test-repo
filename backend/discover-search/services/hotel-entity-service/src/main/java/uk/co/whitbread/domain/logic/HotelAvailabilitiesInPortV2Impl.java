package uk.co.whitbread.domain.logic;

import static uk.co.whitbread.domain.logic.AvailabilitiesResponseUtils.applyFilters;
import static uk.co.whitbread.domain.logic.AvailabilitiesResponseUtils.applyOccupancySupplement;
import static uk.co.whitbread.domain.logic.AvailabilitiesResponseUtils.applyPagination;
import static uk.co.whitbread.domain.logic.AvailabilitiesResponseUtils.applySorting;
import static uk.co.whitbread.domain.logic.AvailabilitiesResponseUtils.checkNumberOfNightsCcui;
import static uk.co.whitbread.domain.logic.AvailabilitiesResponseUtils.convertDistance;
import static uk.co.whitbread.domain.logic.AvailabilitiesResponseUtils.getOpeningSoonHotelIds;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.domain.exceptions.InvalidChannelException;
import uk.co.whitbread.domain.model.availability.in.BookingChannel;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.in.RateV2;
import uk.co.whitbread.domain.model.availability.in.Room;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityResultV2;
import uk.co.whitbread.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.domain.model.availability.out.RoomStay;
import uk.co.whitbread.domain.model.distance.out.DistanceFromSearchResponse;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.model.migrationstatus.in.HotelsMigrationStatusRequest;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelMigrationStatusResponse;
import uk.co.whitbread.domain.model.migrationstatus.out.HotelsMigrationStatusResponse;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.in.OldWorldChannelEnum;
import uk.co.whitbread.domain.model.srp.in.RecommendedSearchModifiers;
import uk.co.whitbread.domain.model.srp.out.Cost;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.domain.ports.primary.HotelAvailabilitiesInPort;
import uk.co.whitbread.domain.ports.secondary.CacheSearchOutPort;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.domain.ports.secondary.OnSaleFlagOutPort;
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Slf4j
@RequiredArgsConstructor
public class HotelAvailabilitiesInPortV2Impl implements HotelAvailabilitiesInPort {
  public static final Float DEFAULT_HUB_MODIFIER_VALUE = 0.85F;
  private static final String PI_CHANNEL_ID = "PI";
  private static final String CCUI_CHANNEL_ID = "CCUI";
  private static final String BB_CHANNEL_ID = "BB";
  private static final String DISTR_CHANNEL_ID = "DISTR";
  private static final String PMS_SOURCE_OPERA = "OPERA";
  private static final String HUB_BRAND = "HUB";
  private static final int MAX_HOTELS_PER_OHIP_CALL = 40;
  private static final List<String> DEFAULT_RATE_PLAN_CODES = List.of("SEMIFLEX", "NONFLEX", "ADVANCE", "STANDARD");
  private final AvailabilitiesResponseFromAvCache availabilitiesResponseFromAvCache;
  private final RulesAgentValidations rulesAgentValidations;
  private final CacheSearchOutPort cacheSearchOutPort;
  private final ContentServiceOutPort contentServiceOutPort;
  private final HotelAvailabilityOutPort hotelAvailabilityOutPort;
  private final AuthenticatedUserService authenticatedUserService;
  //new Opera APIs solution
  private final SnowdropInformation snowdropInformation;
  private final OnSaleFlagOutPort onSaleFlagOutPortImpl;
  private final AvailabilitiesFromOpera availabilitiesFromOpera;
  private final ConcurrentTracer concurrentTracer;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final RulesAgentOutPort rulesAgentOutPort;
  private final MlosCommonLogic mlosCommonLogic;

  @Value("${ccui.srp.negotiatedrate.enabled}")
  protected boolean ccuiSrpNegotiatedRateEnabled;

  @Override
  public HotelAvailabilitiesResponse getAvailabilities(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    return switch (hotelAvailabilitiesRequest.getChannel()) {
      case PI_CHANNEL_ID -> getAvailabilitiesPi(hotelAvailabilitiesRequest);
      case CCUI_CHANNEL_ID -> getAvailabilitiesCcuiV2(hotelAvailabilitiesRequest);
      case BB_CHANNEL_ID -> getAvailabilitiesBbV2(hotelAvailabilitiesRequest);
      case DISTR_CHANNEL_ID -> getAvailabilitiesDistrV2(hotelAvailabilitiesRequest);
      default -> {
        var message = "Invalid channel received: " + hotelAvailabilitiesRequest.getChannel();
        var exception = new InvalidChannelException(ErrorCode.DIGITAL_INVALID_CHANNEL_2_EXCEPTION,
                message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    };
  }

  private HotelAvailabilitiesResponse getAvailabilitiesPi(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    return getAvailabilitiesFromAvCache(hotelAvailabilitiesRequest, true);
  }

  private HotelAvailabilitiesResponse getAvailabilitiesDistrV2(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    return getAvailabilitiesFromAvCache(hotelAvailabilitiesRequest, true);
  }

  private HotelAvailabilitiesResponse getAvailabilitiesCcuiV2(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    rulesAgentValidations.validateBusinessRules(hotelAvailabilitiesRequest,
        hotelAvailabilitiesRequest.getChannel(), true);

    log.info("ccuiSrpNegotiatedRateEnabled {}",  ccuiSrpNegotiatedRateEnabled);

    HotelAvailabilitiesResponse response = new HotelAvailabilitiesResponse();

    boolean specialCaseSearch = checkNumberOfNightsCcui(hotelAvailabilitiesRequest);

    if (specialCaseSearch) {

      var cacheResult = cacheSearchOutPort
          .getOperaAvailabilityFromCache(hotelAvailabilitiesRequest);

      //get hotels from Snowdrop
      List<DistanceFromSearchResponse> snowdropHotelList = snowdropInformation.getHotelsFromSnowdrop(
          hotelAvailabilitiesRequest);

      if (cacheDoesNotExist(cacheResult)) {
        log.debug("No cache entries for availabilities request CCUI {}", hotelAvailabilitiesRequest);

        if (snowdropHotelList.isEmpty()) {
          return AvailabilitiesResponseUtils.buildEmptyResponse(hotelAvailabilitiesRequest);
        }

        //get migration status from Hotel Migration Status
        var migrationStatusResponse = onSaleFlagOutPortImpl.getOnSaleFlag(
            HotelsMigrationStatusRequest.builder()
                .hotelIds(snowdropInformation.getSnowdropHotelIds(snowdropHotelList))
                .build());

        convertAndUpdateDistance(hotelAvailabilitiesRequest, snowdropHotelList);

        var operaHotels = partitionHotels(getOperaHotelsLists(migrationStatusResponse));

        if (!operaHotels.isEmpty()) {
          response = availabilitiesFromOpera.getOperaHotelsAvailabilities(
              hotelAvailabilitiesRequest,
              null,
              operaHotels.get(0));

          AvailabilitiesResponseUtils.updateAvailabilitiesResponseWithOperaSoldOutHotels(response,
              operaHotels.get(0));
        }
        response.setTotal(snowdropHotelList.size());

        updateHotelsDistanceAndPms(hotelAvailabilitiesRequest, snowdropHotelList, migrationStatusResponse,
            response.getHotelAvailabilityList());

        updateOpeningSoonHotels(response);

        cacheSearchOutPort.saveOperaHotels(hotelAvailabilitiesRequest,
            response.getHotelAvailabilityList());

        startAsyncAvailabilityRequests(hotelAvailabilitiesRequest, snowdropHotelList, migrationStatusResponse,
            operaHotels, null);

        // Sorting
        applySorting(response, Collections.singletonList(hotelAvailabilitiesRequest.getSort()));

        // Pagination
        applyPagination(response, hotelAvailabilitiesRequest);
      } else {
        response.setHotelAvailabilityList(cacheResult);
        response.setTotal(snowdropHotelList.size());
        response.setPage(hotelAvailabilitiesRequest.getPage());
        response.setPageSize(hotelAvailabilitiesRequest.getPageSize());
      }
      // Filtering
      applyFlags(hotelAvailabilitiesRequest, response, snowdropHotelList);
    } else {

      if (StringUtils.isNoneBlank(hotelAvailabilitiesRequest.getCompanyId())
          &&  ccuiSrpNegotiatedRateEnabled) {

        return getAvailabilitiesBbV2(hotelAvailabilitiesRequest);
      }
      return getAvailabilitiesFromAvCache(hotelAvailabilitiesRequest, false);
    }
    return response;
  }

  /* Call MLOS restrictions for available hotels. If MLOS applicable, set hotel as available=false */
  private void applyMlos(HotelAvailabilitiesResponse response, HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    if (mlosCommonLogic.isMlosEnabled(hotelAvailabilitiesRequest.getChannel())) {
      var hotelIds = response.getHotelAvailabilityList()
          .stream()
          .filter(HotelAvailabilityResponse::getAvailable)
          .map(HotelAvailabilityResponse::getHotelId)
          .toList();
      var arrivalDate = hotelAvailabilitiesRequest.getArrivalDate();
      var departureDate = hotelAvailabilitiesRequest.getDepartureDate();

      var restrictionsByHotelMap = mlosCommonLogic.getRestrictionsMapForHotels(hotelIds, arrivalDate, departureDate);
      response.getHotelAvailabilityList().forEach(hotel ->
          processHotelRestrictions(hotel, restrictionsByHotelMap.get(hotel.getHotelId()), arrivalDate, departureDate));
    }
  }

  private void processHotelRestrictions(HotelAvailabilityResponse hotel,
                                        RestrictionsByDateRangeResult hotelRestrictions,
                                        String startDate, String endDate) {
    boolean hasHotelRestriction = mlosCommonLogic.isMlosRestrictionApplied(hotelRestrictions, startDate, endDate);
    hotel.setHasMlosRestriction(hasHotelRestriction);
    if (hasHotelRestriction) {
      hotel.setAvailable(Boolean.FALSE);
      hotel.setLowestRoomRate(null);
    }
  }

  private HotelAvailabilitiesResponse getAvailabilitiesBbV2(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {

    var account = Account.builder()
        .companyId(hotelAvailabilitiesRequest.getCompanyId())
        .bartId(hotelAvailabilitiesRequest.getCompanyId())
        .bartEmployeeId("")
        .build();

    HotelAvailabilitiesRequest authorizedHaRequest;

    if (BB_CHANNEL_ID.equalsIgnoreCase(hotelAvailabilitiesRequest.getChannel())) {
      account = authenticatedUserService.getCurrentUserAccount()
          .orElseThrow(() -> new AccessDeniedException("Not allowed to search in BB context"));

      authorizedHaRequest = hotelAvailabilitiesRequest.toBuilder()
          .authorization(authenticatedUserService.getAuthenticatedUser().getToken().getTokenValue())
          .build();
    } else {
      authorizedHaRequest = hotelAvailabilitiesRequest;
    }

    rulesAgentValidations.validateBusinessRules(hotelAvailabilitiesRequest,
        hotelAvailabilitiesRequest.getChannel(), true);

    var cacheResult = cacheSearchOutPort
        .getOperaAvailabilityFromCache(hotelAvailabilitiesRequest);

    HotelAvailabilitiesResponse mergedResponse = new HotelAvailabilitiesResponse();

    //get hotels from Snowdrop
    List<DistanceFromSearchResponse> snowdropHotelList = snowdropInformation.getHotelsFromSnowdrop(
        hotelAvailabilitiesRequest);

    if (cacheDoesNotExist(cacheResult)) {
      log.debug("No cache entries for availabilities request BB {}", hotelAvailabilitiesRequest);

      // new Opera APIs
      if (snowdropHotelList.isEmpty()) {
        return AvailabilitiesResponseUtils.buildEmptyResponse(hotelAvailabilitiesRequest);
      }

      convertAndUpdateDistance(hotelAvailabilitiesRequest, snowdropHotelList);

      //get migration status from Hotel Migration Status
      var migrationStatusResponse = onSaleFlagOutPortImpl.getOnSaleFlag(
          HotelsMigrationStatusRequest.builder()
              .hotelIds(snowdropInformation.getSnowdropHotelIds(snowdropHotelList))
              .build());

      List<HotelMigrationStatusResponse> operaHotelsList = getOperaHotelsLists(migrationStatusResponse);

      CompletableFuture<HotelAvailabilitiesResponse> operaAvailabilities = null;

      var operaHotels = partitionHotels(operaHotelsList);

      if (isSortByRecommendation(hotelAvailabilitiesRequest)) {

        getAvailabilitySortedByRecommendation(hotelAvailabilitiesRequest, snowdropHotelList, migrationStatusResponse,
            operaHotels, account, mergedResponse);

      } else {

        if (!operaHotels.isEmpty()) {
          Account finalAccount = account;
          operaAvailabilities =
              CompletableFuture.supplyAsync(concurrentTracer.wrap(
                  (Supplier<HotelAvailabilitiesResponse>) () ->
                      availabilitiesFromOpera.getOperaHotelsAvailabilities(authorizedHaRequest,
                          finalAccount,
                          operaHotels.get(0))));
        }

        List<HotelAvailabilityResponse> operaHotelsAv = Stream.of(operaAvailabilities)
            .filter(Objects::nonNull)
            .map(CompletableFuture::join)
            .flatMap(elem -> elem.getHotelAvailabilityList().stream())
            .toList();
        mergedResponse.setHotelAvailabilityList(operaHotelsAv);

        if (!operaHotels.isEmpty()) {
          AvailabilitiesResponseUtils.updateAvailabilitiesResponseWithOperaSoldOutHotels(
              mergedResponse, operaHotels.get(0));
        }

        updateHotelsDistanceAndPms(hotelAvailabilitiesRequest, snowdropHotelList, migrationStatusResponse,
            mergedResponse.getHotelAvailabilityList());

        mergedResponse.setTotal(operaHotelsList.size());

        updateOpeningSoonHotels(mergedResponse);

        applyFlagsWithoutFilters(hotelAvailabilitiesRequest, mergedResponse, snowdropHotelList);

        cacheSearchOutPort.saveOperaHotels(hotelAvailabilitiesRequest,
            mergedResponse.getHotelAvailabilityList());

        applyFilters(cacheSearchOutPort, contentServiceOutPort, hotelAvailabilitiesRequest,
            mergedResponse);

        startAsyncAvailabilityRequests(hotelAvailabilitiesRequest, snowdropHotelList, migrationStatusResponse,
            operaHotels, account);

        // Sorting
        applySorting(mergedResponse, Collections.singletonList(hotelAvailabilitiesRequest.getSort()), null);

        // Pagination
        applyPagination(mergedResponse, hotelAvailabilitiesRequest);
      }
    } else {
      mergedResponse.setHotelAvailabilityList(getHotelAvailabilityList(hotelAvailabilitiesRequest, cacheResult));
      mergedResponse.setTotal(snowdropHotelList.size());
      mergedResponse.setPage(hotelAvailabilitiesRequest.getPage());
      mergedResponse.setPageSize(hotelAvailabilitiesRequest.getPageSize());
      applyFlags(hotelAvailabilitiesRequest, mergedResponse, snowdropHotelList);
    }

    //SRP BB for city tax exclusion logic for Edinburgh hotels before 24 july 2026
    checkCityTaxForBbHotel(hotelAvailabilitiesRequest, mergedResponse);

    return mergedResponse;
  }

  private void checkCityTaxForBbHotel(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
                                        HotelAvailabilitiesResponse mergedResponse) {
    if (!unleashWrapper.isEnabled(unleashWrapper.featureFlag().getReleaseBbCityTaxUk())) {
      return;
    }

    var globalConfig = contentServiceOutPort.getGlobalConfig(null, null);

    if (Objects.nonNull(globalConfig) && Objects.nonNull(globalConfig.getHotelsWithCityTax())) {
      List<String> cityTaxHotels = globalConfig.getHotelsWithCityTax();
      List<String> hotelIds = mergedResponse.getHotelAvailabilityList().stream()
          .map(HotelAvailabilityResponse::getHotelId)
          .toList();
      List<String> filteredHotelIds = hotelIds.stream().filter(cityTaxHotels::contains).toList();
      if (org.apache.commons.collections.CollectionUtils.isNotEmpty(filteredHotelIds)) {
        HotelAvailabilityByIdsV2 hotelAvailabilityByIdsV2 = getHotelAvailabilityByIdsV2(hotelAvailabilitiesRequest,
            filteredHotelIds);
        mergedResponse.getHotelAvailabilityList().forEach(hotel -> {
          var matchingHotel = findMatchingHotel(hotelAvailabilityByIdsV2, hotel.getHotelId());
          if (Objects.nonNull(matchingHotel) && !isCityTaxApplicable(hotelAvailabilitiesRequest, hotel.getHotelId())) {
            updateLowestRoomRateIfApplicable(hotel, matchingHotel);
          }
        });
      }
    }
  }

  private HotelAvailabilityResultV2 findMatchingHotel(
      HotelAvailabilityByIdsV2 hotelAvailabilityByIdsV2, String hotelId) {
    return hotelAvailabilityByIdsV2.getHotelAvailability().stream()
        .filter(h -> h.getHotelId().equals(hotelId))
        .findFirst()
        .orElse(null);
  }

  private boolean isCityTaxApplicable(HotelAvailabilitiesRequest request, String hotelId) {
    return CityTaxUtils.shouldSkipCityTax(
        request.getChannel(),
        hotelId,
        request.getArrivalDate(),
        contentServiceOutPort,
        unleashWrapper
    );
  }

  private void updateLowestRoomRateIfApplicable(HotelAvailabilityResponse hotel,
                                                HotelAvailabilityResultV2 matchingHotel) {
    List<RoomStay> roomStays = Optional.ofNullable(matchingHotel.getRoomStays())
        .orElse(Collections.emptyList());

    roomStays.forEach(roomStay -> {
      Map<String, double[]> ratePlanSums = new HashMap<>();
      final String[] currencyCode = {null};

      Optional.ofNullable(roomStay.getRoomTypes()).orElse(Collections.emptyList())
          .forEach(roomType -> Optional.ofNullable(roomType.getRoomRates()).orElse(Collections.emptyList())
              .forEach(roomRate -> {
                if (Objects.isNull(roomRate.getRoomRateInfo())
                    || Objects.isNull(roomRate.getRoomRateInfo().getPriceInfo())) {
                  return;
                }
                String ratePlanCode = roomRate.getRatePlanCode();
                double sumBeforeTax = roomRate.getRoomRateInfo().getPriceInfo().stream()
                    .mapToDouble(price -> price.getAmountBeforeTax().doubleValue())
                    .sum();
                double sumAfterTax = roomRate.getRoomRateInfo().getPriceInfo().stream()
                    .mapToDouble(price -> price.getAmountAfterTax().doubleValue())
                    .sum();
                ratePlanSums.computeIfAbsent(ratePlanCode, k -> new double[2]);
                ratePlanSums.get(ratePlanCode)[0] += sumBeforeTax;
                ratePlanSums.get(ratePlanCode)[1] += sumAfterTax;
                if (Objects.isNull(currencyCode[0])) {
                  currencyCode[0] = roomRate.getCurrencyCode();
                }
              }));
      updateHotelLowestRoomRateIfMatch(hotel, ratePlanSums, currencyCode[0]);
      ratePlanSums.clear();
    });
  }

  private void updateHotelLowestRoomRateIfMatch(HotelAvailabilityResponse hotel, Map<String, double[]> ratePlanSums,
                                                String currencyCode) {
    ratePlanSums.entrySet().stream()
        .filter(entry -> Objects.nonNull(hotel.getLowestRoomRate())
            && hotel.getLowestRoomRate().getNetTotal().doubleValue() == entry.getValue()[1])
        .findFirst()
        .ifPresent(entry -> hotel.setLowestRoomRate(new Cost(BigDecimal.valueOf(entry.getValue()[0]), currencyCode)));
  }

  private HotelAvailabilityByIdsV2 getHotelAvailabilityByIdsV2(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
                                                                List<String> filteredHotelIds) {
    List<Room> rooms = IntStream.range(0, hotelAvailabilitiesRequest.getRoomTypes().size())
        .mapToObj(i -> Room.builder()
            .adults(hotelAvailabilitiesRequest.getAdultsNumber().get(i))
            .children(hotelAvailabilitiesRequest.getChildrenNumber().get(i))
            .tag(hotelAvailabilitiesRequest.getRoomTypes().get(i))
            .numberOfRooms(1)
            .build())
        .toList();
    HotelAvailabilityByIdsV2Request request = HotelAvailabilityByIdsV2Request.builder()
        .bookingChannel(BookingChannel.builder()
            .channel(hotelAvailabilitiesRequest.getChannel())
            .build())
        .hotelIds(filteredHotelIds)
        .arrivalDate(LocalDate.parse(hotelAvailabilitiesRequest.getArrivalDate()))
        .departureDate(LocalDate.parse(hotelAvailabilitiesRequest.getDepartureDate()))
        .rooms(rooms)
        .rates(RateV2.builder().ratePlanCodes(DEFAULT_RATE_PLAN_CODES).build())
        .build();
    return hotelAvailabilityOutPort.getHotelAvailabilityByIdsV2(request);
  }

  private void applyFlags(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      HotelAvailabilitiesResponse mergedResponse,
      List<DistanceFromSearchResponse> snowdropHotelList) {
    // Filtering
    applyFilters(cacheSearchOutPort, contentServiceOutPort, hotelAvailabilitiesRequest,
        mergedResponse);

    final List<String> hubHotels = getHubHotels(snowdropHotelList);

    //hub hotels twin and fam room
    AvailabilitiesResponseUtils.flagHubHotelsAndUpdateForFamilyOrTwin(
        hotelAvailabilitiesRequest.getRoomTypes(), mergedResponse, hubHotels);

    // Set MLOS restriction flag
    applyMlos(mergedResponse, hotelAvailabilitiesRequest);
  }

  private void applyFlagsWithoutFilters(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      HotelAvailabilitiesResponse mergedResponse,
      List<DistanceFromSearchResponse> snowdropHotelList) {

    final List<String> hubHotels = getHubHotels(snowdropHotelList);

    // Hub hotels twin and family room
    AvailabilitiesResponseUtils.flagHubHotelsAndUpdateForFamilyOrTwin(
        hotelAvailabilitiesRequest.getRoomTypes(), mergedResponse, hubHotels);

    // Set MLOS restriction flag
    applyMlos(mergedResponse, hotelAvailabilitiesRequest);
  }

  private static boolean cacheDoesNotExist(List<HotelAvailabilityResponse> cacheResult) {
    return cacheResult == null || cacheResult.isEmpty();
  }

  private static boolean isSortByRecommendation(HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    return hotelAvailabilitiesRequest.getSort() != null
        && AvailabilitySortOption.RECOMMENDATION.name()
        .equals(hotelAvailabilitiesRequest.getSort());
  }

  private void getAvailabilitySortedByRecommendation(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      List<DistanceFromSearchResponse> snowdropHotelList,
      HotelsMigrationStatusResponse migrationStatusResponse,
      List<List<HotelMigrationStatusResponse>> operaHotels,
      Account account,
      HotelAvailabilitiesResponse mergedResponse) {

    var firstPageAvailabilities = getAvailabilityForFirstPage(hotelAvailabilitiesRequest, snowdropHotelList,
        migrationStatusResponse, operaHotels, account);

    var hotelsAv = firstPageAvailabilities.getHotelAvailabilityList().stream()
        .filter(Objects::nonNull)
        .toList();
    mergedResponse.setHotelAvailabilityList(hotelsAv);

    var totalHotels = operaHotels.stream().mapToInt(List::size).sum();

    updateHotelsDistanceAndPms(hotelAvailabilitiesRequest, snowdropHotelList, migrationStatusResponse,
        mergedResponse.getHotelAvailabilityList());

    mergedResponse.setTotal(totalHotels);

    updateOpeningSoonHotels(mergedResponse);

    applyFlags(hotelAvailabilitiesRequest, mergedResponse, snowdropHotelList);

    // sort
    var recommModifiers = getRecommendedSearchModifiers(hotelAvailabilitiesRequest);
    applySorting(mergedResponse, Collections.singletonList(hotelAvailabilitiesRequest.getSort()), recommModifiers);

    final HotelAvailabilitiesResponse originalResponse = HotelAvailabilitiesResponse.builder()
        .hotelAvailabilityList(mergedResponse.getHotelAvailabilityList())
        .build();
    if (hotelAvailabilitiesRequest.getPageSize() < mergedResponse.getHotelAvailabilityList().size()) {
      originalResponse.setHotelAvailabilityList(new ArrayList<>(mergedResponse.getHotelAvailabilityList()));
    }

    // apply pagination
    applyPagination(mergedResponse, hotelAvailabilitiesRequest);

    CompletableFuture.runAsync(() -> saveToCacheForRecommendationSort(hotelAvailabilitiesRequest,
        originalResponse, snowdropHotelList, migrationStatusResponse, account, operaHotels,
        recommModifiers));
  }

  private static List<HotelAvailabilityResponse> getHotelAvailabilityList(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      List<HotelAvailabilityResponse> cacheResult) {
    return hotelAvailabilitiesRequest.getSort() != null && AvailabilitySortOption.RECOMMENDATION.name()
        .equals(hotelAvailabilitiesRequest.getSort()) ? cacheResult.stream()
        .sorted(Comparator.comparingDouble(HotelAvailabilityResponse::getScore).reversed())
        .toList() : cacheResult;
  }

  private void saveToCacheForRecommendationSort(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      HotelAvailabilitiesResponse mergedResponse,
      List<DistanceFromSearchResponse> snowdropHotelList,
      HotelsMigrationStatusResponse migrationStatusResponse,
      Account account,
      List<List<HotelMigrationStatusResponse>> hotels,
      RecommendedSearchModifiers recommModifiers) {

    var allHotelsAvailabilities = new ArrayList<>(mergedResponse.getHotelAvailabilityList());

    if (hotels.size() > 1) {
      var availabilities = getAvailabilityForHotelList(hotelAvailabilitiesRequest,
          snowdropHotelList,
          migrationStatusResponse, hotels.subList(1, hotels.size()), account);

      availabilities.stream()
          .filter(Objects::nonNull)
          .flatMap(avail -> avail.getHotelAvailabilityList().stream())
          .forEach(allHotelsAvailabilities::add);
    }

    mergedResponse.setHotelAvailabilityList(allHotelsAvailabilities);

    applySorting(mergedResponse, Collections.singletonList(hotelAvailabilitiesRequest.getSort()), recommModifiers);

    cacheSearchOutPort.saveOperaHotels(hotelAvailabilitiesRequest, allHotelsAvailabilities);
  }

  private static RecommendedSearchModifiers getRecommendedSearchModifiers(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {


    return new RecommendedSearchModifiers(hotelAvailabilitiesRequest.getRcDistanceModifier(),
            hotelAvailabilitiesRequest.getRcPriceModifier(),
            hotelAvailabilitiesRequest.getRcHubModifier() != null
              ? hotelAvailabilitiesRequest.getRcHubModifier() : DEFAULT_HUB_MODIFIER_VALUE);

  }

  private List<String> getHubHotels(List<DistanceFromSearchResponse> snowdropHotels) {
    return snowdropHotels.stream().filter(hotel -> HUB_BRAND.equals(hotel.getBrand()))
        .map(DistanceFromSearchResponse::getHotelId).toList();
  }

  private void updateOpeningSoonHotels(HotelAvailabilitiesResponse response) {
    var openingSoonHotelIds = getOpeningSoonHotelIds(cacheSearchOutPort, contentServiceOutPort);
    response.getHotelAvailabilityList().forEach(
        hotel -> hotel.setHotelOpeningSoon(openingSoonHotelIds.contains(hotel.getHotelId())));
  }

  private static void convertAndUpdateDistance(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      List<DistanceFromSearchResponse> snowdropHotelList) {
    snowdropHotelList
        .forEach(hotel -> convertDistance(hotel, AvailabilitiesResponseUtils.buildRadiusUnit(
            hotelAvailabilitiesRequest.getRadiusUnit())));
  }

  private void startAsyncAvailabilityRequests(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      List<DistanceFromSearchResponse> snowdropHotelList,
      HotelsMigrationStatusResponse migrationStatusResponse,
      List<List<HotelMigrationStatusResponse>> hotels,
      Account account) {
    for (int i = 1; i < hotels.size(); i++) {
      var currentHotelListIndex = i;
      CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
        HotelAvailabilitiesResponse cacheOperaAvailabilities =
            availabilitiesFromOpera.getOperaHotelsAvailabilities(
                hotelAvailabilitiesRequest,
                account,
                hotels.get(currentHotelListIndex));

        AvailabilitiesResponseUtils.updateAvailabilitiesResponseWithOperaSoldOutHotels(cacheOperaAvailabilities,
            hotels.get(currentHotelListIndex));

        updateHotelsDistanceAndPms(hotelAvailabilitiesRequest, snowdropHotelList,
            migrationStatusResponse, cacheOperaAvailabilities.getHotelAvailabilityList());

        cacheSearchOutPort.saveOperaHotels(hotelAvailabilitiesRequest,
            cacheOperaAvailabilities.getHotelAvailabilityList());
      }));
    }
  }

  private List<HotelAvailabilitiesResponse> getAvailabilityForHotelList(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      List<DistanceFromSearchResponse> snowdropHotelList,
      HotelsMigrationStatusResponse migrationStatusResponse,
      List<List<HotelMigrationStatusResponse>> hotels,
      Account account) {

    var futures = new ArrayList<CompletableFuture<HotelAvailabilitiesResponse>>();

    for (int i = 0; i < hotels.size(); i++) {
      var currentHotelListIndex = i;
      CompletableFuture<HotelAvailabilitiesResponse> future = CompletableFuture.supplyAsync(concurrentTracer.wrap(
          (Supplier<HotelAvailabilitiesResponse>) () ->
              availabilitiesFromOpera.getOperaHotelsAvailabilities(
                  hotelAvailabilitiesRequest,
                  account,
                  hotels.get(currentHotelListIndex))
      ));
      futures.add(future);
    }

    // Combine all futures and wait for them to complete
    List<HotelAvailabilitiesResponse> completeList = futures.stream()
        .map(CompletableFuture::join)
        .toList();

    completeList.forEach(
        availabilities -> {
          AvailabilitiesResponseUtils.updateAvailabilitiesResponseWithOperaSoldOutHotels(availabilities,
              hotels.get(0));

          updateHotelsDistanceAndPms(hotelAvailabilitiesRequest, snowdropHotelList,
              migrationStatusResponse, availabilities.getHotelAvailabilityList());
        }
    );

    return completeList;
  }

  private HotelAvailabilitiesResponse getAvailabilityForFirstPage(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      List<DistanceFromSearchResponse> snowdropHotelList,
      HotelsMigrationStatusResponse migrationStatusResponse,
      List<List<HotelMigrationStatusResponse>> hotels,
      Account account) {

    return getAvailabilityForHotelList(hotelAvailabilitiesRequest, snowdropHotelList,
        migrationStatusResponse, List.of(hotels.get(0)), account).get(0);
  }

  private static void updateHotelsDistanceAndPms(HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
                                                 List<DistanceFromSearchResponse> snowdropHotelList,
                                                 HotelsMigrationStatusResponse migrationStatusResponse,
                                                 List<HotelAvailabilityResponse> hotelAvailabilityResponseList) {
    hotelAvailabilityResponseList.forEach(hotel -> {
      var migratedHotelStatus = migrationStatusResponse
          .getMigrationStatusList().stream()
          .filter(migrate -> hotel.getHotelId().equals(migrate.getHotelId())).findFirst().get();
      DistanceFromSearchResponse distanceFromSearchResponse = snowdropHotelList.stream()
          .filter(snowdrop -> snowdrop.getHotelId().equals(hotel.getHotelId())).findFirst().get();

      hotel.setPmsSource(migratedHotelStatus.getPmsSource());
      hotel.setUnit(hotelAvailabilitiesRequest.getRadiusUnit().name());
      hotel.setDistance(Double.valueOf(distanceFromSearchResponse.getDistance()));
      hotel.setName(distanceFromSearchResponse.getName());

      if (hotel.getHotelOpeningSoon() == null) {
        hotel.setHotelOpeningSoon(false);
      }
    });
  }

  private List<List<HotelMigrationStatusResponse>> partitionHotels(
      List<HotelMigrationStatusResponse> hotelMigrationStatusResponses) {

    List<List<HotelMigrationStatusResponse>> partitions = new ArrayList<>();

    for (int i = 0; i < hotelMigrationStatusResponses.size(); i += MAX_HOTELS_PER_OHIP_CALL) {
      partitions.add(hotelMigrationStatusResponses
          .subList(i, Math.min(i + MAX_HOTELS_PER_OHIP_CALL, hotelMigrationStatusResponses.size())));
    }

    return partitions;
  }

  private List<HotelMigrationStatusResponse> getOperaHotelsLists(
      HotelsMigrationStatusResponse migrationStatusResponse) {

    var operaHotels = migrationStatusResponse.getMigrationStatusList()
        .stream()
        .filter(migrationStatus -> PMS_SOURCE_OPERA.equals(migrationStatus.getPmsSource())
            && migrationStatus.getOnSale())
        .toList();

    log.info("The following Opera migrated hotels={} were found", operaHotels);
    return operaHotels;
  }

  HotelAvailabilitiesResponse getAvailabilitiesFromAvCache(
       HotelAvailabilitiesRequest hotelAvailabilitiesRequest, boolean isValidationRequired) {
    log.info("hotelAvailabilitiesRequest={}", hotelAvailabilitiesRequest);
    boolean isEmptyRoomType = CollectionUtils.isEmpty(hotelAvailabilitiesRequest.getRoomTypes());

    if (OldWorldChannelEnum.WEB.equals(hotelAvailabilitiesRequest.getOldWorldChannel())
        && PI_CHANNEL_ID.equals(hotelAvailabilitiesRequest.getChannel())) {
      rulesAgentValidations.validateBusinessRules(hotelAvailabilitiesRequest, PI_CHANNEL_ID,
          isValidationRequired);
    }

    if (OldWorldChannelEnum.WEB.equals(hotelAvailabilitiesRequest.getOldWorldChannel())
        && DISTR_CHANNEL_ID.equals(hotelAvailabilitiesRequest.getChannel())) {
      rulesAgentValidations.validateBusinessRules(hotelAvailabilitiesRequest, DISTR_CHANNEL_ID,
          isValidationRequired);
    }
    boolean flagMlos = unleashWrapper.isEnabled(unleashWrapper.featureFlag().getShowMlosCcui())
        && hotelAvailabilitiesRequest.getChannel().equalsIgnoreCase(CCUI_CHANNEL_ID);

    HotelAvailabilitiesResponse avCacheResponse;
    if (isEmptyRoomType && PI_CHANNEL_ID.equals(hotelAvailabilitiesRequest.getChannel())) {
      avCacheResponse = getAvCacheResponseWhenRoomTypeEmpty(
                      hotelAvailabilitiesRequest, isValidationRequired, flagMlos);
    } else {
      avCacheResponse = availabilitiesResponseFromAvCache.getFullAvailabilitiesFromAvCache(
              hotelAvailabilitiesRequest, isValidationRequired, flagMlos);
    }

    if (!BB_CHANNEL_ID.equals(hotelAvailabilitiesRequest.getChannel())) {
      // filter by facilities
      if (hotelAvailabilitiesRequest.getFilters() != null
          && !hotelAvailabilitiesRequest.getFilters().isEmpty()) {
        applyFilters(cacheSearchOutPort, contentServiceOutPort, hotelAvailabilitiesRequest, avCacheResponse);
      }

      updateOpeningSoonHotels(avCacheResponse);

      //Sorting
      var recommModifiers = getRecommendedSearchModifiers(hotelAvailabilitiesRequest);
      applySorting(avCacheResponse, Collections.singletonList(hotelAvailabilitiesRequest.getSort()), recommModifiers);

      //Pagination
      applyPagination(avCacheResponse, hotelAvailabilitiesRequest);

    }

    applyOccupancySupplement(hotelAvailabilitiesRequest, avCacheResponse, unleashWrapper, rulesAgentOutPort);

    return avCacheResponse;
  }

  HotelAvailabilitiesResponse getAvCacheResponseWhenRoomTypeEmpty(
          HotelAvailabilitiesRequest hotelAvailabilitiesRequest, boolean isValidationRequired, boolean flagMlos) {
    var allHotelAvRespList = new ArrayList<HotelAvailabilityResponse>();
    var hotelAvailabilitiesResponseList = new ArrayList<HotelAvailabilitiesResponse>();

    var variantsResponses = rulesAgentValidations.retrieveRoomTypesVariants(
                    hotelAvailabilitiesRequest, rulesAgentValidations.getSearchRulesByChannel(
                            hotelAvailabilitiesRequest.getChannel()));

    variantsResponses.parallelStream().forEach(variantsList -> {
      var haReqRoomType = hotelAvailabilitiesRequest.toBuilder().roomTypes(variantsList).build();
      var hotelAvailabilitiesResp = availabilitiesResponseFromAvCache.getFullAvailabilitiesFromAvCache(
              haReqRoomType, isValidationRequired, flagMlos);
      hotelAvailabilitiesResponseList.add(hotelAvailabilitiesResp);
      allHotelAvRespList.addAll(hotelAvailabilitiesResp.getHotelAvailabilityList());
    });

    Map<String, List<HotelAvailabilityResponse>> hotelIdAvailabilitiesMap = new HashMap<>();
    variantsResponses.forEach(variantRespList -> {
      var hotelIdAvailabilitiesFiltered = allHotelAvRespList
              .parallelStream()
              .collect(Collectors.groupingBy(HotelAvailabilityResponse::getHotelId,
              Collectors.mapping(hotelAvailabilityResponse ->
                      hotelAvailabilityResponse, Collectors.toList()
              )));
      hotelIdAvailabilitiesMap.putAll(hotelIdAvailabilitiesFiltered);
    });

    // hotelIdAvailabilitiesMap  ordering by Cost roomRate increasing
    List<HotelAvailabilityResponse> hotelIdAvailabilitiesSelected = new ArrayList<>();
    hotelIdAvailabilitiesMap.forEach((key, value) -> {
      var havListOrdered = value.stream()
              .filter(Objects::nonNull)
              .sorted(Comparator.comparing(
                      availabilityResponse -> {
                        if (availabilityResponse == null) {
                          return null;
                        }
                        Cost cost = availabilityResponse.getLowestRoomRate();
                        return cost == null ? null : cost.getNetTotal();
                      },
                      Comparator.nullsLast(BigDecimal::compareTo)))
              .toList();
      if (!havListOrdered.isEmpty()) {
        hotelIdAvailabilitiesSelected.add(havListOrdered.get(0));
      }
    });

    hotelIdAvailabilitiesSelected.sort(
        Comparator.comparingInt((HotelAvailabilityResponse h) ->
                Boolean.TRUE.equals(h.getAvailable()) ? 0 : 1)
            .thenComparing(h -> {
              Cost cost = h.getLowestRoomRate();
              return cost == null ? null : cost.getNetTotal();
            }, Comparator.nullsLast(BigDecimal::compareTo)));

    HotelAvailabilitiesResponse lowestCostHotelAvailabilities = new HotelAvailabilitiesResponse();
    lowestCostHotelAvailabilities.setHotelAvailabilityList(hotelIdAvailabilitiesSelected);

    // build lowestCostHotelAvailabilities from the first non-null variant response
    HotelAvailabilitiesResponse rep = hotelAvailabilitiesResponseList.stream()
            .filter(Objects::nonNull).findFirst().orElse(null);
    if (rep != null) {
      lowestCostHotelAvailabilities.setPage(rep.getPage());
      lowestCostHotelAvailabilities.setPageSize(rep.getPageSize());
      lowestCostHotelAvailabilities.setTotal(rep.getTotal());
    }
    return lowestCostHotelAvailabilities;
  }
}