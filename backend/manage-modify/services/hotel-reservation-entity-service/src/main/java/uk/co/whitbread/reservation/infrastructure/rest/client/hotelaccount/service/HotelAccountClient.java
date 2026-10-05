package uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.account.service.generated.models.CustomerRequest;
import uk.co.whitbread.hotel.account.service.generated.models.CustomerResponse;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.exceptions.HotelAccountException;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.service.properties.HotelAccountProperties;
import uk.co.whitbread.reservation.infrastructure.rest.utils.WebClientUtils;

@Slf4j
@Component
public class HotelAccountClient {

  private final HotelAccountProperties hotelAccountProperties;
  private final WebClient hotelAccountWebClient;

  public HotelAccountClient(@Qualifier("hotelAccountWebClient") WebClient hotelAccountWebClient,
      HotelAccountProperties hotelAccountProperties) {

    this.hotelAccountProperties = hotelAccountProperties;
    this.hotelAccountWebClient = hotelAccountWebClient;
  }

  public CustomerResponse sendUpdateCustomerRequest(CustomerRequest customerRequest,
      String customerId,
      String authorization) {
    return hotelAccountWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(hotelAccountProperties.getUpdateCustomerEndpoint())
            .build(customerId))
        .header("Authorization", "Bearer " + authorization)
        .body(Mono.just(customerRequest), CustomerRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(HotelAccountException.class);
        })
        .bodyToMono(CustomerResponse.class)
        .doOnError(
            ex -> ExceptionLogger.log(log, ex, "Error while trying to update customer"))
        .block();
  }
}
