package uk.co.whitbread.payments.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.mapper.PaymentMapper;
import uk.co.whitbread.payments.model.EMerchantDetails;
import uk.co.whitbread.payments.model.SaveCardRequest;
import uk.co.whitbread.payments.model.threec.InitialiseRequest;
import uk.co.whitbread.payments.properties.*;
import uk.co.whitbread.payments.service.EMerchantService;
import uk.co.whitbread.payments.util.PaymentRequestFixtures;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.payments.util.TestUtils.getObjectMapper;

@ExtendWith(MockitoExtension.class)
class InitialiseRequestMapperTest {

  private static final String POST_URL_SUCCESS = "testPostUrlSuccess/%s";
  private static final String POST_URL_FAILURE = "testPostUrlFailure/%s";
  private static final String PAYMENT_POST_URL_SUCCESS = "testPostUrlSuccess";
  private static final String PAYMENT_POST_URL_FAILURE = "testPostUrlFailure";
  private static final String TOKENIZED_CARD = "4216333880397891103";
  private static final String NEW_CARD_TEMPLATE = "new-card-template.xml";
  private static final String SAVED_CARD_TEMPLATE = "saved-card-template.xml";
  private static final String NEW_CARD_TRX_OPTIONS = "G";
  private static final String TOKEN_TRX_OPTIONS = "P";
  private static final String TOKEN_INJECTION_ACTION = "T";
  private static final String ANY_PAYMENT_ID = "1234567890D";
  private static final String USERNAME = "WhitbreadTestLondonHotel";
  private static final String COUNTRY_DE = "de";

  @Mock
  private WebhookProperties webhookProperties;
  @Mock
  private RedirectProperties redirectProperties;

  @Mock
  private PaymentRedirectProperties paymentRedirectProperties;
  @Mock
  private EMerchantService eMerchantService;
  @Mock
  private CustomMapper fraudCustomMapper;
  @Mock
  private CardholderStateMapper stateMapper;
  private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

  private InitialiseRequestMapper initialiseRequestMapper;

  @BeforeEach
  void setUp() {
    initialiseRequestMapper = new InitialiseRequestMapper(webhookProperties, redirectProperties, paymentMapper, eMerchantService, fraudCustomMapper, stateMapper, paymentRedirectProperties);

  }

  @Test
  void verifySuccessfulMapping() {
    when(webhookProperties.getSuccess()).thenReturn(POST_URL_SUCCESS);
    when(webhookProperties.getFailure()).thenReturn(POST_URL_FAILURE);
    when(redirectProperties.getSuccess()).thenReturn(POST_URL_SUCCESS);
    when(redirectProperties.getFailure()).thenReturn(POST_URL_FAILURE);
    EMerchantDetails eMerchantDetails = new EMerchantDetails(USERNAME, USERNAME + 1);
    when(eMerchantService.getEMerchantDetails(any(), any())).thenReturn(eMerchantDetails);
    when(stateMapper.getCardholderState(any())).thenReturn("");
    String requestId = UUID.randomUUID().toString();
    var paymentRequest = PaymentRequestFixtures.getPaymentRequest(requestId, "PAY_NOW");
    var expectedInitialiseRequest = PaymentRequestFixtures.getInitialiseRequest(ANY_PAYMENT_ID);
    var result = initialiseRequestMapper.mapInitialiseRequest(paymentRequest, PaymentRequestFixtures.getProviderAccount(),
        ANY_PAYMENT_ID, SAVED_CARD_TEMPLATE);
    assertEquals(expectedInitialiseRequest, result);
  }

  @Test
  void verifySuccessfulMappingNewCardWhenNoToken() {
    when(webhookProperties.getSuccess()).thenReturn(POST_URL_SUCCESS);
    when(webhookProperties.getFailure()).thenReturn(POST_URL_FAILURE);
    when(redirectProperties.getSuccess()).thenReturn(POST_URL_SUCCESS);
    when(redirectProperties.getFailure()).thenReturn(POST_URL_FAILURE);
    EMerchantDetails eMerchantDetails = new EMerchantDetails(USERNAME, USERNAME + 1);
    when(eMerchantService.getEMerchantDetails(any(), any())).thenReturn(eMerchantDetails);
    when(stateMapper.getCardholderState(any())).thenReturn("");
    String requestId = UUID.randomUUID().toString();
    var paymentRequest = PaymentRequestFixtures.getPaymentRequest(requestId, "PAY_NOW");
    paymentRequest.getPayment().setCard(null);

    var result = initialiseRequestMapper.mapInitialiseRequest(paymentRequest, PaymentRequestFixtures.getProviderAccount(),
        ANY_PAYMENT_ID, NEW_CARD_TEMPLATE);

    assertNotNull(result);
    assertEquals(NEW_CARD_TEMPLATE, result.getTemplateId());
    assertEquals(NEW_CARD_TRX_OPTIONS, result.getTrxOptions());
    assertNull(result.getToken());
    assertNull(result.getTokenInjectionAction());
  }

  @Test
  void verifySuccessfulSaveCardMapping() throws IOException {
    when(webhookProperties.getSuccess()).thenReturn(POST_URL_SUCCESS);
    when(webhookProperties.getFailure()).thenReturn(POST_URL_FAILURE);
    when(redirectProperties.getSuccess()).thenReturn(POST_URL_SUCCESS);
    when(redirectProperties.getFailure()).thenReturn(POST_URL_FAILURE);
    EMerchantDetails eMerchantDetails = new EMerchantDetails(USERNAME, USERNAME + 1);
    when(eMerchantService.getEMerchantDetailsByCountry(anyString())).thenReturn(eMerchantDetails);
    var saveCard = getObjectMapper()
        .readValue(new File("src/test/resources/stubs/3c/requests/saveCardRequest.json"), SaveCardRequest.class);
    var result = initialiseRequestMapper.mapInitialiseRequest(saveCard, PaymentRequestFixtures.getProviderAccount(),
        ANY_PAYMENT_ID, NEW_CARD_TEMPLATE);
    assertNotNull(result);
    assertEquals("1234567890D", result.getTrxMerchantReference());
    assertEquals("new-card-template.xml", result.getTemplateId());
  }

  @Test
  void verifySuccessfulMappingForGoogleWalletPayment() {
    when(webhookProperties.getSuccess()).thenReturn(POST_URL_SUCCESS);
    when(webhookProperties.getFailure()).thenReturn(POST_URL_FAILURE);
    when(paymentRedirectProperties.getSuccess()).thenReturn(PAYMENT_POST_URL_SUCCESS);
    when(paymentRedirectProperties.getFailure()).thenReturn(PAYMENT_POST_URL_FAILURE);
    EMerchantDetails eMerchantDetails = new EMerchantDetails(USERNAME, USERNAME + 1);
    when(eMerchantService.getEMerchantDetails(any(), any())).thenReturn(eMerchantDetails);
    when(stateMapper.getCardholderState(any())).thenReturn("");
    String requestId = UUID.randomUUID().toString();
    var paymentRequest = PaymentRequestFixtures.getPaymentRequest(requestId, "PAY_NOW");
    paymentRequest.getPayment().setType("WALLET_GOOGLE");
    paymentRequest.getBooking().setChannel("APPS_ANDROID");

    var expectedInitialiseRequest = PaymentRequestFixtures.getInitialiseRequest(ANY_PAYMENT_ID);
    expectedInitialiseRequest.setWalletType("Digital Wallet");
    expectedInitialiseRequest.setRedirectApproved(PAYMENT_POST_URL_SUCCESS);
    expectedInitialiseRequest.setRedirectDeclined(PAYMENT_POST_URL_FAILURE);

    var result = initialiseRequestMapper.mapInitialiseRequest(paymentRequest, PaymentRequestFixtures.getProviderAccount(),
        ANY_PAYMENT_ID, SAVED_CARD_TEMPLATE);
    assertEquals(expectedInitialiseRequest, result);
  }

  @Test
  void verifyNewCardTemplate() {
    InitialiseRequest initialiseRequest = new InitialiseRequest();
    initialiseRequestMapper.expectNewCard(initialiseRequest, PaymentRequestFixtures.getProviderAccount(), NEW_CARD_TEMPLATE);
    assertEquals(NEW_CARD_TEMPLATE, initialiseRequest.getTemplateId());
    assertEquals(NEW_CARD_TRX_OPTIONS, initialiseRequest.getTrxOptions());
  }

  @Test
  void verifySavedCardTemplate() {
    InitialiseRequest initialiseRequest = new InitialiseRequest();
    initialiseRequestMapper.expectSavedCard(initialiseRequest, TOKENIZED_CARD, PaymentRequestFixtures.getProviderAccount(),
        SAVED_CARD_TEMPLATE);
    assertEquals(SAVED_CARD_TEMPLATE, initialiseRequest.getTemplateId());
    assertEquals(TOKENIZED_CARD, initialiseRequest.getToken());
    assertEquals(TOKEN_INJECTION_ACTION, initialiseRequest.getTokenInjectionAction());
    assertEquals(TOKEN_TRX_OPTIONS, initialiseRequest.getTrxOptions());
  }

  @Test
  void verifyMissingSavedCardTemplateThrowsException() {
    InitialiseRequest initialiseRequest = new InitialiseRequest();
    ProviderAccount account = new ProviderAccount();
    AccountConfigProperties properties = new AccountConfigProperties();
    account.setConfiguration(properties);
    PaymentServiceException thrown = assertThrows(PaymentServiceException.class, () -> initialiseRequestMapper.expectSavedCard(initialiseRequest, TOKENIZED_CARD, account, null));
    assertEquals("Missing iPage template for saved cards", thrown.getReason());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
  }

  @Test
  void verifyBlankSavedCardTemplateThrowsException() {
    InitialiseRequest initialiseRequest = new InitialiseRequest();
    ProviderAccount account = new ProviderAccount();
    AccountConfigProperties properties = new AccountConfigProperties();
    account.setConfiguration(properties);
    PaymentServiceException thrown = assertThrows(PaymentServiceException.class, () -> initialiseRequestMapper.expectSavedCard(initialiseRequest, TOKENIZED_CARD, account, "  "));
    assertEquals("Missing iPage template for saved cards", thrown.getReason());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
  }

  @Test
  void verifyMissingNewCardTemplateThrowsException() {
    InitialiseRequest initialiseRequest = new InitialiseRequest();
    ProviderAccount account = new ProviderAccount();
    AccountConfigProperties properties = new AccountConfigProperties();
    account.setConfiguration(properties);
    PaymentServiceException thrown = assertThrows(PaymentServiceException.class, () -> initialiseRequestMapper.expectNewCard(initialiseRequest, account, null));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
    assertEquals("Missing iPage template for new cards", thrown.getReason());
  }

  @Test
  void verifyBlankNewCardTemplateThrowsException() {
    InitialiseRequest initialiseRequest = new InitialiseRequest();
    ProviderAccount account = new ProviderAccount();
    AccountConfigProperties properties = new AccountConfigProperties();
    account.setConfiguration(properties);
    PaymentServiceException thrown = assertThrows(PaymentServiceException.class, () -> initialiseRequestMapper.expectNewCard(initialiseRequest, account, "  "));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatusCode());
    assertEquals("Missing iPage template for new cards", thrown.getReason());
  }

  @Test
  void verifySuccessfulAuthorizeCardMapping() {
    when(webhookProperties.getSuccess()).thenReturn(POST_URL_SUCCESS);
    when(webhookProperties.getFailure()).thenReturn(POST_URL_FAILURE);
    when(redirectProperties.getSuccess()).thenReturn(POST_URL_SUCCESS);
    when(redirectProperties.getFailure()).thenReturn(POST_URL_FAILURE);
    EMerchantDetails eMerchantDetails = new EMerchantDetails(USERNAME, USERNAME + 1);
    when(eMerchantService.getEMerchantDetailsByCountry(COUNTRY_DE)).thenReturn(eMerchantDetails);
    var result = initialiseRequestMapper.mapInitialiseAuthorizeScaRequest(PaymentRequestFixtures.getProviderAccount(), ANY_PAYMENT_ID, "en", "de");
    assertNotNull(result);
    assertEquals("1234567890D", result.getTrxMerchantReference());
    assertEquals("3dsauthorise", result.getServiceAction());
  }
}