package uk.co.whitbread.booking.infrastructure.rest.client.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.domain.model.exceptions.ErrorCode;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.exceptions.InternalBookingException;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.StayRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.out.StayResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.service.properties.HotelAccountClientProperties;
import uk.co.whitbread.booking.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotelAccountClient {

  private static final String ACCOUNT_SERVICES_BOOKING_EXCEPTION_MSG = "An exception was returned by Account Service";
  private static final String BOOKING_CHANNEL = "bookingChannel";
  private static final String AUTHORIZATION = "Authorization";
  private final WebClient hotelAccountWebClient;
  private final HotelAccountClientProperties hotelAccountClientProperties;

  public StayResponseDto getCustomerBookings(String authorization, String channelCode, StayRequestDto stayRequestDto) {
    return hotelAccountWebClient
        .post()
        .uri(uriBuilder -> uriBuilder.path(hotelAccountClientProperties.getBookingsEndpoint())
                .build())
        .body(Mono.just(stayRequestDto), StayRequestDto.class)
        .headers(httpHeaders -> {
          httpHeaders.set(BOOKING_CHANNEL, channelCode);
          httpHeaders.set(AUTHORIZATION, authorization);
        })
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new InternalBookingException(ErrorCode.CUSTOMER_BOOKING_EXCEPTION.getMessage(),
                  String.format("Error while trying to get reservations. %s",
                      ACCOUNT_SERVICES_BOOKING_EXCEPTION_MSG),
                  ErrorCode.CUSTOMER_BOOKING_EXCEPTION.getCode()));
        })
        .bodyToMono(StayResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}