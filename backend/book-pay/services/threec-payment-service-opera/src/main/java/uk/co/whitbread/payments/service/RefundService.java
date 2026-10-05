package uk.co.whitbread.payments.service;

import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.RefundRequest;
import uk.co.whitbread.payments.model.RefundResponse;

public interface RefundService {

    /**
     * Starts refund process for the payments received with NO SHOW
     * @param refundRequest information to process refund
     * @return RefundResponse if the refund is processed successfully
     */
    Mono<RefundResponse> refund(RefundRequest refundRequest);
}
