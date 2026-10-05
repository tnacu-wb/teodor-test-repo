package uk.co.whitbread.booking.infrastructure.rest.client.basket.service;

import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.infrastructure.rest.client.basket.model.out.BasketForBookingReferencesDto;
import uk.co.whitbread.booking.infrastructure.rest.client.basket.service.properties.BasketProperties;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.exceptions.InternalBasketException;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.in.BaseOperaEmailRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out.ReservationBasketOptionsResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;

@Slf4j
@Component
public class BasketClient {

  public static final String BASKET_EXCEPTION_MSG =
      "An exception was returned by the Basket Service";
  private final WebClient basketWebClient;
  private final BasketProperties basketProperties;

  public BasketClient(@Qualifier("basketWebClient") WebClient basketWebClient,
      BasketProperties basketProperties) {
    this.basketWebClient = basketWebClient;
    this.basketProperties = basketProperties;
  }

  public ReservationBasketOptionsResponseDto getBasketOptions(String basketReference) {

    return basketWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(
                basketProperties.getOperaBasketOptionInformationEndpoint())
            .build(basketReference))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeaderResponse(log, response);
          return response.bodyToMono(InternalBasketException.class);
        })
        .bodyToMono(ReservationBasketOptionsResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while trying to get reservation options with basketReference=%s. %s",
                basketReference, BASKET_EXCEPTION_MSG)))
        .block();
  }

  public ReservationBasketOptionsResponseDto getBasketByBookingReference(String bookingReference) {

    return basketWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(
                basketProperties.getOperaBasketInformationByBookingEndpoint())
            .queryParam("bookingReference", bookingReference)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeaderResponse(log, response);
          return response.bodyToMono(InternalBasketException.class);
        })
        .bodyToMono(ReservationBasketOptionsResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while trying to get reservation options with bookingReference=%s. %s",
                bookingReference, BASKET_EXCEPTION_MSG)))
        .block();
  }

  public ResponseEntity<Void> sendBookingConfirmationOrInvoiceEmail(
      @RequestBody BaseOperaEmailRequestDto emailRequest) {

    return basketWebClient
        .post()
        .uri(basketProperties.getOperaResendEmailEndpoint())
        .body(Mono.just(emailRequest), BaseOperaEmailRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeaderResponse(log, response);
          return response.bodyToMono(InternalBasketException.class);
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while trying to resend email for reservation with basket reference = %s. %s",
                emailRequest.getBookingReference(), BASKET_EXCEPTION_MSG)))
        .block();
  }

  public List<BasketForBookingReferencesDto> getBasketsForBookingReferences(
      Set<String> bookingReferences) {

    return basketWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(
                basketProperties.getGetBasketsForBookingRefs())
            .queryParam("bookingReferences", bookingReferences)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeaderResponse(log, response);
          return response.bodyToMono(InternalBasketException.class);
        })
        .bodyToFlux(BasketForBookingReferencesDto.class)
        .collectList()
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while trying to get reservation options with bookingReference=%s. %s",
                "asd", BASKET_EXCEPTION_MSG)))
        .block();
  }

  public ResponseEntity<BasketDto> sendGetBasketByReference(String bookingReference) {
    return basketWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getOperaBasketInformationByBookingEndpoint())
            .queryParam("bookingReference", bookingReference)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
            log.info("Basket not found for bookingReference={}", bookingReference);
            return Mono.empty();
          }
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(InternalBasketException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(InternalBasketException.class);
        })
        .toEntity(BasketDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while trying to get basket for bookingReference=%s. %s",
                bookingReference, BASKET_EXCEPTION_MSG)))
        .block();
  }

}