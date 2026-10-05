package uk.co.whitbread.dashboard.infrastructure.rest.client.hotelinfo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.HotelInfo;
import uk.co.whitbread.dashboard.infrastructure.rest.client.exception.HotelInfoException;
import uk.co.whitbread.dashboard.infrastructure.rest.client.hotelinfo.properties.HotelInfoProperties;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelInfoClient {

  private final WebClient hotelInfoWebClient;
  private final HotelInfoProperties hotelInfoProperties;

  public HotelInfo getHotelInfo(final String hotelCode) {
    return hotelInfoWebClient.get()
        .uri(hotelInfoProperties.getHotelInfoEndpoint(), hotelCode)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            response.bodyToMono(HotelInfoException.class))
        .bodyToMono(HotelInfo.class)
        .doOnError(
            e -> ExceptionLogger.log(log, e, String.format("Error while trying to get hotel info for hotelCode = %s, ",
                hotelCode)))
        .block();
  }

}
