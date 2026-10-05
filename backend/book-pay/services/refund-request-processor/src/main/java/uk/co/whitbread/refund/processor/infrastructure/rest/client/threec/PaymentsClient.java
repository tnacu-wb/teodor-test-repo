package uk.co.whitbread.refund.processor.infrastructure.rest.client.threec;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.refund.processor.domain.model.in.TokenRefund;
import uk.co.whitbread.refund.processor.generated.models.payments.PaymentResponseDto;
import uk.co.whitbread.refund.processor.generated.models.payments.RefundResponseDto;
import uk.co.whitbread.refund.processor.infrastracture.rest.util.WebClientUtils;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.exception.ErrorCode;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.exception.InvalidPaymentException;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.exception.PaymentException;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.exception.RefundException;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.threec.properties.ThreecProperties;

@Component
@Slf4j
public class PaymentsClient {

  private final WebClient paymentsWebClient;

  private final ThreecProperties threecProperties;
  public static final String PAYMENT_PATH_PARAM = "{paymentId}";
  public static final String TRANSACTIONAL_REFUND_PATH = "/refund";

  public PaymentsClient(
      @Qualifier("paymentsWebClient") WebClient paymentsWebClient,
      ThreecProperties threecProperties) {
    this.paymentsWebClient = paymentsWebClient;
    this.threecProperties = threecProperties;
  }

  public Void sendFullRefund(String paymentId) {
    return sendFullRefund(paymentsWebClient, paymentId);
  }

  private Void sendFullRefund(WebClient webClient, String paymentId) {
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(
            StringUtils.joinWith("/", threecProperties.getPaymentsEndpoint(),
                PAYMENT_PATH_PARAM, TRANSACTIONAL_REFUND_PATH)).build(paymentId))
        .header(threecProperties.getThreecKey(), threecProperties.getThreecValue())
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            response -> {
              WebClientUtils.logErrorResponse(log, response);
              return Mono.error(
                  new InvalidPaymentException(ErrorCode.SEND_FULL_REFUND_INVALID_PAYMENT_EXCEPTION,
                      "Improper call of Payment Service"));
            })
        .onStatus(
            HttpStatusCode::is5xxServerError,
            response -> {
              WebClientUtils.logErrorResponse(log, response);
              return Mono.error(
                  new RefundException(ErrorCode.SEND_FULL_REFUND_EXCEPTION,
                      "An error was returned calling the Payment service."));
            })
        .bodyToMono(Void.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public PaymentResponseDto getPaymentResponse(String paymentId) {
    return getPaymentResponse(paymentsWebClient, paymentId);
  }

  private PaymentResponseDto getPaymentResponse(WebClient webClient, String paymentId) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path(
            StringUtils.joinWith("/", threecProperties.getPaymentsEndpoint(),
                PAYMENT_PATH_PARAM)).build(paymentId))
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            response -> {
              WebClientUtils.logErrorResponse(log, response);
              return Mono.error(
                  new InvalidPaymentException(ErrorCode.INVALID_PAYMENT_RESPONSE_EXCEPTION,
                      "Improper call of Payment Service"));
            })
        .onStatus(
            HttpStatusCode::is5xxServerError,
            response -> {
              WebClientUtils.logErrorResponse(log, response);
              return Mono.error(
                  new PaymentException(ErrorCode.PAYMENT_RESPONSE_EXCEPTION,
                      "An error was returned calling the Payment service"));
            }
        )
        .bodyToMono(PaymentResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public RefundResponseDto sendTokenRefund(TokenRefund tokenRefund) {
    return sendTokenRefund(paymentsWebClient, tokenRefund);
  }


  private RefundResponseDto sendTokenRefund(WebClient webClient, TokenRefund tokenRefund) {
    return webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(threecProperties.getRefundsEndpoint()).build())
        .body(Mono.just(tokenRefund), TokenRefund.class)
        .header(threecProperties.getThreecKey(), threecProperties.getThreecValue())
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            response -> {
              WebClientUtils.logErrorResponse(log, response);
              return Mono.error(
                  new InvalidPaymentException(ErrorCode.TOKEN_REFUND_INVALID_EXCEPTION,
                      "Improper call of Payment Service."));
            }
        )
        .onStatus(
            HttpStatusCode::is5xxServerError,
            response -> {
              WebClientUtils.logErrorResponse(log, response);
              return Mono.error(
                  new RefundException(ErrorCode.TOKEN_REFUND_EXCEPTION,
                      "An error was returned calling the Payment service."));
            }
        )
        .bodyToMono(RefundResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}
