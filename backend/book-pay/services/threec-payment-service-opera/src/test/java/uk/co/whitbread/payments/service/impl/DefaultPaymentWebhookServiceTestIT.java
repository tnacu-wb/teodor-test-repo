package uk.co.whitbread.payments.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.co.whitbread.payments.integration.IntegrationBaseIT;
import uk.co.whitbread.payments.model.Booking;
import uk.co.whitbread.payments.model.BusinessSite;
import uk.co.whitbread.payments.model.Card;
import uk.co.whitbread.payments.model.Payment;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.PaymentResponse;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.properties.AccountConfigProperties;
import uk.co.whitbread.payments.properties.ProviderAccount;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.payments.util.PaymentRequestFixtures.getServerRequest;
import static uk.co.whitbread.payments.util.PaymentRequestFixtures.getWebhookData;

@DirtiesContext
@ExtendWith(MockitoExtension.class)
public class DefaultPaymentWebhookServiceTestIT extends IntegrationBaseIT {
    @Mock
    private DefaultPaymentWebhookService defaultPaymentWebhookService;

    @Test
    public void operaDataReturnsBookingReferenceSuccess() throws IOException {

        String requestId = "12345";
        var paymentId = UUID.randomUUID().toString();
        var paymentRequest = PaymentRequest.builder()
                .requestId(requestId)
                .payment(Payment.builder()
                        .subType("ECOMM")
                        .build())
                .booking(Booking.builder()
                        .businessSite(BusinessSite.builder()
                                .name("London Hotel")
                                .identifier("LONALD")
                                .build())
                        .reference("ref-12345")
                        .bookingReference("book-ref-12345")
                        .build())
                .build();

        //Create a payment resource
        Optional<PaymentsSchema> paymentSchema = Optional.of(PaymentsSchema.builder().requestId(requestId)
                .payment(Payment.builder()
                        .card(Card.builder().token("1234565").build())
                        .settlementReference("123456789012345678").build()).build());

        when(paymentRepositoryMock.getByRequestId(requestId)).thenReturn(Mono.just(paymentSchema));
        StepVerifier.create(paymentRepositoryMock.createPaymentResource(paymentRequest, getProviderAccount()))
                .expectNextMatches(paymentsSchema -> paymentsSchema.getRequestId().equals(requestId))
                .expectComplete()
                .verify();

        var serverRequest = getServerRequest(getWebhookData(), paymentId);
        assertThat(serverRequest).isNotNull();
        var handleWebhookResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/operaDataReturnsBookingReferenceSuccess.json"), PaymentResponse.class);
        when(defaultPaymentWebhookService.handleWebhook(serverRequest)).thenReturn(Mono.just(handleWebhookResponse));
        var result = defaultPaymentWebhookService.handleWebhook(serverRequest).block();
        assertThat(result).isNotNull();
        assertThat(result.getBooking().getReference()).isEqualTo("GAA-a8f7be49-7dc4-4d31-9e23-3bb74717ce41");
        assertThat(result.getBooking().getBookingReference()).isEqualTo("GAA9657235");
    }

    private ProviderAccount getProviderAccount() {
        AccountConfigProperties accountConfigProperties = new AccountConfigProperties();
        accountConfigProperties.setNewCardTemplate("");
        return ProviderAccount.builder().configuration(accountConfigProperties).build();
    }

}
