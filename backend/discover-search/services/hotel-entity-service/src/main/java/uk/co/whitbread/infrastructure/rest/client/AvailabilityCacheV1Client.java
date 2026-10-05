package uk.co.whitbread.infrastructure.rest.client;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.infrastructure.config.AvailabilityCacheProperties;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.exception.AvailabilityCacheV1Exception;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.HotelAvailabilitiesDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.in.AvailabilityCacheRequestV1;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.HotelAvailabilitiesDto;
import uk.co.whitbread.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
public class AvailabilityCacheV1Client {

  private final WebClient availabilityCacheV1WebClient;
  private final AvailabilityCacheProperties availabilityCacheProperties;

  public HotelAvailabilitiesDto getAvailabilitiesResponseFromCacheV1(
      AvailabilityCacheRequestV1 availabilityCacheRequestV1) {
    log.debug(
        "Entered getAvailabilitiesFromCache with availabilityCacheRequest={}",
        availabilityCacheRequestV1);

    return availabilityCacheV1WebClient.get().uri(uriBuilder -> uriBuilder
            .path(availabilityCacheProperties.getAvailabilityCacheEndpoint())
            .queryParam("hotelCodes",
                String.join(",", availabilityCacheRequestV1.getHotelCodes()))
            .queryParam("arrival",
                availabilityCacheRequestV1.getArrival())
            .queryParam("departure",
                availabilityCacheRequestV1.getDeparture())
            .queryParam("channel",
                availabilityCacheRequestV1.getChannel())
            .queryParam("adults", availabilityCacheRequestV1.getAdults())
            .queryParam("children", availabilityCacheRequestV1.getChildren())
            .queryParam("cot", availabilityCacheRequestV1.getCot())
            .queryParam("roomQty", String.join(",", availabilityCacheRequestV1.getRoomQty()
                .stream().map(Object::toString).toList()))
            .queryParam("language", availabilityCacheRequestV1.getLanguage())
            .queryParam("country", availabilityCacheRequestV1.getCountry())
            .queryParam("roomTypes", availabilityCacheRequestV1.getRoomTypesList().stream()
                .map(roomTypeList -> String.join(",", roomTypeList)).toList())
            .queryParam("rooms", availabilityCacheRequestV1.getRooms())
            .queryParam("flagMlos", availabilityCacheRequestV1.getFlagMlos())
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse ->
            WebClientUtils.logErrorResponse(log, clientResponse)
                .then(Mono.error(new AvailabilityCacheV1Exception(ErrorCode.CREATE_HOTEL_AVAILABILITIES_V1_EXCEPTION,
                    "An error was returned while trying to create hotel availabilities from cacheV1!")))
        )
        .bodyToMono(HotelAvailabilitiesDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to create hotel availabilities response for availabilityCacheRequestV1=%s",
            availabilityCacheRequestV1)))
        .block();
  }

  public HotelAvailabilitiesDistrDto getAvailabilitiesResponseFromCacheV1Distr(
      AvailabilityCacheRequestV1 availabilityCacheRequestV1, Set<String> rateList) {
    log.debug(
        "Entered getAvailabilitiesFromCache with availabilityCacheRequest={}",
        availabilityCacheRequestV1);
    return availabilityCacheV1WebClient.get().uri(uriBuilder -> {
      var builder = uriBuilder.path(availabilityCacheProperties.getDistrAvailabilityCacheEndpoint())
                          .queryParam("hotelCodes",
                                  String.join(",", availabilityCacheRequestV1.getHotelCodes()))
                          .queryParam("arrival",
                                  availabilityCacheRequestV1.getArrival())
                          .queryParam("departure",
                                  availabilityCacheRequestV1.getDeparture())
                          .queryParam("adults", 1)
                          .queryParam("cot", false)
                          .queryParam("children", 0)
                          .queryParam("roomQty", 1)
                          .queryParam("roomTypes", availabilityCacheRequestV1.getRoomTypesList().stream()
                                  .map(roomTypeList -> String.join(",", roomTypeList)).toList())
                          .queryParam("rooms", 1);
      if (!CollectionUtils.isEmpty(rateList)) {
        builder = builder.queryParam("ratePlanCodes", rateList);
      }
      return builder.build();
    })
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse ->
            WebClientUtils.logErrorResponse(log, clientResponse)
                .then(Mono.error(new AvailabilityCacheV1Exception(
                    ErrorCode.CREATE_HOTEL_AVAILABILITIES_V1_DISTR_EXCEPTION,
                    "An error was returned while trying to create hotel availabilities from cacheV1 distr!")))
        )
        .bodyToMono(HotelAvailabilitiesDistrDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to create hotel availabilities response for availabilityCacheRequestV1=%s",
            availabilityCacheRequestV1)))
        .block();
  }
}
