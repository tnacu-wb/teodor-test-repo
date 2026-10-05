package uk.co.whitbread.ohip.infrastructure.rest.client.opera.properties;

import static java.util.Collections.singletonList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.opera.out.OperaHotelDetailsList;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.rest.client.hotel.ohip.properties.HotelDetailsOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class OhipHotelDetailsClient {

  private final WebClient ohipWebClient;
  private final HotelDetailsOhipProperties hotelDetailsOhipProperties;

  public OperaHotelDetailsList getHotelDetails(String hotelId) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(OhipConstants.FETCH_INSTRUCTIONS_PARAM, singletonList("General"));
    var message = String.format(
        "Error while trying to get hotel details for hotelId=%s", hotelId);

    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(hotelDetailsOhipProperties.getHotelDetails())
                .queryParams(params)
                .build(hotelId))
        .headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return Mono.error(new OhipBadRequestException(ErrorCode.OHIP_HOTEL_DETAILS_EXCEPTION, message));
        })
        .onStatus(HttpStatusCode::isError, response -> {
          log.error("Error while fetching details from Opera with status code {} and response - {}",
              response.statusCode(), response.bodyToMono(String.class));
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(ErrorCode.OHIP_HOTEL_DETAILS_EXCEPTION,
              message));
        })
        .bodyToMono(OperaHotelDetailsList.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}
