package uk.co.whitbread.promo.infrastructure.rest.client.ohip;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.promo.infrastructure.exception.OhipException;
import uk.co.whitbread.promo.infrastructure.rest.client.ohip.model.out.PromotionResponseDto;
import uk.co.whitbread.promo.infrastructure.rest.client.ohip.properties.OhipAdapterProperties;
import uk.co.whitbread.promo.infrastructure.rest.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class OhipAdapterClient {

  public static final String HOTEL_ID = "hotelId";
  public static final String PROMOTION_CODES = "promotionCodes";

  private final OhipAdapterProperties ohipAdapterProperties;
  private final WebClient ohipAdapterWebClient;


  public List<PromotionResponseDto> getPromotions(List<String> promotionCodes, String hotelId) {
    log.debug("Entered getHotelInfo with hotelId={}", hotelId);

    return ohipAdapterWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(ohipAdapterProperties.getPromotionsEndpoint())
            .queryParam(PROMOTION_CODES, String.join(",", promotionCodes))
            .queryParam(HOTEL_ID, hotelId)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(OhipException.class);
        })
        .bodyToFlux(PromotionResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .collectList()
        .block();
  }
}