package uk.co.whitbread.payments.service;

import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.UpdateBookingReferenceRequest;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.PaymentResponse;

import java.util.Map;
import java.util.Optional;
import uk.co.whitbread.payments.model.WebHookEckohFormData;

public interface PaymentService {
    Mono<PaymentResponse> createPayment(PaymentRequest paymentRequest);
    Mono<PaymentResponse> getPayment(String paymentId, String action);
    Mono<Optional<PaymentResponse>> refund(String paymentId);
    Mono<PaymentResponse> updateBookingReference(UpdateBookingReferenceRequest request);
    Mono<String> handleRedirect(Map<String, String> queryParams, String paymentId);
}
