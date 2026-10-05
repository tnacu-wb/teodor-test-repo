package uk.co.whitbread.payments.service.webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.client.BasketClient;
import uk.co.whitbread.payments.config.RevisedSolutionConfig;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.booking.basket.BasketResponse;
import uk.co.whitbread.payments.model.feature.FeatureFlag;
import uk.co.whitbread.payments.model.feature.UnleashWrapper;
import uk.co.whitbread.payments.repository.PaymentRepository;
import uk.co.whitbread.payments.service.impl.DefaultPaymentService;
import uk.co.whitbread.payments.util.mapper.BasketRequestBuilder;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasketWebhookService {

    private static final String PAYMENT_ID = "paymentId";
    private final BasketClient basketClient;
    private final DefaultPaymentService defaultPaymentService;
    private final RevisedSolutionConfig revisedSolutionConfig;
    private final BasketRequestBuilder basketRequestBuilder;
    private final PaymentRepository paymentRepository;
    private static final String PAYMENT_NOT_FOUND_MESSAGE = "Payment with paymentId %s not found.";

    private final UnleashWrapper<FeatureFlag> unleashWrapper;

    public Mono<BasketResponse> makeBooking(String paymentId, String threeDSIndicator) {

        log.info("Making Opera Booking for payment id: {}", paymentId);
        return paymentRepository.getByPaymentId(paymentId)
                .map(paymentsSchemaOptional -> paymentsSchemaOptional.orElseThrow(() -> new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(PAYMENT_NOT_FOUND_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND)))
                .flatMap(paymentsSchema -> {
                    String bookingReference = paymentsSchema.getBooking() == null ? null : paymentsSchema.getBooking().getReference();
                    log.info("Making Opera Booking with booking reference id: {}", bookingReference);
                    if (bookingReference != null && revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema)) {
                        log.info("Booking for Opera booking reference {} is processed from revised solution", bookingReference);
                        try {
                          var paymentStatus = defaultPaymentService.getPaymentStatus(paymentsSchema);
                          var request = basketRequestBuilder.buildBasketRequest(paymentsSchema, paymentStatus,
                              threeDSIndicator, unleashWrapper);
                          return basketClient.makeBooking(request)
                              .doOnSuccess(resp -> {
                                log.info("Booking from webhook for Opera booking reference {}", bookingReference);
                                var reference = ofNullable(resp.getReference()).orElse("");
                              })
                              .onErrorResume(ex -> {
                                log.warn("Incoming request to refund payment {} due to Opera booking service error : {}", paymentId, ex.getMessage());
                                return defaultPaymentService.refund(paymentId).then(Mono.empty());
                              });
                        } catch (NullPointerException e) {
                            log.error("NullPointerException occurred while processing booking for paymentId {}: {}", paymentId, e.getMessage());
                            return defaultPaymentService.refund(paymentId).then(Mono.empty());
                       }
                    } else {
                        return Mono.empty();
                    }
                });
    }
}
