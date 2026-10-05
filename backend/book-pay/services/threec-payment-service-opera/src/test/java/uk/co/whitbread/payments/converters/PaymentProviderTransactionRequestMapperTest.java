package uk.co.whitbread.payments.converters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.model.EMerchantDetails;
import uk.co.whitbread.payments.service.EMerchantService;
import uk.co.whitbread.payments.util.PaymentRequestFixtures;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.payments.model.BookingType.PAY_NOW;

@ExtendWith(MockitoExtension.class)
class PaymentProviderTransactionRequestMapperTest {

    private static final String ANY_USERNAME = "username";
    private static final String ANY_PASSWORD = "password";
    private static final String REQUEST_TYPE = "getstatusbymerchantref";

    @Mock
    private EMerchantService eMerchantService;
    private PaymentProviderTransactionRequestMapper paymentProviderTransactionRequestMapper;

    @BeforeEach
    public void setUp() {
        paymentProviderTransactionRequestMapper = new PaymentProviderTransactionRequestMapper(eMerchantService);
    }

    @Test
    void verifyPaymentProviderTransactionRequest() {
        EMerchantDetails eMerchantDetails = new EMerchantDetails(ANY_USERNAME, ANY_PASSWORD);
        when(eMerchantService.getEMerchantDetails(any(), any())).thenReturn(eMerchantDetails);
        String requestId = UUID.randomUUID().toString();
        var paymentsSchema = PaymentRequestFixtures.getPaymentSchema(requestId, PAY_NOW.name());
        var result = paymentProviderTransactionRequestMapper.mapGetPaymentStatusByMerchantRefRequest(paymentsSchema);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRequest());
        Assertions.assertNotNull(result.getRequest().getParams());
        Assertions.assertEquals(REQUEST_TYPE, result.getRequest().getType());
    }
}