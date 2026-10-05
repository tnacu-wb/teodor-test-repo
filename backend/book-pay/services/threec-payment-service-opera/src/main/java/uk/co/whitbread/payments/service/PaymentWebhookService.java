package uk.co.whitbread.payments.service;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.PaymentResponse;

public interface PaymentWebhookService {
  Mono<PaymentResponse> handleWebhook(ServerRequest serverRequest);
}
