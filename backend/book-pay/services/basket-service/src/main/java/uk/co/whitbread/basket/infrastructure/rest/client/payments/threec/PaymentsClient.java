package uk.co.whitbread.basket.infrastructure.rest.client.payments.threec;

import java.time.Duration;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.exception.PaymentInvalidException;
import uk.co.whitbread.basket.domain.exception.PaymentProcessingException;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.generated.models.payments.GenericErrorResponseDto;
import uk.co.whitbread.basket.generated.models.payments.PaymentRequestDto;
import uk.co.whitbread.basket.generated.models.payments.PaymentResponseDto;
import uk.co.whitbread.basket.generated.models.payments.TokenRefundRequestDto;
import uk.co.whitbread.basket.generated.models.payments.TokenRefundResponseDto;
import uk.co.whitbread.basket.generated.models.payments.UpdateTokenRequestDto;
import uk.co.whitbread.basket.generated.models.payments.UpdateTokenResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.threec.properties.RefundRequestProcessorProperties;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.threec.properties.ThreecProperties;
import uk.co.whitbread.basket.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Component
@Slf4j
public class PaymentsClient {

  private static final String IMPROPER_CALL_OF_PAYMENT_SERVICE = "Improper call of Payment Service %s";
  private static final String PAYMENT_SERVICE_ERROR = "An error was returned calling the Payment service: %s";
  public static final String ERROR_CODE_PAYPAL_0 = "PAYPAL0";
  public static final String ERROR_CODE_PAYPAL_1 = "PAYPAL1";
  public static final String ERROR_CODE_PAYPAL_2 = "PAYPAL2";
  private final WebClient paymentsWebClient;
  private final WebClient rrpWebClient;
  private final ThreecProperties threecProperties;
  private final RefundRequestProcessorProperties refundProperties;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public PaymentsClient(
      @Qualifier("paymentsWebClient") WebClient paymentsWebClient,
      @Qualifier("rrpWebClient") WebClient rrpWebClient,
      ThreecProperties threecProperties, RefundRequestProcessorProperties refundProperties,
      UnleashWrapper<FeatureFlag> unleashWrapper) {
    this.paymentsWebClient = paymentsWebClient;
    this.rrpWebClient = rrpWebClient;
    this.threecProperties = threecProperties;
    this.refundProperties = refundProperties;
    this.unleashWrapper = unleashWrapper;
  }

  public PaymentResponseDto getPaymentConfirmation(String paymentId) {
    return getPaymentConfirmation(paymentsWebClient, paymentId);
  }

  private PaymentResponseDto getPaymentConfirmation(WebClient webClient, String paymentId) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path(
            StringUtils.joinWith("/", threecProperties.getPaymentsEndpoint(),
                "{paymentId}")).build(paymentId))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new PaymentInvalidException(ErrorCode.GET_PAYMENT_CLIENT_EXCEPTION,
                  String.format(IMPROPER_CALL_OF_PAYMENT_SERVICE,
                      response.statusCode())));
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new PaymentException(ErrorCode.GET_PAYMENT_SERVER_EXCEPTION,
              String.format(
                  "An error occurred calling the 3CP service to get the payment confirmation %s",
                  response.statusCode())));
        })
        .bodyToMono(PaymentResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format("Error while trying to get payment confirmation for paymentId=%s",
                paymentId)))
        .block();
  }

  public PaymentResponseDto createMitCcPayment(PaymentRequestDto paymentRequest) {
    var req = paymentRequest != null && paymentRequest.getBooking().getBookingReference() != null
        ? paymentRequest.getBooking().getBookingReference() : null;
    return paymentsWebClient
        .post()
        .uri(uriBuilder ->
            uriBuilder.path(threecProperties.getPaymentsEndpoint()).build())
        .body(Mono.just(paymentRequest), PaymentRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(PaymentProcessingException.class);
        })
        .bodyToMono(PaymentResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format("Error while trying to create payment for paymentRequest=%s", req)))
        .block();
  }

  public PaymentResponseDto createPayment(PaymentRequestDto paymentRequest) {
    return createPayment(paymentsWebClient, paymentRequest);
  }

  private PaymentResponseDto createPayment(WebClient webClient, PaymentRequestDto paymentRequest) {
    return webClient
        .post()
        .uri(uriBuilder ->
            uriBuilder.path(threecProperties.getPaymentsEndpoint()).build())
        .body(Mono.just(paymentRequest), PaymentRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new PaymentInvalidException(ErrorCode.CREATE_PAYMENT_CLIENT_EXCEPTION,
                  String.format(IMPROPER_CALL_OF_PAYMENT_SERVICE, response.statusCode())));
        })
        .onStatus(HttpStatusCode::is5xxServerError,
            response -> {
              if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getPaypalErrorMapping())) {
                return response.bodyToMono(GenericErrorResponseDto.class)
                    .defaultIfEmpty(new GenericErrorResponseDto())
                    .flatMap(errorBody -> {
                      WebClientUtils.logErrorResponse(log, response);
                      return Mono.error(getPaymentException(errorBody, response.statusCode()));
                    });
              } else {
                WebClientUtils.logErrorResponse(log, response);
                return Mono.error(
                    new PaymentException(ErrorCode.CREATE_PAYMENT_SERVER_EXCEPTION,
                        String.format(PAYMENT_SERVICE_ERROR,
                            response.statusCode())));
              }
            })
        .bodyToMono(PaymentResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format("Error while trying to create payment for paymentRequest=%s",
                paymentRequest.getBooking().getBookingReference())))
        .block();
  }

  private Exception getPaymentException(GenericErrorResponseDto errorResponse, HttpStatusCode statusCode) {
    String errorCode = Optional.ofNullable(errorResponse.getErrorCode()).orElse(StringUtils.EMPTY);
    String errorMessage = Optional.ofNullable(errorResponse.getMessage()).orElse(StringUtils.EMPTY);
    ErrorCode error = switch (errorCode) {
      case ERROR_CODE_PAYPAL_0 -> ErrorCode.PAYPAL_CREATE_CUSTOMER_EXCEPTION;
      case ERROR_CODE_PAYPAL_1 -> ErrorCode.PAYPAL_TIMEOUT_EXCEPTION;
      case ERROR_CODE_PAYPAL_2 -> ErrorCode.PAYPAL_DECLINE_EXCEPTION;
      default -> {
        errorMessage = String.format(PAYMENT_SERVICE_ERROR, statusCode);
        yield ErrorCode.CREATE_PAYMENT_SERVER_EXCEPTION;
      }
    };
    return new PaymentException(error, errorMessage);
  }

  public UpdateTokenResponseDto updateToken(UpdateTokenRequestDto updateTokenRequestDto) {
    return updateToken(paymentsWebClient, updateTokenRequestDto);
  }

  private UpdateTokenResponseDto updateToken(WebClient webClient,
      UpdateTokenRequestDto updateTokenRequestDto) {
    return webClient
        .put()
        .uri(threecProperties.getTokensEndpoint())
        .body(Mono.just(updateTokenRequestDto), UpdateTokenRequestDto.class)
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            response -> Mono.error(
                new PaymentInvalidException(ErrorCode.TOKEN_PAYMENT_CLIENT_EXCEPTION,
                    String.format("Improper call of Payment Service for token update %s",
                        response.statusCode()))
            ))
        .onStatus(
            HttpStatusCode::is5xxServerError,
            response -> Mono.error(
                new PaymentException(ErrorCode.TOKEN_PAYMENT_SERVER_EXCEPTION,
                    String.format(
                        "An error was returned calling the Payment service for card token: %s",
                        response.statusCode())))
        )
        .bodyToMono(UpdateTokenResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to update card token for paymentRequest"))
        .block();
  }

  public TokenRefundResponseDto sendPartialRefund(TokenRefundRequestDto tokenRefundRequest) {

    return rrpWebClient
        .post()
        .uri(uriBuilder ->
            uriBuilder.path(refundProperties.getTokenRefundEndpoint()).build())
        .body(Mono.just(tokenRefundRequest), TokenRefundRequestDto.class)
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            response -> {
              WebClientUtils.logErrorResponse(log, response);
              return Mono.error(
                  new PaymentInvalidException(ErrorCode.TOKEN_REFUND_CLIENT_EXCEPTION,
                      String.format(IMPROPER_CALL_OF_PAYMENT_SERVICE, response.statusCode())));
            }
        )
        .onStatus(
            HttpStatusCode::is5xxServerError,
            response -> {
              WebClientUtils.logErrorResponse(log, response);
              return Mono.error(
                  new PaymentException(ErrorCode.TOKEN_REFUND_SERVER_EXCEPTION,
                      String.format(PAYMENT_SERVICE_ERROR, response.statusCode())));
            }
        )
        .bodyToMono(TokenRefundResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to create payment for paymentRequest"))
        .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
            .filter(throwable -> throwable instanceof PaymentException)
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
              throw new PaymentException(ErrorCode.PAYMENT_SERVICE_EXCEPTION,
                  "Payment service failed to process after max retries.");
            }))
        .block();
  }

}

