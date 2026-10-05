package uk.co.whitbread.payments.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.client.ThreeCPaymentClient;
import uk.co.whitbread.payments.config.PaypalConfig;
import uk.co.whitbread.payments.config.RevisedSolutionConfig;
import uk.co.whitbread.payments.exception.ErrorCode;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentProcessingException;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.threec.InitialiseResponse;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionResponse;
import uk.co.whitbread.payments.model.threec.PaymentProviderTransactionResponse;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionResponse;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionResponseBody;
import uk.co.whitbread.payments.properties.EckohProperties;
import uk.co.whitbread.payments.properties.ThreeCProperties;
import uk.co.whitbread.payments.repository.PaymentRepository;
import uk.co.whitbread.payments.service.PaymentService;
import uk.co.whitbread.payments.service.ProviderAccountFactory;
import uk.co.whitbread.payments.service.TemplateService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static uk.co.whitbread.payments.model.PaymentSubType.MIT_CC;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultPaymentService implements PaymentService {

    public static final String PAYMENT_NOT_FOUND_MESSAGE = "Payment with paymentId %s not found.";
    public static final String PROVIDER_RESPONSE_EXISTS_MSG = "Returning response as payment resource already has provider response.";
    private static final int INITIALISE_IPAGE_SUCCESS_RESULT_CODE = 0;
    private final ThreeCPaymentClient threeCPaymentClient;
    private final PaymentRepository paymentRepository;
    private final ProviderAccountFactory providerAccountFactory;
    private final EckohProperties eckohProperties;
    private final TemplateService templateService;
    private final ThreeCProperties threeCProperties;
    private final RevisedSolutionConfig revisedSolutionConfig;
    @Value("#{'${3c.refunds.status}'.split(',')}")
    protected List<String> validRefundStatus;
    @Value("#{'${3c.provider.success}'.split(',')}")
    protected List<String> successProviderStatus;
    @Value("#{'${3c.provider.failure}'.split(',')}")
    protected List<String> failureProviderStatus;
    @Value("#{'${3c.fraudCheck.valid}'.split(',')}")
    protected List<String> fraudCheckAccept;
    @Value("#{'${3c.noTransaction.code}'.split(',')}")
    protected List<String> noTransactionCode;

    public static final String PAYMENT_TYPE_PAY_NOW = "PAY_NOW";
    private final PaypalConfig paypalConfig;
    public static final String PAYMENT_TYPE_PAYPAL = "PAYPAL";
    public static final String PAYMENT_REQUEST_ERROR_MSG = "Unable to handle create payment request.";

    @Value("${3c.mit.success.trxState}")
    protected String successTrxState;
    @Value("${3c.mit.success.result}")
    protected String successResult;

    @Override
    public Mono<PaymentResponse> createPayment(PaymentRequest paymentRequest) {
        var account = providerAccountFactory.getAccount(paymentRequest);
        return paymentRepository.createPaymentResource(paymentRequest, account)
                .flatMap(paymentsSchema -> {
                    if (paymentsSchema.getProviderResponse() != null) {
                        log.info(PROVIDER_RESPONSE_EXISTS_MSG);
                        return Mono.just(createPaymentResponse(paymentsSchema.getPaymentId(), paymentsSchema));
                    }
                    String paymentId = paymentsSchema.getPaymentId();
                    String cardTemplate = paymentsSchema.getTemplate();
                    switch ( PaymentSubType.valueOf(paymentRequest.getPayment().getSubType()) ) {
                        case ECOMM, SECURE_BOOKING:
                            return threeCPaymentClient.initialiseIpage(paymentRequest, paymentId, cardTemplate, account)
                                    .flatMap(response -> handlePaymentResponse(response, paymentsSchema, paymentId));
                        case MOTO:
                            return threeCPaymentClient.noCardReadRequest(paymentRequest, paymentId, account)
                                    .flatMap(response -> handlePaymentResponse(response, paymentId, paymentRequest.getPayment().getType()));
                        case ECKOH:
                            if (eckohProperties.isEnabled()) {
                                return Mono.just(PaymentResponse.builder()
                                        .paymentId(paymentId)
                                        .build());
                            }
                            throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                                    PAYMENT_REQUEST_ERROR_MSG, ErrorCodes.ERROR_HANDLING_REQUEST);
                        case MIT:
                            return threeCPaymentClient.paypalRequest(paymentRequest, paymentId, account)
                                    .flatMap(response -> {
                                        log.debug(new ObjectAppendingMarker("PaypalForwardingAPIResponse", response), "Paypal Forwarding API Response {}.", response);
                                        if (response.getStatus() == 200 && paymentRequest.getBooking().getType().equalsIgnoreCase(PAYMENT_TYPE_PAY_NOW)) {
                                            return threeCPaymentClient.paypalMitRequest(response, paymentRequest, paymentId, account)
                                                    .flatMap(paypalMitResponse -> handlePaymentResponse(paypalMitResponse, paymentId, paymentRequest.getPayment().getType())
                                                            .doOnSuccess(mitSuccessResponse -> log.debug(new ObjectAppendingMarker("paypalMitResponse", paypalMitResponse), "PayPal MIT Response {}.", paypalMitResponse)));
                                        } else if (response.getStatus() != 200) {
                                            log.error("paypal error with forwarding api response: {}", response);
                                            throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                                                    "Unable to handle paypal create payment request.", ErrorCodes.ERROR_HANDLING_REQUEST);
                                        }
                                        return handlePaypalPaymentResponse(response, paymentId, paymentsSchema);
                                    })
                                    .onErrorMap(PaymentServiceException.class, exception -> {
                                        log.error("Paypal payment unsuccessful for payment: {}", paymentId);
                                        throw exception;
                                    })
                                    .timeout(Duration.ofMillis(paypalConfig.getTimeout()))
                                    .onErrorMap(TimeoutException.class, exception -> {
                                        log.error("Paypal timeout error with payment: {}",paymentId);
                                        return new PaymentServiceException(HttpStatus.GATEWAY_TIMEOUT, "Unable to handle create paypal payment request due to timed out", ErrorCodes.PAYPAL_TIMEOUT);
                                    });
                      case MIT_CC:
                        return threeCPaymentClient.mitCcRequest(paymentRequest, paymentId, account)
                            .flatMap(response -> {
                              log.debug(new ObjectAppendingMarker("mitCcResponse", response),
                                  "MIT_CC NoCardRead Response {}.", response);

                              return handlePaymentResponse(response, paymentId, paymentRequest.getPayment().getSubType());
                            })
                            .onErrorMap(PaymentProcessingException.class, exception -> {
                              log.error("MIT_CC payment unsuccessful for payment: {}", paymentId);
                              throw exception;
                            })
                            .timeout(Duration.ofMillis(paypalConfig.getTimeout()))
                            .onErrorMap(TimeoutException.class, exception -> {
                              log.error("MIT_CC timeout error with payment: {}", paymentId);
                              return new PaymentProcessingException(ErrorCode.DIGITAL_THREEC_TIMEOUT_EXCEPTION,
                                  "Unable to handle MIT_CC payment request due to timeout"
                               );
                            });
                        default:
                            throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                                    PAYMENT_REQUEST_ERROR_MSG, ErrorCodes.ERROR_HANDLING_REQUEST);
                    }
                });
    }

    @Override
    public Mono<PaymentResponse> getPayment(String paymentId, String action) {

        if(action.equalsIgnoreCase("poll")){
            log.info("Get provider response for paymentId {} with action : {}",paymentId,action);
            return paymentRepository.getByPaymentId(paymentId)
                    .map(paymentsSchemaOptional -> paymentsSchemaOptional.orElseThrow(() -> new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(PAYMENT_NOT_FOUND_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND)))
                    .flatMap(paymentsSchema -> Mono.just(createPaymentResponseForPolling(paymentId, paymentsSchema)));
        }else {
            return paymentRepository.getByPaymentId(paymentId)
                    .map(paymentsSchemaOptional -> paymentsSchemaOptional.orElseThrow(() -> new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(PAYMENT_NOT_FOUND_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND)))
                    .flatMap(paymentsSchema -> {
                        if (paymentsSchema.getProviderResponse() == null) {
                            log.info("Provider Response is not present for Payment with id {}", paymentId);
                            return threeCPaymentClient.retrievePaymentProviderTransactionResponse(paymentsSchema)
                                    .flatMap(response -> handlePaymentResponse(response, paymentId, paymentsSchema))
                                    .defaultIfEmpty(createPaymentResponse(paymentId, paymentsSchema));
                        }else {
                            log.info("Provider Response is already present for Payment with id {}", paymentId);
                            return Mono.just(createPaymentResponse(paymentId, paymentsSchema));
                        }
                    });
        }
    }

    @Override
    public Mono<String> handleRedirect(Map<String, String> queryParams, String paymentId) {
        log.info("Redirect request received for payment ID {}.", paymentId);
        return paymentRepository.getByPaymentId(paymentId)
                .map(paymentsSchemaOptional -> paymentsSchemaOptional.orElseThrow(() -> new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(PAYMENT_NOT_FOUND_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND)))
                .map(paymentsSchema -> {
                    var environment = paymentsSchema.getPayment().getEnvironment();
                    var paymentStatus = isNull(paymentsSchema.getProviderResponse())
                            ? PaymentStatus.PENDING.getStatus() : getPaymentStatus(paymentsSchema);
                    return templateService.getRedirectHtml(environment, queryParams, paymentId, paymentStatus);
                });
    }

    /**
     * Fetches payment item from DB and refunds based on transaction id if not already processed.
     *
     * @param paymentId the unique identifier for a payment resource
     * @return an optional payment response, empty if refund has already been processed.
     */
    @Override
    public Mono<Optional<PaymentResponse>> refund(String paymentId) {
        return paymentRepository.getByPaymentId(paymentId)
                .map(paymentsSchemaOptional -> paymentsSchemaOptional.orElseThrow(() -> new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(PAYMENT_NOT_FOUND_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND)))
                .filter(paymentsSchema -> !paymentsSchema.isRefunded())
                .filter(paymentsSchema -> paymentsSchema.getProviderResponse() != null)
                .filter(paymentsSchema -> paymentsSchema.getProviderResponse().getThreeCResponse() != null)
                .filter(paymentsSchema -> validRefundStatus.contains(paymentsSchema.getProviderResponse().getThreeCResponse().getProviderStatus())) // don't refund if the payment state is not correct
                .flatMap(threeCPaymentClient::refund)
                .map(response -> {
                    var params = response.getResponse().getParams();
                    if (!"0".equals(params.getResult())) {
                        throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error encountered during transactional refund [%s].", params.getReason()), ErrorCodes.PROVIDER_ERROR);
                    } else {
                        log.info("Successfully refunded transaction.");
                        return response;
                    }
                })
                .flatMap(reverseByTransactionIdResponse -> paymentRepository.updatePaymentResourceToRefunded(paymentId))
                .map(paymentsSchema -> Optional.of(PaymentResponse.builder()
                        .paymentId(paymentId)
                        .payment(paymentsSchema.getPayment())
                        .booking(paymentsSchema.getBooking())
                        .providerResponse(paymentsSchema.getProviderResponse())
                        .refunded(true)
                        .build()))
                .switchIfEmpty(Mono.just(Optional.empty()));
    }

    @Override
    public Mono<PaymentResponse> updateBookingReference(UpdateBookingReferenceRequest request) {
        final String paymentId = request.getPaymentId();
        return paymentRepository.getByPaymentId(paymentId)
                .map(paymentsSchemaOptional -> paymentsSchemaOptional.orElseThrow(() -> new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(PAYMENT_NOT_FOUND_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND)))
                .flatMap(paymentsSchema -> {
                    if (paymentsSchema.getBookingReference() == null) {
                        log.debug("Booking Reference is not present for payment with id {}", paymentId);
                        return paymentRepository.updatePaymentWithBookingReference(paymentId, request.getBookingReference())
                                .map(ps -> createPaymentResponse(paymentId, ps));
                    } else {
                        return Mono.just(createPaymentResponse(paymentId, paymentsSchema));
                    }
                });
    }

    private Mono<PaymentResponse> handlePaymentResponse(NoCardReadTransactionResponse noCardReadTransactionResponse, String paymentId, String paymentType) {
        log.debug("NoCardReadResponse obtained : {}",noCardReadTransactionResponse);
        var response = noCardReadTransactionResponse.getResponse();
        var params = response.getParams();
        var fraudResponse = Optional.ofNullable(params.getFraudResponse()).orElse(new NoCardReadTransactionResponse.FraudResponse());
        var paymentResponse = PaymentResponse.builder()
                .paymentId(paymentId)
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
                                        .fraudCheckDecision(fraudResponse.getDecision())
                                        .fraudCheckResult(fraudResponse.getReasonCode())
                                        .fraudCheckRequestId(fraudResponse.getRequestId())
                                        .build())
                                .build())
                .build();
        if(paymentType.equalsIgnoreCase(PAYMENT_TYPE_PAYPAL)){
          if (!isThreeCMITPaymentSuccess(paymentResponse)) {
                throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("PayPal MIT Transaction Declined/Refused [%s].", paymentResponse.getProviderResponse().getThreeCResponse().getStatus()), ErrorCodes.PAYPAL_REFUSED);
            }
            paymentResponse.getProviderResponse().getThreeCResponse().setExpiry(isNull(params.getExpiry())?"":formattedExpiryDate(params.getExpiry()));
        }
        if(paymentType.equalsIgnoreCase(MIT_CC.name())){
          if (!isThreeCMITPaymentSuccess(paymentResponse)) {
            throw new PaymentProcessingException(ErrorCode.DIGITAL_THREEC_MIT_CC_PAYMENT_EXCEPTION, String.format(" MIT_CC Transaction Declined/Refused [%s].", paymentResponse.getProviderResponse().getThreeCResponse().getStatus()));
          }
          paymentResponse.getProviderResponse().getThreeCResponse().setExpiry(isNull(params.getExpiry())?"":formattedExpiryDate(params.getExpiry()));
        }
        return paymentRepository.updatePaymentResource(paymentId, paymentResponse.getProviderResponse())
                .flatMap(result -> Mono.just(createPaymentResponse(paymentId, result)));
    }

    Mono<PaymentResponse> handlePaymentResponse(InitialiseResponse initialiseResponse, PaymentsSchema paymentsSchema, String paymentId) {
        if (initialiseResponse.getIpgResultCode() != INITIALISE_IPAGE_SUCCESS_RESULT_CODE) {
            throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Unable to setup iPage session [%s][%s].", initialiseResponse.getIpgResultCode(), initialiseResponse.getIpgResultText()), ErrorCodes.PROVIDER_ERROR);
        }

        String language = paymentsSchema.getBooking().getLanguage();
        String session = initialiseResponse.getIpgSession();
        LocalDate departureDate = paymentsSchema.getBooking().getDepartureDate();
        String iPageHtml = templateService.getIPageHtml(session, language, paymentId, departureDate);
        return Mono.just(PaymentResponse.builder()
                .paymentId(paymentId)
                .providerResponse(
                        ProviderResponse.builder()
                                .threeCResponse(ThreeCResponse.builder()
                                        .iPageHtml(Base64.getEncoder().encodeToString(iPageHtml.getBytes()))
                                        .sessionId(session)
                                        .template(paymentsSchema.getTemplate())
                                        .providerUrl(threeCProperties.getServer())
                                        .build()).build()
                )
                .booking(paymentsSchema.getBooking())
                .payment(paymentsSchema.getPayment())
                .revisedSolution(revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema))
                .build());
    }

    Mono<PaymentResponse> handleSaveCardResponse(InitialiseResponse initialiseResponse, PaymentsSchema paymentsSchema, String paymentId) {
        if (initialiseResponse.getIpgResultCode() != INITIALISE_IPAGE_SUCCESS_RESULT_CODE) {
            throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Unable to setup iPage session [%s][%s].", initialiseResponse.getIpgResultCode(), initialiseResponse.getIpgResultText()), ErrorCodes.PROVIDER_ERROR);
        }

        String language = paymentsSchema.getSaveCardDetails().getLanguage();
        String session = initialiseResponse.getIpgSession();
        String iPageHtml = templateService.getIPageHtml(session, language, paymentId, null);
        return Mono.just(PaymentResponse.builder()
            .paymentId(paymentId)
            .providerResponse(
                ProviderResponse.builder()
                    .threeCResponse(ThreeCResponse.builder()
                        .iPageHtml(Base64.getEncoder().encodeToString(iPageHtml.getBytes()))
                        .sessionId(session)
                        .template(paymentsSchema.getTemplate())
                        .providerUrl(threeCProperties.getServer())
                        .build()).build()
            )
            .build());
    }

    Mono<PaymentResponse> handleAuthorizeScaResponse(InitialiseResponse initialiseResponse, PaymentsSchema paymentsSchema,
                                                     String paymentId, String language) {
        if (initialiseResponse.getIpgResultCode() != INITIALISE_IPAGE_SUCCESS_RESULT_CODE) {
            throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Unable to setup iPage session [%s][%s].",
                            initialiseResponse.getIpgResultCode(), initialiseResponse.getIpgResultText()),
                    ErrorCodes.PROVIDER_ERROR);
        }

        String session = initialiseResponse.getIpgSession();
        String iPageHtml = templateService.getIPageHtml(session, language, paymentId, null);
        String encodedIPageHtml = Base64.getEncoder().encodeToString(iPageHtml.getBytes());
        return Mono.just(PaymentResponse.builder()
                .paymentId(paymentId)
                .providerResponse(
                        ProviderResponse.builder()
                                .threeCResponse(ThreeCResponse.builder()
                                        .iPageHtml(encodedIPageHtml)
                                        .sessionId(session)
                                        .template(paymentsSchema.getTemplate())
                                        .providerUrl(threeCProperties.getServer())
                                        .build()).build()
                )
                .build());
    }

    public PaymentResponse createPaymentResponse(String paymentId, PaymentsSchema paymentsSchema) {
        return PaymentResponse
                .builder()
                .paymentId(paymentId)
                .booking(paymentsSchema.getBooking())
                .payment(paymentsSchema.getPayment())
                .providerResponse(paymentsSchema.getProviderResponse())
                .createdOn(paymentsSchema.getCreatedOn())
                .refunded(paymentsSchema.isRefunded())
                .refundedOn(paymentsSchema.getRefundedOn())
                .bookingReference(paymentsSchema.getBookingReference())
                .paymentStatus(isNull(paymentsSchema.getProviderResponse()) ? PaymentStatus.PENDING.getStatus() : getPaymentStatus(paymentsSchema))
                .revisedSolution(revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema))
                .build();
    }

    public PaymentResponse createAuthorizeScaPaymentResponse(String paymentId,
        PaymentsSchema paymentsSchema) {
        return PaymentResponse
            .builder()
            .paymentId(paymentId)
            .createdOn(paymentsSchema.getCreatedOn())
            .paymentStatus(
                isNull(paymentsSchema.getProviderResponse()) ? PaymentStatus.PENDING.getStatus()
                    : getPaymentStatus(paymentsSchema))
                .providerResponse(paymentsSchema.getProviderResponse())
            .build();
    }

    public String getPaymentStatus(PaymentsSchema paymentsSchema) {
        var providerStatus = Optional.ofNullable(paymentsSchema.getProviderResponse().getThreeCResponse().getProviderStatus()).orElse("");
        var providerResult = Optional.ofNullable(paymentsSchema.getProviderResponse().getThreeCResponse().getProviderResult()).orElse("");
        var fraudDecision = Optional.ofNullable(paymentsSchema.getProviderResponse().getThreeCResponse().getFraudCheckDecision()).orElse("");
        if (noTransactionCode.contains(providerResult)) {
            return PaymentStatus.NO_PAYMENT_ATTEMPT.getStatus();
        }
        if (successProviderStatus.contains(providerStatus)) {
            if(fraudDecision.isEmpty() || fraudCheckAccept.contains(fraudDecision)){
                return PaymentStatus.SUCCESS.getStatus();
            } else{
                return PaymentStatus.FAILURE.getStatus();
            }
        }
        if (failureProviderStatus.contains(providerStatus)) {
            return PaymentStatus.FAILURE.getStatus();
        }
        return PaymentStatus.PENDING.getStatus();
    }

    private PaymentResponse createPaymentResponseForPolling(String paymentId, PaymentsSchema paymentsSchema) {
        return PaymentResponse
                .builder()
                .paymentId(paymentId)
                .booking(paymentsSchema.getBooking())
                .payment(paymentsSchema.getPayment())
                .providerResponse(paymentsSchema.getProviderResponse())
                .createdOn(paymentsSchema.getCreatedOn())
                .refunded(paymentsSchema.isRefunded())
                .refundedOn(paymentsSchema.getRefundedOn())
                .bookingReference(paymentsSchema.getBookingReference())
                .paymentStatus(isNull(paymentsSchema.getProviderResponse()) ? PaymentStatus.NO_PAYMENT_ATTEMPT.getStatus() : PaymentStatus.SUCCESS.getStatus())
                .revisedSolution(revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema))
                .build();
    }

    private Mono<PaymentResponse> handlePaymentResponse(PaymentProviderTransactionResponse paymentProviderTransactionResponse, String paymentId, PaymentsSchema paymentsSchema) {
        var response = paymentProviderTransactionResponse.getResponse();
        var params = response.getParams();
        var providerResponse = ProviderResponse.builder()
                .providerReference(params.getProviderReference())
                .transactionReference(params.getTransactionReference())
                .threeCResponse(ThreeCResponse.builder()
                        .authCode(params.getAuthCode())
                        .cardSchemeId(params.getCardType())
                        .cardSchemeName(params.getCardTypeName())
                        .expiry(getExpiryDate(params))
                        .providerStatus(params.getTransactionState())
                        .token(params.getToken())
                        .avsResult(params.getAvsResult())
                        .binRange(params.getBinRange())
                        .last4Digits(params.getLast4Digits())
                        .providerReason(params.getReason())
                        .providerResult(params.getResult())
                        .providerStatusText(params.getTransactionStateText())
                        .scaReference(isNull(params.getScaReference()) ? "" : params.getScaReference().trim())
                        .threeDSIndicator(params.getThreeDSIndicator())
                        .fraudCheckDecision(isNull(params.getFraudResponse()) ? "" : params.getFraudResponse().getDecision())
                        .fraudCheckRequestId(isNull(params.getFraudResponse()) ? "" : params.getFraudResponse().getRequestId())
                        .fraudCheckResult(isNull(params.getFraudResponse()) ? "" : params.getFraudResponse().getReasonCode())
                        .cardholderFirstName(isNull(paymentsSchema.getPayment().getBilling()) ? "" : paymentsSchema.getPayment().getBilling().getFirstName())
                        .cardholderLastName(isNull(paymentsSchema.getPayment().getBilling()) ? "" : paymentsSchema.getPayment().getBilling().getLastName())
                        .date(LocalDate.now().toString())
                        .time(LocalTime.now().toString())
                        .build())
                .build();
        paymentsSchema.setProviderResponse(providerResponse);
        return Mono.just(createPaymentResponse(paymentId, paymentsSchema));
    }

    String getExpiryDate(ObjectNode objectNode) {
        var cardExpiry = ofNullable(objectNode.get("CardExpiry"));
        var tokenExpiry = ofNullable(objectNode.get("TokenExpiry"));
        if (cardExpiry.isPresent() && !cardExpiry.get().asText().isBlank()) {
            return formattedWebhookExpiryDate(cardExpiry.get().asText());
        }
        if (tokenExpiry.isPresent() && !tokenExpiry.get().asText().isBlank()) {
            return formattedWebhookExpiryDate(tokenExpiry.get().asText());
        }
        return "";
    }

    String getExpiryDate(PaymentProviderTransactionResponse.Params params) {
        var cardExpiry = ofNullable(params.getExpiry());
        var tokenExpiry = ofNullable(params.getTokenExpiry());
        if (cardExpiry.isPresent() && !cardExpiry.get().isBlank()) {
            return formattedExpiryDate(cardExpiry.get());
        }
        if (tokenExpiry.isPresent() && !tokenExpiry.get().isBlank()) {
            return formattedExpiryDate(tokenExpiry.get());
        }
        return "";
    }

    private String formattedExpiryDate(String expiryDate) {
        YearMonth yearMonth = YearMonth.parse(expiryDate, DateTimeFormatter.ofPattern("MMyy"));
        return yearMonth.format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    private String formattedWebhookExpiryDate(String expiryDate) {
        YearMonth yearMonth = YearMonth.parse(expiryDate, DateTimeFormatter.ofPattern("yyMM"));
        return yearMonth.format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    private Mono<PaymentResponse> handlePaypalPaymentResponse(PaypalForwardAPITransactionResponse paypalForwardAPITransactionResponse, String paymentId, PaymentsSchema paymentsSchema) {
        PaypalForwardAPITransactionResponseBody paypalForwardAPITransactionResponseBody;
        try {
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            paypalForwardAPITransactionResponseBody = mapper.readValue(paypalForwardAPITransactionResponse.getBody(), PaypalForwardAPITransactionResponseBody.class);
            log.debug(new ObjectAppendingMarker("paypalForwardAPITransactionResponse", paypalForwardAPITransactionResponseBody), "PayPal Forwarding API Transaction Response Body {}.", paypalForwardAPITransactionResponseBody);
            if(!checkPaypalSuccessCodes(paypalForwardAPITransactionResponseBody)) {
                throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("PayPal Forwarding API Transaction Declined/Refused [%s].", paypalForwardAPITransactionResponse.getStatus()), ErrorCodes.PAYPAL_REFUSED);
            }
        } catch (JsonProcessingException ex) {
            throw new PaymentServiceException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Json Processing Exception for PayPal Forwarding API Transaction Response Body [%s].", ex.getMessage()), ErrorCodes.PROVIDER_ERROR);
        }

        var providerResponse = buildProviderResponseForPayPal(paypalForwardAPITransactionResponseBody, paymentsSchema);
        paymentsSchema.setProviderResponse(providerResponse);
        return paymentRepository.updatePaymentResource(paymentId, providerResponse)
                .flatMap(result -> Mono.just(createPaymentResponse(paymentId, result)));
    }

    private ProviderResponse buildProviderResponseForPayPal(PaypalForwardAPITransactionResponseBody response, PaymentsSchema paymentsSchema) {

        var params = response.getResponse().getParams();
        return ProviderResponse.builder()
            .providerReference(params.getProviderReference())
            .transactionReference(params.getTransactionReference())
            .threeCResponse(ThreeCResponse.builder()
                .authCode(isNull(params.getAuthCode()) ? "" : params.getAuthCode())
                .cardSchemeId(isNull(params.getCardType()) ? "" : params.getCardType())
                .cardSchemeName(isNull(params.getCardTypeName()) ? "" : params.getCardTypeName())
                .expiry(isNull(params.getExpiry()) ? "" : formattedExpiryDate(params.getExpiry()))
                .providerStatus(isNull(params.getTransactionState()) ? "" : params.getTransactionState())
                .token(isNull(params.getToken()) ? "" : params.getToken())
                .avsResult(isNull(params.getAvsResult()) ? "" : params.getAvsResult())
                .binRange(isNull(params.getBinRange()) ? "" : params.getBinRange())
                .last4Digits(isNull(params.getLast4Digits()) ? "" : params.getLast4Digits())
                .providerReason(isNull(params.getReason()) ? "" : params.getReason())
                .providerResult(isNull(params.getResult()) ? "" : params.getResult())
                .providerStatusText(isNull(params.getTransactionStateText()) ? "" : params.getTransactionStateText())
                .scaReference(isNull(params.getScaReference()) ? "" : params.getScaReference().trim())
                .threeDSIndicator("")
                .fraudCheckDecision(isNull(params.getCardFraudInfo()) ? "" : params.getCardFraudInfo().getDecision())
                .fraudCheckRequestId(isNull(params.getCardFraudInfo()) ? "" : params.getCardFraudInfo().getRequestId())
                .fraudCheckResult(isNull(params.getCardFraudInfo()) ? "" : params.getCardFraudInfo().getReasonCode())
                .cardholderFirstName(isNull(paymentsSchema.getPayment().getBilling()) ? "" : paymentsSchema.getPayment().getBilling().getFirstName())
                .cardholderLastName(isNull(paymentsSchema.getPayment().getBilling()) ? "" : paymentsSchema.getPayment().getBilling().getLastName())
                .date(LocalDate.now().toString())
                .time(LocalTime.now().toString())
                .build())
            .build();
    }
  
    public Boolean checkPaypalSuccessCodes(PaypalForwardAPITransactionResponseBody paypalForwardAPITransactionResponseBody){
        var result =  paypalForwardAPITransactionResponseBody.getResponse().getParams().getResult().isBlank()?"":paypalForwardAPITransactionResponseBody.getResponse().getParams().getResult();
        var trxState =  paypalForwardAPITransactionResponseBody.getResponse().getParams().getTransactionState().isBlank()?"":paypalForwardAPITransactionResponseBody.getResponse().getParams().getTransactionState();
        var cardFraudInfo =  paypalForwardAPITransactionResponseBody.getResponse().getParams().getCardFraudInfo();
        String cardFraudInfoDecision = "";
        if(Objects.nonNull(cardFraudInfo)){
            cardFraudInfoDecision =  cardFraudInfo.getDecision().isBlank()?"":paypalForwardAPITransactionResponseBody.getResponse().getParams().getCardFraudInfo().getDecision();
        }

        return paypalConfig.getSuccessCodes().getResult().equalsIgnoreCase(result) &&
                paypalConfig.getSuccessCodes().getTrxState().equalsIgnoreCase(trxState) &&
                paypalConfig.getSuccessCodes().getFraudInfoDecision().equalsIgnoreCase(cardFraudInfoDecision);
    }

    public Mono<PaymentResponse> saveCard(SaveCardRequest saveCardRequest) {
        var account = providerAccountFactory.getSaveCardAccount(saveCardRequest);

        return paymentRepository.createPaymentSaveCardResource(saveCardRequest, account)
            .flatMap(paymentsSchema -> {
                var paymentId = paymentsSchema.getPaymentId();
                var cardTemplate = paymentsSchema.getTemplate();
                if (paymentsSchema.getProviderResponse() != null) {
                    log.info(PROVIDER_RESPONSE_EXISTS_MSG);
                    return Mono.just(createPaymentResponse(paymentId, paymentsSchema));
                }
                return threeCPaymentClient.initialiseIpage(saveCardRequest, paymentId, cardTemplate, account)
                    .flatMap(response -> handleSaveCardResponse(response, paymentsSchema, paymentId));
            });
    }

    public Mono<PaymentResponse> authorizeSca(AuthorizeScaRequest authorizeScaRequest) {
        var account = providerAccountFactory.getAuthorizeScaAccount();

        return paymentRepository.createAuthorizeScaResource(authorizeScaRequest, account)
                .flatMap(paymentsSchema -> {
                    var paymentId = paymentsSchema.getPaymentId();
                    if (paymentsSchema.getProviderResponse() != null) {
                        log.info(PROVIDER_RESPONSE_EXISTS_MSG);
                        return Mono.just(createAuthorizeScaPaymentResponse(paymentId, paymentsSchema));
                    }
                    return threeCPaymentClient.initialiseAuthorizeScaIpage(authorizeScaRequest, paymentId, account)
                            .flatMap(response -> handleAuthorizeScaResponse(response, paymentsSchema,
                                    paymentId, authorizeScaRequest.getLanguage()));
                });
    }

  private boolean isThreeCMITPaymentSuccess(PaymentResponse paymentResponse) {
    return StringUtils.equalsIgnoreCase(successResult, paymentResponse.getProviderResponse().getThreeCResponse().getProviderResult())
        && StringUtils.equalsIgnoreCase(successTrxState, paymentResponse.getProviderResponse().getThreeCResponse().getProviderStatus());
  }
}
