package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static uk.co.whitbread.hotel.ohip.adapter.generated.models.ResProfileTypeType.RESERVATIONCONTACT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_PURCHASE_ORDER_NAME;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BillingInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardNumberTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardProcessingType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CurrencyAmountType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.FormattedTextTextType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PayeeInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoutingInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoutingInfoTypeFolio;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoutingInfoTypeFolioGuestInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoutingInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoutingInstructionTypeDuration;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TimeSpanType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TrxInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CreditCardInfo;
import uk.co.whitbread.hotel.rules.agent.generated.models.BusinessAllowanceRuleDto;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.reservation.in.Allowance;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessAllowance;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItems;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationAmounts;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.UpdateBusinessItemsException;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.PaymentUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class BusinessItemsOhipTransformer {

  public static final String RESERVATION_VALUE = "RESERVATION";
  private static final String DINNER_ALLOWANCE = "dinner";
  private static final String ALCOHOL_ALLOWANCE = "alcohol";

  @Named("injectUserDefinedFields")
  public UserDefinedFieldsType injectUserDefinedFields(BusinessItems businessItems) {
    UserDefinedFieldsType userDefinedFieldsType = new UserDefinedFieldsType();

    if (StringUtils.isNotBlank(businessItems.getPurchaseOrderNumber())) {
      CharacterUDFType purchaseOrderUDFType = new CharacterUDFType();
      purchaseOrderUDFType.name(UDFC_PURCHASE_ORDER_NAME);
      purchaseOrderUDFType.value(businessItems.getPurchaseOrderNumber());

      userDefinedFieldsType.addCharacterUDFsItem(purchaseOrderUDFType);
    }

    return userDefinedFieldsType;
  }

  public List<RoutingInfoType> buildRoutingInstructions(
      HotelReservationType hotelReservationType,
      List<BusinessAllowanceRuleDto> allowanceRules,
      int folioWindow) {

    List<RoutingInfoType> routingInstructions = new ArrayList<>();
    if (allowanceRules != null) {
      allowanceRules.stream()
          .forEach(allowanceRule ->
              routingInstructions.add(
                  buildRoutingInstruction(hotelReservationType, allowanceRule, folioWindow)));
    }
    return routingInstructions;

  }

  public List<RoutingInfoType> injectRoutingInstructions(HotelReservationType hotelReservationType, String companyId,
         int folioWindow) {
    List<RoutingInfoType> routingInstructions = new ArrayList<>();
    routingInstructions.add(buildRoutingInstruction(hotelReservationType, companyId, folioWindow));
    return routingInstructions;
  }

  public List<RoutingInfoType> injectRoutingInstructions(BusinessItems businessItems,
      HotelReservationType hotelReservationType, boolean isDistribution,
      List<BusinessAllowanceRuleDto> businessAllowanceRules, String companyId,
      int folioWindowNo) {
    List<RoutingInfoType> routingInstructions = new ArrayList<>();
    List<String> routingInstructionCodes = new ArrayList<>();

    var businessAllowances = checkForAlcohol(businessItems.getBusinessAllowances());

    businessAllowances.stream().filter(BusinessAllowance::getIsAuthorised).toList()
        .forEach(allowance -> {

          var allowanceRule = businessAllowanceRules.stream()
              .filter(rule -> rule.getSourceId().equals(allowance.getAllowance()))
              .findFirst().orElseThrow(() -> {
                var exception = new UpdateBusinessItemsException(
                    ErrorCode.DIGITAL_INVALID_ALLOWANCE_EXCEPTION, "Invalid allowance");
                ExceptionLogger.log(log, exception);
                throw exception;
              });

          if (!routingInstructionCodes.contains(allowanceRule.getTargetId())) {
            var tempInstruction =
                buildRoutingInstruction(allowance, hotelReservationType, isDistribution,
                    allowanceRule, companyId, folioWindowNo);
            routingInstructions.add(tempInstruction);
            routingInstructionCodes.add(allowanceRule.getTargetId());
          }
        });
    return routingInstructions;
  }

  private RoutingInfoType buildRoutingInstruction(
      BusinessAllowance allowance,
      HotelReservationType hotelReservationType,
      boolean isDistribution,
      BusinessAllowanceRuleDto allowanceRule, String companyId,
      int folioWindowNo) {

    RoutingInfoType routingInstruction = new RoutingInfoType();

    routingInstruction.refreshFolio(Boolean.TRUE);

    RoutingInfoTypeFolio folio = new RoutingInfoTypeFolio();

    RoutingInfoTypeFolioGuestInfo guestInfo =
        buildGuestInfo(hotelReservationType.getReservationGuests().get(0));
    PayeeInfoType payeeInfo =
        buildPayeeInfo(hotelReservationType, isDistribution, companyId);

    folio.guestInfo(guestInfo);
    folio.payeeInfo(payeeInfo);
    var instruction =
        buildRoutingInstructionType(allowance, hotelReservationType.getRoomStay().getArrivalDate(),
            hotelReservationType.getRoomStay().getDepartureDate(), allowanceRule);

    if (!Objects.isNull(allowance.getBudget()) && BigDecimal.ZERO.compareTo(allowance.getBudget()) != 0) {
      instruction.setCreditLimit(allowance.getBudget());
    }

    folio.addInstructionsItem(instruction);
    folio.folioWindowNo(folioWindowNo);

    routingInstruction.setFolio(folio);
    return routingInstruction;
  }

  private RoutingInfoType buildRoutingInstruction(HotelReservationType hotelReservationType,
      BusinessAllowanceRuleDto allowanceRule, int folioWindow) {
    RoutingInfoType routingInstruction = new RoutingInfoType();
    routingInstruction.refreshFolio(Boolean.TRUE);

    RoutingInfoTypeFolio folio = new RoutingInfoTypeFolio();
    RoutingInfoTypeFolioGuestInfo guestInfo =
        buildGuestInfo(hotelReservationType.getReservationGuests().get(0));
    PayeeInfoType payeeInfo = buildPayeeInfo(hotelReservationType.getReservationGuests().get(0),
        hotelReservationType.getReservationProfiles().getReservationProfile(), null);
    folio.guestInfo(guestInfo);
    folio.payeeInfo(payeeInfo);

    var instruction = buildRoutingInstructionType(hotelReservationType.getRoomStay().getArrivalDate(),
        hotelReservationType.getRoomStay().getDepartureDate(), allowanceRule);
    folio.addInstructionsItem(instruction);

    folio.folioWindowNo(folioWindow);
    routingInstruction.setFolio(folio);
    return routingInstruction;
  }

  private RoutingInfoType buildRoutingInstruction(HotelReservationType hotelReservationType, String companyId, 
          int folioWindow) {
    RoutingInfoType routingInstruction = new RoutingInfoType();
    routingInstruction.refreshFolio(Boolean.TRUE);

    RoutingInfoTypeFolio folio = new RoutingInfoTypeFolio();
    RoutingInfoTypeFolioGuestInfo guestInfo =
            buildGuestInfo(hotelReservationType.getReservationGuests().get(0));
    PayeeInfoType payeeInfo = buildPayeeInfo(hotelReservationType.getReservationGuests().get(0),
            hotelReservationType.getReservationProfiles().getReservationProfile(), companyId);
    folio.guestInfo(guestInfo);
    folio.payeeInfo(payeeInfo);

    var instruction = buildRoutingInstructionType(hotelReservationType.getRoomStay().getArrivalDate(),
                    hotelReservationType.getRoomStay().getDepartureDate());
    folio.addInstructionsItem(instruction);

    folio.folioWindowNo(folioWindow);
    routingInstruction.setFolio(folio);
    return routingInstruction;
  }

  public List<CommentInfoType> injectComments(BusinessItems businessItems,
      HotelReservationType hotelReservationType) {
    List<CommentInfoType> comments = new ArrayList<>();

    comments.add(buildComments(businessItems.getBusinessNotes(), hotelReservationType.getHotelId()));
    return comments;
  }

  private CommentInfoType buildComments(String note, String hotelId) {

    FormattedTextTextType formattedTextTextType = new FormattedTextTextType();
    formattedTextTextType.value(note);

    CommentType commentType = new CommentType();
    commentType.hotelId(hotelId);
    commentType.type(RESERVATION_VALUE);
    commentType.notificationLocation(RESERVATION_VALUE);
    commentType.commentTitle(OhipConstants.BUSINESS_NOTES_COMMENT_TITLE);
    commentType.internal(true);
    commentType.text(formattedTextTextType);

    CommentInfoType comment = new CommentInfoType();
    comment.comment(commentType);
    return comment;
  }

  private RoutingInfoTypeFolioGuestInfo buildGuestInfo(ResGuestType info) {
    RoutingInfoTypeFolioGuestInfo guestInfo = new RoutingInfoTypeFolioGuestInfo();
    guestInfo.setProfileIdList(info.getProfileInfo().getProfileIdList());
    return guestInfo;
  }

  private PayeeInfoType buildPayeeInfo(HotelReservationType hotelReservationType,
      boolean isDistribution, String companyId) {
    PayeeInfoType payee = new PayeeInfoType();

    if (Objects.nonNull(companyId)) {
      hotelReservationType.getReservationProfiles().getReservationProfile().stream()
          .filter(reservationProfileType -> reservationProfileType.getProfileIdList()
              .stream()
              .anyMatch(uniqueIDType -> companyId.equals(uniqueIDType.getId())))
          .findFirst()
          .ifPresent(reservationProfileType -> payee.setPayeeId(
              reservationProfileType.getProfileIdList().get(0)));
    } else if (isDistribution) {
      payee
          .setPayeeId(hotelReservationType.getReservationProfiles().getReservationProfile().stream()
              .filter(profile -> RESERVATIONCONTACT.equals(profile.getReservationProfileType()))
              .findFirst()
              .map(ReservationProfileType::getProfileIdList)
              .orElse(hotelReservationType.getReservationGuests().get(0).getProfileInfo()
                  .getProfileIdList()).get(0));
    } else {
      payee.setPayeeId(
          hotelReservationType.getReservationGuests().get(0).getProfileInfo().getProfileIdList()
              .get(0));
    }
    return payee;
  }

  private PayeeInfoType buildPayeeInfo(ResGuestType resGuest, List<ReservationProfileType> resProfileList,
      String companyId) {
    PayeeInfoType payee = new PayeeInfoType();

    if (Objects.nonNull(companyId)) {
      resProfileList.stream()
          .filter(reservationProfileType -> reservationProfileType.getProfileIdList()
              .stream()
              .anyMatch(uniqueIDType -> companyId.equals(uniqueIDType.getId())))
          .findFirst()
          .ifPresent(reservationProfileType -> payee.setPayeeId(
              reservationProfileType.getProfileIdList().get(0)));
    } else {
      payee.setPayeeId(resProfileList.stream()
          .filter(profile -> RESERVATIONCONTACT.equals(profile.getReservationProfileType()))
          .findFirst()
          .map(ReservationProfileType::getProfileIdList)
          .orElse(resGuest.getProfileInfo().getProfileIdList()).get(0));
    }

    return payee;
  }

  private RoutingInstructionType buildRoutingInstructionType(BusinessAllowance allowance,
      LocalDate arrival, LocalDate departure, BusinessAllowanceRuleDto allowanceRule) {
    RoutingInstructionType instructionType = new RoutingInstructionType();
    if (allowance != null) {
      instructionType.duration(buildDuration(arrival, departure, allowanceRule,
          DINNER_ALLOWANCE.equals(allowance.getAllowance()) || ALCOHOL_ALLOWANCE.equals(
              allowance.getAllowance())));
    }

    if (allowanceRule.getIsTransactionCode()) {
      var transactionCode = new TrxInfoType().transactionCode(allowanceRule.getTargetId());
      instructionType.addTransactionCodesItem(transactionCode);
    } else {
      var routingCode = new BillingInstructionType().billingCode(allowanceRule.getTargetId());
      instructionType.addBillingInstructionsItem(routingCode);
    }

    return instructionType;
  }

  private RoutingInstructionType buildRoutingInstructionType(LocalDate arrival, LocalDate departure) {
    RoutingInstructionType instructionType = new RoutingInstructionType();
    instructionType.duration(buildDuration(arrival, departure));
    return instructionType;
  }

  private RoutingInstructionType buildRoutingInstructionType(LocalDate arrival, LocalDate departure,
      BusinessAllowanceRuleDto allowanceRule) {
    RoutingInstructionType instructionType =
        buildRoutingInstructionType(null, arrival, departure, allowanceRule);
    instructionType.duration(buildDuration(arrival, departure));
    return instructionType;
  }

  public List<ReservationPaymentMethodType> buildPaymentMethodType(
      String reservationPaymentMethod,
      CreditCardInfo creditCardInfo,
      ReservationAmounts reservationAmounts) {
    var folio3 = new ReservationPaymentMethodType();

    if (creditCardInfo != null && creditCardInfo.getCreditCard() != null) {
      var card = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
      card.setCardNumber(creditCardInfo.getCreditCard().getCardNumber());
      card.setCardNumberMasked(creditCardInfo.getCreditCard().getCardNumberMasked());
      PaymentUtils.setCardType(creditCardInfo, card);
      card.setExpirationDate(creditCardInfo.getCreditCard().getExpirationDate());
      card.setCardHolderName(creditCardInfo.getCreditCard().getCardHolderName());
      card.setCardNumberLast4Digits(creditCardInfo.getCreditCard().getCardNumberLast4Digits());
      card.setCardOrToken(CardNumberTypeType.TOKEN);
      card.setProcessing(CardProcessingType.MANUAL);
      folio3.setPaymentCard(card);
    }

    var currencyAmountType = new CurrencyAmountType();
    currencyAmountType.setAmount(reservationAmounts.getDeposit());
    currencyAmountType.setCurrencyCode(reservationAmounts.getCurrencyCode());

    folio3.setPaymentMethod(reservationPaymentMethod);
    folio3.setBalance(currencyAmountType);
    folio3.setFolioView(3);

    return Collections.singletonList(folio3);
  }

  private RoutingInstructionTypeDuration buildDuration(LocalDate arrival, LocalDate departure,
      BusinessAllowanceRuleDto allowanceRule, boolean isDinnerOrAlcohol) {
    var duration = new RoutingInstructionTypeDuration().monday(true).tuesday(true)
        .wednesday(true).thursday(true).friday(true).saturday(true).sunday(true);
    if (allowanceRule.getIsApplicableDaily()) {
      LocalDate calculatedDeparture = departure;
      if (isDinnerOrAlcohol && !arrival.isEqual(departure)) {
        calculatedDeparture = departure.minusDays(1);
      }
      return duration.daily(true).timeSpan(new TimeSpanType().startDate(arrival).endDate(calculatedDeparture));
    }
    return duration.daily(false);
  }

  // Method used for DISTR. This is needed to add the Booker to Payment Details
  // so it can be added to the generated invoices
  private RoutingInstructionTypeDuration buildDuration(LocalDate arrival, LocalDate departure) {
    return new RoutingInstructionTypeDuration().daily(false).timeSpan(
            new TimeSpanType().startDate(arrival).endDate(departure));
  }

  private List<BusinessAllowance> checkForAlcohol(List<BusinessAllowance> businessAllowances) {
    List<BusinessAllowance> filteredAllowances = new ArrayList<>();

    businessAllowances.forEach(businessAllowance -> {
      if (businessAllowance.getAllowance().equals(Allowance.ALLOW_ALCOHOL.getCode())
          && Boolean.TRUE.equals(businessAllowance.getIsAuthorised())) {
        var dinnerAllowance = businessAllowances.stream()
            .filter(allowance -> allowance.getAllowance().equals(Allowance.DINNER.getCode()))
            .toList().get(0);
        dinnerAllowance.setIsAuthorised(Boolean.FALSE);
        businessAllowance.setBudget(dinnerAllowance.getBudget());
        filteredAllowances.add(businessAllowance);
        filteredAllowances.add(dinnerAllowance);
      } else {
        filteredAllowances.add(businessAllowance);
      }
    });
    return filteredAllowances;
  }
}
