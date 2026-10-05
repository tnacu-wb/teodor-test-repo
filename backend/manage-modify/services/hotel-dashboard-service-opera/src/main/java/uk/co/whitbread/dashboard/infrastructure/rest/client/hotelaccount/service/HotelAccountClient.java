package uk.co.whitbread.dashboard.infrastructure.rest.client.hotelaccount.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.dashboard.infrastructure.rest.client.exception.HotelAccountException;
import uk.co.whitbread.dashboard.infrastructure.rest.client.hotelaccount.service.properties.HotelAccountProperties;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.BBStaysRequestV2;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.StaysResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelAccountClient {

  private final WebClient hotelAccountWebClient;
  private final HotelAccountProperties hotelAccountProperties;

  public static final String DEFAULT_APPS_BOOKING_CHANNEL = "MOBILE";

  public StaysResponse getAccountStays(
      final BBStaysRequestV2 staysRequest,
      final String customerId,
      final String authorization,
      final String bookingChannel,
      final String origin) {
    return hotelAccountWebClient.post().uri(hotelAccountProperties.getCustomerStaysEndpoint(), customerId)
        .header("Authorization", authorization)
        .header("bookingChannel", StringUtils.isNoneEmpty(bookingChannel) ? bookingChannel :
            DEFAULT_APPS_BOOKING_CHANNEL)
        .header("Origin", origin)
        .body(Mono.just(staysRequest), BBStaysRequestV2.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(HotelAccountException.class))
        .bodyToMono(StaysResponse.class)
        .doOnError(e -> ExceptionLogger.log(log, e, String.format("Error while trying to retrieve stays "
                + "for customerId = %s", customerId)))
        .block();
  }
}