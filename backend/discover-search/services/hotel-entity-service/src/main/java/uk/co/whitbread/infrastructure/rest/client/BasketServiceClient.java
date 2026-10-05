package uk.co.whitbread.infrastructure.rest.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.basket.generated.models.BasketDto;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.infrastructure.config.BasketProperties;
import uk.co.whitbread.infrastructure.rest.client.basket.exceptions.BasketServiceException;
import uk.co.whitbread.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
public class BasketServiceClient {

  private final WebClient basketServiceWebClient;
  private final BasketProperties basketProperties;

  public BasketDto getBasket(String basketReference) {
    return basketServiceWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getBasketEndpoint())
            .build(basketReference))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(BasketServiceException.class);
        })
        .bodyToMono(BasketDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to get basket"))
        .block();
  }
}
