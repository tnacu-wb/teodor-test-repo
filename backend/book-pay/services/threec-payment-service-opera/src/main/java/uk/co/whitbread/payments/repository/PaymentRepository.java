package uk.co.whitbread.payments.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.mapper.SaveCardMapper;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.feature.FeatureFlag;
import uk.co.whitbread.payments.model.feature.UnleashWrapper;
import uk.co.whitbread.payments.properties.ProviderAccount;
import uk.co.whitbread.payments.service.EMerchantService;
import uk.co.whitbread.payments.util.PaymentIdGenerator;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PaymentRepository {

    private static final String REQUEST_ID_INDEX = "request-id";
    private static final String PAYMENT_ERROR_MESSAGE = "Payment with paymentId %s not found.";
    private static final String REFUND_ERROR_MESSAGE = "Payment with refundId %s not found.";
    private final EMerchantService eMerchantService;
    private final PaymentIdGenerator paymentIdGenerator;
    private final SaveCardMapper saveCardMapper;
    private final DynamoDbAsyncTable<PaymentsSchema> paymentsTable;
    private final UnleashWrapper<FeatureFlag> unleashWrapper;

    @Value("${3c.ttl.initial}")
    private long initialTTL;
    @Value("${3c.ttl.final}")
    private long finalTTL;

    public Mono<Optional<PaymentsSchema>> getByRequestId(String requestId) {
        var key = Key.builder()
                .partitionValue(requestId)
                .build();
        var queryConditional = keyEqualTo(key);
        var secondaryIndex = paymentsTable.index(REQUEST_ID_INDEX);
        var publisher = secondaryIndex.query(queryConditional);
        Function<Page<PaymentsSchema>, Optional<PaymentsSchema>> findFirstSchema = paymentsSchemaPage -> paymentsSchemaPage.items().stream().findFirst();
        return Mono.from(publisher).map(findFirstSchema);
    }
    public Mono<Optional<PaymentsSchema>> getByPaymentId(String paymentId) {
        return Mono.just(paymentsTable.getItem(PaymentsSchema.builder().paymentId(paymentId).build()).thenApply(Optional::ofNullable).join());
    }

    public Mono<PaymentsSchema> createPaymentResource(PaymentRequest paymentRequest, ProviderAccount providerAccount) {
        return getByRequestId(paymentRequest.getRequestId())
                .flatMap(paymentsSchema -> paymentsSchema
                        .map(Mono::just)
                        .orElseGet(() -> storePaymentRecord(paymentRequest, providerAccount)));
    }

    public Mono<PaymentsSchema> createPaymentSaveCardResource(SaveCardRequest saveCardRequest, ProviderAccount providerAccount) {
        return getByRequestId(saveCardRequest.getRequestId())
            .flatMap(paymentsSchema -> paymentsSchema
                .map(Mono::just)
                .orElseGet(() -> storePaymentRecord(saveCardRequest, providerAccount)));
    }

    public Mono<PaymentsSchema> createAuthorizeScaResource(AuthorizeScaRequest authorizeScaRequest,
                                                           ProviderAccount providerAccount) {
        return getByRequestId(authorizeScaRequest.getRequestId())
                .flatMap(paymentsSchema -> paymentsSchema
                        .map(Mono::just)
                        .orElseGet(() -> saveScaRecord(authorizeScaRequest, providerAccount)));
    }

    public Mono<PaymentsSchema> updatePaymentResource(String paymentId, ProviderResponse providerResponse) {
        return getByPaymentId(paymentId)
                .map(paymentsSchema -> paymentsSchema.orElseThrow(() -> new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(PAYMENT_ERROR_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND)))
                .map(schema -> {
                    providerResponse.getThreeCResponse().setTemplate(schema.getTemplate());
                    PaymentsSchema updatedPaymentsSchema = schema.toBuilder()
                            .expire(getEpochTimeToLive(finalTTL))
                            .providerResponse(providerResponse).build();
                    return paymentsTable.updateItem(updatedPaymentsSchema).join();
                });
    }

    public Mono<PaymentsSchema> updatePaymentResourceToRefunded(String paymentId) {
        return getByPaymentId(paymentId)
                .flatMap(paymentsSchema ->
                        paymentsSchema.map(schema -> {
                            PaymentsSchema updatedPaymentsSchema = schema.toBuilder().refunded(true).refundedOn(LocalDateTime.now()).build();
                            return Mono.just(paymentsTable.updateItem(updatedPaymentsSchema).join());
                        }).orElseThrow(() -> new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(PAYMENT_ERROR_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND)));
    }

    public Mono<PaymentsSchema> updatePaymentWithBookingReference(String paymentId, String bookingReference) {
        return getByPaymentId(paymentId)
                .flatMap(paymentsSchema ->
                        paymentsSchema.map(schema -> {
                            PaymentsSchema updatedPaymentsSchema = schema.toBuilder().bookingReference(bookingReference).build();
                            return Mono.just(paymentsTable.updateItem(updatedPaymentsSchema).join());
                        }).orElseThrow(() -> new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(PAYMENT_ERROR_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND)));
    }

    private Mono<PaymentsSchema> storePaymentRecord(PaymentRequest paymentRequest, ProviderAccount account) {
        final String paymentId = getPaymentId();
        // get e-merchant details
        EMerchantDetails eMerchantDetails = eMerchantService.getEMerchantDetails(paymentRequest.getPaymentSubType(), paymentRequest.getHotelCode());
        PaymentsSchema paymentsRecord = PaymentsSchema.builder()
                .sessionId(paymentRequest.getSessionId())
                .account(eMerchantDetails.getUsername())
                .createdOn(LocalDateTime.now())
                .booking(paymentRequest.getBooking())
                .payment(mapPayment(paymentRequest.getPayment()))
                .paymentId(paymentId)
                .template(getTemplate(paymentRequest, account))
                .requestId(paymentRequest.getRequestId())
                .bookingReference(paymentRequest.getBooking().getReference()) // The same for BART Opera uses values in booking object
                .expire(getEpochTimeToLive(initialTTL))
                .build().validate();
        return Mono.fromFuture(paymentsTable.putItem(paymentsRecord))
            .thenReturn(paymentsRecord);
    }

    private Mono<PaymentsSchema> storePaymentRecord(SaveCardRequest saveCardRequest, ProviderAccount account) {
        final String paymentId = getPaymentId();
        PaymentsSchema paymentsRecord = PaymentsSchema.builder()
            .createdOn(LocalDateTime.now())
            .paymentId(paymentId)
            .template(getTemplate(saveCardRequest, account))
            .requestId(saveCardRequest.getRequestId())
            .expire(getEpochTimeToLive(initialTTL))
            .booking(Booking.builder()
                .language(saveCardRequest.getLanguage())
                .build())
            .payment(Payment.builder()
                .environment(saveCardRequest.getEnvironment())
                .type(account.getPaymentType().name())
                .subType(PaymentSubType.SAVE_CARD.name()).build())
            .saveCardDetails(saveCardMapper.toDomain(saveCardRequest))
            .build();
        return Mono.just(paymentsTable.putItem(paymentsRecord)
                .thenCompose(a -> paymentsTable.getItem(PaymentsSchema.builder().paymentId(paymentId).build())).join())
            .flatMap(schema -> ofNullable(schema)
                .map(Mono::just)
                .orElseThrow(() -> new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(PAYMENT_ERROR_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND)));
    }

    private Mono<PaymentsSchema> saveScaRecord(AuthorizeScaRequest authorizeScaRequest, ProviderAccount account) {
        final String paymentId = getPaymentId();
        PaymentsSchema paymentsRecord = PaymentsSchema.builder()
                .createdOn(LocalDateTime.now())
                .paymentId(paymentId)
                .template(account.getConfiguration().getNewCardTemplate())
                .requestId(authorizeScaRequest.getRequestId())
                .expire(getEpochTimeToLive(initialTTL))
                .booking(Booking.builder()
                        .language(authorizeScaRequest.getLanguage())
                        .build())
                .payment(Payment.builder()
                        .type(account.getPaymentType().name())
                        .environment(authorizeScaRequest.getEnvironment())
                        .subType(PaymentSubType.AUTHORIZE_CARD.name()).build())
                .bookingReference(authorizeScaRequest.getBookingReference())
                .build();
        return Mono.just(paymentsTable.putItem(paymentsRecord)
                        .thenCompose(a -> paymentsTable.getItem(PaymentsSchema.builder().paymentId(paymentId).build())).join())
                .flatMap(schema -> ofNullable(schema)
                        .map(Mono::just)
                        .orElseThrow(() -> new PaymentServiceException(HttpStatus.NOT_FOUND,
                                String.format(PAYMENT_ERROR_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND)));
    }

    private Payment mapPayment(Payment payment) {
        if (payment.getBilling() != null) {
            var billing = payment.getBilling();
            var billingBuilder = billing.toBuilder();
            var paymentBuilder = payment.toBuilder();
            var firstNameEncoded = billing.getFirstName() != null && !billing.getFirstName().isBlank()
                    ? Base64.getEncoder().encodeToString(billing.getFirstName().getBytes(StandardCharsets.UTF_8))
                    : "";
            var lastNameEncoded = billing.getLastName() != null && !billing.getLastName().isBlank()
                    ? Base64.getEncoder().encodeToString(billing.getLastName().getBytes(StandardCharsets.UTF_8))
                    : "";
            var billingModified = billingBuilder.firstName(firstNameEncoded).lastName(lastNameEncoded).build();
            return paymentBuilder.billing(billingModified).build();
        } else {
            return payment;
        }
    }

    public String getPaymentId() {
        return paymentIdGenerator.generatePaymentId();
    }

    public Mono<PaymentsSchema> createRefundResource(RefundRequest refundRequest) {
        log.info("Refund Resource not found for request id {}", refundRequest.getRequestId());
        return getByRequestId(refundRequest.getRequestId())
                .flatMap(paymentsSchema -> paymentsSchema
                        .map(Mono::just)
                        .orElseGet(() -> storeRefundRecord(refundRequest)));
    }

    public Mono<PaymentsSchema> updateRefundResource(RefundResponse refundResponse) {
        return getByPaymentId(refundResponse.getPaymentId())
                .flatMap(paymentsSchema ->
                        paymentsSchema.map(schema -> {
                            PaymentsSchema updatedPaymentsSchema = schema.toBuilder()
                                    .refunded(refundResponse.isRefunded())
                                    .refundedOn(refundResponse.getRefundedOn())
                                    .providerResponse(refundResponse.getProviderResponse())
                                    .build();
                            return Mono.just(paymentsTable.updateItem(updatedPaymentsSchema).join());
                        }).orElseThrow(() -> new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(REFUND_ERROR_MESSAGE, refundResponse.getPaymentId()), ErrorCodes.PAYMENT_NOT_FOUND)));
    }

    private Mono<PaymentsSchema> storeRefundRecord(RefundRequest refundRequest) {
        String refundId = getPaymentId();
        PaymentsSchema paymentsRecord = PaymentsSchema.builder()
                .createdOn(LocalDateTime.now())
                .booking(refundRequest.getBooking())
                .refund(refundRequest.getRefund())
                .requestId(refundRequest.getRequestId())
                .paymentId(refundId)
                .bookingReference(refundRequest.getBooking().getReference())
                .type("REFUND")
                .expire(getEpochTimeToLive(finalTTL))
                .build();
        return Mono.just(paymentsTable.putItem(paymentsRecord)
                .thenCompose(a -> paymentsTable.getItem(PaymentsSchema.builder().paymentId(refundId).build())).join())
                .flatMap(schema -> ofNullable(schema)
                        .map(Mono::just)
                        .orElseThrow(() -> new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(REFUND_ERROR_MESSAGE, refundId), ErrorCodes.PAYMENT_NOT_FOUND)));
    }

    private String getTemplate(PaymentRequest paymentRequest, ProviderAccount account) {
        return ofNullable(paymentRequest)
            .map(PaymentRequest::getPayment)
            .map(payment -> resolveTemplate(payment, account))
            .orElse(null);
    }

    private String getTemplate(SaveCardRequest saveCardRequest, ProviderAccount account) {
        var config = account.getConfiguration();
        if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getReleasePibaCnpIframeSplit())
            && isPiba(account)
            && saveCardRequest.isCnpRequired()) {
            return ofNullable(config.getNewCardCnpTemplate())
                .filter(t -> !t.isBlank())
                .orElseGet(config::getNewCardTemplate);
        }
        return config.getNewCardTemplate();
    }

    private String resolveTemplate(Payment payment, ProviderAccount account) {
        var config = account.getConfiguration();
        boolean hasToken = ofNullable(payment.getCard())
            .map(Card::getToken)
            .filter(token -> !token.isEmpty())
            .isPresent();

        if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getReleasePibaCnpIframeSplit()) &&
            isPibaCnp(payment, account)) {
            return hasToken
                ? ofNullable(config.getSavedCardCnpTemplate()).filter(t -> !t.isBlank()).orElseGet(config::getSavedCardTemplate)
                : ofNullable(config.getNewCardCnpTemplate()).filter(t -> !t.isBlank()).orElseGet(config::getNewCardTemplate);
        }

        return hasToken ? config.getSavedCardTemplate() : config.getNewCardTemplate();
    }

    private boolean isPiba(ProviderAccount account) {
        return account.getPaymentType() == PaymentType.PIBA || account.getPaymentType() == PaymentType.PIBA_EU;
    }

    private boolean isPibaCnp(Payment payment, ProviderAccount account) {
        return isPiba(account) && !payment.isCardPresent();
    }

    private long getEpochTimeToLive(long ttl) {
        return (LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000) + ttl;
    }
}

