package uk.co.whitbread.payments.converters;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.config.PaypalConfig;
import uk.co.whitbread.payments.config.PaypalConfigData;
import uk.co.whitbread.payments.config.PaypalForwardAPIRequest;
import uk.co.whitbread.payments.config.PaypalForwardAPIRequestConfig;
import uk.co.whitbread.payments.config.PaypalSensitiveData;
import uk.co.whitbread.payments.config.PaypalTestCard;
import uk.co.whitbread.payments.model.Address;
import uk.co.whitbread.payments.model.Amount;
import uk.co.whitbread.payments.model.Billing;
import uk.co.whitbread.payments.model.EMerchantDetails;
import uk.co.whitbread.payments.model.Payment;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionRequest;
import uk.co.whitbread.payments.properties.AccountConfigProperties;
import uk.co.whitbread.payments.properties.ProviderAccount;
import uk.co.whitbread.payments.service.EMerchantService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaypalTransactionRequestMapperTest {
  private static final String MOCK_PAYMENTID = "123456789D";
  private static final String ANY_USERNAME = "username";
  private static final String ANY_PASSWORD = "password";
  private static final String PAYPAL_USER_DATA_4 = "paypal";
  private static final String PAYPAL_CARD_EXPIRY_DATE_MM_YY_PATH = "/body/Request/Params/CardExpiryDateMMYY";

  @InjectMocks
  private PaypalTransactionRequestMapper paypalTransactionRequestMapper;
  @Mock
  private PaypalConfig paypalConfig;
  @Mock
  private CardholderStateMapper stateMapper;
  @Mock
  private CustomMapper customMapper;
  @Mock
  private EMerchantService eMerchantService;
  @Mock
  private PaypalForwardAPIRequestConfig paypalForwardAPIRequestConfig;

  @Test
  void verifyForwardAPIDataMapping() {

    PaymentRequest paymentRequest = getPaymentRequest();

    when(stateMapper.getCardholderState(any())).thenReturn("GB");
    var expected = paypalTransactionRequestMapper.getForwardAPIData(paymentRequest, MOCK_PAYMENTID);

    assertNotNull(expected);
    assertEquals("N/A", expected.getCardholderCity());
  }

  @Test
  void verifySensitiveDataMapping() {

    EMerchantDetails eMerchantDetails = new EMerchantDetails(ANY_USERNAME, ANY_PASSWORD);

    when(paypalConfig.getPaypalSensitiveData()).thenReturn(mockPaypalConfigSensitiveData().getPaypalSensitiveData());

    var expected = paypalTransactionRequestMapper.getSensitiveData(mockForwardApiTransactionRequest(), eMerchantDetails);

    assertNotNull(expected);
    assertEquals("", expected.getMitType());
    assertEquals("", expected.getScaTransRef());
    assertEquals(PAYPAL_USER_DATA_4, expected.getPaypalString());
  }

  @Test
  void populatePaypalRequestWithTestCardEnabled() {
    PaymentRequest paymentRequest = getPaymentRequest();
    ProviderAccount providerAccount = getProviderAccount();
    PaypalSensitiveData paypalSensitiveData = mockPaypalConfigSensitiveData().getPaypalSensitiveData();
    PaypalForwardAPIRequest paypalForwardAPIRequest = getPaypalForwardAPIRequest();

    PaypalTestCard paypalTestCard = getPaypalTestCard();
    PaypalConfigData paypalConfigData = new PaypalConfigData();
    paypalConfigData.setEnablePaypalConfigData(true);
    EMerchantDetails eMerchantDetails = getEMerchantDetails();

    doAnswer(invocation -> {
      PaypalForwardAPITransactionRequest paypalForwardAPITransactionRequest = invocation.getArgument(1);
      paypalForwardAPITransactionRequest.setSensitiveData(mockForwardApiTransactionRequest().getSensitiveData());
      return null;
    }).when(customMapper).mapForPaypal(any(), any(), any());

    when(eMerchantService.getEMerchantDetails(any(), any()))
        .thenReturn(eMerchantDetails);
    when(paypalForwardAPIRequestConfig.getPaypalForwardAPIRequest())
        .thenReturn(List.of(paypalForwardAPIRequest));
    when(paypalConfig.getPaypalTestCard()).thenReturn(paypalTestCard);
    when(paypalConfig.getPaypalConfigData()).thenReturn(paypalConfigData);
    when(paypalConfig.getPaypalSensitiveData()).thenReturn(paypalSensitiveData);

    var expected = paypalTransactionRequestMapper.populatePaypalRequest(
        paymentRequest, providerAccount, UUID.randomUUID().toString());

    assertNotNull(expected);
    assertNull(expected.getName());
    assertEquals("", expected.getUrl());
    assertEquals("", expected.getMethod());
    assertEquals(paypalSensitiveData.getZeroAuthType(), expected.getSensitiveData().getType());
    assertEquals(paypalSensitiveData.getVersion(), expected.getSensitiveData().getVersion());
    assertEquals(paypalSensitiveData.getValidationCodeHash(), expected.getSensitiveData().getValidationCodeHash());
    assertEquals(PAYPAL_CARD_EXPIRY_DATE_MM_YY_PATH, expected.getConfig().getTransformations().get(0).getPath());
  }

  @Test
  void populatePaypalRequestWithTestCardDisabled() {
    PaymentRequest paymentRequest = getPaymentRequest();
    ProviderAccount providerAccount = getProviderAccount();
    PaypalSensitiveData paypalSensitiveData = mockPaypalConfigSensitiveData().getPaypalSensitiveData();
    PaypalForwardAPIRequest paypalForwardAPIRequest = getPaypalForwardAPIRequest();

    PaypalTestCard paypalTestCard = getPaypalTestCard();
    paypalTestCard.setEnablePaypalTestCard(false);
    PaypalConfigData paypalConfigData = new PaypalConfigData();
    paypalConfigData.setEnablePaypalConfigData(true);
    EMerchantDetails eMerchantDetails = getEMerchantDetails();

    doAnswer(invocation -> {
      PaypalForwardAPITransactionRequest paypalForwardAPITransactionRequest = invocation.getArgument(1);
      paypalForwardAPITransactionRequest.setSensitiveData(mockForwardApiTransactionRequest().getSensitiveData());
      return null;
    }).when(customMapper).mapForPaypal(any(), any(), any());

    when(eMerchantService.getEMerchantDetails(any(), any()))
        .thenReturn(eMerchantDetails);
    when(paypalForwardAPIRequestConfig.getPaypalForwardAPIRequest())
        .thenReturn(List.of(paypalForwardAPIRequest));
    when(paypalConfig.getPaypalTestCard()).thenReturn(paypalTestCard);
    when(paypalConfig.getPaypalConfigData()).thenReturn(paypalConfigData);
    when(paypalConfig.getPaypalSensitiveData()).thenReturn(paypalSensitiveData);

    var expected = paypalTransactionRequestMapper.populatePaypalRequest(
        paymentRequest, providerAccount, UUID.randomUUID().toString());

    assertNotNull(expected);
    assertNull(expected.getName());
    assertEquals("", expected.getUrl());
    assertEquals("", expected.getMethod());
    assertEquals(paypalSensitiveData.getZeroAuthType(), expected.getSensitiveData().getType());
    assertEquals(paypalSensitiveData.getVersion(), expected.getSensitiveData().getVersion());
    assertEquals(paypalSensitiveData.getValidationCodeHash(), expected.getSensitiveData().getValidationCodeHash());
    assertEquals(PAYPAL_CARD_EXPIRY_DATE_MM_YY_PATH, expected.getConfig().getTransformations().get(0).getPath());
  }

  @Test
  void populatePaypalRequestWithTestCardDisabledAndPaypalConfigDataDisabled() {
    PaymentRequest paymentRequest = getPaymentRequest();
    ProviderAccount providerAccount = getProviderAccount();
    PaypalSensitiveData paypalSensitiveData = mockPaypalConfigSensitiveData().getPaypalSensitiveData();
    PaypalForwardAPIRequest paypalForwardAPIRequest = getPaypalForwardAPIRequest();

    PaypalTestCard paypalTestCard = getPaypalTestCard();
    paypalTestCard.setEnablePaypalTestCard(false);
    PaypalConfigData paypalConfigData = new PaypalConfigData();
    paypalConfigData.setEnablePaypalConfigData(false);
    EMerchantDetails eMerchantDetails = getEMerchantDetails();

    doAnswer(invocation -> {
      PaypalForwardAPITransactionRequest paypalForwardAPITransactionRequest = invocation.getArgument(1);
      paypalForwardAPITransactionRequest.setSensitiveData(mockForwardApiTransactionRequest().getSensitiveData());
      return null;
    }).when(customMapper).mapForPaypal(any(), any(), any());

    when(eMerchantService.getEMerchantDetails(any(), any()))
        .thenReturn(eMerchantDetails);
    when(paypalForwardAPIRequestConfig.getPaypalForwardAPIRequest())
        .thenReturn(List.of(paypalForwardAPIRequest));
    when(paypalConfig.getPaypalTestCard()).thenReturn(paypalTestCard);
    when(paypalConfig.getPaypalConfigData()).thenReturn(paypalConfigData);
    when(paypalConfig.getPaypalSensitiveData()).thenReturn(paypalSensitiveData);

    var expected = paypalTransactionRequestMapper.populatePaypalRequest(
        paymentRequest, providerAccount, UUID.randomUUID().toString());

    assertNotNull(expected);
    assertEquals("", expected.getName());
    assertEquals("", expected.getUrl());
    assertEquals("", expected.getMethod());
    assertEquals(paypalSensitiveData.getZeroAuthType(), expected.getSensitiveData().getType());
    assertEquals(paypalSensitiveData.getVersion(), expected.getSensitiveData().getVersion());
    assertEquals(paypalSensitiveData.getValidationCodeHash(), expected.getSensitiveData().getValidationCodeHash());
    assertNull(expected.getConfig());
  }

  private PaypalForwardAPIRequest getPaypalForwardAPIRequest() {
    PaypalForwardAPIRequest paypalForwardAPIRequest = new PaypalForwardAPIRequest();
    paypalForwardAPIRequest.setPath(PAYPAL_CARD_EXPIRY_DATE_MM_YY_PATH);
    paypalForwardAPIRequest.setValue(
        Arrays.asList(
            Arrays.asList("0122"),
            Arrays.asList("0223")
        ));
    return paypalForwardAPIRequest;
  }

  private EMerchantDetails getEMerchantDetails() {
    return new EMerchantDetails("WhitbreadTest-LondonHotel", "WhitbreadTestLondonHotel1");
  }


  private PaypalTestCard getPaypalTestCard() {
    PaypalTestCard paypalTestCard = new PaypalTestCard();
    paypalTestCard.setEnablePaypalTestCard(true);
    paypalTestCard.setTestCardHotelCodes(List.of("LondonHotel"));
    paypalTestCard.setTestCardExpiryDateMMYY("0325");
    return paypalTestCard;
  }

  private PaymentRequest getPaymentRequest() {
    PaymentRequest paymentRequest = new PaymentRequest();
    paymentRequest.setPayment(Payment.builder()
        .amount(Amount.builder()
            .currency("EUR")
            .build())
        .billing(Billing.builder()
            .firstName("FirstName")
            .lastName("Surname")
            .address(Address.builder()
                .countryCode("GB")
                .line1("test address")
                .build())
            .build())
        .build());
    return paymentRequest;
  }

  private ProviderAccount getProviderAccount() {
    AccountConfigProperties accountConfigProperties = new AccountConfigProperties();
    accountConfigProperties.setNewCardTemplate("");
    return ProviderAccount.builder().configuration(accountConfigProperties).build();
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
