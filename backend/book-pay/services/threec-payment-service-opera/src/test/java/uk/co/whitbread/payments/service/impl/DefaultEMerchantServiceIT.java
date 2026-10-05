package uk.co.whitbread.payments.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import uk.co.whitbread.payments.Application;
import uk.co.whitbread.payments.service.EMerchantService;

import static org.junit.jupiter.api.Assertions.*;
import static uk.co.whitbread.payments.model.PaymentSubType.ECOMM;
import static uk.co.whitbread.payments.model.PaymentSubType.MOTO;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class DefaultEMerchantServiceIT {

    @Value("${3c.username.default}")
    private String defaultUsername;

    @Value("${3c.username.tokenisation}")
    private String tokensationUsername;

    @Value("${3c.username.tokenisationDE}")
    private String tokensationUsernameDE;

    @Value("${3c.emerchant.validation.code}")
    private String eMerchantValidationCode;

    @Autowired
    private EMerchantService eMerchantService;

    @Test
    void testGetDefaultMerchantDetailsEcomm() {
        var eMerchantDetails = eMerchantService.getEMerchantDetails(ECOMM.name(), "LONHOL");
        assertEquals("eWB-" + defaultUsername, eMerchantDetails.getUsername());
        assertEquals(eMerchantValidationCode, eMerchantDetails.getPassword());
    }

    @Test
    void testGetDefaultMerchantDetailsMoto() {
        var eMerchantDetails = eMerchantService.getEMerchantDetails(MOTO.name(), "LONHOL");
        assertEquals("mWB-" + defaultUsername, eMerchantDetails.getUsername());
        assertEquals(eMerchantValidationCode, eMerchantDetails.getPassword());
    }

    @Test
    void testGetDynamicMerchantDetailsEcomm() {
        var eMerchantDetails = eMerchantService.getEMerchantDetails(ECOMM.name(), "LONALD");
        assertEquals("eWB-LONALD", eMerchantDetails.getUsername());
        assertEquals(eMerchantValidationCode, eMerchantDetails.getPassword());
    }

    @Test
    void testGetDynamicMerchantDetailsMoto() {
        var eMerchantDetails = eMerchantService.getEMerchantDetails(MOTO.name(), "DUBAIR");
        assertEquals("mWB-DUBAIR", eMerchantDetails.getUsername());
        assertEquals(eMerchantValidationCode, eMerchantDetails.getPassword());
    }

    @Test
    void testGetTokenisationMerchantDetails() {
        var eMerchantDetails = eMerchantService.getTokenisedEMerchantDetails();
        assertEquals(tokensationUsername, eMerchantDetails.getUsername());
        assertEquals(eMerchantValidationCode, eMerchantDetails.getPassword());
    }

    @Test
    void expectIllegalArgumentExceptionWhenIncorrectPaymentSubtypeIsPassed() {
        try {
            eMerchantService.getEMerchantDetails("ABC", "LONHOL");
            fail();
        } catch(Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    void testGetTokenisationMerchantDetailsByCountryDE() {
        var eMerchantDetails = eMerchantService.getEMerchantDetailsByCountry("de");
        assertEquals(tokensationUsernameDE, eMerchantDetails.getUsername());
        assertEquals(eMerchantValidationCode, eMerchantDetails.getPassword());
    }

    @Test
    void testGetTokenisationMerchantDetailsByCountry() {
        var eMerchantDetails = eMerchantService.getEMerchantDetailsByCountry("gb");
        assertEquals(tokensationUsername, eMerchantDetails.getUsername());
        assertEquals(eMerchantValidationCode, eMerchantDetails.getPassword());
    }
}