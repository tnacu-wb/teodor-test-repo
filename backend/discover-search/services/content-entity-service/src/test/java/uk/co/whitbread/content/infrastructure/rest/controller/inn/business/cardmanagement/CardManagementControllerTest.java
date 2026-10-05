package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement;

import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.in.CardManagementRequest;
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
import uk.co.whitbread.content.domain.ports.primary.CardManagementInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.mapper.CardManagementRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.mapper.CardManagementResponseDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.in.CardManagementRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.ApplyBannerDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.ArrowDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.BadgeDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.CardHolderDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.CardHolderOptionsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.CardManagementContentDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.CardManagementDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.CardManagementResponseDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.CardStatusDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.CardStatusOptionsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.CentrallyStoredDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.ChevronDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.ColumnsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.CommonIconsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.CreditBoxDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.DownloadDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.ExpenseBoxDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.FiltersDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.InvoicesBoxDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.LinkAccountBannerDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.NewCardDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.NotificationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.PaymentDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.TabsDto;


@ExtendWith(MockitoExtension.class)
class CardManagementControllerTest {

  @InjectMocks
  private CardManagementController cardManagementController;

  @Mock
  private CardManagementInPort cardManagementInPort;

  @Mock
  private CardManagementRequestDtoMapper cardManagementRequestDtoMapper;

  @Mock
  private CardManagementResponseDtoMapper cardManagementResponseDtoMapper;

  @Test
  void getCardManagementInformationShouldReturnOK200() {
    //Arrange
    var cardManagementRequestDto = getCardManagementRequestDtoEn();
    var cardManagementRequest = getCardManagementRequestEn();

    Mockito.when(cardManagementRequestDtoMapper.toModel(cardManagementRequestDto))
        .thenReturn(cardManagementRequest);
    Mockito.when(cardManagementInPort.getCardManagementInfo(cardManagementRequest))
        .thenReturn(getCardManagementInformation());
    Mockito.when(cardManagementResponseDtoMapper.toDto(getCardManagementInformation()))
        .thenReturn(getCardManagementInformationDto());

    //Act
    final ResponseEntity<CardManagementResponseDto> response =
        cardManagementController.getCardManagementInfo(cardManagementRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void getCardManagementInformation__ShouldFinCardManagementInformation() {
    //Arrange
    var cardManagementRequestDto = getCardManagementRequestDtoEn();
    var cardManagementRequest = getCardManagementRequestEn();

    Mockito.when(cardManagementRequestDtoMapper.toModel(cardManagementRequestDto))
        .thenReturn(cardManagementRequest);
    Mockito.when(cardManagementInPort.getCardManagementInfo(cardManagementRequest))
        .thenReturn(getCardManagementInformation());
    Mockito.when(cardManagementResponseDtoMapper.toDto(getCardManagementInformation()))
        .thenReturn(getCardManagementInformationDto());

    //Act
    final var request = cardManagementRequestDtoMapper.toModel(cardManagementRequestDto);
    final var cardManagementInformation = cardManagementInPort.getCardManagementInfo(request);
    final var domainContentRequest = cardManagementResponseDtoMapper.toDto(cardManagementInformation);
    final ResponseEntity<CardManagementResponseDto> response =
        cardManagementController.getCardManagementInfo(cardManagementRequestDto);

    //Assertions
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), domainContentRequest.getCardManagementContent().getCardHolder().getCardHolderOptions().getRegistered(),
        Objects.requireNonNull(response.getBody()).getCardManagementContent().getCardHolder().getCardHolderOptions().getRegistered());
    assertEquals(response.toString(), domainContentRequest.getCardManagementContent().getColumns().getYourCard(),
        response.getBody().getCardManagementContent().getColumns().getYourCard());
    assertEquals(response.toString(), domainContentRequest.getCardManagementContent().getFilters().getCancelledCards(),
        response.getBody().getCardManagementContent().getFilters().getCancelledCards());
    assertEquals(response.toString(), domainContentRequest.getCardManagementContent().getLinkAccountBanner().getTitle(),
        response.getBody().getCardManagementContent().getLinkAccountBanner().getTitle());
    assertEquals(response.toString(), domainContentRequest.getCardManagementContent().getCardStatus().getCardStatusOptions().getExpired(),
        response.getBody().getCardManagementContent().getCardStatus().getCardStatusOptions().getExpired());
    assertEquals(response.toString(), domainContentRequest.getCommonIcons().getChevron().getUp(),
        response.getBody().getCommonIcons().getChevron().getUp());
    assertEquals(response.toString(), domainContentRequest.getCommonIcons().getPayment().getAmex(),
        response.getBody().getCommonIcons().getPayment().getAmex());
    assertEquals(response.toString(), domainContentRequest.getCommonIcons().getNotification().getAlert(),
        response.getBody().getCommonIcons().getNotification().getAlert());
    assertEquals(response.toString(), domainContentRequest.getCommonIcons().getArrow().getDown(),
        response.getBody().getCommonIcons().getArrow().getDown());
    assertEquals(response.toString(), domainContentRequest.getCommonIcons().getChevron().getDownPurple(),
        response.getBody().getCommonIcons().getChevron().getDownPurple());

  }

  private CardManagementResponseDto getCardManagementInformationDto() {
    return CardManagementResponseDto.builder()
        .cardManagementContent(getCardManagementContentDto())
        .commonIcons(getCommonIconsDto())
        .build();
  }

  private CommonIconsDto getCommonIconsDto() {
    return CommonIconsDto.builder()
        .arrow(ArrowDto.builder()
            .up("/content/dam/global/icons/common/arrow-up.svg")
            .right("/content/dam/global/icons/common/arrow-right.svg")
            .down("/content/dam/global/icons/common/arrow-down.svg")
            .left("/content/dam/global/icons/common/arrow-left.svg")
            .build())
        .payment(PaymentDto.builder()
            .visa("/content/dam/global/icons/payments/visa.svg")
            .piba("/content/dam/global/icons/payments/piba.svg")
            .amex("/content/dam/global/icons/payments/amex.svg")
            .pibaEuro("/content/dam/global/icons/payments/piba-euro.svg")
            .mastercard("/content/dam/global/icons/payments/mastercard.svg")
            .build())
        .chevron(ChevronDto.builder()
            .up("/content/dam/global/icons/common/chevron-up.svg")
            .right("/content/dam/global/icons/common/chevron-right.svg")
            .left("/content/dam/global/icons/common/chevron-left.svg")
            .down("/content/dam/global/icons/common/chevron-down.svg")
            .upPurple("/content/dam/global/icons/common/chevron-up-purple.svg")
            .rightPurple("/content/dam/global/icons/common/chevron-right-purple.svg")
            .downPurple("/content/dam/global/icons/common/chevron-down-purple.svg")
            .leftPurple("/content/dam/global/icons/common/chevron-left-purple.svg")
            .build())
        .notification(NotificationDto.builder()
            .error("/content/dam/global/icons/notifications/error.svg")
            .info("/content/dam/global/icons/notifications/info.svg")
            .question("/content/dam/global/icons/notifications/question.svg")
            .success("/content/dam/global/icons/notifications/success.svg")
            .alert("/content/dam/global/icons/notifications/alert.svg")
            .build())
        .build();
  }

  private CardManagementContentDto getCardManagementContentDto() {
    return CardManagementContentDto.builder()
        .title("Card management")
        .tabs(TabsDto.builder()
            .centrallyStoredTitle("Centrally stored")
            .centrallyStored(CentrallyStoredDto.builder()
                .description("Save your Premier Inn Business Account Card or company cards and allow employees to use assigned cards to make payments. You can save an unlimited number of cards.")
                .build())
            .innBusinessPay("InnBusiness Pay")
            .build())
        .applyBanner(ApplyBannerDto.builder()
            .title("Work travel just got easier with InnBusiness Pay")
            .subtitle("The new, hassle-free payment solution that makes travel a breeze.")
            .applyNowButton("Apply now")
            .linkAccountButton("Link an existing account")
            .image("/")
            .build())
        .creditBox(CreditBoxDto.builder()
            .icon("/")
            .title("Interest-free credit")
            .subtitle("Utilise interest-free credit for up to six weeks.")
            .build())
        .expenseBox(ExpenseBoxDto.builder()
            .icon("/")
            .title("Expense management")
            .subtitle("Pre-authorise employee stays and allowances so they don’t need to pay on arrival.")
            .build())
        .invoicesBox(InvoicesBoxDto.builder()
            .icon("/")
            .title("Consolidated invoices")
            .subtitle("Receive a single consolidated VAT invoice to summarise your company spend.")
            .build())
        .linkAccountBanner(LinkAccountBannerDto.builder()
            .title("Already have an InnBusiness Pay account?")
            .subtitle("If you already have an InnBusiness Pay account that you access through MMA you can tether it so you can view and manage it within InnBusiness")
            .linkAccountButton("Link to an existing account")
            .build())
        .cardManagement(CardManagementDto.builder()
            .title("Card management")
            .info("...")
            .build())
        .filters(FiltersDto.builder()
            .show("Show")
            .cancelledCards("Cancelled cards")
            .myCards("Only my cards")
            .build())
        .columns(ColumnsDto.builder()
            .yourCard("Your card")
            .cardHolderName("Card holder")
            .cardHolderRegistered("Card holder reg.")
            .cardNumber("Card no.")
            .cardStatus("Card status")
            .edit("Edit")
            .build())
        .cardHolder(CardHolderDto.builder()
            .cardHolderOptions(CardHolderOptionsDto.builder()
                .resendCode("Resend code")
                .registered("Registered")
                .build())
            .build())
        .cardStatus(CardStatusDto.builder()
            .cardStatusOptions(CardStatusOptionsDto.builder()
                .activate("Activate")
                .active("Active")
                .dispatching("Dispatching")
                .cancelled("Cancelled")
                .expired("Expired")
                .build())
            .build())
        .download(DownloadDto.builder()
            .icon("/")
            .label("Download")
            .build())
        .newCard(NewCardDto.builder()
            .icon("/")
            .label("Add a new card")
            .build())
        .badge(BadgeDto.builder()
            .accountHolder("Account holder")
            .build())
        .build();
  }

  private CardManagementResponse getCardManagementInformation() {
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

  private CardManagementRequest getCardManagementRequestEn() {
    return CardManagementRequest.builder()
        .language("en")
        .build();
  }

  private CardManagementRequestDto getCardManagementRequestDtoEn() {
    return CardManagementRequestDto.builder()
        .language("en")
        .build();
  }

}
