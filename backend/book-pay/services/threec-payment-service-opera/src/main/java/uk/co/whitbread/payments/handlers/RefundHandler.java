package uk.co.whitbread.payments.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.RefundRequest;
import uk.co.whitbread.payments.service.impl.DefaultRefundService;
import uk.co.whitbread.payments.service.impl.DefaultValidationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefundHandler {

    private final DefaultRefundService defaultRefundService;
    private final DefaultValidationService defaultValidationService;

    public Mono<ServerResponse> refund(ServerRequest request) {
        return request.bodyToMono(RefundRequest.class)
                .doOnNext(defaultValidationService::validate)
                .doOnSuccess(refundRequest -> log.info("Incoming request to process refund resource."))
                .flatMap(defaultRefundService::refund)
                .doOnSuccess(refundResponse -> log.info("Refund response is captured"))
                .doOnError(error -> log.error("Refund processing was not successful.", error))
                .flatMap(refundResponse -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(refundResponse)));
    }
}
