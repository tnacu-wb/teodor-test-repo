package uk.co.whitbread.domain.logic;

import static uk.co.whitbread.domain.exceptions.ErrorCode.DIGITAL_NO_HOTELS_FOUND_2_EXCEPTION;
import static uk.co.whitbread.domain.exceptions.ErrorCode.DIGITAL_NO_HOTELS_FOUND_EXCEPTION;
import static uk.co.whitbread.domain.logic.AvailabilitiesResponseUtils.applyFilters;
import static uk.co.whitbread.domain.logic.AvailabilitiesResponseUtils.applyPagination;
import static uk.co.whitbread.domain.logic.AvailabilitiesResponseUtils.applySorting;
import static uk.co.whitbread.domain.logic.AvailabilitiesResponseUtils.checkNumberOfNightsCcui;
import static uk.co.whitbread.domain.logic.AvailabilitiesResponseUtils.getOpeningSoonHotelIds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.domain.exceptions.InvalidChannelException;
import uk.co.whitbread.domain.exceptions.NoResultsFoundException;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.in.OldWorldChannelEnum;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.domain.ports.primary.HotelAvailabilitiesInPort;
import uk.co.whitbread.domain.ports.secondary.CacheSearchOutPort;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Slf4j
@RequiredArgsConstructor
public class HotelAvailabilitiesInPortImpl implements HotelAvailabilitiesInPort {

  private static final String PI_CHANNEL_ID = "PI";
  private static final String CCUI_CHANNEL_ID = "CCUI";
  private static final String BB_CHANNEL_ID = "BB";
  private static final String NO_HOTELS_FOUND = "No hotels found.";
  private final AvailabilitiesResponseFromAvCache availabilitiesResponseFromAvCache;
  private final AvailabilitiesResponseFromOpera availabilitiesResponseFromOpera;
  private final RulesAgentValidations rulesAgentValidations;
  private final CacheSearchOutPort cacheSearchOutPort;
  private final ContentServiceOutPort contentServiceOutPort;
  private final AuthenticatedUserService authenticatedUserService;
  private final ConcurrentTracer concurrentTracer;

  @Override
  public HotelAvailabilitiesResponse getAvailabilities(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {

    return switch (hotelAvailabilitiesRequest.getChannel()) {
      case PI_CHANNEL_ID -> getAvailabilitiesPi(hotelAvailabilitiesRequest);
      case CCUI_CHANNEL_ID -> getAvailabilitiesCcui(hotelAvailabilitiesRequest);
      case BB_CHANNEL_ID -> getAvailabilitiesBb(hotelAvailabilitiesRequest);
      default -> {
        var message = "Invalid channel received: " + hotelAvailabilitiesRequest.getChannel();
        var exception = new InvalidChannelException(ErrorCode.DIGITAL_INVALID_CHANNEL_EXCEPTION,
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

  private HotelAvailabilitiesResponse getAvailabilitiesCcui(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    rulesAgentValidations.validateBusinessRules(hotelAvailabilitiesRequest,
        hotelAvailabilitiesRequest.getChannel(), true);

    return Boolean.TRUE.equals(checkNumberOfNightsCcui(hotelAvailabilitiesRequest)) ? getAvailabilitiesFromOpera(
        hotelAvailabilitiesRequest, null)
        : getAvailabilitiesFromAvCache(hotelAvailabilitiesRequest, false);
  }

  private HotelAvailabilitiesResponse getAvailabilitiesBb(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest) {
    rulesAgentValidations.validateBusinessRules(hotelAvailabilitiesRequest,
        hotelAvailabilitiesRequest.getChannel(), true);

    var authenticationInfo = authenticatedUserService.getCurrentUserAccount()
        .orElseThrow(() -> new AccessDeniedException("Not allowed to search in BB context"));
    var authorizedHaRequest = hotelAvailabilitiesRequest.toBuilder()
        .authorization(authenticatedUserService.getAuthenticatedUser().getToken().getTokenValue())
        .build();

    CompletableFuture<HotelAvailabilitiesResponse> operaAvailabilities =
        CompletableFuture.supplyAsync(concurrentTracer.wrap((Supplier<HotelAvailabilitiesResponse>)
            () -> getAvailabilitiesFromOpera(authorizedHaRequest, authenticationInfo)));

    List<CompletableFuture<HotelAvailabilitiesResponse>> availabilities = new ArrayList<>();
    availabilities.add(operaAvailabilities);

    CompletableFuture<HotelAvailabilitiesResponse>[] array =
        new CompletableFuture[availabilities.size()];
    List<HotelAvailabilitiesResponse> results = new ArrayList<>();
    CompletableFuture<List<HotelAvailabilitiesResponse>> parralelResults =
        CompletableFuture.allOf(availabilities.toArray(array)).thenApply(future -> {
          availabilities.forEach(f -> results.add(f.join()));
          return results;
        });

    HotelAvailabilitiesResponse mergedResponse = new HotelAvailabilitiesResponse();
    mergedResponse.setTotal(0);

    List<HotelAvailabilityResponse> availableHotels;

    try {
      availableHotels = AvailabilitiesResponseUtils.sanitizeResponseForBb(parralelResults.get());
    } catch (InterruptedException e) {
      log.error("Execution interrupted {}", e.getMessage(), e);
      Thread.currentThread().interrupt();
      throw new NoResultsFoundException(DIGITAL_NO_HOTELS_FOUND_EXCEPTION, NO_HOTELS_FOUND);
    } catch (ExecutionException e) {
      log.error("Execution exception {}", e.getMessage(), e);
      throw new NoResultsFoundException(DIGITAL_NO_HOTELS_FOUND_2_EXCEPTION, NO_HOTELS_FOUND);
    }

    mergedResponse.setHotelAvailabilityList(availableHotels);
    mergedResponse.setTotal(availableHotels.size());

    log.info("Merged Response is before filter, sort and pagination {}", mergedResponse);

    //Filtering
    applyFilters(cacheSearchOutPort, contentServiceOutPort, hotelAvailabilitiesRequest, mergedResponse);

    var openingSoonHotelIds = getOpeningSoonHotelIds(cacheSearchOutPort, contentServiceOutPort);
    mergedResponse.getHotelAvailabilityList().forEach(
        hotel -> hotel.setHotelOpeningSoon(openingSoonHotelIds.contains(hotel.getHotelId())));

    //Sorting
    applySorting(mergedResponse, Collections.singletonList(hotelAvailabilitiesRequest.getSort()));

    //Pagination
    applyPagination(mergedResponse, hotelAvailabilitiesRequest);

    log.info("Merged Response is final {}", mergedResponse);

    return mergedResponse;
  }

  private HotelAvailabilitiesResponse getAvailabilitiesFromAvCache(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest, boolean isValidationRequired) {
    log.info("hotelAvailabilitiesRequest={}", hotelAvailabilitiesRequest);

    if (OldWorldChannelEnum.WEB.equals(hotelAvailabilitiesRequest.getOldWorldChannel())
        && PI_CHANNEL_ID.equals(hotelAvailabilitiesRequest.getChannel())) {
      rulesAgentValidations.validateBusinessRules(hotelAvailabilitiesRequest, PI_CHANNEL_ID,
          isValidationRequired);
    }

    var avCacheResponse = availabilitiesResponseFromAvCache.getFullAvailabilitiesFromAvCache(
        hotelAvailabilitiesRequest, isValidationRequired, false);

    if (!BB_CHANNEL_ID.equals(hotelAvailabilitiesRequest.getChannel())) {
      // filter by facilities
      if (hotelAvailabilitiesRequest.getFilters() != null
          && !hotelAvailabilitiesRequest.getFilters().isEmpty()) {
        applyFilters(cacheSearchOutPort, contentServiceOutPort, hotelAvailabilitiesRequest, avCacheResponse);
      }

      var openingSoonHotelIds = getOpeningSoonHotelIds(cacheSearchOutPort, contentServiceOutPort);
      avCacheResponse.getHotelAvailabilityList().forEach(
          hotel -> hotel.setHotelOpeningSoon(openingSoonHotelIds.contains(hotel.getHotelId())));

      //Sorting
      applySorting(avCacheResponse,
          Collections.singletonList(hotelAvailabilitiesRequest.getSort()));

      //Pagination
      applyPagination(avCacheResponse, hotelAvailabilitiesRequest);

    }
    return avCacheResponse;
  }

  private HotelAvailabilitiesResponse getAvailabilitiesFromOpera(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      Account authenticationInfo) {

    String operaCompanyId = null;
    if (authenticationInfo != null) {
      operaCompanyId = authenticationInfo.getOperaCompanyId();
    }
    var ohipAdapterResponse =
        availabilitiesResponseFromOpera.getFullAvailabilitiesFromOpera(hotelAvailabilitiesRequest,
            operaCompanyId);

    // filter by facilities
    if (hotelAvailabilitiesRequest.getFilters() != null && !hotelAvailabilitiesRequest.getFilters()
        .isEmpty()) {
      applyFilters(cacheSearchOutPort, contentServiceOutPort, hotelAvailabilitiesRequest,
          ohipAdapterResponse);
    }

    var openingSoonHotelIds = getOpeningSoonHotelIds(cacheSearchOutPort, contentServiceOutPort);
    ohipAdapterResponse.getHotelAvailabilityList().forEach(
        hotel -> hotel.setHotelOpeningSoon(openingSoonHotelIds.contains(hotel.getHotelId())));

    if (!BB_CHANNEL_ID.equals(hotelAvailabilitiesRequest.getChannel())) {

      //Sorting
      applySorting(ohipAdapterResponse,
          Collections.singletonList(hotelAvailabilitiesRequest.getSort()));

      //Pagination
      applyPagination(ohipAdapterResponse, hotelAvailabilitiesRequest);
    }
    return ohipAdapterResponse;
  }
}





