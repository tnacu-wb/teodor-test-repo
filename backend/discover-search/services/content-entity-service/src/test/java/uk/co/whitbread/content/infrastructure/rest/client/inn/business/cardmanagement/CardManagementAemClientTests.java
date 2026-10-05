package uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_COMMON_ICONS_INFO_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_CARD_MANAGEMENT_INFO_EXCEPTION;


import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.adapter.CardManagementAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.in.CardManagementContentResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.in.CommonIconsResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.out.CardManagementRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.out.CommonIconsRequestAemDto;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class CardManagementAemClientTests {

  @InjectMocks
  private CardManagementAemClient cardManagementAemClient;

  @Mock
  private WebClient webClient;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  @BeforeEach
  public void initWebClient() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
  }

  @Test
  void getCommonIconsInfoShouldReturnOK() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CommonIconsResponseAemDto.class)).thenReturn(getCommonIconsInformation());

    //Act
    final var commonIconsAemResponse =
        cardManagementAemClient.getCommonIconsInformation(createCommonIconsRequest());

    //Assert
    assertThat(commonIconsAemResponse, notNullValue());
  }

  @Test
  void getCardManagementInfoShouldReturnOK() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CardManagementContentResponseAemDto.class)).thenReturn(getCardManagementInformation());

    //Act
    final var cardManagementAemResponse =
        cardManagementAemClient.getCardManagementContentInformation(createCardManagementRequest());

    //Assert
    assertThat(cardManagementAemResponse, notNullValue());
  }

  @Test
  void getCommonIconsInfoShouldReturnException() {
    //Arrange
    CommonIconsRequestAemDto commonIconsRequestAemDto = createCommonIconsRequest();
    var exception = new AemResponseException(
        AEM_COMMON_ICONS_INFO_EXCEPTION,
        "Unable to get InnBusiness common icons information.",
        new Exception());
    when(responseSpec.onStatus(any(), any())).thenThrow(exception);

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () -> cardManagementAemClient.getCommonIconsInformation(commonIconsRequestAemDto));

    //Assert
    assertThat(actual, notNullValue());

    assertThat(actual.getGlobalErrTextTemplate(),
        is(AEM_COMMON_ICONS_INFO_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("Unable to get InnBusiness common icons information."));
    assertThat(actual.getErrorCode(), is(AEM_COMMON_ICONS_INFO_EXCEPTION.getCode()));
  }

  @Test
  void getCardManagementInfoShouldReturnException() {
    //Arrange
    CardManagementRequestAemDto cardManagementRequestAemDto = createCardManagementRequest();
    var exception = new AemResponseException(
        AEM_CARD_MANAGEMENT_INFO_EXCEPTION,
        "Unable to get InnBusiness card management content information.",
        new Exception());
    when(responseSpec.onStatus(any(), any())).thenThrow(exception);

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () -> cardManagementAemClient.getCardManagementContentInformation(cardManagementRequestAemDto));

    //Assert
    assertThat(actual, notNullValue());

    assertThat(actual.getGlobalErrTextTemplate(),
        is(AEM_CARD_MANAGEMENT_INFO_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("Unable to get InnBusiness card management content information."));
    assertThat(actual.getErrorCode(), is(AEM_CARD_MANAGEMENT_INFO_EXCEPTION.getCode()));

  }

  private CardManagementRequestAemDto createCardManagementRequest() {
    return CardManagementRequestAemDto.builder()
        .language("en")
        .build();
  }

  private Mono<CardManagementContentResponseAemDto> getCardManagementInformation() {
    var response = CardManagementContentResponseAemDto.builder()
        .cardStatusOptionsExpired("Expired")
        .filtersMyCards("Only my cards")
        .applyBannerImage("/content/dam/pi/websites/desktop/Business/innbusiness/InnBusiness-card.png")
        .cardManagementTitle("Card management")
        .linkAccountBannerLinkAccountButton("Link to an existing account")
        .applyBannerSubtitle("The new, hassle-free payment solution that makes travel a breeze.")
        .newCardLabel("Add a new card")
        .linkAccountBannerSubtitle("If you already have an InnBusiness Pay account that you access through MMA you can tether it so you can view and manage it within InnBusiness")
        .expenseBoxTitle("Expense management")
        .cardStatusOptionsActivate("Activate")
        .cardStatusOptionsCancelled("Cancelled")
        .filtersShow("Show")
        .columnsYourCard("Your card")
        .newCardIcon("/content/dam/global/icons/common/payment-card-add-white.svg")
        .cardStatusOptionsDispatching("Dispatching")
        .applyBannerLinkAccountButton("Link an existing account")
        .title("Card management")
        .columnsCardHolderName("Card holder")
        .applyBannerApplyNowButton("Apply now")
        .tabsCentrallyStored("Centrally stored")
        .columnsCardExpiry("Expiry")
        .badgeAccountHolder("Account holder")
        .applyBannerTitle("Work travel just got easier with InnBusiness Pay")
        .tabsInnBusinessPay("InnBusiness Pay")
        .invoicesBoxTitle("Consolidated invoices")
        .tabsCentrallyStoredDescription("Save your Premier Inn Business Account Card or company cards and allow employees to use assigned cards to make payments. You can save an unlimited number of cards.")
        .downloadLabel("Download")
        .invoicesBoxSubtitle("Receive a single consolidated VAT invoice to summarise your company spend.")
        .columnsCardId("Card ID")
        .columnsCardStatus("Card status")
        .columnsEdit("Edit")
        .columnsCardNumber("Card no.")
        .invoicesBoxIcon("/")
        .columnsCardHolderRegistered("Card holder reg.")
        .expenseBoxSubtitle("Pre-authorise employee stays and allowances so they don’t need to pay on arrival.")
        .creditBoxSubtitle("Utilise interest-free credit for up to six weeks.")
        .cardHolderOptionsResendCode("Resend code")
        .filtersCancelledCards("Cancelled cards")
        .cardStatusOptionsActive("Active")
        .creditBoxIcon("/content/dam/pi/websites/desktop/Business/innbusiness/icons/cardMgmt-box-credit.svg")
        .downloadIcon("/content/dam/global/icons/common/download-purple.svg")
        .creditBoxTitle("Interest-free credit")
        .cardHolderOptionsRegistered("Registered")
        .linkAccountBannerTitle("Already have an InnBusiness Pay account?")
        .expenseBoxIcon("/content/dam/pi/websites/desktop/Business/innbusiness/icons/cardMgmt-box-expense.svg")
        .cardManagementInfo("...")
        .build();

    return Mono.just(response);
  }

  private CommonIconsRequestAemDto createCommonIconsRequest() {
    return CommonIconsRequestAemDto.builder()
        .language("en")
        .build();
  }

  private Mono<CommonIconsResponseAemDto> getCommonIconsInformation() {
    var response = CommonIconsResponseAemDto.builder()
        .paymentVisa("/content/dam/global/icons/payments/visa.svg")
        .chevronUp("/content/dam/global/icons/common/chevron-up.svg")
        .notificationError("/content/dam/global/icons/notifications/error.svg")
        .chevronRight("/content/dam/global/icons/common/chevron-right.svg")
        .paymentPiba("/content/dam/global/icons/payments/piba.svg")
        .paymentAmex("/content/dam/global/icons/payments/amex.svg")
        .paymentPibaEuro("/content/dam/global/icons/payments/piba-euro.svg")
        .notificationInfo("/content/dam/global/icons/notifications/info.svg")
        .notificationQuestion("/content/dam/global/icons/notifications/question.svg")
        .chevronLeft("/content/dam/global/icons/common/chevron-left.svg")
        .paymentMastercard("/content/dam/global/icons/payments/mastercard.svg")
        .notificationSuccess("/content/dam/global/icons/notifications/success.svg")
        .chevronDown("/content/dam/global/icons/common/chevron-down.svg")
        .notificationAlert("/content/dam/global/icons/notifications/alert.svg")
        .chevronUpPurple("/content/dam/global/icons/common/chevron-up-purple.svg")
        .chevronRightPurple("/content/dam/global/icons/common/chevron-right-purple.svg")
        .chevronDownPurple("/content/dam/global/icons/common/chevron-down-purple.svg")
        .chevronLeftPurple("/content/dam/global/icons/common/chevron-left-purple.svg")
        .arrowUp("/content/dam/global/icons/common/arrow-up.svg")
        .arrowRight("/content/dam/global/icons/common/arrow-right.svg")
        .arrowDown("/content/dam/global/icons/common/arrow-down.svg")
        .arrowLeft("/content/dam/global/icons/common/arrow-left.svg")
        .build();
    return Mono.just(response);
  }

}
