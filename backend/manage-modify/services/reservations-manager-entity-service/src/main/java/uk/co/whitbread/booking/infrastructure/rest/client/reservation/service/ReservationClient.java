package uk.co.whitbread.booking.infrastructure.rest.client.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.exceptions.InternalBasketException;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.CancelReservationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.ReservationBookingInfoRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.ReservationCancelRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.CancelReservationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationAllowancesDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationCancelInfoResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.service.properties.ReservationProperties;
import uk.co.whitbread.booking.infrastructure.rest.utils.WebClientUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationClient {

  private static final String HOTEL_RESERVATION_EXCEPTION_MSG
      = "An exception was returned by Hotel Reservation Entity Service";
  private static final String WB_AUTHORIZATION = "WB-Authorization";
  private static final String APPLICATION_JSON = "application/json";
  private static final String CONTENT_TYPE = "Content-Type";

  private final WebClient reservationWebClient;
  private final ReservationProperties reservationProperties;

  public ReservationResponseDto getReservationInformationAuth(
      @RequestHeader(WB_AUTHORIZATION) String authorization,
      String bookingReference) {

    return reservationWebClient
        .get()
        .uri(
            uriBuilder -> uriBuilder.path(reservationProperties.getOperaBasketInformationAuthEndpoint())
                .build(bookingReference))
        .headers(httpHeaders -> httpHeaders.set(CONTENT_TYPE, APPLICATION_JSON))
        .headers(httpHeaders -> httpHeaders.set(WB_AUTHORIZATION, authorization))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeaderResponse(log, response);
          return response.bodyToMono(InternalBasketException.class);
        })
        .bodyToMono(ReservationResponseDto.class)
        .doOnError(e -> log.error(
            String.format("Error while trying to get reservation by bookingReference=%s. %s",
                bookingReference, HOTEL_RESERVATION_EXCEPTION_MSG)))
        .block();
  }

  public ReservationResponseDto getReservationInformation(
      String bookingReference) {

    return reservationWebClient
        .get()
        .uri(
            uriBuilder -> uriBuilder.path(reservationProperties.getOperaBasketInformationEndpoint())
                .build(bookingReference))
        .headers(httpHeaders -> httpHeaders.set(CONTENT_TYPE, APPLICATION_JSON))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeaderResponse(log, response);
          return response.bodyToMono(InternalBasketException.class);
        })
        .bodyToMono(ReservationResponseDto.class)
        .doOnError(e -> log.error(
            String.format("Error while trying to get reservation by bookingReference=%s. %s",
                bookingReference, HOTEL_RESERVATION_EXCEPTION_MSG)))
        .block();
  }

  public ReservationCancelInfoResponseDto getCancelReservationInformation(
      @RequestHeader(WB_AUTHORIZATION) String authorization,
      ReservationCancelRequestDto reservationCancelRequestDto) {

    return reservationWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(
                reservationProperties.getOperaCancelReservationInformationEndpoint())
            .queryParam("hotelId", reservationCancelRequestDto.getHotelId())
            .queryParam("basketReference", reservationCancelRequestDto.getBasketReference())
            .queryParam("userDateTime", reservationCancelRequestDto.getUserDateTime())
            .queryParam("country", reservationCancelRequestDto.getCountry())
            .queryParam("language", reservationCancelRequestDto.getLanguage())
            .queryParam("channel", reservationCancelRequestDto.getChannel())
            .queryParam("subchannel", reservationCancelRequestDto.getSubchannel())
            .queryParam("token", reservationCancelRequestDto.getToken())
            .build())
        .headers(httpHeaders -> httpHeaders.set(CONTENT_TYPE, APPLICATION_JSON))
        .headers(httpHeaders -> httpHeaders.set(WB_AUTHORIZATION, authorization))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeaderResponse(log, response);
          return response.bodyToMono(InternalBasketException.class);
        })
        .bodyToMono(ReservationCancelInfoResponseDto.class)
        .doOnError(e -> log.error(
            String.format("Error while trying to get reservation cancel information. %s",
                HOTEL_RESERVATION_EXCEPTION_MSG)))
        .block();
  }

  public ReservationAllowancesDto getDinnerAllowances(String basketReference) {

    return reservationWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(reservationProperties.getOperaDinnerAllowanceEndpoint())
            .build(basketReference))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeaderResponse(log, response);
          return response.bodyToMono(InternalBasketException.class);
        })
        .bodyToMono(ReservationAllowancesDto.class)
        .doOnError(e -> log.error(
            "Error while trying to get reservation token ", e))
        .doOnError(e -> log.error(
            String.format("Error while trying to get reservation token . %s",
                HOTEL_RESERVATION_EXCEPTION_MSG)))
        .block();
  }

  public CancelReservationResponseDto cancelReservation(
      @RequestHeader(WB_AUTHORIZATION) String authorization,
      @RequestBody CancelReservationRequestDto cancelReservationRequestDto) {

    log.info("Cancelreservationrequest {}", cancelReservationRequestDto);

    return reservationWebClient
        .post()
        .uri(reservationProperties.getOperaCancelReservationEndpoint())
        .headers(httpHeaders -> httpHeaders.set(WB_AUTHORIZATION, authorization))
        .body(Mono.just(cancelReservationRequestDto), CancelReservationRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeaderResponse(log, response);
          return response.bodyToMono(InternalBasketException.class);
        })
        .bodyToMono(CancelReservationResponseDto.class)
        .doOnError(e -> log.error(
            String.format("Error while trying to cancel reservation with basket reference = %s. %s",
                cancelReservationRequestDto.getBasketReference(), HOTEL_RESERVATION_EXCEPTION_MSG)))
        .block();
  }

  public Object findBooking(ReservationBookingInfoRequestDto requestDto) {
    return reservationWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(reservationProperties.getOperaFindBookingEndpoint())
            .queryParams(requestDto.toMultiValueMap())
            .build()
        )
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeaderResponse(log, response);
          return response.bodyToMono(InternalBasketException.class);
        })
        .bodyToMono(Object.class)
        .doOnError(e -> log.error(
            String.format("Error while trying to find booking with booking reference = %s. %s",
                requestDto.getResNo(), HOTEL_RESERVATION_EXCEPTION_MSG))
        )
        .block();
  }
}
