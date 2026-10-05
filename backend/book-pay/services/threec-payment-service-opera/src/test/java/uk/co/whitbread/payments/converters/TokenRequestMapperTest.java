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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenRequestMapperTest {

    private static final String ANY_USERNAME = "username";
    private static final String ANY_PASSWORD = "password";
    private static final String CARDHOLDER_NAME = "Mr James Bond";

    @Mock
    private EMerchantService eMerchantService;
    @Mock
    private CardholderStateMapper stateMapper;

    private TokenRequestMapper tokenRequestMapper;

    @BeforeEach
    public void setUp() {
        tokenRequestMapper = new TokenRequestMapper(eMerchantService, stateMapper);
    }

    @Test
    void verifyPopulateTokenCreateFormData() {
        EMerchantDetails eMerchantDetails = new EMerchantDetails(ANY_USERNAME, ANY_PASSWORD);
        when(eMerchantService.getTokenisedEMerchantDetails()).thenReturn(eMerchantDetails);
        String requestId = UUID.randomUUID().toString();
        var createTokenRequest = PaymentRequestFixtures.getCreateTokenRequest(requestId);
        var result = tokenRequestMapper.mapCreateTokenRequest(createTokenRequest);
        assertNotNull(result);
        assertEquals(CARDHOLDER_NAME, result.getFirst("CardHolderFirstName"));
    }

}