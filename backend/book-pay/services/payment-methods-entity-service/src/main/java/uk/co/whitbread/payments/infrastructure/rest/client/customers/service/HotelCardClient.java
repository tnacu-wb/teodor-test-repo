package uk.co.whitbread.payments.infrastructure.rest.client.customers.service;

import static uk.co.whitbread.payments.infrastructure.rest.client.config.WebClientConstants.AUTHORIZATION;
import static uk.co.whitbread.payments.infrastructure.rest.client.config.WebClientConstants.BOOKING_CHANNEL;
import static uk.co.whitbread.payments.infrastructure.rest.client.config.WebClientConstants.SESSION_ID;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.payments.domain.exception.AccountsServiceException;
import uk.co.whitbread.payments.domain.exception.ErrorCode;
import uk.co.whitbread.payments.domain.exception.PaymentMethodsException;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out.PaymentCardDto;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.properties.HotelCardClientProperties;
import uk.co.whitbread.payments.infrastructure.rest.client.util.WebClientUtils;

@Slf4j
@Component
public class HotelCardClient {

  public static final String HOTEL_CARD_SERVICE_ERROR_MESSAGE = "An error was returned by Hotel Card Service";
  private final WebClient hotelCardWebClient;
  private final HotelCardClientProperties hotelCardClientProperties;

  public HotelCardClient(@Qualifier("hotelCardWebClient") WebClient hotelCardWebClient,
                         HotelCardClientProperties hotelCardClientProperties) {
    this.hotelCardWebClient = hotelCardWebClient;
    this.hotelCardClientProperties = hotelCardClientProperties;
  }

  public List<PaymentCardDto> findCentralStoredCard(String companyId,
        String channel, String sessionId, String token) {

    return hotelCardWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(hotelCardClientProperties.getCompanyCardsEndpoint())
            .build(companyId))
        .header(BOOKING_CHANNEL, channel)
        .header(SESSION_ID, sessionId)
        .header(AUTHORIZATION, token)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new AccountsServiceException(ErrorCode.IMPROPER_CALL_EXCEPTION,
                  "Improper call of Accounts Service "
                  + response.statusCode()));
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new PaymentMethodsException(ErrorCode.FIND_CENTRAL_STORED_CARD_EXCEPTION,
                  HOTEL_CARD_SERVICE_ERROR_MESSAGE));
        })
          .bodyToMono(new ParameterizedTypeReference<List<PaymentCardDto>>() {})
          .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to find central stored card details!"))
          .block();
  }
}