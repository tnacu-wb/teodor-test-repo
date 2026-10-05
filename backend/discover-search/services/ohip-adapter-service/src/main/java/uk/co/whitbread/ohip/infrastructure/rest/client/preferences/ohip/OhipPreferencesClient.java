package uk.co.whitbread.ohip.infrastructure.rest.client.preferences.ohip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.preferences.HotelPreferencesOhipResponseDto;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.preferences.ohip.properties.PreferencesOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class OhipPreferencesClient {

  private final WebClient ohipWebClient;
  private final PreferencesOhipProperties preferencesOhipProperties;

  public HotelPreferencesOhipResponseDto getPreferencesForGroup(String hotelId, String groupCode) {

    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(preferencesOhipProperties.getPreferencesEndpoint())
                .queryParam(OhipConstants.PREFERENCES_GROUP_CODE, groupCode).build(hotelId))
        .headers(httpHeaders -> httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          var message = String.format(
              "Error while trying to get hotel preferences for hotelId=%s with groupCode=%s", hotelId,
              groupCode);
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new HotelReservationException(ErrorCode.OHIP_HOTEL_CONFIG_EXCEPTION, message));
        }).bodyToMono(HotelPreferencesOhipResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

}
