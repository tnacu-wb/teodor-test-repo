package uk.co.whitbread.payments.util;

import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.Address;
import uk.co.whitbread.payments.model.Amount;
import uk.co.whitbread.payments.model.Billing;
import uk.co.whitbread.payments.model.Booking;
import uk.co.whitbread.payments.model.BusinessSite;
import uk.co.whitbread.payments.model.Card;
import uk.co.whitbread.payments.model.CardHolder;
import uk.co.whitbread.payments.model.CreateTokenRequest;
import uk.co.whitbread.payments.model.Guest;
import uk.co.whitbread.payments.model.Mit;
import uk.co.whitbread.payments.model.MitType;
import uk.co.whitbread.payments.model.Payment;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.threec.InitialiseRequest;
import uk.co.whitbread.payments.properties.AccountConfigProperties;
import uk.co.whitbread.payments.properties.ProviderAccount;

import java.util.List;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static uk.co.whitbread.payments.model.Currency.GBP;

public class PaymentRequestFixtures {

    private static final String LANGUAGE = "en";
    private static final String POST_URL_SUCCESS = "testPostUrlSuccess/%s";
    private static final String POST_URL_FAILURE = "testPostUrlFailure/%s";
    private static final String TOKENIZED_CARD = "4216333880397891103";
    private static final String SERVICE_ACTION = "3dsauthorise";
    private static final String USERNAME = "WhitbreadTestLondonHotel";
    private static final String PASSWORD = "WhitbreadTestLondonHotel1";
    private static final String NEW_CARD_TEMPLATE = "new-card-template.xml";
    private static final String SAVED_CARD_TEMPLATE = "saved-card-template.xml";
    private static final int AMOUNT = 2000;
    private static final String ANY_MONTH = "ANY_MONTH";
    private static final String ANY_YEAR = "ANY_YEAR";
    private static final String ANY_REF = "ANY_REF";
    private static final String CARD_ON_FILE_INDICATOR = "P";
    private static final String ANY_TRANS_INITIATOR = "ANY_TRANS_INITIATOR";
    private static final String ANY_OPTION_FLAG = "ANY_OPTION_FLAG";
    private static final String NEW_CARD_TRX_OPTIONS = "G";
    private static final String TOKEN_TRX_OPTIONS = "P";
    private static final String EMPTY_STRING = "";
    private static final String NOT_APPLICABLE = "N/A";
    private static final String CARDHOLDER_NAME = "Mr James Bond";
    private static final String FIRST_NAME = "Ashutosh";
    private static final String LAST_NAME = "Dhakate";
    private static final String PAYMENT_ID = "paymentId";

    public static PaymentRequest getPaymentRequest(String requestId, String bookingType) {
        return PaymentRequest.builder()
                .requestId(requestId)
                .payment(getPayment())
                .booking(getBooking(bookingType)).build();
    }

    public static Payment getPayment() {
        return Payment.builder()
                .type("CARD")
                .subType("ECOMM")
                .environment("https://www.premierinn.com")
                .billing(Billing.builder()
                        .firstName("Scott")
                        .lastName("Santos")
                        .email("accept@email.com")
                        .address(Address.builder()
                                .line1("120 Holborn")
                                .line2("1st Floor")
                                .postalCode("EC1N 2TD")
                                .countryCode("GB").build())
                        .build())
                .amount(getAmount())
                .card(getCard())
                .build();
    }

    public static Card getCard() {
        var card = new Card();
        card.setToken(TOKENIZED_CARD);
        card.setExpiryMonth("06");
        card.setExpiryYear("26");
        return card;
    }

    public static Booking getBooking(String bookingType) {
        return Booking.builder()
                .type(bookingType)
                .language(LANGUAGE)
                .journey("BOOKING")
                .channel("PI")
                .businessSite(getBusinessSite())
                .leadGuest(Guest.builder()
                        .name("Scott Santos").build())
                .build();
    }

    public static BusinessSite getBusinessSite() {
        BusinessSite site = new BusinessSite();
        site.setType("HOTEL");
        site.setIdentifier("LONHOL");
        site.setName("London Holborn");
        site.setLocation("London");
        return site;
    }

    public static Amount getAmount() {
        return Amount.builder()
                .currency(GBP.name())
                .minorUnits(AMOUNT)
                .build();
    }

    public static InitialiseRequest getInitialiseRequest(String paymentId) {
        InitialiseRequest initialiseRequest = new InitialiseRequest();
        initialiseRequest.setTrxMerchantReference(paymentId);
        initialiseRequest.setCardholderAddressLine1("120 Holborn");
        initialiseRequest.setCardholderAddressLine2("1st Floor");
        initialiseRequest.setCardholderAddressPostalCode("EC1N 2TD");
        initialiseRequest.setCardholderAddressCountry("GB");
        initialiseRequest.setCardholderFirstName("Scott");
        initialiseRequest.setCardholderLastName("Santos");
        initialiseRequest.setCardholderEmail("accept@email.com");
        initialiseRequest.setCardholderAddressState(EMPTY_STRING);
        initialiseRequest.setCardholderAddressCity(NOT_APPLICABLE);
        initialiseRequest.setSecurityEMerchantId(USERNAME);
        initialiseRequest.setSecurityValidationCode(PASSWORD);
        initialiseRequest.setTrxAmountCurrencyCode(GBP.name());
        initialiseRequest.setTrxAmountValue(valueOf(AMOUNT));
        initialiseRequest.setTrxOptions(TOKEN_TRX_OPTIONS);
        initialiseRequest.setFraudMode(AccountConfigProperties.DEFAULT_FRAUD_MODE);
        initialiseRequest.setCardOnFileIndicator(AccountConfigProperties.DEFAULT_CARD_ON_FILE_INDICATOR);
        initialiseRequest.setPostUrlSuccess(format(POST_URL_SUCCESS, paymentId));
        initialiseRequest.setPostUrlFailure(format(POST_URL_FAILURE, paymentId));
        initialiseRequest.setRedirectApproved(format(POST_URL_SUCCESS, paymentId));
        initialiseRequest.setRedirectDeclined(format(POST_URL_FAILURE, paymentId));
        initialiseRequest.setServiceAction(SERVICE_ACTION);
        initialiseRequest.setTemplateId(SAVED_CARD_TEMPLATE);
        initialiseRequest.setLanguage(LANGUAGE);
        initialiseRequest.setToken(TOKENIZED_CARD);
        initialiseRequest.setCardExpiryMonth("06");
        initialiseRequest.setCardExpiryYear("26");
        initialiseRequest.setTokenInjectionAction("T");
        return initialiseRequest;
    }

    public static ProviderAccount getProviderAccount() {
        ProviderAccount account = new ProviderAccount();
        AccountConfigProperties configuration = new AccountConfigProperties();
        configuration.setNewCardTemplate(NEW_CARD_TEMPLATE);
        configuration.setSavedCardTemplate(SAVED_CARD_TEMPLATE);
        configuration.setServiceAction(SERVICE_ACTION);
        configuration.setNewCardTrxOption(NEW_CARD_TRX_OPTIONS);
        configuration.setSavedCardTrxOption(TOKEN_TRX_OPTIONS);
        account.setConfiguration(configuration);
        return account;
    }

    public static ProviderAccount createProviderAccountEft() {
        ProviderAccount account = new ProviderAccount();
        AccountConfigProperties properties = new AccountConfigProperties();
        properties.setCardOnFileIndicator(CARD_ON_FILE_INDICATOR);
        properties.setTransInitiator(ANY_TRANS_INITIATOR);
        properties.setOptionsFlag(ANY_OPTION_FLAG);
        properties.setServiceAction("EftAuthorization");
        account.setConfiguration(properties);
        return account;
    }

    public static ProviderAccount createProviderAccount() {
        ProviderAccount account = new ProviderAccount();
        AccountConfigProperties properties = new AccountConfigProperties();
        properties.setCardOnFileIndicator(CARD_ON_FILE_INDICATOR);
        properties.setTransInitiator(ANY_TRANS_INITIATOR);
        properties.setOptionsFlag(ANY_OPTION_FLAG);
        account.setConfiguration(properties);
        return account;
    }

    public static Payment createPaymentWithMit() {
        return Payment.builder()
                .type("ANY_TYPE")
                .mit(Mit.builder()
                        .scaReference(ANY_REF)
                        .type(MitType.L)
                        .build())
                .card(createCard())
                .build();
    }

    public static Card createCard() {
        Card card = new Card();
        card.setExpiryMonth(ANY_MONTH);
        card.setExpiryYear(ANY_YEAR);
        return card;
    }

    public static PaymentsSchema getPaymentSchema(String requestId, String bookingType) {
        return PaymentsSchema.builder()
                .requestId(requestId)
                .payment(getPayment())
                .booking(getBooking(bookingType)).build();
    }

    public static CreateTokenRequest getCreateTokenRequest(String requestId) {
        return CreateTokenRequest.builder()
                .cardNumber("4242424242424242")
                .expiryYear("22")
                .expiryMonth("12")
                .cardHolder(CardHolder.builder()
                        .cardHolderName(CARDHOLDER_NAME)
                        .address(Address.builder()
                                .line1("120 Holborn")
                                .line2("1st Floor")
                                .postalCode("EC1N 2TD")
                                .build())
                        .build())
                .build();
    }

    public static LinkedMultiValueMap<String, String> getWebhookData() {
        var multiValueMap = new LinkedMultiValueMap<String, String>();
        multiValueMap.put("TxState", List.of("CQ"));
        multiValueMap.put("TxID", List.of("63d325c1-fb62-4a12-b368-a1b1472f802a"));
        multiValueMap.put("ref", List.of("1211114"));
        multiValueMap.put("3DSIndicator", List.of("3"));
        multiValueMap.put("Amount", List.of("1000"));
        multiValueMap.put("AuthorisationCode", List.of("110920"));
        multiValueMap.put("CardNumberFirst6", List.of("424242"));
        multiValueMap.put("CardType", List.of("VS"));
        multiValueMap.put("CardTypeName", List.of("VISA"));
        multiValueMap.put("CurrencyCode", List.of("GBP"));
        multiValueMap.put("CardExpiry", List.of("2402"));
        multiValueMap.put("FirstName", List.of(FIRST_NAME));
        multiValueMap.put("LastName", List.of(LAST_NAME));
        multiValueMap.put("ReturnCode", List.of("0000"));
        multiValueMap.put("SCATransRef", List.of("V0202105131109203UGA"));
        multiValueMap.put("card_pan_last4digits", List.of("4242"));
        multiValueMap.put("TokenNo", List.of("4943056398164344242"));
        return multiValueMap;
    }

    public static ServerRequest getServerRequest(LinkedMultiValueMap<String, String> multiValueMap, String paymentId) {
        return MockServerRequest.builder()
                .pathVariable(PAYMENT_ID, paymentId)
                .body(Mono.just(multiValueMap));
    }
}
