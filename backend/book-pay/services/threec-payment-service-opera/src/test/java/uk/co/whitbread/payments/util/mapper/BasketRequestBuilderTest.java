package uk.co.whitbread.payments.util.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.getunleash.Unleash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.whitbread.payments.model.Address;
import uk.co.whitbread.payments.model.Billing;
import uk.co.whitbread.payments.model.Booking;
import uk.co.whitbread.payments.model.Payment;
import uk.co.whitbread.payments.model.ProviderResponse;
import uk.co.whitbread.payments.model.ThreeCResponse;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.feature.FeatureFlag;
import uk.co.whitbread.payments.model.feature.UnleashWrapper;

class BasketRequestBuilderTest {

    private BasketRequestBuilder basketRequestBuilder;
    private FeatureFlag mockedFeatureFlag;
    private UnleashWrapper<FeatureFlag> unleashWrapper;

    @BeforeEach
    void init() {
        var unleash = mock(Unleash.class);
        mockedFeatureFlag = spy(new FeatureFlag());
        unleashWrapper = new UnleashWrapper<>(unleash, mockedFeatureFlag);
        basketRequestBuilder = new BasketRequestBuilder();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void basketRequestBuilderMapsAllFields(boolean mockReturnCodeFf) {

        var paymentSchema = PaymentsSchema.builder()
                .paymentId("12345")
                .sessionId("session")
                .payment(Payment.builder()
                        .billing(Billing.builder()
                                .lastName("tester")
                                .firstName("john")
                                .address(Address.builder()
                                        .countryCode("GB")
                                        .build())
                                .build())
                        .build())

                .booking(Booking.builder()
                        .bookingReference("non uuid")
                        .reference("uuid")
                        .language("EN").build())
                .providerResponse(ProviderResponse.builder()
                        .threeCResponse(ThreeCResponse.builder()
                                .expiry("24/2019")
                                .cardSchemeId("2")
                                .fraudCheckDecision("APPROVED")
                                .token("token")
                                .last4Digits("3456")
                                .build())
                        .build())
                .build();

        var mockedFeatureMockPaymentReturnCode = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMockPaymentReturnCode()).thenReturn(mockedFeatureMockPaymentReturnCode);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMockPaymentReturnCode())).thenReturn(
            mockReturnCodeFf);

        var result = basketRequestBuilder.buildBasketRequest(paymentSchema, "SUCCESS", "1", unleashWrapper);

        assertThat(result).isNotNull();
        assertThat(result.getReference()).isEqualTo("uuid");
        assertThat(result.getBookingReference()).isEqualTo("non uuid");
        assertThat(result.getPaymentId()).isEqualTo("12345");
        assertThat(result.getPaymentStatus()).isEqualTo("SUCCESS");
        assertThat(result.getExpiry()).isEqualTo("24/2019");
        assertThat(result.getChannel()).isNull();
        assertThat(result.getToken()).isEqualTo("token");
        assertThat(result.getCardSchemeId()).isEqualTo("2");
        assertThat(result.getFraudCheckDecision()).isEqualTo("APPROVED");
        assertThat(result.getCountryCode()).isEqualTo("GB");
        assertThat(result.getLanguage()).isEqualTo("EN");
        assertThat(result.getLastName()).isEqualTo("tester");
        assertThat(result.getFirstName()).isEqualTo("john");
    }

    @ParameterizedTest
    @CsvSource({"https://www.qablue.premierinn.digital,qablue", "https://www.dev.premierinn.digital,dev", "https://www.prod.premierinn.digital,prod"})
    void processEnvironmentSuccessTest(String input, String expectedResult) {

        var result = basketRequestBuilder.processEnvironment(input);

        assertThat(result).isNotNull().isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"  ", "\t", "\n", "https://ww.w.qablue.premierinn.digital,qablue"})
    void processEnvironmentFailedTest(String input) {

        var expectedResult = "";
        var result = basketRequestBuilder.processEnvironment(input);

        assertThat(result).isNotNull().isEqualTo(expectedResult);
    }


}
