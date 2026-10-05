package uk.co.whitbread.account.infrastructure.rest.client.customers.service;

import static uk.co.whitbread.account.infrastructure.exception.GlobalErrorHandler.handleError;
import static uk.co.whitbread.account.infrastructure.rest.client.config.WebClientConstants.COUNTRY;
import static uk.co.whitbread.account.infrastructure.rest.client.config.WebClientConstants.LANGUAGE;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.account.infrastructure.exception.ErrorCode;
import uk.co.whitbread.account.infrastructure.exception.ServiceRequestException;
import uk.co.whitbread.account.infrastructure.rest.client.customers.model.in.CustomerRegistrationRequestDto;
import uk.co.whitbread.account.infrastructure.rest.client.customers.model.out.CustomerRegistrationResponseDto;
import uk.co.whitbread.account.infrastructure.rest.client.customers.service.properties.CustomerRegistrationClientProperties;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@Component
public class CustomerRegistrationClient {
  public static final String HOTEL_REGISTER_SERVICE_ERROR_MESSAGE =
      "An error was returned by Hotel Register Service";
  private final WebClient customerRegistrationWebClient;
  private final CustomerRegistrationClientProperties registrationServiceClientProperties;

  public CustomerRegistrationClient(@Qualifier("customerRegistrationWebClient")
                                        WebClient customerRegistrationWebClient,
                                    CustomerRegistrationClientProperties registrationServiceClientProperties) {
    this.customerRegistrationWebClient = customerRegistrationWebClient;
    this.registrationServiceClientProperties = registrationServiceClientProperties;
  }

  public CustomerRegistrationResponseDto registerCustomer(
      CustomerRegistrationRequestDto customerRegistrationRequest,
      String country, String language) throws ServiceRequestException {
    return customerRegistrationWebClient.post()
          .uri(registrationServiceClientProperties.getRegistrationEndpoint())
          .contentType(MediaType.APPLICATION_JSON)
          .header(COUNTRY, country)
          .header(LANGUAGE, language)
          .body(Mono.just(customerRegistrationRequest), CustomerRegistrationRequestDto.class)
          .retrieve()
          .onStatus(HttpStatusCode::isError, response -> handleError(response,
                ErrorCode.REGISTER_CUSTOMER_REQUEST_INCORRECT_EXCEPTION,
                ErrorCode.REGISTER_CUSTOMER_EXCEPTION))
          .bodyToMono(CustomerRegistrationResponseDto.class)
          .doOnError(exception -> ExceptionLogger.log(log, exception,
                HOTEL_REGISTER_SERVICE_ERROR_MESSAGE))
          .block();
  }
}