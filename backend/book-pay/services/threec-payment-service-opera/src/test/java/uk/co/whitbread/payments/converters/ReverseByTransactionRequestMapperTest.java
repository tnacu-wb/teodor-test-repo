package uk.co.whitbread.payments.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.model.EMerchantDetails;
import uk.co.whitbread.payments.service.EMerchantService;
import uk.co.whitbread.payments.util.PaymentRequestFixtures;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.payments.model.BookingType.PAY_NOW;

@ExtendWith(MockitoExtension.class)
class ReverseByTransactionRequestMapperTest {

    private static final String ANY_USERNAME = "username";
    private static final String ANY_PASSWORD = "password";

    @Mock
    private EMerchantService eMerchantService;
    private ReverseByTransactionRequestMapper reverseByTransactionRequestMapper;

    @BeforeEach
    public void setUp() {
        reverseByTransactionRequestMapper = new ReverseByTransactionRequestMapper(eMerchantService);
    }

    @Test
    void verifySuccessfulMappingForRefund() {
        EMerchantDetails eMerchantDetails = new EMerchantDetails(ANY_USERNAME, ANY_PASSWORD);
        when(eMerchantService.getEMerchantDetails(any(), any())).thenReturn(eMerchantDetails);
        String requestId = UUID.randomUUID().toString();
        var paymentsSchema = PaymentRequestFixtures.getPaymentSchema(requestId, PAY_NOW.name());
        var result = reverseByTransactionRequestMapper.mapReverseByTransactionRequest(paymentsSchema);
        assertNotNull(result);
        assertEquals(ANY_USERNAME, result.getRequest().getCredentials().getValidationId());
        assertEquals(ANY_PASSWORD, result.getRequest().getCredentials().getValidationCode());
    }
}