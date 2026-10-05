package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.paymentmethod;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.GatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ServiceUnavailableException;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentMethodValidationResult;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.PaymentMethodOutPort;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.PaymentMethodProperties;

/**
 * REST client adapter for the Payment Method Entity Service.
 *
 * <p>Calls {@code GET /v1/payment-methods} to validate that new card payment via
 * Datatrans is available and extract the supported card brand codes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMethodRestAdapter implements PaymentMethodOutPort {

  private static final String CARD_NAME = "CARD";
  private static final String NEW_CARD_TYPE = "NEW_CARD";
  private static final String DATATRANS_PROVIDER = "Datatrans";
  private static final int TOO_MANY_REQUESTS = 429;

  @Qualifier("paymentMethodRestClient")
  private final RestClient paymentMethodRestClient;
  private final PaymentMethodProperties properties;

  @Override
  public PaymentMethodValidationResult validatePaymentMethods(
      String basketReference, String country, String language,
      String userType, String clientChannel) {
    log.info("Validating payment methods: basketReference={}, country={}, "
        + "language={}, userType={}, clientChannel={}",
        basketReference, country, language, userType, clientChannel);
    try {
      PaymentMethodDto[] response = paymentMethodRestClient.get()
          .uri(uriBuilder -> uriBuilder
              .path(properties.getPaymentMethodsEndpoint())
              .queryParam("basketReference", basketReference)
              .queryParam("country", country)
              .queryParam("language", language)
              .queryParam("userType", userType)
              .queryParam("clientChannel", clientChannel)
              .build())
          .retrieve()
          .onStatus(HttpStatusCode::isError, (req, res) -> {
            log.error("Payment Method Entity Service error: status={}",
                res.getStatusCode());
            throw toFailure(res.getStatusCode());
          })
          .body(PaymentMethodDto[].class);

      if (response == null || response.length == 0) {
        log.warn("Payment Method Entity Service returned empty response: "
            + "basketReference={}", basketReference);
        return new PaymentMethodValidationResult(false, List.of());
      }

      // Find the NEW_CARD entry
      Optional<PaymentMethodDto> newCardMethod = Arrays.stream(response)
          .filter(pm -> CARD_NAME.equals(pm.name())
              && NEW_CARD_TYPE.equals(pm.type()))
          .findFirst();

      if (newCardMethod.isEmpty()) {
        log.info("No CARD/NEW_CARD payment method found in response: "
            + "basketReference={}", basketReference);
        return new PaymentMethodValidationResult(false, List.of());
      }

      PaymentMethodDto cardMethod = newCardMethod.get();

      // Check if it's enabled
      if (!cardMethod.enabled()) {
        log.info("CARD/NEW_CARD payment method is disabled: "
            + "basketReference={}", basketReference);
        return new PaymentMethodValidationResult(false, List.of());
      }

      // Check the payment provider is Datatrans
      if (!DATATRANS_PROVIDER.equals(cardMethod.paymentProvider())) {
        log.info("CARD/NEW_CARD payment provider is not Datatrans: "
            + "basketReference={}, provider={}",
            basketReference, cardMethod.paymentProvider());
        return new PaymentMethodValidationResult(false, List.of());
      }

      List<AcceptedCardTypeDto> acceptedCards = cardMethod.acceptedCardTypes() != null
          ? cardMethod.acceptedCardTypes() : List.of();
      List<String> cardBrands = acceptedCards.stream()
          .map(AcceptedCardTypeDto::type)
          .filter(Objects::nonNull)
          .distinct()
          .toList();

      log.info("Payment method validation successful: basketReference={}, "
          + "cardBrands={}", basketReference, cardBrands);
      return new PaymentMethodValidationResult(true, cardBrands);
    } catch (ResourceAccessException e) {
      log.error("Payment Method Entity Service is unreachable: {}",
          e.getMessage(), e);
      throw new ServiceUnavailableException(
          "Payment Method Entity Service is unreachable: " + e.getMessage(),
          e);
    }
  }

  /**
   * Classifies an error status as retryable or terminal.
   *
   * <p>Every non-2xx used to become a {@link ServiceUnavailableException}, which the workflow
   * retries. A 400 or a 404 from this service is a decision, not an accident — the request we
   * sent is the request we will send again — so retrying it only delays the customer's error by
   * three attempts and triples the noise in the logs. Those become a
   * {@link GatewayException}, matching what {@code ReservationServiceClient} already raises for
   * a downstream non-2xx and mapping through {@code PaymentFailureMapper} to
   * {@code GATEWAY_ERROR} and through the exception handler to {@code 502}.
   *
   * <p>429 is the exception among the 4xx codes: it explicitly asks us to come back later, so
   * it stays retryable alongside 5xx and the unreachable/timeout cases.
   */
  private RuntimeException toFailure(HttpStatusCode status) {
    String message = "Payment Method Entity Service error: " + status;
    if (status.is4xxClientError() && status.value() != TOO_MANY_REQUESTS) {
      return new GatewayException(message);
    }
    return new ServiceUnavailableException(message);
  }
}
