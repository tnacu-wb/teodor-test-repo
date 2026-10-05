package uk.co.whitbread.payments.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.threec.*;
import uk.co.whitbread.payments.properties.ProviderAccount;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreeCTransformer {

    private final InitialiseRequestMapper initialiseRequestMapper;
    private final NoCardReadTransactionRequestMapper noCardReadTransactionRequestMapper;
    private final TokenRequestMapper tokenRequestMapper;
    private final PaymentProviderTransactionRequestMapper paymentProviderTransactionRequestMapper;
    private final ReverseByTransactionRequestMapper reverseByTransactionRequestMapper;
    private final PaypalTransactionRequestMapper paypalTransactionRequestMapper;

    public InitialiseRequest populateInitialiseRequest(PaymentRequest paymentRequest, String paymentId, String cardTemplate, ProviderAccount account) {
        return initialiseRequestMapper.mapInitialiseRequest(paymentRequest, account, paymentId, cardTemplate);
    }

    public InitialiseRequest populateInitialiseRequest(SaveCardRequest saveCardRequest, String paymentId, String cardTemplate, ProviderAccount account) {
        return initialiseRequestMapper.mapInitialiseRequest(saveCardRequest, account, paymentId, cardTemplate);
    }

    public InitialiseRequest populateInitialiseAuthorizeScaRequest(String paymentId, ProviderAccount account, String language, String country) {
        return initialiseRequestMapper.mapInitialiseAuthorizeScaRequest(account, paymentId, language, country);
    }

    public NoCardReadTransactionRequest populateNoCardReadRequest(PaymentRequest paymentRequest, String paymentId, ProviderAccount account) {
        return noCardReadTransactionRequestMapper.populateNoCardReadRequest(paymentRequest, account, paymentId);
    }

    public NoCardReadTransactionRequest populateNoCardReadRequest(RefundRequest refundRequest, ProviderAccount account, String requestId) {
        return noCardReadTransactionRequestMapper.populateNoCardReadRequest(refundRequest, account, requestId);
    }

    public MultiValueMap<String, String> populateCreateTokenFormData(CreateTokenRequest createTokenRequest) {
        return tokenRequestMapper.mapCreateTokenRequest(createTokenRequest);
    }

    public MultiValueMap<String, String> populateUpdateTokenFormData(UpdateTokenRequest createTokenRequest) {
        return tokenRequestMapper.mapUpdateTokenRequest(createTokenRequest);
    }

    public PaymentProviderTransactionRequest populatePaymentProviderTransactionRequest(PaymentsSchema paymentsSchema) {
        return paymentProviderTransactionRequestMapper.mapGetPaymentStatusByMerchantRefRequest(paymentsSchema);
    }

    public ReverseByTransactionIdRequest populateReverseByTransactionIdRequest(PaymentsSchema paymentsSchema) {
        return reverseByTransactionRequestMapper.mapReverseByTransactionRequest(paymentsSchema);
    }

    public PaypalForwardAPITransactionRequest populatePaypalRequest(PaymentRequest paymentRequest, String paymentId, ProviderAccount account) {
        return paypalTransactionRequestMapper.populatePaypalRequest(paymentRequest, account, paymentId);
    }

    public NoCardReadTransactionRequest populatePaypalMITRequest(PaypalForwardAPITransactionResponseBody paypalForwardAPITransactionResponseBody, PaymentRequest paymentRequest, String paymentId, ProviderAccount account) {
        return noCardReadTransactionRequestMapper.populatePaypalMitRequest(paypalForwardAPITransactionResponseBody, paymentRequest, account, paymentId);
    }

   public NoCardReadTransactionRequest populateMitCcRequest(PaymentRequest paymentRequest,
                                                           String paymentId,
                                                           ProviderAccount account) {
    return noCardReadTransactionRequestMapper.populateMitCcRequest(paymentRequest, account, paymentId);
  }
}