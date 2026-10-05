package uk.co.whitbread.payments.infrastructure.rest.client.basket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.exceptions.BasketBadRequestException;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.exceptions.BasketInternalException;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.model.out.BasketDto;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.service.properties.BasketClientProperties;
import uk.co.whitbread.payments.infrastructure.rest.client.util.WebClientUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class BasketClient {

  private final WebClient basketWebClient;
  private final BasketClientProperties basketProperties;

  public BasketDto sendGetBasketByReference(String basketReference) {
    return basketWebClient
        .get()
        .uri(uriBuilder -> uriBuilder.path(basketProperties.getBasketEndpoint())
            .build(basketReference))
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response -> {
          if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
            log.info("Basket not found for basketReference={}", basketReference);
            return Mono.empty();
          }
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketBadRequestException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(BasketInternalException.class);
        })
        .bodyToMono(BasketDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex))
        .block();
  }
}
