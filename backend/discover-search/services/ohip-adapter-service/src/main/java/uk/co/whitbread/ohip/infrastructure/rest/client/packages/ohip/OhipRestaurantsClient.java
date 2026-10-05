package uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip;

import static java.util.Collections.singletonList;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_HOTEL_RESTAURANT_EXCEPTION;
import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.logErrorResponse;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.RestaurantsResponseOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.out.PackagesRequestOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip.properties.RestaurantsOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.properties.EnterpriseProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OhipRestaurantsClient {

  private final WebClient ohipWebClient;
  private final RestaurantsOhipProperties restaurantsOhipProperties;
  private final EnterpriseProperties enterpriseProperties;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "OperaRestaurantsConfigCache", key = "#packagesRequestOhip.hotelId")
  public RestaurantsResponseOhipDto getHotelRestaurants(
      PackagesRequestOhipDto packagesRequestOhip) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(OhipConstants.HOTEL_ID_PARAM, singletonList(packagesRequestOhip.getHotelId()));
    params.put(OhipConstants.FETCH_INSTRUCTIONS_PARAM, enterpriseProperties.getFetchInstructions());

    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(restaurantsOhipProperties.getRestaurantsEndpoint())
                .queryParams(params)
                .build(packagesRequestOhip.getHotelId()))
        .headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, packagesRequestOhip.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(OHIP_HOTEL_RESTAURANT_EXCEPTION,
              String.format("Error while trying to get hotel restaurants for hotelId=%s",
                  packagesRequestOhip.getHotelId())));
        })
        .bodyToMono(RestaurantsResponseOhipDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}
