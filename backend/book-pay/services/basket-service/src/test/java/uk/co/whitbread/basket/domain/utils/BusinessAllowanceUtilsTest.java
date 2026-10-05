package uk.co.whitbread.basket.domain.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.ACCOMMODATION_ALLOWANCE_NAME;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.logic.utils.BusinessAllowancesUtils;
import uk.co.whitbread.basket.domain.model.business.in.BusinessAllowance;
import uk.co.whitbread.basket.domain.model.business.in.BusinessItems;
import uk.co.whitbread.basket.domain.model.content.out.BusinessNote;
import uk.co.whitbread.basket.domain.model.content.out.BusinessNotesResponse;
import uk.co.whitbread.basket.domain.model.content.out.Note;
import uk.co.whitbread.basket.domain.model.payments.in.Allowance;
import uk.co.whitbread.basket.domain.model.payments.in.BusinessAccount;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceRule;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BusinessAllowancesUtils.class)
public class BusinessAllowanceUtilsTest {

  private static Map<Integer, Allowance> mealsMap;
  private static List<BusinessAllowanceRule> businessAllowanceRules;
  private static BusinessNotesResponse businessNotesResponse;

  @BeforeAll
  public static void setUp() {
    mealsMap = buildMealsMap();
    businessAllowanceRules = buildBusinessAllowanceRules();
    businessNotesResponse = buildBusinessNotesResponse();
  }

  @Test
  void testBuildBusinessItemsFromBusinessAccount_businessAccountNull_success() {
    //Act
    BusinessItems businessItems =
        BusinessAllowancesUtils.buildBusinessItemsFromBusinessAccount(null, mealsMap);

    //Assert
    assertEquals(0, businessItems.getBusinessAllowances().size());
  }

  @Test
  void testBuildBusinessItemsFromBusinessAccount_allNull_success() {
    BusinessAccount businessAccount = BusinessAccount.builder()
        .build();

    //Act
    BusinessItems businessItems =
        BusinessAllowancesUtils.buildBusinessItemsFromBusinessAccount(businessAccount, mealsMap);

    //Assert
    assertEquals(0, businessItems.getBusinessAllowances().size());
  }

  @Test
  void testBuildBusinessItemsFromBusinessAccount_allActive_success() {
    BusinessAccount businessAccount = BusinessAccount.builder()
        .carParkingAllowed("Yes")
        .dinnerAllowance(BigDecimal.valueOf(50))
        .alcoholAllowed("Yes")
        .breakfastCodeReq(11)
        .wifiAllowed("Yes")
        .otherChargesAllowed("Yes")
        .build();

    //Act
    BusinessItems businessItems =
        BusinessAllowancesUtils.buildBusinessItemsFromBusinessAccount(businessAccount, mealsMap);

    //Assert
    assertEquals(6, businessItems.getBusinessAllowances().size());
    businessItems.getBusinessAllowances().forEach(allowance -> assertTrue(allowance.getIsAuthorised()));
  }

  @Test
  void testGetBusinessItems_NoAllowancesOrPackagesSelected_success() {
    List packageCodes = new ArrayList<>();
    //Act
    BusinessItems businessItems = BusinessAllowancesUtils.getBusinessItems(null, businessAllowanceRules,
        businessNotesResponse, packageCodes, "PIBA");

    final var expectedBusinessNotes =
        "Please charge the PIBA card used to secure the reservation for all booked items which are included in the final total rate.\n"
        + "The following Allowances are to be charged to the PIBA card used to secure the reservation if authorised:\n"
        + "Dinner Allowance is NOT Authorised.\n"
        + "Alcohol is NOT Authorised with the evening meal.\n"
        + "Car Parking is NOT Authorised.\n"
        + "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.";

    //Assert
    assertEquals(1, businessItems.getBusinessAllowances().size());
    assertEquals(ACCOMMODATION_ALLOWANCE_NAME, businessItems.getBusinessAllowances().get(0).getAllowance());

    assertEquals(expectedBusinessNotes, businessItems.getBusinessNotes());
  }

  @Test
  void testGetBusinessItems_OnlyPackagesSelected_success() {
    List packageCodes = List.of("BFADBF");
    //Act
    BusinessItems businessItems = BusinessAllowancesUtils.getBusinessItems(null,
        businessAllowanceRules,
        businessNotesResponse, packageCodes, "PIBA");

    final var expectedBusinessNotes =
        "Please charge the PIBA card used to secure the reservation for all booked items which are included in the final total rate.\n"
            + "The following Allowances are to be charged to the PIBA card used to secure the reservation if authorised:\n"
            + "Premier Inn Breakfast is Pre-Booked and Authorised.\n"
            + "Dinner Allowance is NOT Authorised.\n"
            + "Alcohol is NOT Authorised with the evening meal.\n"
            + "Car Parking is NOT Authorised.\n"
            + "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.";

    //Assert
    assertEquals(2, businessItems.getBusinessAllowances().size());
    assertEquals("BFADBF", businessItems.getBusinessAllowances().get(0).getAllowance());
    assertEquals(ACCOMMODATION_ALLOWANCE_NAME, businessItems.getBusinessAllowances().get(1).getAllowance());

    assertEquals(expectedBusinessNotes, businessItems.getBusinessNotes());
  }

  @Test
  void testGetBusinessItems_OnlyPackagesSelected_fail() {
    final var basketPackages = List.of("OBFBOX");

    //Act
    var exception = assertThrows(PaymentException.class,
        () -> BusinessAllowancesUtils.getBusinessItems(null, businessAllowanceRules, businessNotesResponse,
            basketPackages, "PIBA"));
    //Assert
    assertEquals(ErrorCode.DIGITAL_FORMATTING_BUSINESS_NOTES_EXCEPTION.getCode(), exception.getErrorCode());
    assertEquals("Error while formatting business notes!", exception.getMessage());
  }

  @Test
  void testGetBusinessItems_OnlyAllowanceSelectedAuthorisedAndDenied_success() {
    final var initialBusinessItems = BusinessItems.builder()
        .businessAllowances(List.of(
            BusinessAllowance.builder()
                .allowance("dinner")
                .budget(BigDecimal.valueOf(50))
                .isAuthorised(true)
                .build(),
            BusinessAllowance.builder()
                .allowance("alcohol")
                .isAuthorised(false)
                .build(),
            BusinessAllowance.builder()
                .allowance("premierInnBreakfast")
                .isAuthorised(true)
                .build())
        ).build();

    //Act
    BusinessItems businessItems = BusinessAllowancesUtils.getBusinessItems(initialBusinessItems, businessAllowanceRules,
        businessNotesResponse, new ArrayList<>(), "PIBA");

    final var expectedBusinessNotes =
        "Please charge the PIBA card used to secure the reservation for all booked items which are included in the final total rate.\n"
            + "The following Allowances are to be charged to the PIBA card used to secure the reservation if authorised:\n"
            + "50 Dinner Allowance is Authorised.\n"
            + "Alcohol is NOT Authorised with the evening meal.\n"
            + "Car Parking is NOT Authorised.\n"
            + "Premier Inn Breakfast is Authorised.\n"
            + "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.";

    //Assert
    assertEquals(4, businessItems.getBusinessAllowances().size());
    assertEquals("dinner", businessItems.getBusinessAllowances().get(0).getAllowance());
    assertTrue(businessItems.getBusinessAllowances().get(0).getIsAuthorised());
    assertEquals("alcohol", businessItems.getBusinessAllowances().get(1).getAllowance());
    assertFalse(businessItems.getBusinessAllowances().get(1).getIsAuthorised());
    assertEquals("premierInnBreakfast", businessItems.getBusinessAllowances().get(2).getAllowance());
    assertTrue(businessItems.getBusinessAllowances().get(2).getIsAuthorised());
    assertEquals(ACCOMMODATION_ALLOWANCE_NAME, businessItems.getBusinessAllowances().get(3).getAllowance());
    assertTrue(businessItems.getBusinessAllowances().get(3).getIsAuthorised());

    assertEquals(expectedBusinessNotes, businessItems.getBusinessNotes());
  }
  @Test
  void testGetBusinessItems_samePackageAndAllowanceSelected_success() {

    final List packageCodes = List.of("BFADBF");
    final var initialBusinessItems = BusinessItems.builder()
        .businessAllowances(List.of(
            BusinessAllowance.builder()
                .allowance("premierInnBreakfast")
                .isAuthorised(true)
                .build())
        ).build();

    //Act
    BusinessItems businessItems = BusinessAllowancesUtils.getBusinessItems(initialBusinessItems, businessAllowanceRules,
        businessNotesResponse, packageCodes, "PIBA");

    final var expectedBusinessNotes =
        "Please charge the PIBA card used to secure the reservation for all booked items which are included in the final total rate.\n"
            + "The following Allowances are to be charged to the PIBA card used to secure the reservation if authorised:\n"
            + "Premier Inn Breakfast is Pre-Booked and Authorised.\n"
            + "Dinner Allowance is NOT Authorised.\n"
            + "Alcohol is NOT Authorised with the evening meal.\n"
            + "Car Parking is NOT Authorised.\n"
            + "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.";

    //Assert
    assertEquals(3, businessItems.getBusinessAllowances().size());
    assertEquals("premierInnBreakfast", businessItems.getBusinessAllowances().get(0).getAllowance());
    assertEquals("BFADBF", businessItems.getBusinessAllowances().get(1).getAllowance());
    assertEquals(ACCOMMODATION_ALLOWANCE_NAME, businessItems.getBusinessAllowances().get(2).getAllowance());

    assertEquals(expectedBusinessNotes, businessItems.getBusinessNotes());
  }

  @Test
  void testGetBusinessItems_cityTaxPackage_success() {
    final var initialBusinessItems = BusinessItems.builder()
        .businessAllowances(Collections.emptyList()).build();
    final List packageCodes = List.of("BFADBF", "CITYTAX");
    //Act
    BusinessItems businessItems = BusinessAllowancesUtils.getBusinessItems(initialBusinessItems,
        businessAllowanceRules,
        businessNotesResponse, packageCodes, "PIBA");

    final var expectedBusinessNotes =
        "Please charge the PIBA card used to secure the reservation for all booked items which are included in the final total rate.\n"
            + "The following Allowances are to be charged to the PIBA card used to secure the reservation if authorised:\n"
            + "Premier Inn Breakfast is Pre-Booked and Authorised.\n"
            + "Dinner Allowance is NOT Authorised.\n"
            + "Alcohol is NOT Authorised with the evening meal.\n"
            + "Car Parking is NOT Authorised.\n"
            + "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.";

    //Assert
    assertEquals(3, businessItems.getBusinessAllowances().size());
    assertEquals("BFADBF", businessItems.getBusinessAllowances().get(0).getAllowance());
    assertEquals("CITYTAX", businessItems.getBusinessAllowances().get(1).getAllowance());
    assertEquals(ACCOMMODATION_ALLOWANCE_NAME, businessItems.getBusinessAllowances().get(2).getAllowance());

    assertEquals(expectedBusinessNotes, businessItems.getBusinessNotes());
  }

  @ParameterizedTest
  @ValueSource(strings = {"PIBA_UK", "PIBA_EU"})
  void testGetBusinessItems_CcuiPibaCardTypes_success(String cardType) {
    final List packageCodes = new ArrayList<>();
    //Act
    BusinessItems businessItems = BusinessAllowancesUtils.getBusinessItems(null,
        businessAllowanceRules,
        businessNotesResponse, packageCodes, cardType);

    final var expectedBusinessNotes =
        "Please charge the PIBA card used to secure the reservation for all booked items which are included in the final total rate.\n"
            + "The following Allowances are to be charged to the PIBA card used to secure the reservation if authorised:\n"
            + "Dinner Allowance is NOT Authorised.\n"
            + "Alcohol is NOT Authorised with the evening meal.\n"
            + "Car Parking is NOT Authorised.\n"
            + "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.";

    //Assert
    assertEquals(1, businessItems.getBusinessAllowances().size());
    assertEquals(ACCOMMODATION_ALLOWANCE_NAME, businessItems.getBusinessAllowances().get(0).getAllowance());

    assertEquals(expectedBusinessNotes, businessItems.getBusinessNotes());
  }

  @Test
  void testGetBusinessItems_NoNotesForBusinessAllowances_fail() {
    BusinessNotesResponse response = buildBusinessNotesResponse();
    response.setBusinessNotes(List.of());
    final List packageCodes = List.of("BFADBF");
    //Act
    var exception = assertThrows(PaymentException.class,
        () -> BusinessAllowancesUtils.getBusinessItems(null, businessAllowanceRules, response,
            packageCodes, "PIBA"));
    //Assert
    assertEquals(ErrorCode.DIGITAL_FORMATTING_BA_BUSINESS_NOTES_EXCEPTION.getCode(), exception.getErrorCode());
    assertEquals("Error while formatting business notes!", exception.getMessage());
  }


  private static Map<Integer, Allowance> buildMealsMap() {

    final var mealsMapResult = new HashMap<Integer, Allowance>();
    mealsMapResult.put(11, Allowance.PREMIER_BREAKFAST);
    mealsMapResult.put(12, Allowance.CONTINENTAL_BREAKFAST);
    mealsMapResult.put(17, Allowance.MEAL_DEAL);
    mealsMapResult.put(18, Allowance.BOXED_BREAKFAST);

    return mealsMapResult;
  }

  private static List<BusinessAllowanceRule> buildBusinessAllowanceRules() {
    return List.of(
        BusinessAllowanceRule.builder().sourceId("BFADBF").aemId("premierInnBreakfast").sourceType("PACKAGE")
            .pms("PMS").isNotesMandatory(true).build(),
        BusinessAllowanceRule.builder().sourceId("OBFBOX").aemId("boxedBreakfast").sourceType("PACKAGE")
            .pms("PMS").isNotesMandatory(true).build(),
        BusinessAllowanceRule.builder().sourceId("BFADCT").aemId("continentalBreakfast").sourceType("PACKAGE")
            .pms("PMS").isNotesMandatory(true).build(),
        BusinessAllowanceRule.builder().sourceId("MD2DIN").aemId("continentalBreakfast").sourceType("PACKAGE")
            .pms("PMS").isNotesMandatory(true).build(),
        BusinessAllowanceRule.builder().sourceId("MDBFST").aemId("mealDeal").sourceType("PACKAGE")
            .pms("PMS").isNotesMandatory(true).build(),
        BusinessAllowanceRule.builder().sourceId("MDBEVA").aemId("mealDeal").sourceType("PACKAGE")
            .pms("PMS").isNotesMandatory(true).build(),
        BusinessAllowanceRule.builder().sourceId("CITYTAX").aemId("cityTax").sourceType("PACKAGE")
            .pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("dinner").aemId("dinner").sourceType("ALLOWANCE")
            .pms("PMS").isNotesMandatory(true).build(),
        BusinessAllowanceRule.builder().sourceId("alcohol").aemId("alcohol").sourceType("ALLOWANCE")
            .pms("PMS").isNotesMandatory(true).build(),
        BusinessAllowanceRule.builder().sourceId("mealDeal").aemId("mealDeal").sourceType("ALLOWANCE")
            .pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("continentalBreakfast").aemId("continentalBreakfast")
            .sourceType("ALLOWANCE").pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("premierInnBreakfast").aemId("premierInnBreakfast")
            .sourceType("ALLOWANCE").pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("carParking").aemId("carParking").sourceType("ALLOWANCE")
            .pms("PMS").isNotesMandatory(true).build(),
        BusinessAllowanceRule.builder().sourceId("otherCharges").aemId("otherCharges").sourceType("ALLOWANCE")
            .pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("ultimateWifi").aemId("ultimateWifi").sourceType("ALLOWANCE")
            .pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("accommodation").aemId("accommodation").sourceType("ACCOMMODATION")
            .pms("PMS").isNotesMandatory(false).build());
  }

  private static BusinessNotesResponse buildBusinessNotesResponse() {
    return BusinessNotesResponse.builder()
        .headers(List.of(
            Note.builder().id("authorizedCharges").value(
                "Please charge the {cardType} card used to secure the reservation for all booked items which are "
                    + "included in the final total rate.").build())
        )
        .allowances(List.of(
            Note.builder().id("authorizedAllowances").value(
                "The following Allowances are to be charged to the {cardType} card used to secure the reservation "
                    + "if authorised:").build())
        )
        .businessNotes(List.of(
            BusinessNote.builder().id("carParking").lang("en").allow("Car Parking is Authorised.")
                .deny("Car Parking is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("premierInnBreakfast").lang("en").allow("Premier Inn Breakfast is Authorised.")
                .deny("Premier Inn Breakfast is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("ultimateWifi").lang("en").allow("Wi-Fi Access is authorised.")
                .deny("WiFi is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("dinner").lang("en").allow("{price} Dinner Allowance is Authorised.")
                .deny("Dinner Allowance is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("alcohol").lang("en").allow("Alcohol is Authorised with the evening meal.")
                .deny("Alcohol is NOT Authorised with the evening meal.")
                .build(),
            BusinessNote.builder().id("otherCharges").lang("en").allow("Other charges "
                    + "(Travel Refresh Kits, Newspapers, etc.) are Authorised.")
                .deny("Other charges (Travel Refresh Kits, Newspapers, etc.) are Not Authorised.")
                .build())
        )
        .packages(List.of(
            Note.builder().id("premierInnBreakfast").value("Premier Inn Breakfast is Pre-Booked and Authorised.")
                .build())
        )
        .cardTypes(List.of(
                Note.builder().id("card").value("Credit").build(),
                Note.builder().id("piba").value("PIBA").build()
            )
        )
        .footers(
            List.of(
                Note.builder().id("cnpAuthorization").value(
                        "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.")
                    .build()
            )
        )
        .build();
  }

}
