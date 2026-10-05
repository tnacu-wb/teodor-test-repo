package uk.co.whitbread.content.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CardManagementResponse;
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
import uk.co.whitbread.content.domain.ports.secondary.CardManagementOutPort;

@ExtendWith(MockitoExtension.class)
class CardManagementInPortImplTest {

  @Mock
  private CardManagementOutPort cardManagementOutPort;

  @InjectMocks
  private CardManagementInPortImpl cardManagementInPort;

  @Test
  void getCommonIconsInfoShouldReturnOk() {
    when(this.cardManagementOutPort.getCommonIconsInfo(any())).thenReturn(mockCommonIconsInfo());

    final var aemResponse = cardManagementInPort.getCommonIconsInfo(createCommonIconsRequest());

    assertEquals("/content/dam/global/icons/payments/visa.svg", aemResponse.getPayment().getVisa());
    assertEquals("/content/dam/global/icons/common/chevron-up.svg", aemResponse.getChevron().getUp());
    assertEquals("/content/dam/global/icons/notifications/error.svg", aemResponse.getNotification().getError());
    assertEquals("/content/dam/global/icons/common/arrow-down.svg", aemResponse.getArrow().getDown());
    assertEquals("/content/dam/global/icons/common/chevron-down-purple.svg", aemResponse.getChevron().getDownPurple());

    verify(cardManagementOutPort).getCommonIconsInfo(any());
  }

  @Test
  void getCardManagementInfoShouldReturnOk() {
    when(this.cardManagementOutPort.getCardManagementInfo(any())).thenReturn(mockCardManagementInfo());

    final var aemResponse = cardManagementInPort.getCardManagementInfo(createCardManagementRequest());

    assertEquals("/content/dam/global/icons/payments/visa.svg", aemResponse.getCommonIcons().getPayment().getVisa());
    assertEquals("/content/dam/global/icons/common/chevron-up.svg", aemResponse.getCommonIcons().getChevron().getUp());
    assertEquals("/content/dam/global/icons/notifications/error.svg", aemResponse.getCommonIcons().getNotification().getError());
    assertEquals("/content/dam/global/icons/common/arrow-down.svg", aemResponse.getCommonIcons().getArrow().getDown());
    assertEquals("/content/dam/global/icons/common/chevron-down-purple.svg", aemResponse.getCommonIcons().getChevron().getDownPurple());

    assertEquals("Card management", aemResponse.getCardManagementContent().getTitle());
    assertEquals("Centrally stored", aemResponse.getCardManagementContent().getTabs().getCentrallyStoredTitle());
    assertEquals("Link an existing account", aemResponse.getCardManagementContent().getApplyBanner().getLinkAccountButton());
    assertEquals("Interest-free credit", aemResponse.getCardManagementContent().getCreditBox().getTitle());
    assertEquals("Expense management", aemResponse.getCardManagementContent().getExpenseBox().getTitle());
    assertEquals("Consolidated invoices", aemResponse.getCardManagementContent().getInvoicesBox().getTitle());
    assertEquals("Already have an InnBusiness Pay account?", aemResponse.getCardManagementContent().getLinkAccountBanner().getTitle());
    assertEquals("Card management", aemResponse.getCardManagementContent().getCardManagement().getTitle());
    assertEquals("Only my cards", aemResponse.getCardManagementContent().getFilters().getMyCards());
    assertEquals("Your card", aemResponse.getCardManagementContent().getColumns().getYourCard());
    assertEquals("Resend code", aemResponse.getCardManagementContent().getCardHolder().getCardHolderOptions().getResendCode());
    assertEquals("Activate", aemResponse.getCardManagementContent().getCardStatus().getCardStatusOptions().getActivate());
    assertEquals("Download", aemResponse.getCardManagementContent().getDownload().getLabel());
    assertEquals("Add a new card", aemResponse.getCardManagementContent().getNewCard().getLabel());
    assertEquals("Account holder", aemResponse.getCardManagementContent().getBadge().getAccountHolder());
    assertEquals("Expired", aemResponse.getCardManagementContent().getCardStatus().getCardStatusOptions().getExpired());

    verify(cardManagementOutPort).getCardManagementInfo(any());
  }

  private CardManagementRequest createCardManagementRequest() {
    return CardManagementRequest.builder()
        .language("en")
        .build();
  }

  private CardManagementResponse mockCardManagementInfo() {

    return CardManagementResponse.builder()
        .cardManagementContent(CardManagementContent.builder()
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
            .build())
        .commonIcons(mockCommonIconsInfo())
        .build();
  }

  private CommonIconsRequest createCommonIconsRequest() {
    return CommonIconsRequest.builder()
        .language("en")
        .build();
  }

  private CommonIcons mockCommonIconsInfo() {

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



}
