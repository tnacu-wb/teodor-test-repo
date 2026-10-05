package uk.co.whitbread.content.infrastructure.rest.client.snowdrop.adapter;

import static uk.co.whitbread.content.domain.model.ErrorCode.SNOWDROP_HOTEL_SEARCH_EXCEPTION;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.SnowdropResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.model.in.HotelLocationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.model.out.HotelLocationResponseDto;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.properties.SnowdropProperties;
import uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class SnowdropClient {

  private final WebClient snowdropWebClient;
  private final SnowdropProperties snowdropProperties;

  public List<HotelLocationResponseDto> getHotelsLocationByLatLong(HotelLocationRequestDto hotelLocationRequestDto) {
    log.debug("Entered getHotelsFromSnowdrop with latitude={}, longitude={}, radius={}, radiusUnit={}",
            hotelLocationRequestDto.getLatitude(), hotelLocationRequestDto.getLongitude(),
            hotelLocationRequestDto.getRadius(), hotelLocationRequestDto.getRadiusUnit());
    return snowdropWebClient
            .get()
            .uri(uriBuilder -> uriBuilder
                    .path(snowdropProperties.getHotelSearchEndpoint())
                    .queryParam("latitude", hotelLocationRequestDto.getLatitude())
                    .queryParam("longitude", hotelLocationRequestDto.getLongitude())
                    .queryParam("radius", hotelLocationRequestDto.getRadius() + hotelLocationRequestDto.getRadiusUnit())
                    .build())
            .retrieve()
            .onStatus(HttpStatusCode::isError, response -> {
              WebClientUtils.logErrorResponse(log, response);
              return response.createException()
                      .map(exception -> new SnowdropResponseException(
                              SNOWDROP_HOTEL_SEARCH_EXCEPTION,
                              "Resource not found in SnowDrop",
                              exception))
                      .flatMap(Mono::error);
            })
            .bodyToMono(new ParameterizedTypeReference<List<HotelLocationResponseDto>>() {
            })
            .doOnError(exception -> ExceptionLogger.log(log, exception))
            .block();
  }
}
