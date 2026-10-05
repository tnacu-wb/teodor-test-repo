package uk.co.whitbread.infrastructure.rest.client;

import static uk.co.whitbread.domain.exceptions.ErrorCode.CREATE_HOTEL_AVAILABILITIES_EXCEPTION;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.infrastructure.config.AvailabilityCacheProperties;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.exception.AvailabilityCacheException;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.in.AvailabilityCacheRequest;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out.HotelAvailabilitiesDto;
import uk.co.whitbread.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
public class AvailabilityCacheClient {

  private final WebClient availabilityCacheWebClient;
  private final AvailabilityCacheProperties availabilityCacheProperties;

  public HotelAvailabilitiesDto getAvailabilitiesFromCache(
      AvailabilityCacheRequest availabilityCacheRequest) {
    log.debug(
        "Entered getAvailabilitiesFromCache with availabilityCacheRequest={}",
        availabilityCacheRequest);

    return availabilityCacheWebClient.get().uri(uriBuilder -> uriBuilder
            .path(availabilityCacheProperties.getAvailabilityCacheEndpoint())
            .queryParam("hotelCodes",
                availabilityCacheRequest.getHotelCodes())
            .queryParam("arrival",
                availabilityCacheRequest.getArrival())
            .queryParam("departure",
                availabilityCacheRequest.getDeparture())
            .queryParam("language",
                availabilityCacheRequest.getLanguage())
            .queryParam("country",
                availabilityCacheRequest.getCountry())
            .queryParam("rooms",
                availabilityCacheRequest.getRooms())
            .queryParam("adults",
                String.join(",", Arrays.stream(availabilityCacheRequest.getAdults())
                    .mapToObj(String::valueOf).toList()))
            .queryParam("children",
                String.join(",", Arrays.stream(availabilityCacheRequest.getChildren())
                    .mapToObj(String::valueOf).toList()))
            .queryParam("type",
                String.join(",", availabilityCacheRequest.getType()))
            .queryParam("page",
                availabilityCacheRequest.getPage())
            .queryParam("size",
                availabilityCacheRequest.getSize())
            .build()).retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse ->
            WebClientUtils.logErrorResponse(log, clientResponse)
                .then(Mono.error(new AvailabilityCacheException(CREATE_HOTEL_AVAILABILITIES_EXCEPTION,
                    "An error was returned while trying to create hotel availabilities from cache!")))
        )
        .bodyToMono(HotelAvailabilitiesDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format("Error while trying to create hotel availabilities for availabilityCacheRequest=%s",
            availabilityCacheRequest)))
        .block();
  }
}
