package uk.co.whitbread.payments.service.impl;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.co.whitbread.payments.client.ThreeCPaymentClient;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.Address;
import uk.co.whitbread.payments.model.CardHolder;
import uk.co.whitbread.payments.model.CreateTokenRequest;
import uk.co.whitbread.payments.model.UpdateTokenRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import uk.co.whitbread.payments.properties.CardTypeProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultTokensServiceTest {

    private static final String TOKEN = "4764776852337921111";
    private static final String VISA_PLANET_CARD_TYPE = "VS";
    private static final String VISA_BART_CARD_TYPE = "VI";
    private static final String MASTERCARD_PLANET_CARD_TYPE = "MC";

    private DefaultTokensService defaultTokenService;
    private final ThreeCPaymentClient threeCPaymentClient = mock(ThreeCPaymentClient.class);
    private static final String PAYPAL_CLIENT_TOKEN_GB = "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHoYVc1MGNtVmxaMnJlYWRHQiIsImN1cnJlbmN5SXNvQ29kZSI6IkdCUCJ9fQ==";
    private static final String PAYPAL_CLIENT_TOKEN_DE = "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJleUowZVhBaU9pSktWMVFdytkvmN5SXNvQ29kZSI6IkVVUiJ9fQ==";
    private static final String PAYPAL_CLIENT_ID = "Af5tWcftry239W03bvHOPcJndrOT13w5rAcg-qSF";
    private static final String MERCHANT_ACCOUNT_DE = "de";
    private static final String MERCHANT_ACCOUNT_GB = "gb";
    private static final String MERCHANT_ACCOUNT_GB_ERROR = "gb1";
    private static final String MERCHANT_ACCOUNT_DE_ERROR = "de1";


    @BeforeEach
    void setup() {
        var cardTypeProperties = new CardTypeProperties();
        cardTypeProperties.setCardTypes(Map.of("VS", "VI"));
        defaultTokenService = new DefaultTokensService(threeCPaymentClient, List.of(0, 700), cardTypeProperties);
    }

    @Test
    void testTokenCreate_visaCardType_success() {
        var createTokenRequest = generateCreateTokenRequest();
        var createTokenResponse = generateCreateTokenResponse(0, VISA_PLANET_CARD_TYPE);
        when(threeCPaymentClient.createToken(any())).thenReturn(Mono.just(createTokenResponse));

        StepVerifier.create(defaultTokenService.createToken(createTokenRequest))
            .assertNext(response -> {
                assertEquals(TOKEN, response.getToken());
                assertEquals(VISA_BART_CARD_TYPE, response.getCardType());
            })
            .verifyComplete();
    }

    @Test
    void testTokenCreate_keepPlanetCardType_success() {
        var createTokenRequest = generateCreateTokenRequest();
        var createTokenResponse = generateCreateTokenResponse(0, MASTERCARD_PLANET_CARD_TYPE);
        when(threeCPaymentClient.createToken(any())).thenReturn(Mono.just(createTokenResponse));

        StepVerifier.create(defaultTokenService.createToken(createTokenRequest))
            .assertNext(response -> {
                assertEquals(TOKEN, response.getToken());
                assertEquals(MASTERCARD_PLANET_CARD_TYPE, response.getCardType());
            })
            .verifyComplete();
    }

    @Test
    void testTokenCreate_error() {
        var createTokenRequest = generateCreateTokenRequest();
        var createTokenResponse = generateCreateTokenResponse(1, VISA_PLANET_CARD_TYPE);
        when(threeCPaymentClient.createToken(any())).thenReturn(Mono.just(createTokenResponse));

        StepVerifier.create(defaultTokenService.createToken(createTokenRequest))
                .expectError(PaymentServiceException.class).verify();
    }

    @Test
    void testTokenUpdate_success() {
        var updateTokenRequest = generateUpdateTokenRequest();
        var updateTokenResponse = generateUpdateTokenResponse(0);
        when(threeCPaymentClient.updateToken(any())).thenReturn(Mono.just(updateTokenResponse));

        StepVerifier.create(defaultTokenService.updateToken(updateTokenRequest))
                .assertNext(response -> assertEquals(TOKEN, response.getToken()))
                .verifyComplete();
    }

    @Test
    void testTokenUpdate_error() {
        var updateTokenRequest = generateUpdateTokenRequest();
        var updateTokenResponse = generateUpdateTokenResponse(1);
        when(threeCPaymentClient.updateToken(any())).thenReturn(Mono.just(updateTokenResponse));

        StepVerifier.create(defaultTokenService.updateToken(updateTokenRequest))
                .expectError(PaymentServiceException.class).verify();
    }

    @Test
    void testPaypalClientTokenGenerate_UK_success() {
        var paypalClientTokenResponse = generatePaypalClientTokenResponse(MERCHANT_ACCOUNT_GB);

        when(threeCPaymentClient.createPaypalClientToken(any())).thenReturn(Mono.just(paypalClientTokenResponse));

        StepVerifier.create(defaultTokenService.createPaypalClientToken(MERCHANT_ACCOUNT_GB))
                .assertNext(response -> assertEquals(PAYPAL_CLIENT_TOKEN_GB, response.getClientToken()))
                .verifyComplete();
    }

    @Test
    void testPaypalClientTokenGenerate_UK_error() {
        var paypalClientTokenResponse = generatePaypalClientTokenResponse(MERCHANT_ACCOUNT_GB_ERROR);

        when(threeCPaymentClient.createPaypalClientToken(any())).thenReturn(Mono.just(paypalClientTokenResponse));

        StepVerifier.create(defaultTokenService.createPaypalClientToken(MERCHANT_ACCOUNT_GB_ERROR))
                .expectErrorMatches(throwable -> throwable instanceof PaymentServiceException &&
                        Objects.equals(throwable.getMessage(), "Error encountered during paypal token create for the country code [gb1]."));
    }

    @Test
    void testPaypalClientTokenGenerate_Germany_success() {
        var paypalClientTokenResponse = generatePaypalClientTokenResponse(MERCHANT_ACCOUNT_DE);

        when(threeCPaymentClient.createPaypalClientToken(any())).thenReturn(Mono.just(paypalClientTokenResponse));

        StepVerifier.create(defaultTokenService.createPaypalClientToken(MERCHANT_ACCOUNT_DE))
                .assertNext(response -> assertEquals(PAYPAL_CLIENT_TOKEN_DE, response.getClientToken()))
                .verifyComplete();
    }

    @Test
    void testPaypalClientTokenGenerate_Germany_error() {
        var paypalClientTokenResponse = generatePaypalClientTokenResponse(MERCHANT_ACCOUNT_DE_ERROR);

        when(threeCPaymentClient.createPaypalClientToken(any())).thenReturn(Mono.just(paypalClientTokenResponse));

        StepVerifier.create(defaultTokenService.createPaypalClientToken(MERCHANT_ACCOUNT_DE_ERROR))
                .expectErrorMatches(throwable -> throwable instanceof PaymentServiceException &&
                        Objects.equals(throwable.getMessage(), "Error encountered during paypal token create for the country code [de2]."));
    }

    private CreateTokenRequest generateCreateTokenRequest() {
        return CreateTokenRequest.builder()
                .cardNumber("12345678901234567890")
                .expiryMonth("05")
                .expiryYear("99")
                .cardHolder(CardHolder.builder().build())
                .build();
    }

    private uk.co.whitbread.payments.model.threec.CreateTokenResponse generateCreateTokenResponse(int returnCode, String cardType) {
        var response = new uk.co.whitbread.payments.model.threec.CreateTokenResponse();
        response.setToken(TOKEN);
        response.setCardType(cardType);
        response.setReturnCode(returnCode);
        return response;
    }

    private UpdateTokenRequest generateUpdateTokenRequest() {
        return UpdateTokenRequest.builder()
                .token(TOKEN)
                .cardHolderFirstName("John")
                .cardHolderLastName("Doe")
                .cardHolderAddress(Address.builder()
                        .line1("Line 1")
                        .countryCode("UK")
                        .postalCode("123456")
                        .build())
                .build();
    }

    private uk.co.whitbread.payments.model.threec.UpdateTokenResponse generateUpdateTokenResponse(int returnCode) {
        var response = new uk.co.whitbread.payments.model.threec.UpdateTokenResponse();
        response.setToken(TOKEN);
        response.setReturnCode(returnCode);
        return response;
    }

    private uk.co.whitbread.payments.model.PaypalClientTokenResponse generatePaypalClientTokenResponse(String countryCode) {
        var response = new uk.co.whitbread.payments.model.PaypalClientTokenResponse();
        if(countryCode.equalsIgnoreCase(MERCHANT_ACCOUNT_GB)){
            response.setClientToken(PAYPAL_CLIENT_TOKEN_GB);
        }else if(countryCode.equalsIgnoreCase(MERCHANT_ACCOUNT_DE)){
            response.setClientToken(PAYPAL_CLIENT_TOKEN_DE);
        }else {
            response.setClientToken("");
            return response;
        }
        response.setClientId(PAYPAL_CLIENT_ID);
        response.setGeneratedAt(LocalDateTime.now());
        return response;
    }
}
