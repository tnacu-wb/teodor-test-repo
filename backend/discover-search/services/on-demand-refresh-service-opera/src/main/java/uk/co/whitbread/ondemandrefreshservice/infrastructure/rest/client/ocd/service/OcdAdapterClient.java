package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.ocd.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.ocd.adapter.service.generated.models.TaxResponseDto;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.OcdAdapterException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.ocd.service.properties.OcdAdapterProperties;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.util.WebClientUtils;

@Slf4j
@Component
public class OcdAdapterClient {

  private static final String ARRIVAL_DATE_PARAM = "arrivalDate";
  private static final String DEPARTURE_DATE_PARAM = "departureDate";
  private static final String ADULTS_PARAM = "adults";
  private static final String RATE_PLAN_CODE_PARAM = "ratePlanCode";
  private static final String ROOM_TYPE_PARAM = "roomType";
  private final OcdAdapterProperties ocdAdapterProperties;
  private final WebClient ocdAdapterWebClient;


  public OcdAdapterClient(OcdAdapterProperties ocdAdapterProperties,
                          @Qualifier("ocdAdapterWebClient") WebClient ocdAdapterWebClient) {
    this.ocdAdapterProperties = ocdAdapterProperties;
    this.ocdAdapterWebClient = ocdAdapterWebClient;
  }

  public TaxResponseDto getTax(String hotelId, String arrivalDate, String departureDate,
      Integer adults, String ratePlanCode, String roomType) {

    return ocdAdapterWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(ocdAdapterProperties.getOfferEndpoint())
            .queryParam(ARRIVAL_DATE_PARAM, arrivalDate)
            .queryParam(DEPARTURE_DATE_PARAM, departureDate)
            .queryParam(ADULTS_PARAM, adults)
            .queryParam(RATE_PLAN_CODE_PARAM, ratePlanCode)
            .queryParam(ROOM_TYPE_PARAM, roomType)
            .build(hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(OcdAdapterException.class);
        })
        .bodyToMono(TaxResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "Error while trying to get tax for hotelId: " + hotelId))
        .block();
  }
}