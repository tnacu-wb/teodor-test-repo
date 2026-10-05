package uk.co.whitbread.payments.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.ProviderResponse;
import uk.co.whitbread.payments.model.RefundRequest;
import uk.co.whitbread.payments.model.RefundResponse;
import uk.co.whitbread.payments.model.ThreeCResponse;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionResponse;
import uk.co.whitbread.payments.repository.PaymentRepository;
import uk.co.whitbread.payments.service.ProviderAccountFactory;
import uk.co.whitbread.payments.service.RefundService;
import uk.co.whitbread.payments.client.ThreeCPaymentClient;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRefundService implements RefundService {

    private final ThreeCPaymentClient threeCPaymentClient;
    private final PaymentRepository paymentRepository;
    private final ProviderAccountFactory providerAccountFactory;

    @Override
    public Mono<RefundResponse> refund(RefundRequest refundRequest) {
        String requestId = refundRequest.getRequestId();
        return paymentRepository
                .getByRequestId(requestId)
                .flatMap(schema -> schema
                        .map(paymentsSchema -> {
                            var refundId = paymentsSchema.getPaymentId();
                            if (paymentsSchema.isRefunded()) {
                                log.info("Payment is already refunded for the request id {}", requestId);
                                return Mono.just(createRefundResponse(refundRequest, paymentsSchema));
                            } else {
                                return processNonTransactionalRefund(refundRequest, requestId, refundId);
                            }
                        })
                        .orElseGet(() -> paymentRepository.createRefundResource(refundRequest)
                            .flatMap(paymentsSchema -> processNonTransactionalRefund(refundRequest, requestId, paymentsSchema.getPaymentId())))
                );
    }

    private Mono<RefundResponse> processNonTransactionalRefund(RefundRequest refundRequest, String requestId, String refundId) {
        log.info("Payment is not refunded for the request id {}", requestId);
        String siteIdentifier = refundRequest.getBooking().getBusinessSite().getIdentifier();
        var account = providerAccountFactory.getAccountForSite(siteIdentifier);
        return threeCPaymentClient.noCardReadRequest(refundRequest, requestId, account)
                .flatMap(noCardReadTransactionResponse ->
                        paymentRepository.updateRefundResource(handleNoCardReadTransactionResponse(noCardReadTransactionResponse, refundId))
                )
                .map(paymentsSchema -> createRefundResponse(refundRequest, paymentsSchema));
    }

    private RefundResponse createRefundResponse(RefundRequest refundRequest, PaymentsSchema paymentsSchema) {
        return RefundResponse
                .builder()
                .requestId(refundRequest.getRequestId())
                .refundId(paymentsSchema.getPaymentId())
                .refunded(paymentsSchema.isRefunded())
                .refundedOn(paymentsSchema.getRefundedOn())
                .providerResponse(paymentsSchema.getProviderResponse())
                .refund(refundRequest.getRefund())
                .booking(refundRequest.getBooking())
                .build();
    }

    private RefundResponse handleNoCardReadTransactionResponse(NoCardReadTransactionResponse noCardReadTransactionResponse, String refundId) {
        var response = noCardReadTransactionResponse.getResponse();
        var params = response.getParams();
        boolean refundFlag = false;
        LocalDateTime refundedOn = null;
        if ("0".equals(params.getResult())) {
            log.info("3CP request has been successful for the refund with id {}", refundId);
            refundFlag = true;
            refundedOn = LocalDateTime.now();
        }
        return RefundResponse.builder()
                .paymentId(refundId)
                .refunded(refundFlag)
                .refundedOn(refundedOn)
                .providerResponse(
                        ProviderResponse.builder()
                                .providerReference(params.getProviderReference())
                                .transactionReference(params.getTransactionReference())
                                .threeCResponse(ThreeCResponse.builder()
                                        .authCode(params.getAuthCode())
                                        .cardSchemeId(params.getCardType())
                                        .cardSchemeName(params.getCardTypeName())
                                        .expiry(params.getExpiry())
                                        .providerStatus(params.getTransactionState())
                                        .token(params.getToken())
                                        .avsResult(params.getAvsResult())
                                        .binRange(params.getBinRange())
                                        .last4Digits(params.getLast4Digits())
                                        .providerReason(params.getReason())
                                        .providerResult(params.getResult())
                                        .providerStatusText(params.getTransactionStateText())
                                        .scaReference(params.getScaReference())
                                        .build())
                                .build())
                .build();
    }

    private String generateId(){
        return paymentRepository.getPaymentId();
    }
}
