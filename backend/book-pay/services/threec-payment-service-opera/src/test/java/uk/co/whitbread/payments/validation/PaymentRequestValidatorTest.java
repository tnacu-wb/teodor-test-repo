package uk.co.whitbread.payments.validation;

import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import uk.co.whitbread.payments.model.*;

import jakarta.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static uk.co.whitbread.payments.model.Currency.GBP;
import static uk.co.whitbread.payments.model.Language.en;

class PaymentRequestValidatorTest {

    private static final LocalDate ARRIVAL_DATE = LocalDate.of(2022, 1, 9);
    private static final LocalDate DEPARTURE_DATE = LocalDate.of(2022, 1, 20);
    private static final String BOOKING_REFERENCE = "ANY_BOOKING_REFERENCE";
    private static final String TITLE = "Mr.";
    private static final String CARDHOLDER_FIRST_NAME = "James";
    private static final String CARDHOLDER_LAST_NAME = "Bond";
    private static final String CARDHOLDER_EMAIL = "Jamed.Bond@whitbread.com";
    private static final String CARDHOLDER_TELEPHONE = "07891142506";
    private static final String ADDRESS_LINE1 = "ADDRESS LINE 1";
    private static final String ADDRESS_LINE2 = "ADDRESS LINE 2";
    private static final String ADDRESS_LINE3 = "ADDRESS LINE 3";
    private static final String ADDRESS_LINE4 = "ADDRESS LINE 4";
    private static final String COUNTRY_CODE = "GB";
    private static final String POSTCODE = "EC1N 2TD";
    private static final String EMPTY = "";
    private static final String PAYMENT_SUBTYPE_ECOMM = "ECOMM";
    private static final String PAYMENT_SUBTYPE_MOTO = "MOTO";

    LocalValidatorFactoryBean localValidatorFactory;
    Mit mit;

    @BeforeEach
    public void setup() {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
        mit = new Mit("43686363637", MitType.L);
    }

    @Test
    void verifyEcommNoLanguage() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("PI", null, "BOOKING", "PAY_NOW", BOOKING_REFERENCE, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, null, null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var payment = getPayment(PAYMENT_SUBTYPE_ECOMM,amount, null, null);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();
        var result = localValidatorFactory.validate(paymentRequest);
        List<String> messages = getErrorMessages(result);
        assertThat("", result, hasSize(1));
        assertThat("", messages, contains("Please specify a language."));
    }

    @Test
    void verifyEcommLanguage() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("PI", null, "BOOKING", "PAY_NOW", BOOKING_REFERENCE, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, en.name(), null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var payment = getPayment(PAYMENT_SUBTYPE_ECOMM,amount, null, null);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();
        var result = localValidatorFactory.validate(paymentRequest);
        assertThat("", result, hasSize(0));
    }

    @Test
    void verifyEcommNoToken() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("PI", null, "BOOKING", "PAY_NOW", BOOKING_REFERENCE, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, en.name(), null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var card = new Card();
        var payment = getPayment(PAYMENT_SUBTYPE_ECOMM, amount, null, card);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();
        var result = localValidatorFactory.validate(paymentRequest);
        List<String> messages = getErrorMessages(result);
        assertThat("", result, hasSize(1));
        assertThat("", messages, contains("Please provide a card token."));
    }

    @Test
    void verifyEcommToken() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("PI", null, "BOOKING", "PAY_NOW", BOOKING_REFERENCE, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, en.name(), null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var card = new Card();
        card.setToken("4216333880397891103");
        var payment = getPayment(PAYMENT_SUBTYPE_ECOMM, amount, null, card);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();
        var result = localValidatorFactory.validate(paymentRequest);
        List<String> messages = getErrorMessages(result);
        assertThat("", result, hasSize(0));
    }

    @Test
    void verifyMotoLanguageNotMandatory() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("CCC", null, "BOOKING", "PAY_NOW", BOOKING_REFERENCE, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, null, null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var card = new Card();
        card.setToken("4216333880397891103");
        card.setExpiryMonth("04");
        card.setExpiryYear("25");
        var payment = getPayment(PAYMENT_SUBTYPE_MOTO, amount, null, card);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();
        var result = localValidatorFactory.validate(paymentRequest);
        assertThat("", result, hasSize(0));
    }

    @Test
    void verifyMotoCardIdentifier() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("CCC", null, "BOOKING", "PAY_NOW", BOOKING_REFERENCE, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, null, null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var card = new Card();
        var payment = getPayment(PAYMENT_SUBTYPE_MOTO, amount, null, card);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();
        var result = localValidatorFactory.validate(paymentRequest);
        List<String> messages = getErrorMessages(result);
        assertThat("", result, hasSize(1));
        assertThat("", messages, contains("Please provide a card token."));
    }

    @Test
    void verifyMotoAtLeastOneCardIsProvided() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("CCC", null, "BOOKING", "PAY_NOW", BOOKING_REFERENCE, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, null, null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var payment = getPayment(PAYMENT_SUBTYPE_MOTO, amount, null, null);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();
        var result = localValidatorFactory.validate(paymentRequest);
        List<String> messages = getErrorMessages(result);
        assertThat("", result, hasSize(1));
        assertThat("", messages, contains("At least one card must be provided."));
    }

    @Test
    void verifyMotoNoExpiryDate() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("CCC", null, "BOOKING", "PAY_NOW", BOOKING_REFERENCE, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, null, null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var card = new Card();
        card.setToken("4216333880397891103");
        var payment = getPayment(PAYMENT_SUBTYPE_MOTO, amount, null, card);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();
        var result = localValidatorFactory.validate(paymentRequest);
        List<String> messages = getErrorMessages(result);
        assertThat("", result, hasSize(1));
        assertThat("", messages, contains("Please provide the expiry date month and year for the card/token."));
    }

    @Test
    void verifyEcommBookingRefNotMandatory() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("PI", null, "BOOKING", "PAY_NOW", null, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, en.name(), null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var payment = getPayment(PAYMENT_SUBTYPE_ECOMM, amount, null, null);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();
        var result = localValidatorFactory.validate(paymentRequest);
        assertThat("", result, hasSize(0));
    }

    @Test
    void verifyAddressParamsMandatory() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("PI", null, "BOOKING", "PAY_NOW", null, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, en.name(), null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var address = new Address(null, ADDRESS_LINE2, ADDRESS_LINE3, ADDRESS_LINE4, null, null, "", null, null);
        var billing = new Billing(TITLE, CARDHOLDER_FIRST_NAME, CARDHOLDER_LAST_NAME, CARDHOLDER_EMAIL, CARDHOLDER_TELEPHONE, address);
        var payment = getPayment(PAYMENT_SUBTYPE_ECOMM, amount,billing, null);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();
        var result = localValidatorFactory.validate(paymentRequest);
        assertThat("", result, hasSize(2));
    }

    @Test
    void verifyCountryCodeMustBeValidISOCode() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("PI", null, "BOOKING", "PAY_NOW", null, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, en.name(), null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var address = new Address(ADDRESS_LINE1, ADDRESS_LINE2, ADDRESS_LINE3, ADDRESS_LINE4, null, "XX", POSTCODE, null, null);
        var billing = new Billing(TITLE, CARDHOLDER_FIRST_NAME, CARDHOLDER_LAST_NAME, CARDHOLDER_EMAIL, CARDHOLDER_TELEPHONE, address);
        var payment = getPayment(PAYMENT_SUBTYPE_ECOMM, amount, billing, null);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();
        var result = localValidatorFactory.validate(paymentRequest);
        List<String> messages = getErrorMessages(result);
        assertThat("", result, hasSize(1));
        assertThat("", messages, contains("Please provide valid ISO country code."));
    }

    @Test
    void verifyCardHolderEmailNotMandatory() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("PI", null, "BOOKING", "PAY_NOW", null, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, en.name(), null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var billing = new Billing(TITLE, CARDHOLDER_FIRST_NAME, CARDHOLDER_LAST_NAME, "", CARDHOLDER_TELEPHONE, buildFullAddress());
        var payment = getPayment(PAYMENT_SUBTYPE_ECOMM, amount, billing, null);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();
        var result = localValidatorFactory.validate(paymentRequest);
        assertThat("", result, hasSize(0));
    }

    @Test
    void verifyChannelIsFrontDeskNoValidationForEmptyBilling() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("FRONT_DESK", null, "BOOKING", "PAY_NOW", null, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, en.name(), null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var billing = new Billing(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, buildEmptyAddress());
        var payment = getPayment(PAYMENT_SUBTYPE_ECOMM, amount, billing, null);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();

        var result = localValidatorFactory.validate(paymentRequest);

        assertThat("", result, hasSize(0));
    }

    @Test
    void verifyChannelIsPIValidationForEmptyBilling() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("PI", null, "BOOKING", "PAY_NOW", null, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, en.name(), null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var billing = new Billing(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, buildEmptyAddress());
        var payment = getPayment(PAYMENT_SUBTYPE_ECOMM, amount, billing, null);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();

        var result = localValidatorFactory.validate(paymentRequest);

        assertThat("", result, hasSize(1));
    }

    @Test
    void verifyChannelIsPIValidationForAddress() {
        var requestId = UUID.randomUUID().toString();
        var businessSite = new BusinessSite("LONHOL", "Lonhol", BusinessType.HOTEL.name(), "London", null, null);
        var booking = new Booking("PI", null, "BOOKING", "PAY_NOW", null, businessSite, ARRIVAL_DATE, DEPARTURE_DATE, en.name(), null, null, null,"GAA-3d49012a-48d0-4dc9-9dcc-87cba647e354");
        var amount = new Amount(GBP.name(), 1000);
        var billing = new Billing(TITLE, CARDHOLDER_FIRST_NAME, CARDHOLDER_LAST_NAME, "EMAIL", CARDHOLDER_TELEPHONE, buildNotAddressWithNoAddressLinesInfo());
        var payment = getPayment(PAYMENT_SUBTYPE_ECOMM, amount, billing, null);
        var paymentRequest = PaymentRequest.builder().payment(payment).booking(booking).requestId(requestId).build();

        var result = localValidatorFactory.validate(paymentRequest);

        assertThat("", result, hasSize(1));
    }


    private List<String> getErrorMessages(Set<ConstraintViolation<PaymentRequest>> result) {
        return result.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    }

    private Address buildFullAddress() {
        return new Address(ADDRESS_LINE1, ADDRESS_LINE2, ADDRESS_LINE3, ADDRESS_LINE4, null, COUNTRY_CODE, POSTCODE, null, null);
    }

    private Address buildNotAddressWithNoAddressLinesInfo() {
        return new Address(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, COUNTRY_CODE, POSTCODE, EMPTY, EMPTY);
    }

    private Address buildEmptyAddress() {
        return new Address(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, COUNTRY_CODE, EMPTY, EMPTY, EMPTY);
    }

    private Billing getBilling() {
        return new Billing(TITLE, CARDHOLDER_FIRST_NAME, CARDHOLDER_LAST_NAME, CARDHOLDER_EMAIL, CARDHOLDER_TELEPHONE, buildFullAddress());
    }

    private uk.co.whitbread.payments.model.Payment getPayment(String subtype,Amount amount, Billing billing, Card card) {
        var payment = new uk.co.whitbread.payments.model.Payment();
        payment.setType("CARD");
        payment.setSubType(subtype);
        payment.setSettlementReference(null);
        payment.setEnvironment("https://www.premierinn.com");
        payment.setCardPresent(false);
        payment.setCard(card);
        payment.setAmount(amount);
        payment.setBilling(billing!=null?billing:getBilling());
        payment.setMit(mit);
        payment.setPaypalNonce(null);
        payment.setPaypalDeviceData(null);
        return payment;
    }
}