package uk.co.whitbread.payments.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.config.RevisedSolutionConfig;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.properties.EckohProperties;
import uk.co.whitbread.payments.service.impl.DefaultEckohPaymentWebhookService;
import uk.co.whitbread.payments.service.impl.DefaultPaymentService;
import uk.co.whitbread.payments.service.impl.DefaultPaymentWebhookService;
import uk.co.whitbread.payments.service.impl.DefaultValidationService;
import uk.co.whitbread.payments.service.webhook.BasketWebhookService;
import uk.co.whitbread.payments.service.webhook.HotelBookingWebhookService;
import uk.co.whitbread.payments.service.webhook.HotelCardWebhookService;

import java.util.Optional;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentsHandler {

    private static final String PAYMENT_ID = "paymentId";
    private static final String ACTION = "action";
    private final DefaultPaymentService defaultPaymentService;
    private final DefaultValidationService defaultValidationService;
    private final EckohProperties eckohProperties;
    private final DefaultEckohPaymentWebhookService eckohPaymentWebhookService;
    private final DefaultPaymentWebhookService paymentWebhookService;
    private final HotelBookingWebhookService hotelBookingWebhookService;
    private final BasketWebhookService basketWebhookService;
    private final HotelCardWebhookService hotelCardWebhookService;

    private final RevisedSolutionConfig revisedSolutionConfig;

    public Mono<ServerResponse> createPayment(ServerRequest request) {
        return
                request.bodyToMono(PaymentRequest.class)
                        .doOnNext(defaultValidationService::validate)
                        .doOnSuccess(paymentsRequest -> log.info("Incoming request to create payment resource {}.", paymentsRequest.getBooking().getReference()))
                        .flatMap(defaultPaymentService::createPayment)
                        .doOnSuccess(paymentsResponse -> log.info("Payment resource successfully created {}.", paymentsResponse.getPaymentId()))
                        .doOnError(error -> log.error("Payment resource creation was not successful.", error))
                        .flatMap(paymentsResponse -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(paymentsResponse)));
    }

    public Mono<ServerResponse> getPayment(ServerRequest request) {
        String paymentId = request.pathVariable(PAYMENT_ID);
        String action = request.queryParam(ACTION).orElse("");
        return defaultPaymentService.getPayment(paymentId, action)
                .flatMap(paymentsResponse -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(paymentsResponse)));
    }

    public Mono<ServerResponse> webhook(ServerRequest request) {
      String paymentId = request.pathVariable(PAYMENT_ID);

      return paymentWebhookService.handleWebhook(request)
          .doOnSuccess(webhook -> log.info("Webhook successfully acknowledged for payment resource with id {}.", paymentId))
          .flatMap(paymentResponse -> {
            if (isAuthorizeCardFlow(paymentResponse)) {
              return ServerResponse.noContent().build();
            } else if (isSaveOrUpdateCardFlow(paymentResponse)) {
              //fire and forget
              hotelCardWebhookService.saveOrUpdateCard(paymentResponse).subscribe();
              return ServerResponse.noContent().build();
            } else {
              if (revisedSolutionConfig.isFeatureEnabled()) {
                var reference = paymentResponse.getBooking() == null ? null
                    : paymentResponse.getBooking().getReference();
                var threeDSIndicator = Optional.ofNullable(paymentResponse.getProviderResponse())
                    .map(ProviderResponse::getThreeCResponse)
                    .map(ThreeCResponse::getThreeDSIndicator)
                    .orElse("");
                if (isOpera.test(reference)) {
                  return basketWebhookService.makeBooking(paymentId, threeDSIndicator)
                      .then(ServerResponse.noContent().build());
                } else {
                  return hotelBookingWebhookService.makeBooking(paymentId)
                      .then(ServerResponse.noContent().build());
                }
              }
            }
            return ServerResponse.noContent().build();
          });
    }

    private static boolean isSaveOrUpdateCardFlow(PaymentResponse paymentResponse) {
        return paymentResponse.getPayment() != null
            && paymentResponse.getPayment().getSubType() != null
            && PaymentSubType.SAVE_CARD.name().equals(paymentResponse.getPayment().getSubType());
    }

    private boolean isAuthorizeCardFlow(PaymentResponse paymentResponse) {
        return paymentResponse.getPayment() != null
                && paymentResponse.getPayment().getSubType() != null
                && PaymentSubType.AUTHORIZE_CARD.name().equals(paymentResponse.getPayment().getSubType());
    }

    public Mono<ServerResponse> webhookEckoh(ServerRequest request) {
        if (eckohProperties.isEnabled()) {
            var formDataMap = request.bodyToMono(WebHookEckohFormData.class);
            return eckohPaymentWebhookService.handleWebhook(request)
                    .doOnSuccess(webhook -> log
                            .info("Webhook successfully acknowledged for payment resource with id {}.",
                                    formDataMap.map(WebHookEckohFormData::getPaymentId)))
                    .flatMap(paymentResponse -> ServerResponse.noContent().build());
        } else {
            throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to handle webhook callback request.",
                    ErrorCodes.ERROR_HANDLING_REQUEST);
        }
    }

    public Mono<ServerResponse> redirect(ServerRequest request) {
        final String paymentId = request.pathVariable(PAYMENT_ID);
        final var queryParams = request.queryParams().toSingleValueMap();
        log.info(new ObjectAppendingMarker("params", queryParams), "Incoming redirect for payment resource with id {}.", paymentId);
        return defaultPaymentService.handleRedirect(queryParams, paymentId)
                .flatMap(redirectHtml -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(BodyInserters.fromValue(redirectHtml)));
    }

    public Mono<ServerResponse> transactionalRefund(ServerRequest request) {
        String paymentId = request.pathVariable(PAYMENT_ID);
        log.info("Incoming request to refund payment resource with id {}.", paymentId);
        return defaultPaymentService.refund(paymentId)
                .doOnNext(paymentResponse -> {
                    if (paymentResponse.isPresent() && paymentResponse.get().isRefunded()) {
                        log.info("Successful transactional refund for payment resource with id {}.", paymentId);
                    } else {
                        log.info("Refund for payment resource with id {} is not processed.", paymentId);
                    }
                })
                .flatMap(paymentResponse -> ServerResponse.noContent().build())
                .switchIfEmpty(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateBookingReference(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UpdateBookingReferenceRequest.class)
                .doOnNext(defaultValidationService::validate)
                .doOnSuccess(request -> log.info(new ObjectAppendingMarker("updateBookingReference", request), "Incoming request to update payment resource for payment ID {}.", request.getPaymentId()))
                .flatMap(defaultPaymentService::updateBookingReference)
                .doOnNext(response -> log.info("Payment resource with id {} is updated with booking reference {}.", response.getPayment(), response.getBookingReference()))
                .flatMap(paymentResponse -> ServerResponse.accepted().build())
                .switchIfEmpty(ServerResponse.noContent().build());
    }

    /**
     * There will not be a booingReference for BART bookings until they are confirmed but with OPERA we will have one
     */
    Predicate<String> isOpera = booingReference -> booingReference != null && !booingReference.equals("");
}