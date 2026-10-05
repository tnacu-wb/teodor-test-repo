package uk.co.whitbread.reservation.domain.logic.utils;

import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.ALLOWED;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.BUSINESS_ALLOWANCE_RULE_ALLOWANCE_TYPE;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.BUSINESS_ALLOWANCE_RULE_PACKAGE_TYPE;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.UNKNOWN_CARD_TYPE;

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
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.constants.HotelReservationConstants;
import uk.co.whitbread.reservation.domain.exceptions.BusinessItemsUpdateException;
import uk.co.whitbread.reservation.domain.model.in.BusinessAccountCnp;
import uk.co.whitbread.reservation.domain.model.in.BusinessAllowance;
import uk.co.whitbread.reservation.domain.model.in.BusinessItems;
import uk.co.whitbread.reservation.domain.model.index.in.Allowance;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;
import uk.co.whitbread.reservation.domain.model.out.BusinessAllowanceRule;
import uk.co.whitbread.reservation.domain.model.out.BusinessAllowanceSourceType;
import uk.co.whitbread.reservation.domain.model.out.BusinessNotesResponse;
import uk.co.whitbread.reservation.domain.model.out.Note;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BusinessAllowancesUtils {

  public static final String BUSINESS_NOTES_FORMATTING_ERROR = "Error while formatting business notes!";

  public static BusinessItems buildBusinessItemsFromBusinessAccountCnp(
      BusinessAccountCnp businessAccountCnp,
      Map<Integer, String> mealsMap) {

    final var businessAllowances = new ArrayList<BusinessAllowance>();

    if (businessAccountCnp != null) {
      if (Optional.ofNullable(businessAccountCnp.getDinnerAllowance()).isPresent()) {
        businessAllowances.add(
            BusinessAllowance.builder().allowance(Allowance.DINNER.getCode())
                .budget(businessAccountCnp.getDinnerAllowance()).isAuthorised(Boolean.TRUE)
                .build());
      }
      if (Optional.ofNullable(businessAccountCnp.getBreakfastCodeReq()).isPresent()) {
        businessAllowances.add(
            BusinessAllowance.builder()
                .allowance(Allowance.valueOf(mealsMap.get(businessAccountCnp.getBreakfastCodeReq()))
                    .getCode())
                .isAuthorised(Boolean.TRUE).build());
      }
      if (Optional.ofNullable(businessAccountCnp.getAlcoholAllowed()).isPresent()) {
        businessAllowances.add(
            BusinessAllowance.builder().allowance(Allowance.ALLOW_ALCOHOL.getCode())
                .isAuthorised(ALLOWED.equalsIgnoreCase(businessAccountCnp.getAlcoholAllowed()))
                .build());
      }
      if (Optional.ofNullable(businessAccountCnp.getCarParkingAllowed()).isPresent()) {
        businessAllowances.add(
            BusinessAllowance.builder().allowance(Allowance.CAR_PARKING.getCode())
                .isAuthorised(ALLOWED.equalsIgnoreCase(businessAccountCnp.getCarParkingAllowed()))
                .build());
      }
      if (Optional.ofNullable(businessAccountCnp.getWifiAllowed()).isPresent()) {
        businessAllowances.add(
            BusinessAllowance.builder().allowance(Allowance.ULTIMATE_WIFI.getCode())
                .isAuthorised(ALLOWED.equalsIgnoreCase(businessAccountCnp.getWifiAllowed()))
                .build());
      }
      if (Optional.ofNullable(businessAccountCnp.getOtherChargesAllowed()).isPresent()) {
        businessAllowances.add(
            BusinessAllowance.builder().allowance(Allowance.ADDITIONAL_CHARGES.getCode())
                .isAuthorised(ALLOWED.equalsIgnoreCase(businessAccountCnp.getOtherChargesAllowed()))
                .build());
      }
    }

    return BusinessItems.builder().businessAllowances(businessAllowances)
        .customReferenceNumber(
            businessAccountCnp != null ? businessAccountCnp.getCustomerReference() : null)
        .purchaseOrderNumber(
            businessAccountCnp != null ? businessAccountCnp.getPurchaseOrder() : null)
        .build();
  }

  public static BusinessItems getBusinessItems(BusinessItems initialBusinessItems,
      List<BusinessAllowanceRule> businessAllowanceRules,
      BusinessNotesResponse businessNotesResponse, List<String> rsvPackageCodes,
      String cardType) {

    final var finalBusinessItems = initialBusinessItems == null ? BusinessItems.builder().build() :
        BusinessItems.builder()
            .customReferenceNumber(initialBusinessItems.getCustomReferenceNumber())
            .purchaseOrderNumber(initialBusinessItems.getPurchaseOrderNumber())
            .businessAllowances(initialBusinessItems.getBusinessAllowances())
            .businessNotes(initialBusinessItems.getBusinessNotes())
            .build();

    // add business allowances for packages
    final List<BusinessAllowance> finalBusinessAllowances =
        Objects.nonNull(finalBusinessItems.getBusinessAllowances())
            ? new ArrayList<>(finalBusinessItems.getBusinessAllowances()) : new ArrayList<>();

    final var allowancesWithSourceTypePackage = getAllowancesWithSourceTypePackage(businessAllowanceRules);

    rsvPackageCodes.forEach(packageCode -> {
      if (allowancesWithSourceTypePackage.contains(packageCode)) {
        finalBusinessAllowances.add(BusinessAllowance.builder()
            .allowance(packageCode)
            .isAuthorised(true)
            .build());
      }
    });

    // add business allowance for accommodation
    finalBusinessAllowances.add(BusinessAllowance.builder()
        .allowance(HotelReservationConstants.ACCOMMODATION_ALLOWANCE_NAME)
        .isAuthorised(true)
        .build());

    finalBusinessItems.setBusinessAllowances(finalBusinessAllowances);

    finalBusinessItems.setBusinessNotes(modelBusinessNotes(businessNotesResponse,
        finalBusinessAllowances, businessAllowanceRules, cardType));

    return finalBusinessItems;
  }

  public static BusinessItems filterPackageAllowancesAndGenerateBusinessItems(
      List<BookingAllowance> bookingAllowances,
      List<BusinessAllowanceRule> businessAllowanceRules) {
    var packageAllowances = getAllowancesWithSourceTypePackage(businessAllowanceRules);

    var businessAllowances = bookingAllowances
        .stream()
        .filter(bookingAllowance -> !packageAllowances.contains(bookingAllowance.getAllowance()))
        .map(bookingAllowance -> BusinessAllowance.builder()
            .allowance(bookingAllowance.getAllowance())
            .budget(bookingAllowance.getBudget() != null ? bookingAllowance.getBudget()
                : BigDecimal.ZERO)
            .isAuthorised(true)
            .build())
        .toList();

    return BusinessItems.builder()
        .businessAllowances(businessAllowances)
        .build();
  }

  private static List<String> getAllowancesWithSourceTypePackage(
      List<BusinessAllowanceRule> businessAllowances) {
    return businessAllowances
        .stream()
        .filter(businessAllowanceRule -> BusinessAllowanceSourceType.PACKAGE.name()
            .equals(businessAllowanceRule.getSourceType()))
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
            .filter(cardTypeValue -> paymentType.equalsIgnoreCase(cardTypeValue.getId()))
            .findFirst()
            .map(Note::getValue).orElse(UNKNOWN_CARD_TYPE);

    // headers
    addNotesForHeaders(notes, businessNotesResponse, cardType);

    //allowances headers
    addNotesForAllowancesHeaders(notes, businessNotesResponse, cardType);

    final var aemIds = new ArrayList<String>();

    // allowances for packages added within reservation
    addNotesForPackages(notes, aemIds, businessNotesResponse, businessAllowances,
        businessAllowanceRules);

    // allowances for business items selected
    addNotesForBusinessAllowances(notes, aemIds, businessNotesResponse, businessAllowances,
        businessAllowanceRules);

    // footers
    addNotesForFooters(notes, businessNotesResponse);

    return StringUtils.join(notes, "\n");
  }

  private static void addNotesForHeaders(List<String> notes,
      BusinessNotesResponse businessNotesResponse,
      String cardType) {
    if (Objects.nonNull(businessNotesResponse.getHeaders())) {
      notes.addAll(businessNotesResponse.getHeaders().stream().map(Note::getValue)
          .map(str -> replaceCardTypePlaceHolder(str, cardType)).toList());
    }
  }

  private static void addNotesForAllowancesHeaders(List<String> notes,
      BusinessNotesResponse businessNotesResponse,
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
                    var exception = new BusinessItemsUpdateException(
                            ErrorCode.DIGITAL_BUSINESS_NOTES_FORMATTING_EXCEPTION, BUSINESS_NOTES_FORMATTING_ERROR);
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
        .filter(rule -> rule.isNotesMandatory() && BUSINESS_ALLOWANCE_RULE_ALLOWANCE_TYPE.equals(
            rule.getSourceType()))
        .forEach(rule -> {
          if (!aemIds.contains(rule.getAemId())) {
            var businessNote = businessNotesResponse.getBusinessNotes().stream()
                .filter(note -> note.getId().equals(rule.getAemId())).findFirst();
            if (businessNote.isPresent()) {
              final var businessAllowance =
                  businessAllowances.stream()
                      .filter(allowance -> rule.getSourceId().equals(allowance.getAllowance()))
                      .findFirst();
              if (businessAllowance.isPresent()) {
                notes.add(Boolean.TRUE.equals(businessAllowance.get().getIsAuthorised())
                    ? replacePricePlaceHolder(businessNote.get().getAllow(),
                    businessAllowance.get().getBudget())
                    : businessNote.get().getDeny());
              } else {
                notes.add(businessNote.get().getDeny());
              }
              aemIds.add(rule.getAemId());
            } else {
              var exception = new BusinessItemsUpdateException(
                      ErrorCode.DIGITAL_BUSINESS_NOTES_FORMATTING2_EXCEPTION, BUSINESS_NOTES_FORMATTING_ERROR);
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
                    .filter(note -> note.getId().equals(businessAllowanceRule.getAemId()))
                    .findFirst();
                if (businessNote.isPresent()) {
                  notes.add(Boolean.TRUE.equals(allowance.getIsAuthorised())
                      ? replacePricePlaceHolder(businessNote.get().getAllow(),
                      allowance.getBudget())
                      : businessNote.get().getDeny());
                  aemIds.add(businessAllowanceRule.getAemId());
                } else {

                  var exception = new BusinessItemsUpdateException(
                          ErrorCode.DIGITAL_BUSINESS_NOTES_FORMATTING3_EXCEPTION, BUSINESS_NOTES_FORMATTING_ERROR);
                  ExceptionLogger.log(log, exception);
                  throw exception;
                }
              }
            })
    );
  }

  private static void addNotesForFooters(List<String> notes,
      BusinessNotesResponse businessNotesResponse) {
    if (Objects.nonNull(businessNotesResponse.getFooters())) {
      notes.addAll(businessNotesResponse.getFooters().stream().map(Note::getValue).toList());
    }
  }

  private static String replacePricePlaceHolder(String textWithPrice, BigDecimal actualPrice) {
    return actualPrice == null ? textWithPrice
        : textWithPrice.replace("{price}", actualPrice.toString());
  }

  private static String replaceCardTypePlaceHolder(String textWithCardType, String actualCardType) {
    return textWithCardType.replace("{cardType}", actualCardType);
  }

}
