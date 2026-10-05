package uk.co.whitbread.payments.service.webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.client.HotelBookingClient;
import uk.co.whitbread.payments.config.RevisedSolutionConfig;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.booking.WebhookBookingResponse;
import uk.co.whitbread.payments.repository.PaymentRepository;
import uk.co.whitbread.payments.service.impl.DefaultPaymentService;
import uk.co.whitbread.payments.util.mapper.HotelBookingRequestBuilder;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelBookingWebhookService {

    private static final String PAYMENT_ID = "paymentId";
    private final PaymentRepository paymentRepository;
    private final HotelBookingClient hotelBookingClient;
    private static final String PAYMENT_NOT_FOUND_MESSAGE = "Payment with paymentId %s not found.";
    private final RevisedSolutionConfig revisedSolutionConfig;
    private final DefaultPaymentService defaultPaymentService;
    private final HotelBookingRequestBuilder hotelBookingRequestBuilder;

    public Mono<WebhookBookingResponse> makeBooking(String paymentId) {

        return paymentRepository.getByPaymentId(paymentId)
                .map(paymentsSchemaOptional -> paymentsSchemaOptional.orElseThrow(() -> new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(PAYMENT_NOT_FOUND_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND)))
                .flatMap(paymentsSchema -> {
                    String bookingId = paymentsSchema.getSessionId();
                    if (bookingId != null && revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema)) {
                        log.info("Booking for bookingId {} is processed from revised solution", bookingId);
                        var threecPaymentResponse = defaultPaymentService.createPaymentResponse(paymentId,paymentsSchema);
                        var request = hotelBookingRequestBuilder.buildHotelBookingRequst(threecPaymentResponse);
                        return hotelBookingClient.makeBooking(request, bookingId)
                                .flatMap(this::createWebhookBookingResponse)
                                .doOnSuccess(resp -> {
                                    log.info("Booking from webhook for bookingId {} is processed with status {}", bookingId, resp.getBookingStatus().toString());
                                    var bookingStatus = ofNullable(resp.getBookingStatus().toString()).orElse("PENDING");
                                    var confirmationNumber = ofNullable(resp.getConfirmationNumber()).orElse("");
                                    var code = ofNullable(resp.getCode()).orElse("");
                                })
                                .onErrorResume(ex -> {
                                    log.warn("Incoming request to refund payment {} due to booking service error : {}", paymentId, ex.getMessage());
                                    return defaultPaymentService.refund(paymentId).then(Mono.empty());
                                });

                    } else {
                        return Mono.empty();
                    }
                });
    }

    protected Mono<WebhookBookingResponse> createWebhookBookingResponse(WebhookBookingResponse response) {
        return Mono.just(WebhookBookingResponse.builder()
                .bookingStatus(response.getBookingStatus())
                .sessionId(response.getSessionId())
                .build());
    }
}
