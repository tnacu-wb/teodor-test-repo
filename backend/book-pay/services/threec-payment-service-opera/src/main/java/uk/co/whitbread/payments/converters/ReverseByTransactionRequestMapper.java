package uk.co.whitbread.payments.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.payments.model.EMerchantDetails;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.threec.ReverseByTransactionIdRequest;
import uk.co.whitbread.payments.properties.AccountConfigProperties;
import uk.co.whitbread.payments.service.EMerchantService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReverseByTransactionRequestMapper {

    private final EMerchantService eMerchantService;

    public ReverseByTransactionIdRequest mapReverseByTransactionRequest(PaymentsSchema paymentsSchema) {
        ReverseByTransactionIdRequest reverseByTransactionIdRequest = new ReverseByTransactionIdRequest();
        // get e-merchant details
        EMerchantDetails eMerchantDetails = eMerchantService.getEMerchantDetails(paymentsSchema.getPaymentSubType(), paymentsSchema.getHotelCode());
        var request = ReverseByTransactionIdRequest.Request.builder()
                .version(AccountConfigProperties.DEFAULT_VERSION)
                .credentials(ReverseByTransactionIdRequest.Credentials.builder()
                        .validationId(eMerchantDetails.getUsername())
                        .validationCode(eMerchantDetails.getPassword())
                        .build())
                .build();
        reverseByTransactionIdRequest.setRequest(request);
        return reverseByTransactionIdRequest;
    }
}
