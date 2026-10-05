package uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_COMMON_ICONS_INFO_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_CARD_MANAGEMENT_INFO_EXCEPTION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.in.CardManagementRequest;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.in.CommonIconsRequest;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.ApplyBanner;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.Arrow;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.Badge;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CardHolder;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CardHolderOptions;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CardManagement;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CardManagementContent;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CardStatus;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CardStatusOptions;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CentrallyStored;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.Chevron;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.Columns;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CommonIcons;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CreditBox;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.Download;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.ExpenseBox;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.Filters;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.InvoicesBox;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.LinkAccountBanner;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.NewCard;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.Notification;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.Payment;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.Tabs;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.adapter.CardManagementAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper.CardManagementRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper.CardManagementResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper.CommonIconsRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper.CommonIconsResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.in.CardManagementContentResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.in.CommonIconsResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.out.CardManagementRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.out.CommonIconsRequestAemDto;

@ExtendWith(MockitoExtension.class)
class CardManagementOutPortImplTests {

  @Mock
  private CardManagementAemClient aemClient;

  @Mock
  private CommonIconsRequestMapper commonIconsRequestMapper;

  @Mock
  private CommonIconsResponseMapper commonIconsResponseMapper;

  @Mock
  private CardManagementRequestMapper cardManagementRequestMapper;

  @Mock
  private CardManagementResponseMapper cardManagementResponseMapper;

  @InjectMocks
  private CardManagementOutPortImpl cardManagementOutPort;

  Exception exception = new Exception();

  @Test
  void getCommonIconsShouldReturnException() {
    //Arrange
    var expectedMessage = "Unable to get InnBusiness common icons information.";
    when(commonIconsRequestMapper.toDto(any())).thenReturn(new CommonIconsRequestAemDto());
    when(aemClient.getCommonIconsInformation(any())).thenThrow(
        new AemResponseException(AEM_COMMON_ICONS_INFO_EXCEPTION, expectedMessage, exception));
    var commonIconsRequest = CommonIconsRequest.builder()
        .language("en")
        .build();

    //Act
    var actual = assertThrows(AemResponseException.class,
        () -> cardManagementOutPort.getCommonIconsInfo(commonIconsRequest));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(AEM_COMMON_ICONS_INFO_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(AEM_COMMON_ICONS_INFO_EXCEPTION.getCode()));
  }

  @Test
  void getCommonIconsShouldReturnOk() {
    //Arrange
    when(aemClient.getCommonIconsInformation(any(CommonIconsRequestAemDto.class)))
        .thenReturn(getCommonIconsAemInformation());
    when(commonIconsRequestMapper.toDto(any())).thenReturn(new CommonIconsRequestAemDto());
    when(commonIconsResponseMapper.toModel(any())).thenReturn(mockCommonIconsInformation());

    //Act
    var commonIconsResponse = cardManagementOutPort.getCommonIconsInfo(getCommonIconsRequestEn());

    //Assert
    assertThat(commonIconsResponse, notNullValue());
  }

  @Test
  void getCardManagementContentShouldReturnException() {
    //Arrange
    var expectedMessage = "Unable to get InnBusiness card management content information.";
    when(cardManagementRequestMapper.toDto(any())).thenReturn(new CardManagementRequestAemDto());
    when(aemClient.getCommonIconsInformation(any(CommonIconsRequestAemDto.class)))
        .thenReturn(getCommonIconsAemInformation());
    when(commonIconsRequestMapper.toDto(any())).thenReturn(new CommonIconsRequestAemDto());
    when(commonIconsResponseMapper.toModel(any())).thenReturn(mockCommonIconsInformation());
    when(aemClient.getCardManagementContentInformation(any())).thenThrow(
        new AemResponseException(AEM_CARD_MANAGEMENT_INFO_EXCEPTION, expectedMessage, exception));
    var cardManagementRequest = CardManagementRequest.builder()
        .language("en")
        .build();

    //Act
    var actual = assertThrows(AemResponseException.class,
        () -> cardManagementOutPort.getCardManagementInfo(cardManagementRequest));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(AEM_CARD_MANAGEMENT_INFO_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(AEM_CARD_MANAGEMENT_INFO_EXCEPTION.getCode()));
  }

  @Test
  void getCardManagementShouldReturnOk() {
    //Arrange
    when(aemClient.getCardManagementContentInformation(any(CardManagementRequestAemDto.class)))
        .thenReturn(getCardManagementAemInformation());
    when(cardManagementRequestMapper.toDto(any())).thenReturn(new CardManagementRequestAemDto());
    when(cardManagementResponseMapper.toModel(any())).thenReturn(mockCardManagementInformation());
    when(aemClient.getCommonIconsInformation(any(CommonIconsRequestAemDto.class)))
        .thenReturn(getCommonIconsAemInformation());
    when(commonIconsRequestMapper.toDto(any())).thenReturn(new CommonIconsRequestAemDto());
    when(commonIconsResponseMapper.toModel(any())).thenReturn(mockCommonIconsInformation());

    //Act
    var cardManagementResponse = cardManagementOutPort.getCardManagementInfo(getCardManagementRequestEn());

    //Assert
    assertThat(cardManagementResponse, notNullValue());
    assertEquals("/content/dam/global/icons/payments/visa.svg", cardManagementResponse.getCommonIcons().getPayment().getVisa());
    assertEquals("/content/dam/global/icons/common/chevron-up.svg", cardManagementResponse.getCommonIcons().getChevron().getUp());
    assertEquals("/content/dam/global/icons/notifications/error.svg", cardManagementResponse.getCommonIcons().getNotification().getError());
    assertEquals("/content/dam/global/icons/common/arrow-down.svg", cardManagementResponse.getCommonIcons().getArrow().getDown());
    assertEquals("/content/dam/global/icons/common/chevron-down-purple.svg", cardManagementResponse.getCommonIcons().getChevron().getDownPurple());

    assertEquals("Card management", cardManagementResponse.getCardManagementContent().getTitle());
    assertEquals("Centrally stored", cardManagementResponse.getCardManagementContent().getTabs().getCentrallyStoredTitle());
    assertEquals("Link an existing account", cardManagementResponse.getCardManagementContent().getApplyBanner().getLinkAccountButton());
    assertEquals("Interest-free credit", cardManagementResponse.getCardManagementContent().getCreditBox().getTitle());
    assertEquals("Expense management", cardManagementResponse.getCardManagementContent().getExpenseBox().getTitle());
    assertEquals("Consolidated invoices", cardManagementResponse.getCardManagementContent().getInvoicesBox().getTitle());
    assertEquals("Already have an InnBusiness Pay account?", cardManagementResponse.getCardManagementContent().getLinkAccountBanner().getTitle());
    assertEquals("Card management", cardManagementResponse.getCardManagementContent().getCardManagement().getTitle());
    assertEquals("Only my cards", cardManagementResponse.getCardManagementContent().getFilters().getMyCards());
    assertEquals("Your card", cardManagementResponse.getCardManagementContent().getColumns().getYourCard());
    assertEquals("Resend code", cardManagementResponse.getCardManagementContent().getCardHolder().getCardHolderOptions().getResendCode());
    assertEquals("Activate", cardManagementResponse.getCardManagementContent().getCardStatus().getCardStatusOptions().getActivate());
    assertEquals("Download", cardManagementResponse.getCardManagementContent().getDownload().getLabel());
    assertEquals("Add a new card", cardManagementResponse.getCardManagementContent().getNewCard().getLabel());
    assertEquals("Account holder", cardManagementResponse.getCardManagementContent().getBadge().getAccountHolder());
    assertEquals("Expired", cardManagementResponse.getCardManagementContent().getCardStatus().getCardStatusOptions().getExpired());

    verify(commonIconsResponseMapper).toModel(any(CommonIconsResponseAemDto.class));
  }

  private CardManagementRequest getCardManagementRequestEn() {
    return CardManagementRequest.builder()
        .language("en")
        .build();
  }

  private CardManagementContent mockCardManagementInformation() {
    return CardManagementContent.builder()
        .title("Card management")
        .tabs(Tabs.builder()
            .centrallyStoredTitle("Centrally stored")
            .centrallyStored(CentrallyStored.builder()
                .description("Save your Premier Inn Business Account Card or company cards and allow employees to use assigned cards to make payments. You can save an unlimited number of cards.")
                .build())
            .innBusinessPay("InnBusiness Pay")
            .build())
        .applyBanner(ApplyBanner.builder()
            .title("Work travel just got easier with InnBusiness Pay")
            .subtitle("The new, hassle-free payment solution that makes travel a breeze.")
            .applyNowButton("Apply now")
            .linkAccountButton("Link an existing account")
            .image("/content/dam/pi/websites/desktop/Business/innbusiness/InnBusiness-card.png")
            .build())
        .creditBox(CreditBox.builder()
            .icon( "/content/dam/pi/websites/desktop/Business/innbusiness/icons/cardMgmt-box-credit.svg")
            .title("Interest-free credit")
            .subtitle("Utilise interest-free credit for up to six weeks.")
            .build())
        .expenseBox(ExpenseBox.builder()
            .icon("/content/dam/pi/websites/desktop/Business/innbusiness/icons/cardMgmt-box-expense.svg")
            .title("Expense management")
            .subtitle("Pre-authorise employee stays and allowances so they don’t need to pay on arrival.")
            .build())
        .invoicesBox(InvoicesBox.builder()
            .icon("/content/dam/pi/websites/desktop/Business/innbusiness/icons/cardMgmt-box-invoices.svg")
            .title("Consolidated invoices")
            .subtitle("Receive a single consolidated VAT invoice to summarise your company spend.")
            .build())
        .linkAccountBanner(LinkAccountBanner.builder()
            .title("Already have an InnBusiness Pay account?")
            .subtitle("If you already have an InnBusiness Pay account that you access through MMA you can tether it so you can view and manage it within InnBusiness")
            .linkAccountButton("Link to an existing account")
            .build())
        .cardManagement(CardManagement.builder()
            .title("Card management")
            .info("...")
            .build())
        .filters(Filters.builder()
            .show("Show")
            .cancelledCards("Cancelled cards")
            .myCards("Only my cards")
            .build())
        .columns(Columns.builder()
            .cardId("Card ID")
            .cardLabel("Card label")
            .expiry("Expiry")
            .yourCard("Your card")
            .cardHolderName("Card holder")
            .cardHolderRegistered("Card holder reg.")
            .cardNumber("Card no.")
            .cardStatus("Card status")
            .edit("Edit")
            .build())
        .cardHolder(CardHolder.builder()
            .cardHolderOptions(CardHolderOptions.builder()
                .resendCode("Resend code")
                .registered("Registered")
                .build())
            .build())
        .cardStatus(CardStatus.builder()
            .cardStatusOptions(CardStatusOptions.builder()
                .activate("Activate")
                .active("Active")
                .dispatching("Dispatching")
                .cancelled("Cancelled")
                .expired("Expired")
                .build())
            .build())
        .download(Download.builder()
            .icon("/content/dam/global/icons/common/download-purple.svg")
            .label("Download")
            .build())
        .newCard(NewCard.builder()
            .icon("/content/dam/global/icons/common/payment-card-add-white.svg")
            .label("Add a new card")
            .build())
        .badge(Badge.builder()
            .accountHolder("Account holder")
            .build())
        .build();
  }

  private CardManagementContentResponseAemDto getCardManagementAemInformation() {
    return CardManagementContentResponseAemDto.builder()
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
  }

  private CommonIconsRequest getCommonIconsRequestEn() {
    return CommonIconsRequest.builder()
        .language("en")
        .build();
  }

  private CommonIcons mockCommonIconsInformation() {
    return CommonIcons.builder()
        .arrow(Arrow.builder()
            .up("/content/dam/global/icons/common/arrow-up.svg")
            .right("/content/dam/global/icons/common/arrow-right.svg")
            .down("/content/dam/global/icons/common/arrow-down.svg")
            .left("/content/dam/global/icons/common/arrow-left.svg")
            .build())
        .payment(Payment.builder()
            .visa("/content/dam/global/icons/payments/visa.svg")
            .piba("/content/dam/global/icons/payments/piba.svg")
            .amex("/content/dam/global/icons/payments/amex.svg")
            .pibaEuro("/content/dam/global/icons/payments/piba-euro.svg")
            .mastercard("/content/dam/global/icons/payments/mastercard.svg")
            .build())
        .chevron(Chevron.builder()
            .up("/content/dam/global/icons/common/chevron-up.svg")
            .right("/content/dam/global/icons/common/chevron-right.svg")
            .left("/content/dam/global/icons/common/chevron-left.svg")
            .down("/content/dam/global/icons/common/chevron-down.svg")
            .upPurple("/content/dam/global/icons/common/chevron-up-purple.svg")
            .rightPurple("/content/dam/global/icons/common/chevron-right-purple.svg")
            .downPurple("/content/dam/global/icons/common/chevron-down-purple.svg")
            .leftPurple("/content/dam/global/icons/common/chevron-left-purple.svg")
            .build())
        .notification(Notification.builder()
            .error("/content/dam/global/icons/notifications/error.svg")
            .info("/content/dam/global/icons/notifications/info.svg")
            .question("/content/dam/global/icons/notifications/question.svg")
            .success("/content/dam/global/icons/notifications/success.svg")
            .alert("/content/dam/global/icons/notifications/alert.svg")
            .build())
        .build();
  }

  private CommonIconsResponseAemDto getCommonIconsAemInformation() {
    return CommonIconsResponseAemDto.builder()
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
  }

}
