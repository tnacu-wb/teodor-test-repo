package uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.basket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.basket.properties.BasketProperties;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.exception.BasketConfirmationException;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.model.in.ConfirmItemProcessingRequestDto;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@Component
@RequiredArgsConstructor
public class BasketClient {

  private final WebClient basketWebClient;
  private final BasketProperties basketProperties;

  public Void sendAcknowledge(final String itemId, final String basketReference,
      final ConfirmItemProcessingRequestDto confirmItemProcessingRequestDto) {
    log.info("Send acknowledge: {}, {}, {}", itemId, basketReference, confirmItemProcessingRequestDto);
    return basketWebClient
        .post()
        .uri(uriBuilder ->
            uriBuilder.path(basketProperties.getConfirmItemProcessingEndpoint())
                .build(basketReference, itemId))
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(confirmItemProcessingRequestDto), ConfirmItemProcessingRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(BasketConfirmationException.class);
        })
        .bodyToMono(Void.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format("An error was returned while "
              + "trying to call Basket Service. Error while trying to send ack "
              + "basketReference=%s", basketReference)))
        .block();
  }
}
