package uk.co.whitbread.payments.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.mapper.SaveCardMapper;
import uk.co.whitbread.payments.mapper.SaveCardMapperImpl;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.feature.FeatureFlag;
import uk.co.whitbread.payments.model.feature.UnleashWrapper;
import uk.co.whitbread.payments.properties.AccountConfigProperties;
import uk.co.whitbread.payments.properties.ProviderAccount;
import uk.co.whitbread.payments.service.EMerchantService;
import uk.co.whitbread.payments.util.PaymentIdGenerator;
import uk.co.whitbread.payments.util.TestUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentRepositoryTest {

  private static final String BOOKING_REFERENCE = "ANY_BOOKING_REFERENCE";
  private static final String ERROR_MESSAGE = "Payment with paymentId %s not found.";
  private static final String ANY_PAYMENT_ID = "1234567890D";
  @Mock
  DynamoDbAsyncTable<PaymentsSchema> paymentsTable;
  @Mock
  private EMerchantService eMerchantService;
  @Mock
  private PaymentIdGenerator paymentIdGenerator;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  private PaymentRepository paymentRepository;
  @Spy
  private SaveCardMapper saveCardMapper = new SaveCardMapperImpl();

  @BeforeEach
  void setUp() {
    paymentRepository = new PaymentRepository(eMerchantService, paymentIdGenerator, saveCardMapper, paymentsTable, unleashWrapper);
    paymentRepository = Mockito.spy(paymentRepository);
  }

  @Test
  void testGetByPaymentId() {
    var future = CompletableFuture.completedFuture(PaymentsSchema.builder().paymentId(ANY_PAYMENT_ID).build());
    when(paymentsTable.getItem(any(PaymentsSchema.class))).thenReturn(future);
    var paymentSchema = paymentRepository.getByPaymentId(ANY_PAYMENT_ID).block();
    assertEquals(ANY_PAYMENT_ID, paymentSchema.get().getPaymentId());
  }

  @Test
  void testUpdatePaymentResource() {
    var future = CompletableFuture.completedFuture(PaymentsSchema.builder().providerResponse(ProviderResponse.builder().transactionReference("someref").build()).expire(7776000).paymentId(ANY_PAYMENT_ID).build());
    when(paymentsTable.getItem(any(PaymentsSchema.class))).thenReturn(future);
    when(paymentsTable.updateItem(any(PaymentsSchema.class))).thenReturn(future);
    PaymentsSchema paymentsSchema = paymentRepository.updatePaymentResource(ANY_PAYMENT_ID, ProviderResponse.builder().threeCResponse(ThreeCResponse.builder().build()).build()).block();
    assertEquals("someref", paymentsSchema.getProviderResponse().getTransactionReference());
    assertEquals(7776000, paymentsSchema.getExpire());
  }

  @Test
  void testUpdateRefundResource() {
    String requestId = UUID.randomUUID().toString();
    String refundId = paymentIdGenerator.generatePaymentId();
    PaymentsSchema paySchema = PaymentsSchema
        .builder()
        .requestId(requestId)
        .refunded(true)
        .refundedOn(LocalDateTime.now())
        .paymentId(refundId)
        .build();
    var refundResponse = RefundResponse.builder().paymentId(refundId).build();
    var future = CompletableFuture.completedFuture(paySchema);
    when(paymentsTable.getItem(any(PaymentsSchema.class))).thenReturn(future);
    when(paymentsTable.updateItem(any(PaymentsSchema.class))).thenReturn(future);
    PaymentsSchema paymentsSchema = paymentRepository.updateRefundResource(refundResponse).block();
    assertEquals(requestId, paymentsSchema.getRequestId());
    assertEquals(refundId, paymentsSchema.getPaymentId());
    assertEquals(true, paymentsSchema.isRefunded());
    assertNotNull(paymentsSchema.getRefundedOn());
  }

  @Test
  void testUpdatePaymentResourceWithBookingReference() {
    String paymentId = UUID.randomUUID().toString();
    var future = CompletableFuture.completedFuture(PaymentsSchema.builder().paymentId(paymentId).bookingReference(BOOKING_REFERENCE).build());
    when(paymentsTable.getItem(any(PaymentsSchema.class))).thenReturn(future);
    when(paymentsTable.updateItem(any(PaymentsSchema.class))).thenReturn(future);
    PaymentsSchema paymentsSchema = paymentRepository.updatePaymentWithBookingReference(paymentId, BOOKING_REFERENCE).block();
    assertNotNull(paymentsSchema);
    assertEquals(paymentId, paymentsSchema.getPaymentId());
    assertEquals(BOOKING_REFERENCE, paymentsSchema.getBookingReference());
  }

  @Test
  void testUpdatePaymentResource_WhenPaymentIdNotFoundInDynamoDB() {
    String paymentId = UUID.randomUUID().toString();
    when(paymentsTable.getItem(any(PaymentsSchema.class))).thenThrow(new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(ERROR_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND));
    try {
      paymentRepository.updatePaymentWithBookingReference(paymentId, BOOKING_REFERENCE).block();
      fail();
    } catch (Exception e) {
      assertTrue(e instanceof PaymentServiceException);
      var exception = (PaymentServiceException) e;
      assertEquals(ErrorCodes.PAYMENT_NOT_FOUND, exception.getErrorCode());
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
  }

  @Test
  void testCreatePaymentSaveCardResource_WhenNoRecordInDB() throws IOException {
    var saveCard = TestUtils.getObjectMapper().readValue(new File("src/test/resources/stubs/3c/requests/saveCardRequest.json"),
        SaveCardRequest.class);
    var configuration = new AccountConfigProperties();
    configuration.setNewCardTemplate("newCardTemplate");
    var providerAccount = ProviderAccount.builder()
        .configuration(configuration)
        .paymentType(PaymentType.CARD)
        .build();
    var expectedPaymentSchema = PaymentsSchema.builder()
        .requestId("1234567890D")
        .template("newCardTemplate")
        .booking(Booking.builder().language("en").build())
        .payment(Payment.builder().environment("LOCAL").type(PaymentType.CARD.name()).subType(PaymentSubType.SAVE_CARD.name()).build())
        .saveCardDetails(SaveCardDetails.builder()
            .billingAddress(Address.builder()
                .line1("Whitbread Group PLC")
                .line2("Houghton Hall Business Park")
                .line3("")
                .line4("")
                .line5("")
                .countryCode("GB")
                .postalCode("123")
                .companyName("Whitbread")
                .type("BUSINESS")
                .build())
            .business(true)
            .cardId("1")
            .cardLabel("business card")
            .memorableWord("hello")
            .cnpRequired(false)
            .personalCard(false)
            .environment("LOCAL")
            .email("sasa.radivoi@whitbread.com")
            .accountId("account-id-1")
            .companyAccountId("company-id-1")
            .employeeAccountId("employee-id-1")
            .language("en")
            .build())
        .build();
    var argumentCaptor = ArgumentCaptor.forClass(PaymentsSchema.class);
    doReturn(Mono.just(Optional.empty())).when(paymentRepository).getByRequestId(any());
    when(paymentsTable.putItem(any(PaymentsSchema.class)))
        .thenReturn(CompletableFuture.completedFuture(null));
    when(paymentsTable.getItem(any(PaymentsSchema.class))).thenReturn(CompletableFuture.completedFuture(PaymentsSchema.builder().build()));
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePibaCnpIframeSplit())).thenReturn(false);
    paymentRepository.createPaymentSaveCardResource(saveCard, providerAccount).block();

    verify(paymentsTable).putItem(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue())
        .usingRecursiveComparison()
        .ignoringFields("createdOn", "refundedOn", "paymentId", "expire")
        .isEqualTo(expectedPaymentSchema);
    verifyNoMoreInteractions(paymentsTable);
  }

  @Test
  void testUpdatePaymentResource_WhenPaymentIdIsNull() {
    String paymentId = null;
    when(paymentsTable.getItem(any(PaymentsSchema.class))).thenThrow(new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(ERROR_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND));
    try {
      paymentRepository.updatePaymentWithBookingReference(paymentId, BOOKING_REFERENCE).block();
      fail();
    } catch (Exception e) {
      assertTrue(e instanceof PaymentServiceException);
      var exception = (PaymentServiceException) e;
      assertEquals(ErrorCodes.PAYMENT_NOT_FOUND, exception.getErrorCode());
      assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
  }

  @Test
  void testCreateAuthorizeScaResource_WhenNoRecordInDB() {
    var requestId = UUID.randomUUID().toString();
    var authorizeSca = AuthorizeScaRequest.builder()
            .requestId(requestId)
            .environment("LOCAL")
            .language("en").build();
    var configuration = new AccountConfigProperties();
    configuration.setNewCardTemplate("newCardTemplate");
    var providerAccount = ProviderAccount.builder()
            .configuration(configuration)
            .paymentType(PaymentType.CARD)
            .build();
    var expectedPaymentSchema = PaymentsSchema.builder()
            .requestId(requestId)
            .template("newCardTemplate")
            .booking(Booking.builder().language("en").build())
            .payment(Payment.builder().environment("LOCAL").type(PaymentType.CARD.name()).subType(PaymentSubType.AUTHORIZE_CARD.name()).build())
            .build();
    var argumentCaptor = ArgumentCaptor.forClass(PaymentsSchema.class);
    doReturn(Mono.just(Optional.empty())).when(paymentRepository).getByRequestId(any());
    when(paymentsTable.putItem(any(PaymentsSchema.class)))
            .thenReturn(CompletableFuture.completedFuture(null));
    when(paymentsTable.getItem(any(PaymentsSchema.class))).thenReturn(CompletableFuture.completedFuture(PaymentsSchema.builder().build()));
    paymentRepository.createAuthorizeScaResource(authorizeSca, providerAccount).block();

    verify(paymentsTable).putItem(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue())
            .usingRecursiveComparison()
            .ignoringFields("createdOn", "refundedOn", "expire")
            .isEqualTo(expectedPaymentSchema);
    verifyNoMoreInteractions(paymentsTable);
  }

  @Test
  void testCreatePaymentResource_WhenNoRecordInDB() throws IOException {
    var paymentRequest = TestUtils.getObjectMapper().readValue(
        new File("src/test/resources/stubs/3c/requests/createPaymentRequest.json"),
        PaymentRequest.class
    );
    var configuration = new AccountConfigProperties();
    configuration.setNewCardTemplate("newCardTemplate");
    var providerAccount = ProviderAccount.builder()
        .configuration(configuration)
        .paymentType(PaymentType.CARD)
        .build();

    var eMerchantDetails = new EMerchantDetails("testAccount", "testPassword");

    when(eMerchantService.getEMerchantDetails(anyString(), anyString()))
        .thenReturn(eMerchantDetails);
    doReturn(Mono.just(Optional.empty())).when(paymentRepository).getByRequestId(any());
    when(paymentIdGenerator.generatePaymentId()).thenReturn("1234567890D");
    when(paymentsTable.putItem(any(PaymentsSchema.class)))
        .thenReturn(CompletableFuture.completedFuture(null));
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePibaCnpIframeSplit())).thenReturn(false);

    var result = paymentRepository.createPaymentResource(paymentRequest, providerAccount).block();

    assertNotNull(result);
    assertEquals("1234567890D", result.getPaymentId());
    assertEquals(paymentRequest.getRequestId(), result.getRequestId());
    verify(paymentsTable).putItem(any(PaymentsSchema.class));
  }

  @Test
  void testCreatePaymentResource_WhenNoRecordInDBAndEmptyName() throws IOException {
    var paymentRequest = TestUtils.getObjectMapper().readValue(
        new File("src/test/resources/stubs/3c/requests/createPaymentRequest.json"),
        PaymentRequest.class
    );

    paymentRequest.getPayment().getBilling().setFirstName(null);
    paymentRequest.getPayment().getBilling().setLastName(null);

    var configuration = new AccountConfigProperties();
    configuration.setNewCardTemplate("newCardTemplate");
    var providerAccount = ProviderAccount.builder()
        .configuration(configuration)
        .paymentType(PaymentType.CARD)
        .build();

    var eMerchantDetails = new EMerchantDetails("testAccount", "testPassword");

    when(eMerchantService.getEMerchantDetails(anyString(), anyString()))
        .thenReturn(eMerchantDetails);
    doReturn(Mono.just(Optional.empty())).when(paymentRepository).getByRequestId(any());
    when(paymentIdGenerator.generatePaymentId()).thenReturn("1234567890D");
    when(paymentsTable.putItem(any(PaymentsSchema.class)))
        .thenReturn(CompletableFuture.completedFuture(null));
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePibaCnpIframeSplit())).thenReturn(false);

    var result = paymentRepository.createPaymentResource(paymentRequest, providerAccount).block();

    assertNotNull(result);
    assertEquals("1234567890D", result.getPaymentId());
    assertEquals(paymentRequest.getRequestId(), result.getRequestId());
    verify(paymentsTable).putItem(any(PaymentsSchema.class));
  }

  // ==================== resolveTemplate parameterized tests (via createPaymentResource) ====================

  /**
   * Each argument: description, featureEnabled, paymentType, cardPresent, token, newCardCnpTemplate,
   *                savedCardCnpTemplate, expectedTemplate
   *
   * Distinct branches in resolveTemplate:
   *  1. Feature OFF  → standard path (hasToken ? savedCard : newCard)
   *  2. Feature ON + non-PIBA → standard path
   *  3. Feature ON + PIBA + cardPresent → standard path (isPibaCnp == false)
   *  4. Feature ON + PIBA + CNP + no token → newCardCnpTemplate (or fallback)
   *  5. Feature ON + PIBA + CNP + has token → savedCardCnpTemplate (or fallback)
   */
  static Stream<Arguments> resolveTemplateArgs() {
    return Stream.of(
        // Standard path — feature disabled
        Arguments.of("Feature OFF, no token → newCardTemplate",
            false, PaymentType.PIBA, false, null, "cnp", "cnpSaved", "newCardTemplate"),
        Arguments.of("Feature OFF, has token → savedCardTemplate",
            false, PaymentType.PIBA, false, "tok", "cnp", "cnpSaved", "savedCardTemplate"),

        // Standard path — feature enabled but not PIBA CNP
        Arguments.of("Feature ON, non-PIBA, no token → newCardTemplate",
            true, PaymentType.CARD, false, null, "cnp", "cnpSaved", "newCardTemplate"),
        Arguments.of("Feature ON, non-PIBA, has token → savedCardTemplate",
            true, PaymentType.CARD, false, "tok", "cnp", "cnpSaved", "savedCardTemplate"),
        Arguments.of("Feature ON, PIBA, card present, no token → newCardTemplate",
            true, PaymentType.PIBA, true, null, "cnp", "cnpSaved", "newCardTemplate"),
        Arguments.of("Feature ON, PIBA, card present, has token → savedCardTemplate",
            true, PaymentType.PIBA, true, "tok", "cnp", "cnpSaved", "savedCardTemplate"),

        // CNP path — feature enabled + PIBA + card not present
        Arguments.of("Feature ON, PIBA CNP, no token → newCardCnpTemplate",
            true, PaymentType.PIBA, false, null, "newCardCnpTemplate", "cnpSaved", "newCardCnpTemplate"),
        Arguments.of("Feature ON, PIBA CNP, has token → savedCardCnpTemplate",
            true, PaymentType.PIBA, false, "tok", "cnp", "savedCardCnpTemplate", "savedCardCnpTemplate"),
        Arguments.of("Feature ON, PIBA_EU CNP, no token → newCardCnpTemplate",
            true, PaymentType.PIBA_EU, false, null, "newCardCnpTemplate", "cnpSaved", "newCardCnpTemplate"),
        Arguments.of("Feature ON, PIBA_EU CNP, has token → savedCardCnpTemplate",
            true, PaymentType.PIBA_EU, false, "tok", "cnp", "savedCardCnpTemplate", "savedCardCnpTemplate"),

        // CNP fallback — blank / null CNP templates fall back to standard
        Arguments.of("Feature ON, PIBA CNP, no token, blank cnp → fallback newCardTemplate",
            true, PaymentType.PIBA, false, null, "  ", "cnpSaved", "newCardTemplate"),
        Arguments.of("Feature ON, PIBA CNP, no token, null cnp → fallback newCardTemplate",
            true, PaymentType.PIBA, false, null, null, "cnpSaved", "newCardTemplate"),
        Arguments.of("Feature ON, PIBA CNP, has token, blank cnp → fallback savedCardTemplate",
            true, PaymentType.PIBA, false, "tok", "cnp", "  ", "savedCardTemplate"),
        Arguments.of("Feature ON, PIBA CNP, has token, null cnp → fallback savedCardTemplate",
            true, PaymentType.PIBA, false, "tok", "cnp", null, "savedCardTemplate"),

        // Edge case — empty string token treated as no token
        Arguments.of("Feature ON, PIBA CNP, empty token → newCardCnpTemplate",
            true, PaymentType.PIBA, false, "", "newCardCnpTemplate", "cnpSaved", "newCardCnpTemplate")
    );
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("resolveTemplateArgs")
  void testResolveTemplate(String description, boolean featureEnabled, PaymentType paymentType,
                           boolean cardPresent, String token, String newCardCnpTemplate,
                           String savedCardCnpTemplate, String expectedTemplate) {
    var paymentBuilder = Payment.builder()
        .type(paymentType.name())
        .subType("ECOMM")
        .environment("LOCAL")
        .isCardPresent(cardPresent)
        .amount(Amount.builder().currency("GBP").minorUnits(1).build());
    if (token != null) {
      paymentBuilder.card(Card.builder().token(token).build());
    }

    var paymentRequest = PaymentRequest.builder()
        .requestId("req-param")
        .payment(paymentBuilder.build())
        .booking(Booking.builder().language("en").reference("REF")
            .businessSite(BusinessSite.builder().identifier("HOTEL").build()).build())
        .build();

    var configuration = buildConfig("newCardTemplate", "savedCardTemplate",
        newCardCnpTemplate, savedCardCnpTemplate);
    var providerAccount = ProviderAccount.builder()
        .configuration(configuration)
        .paymentType(paymentType)
        .build();

    stubPaymentRequestMocks(featureEnabled);

    var result = paymentRepository.createPaymentResource(paymentRequest, providerAccount).block();

    assertNotNull(result);
    assertEquals(expectedTemplate, result.getTemplate());
  }

  // ==================== getTemplate parameterized tests (SaveCardRequest) ====================

  /**
   * Each argument: description, featureEnabled, paymentType, cnpRequired, newCardCnpTemplate, expectedTemplate
   *
   * Distinct branches in getTemplate(SaveCardRequest):
   *  1. Feature OFF → always newCardTemplate
   *  2. Feature ON + non-PIBA → newCardTemplate
   *  3. Feature ON + PIBA + cnp not required → newCardTemplate
   *  4. Feature ON + PIBA + cnp required → newCardCnpTemplate (or fallback)
   */
  static Stream<Arguments> saveCardTemplateArgs() {
    return Stream.of(
        // Feature disabled — always newCardTemplate regardless of paymentType / cnpRequired
        Arguments.of("Feature OFF, PIBA, cnp required → newCardTemplate",
            false, PaymentType.PIBA, true, "newCardCnpTemplate", "newCardTemplate"),

        // Feature enabled, condition short-circuits
        Arguments.of("Feature ON, non-PIBA, cnp required → newCardTemplate",
            true, PaymentType.CARD, true, "newCardCnpTemplate", "newCardTemplate"),
        Arguments.of("Feature ON, PIBA, cnp NOT required → newCardTemplate",
            true, PaymentType.PIBA, false, "newCardCnpTemplate", "newCardTemplate"),

        // Feature enabled, all conditions met — uses CNP template
        Arguments.of("Feature ON, PIBA, cnp required → newCardCnpTemplate",
            true, PaymentType.PIBA, true, "newCardCnpTemplate", "newCardCnpTemplate"),
        Arguments.of("Feature ON, PIBA_EU, cnp required → newCardCnpTemplate",
            true, PaymentType.PIBA_EU, true, "newCardCnpTemplate", "newCardCnpTemplate"),

        // Fallback — blank / null CNP template
        Arguments.of("Feature ON, PIBA, cnp required, blank cnp → fallback newCardTemplate",
            true, PaymentType.PIBA, true, "  ", "newCardTemplate"),
        Arguments.of("Feature ON, PIBA, cnp required, null cnp → fallback newCardTemplate",
            true, PaymentType.PIBA, true, null, "newCardTemplate")
    );
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("saveCardTemplateArgs")
  void testSaveCardTemplate(String description, boolean featureEnabled, PaymentType paymentType,
                            boolean cnpRequired, String newCardCnpTemplate,
                            String expectedTemplate) throws IOException {
    var saveCard = TestUtils.getObjectMapper().readValue(
        new File("src/test/resources/stubs/3c/requests/saveCardRequest.json"),
        SaveCardRequest.class);
    saveCard.setCnpRequired(cnpRequired);

    var configuration = buildConfig("newCardTemplate", "savedCardTemplate",
        newCardCnpTemplate, "savedCardCnpTemplate");
    var providerAccount = ProviderAccount.builder()
        .configuration(configuration)
        .paymentType(paymentType)
        .build();

    stubSaveCardRequestMocks(featureEnabled);

    var argumentCaptor = ArgumentCaptor.forClass(PaymentsSchema.class);
    paymentRepository.createPaymentSaveCardResource(saveCard, providerAccount).block();

    verify(paymentsTable).putItem(argumentCaptor.capture());
    assertEquals(expectedTemplate, argumentCaptor.getValue().getTemplate());
  }

  // ==================== Helper methods ====================

  private AccountConfigProperties buildConfig(String newCard, String savedCard,
                                              String newCardCnp, String savedCardCnp) {
    var config = new AccountConfigProperties();
    config.setNewCardTemplate(newCard);
    config.setSavedCardTemplate(savedCard);
    config.setNewCardCnpTemplate(newCardCnp);
    config.setSavedCardCnpTemplate(savedCardCnp);
    return config;
  }

  private void stubPaymentRequestMocks(boolean featureEnabled) {
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePibaCnpIframeSplit())).thenReturn(featureEnabled);
    when(eMerchantService.getEMerchantDetails(any(), any()))
        .thenReturn(new EMerchantDetails("acc", "pwd"));
    doReturn(Mono.just(Optional.empty())).when(paymentRepository).getByRequestId(any());
    when(paymentIdGenerator.generatePaymentId()).thenReturn("PAY-PARAM");
    when(paymentsTable.putItem(any(PaymentsSchema.class)))
        .thenReturn(CompletableFuture.completedFuture(null));
  }

  private void stubSaveCardRequestMocks(boolean featureEnabled) {
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePibaCnpIframeSplit())).thenReturn(featureEnabled);
    doReturn(Mono.just(Optional.empty())).when(paymentRepository).getByRequestId(any());
    when(paymentsTable.putItem(any(PaymentsSchema.class)))
        .thenReturn(CompletableFuture.completedFuture(null));
    when(paymentsTable.getItem(any(PaymentsSchema.class)))
        .thenReturn(CompletableFuture.completedFuture(PaymentsSchema.builder().build()));
  }

}