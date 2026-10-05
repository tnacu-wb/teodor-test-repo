package uk.co.whitbread.payments.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.payments.model.EMerchantDetails;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.threec.PaymentProviderTransactionRequest;
import uk.co.whitbread.payments.properties.AccountConfigProperties;
import uk.co.whitbread.payments.service.EMerchantService;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentProviderTransactionRequestMapper {

    private static final String REQUEST_TYPE = "getstatusbymerchantref";
    private final EMerchantService eMerchantService;

    public PaymentProviderTransactionRequest mapGetPaymentStatusByMerchantRefRequest(PaymentsSchema paymentsSchema) {
        PaymentProviderTransactionRequest.Request request = new PaymentProviderTransactionRequest.Request();
        PaymentProviderTransactionRequest.Params params = new PaymentProviderTransactionRequest.Params();
        request.setType(REQUEST_TYPE);
        request.setVersion(AccountConfigProperties.DEFAULT_VERSION);
        // get e-merchant details
        EMerchantDetails eMerchantDetails = eMerchantService.getEMerchantDetails(paymentsSchema.getPaymentSubType(), paymentsSchema.getHotelCode());
        request.setCredentials(PaymentProviderTransactionRequest.Credentials.builder()
                .validationId(eMerchantDetails.getUsername())
                .validationCode(eMerchantDetails.getPassword()).build());
        params.setTransactionReference(paymentsSchema.getPaymentId());
        request.setParams(params);

        PaymentProviderTransactionRequest paymentProviderTransactionRequest = new PaymentProviderTransactionRequest();
        paymentProviderTransactionRequest.setRequest(request);
        return paymentProviderTransactionRequest;
    }
}
