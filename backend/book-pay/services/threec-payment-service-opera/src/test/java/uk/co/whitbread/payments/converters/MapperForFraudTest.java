package uk.co.whitbread.payments.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.config.PaypalConfig;
import uk.co.whitbread.payments.config.PaypalSensitiveData;
import uk.co.whitbread.payments.model.Booking;
import uk.co.whitbread.payments.model.BusinessSite;
import uk.co.whitbread.payments.model.Guest;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.Room;
import uk.co.whitbread.payments.model.threec.FraudCheckData;
import uk.co.whitbread.payments.model.threec.InitialiseRequest;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionRequest;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionRequest;
import uk.co.whitbread.payments.properties.AccountConfigProperties;
import uk.co.whitbread.payments.properties.ProviderAccount;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class MapperForFraudTest {
    private static final String CALL_CENTER_PROFILE = "configCallCenter";
    private static final String UK_PROFILE = "Web profile";
    private static final String GERMANY_PROFILE = "Germany website";
    private static final String ANY_CHANNEL = "ANY_CHANNEL";
    private static final String ANY_SUB_CHANNEL = "ANY_SUB_CHANNEL";
    private static final String ANY_LANGUAGE = "ANY_LANGUAGE";
    private static final String ANY_TYPE = "ANY_TYPE";
    private static final String ANY_NAME = "ANY_NAME";
    private static final String ANY_IDENTIFIER = "ANY_IDENTIFIER";
    private static final String ANY_LOCATION = "ANY_LOCATION";
    private static final String TRUE = "true";
    private static final String ANY_RATE = "ANY_RATE";
    private static final String SERVICES = "ANY_SERVICE,OTHER_SERVICE";
    private static final LocalDate ANY_ARRIVAL_DATE = LocalDate.of(2030, 1, 1);
    private static final int ANY_NO_OF_NIGHTS = 2;
    private static final int ANY_NO_OF_GUESTS = 2;
    private static final int ANY_NO_OF_ROOMS = 1;
    private static final int ANY_NO_OF_PREV_BOOKINGS = 1;
    private static final String ANY_USERNAME = "username";
    private static final String ANY_PASSWORD = "password";

    PaymentRequest paymentRequest = null;

    @BeforeEach
    public void setUp() {
        paymentRequest = new PaymentRequest();
        Room room = new Room();
        room.setType(ANY_TYPE);
        room.setAdults(2);
        room.setRate(ANY_RATE);
        paymentRequest.setBooking(Booking.builder()
                .channel(ANY_CHANNEL)
                .subChannel(ANY_SUB_CHANNEL)
                .language(ANY_LANGUAGE)
                .arrivalDate(ANY_ARRIVAL_DATE)
                .departureDate(LocalDate.of(2030, 1, 3))
                .rooms(Collections.singletonList(room))
                .leadGuest(Guest.builder()
                        .name(ANY_NAME)
                        .registered(true)
                        .registeredSince(LocalDate.of(2021, 1, 1))
                        .previousBookings(1)
                        .build())
                .businessSite(BusinessSite.builder()
                        .name(ANY_NAME)
                        .identifier(ANY_IDENTIFIER)
                        .location(ANY_LOCATION)
                        .additionalServices(List.of("ANY_SERVICE", "OTHER_SERVICE"))
                        .build())
                .build());
    }

    @Test
    void givenAFraudConfigurationDisabled_thenNoFraudInfoIsAdded() {
        MapperForFraud mapper = new MapperForFraud(false, "0");
        NoCardReadTransactionRequest.Params params = new NoCardReadTransactionRequest.Params();

        mapper.mapForNoCardRead(null, params, paymentRequest);

        assertNull(params.getFraudCheckData());
    }

    @Test
    void givenAccountConfigurationWithFraudScreeningDisabled_thenNoFraudInfoIsAdded() {
        MapperForFraud mapper = new MapperForFraud(true, "5");
        ProviderAccount account = new ProviderAccount();
        AccountConfigProperties configuration = new AccountConfigProperties();
        configuration.setFraudScreened(false);
        account.setConfiguration(configuration);
        NoCardReadTransactionRequest.Params params = new NoCardReadTransactionRequest.Params();

        mapper.mapForNoCardRead(account, params, paymentRequest);

        assertNull(params.getFraudCheckData());
    }

    @Test
    void givenAccountConfigurationWithFraudScreeningEnabled_thenFraudInfoIsAddedWithCallCenterFraudProfile() {
        MapperForFraud mapper = new MapperForFraud(true, "5");
        ProviderAccount account = new ProviderAccount();
        AccountConfigProperties configuration = new AccountConfigProperties();
        configuration.setFraudScreened(true);
        configuration.setFraudProfile(CALL_CENTER_PROFILE);
        account.setConfiguration(configuration);

        NoCardReadTransactionRequest.Params params = new NoCardReadTransactionRequest.Params();
        params.setFraudCheckData(new FraudCheckData());

        mapper.mapForNoCardRead(account, params, paymentRequest);

        assertEquals(CALL_CENTER_PROFILE, params.getFraudCheckData().getFraudProfileName());
    }

    @Test
    void givenAccountConfigurationWithFraudScreeningEnabled_thenFraudInfoIsAdded() {
        MapperForFraud mapper = new MapperForFraud(true, "5");
        ProviderAccount account = new ProviderAccount();
        AccountConfigProperties configuration = new AccountConfigProperties();
        configuration.setFraudScreened(true);
        configuration.setFraudProfile(CALL_CENTER_PROFILE);
        account.setConfiguration(configuration);

        NoCardReadTransactionRequest.Params params = new NoCardReadTransactionRequest.Params();
        params.setFraudCheckData(new FraudCheckData());

        mapper.mapForNoCardRead(account, params, paymentRequest);

        assertEquals(CALL_CENTER_PROFILE, params.getFraudCheckData().getFraudProfileName());
        assertEquals(ANY_CHANNEL, params.getFraudCheckData().getChannel());
        assertEquals(ANY_SUB_CHANNEL, params.getFraudCheckData().getSubChannel());
        assertEquals(ANY_LANGUAGE, params.getFraudCheckData().getWebsite());
        assertEquals(ANY_ARRIVAL_DATE, params.getFraudCheckData().getArrivalDate());
        assertEquals(ANY_NO_OF_NIGHTS, params.getFraudCheckData().getNoOfNights());
        assertEquals(ANY_TYPE, params.getFraudCheckData().getRoomType());
        assertEquals(ANY_NO_OF_ROOMS, params.getFraudCheckData().getNoOfRooms());
        assertEquals(ANY_NAME, params.getFraudCheckData().getGuestName());
        assertEquals(ANY_NAME, params.getFraudCheckData().getBookerName());
        assertEquals(ANY_IDENTIFIER, params.getFraudCheckData().getHotelCode());
        assertEquals(ANY_LOCATION, params.getFraudCheckData().getHotelCity());
        assertEquals(TRUE, params.getFraudCheckData().getMemberRegistered());
        assertEquals(ANY_NO_OF_GUESTS, params.getFraudCheckData().getNoOfGuests());
        assertEquals(ANY_RATE, params.getFraudCheckData().getRoomRateType());
        assertEquals(SERVICES, params.getFraudCheckData().getAdditionalServices());
        assertEquals(ANY_NO_OF_PREV_BOOKINGS, params.getFraudCheckData().getPreviousBookingsCount());
    }

    /////

    @Test
    void givenAFraudConfigurationDisabled_thenNoFraudInfoIsAddedForInitialise() {
        MapperForFraud mapper = new MapperForFraud(false, "0");
        InitialiseRequest initialiseRequest = new InitialiseRequest();

        mapper.mapForInitialise(null, initialiseRequest, paymentRequest);

        assertNull(initialiseRequest.getFraudCheckData());
    }

    @Test
    void givenAccountConfigurationWithFraudScreeningDisabled_thenNoFraudInfoIsAddedForInitialise() {
        MapperForFraud mapper = new MapperForFraud(true, "5");
        ProviderAccount account = new ProviderAccount();
        AccountConfigProperties configuration = new AccountConfigProperties();
        configuration.setFraudScreened(false);
        account.setConfiguration(configuration);
        InitialiseRequest initialiseRequest = new InitialiseRequest();

        mapper.mapForInitialise(account, initialiseRequest, paymentRequest);

        assertNull(initialiseRequest.getFraudCheckData());
    }

    @Test
    void givenAccountConfigurationWithFraudScreeningEnabled_thenFraudInfoIsAddedWithEnFraudProfileForInitialise() {
        MapperForFraud mapper = new MapperForFraud(true, "5");
        ProviderAccount account = new ProviderAccount();
        AccountConfigProperties configuration = new AccountConfigProperties();
        configuration.setFraudScreened(true);
        configuration.setFraudProfile(UK_PROFILE);
        account.setConfiguration(configuration);
        InitialiseRequest initialiseRequest = new InitialiseRequest();

        mapper.mapForInitialise(account, initialiseRequest, paymentRequest);

        assertEquals(UK_PROFILE, initialiseRequest.getFraudCheckData().getFraudProfileName());
    }

    @Test
    void givenAccountConfigurationWithFraudScreeningEnabled_thenFraudInfoIsAddedWithDeFraudProfileForInitialise() {
        MapperForFraud mapper = new MapperForFraud(true, "5");
        ProviderAccount account = new ProviderAccount();
        AccountConfigProperties configuration = new AccountConfigProperties();
        configuration.setFraudScreened(true);
        configuration.setFraudProfile(GERMANY_PROFILE);
        account.setConfiguration(configuration);
        InitialiseRequest initialiseRequest = new InitialiseRequest();

        mapper.mapForInitialise(account, initialiseRequest, paymentRequest);

        assertEquals(GERMANY_PROFILE, initialiseRequest.getFraudCheckData().getFraudProfileName());
        assertEquals(ANY_CHANNEL, initialiseRequest.getFraudCheckData().getChannel());
        assertEquals(ANY_SUB_CHANNEL, initialiseRequest.getFraudCheckData().getSubChannel());
        assertEquals(ANY_LANGUAGE, initialiseRequest.getFraudCheckData().getWebsite());
        assertEquals(ANY_ARRIVAL_DATE, initialiseRequest.getFraudCheckData().getArrivalDate());
        assertEquals(ANY_NO_OF_NIGHTS, initialiseRequest.getFraudCheckData().getNoOfNights());
        assertEquals(ANY_TYPE, initialiseRequest.getFraudCheckData().getRoomType());
        assertEquals(ANY_NO_OF_ROOMS, initialiseRequest.getFraudCheckData().getNoOfRooms());
        assertEquals(ANY_NAME, initialiseRequest.getFraudCheckData().getGuestName());
        assertEquals(ANY_NAME, initialiseRequest.getFraudCheckData().getBookerName());
        assertEquals(ANY_IDENTIFIER, initialiseRequest.getFraudCheckData().getHotelCode());
        assertEquals(ANY_LOCATION, initialiseRequest.getFraudCheckData().getHotelCity());
        assertEquals(TRUE, initialiseRequest.getFraudCheckData().getMemberRegistered());
        assertEquals(ANY_NO_OF_GUESTS, initialiseRequest.getFraudCheckData().getNoOfGuests());
        assertEquals(ANY_RATE, initialiseRequest.getFraudCheckData().getRoomRateType());
        assertEquals(SERVICES, initialiseRequest.getFraudCheckData().getAdditionalServices());
        assertEquals(ANY_NO_OF_PREV_BOOKINGS, initialiseRequest.getFraudCheckData().getPreviousBookingsCount());
    }

    @Test
    void givenEmptyPaymentRequest_thenDefaultsValuesAreReturned(){
        MapperForFraud mapper = new MapperForFraud(true, "5");
        ProviderAccount account = new ProviderAccount();
        AccountConfigProperties configuration = new AccountConfigProperties();
        configuration.setFraudScreened(true);
        configuration.setFraudProfile(GERMANY_PROFILE);
        account.setConfiguration(configuration);
        InitialiseRequest initialiseRequest = new InitialiseRequest();
        PaymentRequest emptyPaymentRequest = new PaymentRequest();

        mapper.mapForInitialise(account, initialiseRequest, emptyPaymentRequest);

        assertEquals(FraudCheckData.builder().build(), initialiseRequest.getFraudCheckData());
    }

    @Test
    void givenFraudScreeningEnabled_thenFraudInfoIsAdded_Paypal() {
        MapperForFraud mapper = new MapperForFraud(true, "0");
        PaypalForwardAPITransactionRequest request = mockForwardApiTransactionRequest();
        ProviderAccount account = new ProviderAccount();
        AccountConfigProperties configuration = new AccountConfigProperties();
        configuration.setFraudScreened(true);
        configuration.setFraudProfile(UK_PROFILE);
        account.setConfiguration(configuration);

        mapper.mapForPaypal(account, request, paymentRequest);

        assertNotNull(request.getSensitiveData());
        assertEquals(UK_PROFILE, request.getSensitiveData().getFraudProfileName());
    }

    private PaypalForwardAPITransactionRequest mockForwardApiTransactionRequest() {

        var tsp = PaypalForwardAPITransactionRequest.Tsp.builder()
            .currencyCode("EUR")
            .build();
        var sensitiveData = PaypalForwardAPITransactionRequest.SensitiveData.builder()
            .type(mockPaypalConfigSensitiveData().getPaypalSensitiveData().getZeroAuthType())
            .version(mockPaypalConfigSensitiveData().getPaypalSensitiveData().getVersion())
            .validationID(ANY_USERNAME)
            .validationCode(ANY_PASSWORD)
            .validationCodeHash(mockPaypalConfigSensitiveData().getPaypalSensitiveData().getValidationCodeHash())
            .optionFlags(mockPaypalConfigSensitiveData().getPaypalSensitiveData().getOptionFlags())
            .cofIndicator(mockPaypalConfigSensitiveData().getPaypalSensitiveData().getCofIndicator())
            .transInitiator(mockPaypalConfigSensitiveData().getPaypalSensitiveData().getTransInitiator())
            .build();
        var forwardAPIData = PaypalForwardAPITransactionRequest.ForwardAPIData.builder()
            .cardholderStreetAddress1("address line 1")
            .cardholderCity("N/A")
            .cardholderCountry("GB")
            .cardholderNameFirst("First name")
            .cardholderNameLast("LastName")
            .build();
        PaypalForwardAPITransactionRequest request = PaypalForwardAPITransactionRequest.builder()
            .tsp(tsp)
            .config(null)
            .sensitiveData(sensitiveData)
            .data(forwardAPIData)
            .build();
        return request;
    }
    private PaypalConfig mockPaypalConfigSensitiveData() {

        PaypalSensitiveData data = new PaypalSensitiveData();
        data.setZeroAuthType("EftAuthorization");
        data.setVersion("W2MXG520");
        data.setValidationCodeHash("");
        data.setOptionFlags("G");
        data.setCofIndicator("C");
        data.setTransInitiator("M");

        PaypalConfig config = new PaypalConfig();
        config.setPaypalSensitiveData(data);

        return config;
    }
}