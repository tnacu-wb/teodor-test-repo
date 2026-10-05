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
import uk.co.whitbread.payments.model.AuthorizeScaRequest;
import uk.co.whitbread.payments.model.SaveCardRequest;
import uk.co.whitbread.payments.service.impl.DefaultPaymentService;
import uk.co.whitbread.payments.service.impl.DefaultValidationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScaHandler {

    private final DefaultValidationService defaultValidationService;
    private final DefaultPaymentService defaultPaymentService;

    public Mono<ServerResponse> authorizeSca(ServerRequest request) {
        return request.bodyToMono(AuthorizeScaRequest.class)
                .doOnNext(defaultValidationService::validate)
                .doOnSuccess(authorizeScaRequest -> log.info(new ObjectAppendingMarker(
                        "request", authorizeScaRequest), "Incoming request for SCA {}.", authorizeScaRequest))
                .flatMap(defaultPaymentService::authorizeSca)
                .doOnSuccess(paymentsResponse -> log.info("SCA resource successfully created {}.", paymentsResponse))
                .doOnError(error -> log.error("SCA resource creation was not successful.", error))
                .flatMap(authorizeScaRequest -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(authorizeScaRequest)));
    }
}
