package uk.co.whitbread.reservation.infrastructure.rest.client.basket.service;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddAmendedReservationsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddBasketItemRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CancelBasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CcuiPaymentRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ChangeBasketIdContextDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ChangeBasketStatusDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestReservationDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.EmailRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ErroredBookingDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.InitiatePaymentResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PaymentCcuiResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PaymentRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PrepaidDepositsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PrepaidDepositsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ProcessAmendRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromotionsInformationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.RefundRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.RefundResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.UpdateAllowancesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.UpdateBasketItemOccupancyRequestDto;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.reservation.domain.exceptions.InvalidTokenException;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.exceptions.BasketDigitalException;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.exceptions.BasketInternalException;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.properties.BasketProperties;
import uk.co.whitbread.reservation.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;

@Slf4j
@Component
public class BasketClient {

  public static final String BASKET_EXCEPTION_MSG =
      "An exception was returned by the Basket Service";
  public static final String DTAGENT = ":dtagent";
  private static final String AUTHORIZATION = "WB-Authorization";
  private static final String BEARER = "Bearer ";
  private static final String ERROR_WHILE_TRYING_TO_GET_BASKET =
      "Error while trying to get basket by basketReference=%s";
  private static final String ERROR_WHILE_TRYING_TO_INIT_PAYMENT =
      "Error while trying to initiate payment for basketReference=%s";
  private final WebClient basketWebClient;
  private final BasketProperties basketProperties;

  public BasketClient(@Qualifier("basketWebClient") WebClient basketWebClient,
      BasketProperties basketProperties) {
    this.basketWebClient = basketWebClient;
    this.basketProperties = basketProperties;
  }

  public ResponseEntity<BasketDto> sendGetBasketByReference(String bookingReference) {
    return basketWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getBasketByReferenceEndpoint())
            .queryParam("bookingReference", bookingReference)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
            log.info("Basket not found for bookingReference={}", bookingReference);
            return Mono.empty();
          }
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketNotFoundException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toEntity(BasketDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format(ERROR_WHILE_TRYING_TO_GET_BASKET,
                bookingReference)))
        .block();
  }

  public Tuple2<BasketDto, String> createBasket(
      final CreateBasketRequestDto createBasketRequestDto) {
    ResponseEntity<BasketDto> responseEntity = basketWebClient
        .post()
        .uri(basketProperties.getCreateEndpoint())
        .body(Mono.just(createBasketRequestDto), CreateBasketRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toEntity(BasketDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to create basket for hotelId=%s, userId=%s",
                createBasketRequestDto.getHotelId(), createBasketRequestDto.getUserId())))
        .block();

    var basket = getBasketFromBody(responseEntity);
    var eTag = getEtag(responseEntity);

    return Tuples.of(basket, eTag);
  }

  public Tuple2<BasketDto, String> addBasketItems(final String basketReference,
      final String lastModifiedETag,
      final AddBasketItemRequestDto addBasketItemRequestDto) {
    var responseEntity = basketWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getAddItemEndpoint())
            .build(basketReference))
        .header("If-Match", lastModifiedETag)
        .body(Mono.just(addBasketItemRequestDto), AddBasketItemRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toEntity(BasketDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to add basket items for basketReference=%s",
                basketReference)))
        .block();

    var basket = getBasketFromBody(responseEntity);
    var eTag = getEtag(responseEntity);

    return Tuples.of(basket, eTag);
  }

  public Tuple2<BasketDto, String> linkAmendReservationsInBasket(final String basketReference,
      final String lastModifiedETag,
      final AddAmendedReservationsRequestDto amendedReservationsRequestDto) {
    var responseEntity = basketWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getLinkAmendReservations())
            .build(basketReference))
        .header("If-Match", lastModifiedETag)
        .body(Mono.just(amendedReservationsRequestDto), AddAmendedReservationsRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toEntity(BasketDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to add link to original basket reservations"
                + " for basketReference=%s", basketReference)))
        .block();

    var basket = getBasketFromBody(responseEntity);
    var eTag = getEtag(responseEntity);

    return Tuples.of(basket, eTag);
  }

  public RefundResponseDto triggerRefund(String basketReference, RefundRequestDto refundDto) {
    return basketWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getRefundEndpoint())
            .build(basketReference))
        .body(Mono.just(refundDto), RefundRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .bodyToMono(RefundResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to process refund for basketReference=%s",
                basketReference)))
        .block();
  }

  public Tuple2<BasketDto, String> removeItem(String basketReference, String basketItem,
      String lastModifiedETag) {
    var responseEntity = basketWebClient
        .delete()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getRemoveItemEndpoint())
            .build(basketReference, basketItem))
        .header("If-Match", lastModifiedETag)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketNotFoundException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toEntity(BasketDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format(ERROR_WHILE_TRYING_TO_GET_BASKET,
                basketReference)))
        .block();

    var basket = getBasketFromBody(responseEntity);
    var eTag = getEtag(responseEntity);

    return Tuples.of(basket, eTag);
  }

  public Tuple2<BasketDto, String> removeItems(String basketReference, List<String> basketItems,
      String lastModifiedETag) {
    var responseEntity = basketWebClient
        .delete()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getRemoveItemsEndpoint())
            .queryParam("itemIds", basketItems)
            .build(basketReference))
        .header("If-Match", lastModifiedETag)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketNotFoundException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toEntity(BasketDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format(ERROR_WHILE_TRYING_TO_GET_BASKET,
                basketReference)))
        .block();

    var basket = getBasketFromBody(responseEntity);
    var eTag = getEtag(responseEntity);

    return Tuples.of(basket, eTag);
  }

  public Tuple2<BasketDto, String> sendGetBasket(String basketReference) {
    var responseEntity = basketWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getBasketEndpoint())
            .build(basketReference))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketNotFoundException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toEntity(BasketDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format(ERROR_WHILE_TRYING_TO_GET_BASKET, basketReference)))
        .block();

    var basket = getBasketFromBody(responseEntity);
    var eTag = getEtag(responseEntity);

    return Tuples.of(basket, eTag);
  }

  public void sendCancelBasket(final String basketReference,
      final CancelBasketDto cancelBasketDto) {
    basketWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getCancelEndpoint())
            .build(basketReference))
        .body(Mono.just(cancelBasketDto), CancelBasketDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to cancel basket for basketReference=%s",
                basketReference)))
        .block();
  }

  public void setErroredBooking(final String basketReference,
      final ErroredBookingDto erroredBookingDto) {
    basketWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getErroredBookingEndpoint())
            .build(basketReference))
        .body(Mono.just(erroredBookingDto), ErroredBookingDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to cancel basket for basketReference=%s",
                basketReference)))
        .block();
  }

  public String updateAllowances(final String basketReference,
      final UpdateAllowancesRequestDto updateAllowancesRequestDto,
      final String lastModifiedETag) {
    var responseEntity = basketWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getUpdateAllowancesEndpoint())
            .build(basketReference))
        .header("If-Match", lastModifiedETag)
        .body(Mono.just(updateAllowancesRequestDto), UpdateAllowancesRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to update allowances for basketReference=%s",
                basketReference)))
        .block();
    return getEtag(responseEntity);
  }

  public void saveCharges(PrepaidDepositsRequestDto requestDto) {
    basketWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getSaveChargesEndpoint())
            .build())
        .body(Mono.just(requestDto), PrepaidDepositsRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex, "Error while trying to save charges."))
        .block();
  }

  public PrepaidDepositsDto getCharges(String reservationId) {
    return basketWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getChargesByReservationIdEndpoint())
            .build(reservationId))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketNotFoundException.class);
        })
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .bodyToMono(PrepaidDepositsDto.class)
        .onErrorResume(BasketNotFoundException.class, notFound -> Mono.empty())
        .doOnError(ex -> ExceptionLogger.log(log, ex, "Error while trying to get charges."))
        .block();
  }

  public PrepaidDepositsDto getChargesByReservationIds(List<String> reservationIds) {
    return basketWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getChargesByReservationIdsEndpoint())
            .queryParam("reservationIds", reservationIds)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketNotFoundException.class);
        })
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .bodyToMono(PrepaidDepositsDto.class)
        .onErrorResume(BasketNotFoundException.class, notFound -> Mono.empty())
        .doOnError(ex -> ExceptionLogger.log(log, ex, "Error while trying to get charges."))
        .block();
  }

  public void deleteBasket(String basketReference, String lastModifiedETag) {
    basketWebClient
        .delete()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getBasketEndpoint())
            .build(basketReference))
        .header("If-Match", lastModifiedETag)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketNotFoundException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toEntity(Void.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to delete basket by basketReference=%s",
                basketReference)))
        .block();
  }

  public void triggerEmailConfirmation(EmailRequestDto emailRequestDto) {

    basketWebClient
        .post()
        .uri(basketProperties.getEmailConfirmationEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(emailRequestDto), EmailRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketNotFoundException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toEntity(Void.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format(
                "Error while trying to trigger email confirmation for bookingReference=%s",
                emailRequestDto.getBookingReference())))
        .block();
  }

  public InitiatePaymentResponseDto initiatePayment(String basketReference,
      PaymentRequestDto paymentRequestDto) {
    return basketWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getInitiatePaymentEndpoint())
            .build(basketReference))
        .body(Mono.just(paymentRequestDto), PaymentRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .bodyToMono(InitiatePaymentResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format(ERROR_WHILE_TRYING_TO_INIT_PAYMENT, basketReference)))
        .block();
  }

  public void processAmend(String basketReference, ProcessAmendRequestDto processAmendRequestDto) {
    basketWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getProcessAmendEndpoint())
            .build(basketReference))
        .body(Mono.just(processAmendRequestDto), ProcessAmendRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format(ERROR_WHILE_TRYING_TO_INIT_PAYMENT, basketReference)))
        .block();
  }

  public PaymentCcuiResponseDto initiateCcuiPayment(String basketReference,
      CcuiPaymentRequestDto paymentRequestDto) {

    CustomJwtAuthenticationToken customJwt = (CustomJwtAuthenticationToken) SecurityContextHolder.getContext()
        .getAuthentication();

    String token = customJwt != null && customJwt.getToken() != null
        ? BEARER + customJwt.getToken().getTokenValue() : "";

    if (token.isEmpty()) {
      var ex = new InvalidTokenException(ErrorCode.DIGITAL_INVALID_TOKEN3,
          "Invalid token. Error while trying to initiate ccui payment for basketReference. "
              + "Authorization role missing for CCUI.");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    return basketWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getInitiateCcuiPaymentProcess())
            .build(basketReference))
        .body(Mono.just(paymentRequestDto), CcuiPaymentRequestDto.class)
        .header(AUTHORIZATION, token)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .bodyToMono(PaymentCcuiResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format(ERROR_WHILE_TRYING_TO_INIT_PAYMENT, basketReference)))
        .block();
  }

  private static <T> String getEtag(ResponseEntity<T> responseEntity) {
    String eTag = Optional.ofNullable(responseEntity)
        .map(ResponseEntity::getHeaders)
        .map(HttpHeaders::getETag)
        .orElseThrow(() -> {
          var exception = new BasketDigitalException(
              ErrorCode.DIGITAL_EXTRACT_ETAG_HEADERS_EXCEPTION,
              "Error while trying to extract eTag from headers");
          ExceptionLogger.log(log, exception);
          throw exception;
        })
        .replace("\"", "");

    return eTag.contains(DTAGENT) ? eTag.substring(0, eTag.indexOf(DTAGENT)) : eTag;
  }

  private static BasketDto getBasketFromBody(ResponseEntity<BasketDto> responseEntity) {
    return Optional.ofNullable(responseEntity)
        .map(ResponseEntity::getBody)
        .orElseThrow(() -> {
          var exception = new BasketDigitalException(
              ErrorCode.DIGITAL_EXTRACT_BASKET_BODY_EXCEPTION,
              "Error while trying to extract basket from body");
          ExceptionLogger.log(log, exception);
          throw exception;
        });
  }

  public String updateOccupancySupplement(String basketRef,
                                          UpdateBasketItemOccupancyRequestDto updateRequest,
                                          String lastModifiedETag) {
    var responseEntity = basketWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getUpdateOccupancySupplementEndpoint())
            .build(basketRef))
        .header("If-Match", lastModifiedETag)
        .body(Mono.just(updateRequest), UpdateBasketItemOccupancyRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toBodilessEntity()
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to update occupancy supplement for basketReference=%s",
                basketRef)))
        .block();
    return getEtag(responseEntity);
  }

  public BasketDto changeStatus(String bookingRef, ChangeBasketStatusDto changeStatusDto) {
    return basketWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getChangeStatusEndpoint())
            .build(bookingRef))
        .body(Mono.just(changeStatusDto), ChangeBasketStatusDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .bodyToMono(BasketDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to update basket status for bookingRef=%s",
                bookingRef)))
        .block();
  }

  public Tuple2<BasketDto, String> addPromotionToBasket(String basketReference,
      PromotionsInformationRequestDto promotionsInformationRequest, String lastModifiedETag) {
    var responseEntity = basketWebClient
        .put()
        .uri(
            uriBuilder -> uriBuilder.path(basketProperties.getAddPromotionToBasketEndpoint())
                .build(basketReference))
        .body(Mono.just(promotionsInformationRequest), PromotionsInformationRequestDto.class)
        .header("If-Match", lastModifiedETag)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketNotFoundException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toEntity(BasketDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format(ERROR_WHILE_TRYING_TO_GET_BASKET,
                basketReference)))
        .block();

    var basket = getBasketFromBody(responseEntity);
    var eTag = getEtag(responseEntity);

    return Tuples.of(basket, eTag);
  }

  public BasketDto changeIdContext(String bookingRef, ChangeBasketIdContextDto changeIdContextDto) {
    return basketWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getChangeIdContextEndpoint())
            .build(bookingRef))
        .body(Mono.just(changeIdContextDto), ChangeBasketIdContextDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .bodyToMono(BasketDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to update basket idContext for bookingRef=%s",
                bookingRef)))
        .block();
  }

  public BasketDto createBasketReservation(
      final CreateBasketRequestReservationDto createBasketRequestReservationDto) {
    ResponseEntity<BasketDto> responseEntity = basketWebClient
        .post()
        .uri(basketProperties.getCreateReservationEndpoint())
        .body(Mono.just(createBasketRequestReservationDto), CreateBasketRequestReservationDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .toEntity(BasketDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to create basket for hotelId=%s, userId=%s",
                createBasketRequestReservationDto.getHotelId(),
                createBasketRequestReservationDto.getUserInfoDto().getUserId())))
        .block();
    return getBasketFromBody(responseEntity);

  }

}


