package uk.co.whitbread.payments.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import uk.co.whitbread.payments.Application;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.PaymentSubType;
import uk.co.whitbread.payments.model.PaymentType;
import uk.co.whitbread.payments.model.SaveCardRequest;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static uk.co.whitbread.payments.util.TestUtils.getObjectMapper;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class DefaultProviderAccountFactoryIT {

    public static final String WB_SCA_TEMPLATE = "wb_newcard_sca_v5.xml";
    @Autowired
    private DefaultProviderAccountFactory defaultProviderAccountFactory;

    @ParameterizedTest
    @MethodSource("createPaymentRequests")
    void testShouldFindMatchingProviderAccount(PaymentRequest paymentRequest, String ipageTemplate, boolean savedPaymentMethod) {
        var result = defaultProviderAccountFactory.getAccount(paymentRequest);
        assertNotNull(result);
        assertEquals(ipageTemplate, savedPaymentMethod ? result.getConfiguration().getSavedCardTemplate() : result.getConfiguration().getNewCardTemplate());
    }

    @ParameterizedTest
    @MethodSource("createSaveCardRequests")
    void testShouldFindMatchingProviderAccount(SaveCardRequest paymentRequest, String ipageTemplate) {
        var result = defaultProviderAccountFactory.getSaveCardAccount(paymentRequest);
        assertNotNull(result);
        assertEquals(ipageTemplate, result.getConfiguration().getNewCardTemplate());
    }

    @Test
    void testShouldThrowPaymentServiceExceptionNoMatchingAccount() {
        try {
            defaultProviderAccountFactory.getAccount(getObjectMapper().readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestInvalidPaymentType.json"), PaymentRequest.class));
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof PaymentServiceException);
            var exception = (PaymentServiceException) e;
            assertEquals(ErrorCodes.PROVIDER_ACCOUNT_NOT_FOUND, exception.getErrorCode());
        }
    }

    //Could manage JSON files in resource structure
    static Stream<Arguments> createSaveCardRequests() throws IOException {
        return Stream.of(
            Arguments.of(getObjectMapper().readValue(new File("src/test/resources/stubs/3c/requests/saveCardRequest" +
                "-piba.json"), SaveCardRequest.class), "wb_addcard_pci_piba_v6.xml"),
            Arguments.of(getObjectMapper().readValue(new File("src/test/resources/stubs/3c/requests/saveCardRequest.json"), SaveCardRequest.class), "wb_addcard_pci_v6.xml")
        );
    }

    //Could manage JSON files in resource structure
    static Stream<Arguments> createPaymentRequests() throws IOException {
        return Stream.of(
                Arguments.of(getObjectMapper().readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequest.json"), PaymentRequest.class), "wb_newcard_pn_v15.xml", false),
                Arguments.of(getObjectMapper().readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestWithSavedCard.json"), PaymentRequest.class), "wb_savedcard_pn_v15.xml", true),
                Arguments.of(getObjectMapper().readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestPIBA.json"), PaymentRequest.class), "wb_newcard_poa_piba_v15.xml", false),
                Arguments.of(getObjectMapper().readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestWithSavedPIBACard.json"), PaymentRequest.class), "wb_savedcard_poa_piba_v15.xml", true)
        );
    }

    @Test
    void testShouldFindMatchingAuthorizeScaAccount() {
        var result = defaultProviderAccountFactory.getAuthorizeScaAccount();
        assertNotNull(result);
        assertEquals(WB_SCA_TEMPLATE, result.getConfiguration().getNewCardTemplate());
        assertTrue(result.getPaymentSubTypes().contains(PaymentSubType.AUTHORIZE_CARD));
        assertTrue(result.getPaymentType().equals(PaymentType.CARD));
    }
}