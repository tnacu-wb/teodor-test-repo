package uk.co.whitbread.payments.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.PaymentSubType;
import uk.co.whitbread.payments.model.ReconciliationRequest;
import uk.co.whitbread.payments.model.threec.StartReconciliationResponse;
import uk.co.whitbread.payments.service.ReconciliationService;
import uk.co.whitbread.payments.client.ThreeCPaymentClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultReconciliationService implements ReconciliationService {

    private final ThreeCPaymentClient threeCPaymentClient;

    @Override
    public Mono<Boolean> reconcile(ReconciliationRequest reconciliationRequest) {
        log.info("Incoming request to start reconciliation for Whitbread site.");
        var siteIdentifier = reconciliationRequest.getBusinessSite().getIdentifier();
        var ecommResponse = threeCPaymentClient.startReconciliation(siteIdentifier, PaymentSubType.ECOMM.name()); //eWB-HOTEL_CODE
        var motoResponse = threeCPaymentClient.startReconciliation(siteIdentifier, PaymentSubType.MOTO.name()); //mWB-HOTEL_CODE
        return Flux.concat(List.of(ecommResponse, motoResponse))
                .collectList()
                .map(response -> {
                    for (StartReconciliationResponse startReconciliationResponse : response) {
                        if (startReconciliationResponse.getReturnCode() != 0) {
                            throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Error encountered triggering reconciliation for site.", ErrorCodes.PROVIDER_ERROR);
                        } else {
                          return startReconciliationResponse;
                        }
                    }
                    return response;
                })
                .doOnSuccess(startReconciliationResponse -> log.info("Successfully started reconciliation for Whitbread site {}.", siteIdentifier))
                .doOnError(throwable -> log.info(new ObjectAppendingMarker("reconciliationError", throwable), "Error occurred when starting reconciliation for Whitbread site."))
                .thenReturn(true);
    }
}
