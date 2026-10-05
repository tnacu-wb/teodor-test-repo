package uk.co.whitbread.infrastructure.rest.client;

import static uk.co.whitbread.domain.exceptions.ErrorCode.RESOURCE_NOT_FOUND_SNOWDROP_2_EXCEPTION;
import static uk.co.whitbread.domain.exceptions.ErrorCode.RESOURCE_NOT_FOUND_SNOWDROP_EXCEPTION;
import static uk.co.whitbread.domain.exceptions.ErrorCode.SNOWDROP_INTERNAL_ERROR;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.infrastructure.config.snowdrop.SnowDropProperties;
import uk.co.whitbread.infrastructure.rest.client.distance.exception.SnowDropException;
import uk.co.whitbread.infrastructure.rest.client.distance.model.in.HotelLocationRequest;
import uk.co.whitbread.infrastructure.rest.client.distance.model.out.HotelLocationResponse;
import uk.co.whitbread.infrastructure.rest.client.hotelsearch.exception.HotelSearchLocationException;
import uk.co.whitbread.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
public class SnowDropClient {

  private final WebClient snowDropWebClient;
  private final SnowDropProperties snowDropProperties;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager12Hours",
      value = "HotelsLocationResponseCache", key = "{#distance.hotelId, "
      + "#distance.location, #locationFormat, "
      + "#distance.radius, #distance.radiusUnit}")
  public List<HotelLocationResponse> getHotelsLocationByPlaceId(HotelLocationRequest distance,
                                                                String locationFormat) {
    log.debug(
        "Entered getHotelDistancesFromSearch with distance={}, locationFormat={}",
        distance, locationFormat);

    return snowDropWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(snowDropProperties.getHotelSearchEndpoint())
            .queryParam(locationFormat, distance.getLocation())
            .queryParam("radius", distance.getRadius() + distance.getRadiusUnit())
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelSearchLocationException(
              RESOURCE_NOT_FOUND_SNOWDROP_EXCEPTION, "Resource not found in SnowDrop"));
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new SnowDropException(SNOWDROP_INTERNAL_ERROR,
                  "SnowDrop server error"));
        })
        .bodyToFlux(HotelLocationResponse.class).collect(Collectors.toList())
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to get hotel distances from search for distance=%s,"
                + " locationFormat=%s", distance, locationFormat)))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager12Hours",
      value = "HotelsLocationResponseCache", key = "{#distance.hotelId, "
      + "#distance.location, #distance.locationFormat, "
      + "#distance.radius, #distance.radiusUnit}")
  public List<HotelLocationResponse> getHotelsLocationByLatLong(HotelLocationRequest distance,
                                                                List<String> latlongLocation) {
    log.debug(
        "Entered getHotelsLocationByLatLong with hotelId={}, location={}, locationFormat={},"
            + " radius={}, radiusUnit={}, latlongLocationSize={}",
        distance.getHotelId(), distance.getLocation(),
        distance.getLocationFormat(),
        distance.getRadius(), distance.getRadiusUnit(), latlongLocation.size());

    return snowDropWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(snowDropProperties.getHotelSearchEndpoint())
            .queryParam("latitude", latlongLocation.get(0))
            .queryParam("longitude", latlongLocation.get(1))
            .queryParam("radius", distance.getRadius() + distance.getRadiusUnit())
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelSearchLocationException(
              RESOURCE_NOT_FOUND_SNOWDROP_2_EXCEPTION, "Resource not found in SnowDrop"));
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new SnowDropException(SNOWDROP_INTERNAL_ERROR,
                  "SnowDrop server error"));
        })
        .bodyToFlux(HotelLocationResponse.class).collect(Collectors.toList())
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to get hotels location for hotelId=%s, location=%s,"
                + " locationFormat=%s, radius=%s, radiusUnit=%s, latlongLocationSize=%s",
            distance.getHotelId(), distance.getLocation(),
            distance.getLocationFormat(),
            distance.getRadius(), distance.getRadiusUnit(), latlongLocation.size())))
        .block();
  }
}
