package uk.co.whitbread.payments.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.ReconciliationRequest;
import uk.co.whitbread.payments.service.ReconciliationService;
import uk.co.whitbread.payments.service.ValidationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReconciliationHandler {

    private final ReconciliationService reconciliationService;
    private final ValidationService validationService;

    public Mono<ServerResponse> reconcile(ServerRequest request) {
        return request.bodyToMono(ReconciliationRequest.class)
                .doOnNext(validationService::validate)
                .flatMap(reconciliationService::reconcile)
                .flatMap(success -> ServerResponse.noContent().build());
    }
}