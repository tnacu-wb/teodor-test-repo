package uk.co.whitbread.content.infrastructure.rest.client.ohip;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.client.content.exceptions.OhipException;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.model.out.HotelInfoDto;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.model.out.RatePlansResponseDto;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.properties.OhipAdapterProperties;
import uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class OhipAdapterClient {

  public static final String HOTEL_ID = "hotelId";
  public static final String RATE_PLAN_CODES = "ratePlanCodes";

  private final OhipAdapterProperties ohipAdapterProperties;
  private final WebClient ohipAdapterWebClient;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "HotelInfoCache")
  public HotelInfoDto getHotelInfo(String hotelId) {
    log.debug("Entered getHotelInfo with hotelId={}", hotelId);

    return ohipAdapterWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(ohipAdapterProperties.getHotelInfoEndpoint())
            .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logClientResponseStatus(log, response);
          return response.bodyToMono(OhipException.class);
        })
        .bodyToMono(HotelInfoDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
            value = "RatePlansCacheContent")
  public RatePlansResponseDto sendGetRatePlansRequest(List<String> ratePlanCodes, String hotelId) {

    return ohipAdapterWebClient.get()
        .uri(
            uriBuilder -> uriBuilder.path(
                    ohipAdapterProperties.getRatePlansEndpoint())
                .queryParam(RATE_PLAN_CODES, ratePlanCodes)
                .queryParam(HOTEL_ID, hotelId)
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logClientResponseStatus(log, response);
          return response.bodyToMono(OhipException.class);
        })
        .bodyToMono(RatePlansResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}
