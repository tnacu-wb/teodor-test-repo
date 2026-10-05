package uk.co.whitbread.payments.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.SaveCardRequest;
import uk.co.whitbread.payments.service.impl.DefaultPaymentService;
import uk.co.whitbread.payments.service.impl.DefaultValidationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardHandler {

  private final DefaultValidationService defaultValidationService;
  private final DefaultPaymentService defaultPaymentService;

  public Mono<ServerResponse> saveCard(ServerRequest request) {
    return request.bodyToMono(SaveCardRequest.class)
        .doOnNext(defaultValidationService::validate)
        .doOnSuccess(saveCardRequest -> log.info(new ObjectAppendingMarker("request", saveCardRequest), "Incoming request to save card resource {}.", saveCardRequest))
        .flatMap(defaultPaymentService::saveCard)
        .doOnSuccess(paymentsResponse -> log.info("Card resource successfully created {}.", paymentsResponse))
        .doOnError(error -> log.error("Card resource creation was not successful.", error))
        .flatMap(saveCardResponse -> ServerResponse.ok().contentType(
                MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(saveCardResponse)));
  }
}
