package uk.co.whitbread.payments.infrastructure.rest.client.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.HotelReservationException;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.DepositsResponseDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.ReservationListDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.service.properties.ReservationClientProperties;
import uk.co.whitbread.payments.infrastructure.rest.client.util.CacheHelper;
import uk.co.whitbread.payments.infrastructure.rest.client.util.WebClientUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationClient {

  public static final String CACHE_NAME = "ReservationsByBasketRef";

  private final WebClient reservationWebClient;
  private final ReservationClientProperties reservationClientProperties;
  private final CacheHelper cacheHelper;

  public DepositsResponseDto getDepositFolios(String hotelId, String reservationId) {
    return reservationWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(reservationClientProperties.getReservationDeposits())
            .queryParam("hotelId", hotelId)
            .queryParam("reservationId", reservationId)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .bodyToMono(DepositsResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format("Error while trying to get deposit folios by reservationid=%s",
                reservationId)))
        .block();
  }

  private ReservationListDto fetchReservations(String basketReference) {
    return reservationWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(reservationClientProperties.getReservationEndpoint())
            .build(basketReference))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .bodyToMono(ReservationListDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
                String.format("Error while trying to get reservation by basketReference=%s",
            basketReference)))
        .block();
  }

  @CachePut(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager30MinutesNoTyping",
      value = CACHE_NAME)
  public ReservationListDto findReservations(String basketReference) {
    return fetchReservations(basketReference);
  }

  public ReservationListDto getCachedReservations(String basketReference) {
    ReservationListDto cachedReservations = cacheHelper.getCacheValue(CACHE_NAME, basketReference,
        ReservationListDto.class);
    if (cachedReservations != null) {
      log.debug("Cache hit for basketReference={}", basketReference);
      return cachedReservations;
    }
    log.debug("Cache miss for basketReference={}", basketReference);
    return fetchReservations(basketReference);
  }
}
