package uk.co.whitbread.payments.infrastructure.rest.client.customers.service;

import static uk.co.whitbread.payments.infrastructure.rest.client.config.WebClientConstants.AUTHORIZATION;
import static uk.co.whitbread.payments.infrastructure.rest.client.config.WebClientConstants.BOOKING_CHANNEL;
import static uk.co.whitbread.payments.infrastructure.rest.client.config.WebClientConstants.BUSINESS;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.payments.domain.exception.AccountsServiceException;
import uk.co.whitbread.payments.domain.exception.ErrorCode;
import uk.co.whitbread.payments.domain.exception.PaymentMethodsException;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out.CustomerAccountDto;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.properties.HotelAccountClientProperties;
import uk.co.whitbread.payments.infrastructure.rest.client.util.WebClientUtils;


@Slf4j
@Component
public class HotelAccountClient {

  public static final String ACCOUNT_SERVICE_ERROR_MESSAGE = "An error was returned by Hotel Account Service";
  private final WebClient hotelAccountWebClient;
  private final HotelAccountClientProperties hotelAccountClientProperties;

  public HotelAccountClient(@Qualifier("hotelAccountWebClient") WebClient hotelAccountWebClient,
                            HotelAccountClientProperties hotelAccountClientProperties) {
    this.hotelAccountWebClient = hotelAccountWebClient;
    this.hotelAccountClientProperties = hotelAccountClientProperties;
  }

  public CustomerAccountDto findCustomerAccount(String customerId, boolean isBusinessUser,
      String channel, String token) {
    return hotelAccountWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(hotelAccountClientProperties.getHotelAccountEndpoint())
            .queryParam(BUSINESS, isBusinessUser)
            .build(customerId))
        .header(BOOKING_CHANNEL, channel)
        .header(AUTHORIZATION, token)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new AccountsServiceException(ErrorCode.FIND_CUSTOMER_ACCOUNT_EXCEPTION,
                ACCOUNT_SERVICE_ERROR_MESSAGE));
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new PaymentMethodsException(ErrorCode.FIND_CUSTOMER_ACCOUNT_2_EXCEPTION, ACCOUNT_SERVICE_ERROR_MESSAGE));
        })
        .bodyToMono(CustomerAccountDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
                "Error while trying to find customer account details!"))
        .block();
  }
}