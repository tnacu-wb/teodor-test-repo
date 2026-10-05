package uk.co.whitbread.content.infrastructure.rest.client.meals.adapter;

import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_MEALS_INFO_EXCEPTION;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.meals.model.in.MealsInfoResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.meals.model.out.MealsRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class MealsAemClient {

  private final WebClient aemWebClient;
  private final AemProperties aemProperties;

  public MealsInfoResponseAemDto getMealsInfo(MealsRequestAemDto mealsRequestAemDto) {
    log.debug("Entered getMealsInfo with country={}, language={}, hotelId={}",
        mealsRequestAemDto.getCountry(), mealsRequestAemDto.getLanguage(),
        mealsRequestAemDto.getHotelId());
    return aemWebClient.get()
        .uri(
            uriBuilder -> uriBuilder.path(aemProperties.getUpsellItemsEndpoint())
                .build(mealsRequestAemDto.getCountry(), mealsRequestAemDto.getLanguage(),
                    mealsRequestAemDto.getHotelId().charAt(0), mealsRequestAemDto.getHotelId()))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.createException()
              .map(exception -> new AemResponseException(
                  AEM_MEALS_INFO_EXCEPTION, "Unable to get meals info",
                  exception))
              .flatMap(Mono::error);
        })
        .bodyToMono(MealsInfoResponseAemDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}
