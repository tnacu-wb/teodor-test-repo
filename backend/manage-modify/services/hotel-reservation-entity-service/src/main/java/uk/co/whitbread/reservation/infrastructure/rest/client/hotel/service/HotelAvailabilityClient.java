package uk.co.whitbread.reservation.infrastructure.rest.client.hotel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.HotelAvailabilityByIdsV2Dto;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.exceptions.HotelAvailabilityException;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.model.HotelAvailabilityV2RequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.service.properties.HotelAvailabilityClientProperties;
import uk.co.whitbread.reservation.infrastructure.rest.utils.WebClientUtils;

@Component
@Slf4j
public class HotelAvailabilityClient {

  private final WebClient hotelAvailabilityWebClient;
  private final HotelAvailabilityClientProperties hotelAvailabilityClientProperties;

  public HotelAvailabilityClient(
      @Qualifier("hotelAvailabilityWebClient") WebClient hotelAvailabilityClient,
      HotelAvailabilityClientProperties hotelInfoClientProperties) {
    this.hotelAvailabilityWebClient = hotelAvailabilityClient;
    this.hotelAvailabilityClientProperties = hotelInfoClientProperties;
  }

  public HotelAvailabilityByIdsV2Dto getHotelAvailabilityV2(HotelAvailabilityV2RequestDto requestDto) {
    return hotelAvailabilityWebClient
        .post()
        .uri(hotelAvailabilityClientProperties.getHotelsAvailabilityV2Endpoint())
        .body(Mono.just(requestDto), HotelAvailabilityV2RequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          log.error("Error while fetching hotel availability");
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new HotelAvailabilityException(ErrorCode.DIGITAL_INVALID_OCCUPANCY_EXCEPTION,
                  "An error was returned by Hotel Entity!"));
        })
        .bodyToMono(HotelAvailabilityByIdsV2Dto.class)
        .block();
  }

}
