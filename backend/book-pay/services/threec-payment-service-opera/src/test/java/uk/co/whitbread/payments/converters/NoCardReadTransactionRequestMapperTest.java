package uk.co.whitbread.payments.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.config.PaypalConfig;
import uk.co.whitbread.payments.config.PaypalSensitiveData;
import uk.co.whitbread.payments.mapper.PaymentMapper;
import uk.co.whitbread.payments.model.Booking;
import uk.co.whitbread.payments.model.EMerchantDetails;
import uk.co.whitbread.payments.model.MitType;
import uk.co.whitbread.payments.model.Payment;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.Refund;
import uk.co.whitbread.payments.model.RefundRequest;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionRequest;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionResponseBody;
import uk.co.whitbread.payments.properties.ProviderAccount;
import uk.co.whitbread.payments.service.EMerchantService;
import uk.co.whitbread.payments.util.PaymentRequestFixtures;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.payments.model.ChannelType.CCC;
import static uk.co.whitbread.payments.model.ChannelType.WEB;
import static uk.co.whitbread.payments.model.Currency.GBP;

@ExtendWith(MockitoExtension.class)
class NoCardReadTransactionRequestMapperTest {

    private static final String LANGUAGE = "en";
    private static final String BOOKING_REFERENCE = "ANY_BOOKING_REFERENCE";
    private static final int AMOUNT = 2000;
    private static final String ANY_MONTH = "ANY_MONTH";
    private static final String ANY_YEAR = "ANY_YEAR";
    private static final String ANY_REF = "ANY_REF";
    private static final String CARD_ON_FILE_INDICATOR = "P";
    private static final String ANY_TRANS_INITIATOR = "ANY_TRANS_INITIATOR";
    private static final String TOKEN_TRX_OPTIONS = "P";
    private static final String ANY_PAYMENT_ID = "1234567890D";
    private static final String EMPTY_STRING = "";
    private static final String NOT_APPLICABLE = "N/A";
    private static final String PAY_ON_ARRIVAL = "PAY_ON_ARRIVAL";
    private static final String ANY_USERNAME = "username";
    private static final String ANY_PASSWORD = "password";
    private static final String EMAIL = "default@whitbread.com";

    @Mock
    private EMerchantService eMerchantService;
    @Mock
    private CustomMapper fraudCustomMapper;
    @Mock
    private CardholderStateMapper stateMapper;
    private NoCardReadTransactionRequestMapper noCardReadTransactionRequestMapper;
    @Mock
    private PaypalConfig paypalConfig;
    private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    @BeforeEach
    public void setUp() {
        noCardReadTransactionRequestMapper = new NoCardReadTransactionRequestMapper(paymentMapper, eMerchantService, fraudCustomMapper, stateMapper, paypalConfig);
    }

    @Test
    void givenAPaymentRequestWithMitInfo_ThenMitInfoIsFilledIntoParams() {
        EMerchantDetails eMerchantDetails = new EMerchantDetails(ANY_USERNAME, ANY_PASSWORD);
        String cardOnFileIndicator = "C";
        PaypalSensitiveData sensitiveData = new PaypalSensitiveData();
        sensitiveData.setMitCofIndicator(cardOnFileIndicator);
        when(paypalConfig.getPaypalSensitiveData()).thenReturn(sensitiveData);
        when(stateMapper.getCardholderState(any())).thenReturn("");
        when(eMerchantService.getEMerchantDetails(any(), any())).thenReturn(eMerchantDetails);
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPayment(PaymentRequestFixtures.createPaymentWithMit());
        paymentRequest.setBooking(Booking.builder().language(LANGUAGE).channel(CCC.name()).reference(BOOKING_REFERENCE).build());
        NoCardReadTransactionRequest expected = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        NoCardReadTransactionRequest.Params params = new NoCardReadTransactionRequest.Params();
        params.setCardOnFileIndicator(cardOnFileIndicator);
        params.setTransInitiator(ANY_TRANS_INITIATOR);
        params.setScaTransRef(ANY_REF);
        params.setMitType(MitType.L);
        params.setExpiryDate(ANY_MONTH + ANY_YEAR);
        params.setOptionFlags(TOKEN_TRX_OPTIONS);
        params.setTransactionReference(ANY_PAYMENT_ID);
        params.setBookingReference(BOOKING_REFERENCE);
        params.setCardholderEmail(EMAIL);
        params.setCardholderState(EMPTY_STRING);
        params.setCardholderCity(NOT_APPLICABLE);
        params.setBookingChannel(CCC.name());
        request.setParams(params);
        expected.setRequest(request);

        var requestTransformed = noCardReadTransactionRequestMapper.populateNoCardReadRequest(paymentRequest, PaymentRequestFixtures.createProviderAccount(), ANY_PAYMENT_ID);

        assertEquals(expected.getRequest().getParams(), requestTransformed.getRequest().getParams());
    }


    @Test
    void givenAPaymentRequestWithoutMitInfo_ThenMitInfoIsNOTFilledIntoParams() {
        EMerchantDetails eMerchantDetails = new EMerchantDetails(ANY_USERNAME, ANY_PASSWORD);
        when(eMerchantService.getEMerchantDetails(any(), any())).thenReturn(eMerchantDetails);
        when(stateMapper.getCardholderState(any())).thenReturn("");
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPayment(Payment.builder()
                .card(PaymentRequestFixtures.createCard())
                .type("ANY_TYPE")
                .build());
        paymentRequest.setBooking(Booking.builder().language(LANGUAGE).reference(BOOKING_REFERENCE).build());
        NoCardReadTransactionRequest expected = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        NoCardReadTransactionRequest.Params params = new NoCardReadTransactionRequest.Params();
        params.setCardOnFileIndicator(CARD_ON_FILE_INDICATOR);
        params.setExpiryDate(ANY_MONTH + ANY_YEAR);
        params.setOptionFlags(TOKEN_TRX_OPTIONS);
        params.setTransactionReference(ANY_PAYMENT_ID);
        params.setBookingReference(BOOKING_REFERENCE);
        params.setCardholderEmail(EMAIL);
        params.setCardholderState(EMPTY_STRING);
        params.setCardholderCity(NOT_APPLICABLE);
        request.setParams(params);
        expected.setRequest(request);

        var requestTransformed = noCardReadTransactionRequestMapper.populateNoCardReadRequest(paymentRequest, PaymentRequestFixtures.createProviderAccount(), ANY_PAYMENT_ID);

        assertEquals(requestTransformed.getRequest().getParams(), expected.getRequest().getParams());
    }

    @Test
    void givenARefundRequest_ThenVerifyMapping() {
        EMerchantDetails eMerchantDetails = new EMerchantDetails(ANY_USERNAME, ANY_PASSWORD);
        when(eMerchantService.getEMerchantDetails(any(), any())).thenReturn(eMerchantDetails);
        String requestId = UUID.randomUUID().toString();
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setRefund(Refund.builder().card(PaymentRequestFixtures.createCard()).amount(PaymentRequestFixtures.getAmount()).build());
        refundRequest.setBooking(Booking.builder().language(LANGUAGE).reference(BOOKING_REFERENCE).build());
        NoCardReadTransactionRequest expected = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        NoCardReadTransactionRequest.Params params = new NoCardReadTransactionRequest.Params();
        params.setCardOnFileIndicator(CARD_ON_FILE_INDICATOR);
        params.setAmount(String.valueOf(-AMOUNT));
        params.setCurrency(GBP.name());
        params.setOptionFlags(TOKEN_TRX_OPTIONS);
        params.setExpiryDate(ANY_MONTH + ANY_YEAR);
        params.setTransactionReference(requestId);
        params.setBookingReference(BOOKING_REFERENCE);
        request.setParams(params);
        expected.setRequest(request);

        var requestTransformed = noCardReadTransactionRequestMapper.populateNoCardReadRequest(refundRequest, PaymentRequestFixtures.createProviderAccount(), requestId);

        assertEquals(expected.getRequest().getParams(), requestTransformed.getRequest().getParams());
    }

    @Test
    void givenAPayOnArrivalRequestForEFT_ThenAmountShouldBeZero() {
        EMerchantDetails eMerchantDetails = new EMerchantDetails(ANY_USERNAME, ANY_PASSWORD);
        when(eMerchantService.getEMerchantDetails(any(), any())).thenReturn(eMerchantDetails);
        String requestId = UUID.randomUUID().toString();
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPayment(Payment.builder().card(PaymentRequestFixtures.createCard()).build());
        paymentRequest.setBooking(Booking.builder().language(LANGUAGE).reference(BOOKING_REFERENCE).type(PAY_ON_ARRIVAL).build());
        NoCardReadTransactionRequest expected = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        NoCardReadTransactionRequest.Params params = new NoCardReadTransactionRequest.Params();
        params.setCardOnFileIndicator(CARD_ON_FILE_INDICATOR);
        params.setAmount(String.valueOf(AMOUNT));
        params.setCurrency(GBP.name());
        params.setOptionFlags(TOKEN_TRX_OPTIONS);
        params.setExpiryDate(ANY_MONTH + ANY_YEAR);
        params.setTransactionReference(requestId);
        params.setBookingReference(BOOKING_REFERENCE);
        request.setParams(params);
        expected.setRequest(request);

        var requestTransformed = noCardReadTransactionRequestMapper.populateNoCardReadRequest(paymentRequest, PaymentRequestFixtures.createProviderAccountEft(), requestId);

        assertEquals("0", requestTransformed.getRequest().getParams().getAmount());
    }

    @Test
    void givenARefundRequest_ThenVerifyMappingForWEBChannel() {
        EMerchantDetails eMerchantDetails = new EMerchantDetails(ANY_USERNAME, ANY_PASSWORD);
        when(eMerchantService.getEMerchantDetails(any(), any())).thenReturn(eMerchantDetails);
        String requestId = UUID.randomUUID().toString();
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setRefund(Refund.builder().card(PaymentRequestFixtures.createCard()).amount(PaymentRequestFixtures.getAmount()).build());
        refundRequest.setBooking(Booking.builder().language(LANGUAGE).channel(WEB.name()).reference(BOOKING_REFERENCE).build());
        NoCardReadTransactionRequest expected = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        NoCardReadTransactionRequest.Params params = new NoCardReadTransactionRequest.Params();
        params.setCardOnFileIndicator(CARD_ON_FILE_INDICATOR);
        params.setAmount(String.valueOf(-AMOUNT));
        params.setCurrency(GBP.name());
        params.setOptionFlags(TOKEN_TRX_OPTIONS);
        params.setExpiryDate(ANY_MONTH + ANY_YEAR);
        params.setTransactionReference(requestId);
        params.setBookingReference(BOOKING_REFERENCE);
        request.setParams(params);
        expected.setRequest(request);

        var requestTransformed = noCardReadTransactionRequestMapper.populateNoCardReadRequest(refundRequest, PaymentRequestFixtures.createProviderAccount(), requestId);

        assertNotNull(requestTransformed);
        assertEquals(eMerchantDetails.getUsername(), requestTransformed.getRequest().getCredentials().getValidationId());
        assertEquals(eMerchantDetails.getPassword(), requestTransformed.getRequest().getCredentials().getValidationCode());
        assertEquals(expected.getRequest().getParams(), requestTransformed.getRequest().getParams());
    }

    @Test
    void givenAPaypalMitRequest_ThenVerifyMapping() {
        EMerchantDetails eMerchantDetails = new EMerchantDetails(ANY_USERNAME, ANY_PASSWORD);
        when(eMerchantService.getEMerchantDetails(any(), any())).thenReturn(eMerchantDetails);
        String requestId = UUID.randomUUID().toString();

        PaypalSensitiveData sensitiveData = new PaypalSensitiveData();
        sensitiveData.setType("test");
        sensitiveData.setMitCofIndicator("C");
        sensitiveData.setVersion("version");

        when(paypalConfig.getPaypalSensitiveData()).thenReturn(sensitiveData);

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPayment(Payment.builder().card(PaymentRequestFixtures.createCard()).amount(PaymentRequestFixtures.getAmount()).build());
        paymentRequest.setBooking(Booking.builder().language(LANGUAGE).reference(BOOKING_REFERENCE).type(PAY_ON_ARRIVAL).build());

        PaypalForwardAPITransactionResponseBody responseBody = new PaypalForwardAPITransactionResponseBody();
        PaypalForwardAPITransactionResponseBody.Response response = new PaypalForwardAPITransactionResponseBody.Response();
        PaypalForwardAPITransactionResponseBody.Params paramsBody = new PaypalForwardAPITransactionResponseBody.Params();
        response.setType("test");
        response.setVersion("version");
        responseBody.setResponse(response);
        paramsBody.setScaReference("");
        responseBody.getResponse().setParams(paramsBody);

        NoCardReadTransactionRequest expected = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        NoCardReadTransactionRequest.Params params = new NoCardReadTransactionRequest.Params();
        params.setCardOnFileIndicator(CARD_ON_FILE_INDICATOR);
        params.setOptionFlags(TOKEN_TRX_OPTIONS);
        params.setTransactionReference(requestId);
        params.setBookingReference(BOOKING_REFERENCE);
        params.setScaTransRef(responseBody.getResponse().getParams().getScaReference());
        request.setParams(params);
        request.setType(paypalConfig.getPaypalSensitiveData().getType());
        request.setVersion(paypalConfig.getPaypalSensitiveData().getVersion());
        expected.setRequest(request);

        var requestTransformed =
            noCardReadTransactionRequestMapper.populatePaypalMitRequest(responseBody, paymentRequest, PaymentRequestFixtures.createProviderAccountEft(), requestId);

        assertEquals("P", requestTransformed.getRequest().getParams().getOptionFlags());
        assertEquals("N/A", requestTransformed.getRequest().getParams().getCardholderCity());
        assertEquals("C", requestTransformed.getRequest().getParams().getCardOnFileIndicator());
        assertEquals("paypal", requestTransformed.getRequest().getParams().getWalletType());
        assertEquals("", requestTransformed.getRequest().getParams().getScaTransRef());
    }

    @Test
    void givenAMitCcRequest_ThenVerifyMapping() {
      // Arrange
      EMerchantDetails eMerchantDetails = new EMerchantDetails(ANY_USERNAME, ANY_PASSWORD);
      when(eMerchantService.getEMerchantDetails(any(), any())).thenReturn(eMerchantDetails);

      String requestId = UUID.randomUUID().toString();

      ProviderAccount providerAccount = PaymentRequestFixtures.createProviderAccountEft();

      PaymentRequest paymentRequest = new PaymentRequest();
      paymentRequest.setPayment(
          Payment.builder()
              .card(PaymentRequestFixtures.createCard())
              .amount(PaymentRequestFixtures.getAmount())
              .build()
      );
      paymentRequest.setBooking(
          Booking.builder()
              .language(LANGUAGE)
              .reference(BOOKING_REFERENCE)
              .type(PAY_ON_ARRIVAL)
              .build()
      );

      NoCardReadTransactionRequest expected = new NoCardReadTransactionRequest();
      NoCardReadTransactionRequest.Request request = new NoCardReadTransactionRequest.Request();
      NoCardReadTransactionRequest.Params params = new NoCardReadTransactionRequest.Params();

      params.setTransactionReference(requestId);
      params.setBookingReference(BOOKING_REFERENCE);
      params.setOptionFlags(TOKEN_TRX_OPTIONS);
      params.setCardholderCity("N/A");
      params.setCardOnFileIndicator(providerAccount.getConfiguration().getCardOnFileIndicator());
      params.setTransInitiator(providerAccount.getConfiguration().getTransInitiator());

      request.setParams(params);
      request.setType(providerAccount.getConfiguration().getServiceAction());
      request.setVersion(providerAccount.getConfiguration().getVersion());
      expected.setRequest(request);

      // Act
      NoCardReadTransactionRequest actual =
          noCardReadTransactionRequestMapper.populateMitCcRequest(
              paymentRequest,
              providerAccount,
              requestId
          );

      // Assert
      assertEquals(TOKEN_TRX_OPTIONS, actual.getRequest().getParams().getOptionFlags());
      assertEquals("N/A", actual.getRequest().getParams().getCardholderCity());
      assertEquals(providerAccount.getConfiguration().getCardOnFileIndicator(),
          actual.getRequest().getParams().getCardOnFileIndicator());
      assertEquals(providerAccount.getConfiguration().getTransInitiator(),
          actual.getRequest().getParams().getTransInitiator());
      assertEquals(BOOKING_REFERENCE,
          actual.getRequest().getParams().getBookingReference());
    }



}