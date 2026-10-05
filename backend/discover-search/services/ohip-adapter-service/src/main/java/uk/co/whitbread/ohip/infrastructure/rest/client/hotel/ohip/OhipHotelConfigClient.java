package uk.co.whitbread.ohip.infrastructure.rest.client.hotel.ohip;

import static java.util.Collections.singletonList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelDetails;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.infrastructure.rest.client.hotel.ohip.properties.HotelConfigOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class OhipHotelConfigClient {

  private final WebClient ohipWebClient;
  private final HotelConfigOhipProperties hotelConfigOhipProperties;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "OperaHotelConfigCache", key = "#hotelId")
  public HotelDetails getHotelConfig(String hotelId) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(OhipConstants.FETCH_INSTRUCTIONS_PARAM, singletonList("General"));
    var message = String.format(
        "Error while trying to get hotel config for hotelId=%s", hotelId);
    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(hotelConfigOhipProperties.getHotelConfig())
                .queryParams(params)
                .build(hotelId))
        .headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_HOTEL_CONFIG_EXCEPTION,
              message));
        })
        .bodyToMono(HotelDetails.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

}
