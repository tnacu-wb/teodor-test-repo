package uk.co.whitbread.ohip.infrastructure.rest.client.frontdesk.ohip;

import static java.util.Collections.singletonList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CreditCardInfo;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.infrastructure.rest.client.frontdesk.ohip.properties.FrontDeskOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class OhipFrontDeskClient {

  private static final String HOTEL_ID = "hotelId";
  private static final String CARD_ID = "cardId";
  public static final String CARD_ID_CONTEXT = "cardIdContext";
  public static final String CARD_ID_TYPE = "cardIdType";
  public static final String CREDIT_CARD = "CreditCard";

  private final WebClient ohipWebClient;
  private final FrontDeskOhipProperties frontDeskOhipProperties;

  public CreditCardInfo getCreditCardInfo(String hotelId, String cardId) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(HOTEL_ID, singletonList(hotelId));
    params.put(CARD_ID, singletonList(cardId));
    params.put(CARD_ID_CONTEXT, singletonList(OhipConstants.CONTEXT));
    params.put(CARD_ID_TYPE, singletonList(CREDIT_CARD));
    var message = String.format(
        "Error while trying to get credit card information for hotelId=%s and cardId=%s",
        hotelId, cardId);
    return ohipWebClient.get().uri(
            uriBuilder -> uriBuilder.path(frontDeskOhipProperties.getCreditCardInfoEndpoint())
                .queryParams(params)
                .build())
        .headers(httpHeaders ->
            httpHeaders.add(OhipConstants.HOTEL_ID_HEADER, hotelId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new HotelReservationException(
              ErrorCode.OHIP_FRONT_DESK_EXCEPTION,
              message));
        })
        .bodyToMono(CreditCardInfo.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

}
