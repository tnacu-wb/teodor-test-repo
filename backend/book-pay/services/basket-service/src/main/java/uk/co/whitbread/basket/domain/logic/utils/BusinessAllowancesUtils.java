package uk.co.whitbread.basket.domain.logic.utils;

import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.ALLOWED;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.BUSINESS_ALLOWANCE_RULE_ALLOWANCE_TYPE;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.BUSINESS_ALLOWANCE_RULE_PACKAGE_TYPE;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.UNKNOWN_CARD_TYPE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.model.basket.out.BookingAllowance;
import uk.co.whitbread.basket.domain.model.business.in.BusinessAllowance;
import uk.co.whitbread.basket.domain.model.business.in.BusinessItems;
import uk.co.whitbread.basket.domain.model.content.out.BusinessNotesResponse;
import uk.co.whitbread.basket.domain.model.content.out.Note;
import uk.co.whitbread.basket.domain.model.payments.in.Allowance;
import uk.co.whitbread.basket.domain.model.payments.in.BusinessAccount;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceRule;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceSourceType;
import uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BusinessAllowancesUtils {

  public static final String BUSINESS_NOTES_FORMATTING_ERROR = "Error while formatting business notes!";

  public static BusinessItems buildBusinessItemsFromBusinessAccount(BusinessAccount businessAccount,
      Map<Integer, Allowance> mealsMap) {

    final var businessAllowances = new ArrayList<BusinessAllowance>();

    if (businessAccount != null) {
      if (Optional.ofNullable(businessAccount.getDinnerAllowance()).isPresent()) {
        businessAllowances.add(
            BusinessAllowance.builder().allowance(Allowance.DINNER.getCode())
                .budget(businessAccount.getDinnerAllowance())
                .isAuthorised(Boolean.TRUE).build());
      }
      if (Optional.ofNullable(businessAccount.getBreakfastCodeReq()).isPresent()) {
        businessAllowances.add(
            BusinessAllowance.builder().allowance(
                    mealsMap.get(businessAccount.getBreakfastCodeReq())
                        .getCode())
                .isAuthorised(Boolean.TRUE).build());
      }
      if (Optional.ofNullable(businessAccount.getAlcoholAllowed()).isPresent()) {
        businessAllowances.add(
            BusinessAllowance.builder().allowance(Allowance.ALLOW_ALCOHOL.getCode())
                .isAuthorised(ALLOWED.equalsIgnoreCase(businessAccount.getAlcoholAllowed())).build());
      }
      if (Optional.ofNullable(businessAccount.getCarParkingAllowed()).isPresent()) {
        businessAllowances.add(
            BusinessAllowance.builder().allowance(Allowance.CAR_PARKING.getCode())
                .isAuthorised(ALLOWED.equalsIgnoreCase(businessAccount.getCarParkingAllowed()))
                .build());
      }
      if (Optional.ofNullable(businessAccount.getWifiAllowed()).isPresent()) {
        businessAllowances.add(
            BusinessAllowance.builder().allowance(Allowance.ULTIMATE_WIFI.getCode())
                .isAuthorised(ALLOWED.equalsIgnoreCase(businessAccount.getWifiAllowed())).build());
      }
      if (Optional.ofNullable(businessAccount.getOtherChargesAllowed()).isPresent()) {
        businessAllowances.add(
            BusinessAllowance.builder().allowance(Allowance.ADDITIONAL_CHARGES.getCode())
                .isAuthorised(ALLOWED.equalsIgnoreCase(businessAccount.getOtherChargesAllowed()))
                .build());
      }
    }

    return BusinessItems.builder().businessAllowances(businessAllowances)
        .customReferenceNumber(businessAccount != null ? businessAccount.getCustomerReference() : null)
        .purchaseOrderNumber(businessAccount != null ? businessAccount.getPurchaseOrder() : null)
        .build();
  }

  public static BusinessItems getBusinessItems(BusinessItems initialBusinessItems,
      List<BusinessAllowanceRule> businessAllowanceRules,
      BusinessNotesResponse businessNotesResponse, List<String> basketPackageCodes, String cardType) {

    final var finalBusinessItems = initialBusinessItems == null ? BusinessItems.builder().build() :
        BusinessItems.builder()
            .customReferenceNumber(initialBusinessItems.getCustomReferenceNumber())
            .purchaseOrderNumber(initialBusinessItems.getPurchaseOrderNumber())
            .businessAllowances(initialBusinessItems.getBusinessAllowances())
            .businessNotes(initialBusinessItems.getBusinessNotes())
            .build();

    // add business allowances for packages
    final List<BusinessAllowance> finalBusinessAllowances = Objects.nonNull(finalBusinessItems.getBusinessAllowances())
        ? new ArrayList<>(finalBusinessItems.getBusinessAllowances()) : new ArrayList<>();

    final var packageCodesWithAllowances = getPackageCodesWithAllowances(businessAllowanceRules);

    basketPackageCodes.forEach(packageCode -> {
      if (packageCodesWithAllowances.contains(packageCode)) {
        finalBusinessAllowances.add(
            BusinessAllowance.builder().allowance(packageCode)
                .isAuthorised(true).build());
      }
    });

    // add business allowance for accommodation
    finalBusinessAllowances.add(
        BusinessAllowance.builder()
            .allowance(BasketConstants.ACCOMMODATION_ALLOWANCE_NAME)
            .isAuthorised(true).build());

    finalBusinessItems.setBusinessAllowances(finalBusinessAllowances);

    finalBusinessItems.setBusinessNotes(
        modelBusinessNotes(businessNotesResponse, finalBusinessAllowances, businessAllowanceRules, cardType));

    return finalBusinessItems;
  }

  private static List<String> getPackageCodesWithAllowances(List<BusinessAllowanceRule> businessAllowances) {
    return businessAllowances
        .stream()
        .filter(businessAllowanceRule ->
            BusinessAllowanceSourceType.PACKAGE.name().equals(businessAllowanceRule.getSourceType()))
        .map(BusinessAllowanceRule::getSourceId)
        .toList();
  }

  private static String modelBusinessNotes(BusinessNotesResponse businessNotesResponse,
      List<BusinessAllowance> businessAllowances,
      List<BusinessAllowanceRule> businessAllowanceRules,
      String paymentType) {

    List<String> notes = new ArrayList<>();

    // card type to show PIBA/Credit/""
    var cardType =
        businessNotesResponse.getCardTypes().stream()
            .filter(cardTypeValue -> paymentType.toLowerCase().contains(cardTypeValue.getId().toLowerCase()))
            .findFirst()
            .map(Note::getValue).orElse(UNKNOWN_CARD_TYPE);

    // headers
    addNotesForHeaders(notes, businessNotesResponse, cardType);

    //allowances headers
    addNotesForAllowancesHeaders(notes, businessNotesResponse, cardType);

    final var aemIds = new ArrayList<String>();

    // allowances for packages added within reservation
    addNotesForPackages(notes, aemIds, businessNotesResponse, businessAllowances, businessAllowanceRules);

    // allowances for business items selected
    addNotesForBusinessAllowances(notes, aemIds, businessNotesResponse, businessAllowances, businessAllowanceRules);

    // footers
    addNotesForFooters(notes, businessNotesResponse);

    return StringUtils.join(notes, "\n");
  }

  private static void addNotesForHeaders(List<String> notes, BusinessNotesResponse businessNotesResponse,
      String cardType) {
    if (Objects.nonNull(businessNotesResponse.getHeaders())) {
      notes.addAll(businessNotesResponse.getHeaders().stream().map(Note::getValue)
          .map(str -> replaceCardTypePlaceHolder(str, cardType)).toList());
    }
  }

  private static void addNotesForAllowancesHeaders(List<String> notes, BusinessNotesResponse businessNotesResponse,
      String cardType) {
    if (Objects.nonNull(businessNotesResponse.getAllowances())) {
      notes.addAll(businessNotesResponse.getAllowances().stream().map(Note::getValue)
          .map(str -> replaceCardTypePlaceHolder(str, cardType)).toList());
    }
  }

  private static void addNotesForPackages(List<String> notes, List<String> aemIds,
      BusinessNotesResponse businessNotesResponse, List<BusinessAllowance> businessAllowances,
      List<BusinessAllowanceRule> businessAllowanceRules) {
    if (Objects.nonNull(businessNotesResponse.getPackages())) {
      businessAllowances.forEach(allowance ->
          businessAllowanceRules.stream()
              .filter(rule -> rule.isNotesMandatory() && rule.getSourceId().equals(allowance.getAllowance())
                  && BUSINESS_ALLOWANCE_RULE_PACKAGE_TYPE.equals(rule.getSourceType())).findFirst()
              .ifPresent(rule -> {
                if (!aemIds.contains(rule.getAemId())) {
                  var businessNotes = businessNotesResponse.getPackages().stream()
                      .filter(note -> note.getId().equals(rule.getAemId())).findFirst();
                  if (businessNotes.isPresent()) {
                    notes.addAll(businessNotes.stream().map(Note::getValue).toList());
                    aemIds.add(rule.getAemId());
                  } else {
                    var exception =  new PaymentException(
                        ErrorCode.DIGITAL_FORMATTING_BUSINESS_NOTES_EXCEPTION, BUSINESS_NOTES_FORMATTING_ERROR);
                    ExceptionLogger.log(log, exception);
                    throw exception;
                  }
                }
              }));
    }
  }

  private static void addNotesForBusinessAllowances(List<String> notes, List<String> aemIds,
      BusinessNotesResponse businessNotesResponse, List<BusinessAllowance> businessAllowances,
      List<BusinessAllowanceRule> businessAllowanceRules) {

    // add mandatory notes either they are present or not in the input
    businessAllowanceRules.stream()
        .filter(rule -> rule.isNotesMandatory() && BUSINESS_ALLOWANCE_RULE_ALLOWANCE_TYPE.equals(rule.getSourceType()))
        .forEach(rule -> {
          if (!aemIds.contains(rule.getAemId())) {
            var businessNote = businessNotesResponse.getBusinessNotes().stream()
                .filter(note -> note.getId().equals(rule.getAemId())).findFirst();
            if (businessNote.isPresent()) {
              final var businessAllowance =
                  businessAllowances.stream().filter(allowance -> rule.getSourceId().equals(allowance.getAllowance()))
                      .findFirst();
              if (businessAllowance.isPresent()) {
                notes.add(Boolean.TRUE.equals(businessAllowance.get().getIsAuthorised())
                    ? replacePricePlaceHolder(businessNote.get().getAllow(), businessAllowance.get().getBudget())
                    : businessNote.get().getDeny());
              } else {
                notes.add(businessNote.get().getDeny());
              }
              aemIds.add(rule.getAemId());
            } else {
              var exception =  new PaymentException(
                  ErrorCode.DIGITAL_FORMATTING_BA_BUSINESS_NOTES_EXCEPTION, BUSINESS_NOTES_FORMATTING_ERROR);
              ExceptionLogger.log(log, exception);
              throw exception;
            }
          }
        });

    // ad the rest of the allowances if any provided
    businessAllowances.forEach(allowance ->
        businessAllowanceRules.stream()
            .filter(rule -> rule.getSourceId().equals(allowance.getAllowance())
                && BUSINESS_ALLOWANCE_RULE_ALLOWANCE_TYPE.equals(rule.getSourceType()))
            .findFirst()
            .ifPresent(businessAllowanceRule -> {
              if (!aemIds.contains(businessAllowanceRule.getAemId())) {
                var businessNote = businessNotesResponse.getBusinessNotes().stream()
                    .filter(note -> note.getId().equals(businessAllowanceRule.getAemId())).findFirst();
                if (businessNote.isPresent()) {
                  notes.add(Boolean.TRUE.equals(allowance.getIsAuthorised())
                      ? replacePricePlaceHolder(businessNote.get().getAllow(), allowance.getBudget())
                      : businessNote.get().getDeny());
                  aemIds.add(businessAllowanceRule.getAemId());
                } else {
                  var exception = new PaymentException(
                      ErrorCode.DIGITAL_FORMATTING_BA_PROV_BUSINESS_NOTES_EXCEPTION,
                      BUSINESS_NOTES_FORMATTING_ERROR);
                  ExceptionLogger.log(log, exception);
                  throw exception;
                }
              }
            })
    );
  }

  private static void addNotesForFooters(List<String> notes, BusinessNotesResponse businessNotesResponse) {
    if (Objects.nonNull(businessNotesResponse.getFooters())) {
      notes.addAll(businessNotesResponse.getFooters().stream().map(Note::getValue).toList());
    }
  }

  private static String replacePricePlaceHolder(String textWithPrice, BigDecimal actualPrice) {
    if (!Objects.isNull(actualPrice)) {
      return textWithPrice.replace("{price}", actualPrice.toString());
    }
    return textWithPrice;
  }

  private static String replaceCardTypePlaceHolder(String textWithCardType, String actualCardType) {
    return textWithCardType.replace("{cardType}", actualCardType);
  }

  public static List<BookingAllowance> buildBasketBookingAllowances(
      List<BusinessAllowance> businessAllowances) {
    return businessAllowances.stream()
        .filter(businessAllowance -> Boolean.TRUE.equals(businessAllowance.getIsAuthorised()))
        .map(businessAllowance -> BookingAllowance.builder()
            .allowance(businessAllowance.getAllowance())
            .budget(businessAllowance.getBudget() != null ? String.valueOf(businessAllowance.getBudget()) : null)
            .build())
        .toList();
  }

}
