package uk.co.whitbread.basket.infrastructure.rest.client.hotel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.basket.generated.models.hotel.HotelInfoDto;
import uk.co.whitbread.basket.infrastructure.rest.client.hotel.service.properties.HotelInfoClientProperties;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.basket.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Component
@Slf4j
public class HotelInfoClient {

  private final WebClient hotelInfoWebClient;
  private final HotelInfoClientProperties hotelInfoClientProperties;

  public HotelInfoClient(
      @Qualifier("hotelInfoWebClient") WebClient hotelInfoWebClient,
      HotelInfoClientProperties hotelInfoClientProperties) {
    this.hotelInfoWebClient = hotelInfoWebClient;
    this.hotelInfoClientProperties = hotelInfoClientProperties;
  }

  public HotelInfoDto getHotelInfo(final String hotelId) {
    return hotelInfoWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(hotelInfoClientProperties.getHotelInfoEndpoint())
            .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(HotelReservationException.class);
        })
        .bodyToMono(HotelInfoDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while fetching hotel info for hotelId=%s. "
                    + "An error was returned by Rules Agent when retrieving business allowances.",
                hotelId)))
        .block();
  }

}
