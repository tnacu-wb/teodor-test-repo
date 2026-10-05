package uk.co.whitbread.payments.repository;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.co.whitbread.payments.integration.IntegrationBaseIT;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.properties.AccountConfigProperties;
import uk.co.whitbread.payments.properties.ProviderAccount;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@DirtiesContext
class PaymentRepositoryIT extends IntegrationBaseIT {
    private static final String SESSION_ID = "ANY_SESSION_ID";

    @Test
    void expectEmptyOptionalWhenRequestIdIsNotFound() {
        String requestId = UUID.randomUUID().toString();

        //Create a payment resource
        Optional<PaymentsSchema> paymentSchema = Optional.empty();

        when(paymentRepositoryMock.getByRequestId(requestId)).thenReturn(Mono.just(paymentSchema));

        StepVerifier.create(paymentRepositoryMock.getByRequestId(requestId))
                .expectNext(Optional.empty())
                .expectComplete()
                .verify();
    }

    @Test
    void expectPaymentsSchemaWhenRequestIdIsFound() {
        String requestId = UUID.randomUUID().toString();

        //Create a payment resource
        Optional<PaymentsSchema> paymentSchema = Optional.of(PaymentsSchema.builder().requestId(requestId)
                .payment(Payment.builder()
                .card(Card.builder().token("1234565").build())
                .settlementReference("123456789012345678").build()).build());

        when(paymentRepositoryMock.getByRequestId(requestId)).thenReturn(Mono.just(paymentSchema));

        StepVerifier.create(paymentRepositoryMock.createPaymentResource(getPaymentRequest(requestId), getProviderAccount()))
                .expectNextMatches(paymentsSchema -> paymentsSchema.getRequestId().equals(requestId)
                        && paymentsSchema.getPayment().getCard().getToken().equals("1234565")
                        && paymentsSchema.getPayment().getSettlementReference().equals("123456789012345678"))
                .expectComplete()
                .verify();

        //Fetch payment resource by request-id
        StepVerifier.create(paymentRepositoryMock.getByRequestId(requestId))
                .expectNextMatches(paymentsSchema -> paymentsSchema.get().getRequestId().equals(requestId)
                        && paymentsSchema.get().getPayment().getCard().getToken().equals("1234565")
                        && paymentsSchema.get().getPayment().getSettlementReference().equals("123456789012345678"))
                .expectComplete()
                .verify();
    }

    @Test
    void expectPaymentsSchemaWhenCreatePaymentResourceIsSuccessful() {
        String requestId = UUID.randomUUID().toString();

        //Create a payment resource
        Optional<PaymentsSchema> paymentSchema = Optional.of(PaymentsSchema.builder().requestId(requestId)
                .payment(Payment.builder()
                        .card(Card.builder().token("1234567").build())
                        .settlementReference("123456789012349865655").build()).build());

        when(paymentRepositoryMock.getByRequestId(requestId)).thenReturn(Mono.just(paymentSchema));

        StepVerifier.create(paymentRepositoryMock.createPaymentResource(getPaymentRequest(requestId), getProviderAccount()))
                .expectNextMatches(paymentsSchema -> paymentsSchema.getRequestId().equals(requestId)
                        && paymentsSchema.getPayment().getCard().getToken().equals("1234567")
                        && paymentsSchema.getPayment().getSettlementReference().equals("123456789012349865655"))
                .expectComplete()
                .verify();
    }

    @Test
    void expectPaymentsSaveCardSchemaWhenCreatePaymentResourceIsSuccessful() {
        String requestId = UUID.randomUUID().toString();

        //Create a payment resource
        Optional<PaymentsSchema> paymentSchema = Optional.of(PaymentsSchema.builder()
            .requestId(requestId)
            .payment(Payment.builder()
                .card(Card.builder().token("1234567").build())
                .settlementReference("123456789012349865655").build()).build());

        when(paymentRepositoryMock.getByRequestId(requestId)).thenReturn(Mono.just(paymentSchema));

        StepVerifier.create(paymentRepositoryMock.createPaymentSaveCardResource(SaveCardRequest.builder().requestId(requestId).build(), getProviderAccount()))
            .expectNextMatches(paymentsSchema -> paymentsSchema.getRequestId().equals(requestId)
                && paymentsSchema.getPayment().getCard().getToken().equals("1234567")
                && paymentsSchema.getPayment().getSettlementReference().equals("123456789012349865655"))
            .expectComplete()
            .verify();
    }

    @Test
    void expectPaymentsSchemaWhenCreateRefundResourceIsSuccessful() {
        String requestId = UUID.randomUUID().toString();

        //Create a payment resource
        Optional<PaymentsSchema> paymentSchema = Optional.of(PaymentsSchema.builder().requestId(requestId)
                .refund(Refund.builder()
                        .card(Card.builder().token("123456789").build())
                        .amount(Amount.builder().minorUnits(100).build()).build()).build());

        when(paymentRepositoryMock.getByRequestId(requestId)).thenReturn(Mono.just(paymentSchema));

        StepVerifier.create(paymentRepositoryMock.createRefundResource(getRefundRequest(requestId)))
                .expectNextMatches(paymentsSchema -> paymentsSchema.getRequestId().equals(requestId)
                        && paymentsSchema.getRefund().getCard().getToken().equals("123456789")
                        && paymentsSchema.getRefund().getAmount().getMinorUnits() == 100)
                .expectComplete()
                .verify();
    }

    @Test
    void expectPaymentsAuthorizeScaSchemaOnSuccess() {
        String requestId = UUID.randomUUID().toString();

        Optional<PaymentsSchema> paymentSchema = Optional.of(PaymentsSchema.builder()
                .requestId(requestId)
                .paymentId("123456789D")
                .booking(Booking.builder().language("en").build())
                .payment(Payment.builder()
                        .subType(PaymentSubType.AUTHORIZE_CARD.name()).build()).build());

        when(paymentRepositoryMock.getByRequestId(requestId)).thenReturn(Mono.just(paymentSchema));

        StepVerifier.create(
                paymentRepositoryMock.createAuthorizeScaResource(AuthorizeScaRequest.builder()
                    .requestId(requestId).bookingReference("GAI7841336").country("gb").language("en")
                    .environment("LOCAL").build(), getProviderAccount()))
            .expectNextMatches(paymentsSchema -> paymentsSchema.getRequestId().equals(requestId)
                && paymentsSchema.getPayment().getSubType()
                .equals(PaymentSubType.AUTHORIZE_CARD.name())
                && paymentsSchema.getBooking().getLanguage().equals("en"))
            .expectComplete()
            .verify();
    }

    private ProviderAccount getProviderAccount() {
        var configProperties = new AccountConfigProperties();
        configProperties.setServiceAction("3dspay");
        configProperties.setNewCardTemplate("wb_newcard_pn_v15.xml");
        configProperties.setSavedCardTemplate("wb_savedcard_pn_v15.xml");
        configProperties.setNewCardTrxOption("G");
        configProperties.setSavedCardTrxOption("P");
        configProperties.setFraudScreened(true);
        configProperties.setFraudProfile("Web profile");
        return ProviderAccount.builder()
                .bookingType(BookingType.PAY_NOW)
                .channelTypes(List.of(ChannelType.PI, ChannelType.BB, ChannelType.APPS_IOS, ChannelType.APPS_ANDROID))
                .configuration(configProperties)
                .currency(Currency.GBP)
                .paymentSubTypes(List.of(PaymentSubType.ECOMM))
                .paymentType(PaymentType.CARD)
                .build();
    }

    private PaymentRequest getPaymentRequest(String requestId) {
        return PaymentRequest.builder()
                .booking(getBooking())
                .payment(getPayment())
                .requestId(requestId)
                .sessionId(SESSION_ID)
                .build();
    }

    private RefundRequest getRefundRequest(String requestId) {
        return RefundRequest.builder()
                .booking(getBooking())
                .refund(getRefund())
                .requestId(requestId)
                .build();
    }

    private Booking getBooking() {
        return Booking.builder()
                .arrivalDate(LocalDate.parse("2022-10-01"))
                .businessSite(BusinessSite.builder()
                        .additionalServices(List.of("wifi"))
                        .identifier("DUNGOU")
                        .location("Dundee")
                        .name("Dundee West")
                        .type(BusinessType.HOTEL.name())
                        .build())
                .channel(ChannelType.PI.name())
                .departureDate(LocalDate.parse("2022-10-02"))
                .journey(JourneyType.BOOKING.name())
                .language(Language.en.name())
                .leadGuest(Guest.builder()
                        .name("Samuel Whitbread")
                        .registered(true)
                        .registeredSince(LocalDate.parse("2020-11-26"))
                        .previousBookings(10)
                        .build())
                .rooms(List.of(Room.builder()
                        .adults(2)
                        .rate("FLEX")
                        .type("DB")
                        .build()))
                .type("PAY_NOW")
                .build();
    }

    private Payment getPayment() {
        return Payment.builder()
                .amount(Amount.builder()
                        .currency(Currency.GBP.name())
                        .minorUnits(1)
                        .build())
                .billing(Billing.builder()
                        .address(Address.builder()
                                .countryCode("GB")
                                .line1("120 Holborn")
                                .postalCode("EC1N 2TD")
                                .build())
                        .email("test@whitbread.com")
                        .firstName("Samuel")
                        .lastName("Whitbread")
                        .title("Mr")
                        .build())
                .card(Card.builder()
                        .token("123456")
                        .build())
                .environment("https://www.premierinn.com")
                .settlementReference("123456789101112111")
                .subType(PaymentSubType.ECOMM.name())
                .type(PaymentType.CARD.name())
                .build();
    }

    private Refund getRefund() {
        return Refund.builder()
                .amount(Amount.builder()
                        .currency(Currency.GBP.name())
                        .minorUnits(1000)
                        .build())
                .card(Card.builder()
                        .cvv("222")
                        .expiryMonth("12")
                        .expiryYear("21")
                        .token("4943056398164344242")
                        .build())
                .type(PaymentType.CARD.name())
                .build();
    }

}
