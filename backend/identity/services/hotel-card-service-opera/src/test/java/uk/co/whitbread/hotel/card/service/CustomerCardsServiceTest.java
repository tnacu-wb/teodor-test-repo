package uk.co.whitbread.hotel.card.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.getunleash.FakeUnleash;
import io.getunleash.Unleash;
import io.getunleash.UnleashContext;
import io.getunleash.UnleashContextProvider;
import io.getunleash.Variant;
import io.getunleash.variant.Payload;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.card.client.account.model.AccessLevel;
import uk.co.whitbread.hotel.card.client.payment.Payment3CP;
import uk.co.whitbread.hotel.card.exceptions.ThreeCPClientException;
import uk.co.whitbread.hotel.card.exceptions.UnauthorizedSaveCentralCardException;
import uk.co.whitbread.hotel.card.exceptions.UnknownStrategyException;
import uk.co.whitbread.hotel.card.generated.models.payments.AddressDto;
import uk.co.whitbread.hotel.card.generated.models.payments.AuthorizeScaRequestDto;
import uk.co.whitbread.hotel.card.generated.models.payments.PaymentResponseDto;
import uk.co.whitbread.hotel.card.generated.models.payments.ProviderResponseDto;
import uk.co.whitbread.hotel.card.generated.models.payments.SaveCardRequestDto;
import uk.co.whitbread.hotel.card.generated.models.payments.ThreeCResponseDto;
import uk.co.whitbread.hotel.card.mapper.AuthorizeScaMapper;
import uk.co.whitbread.hotel.card.mapper.AuthorizeScaMapperImpl;
import uk.co.whitbread.hotel.card.mapper.SaveCardMapper;
import uk.co.whitbread.hotel.card.mapper.SaveCardMapperImpl;
import uk.co.whitbread.hotel.card.model.Address;
import uk.co.whitbread.hotel.card.model.AuthorizeScaRequest;
import uk.co.whitbread.hotel.card.model.CardType;
import uk.co.whitbread.hotel.card.model.PaymentCardCommon;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;
import uk.co.whitbread.hotel.card.model.SaveCardDetails;
import uk.co.whitbread.hotel.card.model.SaveCardPurpose;
import uk.co.whitbread.hotel.card.model.SaveCardRequest;
import uk.co.whitbread.hotel.card.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.card.model.feature.FeatureFlag.Feature;
import uk.co.whitbread.hotel.card.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.card.service.cards.BBCentralCardService;
import uk.co.whitbread.hotel.card.service.cards.PaymentCardContext;
import uk.co.whitbread.hotel.card.service.cards.PaymentCardStrategy;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;
import uk.co.whitbread.shared.auth.service.TokenService;

@ExtendWith(MockitoExtension.class)
class CustomerCardsServiceTest {

  @InjectMocks
  private CustomerCardsService customerCardsService;
  @Mock
  private Payment3CP payment3CP;
  @Mock
  private TokenService authTokenService;
  @Mock
  private PaymentCardContext paymentCardContext;
  @Mock
  private BBCentralCardService bbCentralCardService;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private UnleashContextProvider unleashContextProvider;
  @Spy
  private SaveCardMapper saveCardMapper = new SaveCardMapperImpl();
  @Spy
  private AuthorizeScaMapper authorizeScaMapper = new AuthorizeScaMapperImpl();
  private CustomerCardsService spyCustomerCardsService;
  @Mock
  private Unleash unleash;
  @Mock
  private AuthenticatedUserService authenticatedUserService;
  @Mock
  private CustomJwtAuthenticationToken customJwtAuthenticationToken;

  @Test
  void successfulResponse_noneCardType() {
    // Arrange
    var saveCardDetails = newSaveCardDetails();
    saveCardDetails.getCardDetails().setCardType(CardType.NONE);
    var authorizationToken = "token";
    var cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId("employeeAccountId")
            .companyAccountId("companyAccountId")
            .build();

    doNothing().when(bbCentralCardService).updatePaymentCard(any());
    when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorizationToken)).thenReturn(
        cdhEmployeeDetails);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(customJwtAuthenticationToken);
    when(customJwtAuthenticationToken.getAccount()).thenReturn(mock(Account.class));
    when(customJwtAuthenticationToken.getAccount().getAccessLevel()).thenReturn(
        AccessLevel.SUPER.name());

    // Act
    var initiateSaveResponse = customerCardsService.initiateSave(saveCardDetails, authorizationToken);

    // Assert
    Assertions.assertNotNull(initiateSaveResponse);

    // Verify interactions
    verify(authTokenService).retrieveCdhEmployeeDetailsAndVerifyToken(authorizationToken);
  }

  @Test
  void successfulResponse_business() {
    // Arrange
    var saveCardDetails = newSaveCardDetails();
    saveCardDetails.getCardDetails().setBusiness(true);
    var expectedSaveCardRequest = newSaveCardRequestDto();
    expectedSaveCardRequest.email(null);
    expectedSaveCardRequest.accountId(null);
    expectedSaveCardRequest.country("gb");
    var authorizationToken = "token";
    var cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .employeeAccountId("employeeAccountId")
        .companyAccountId("companyAccountId")
        .build();

    when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorizationToken)).thenReturn(
        cdhEmployeeDetails);
    when(payment3CP.initiateSaveCard(expectedSaveCardRequest)).thenReturn(newPaymentResponseDto());
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(customJwtAuthenticationToken);
    when(customJwtAuthenticationToken.getAccount()).thenReturn(mock(Account.class));
    when(customJwtAuthenticationToken.getAccount().getAccessLevel()).thenReturn(
        AccessLevel.SUPER.name());

    // Act
    var initiateSaveResponse = customerCardsService.initiateSave(saveCardDetails, authorizationToken);

    // Assert
    Assertions.assertEquals("iPageHtml", initiateSaveResponse.getPaymentRedirect());
    Assertions.assertEquals("sessionId", initiateSaveResponse.getSessionId());
    Assertions.assertEquals("template", initiateSaveResponse.getTemplate());
    Assertions.assertEquals("providerUrl", initiateSaveResponse.getProviderUrl());

    // Verify interactions
    verify(authTokenService).retrieveCdhEmployeeDetailsAndVerifyToken(authorizationToken);
    verify(payment3CP).initiateSaveCard(any());
  }

  @Test
  void initiateSave_throwsUnauthorizedSaveCentralCardException_whenNotSuperAndNotPersonalCard() {
    // Arrange
    var saveCardDetails = newSaveCardDetails();
    saveCardDetails.getCardDetails().setBusiness(true);
    saveCardDetails.getCardDetails().setPersonalCard(false);
    var authorizationToken = "token";

    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(customJwtAuthenticationToken);
    when(customJwtAuthenticationToken.getAccount()).thenReturn(mock(Account.class));
    when(customJwtAuthenticationToken.getAccount().getAccessLevel()).thenReturn(
        AccessLevel.STAYER.name());

    // Act & Assert
    assertThrows(UnauthorizedSaveCentralCardException.class, () ->
        customerCardsService.initiateSave(saveCardDetails, authorizationToken)
    );
  }

  @Test
  void successfulResponse_customer() {
    // Arrange
    var expectedSaveCardRequest = newSaveCardRequestDto();
    expectedSaveCardRequest.employeeAccountId(null);
    expectedSaveCardRequest.companyAccountId(null);
    expectedSaveCardRequest.business(false);
    expectedSaveCardRequest.cardId(null);
    expectedSaveCardRequest.country("gb");
    var saveCardDetails = newSaveCardDetails();
    saveCardDetails.getCardDetails().setBusiness(false);
    saveCardDetails.getCardDetails().setCardId(null);
    var authorizationToken = "token";

    when(authTokenService.retrieveCustomerAccountIdAndVerifyToken(authorizationToken)).thenReturn(Optional.of("accountId"));
    when(authTokenService.retrieveEmailAndVerifyToken(authorizationToken)).thenReturn(Optional.of("email"));
    when(payment3CP.initiateSaveCard(expectedSaveCardRequest)).thenReturn(newPaymentResponseDto());

    // Act
    var initiateSaveResponse = customerCardsService.initiateSave(saveCardDetails, authorizationToken);

    // Assert
    Assertions.assertEquals("iPageHtml", initiateSaveResponse.getPaymentRedirect());
    Assertions.assertEquals("sessionId", initiateSaveResponse.getSessionId());
    Assertions.assertEquals("template", initiateSaveResponse.getTemplate());
    Assertions.assertEquals("providerUrl", initiateSaveResponse.getProviderUrl());

    // Verify interactions
    verify(authTokenService).retrieveCustomerAccountIdAndVerifyToken(authorizationToken);
    verify(authTokenService).retrieveEmailAndVerifyToken(authorizationToken);
    verify(payment3CP).initiateSaveCard(any());
  }

  @Test
  void testMapPaymentCard_StrategyNotFound() {
    PaymentCardDTO paymentCard = new PaymentCardDTO();

    assertThrows(UnknownStrategyException.class, () -> customerCardsService.mapPaymentCard(null, paymentCard));
  }

  @Test
  void testMapPaymentCard_ValidPaymentCard() {
    PaymentCardDTO paymentCard = new PaymentCardDTO();
    PaymentCardCommon expectedPaymentCard = new PaymentCardCommon();

    when(paymentCardContext.getStrategy(SaveCardPurpose.PI_PERSONAL)).thenReturn(mock(PaymentCardStrategy.class));
    when(paymentCardContext.getStrategy(SaveCardPurpose.PI_PERSONAL).mapPaymentCard(paymentCard)).thenReturn(expectedPaymentCard);

    PaymentCardCommon result = customerCardsService.mapPaymentCard(SaveCardPurpose.PI_PERSONAL, paymentCard);

    assertEquals(expectedPaymentCard, result);
  }

  @Test
  void testUpdatePaymentCard_StrategyNotFound() {
    PaymentCardCommon paymentCard = new PaymentCardCommon();

    assertThrows(UnknownStrategyException.class, () -> customerCardsService.updatePaymentCard(null, paymentCard));
  }

  @Test
  void testUpdatePaymentCard_ValidPaymentCard() {
    PaymentCardCommon paymentCard = new PaymentCardCommon();
    when(paymentCardContext.getStrategy(SaveCardPurpose.PI_PERSONAL)).thenReturn(mock(PaymentCardStrategy.class));
    mockCheckCDHDelayFeatureFlag(false, false);

    customerCardsService.updatePaymentCard(SaveCardPurpose.PI_PERSONAL, paymentCard);

    verify(paymentCardContext, times(1)).getStrategy(SaveCardPurpose.PI_PERSONAL);
  }

  @Test
  void updatePaymentCard_cdhDelayInvalidVariant() {
    spyCustomerCardsService = spy(customerCardsService);
    PaymentCardCommon paymentCard = new PaymentCardCommon();
    when(paymentCardContext.getStrategy(SaveCardPurpose.PI_PERSONAL)).thenReturn(mock(PaymentCardStrategy.class));
    mockCheckCDHDelayFeatureFlag(true, false);

    spyCustomerCardsService.updatePaymentCard(SaveCardPurpose.PI_PERSONAL, paymentCard);

    verify(spyCustomerCardsService).updatePaymentCard(any(), any());
  }

  @Test
  void updatePaymentCard_cdhDelayValidVariant() {
    spyCustomerCardsService = spy(customerCardsService);
    PaymentCardCommon paymentCard = new PaymentCardCommon();
    when(paymentCardContext.getStrategy(SaveCardPurpose.PI_PERSONAL)).thenReturn(mock(PaymentCardStrategy.class));
    mockCheckCDHDelayFeatureFlag(true, true);

    spyCustomerCardsService.updatePaymentCard(SaveCardPurpose.PI_PERSONAL, paymentCard);

    verify(spyCustomerCardsService).updatePaymentCard(any(), any());
  }


  @Test
  void authorizeSca_success() {
    // Arrange
    var authorizeScaRequest = authorizeScaRequest();
    var expectedSaveCardRequest = authorizeScaRequestDto();

    when(payment3CP.initiateAuthorizeSca(expectedSaveCardRequest)).thenReturn(newPaymentResponseDto());

    // Act
    var initiateSaveResponse = customerCardsService.initiateAuthorizeSca(authorizeScaRequest);

    // Assert
    Assertions.assertEquals("iPageHtml", initiateSaveResponse.getPaymentRedirect());
    Assertions.assertEquals("sessionId", initiateSaveResponse.getSessionId());

    // Verify interactions
    verify(payment3CP).initiateAuthorizeSca(any());
  }

  @Test
  void authorizeSca_nullResponse_throwsException() {
    // Arrange
    var authorizeScaRequest = authorizeScaRequest();
    var authorizeScaRequestDto = authorizeScaRequestDto();

    when(authorizeScaMapper.toDto(authorizeScaRequest)).thenReturn(authorizeScaRequestDto);
    when(payment3CP.initiateAuthorizeSca(authorizeScaRequestDto)).thenReturn(null);

    // Act & Assert
    Assertions.assertThrows(ThreeCPClientException.class,
            () -> customerCardsService.initiateAuthorizeSca(authorizeScaRequest),
            "There is no 3CP response"
    );
  }

  @Test
  void authorizeSca_missingThreeCResponse_throwsException() {
    // Arrange
    var authorizeScaRequest = authorizeScaRequest();
    var authorizeScaRequestDto = authorizeScaRequestDto();
    var paymentResponseDto = new PaymentResponseDto();

    when(authorizeScaMapper.toDto(authorizeScaRequest)).thenReturn(authorizeScaRequestDto);
    when(payment3CP.initiateAuthorizeSca(authorizeScaRequestDto)).thenReturn(paymentResponseDto);

    // Act & Assert
    Assertions.assertThrows(ThreeCPClientException.class,
            () -> customerCardsService.initiateAuthorizeSca(authorizeScaRequest),
            "There is no 3CP response"
    );
  }

  private PaymentResponseDto newPaymentResponseDto() {
    var threeCResponseDto = new ThreeCResponseDto();
    threeCResponseDto.setiPageHtml("iPageHtml");
    threeCResponseDto.setSessionId("sessionId");
    threeCResponseDto.setTemplate("template");
    threeCResponseDto.setProviderUrl("providerUrl");
    var providerResponse = new ProviderResponseDto();
    providerResponse.setThreecResponse(threeCResponseDto);
    var paymentResponse = new PaymentResponseDto();
    paymentResponse.setProviderResponse(providerResponse);
    return paymentResponse;
  }

  private SaveCardRequest newSaveCardDetails() {
    Address billingAddress = new Address();
    billingAddress.setCountryCode("GB");
    billingAddress.setCompanyName("Whitbread");
    billingAddress.setLine1("123 Main St");
    billingAddress.setLine2("line2");
    billingAddress.setLine3("line3");
    billingAddress.setLine4("line4");
    billingAddress.setLine5("line5");
    billingAddress.setPostCode("E1 6AN");
    billingAddress.setType("HOME");

    return SaveCardRequest.builder()
        .requestId("requestId")
        .environment("http://localhost")
        .language("en")
        .country("gb")
        .cardDetails(SaveCardDetails.builder()
            .cardType(CardType.CARD)
            .cardId("cardId")
            .cardLabel("cardLabel")
            .memorableWord("memorable")
            .business(true)
            .cnpRequired(false)
            .build())
        .billingAddress(billingAddress)
        .build();
  }

  private SaveCardRequestDto newSaveCardRequestDto() {
    var saveCardRequestDto = new SaveCardRequestDto();
    var billingAddress = new AddressDto();
    billingAddress.setCompanyName("Whitbread");
    billingAddress.setCountryCode("GB");
    billingAddress.setLine1("123 Main St");
    billingAddress.setLine2("line2");
    billingAddress.setLine3("line3");
    billingAddress.setLine4("line4");
    billingAddress.setLine5("line5");
    billingAddress.setPostalCode("E1 6AN");
    billingAddress.setType("HOME");
    saveCardRequestDto.setBillingAddress(billingAddress);
    saveCardRequestDto.setAccountId("accountId");
    saveCardRequestDto.business(true);
    saveCardRequestDto.personalCard(false);
    saveCardRequestDto.setCardId("cardId");
    saveCardRequestDto.setCardLabel("cardLabel");
    saveCardRequestDto.setCardType("CARD");
    saveCardRequestDto.setCnpRequired(false);
    saveCardRequestDto.companyAccountId("companyAccountId");
    saveCardRequestDto.setEmail("email");
    saveCardRequestDto.employeeAccountId("employeeAccountId");
    saveCardRequestDto.setEnvironment("http://localhost");
    saveCardRequestDto.setLanguage("en");
    saveCardRequestDto.setMemorableWord("memorable");
    saveCardRequestDto.setRequestId("requestId");
    return saveCardRequestDto;
  }

  private AuthorizeScaRequest authorizeScaRequest() {
    return AuthorizeScaRequest.builder()
            .requestId("requestId").language("en").environment("http://localhost")
            .country("de").bookingReference("GAA7360661")
            .build();
  }

  private AuthorizeScaRequestDto authorizeScaRequestDto() {
    var authorizeScaRequestDto = new AuthorizeScaRequestDto();
    authorizeScaRequestDto.setRequestId("requestId");
    authorizeScaRequestDto.setEnvironment("http://localhost");
    authorizeScaRequestDto.setLanguage("en");
    authorizeScaRequestDto.country("de");
    authorizeScaRequestDto.bookingReference("GAA7360661");
    return authorizeScaRequestDto;
  }

  private void mockCheckCDHDelayFeatureFlag(boolean isOn, boolean validVariant) {
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getCdhDelay()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(eq(mockedFeatureFlag.getCdhDelay()), any())).thenReturn(isOn);
    when(unleashContextProvider.getContext()).thenReturn(UnleashContext.builder().build());

    if (isOn) {
      when(mockedFeatureFlag.getCdhDelay().getKey()).thenReturn("key");
      if (validVariant) {
        when(unleashWrapper.unleash()).thenReturn(unleash);
        when(unleash.getVariant(any(String.class), any(UnleashContext.class)))
            .thenReturn(new Variant("", new Payload("number", "3000"), true));
      } else {
        when(unleashWrapper.unleash()).thenReturn(new FakeUnleash());
      }
    }
  }
}