package uk.co.whitbread.ocd.infrastructure.rest.client.ocd;

import static uk.co.whitbread.ocd.infrastructure.rest.client.ocd.config.OcdConstants.ADULTS_PARAM;
import static uk.co.whitbread.ocd.infrastructure.rest.client.ocd.config.OcdConstants.ARRIVAL_DATE_PARAM;
import static uk.co.whitbread.ocd.infrastructure.rest.client.ocd.config.OcdConstants.DEPARTURE_DATE_PARAM;
import static uk.co.whitbread.ocd.infrastructure.rest.client.ocd.config.OcdConstants.RATE_PLAN_CODE_PARAM;
import static uk.co.whitbread.ocd.infrastructure.rest.client.ocd.config.OcdConstants.ROOM_TYPE_CODE_PARAM;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferDetailsResponse;
import uk.co.whitbread.ocd.domain.model.tax.in.TaxRequest;
import uk.co.whitbread.ocd.infrastructure.rest.client.ocd.exceptions.OcdOfferException;
import uk.co.whitbread.ocd.infrastructure.rest.client.ocd.properties.OcdOfferProperties;
import uk.co.whitbread.ocd.infrastructure.rest.exception.ErrorCode;
import uk.co.whitbread.ocd.infrastructure.rest.utils.WebClientUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class OcdClient {

  private final WebClient ocdWebClient;

  private final OcdOfferProperties ocdOfferProperties;

  public OfferDetailsResponse getTaxDetails(TaxRequest taxRequest) {
    return ocdWebClient.get().uri(
            uriBuilder -> uriBuilder.path(
                    ocdOfferProperties.getPropertyOfferEndpoint())
                .queryParam(ARRIVAL_DATE_PARAM, taxRequest.getArrivalDate())
                .queryParam(DEPARTURE_DATE_PARAM, taxRequest.getDepartureDate())
                .queryParam(ADULTS_PARAM, taxRequest.getAdults())
                .queryParam(RATE_PLAN_CODE_PARAM, taxRequest.getRatePlanCode())
                .queryParam(ROOM_TYPE_CODE_PARAM, taxRequest.getRoomType())
                .build(taxRequest.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new OcdOfferException(
              ErrorCode.OCD_OFFER_EXCEPTION,
              String.format(
                  "Error while trying to get the offers for hotelId=%s",
                  taxRequest.getHotelId())));
        })
        .bodyToMono(OfferDetailsResponse.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}
