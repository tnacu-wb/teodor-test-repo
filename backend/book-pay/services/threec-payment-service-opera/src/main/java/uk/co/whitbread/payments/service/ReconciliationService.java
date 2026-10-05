package uk.co.whitbread.payments.service;

import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.ReconciliationRequest;

public interface ReconciliationService {
    /**
     * Starts reconciliation process for Whitbread Site with payment provider
     * @param reconciliationRequest information regarding site to start reconciliation for
     * @return a boolean indicating whether the reconcilation started successfully
     */
    Mono<Boolean> reconcile(ReconciliationRequest reconciliationRequest);
}
