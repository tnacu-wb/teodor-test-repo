package uk.co.whitbread.reservation.domain.logic.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.ACCOMMODATION_ALLOWANCE_NAME;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.reservation.domain.exceptions.BusinessItemsUpdateException;
import uk.co.whitbread.reservation.domain.model.in.BusinessAccountCnp;
import uk.co.whitbread.reservation.domain.model.in.BusinessAllowance;
import uk.co.whitbread.reservation.domain.model.in.BusinessItems;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;
import uk.co.whitbread.reservation.domain.model.out.BusinessAllowanceRule;
import uk.co.whitbread.reservation.domain.model.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.BusinessNote;
import uk.co.whitbread.reservation.domain.model.out.BusinessNotesResponse;
import uk.co.whitbread.reservation.domain.model.out.Note;

public class BusinessAllowanceUtilsTest {

  private static Map<Integer, String> mealsMap;
  private static List<BusinessAllowanceRule> businessAllowanceRules;
  private static BusinessNotesResponse businessNotesResponse;

  @BeforeAll
  public static void setUp() {
    mealsMap = buildMealsMap();
    businessAllowanceRules = buildBusinessAllowanceRules();
    businessNotesResponse = buildBusinessNotesResponse();
  }

  @Test
  void testBuildBusinessItemsFromBusinessAccountCnp_businessAccountNull_success() {
    //Act
    BusinessItems businessItems =
        BusinessAllowancesUtils.buildBusinessItemsFromBusinessAccountCnp(null, mealsMap);

    //Assert
    assertEquals(0, businessItems.getBusinessAllowances().size());
  }

  @Test
  void testBuildBusinessItemsFromBusinessAccount_allNull_success() {
    BusinessAccountCnp businessAccountCnp = BusinessAccountCnp.builder()
        .build();

    //Act
    BusinessItems businessItems =
        BusinessAllowancesUtils.buildBusinessItemsFromBusinessAccountCnp(businessAccountCnp,
            mealsMap);

    //Assert
    assertEquals(0, businessItems.getBusinessAllowances().size());
  }

  @Test
  void testBuildBusinessItemsFromBusinessAccount_allActive_success() {
    BusinessAccountCnp businessAccountCnp = BusinessAccountCnp.builder()
        .carParkingAllowed("Yes")
        .dinnerAllowance(BigDecimal.valueOf(50))
        .alcoholAllowed("Yes")
        .breakfastCodeReq(11)
        .wifiAllowed("Yes")
        .otherChargesAllowed("Yes")
        .build();

    //Act
    BusinessItems businessItems =
        BusinessAllowancesUtils.buildBusinessItemsFromBusinessAccountCnp(businessAccountCnp,
            mealsMap);

    //Assert
    assertEquals(6, businessItems.getBusinessAllowances().size());
    businessItems.getBusinessAllowances()
        .forEach(allowance -> assertTrue(allowance.getIsAuthorised()));
  }

  @Test
  void testGetBusinessItems_NoAllowancesOrPackagesSelected_success() {
    //Act
    BusinessItems businessItems = BusinessAllowancesUtils.getBusinessItems(null,
        businessAllowanceRules,
        businessNotesResponse, new ArrayList<>(), "PIBA");

    final var expectedBusinessNotes =
        "Please charge the PIBA card used to secure the reservation for all booked items which are included in the final total rate.\n"
            + "The following Allowances are to be charged to the PIBA card used to secure the reservation if authorised:\n"
            + "Dinner Allowance is NOT Authorised.\n"
            + "Alcohol is NOT Authorised with the evening meal.\n"
            + "Car Parking is NOT Authorised.\n"
            + "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.";

    //Assert
    assertEquals(1, businessItems.getBusinessAllowances().size());
    assertEquals(ACCOMMODATION_ALLOWANCE_NAME,
        businessItems.getBusinessAllowances().get(0).getAllowance());

    assertEquals(expectedBusinessNotes, businessItems.getBusinessNotes());
  }

  @Test
  void testGetBusinessItems_OnlyPackagesSelected_success() {
    //Act
    BusinessItems businessItems = BusinessAllowancesUtils.getBusinessItems(null,
        businessAllowanceRules,
        businessNotesResponse, List.of("BFADBF"), "PIBA");

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
    assertEquals(ACCOMMODATION_ALLOWANCE_NAME,
        businessItems.getBusinessAllowances().get(1).getAllowance());

    assertEquals(expectedBusinessNotes, businessItems.getBusinessNotes());
  }

  @Test
  void testGetBusinessItems_OnlyPackagesSelected_exception() {
    final var basketPackages = List.of("OBFBOX");

    //Act
    assertThrows(BusinessItemsUpdateException.class,
        () -> BusinessAllowancesUtils.getBusinessItems(null, businessAllowanceRules,
            businessNotesResponse,
            basketPackages, "PIBA"));
  }

  @Test
  void testGetBusinessItems_addNotesForBusinessAllowances_exception() {
    final var basketPackages = List.of("x");
    final var initialBusinessItems = BusinessItems.builder()
        .businessAllowances(List.of(
            BusinessAllowance.builder()
                .allowance("dinner")
                .budget(BigDecimal.valueOf(50))
                .isAuthorised(true)
                .build(),
            BusinessAllowance.builder()
                .allowance("alcohol")
                .budget(BigDecimal.ONE)
                .isAuthorised(false)
                .build(),
            BusinessAllowance.builder()
                .allowance("premierInnBreakfast")
                .budget(BigDecimal.ONE)
                .isAuthorised(true)
                .build())
        ).build();
    BusinessNotesResponse businessNotes = BusinessNotesResponse.builder()
        .businessNotes(List.of(
            BusinessNote.builder().id("carParking").lang("en").allow("Car Parking is Authorised.")
                .deny("Car Parking is NOT Authorised.")
                .build()))
        .cardTypes(List.of(
            Note.builder().id("card").value("Credit").build(),
            Note.builder().id("piba").value("PIBA").build()
        ))
        .build();
    //Act
    assertThrows(BusinessItemsUpdateException.class,
        () -> BusinessAllowancesUtils.getBusinessItems(initialBusinessItems, businessAllowanceRules,
            businessNotes,
            basketPackages, "PIBA"));
  }

  @Test
  void testGetBusinessItems_addNotesForBusinessAllowancesNotes_exception() {
    final var initialBusinessItems = BusinessItems.builder()
        .businessAllowances(List.of(
            BusinessAllowance.builder()
                .allowance("dinner")
                .budget(BigDecimal.valueOf(50))
                .isAuthorised(true)
                .build(),
            BusinessAllowance.builder()
                .allowance("alcohol")
                .budget(BigDecimal.ONE)
                .isAuthorised(false)
                .build(),
            BusinessAllowance.builder()
                .allowance("premierInnBreakfast")
                .budget(BigDecimal.ONE)
                .isAuthorised(true)
                .build())
        ).build();

    BusinessNotesResponse businessNotes = BusinessNotesResponse.builder()
        .businessNotes(List.of(
                BusinessNote.builder().id("carParking").lang("en").allow("Car Parking is Authorised.")
                    .deny("Car Parking is NOT Authorised.")
                    .build(),
                BusinessNote.builder().id("premierInnBreakfast2").lang("en")
                    .allow("Premier Inn Breakfast is Authorised.")
                    .deny("Premier Inn Breakfast is NOT Authorised.")
                    .build(),
                BusinessNote.builder().id("ultimateWifi").lang("en")
                    .allow("Wi-Fi Access is authorised.")
                    .deny("WiFi is NOT Authorised.")
                    .build(),
                BusinessNote.builder().id("dinner").lang("en")
                    .allow("{price} Dinner Allowance is Authorised.")
                    .deny("Dinner Allowance is NOT Authorised.")
                    .build(),
                BusinessNote.builder().id("alcohol")
                    .build(),
                BusinessNote.builder().id("otherCharges").lang("en").allow("Other charges "
                        + "(Travel Refresh Kits, Newspapers, etc.) are Authorised.")
                    .deny("Other charges (Travel Refresh Kits, Newspapers, etc.) are Not Authorised.")
                    .build()))
        .cardTypes(List.of(
            Note.builder().id("card").value("Credit").build(),
            Note.builder().id("piba").value("PIBA").build()
        ))
        .build();
    //Act
  assertThrows(BusinessItemsUpdateException.class,
        () -> BusinessAllowancesUtils.getBusinessItems(initialBusinessItems, businessAllowanceRules,
            businessNotes,            new ArrayList<>(),  "piba"));
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
                .budget(BigDecimal.ONE)
                .isAuthorised(false)
                .build(),
            BusinessAllowance.builder()
                .allowance("premierInnBreakfast")
                .budget(BigDecimal.ONE)
                .isAuthorised(true)
                .build())
        ).build();

    //Act
    BusinessItems businessItems = BusinessAllowancesUtils.getBusinessItems(initialBusinessItems,
        businessAllowanceRules,
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
    assertEquals("premierInnBreakfast",
        businessItems.getBusinessAllowances().get(2).getAllowance());
    assertTrue(businessItems.getBusinessAllowances().get(2).getIsAuthorised());
    assertEquals(ACCOMMODATION_ALLOWANCE_NAME,
        businessItems.getBusinessAllowances().get(3).getAllowance());
    assertTrue(businessItems.getBusinessAllowances().get(3).getIsAuthorised());

    assertEquals(expectedBusinessNotes, businessItems.getBusinessNotes());
  }

  @Test
  void testGetBusinessItems_samePackageAndAllowanceSelected_success() {
    final var initialBusinessItems = BusinessItems.builder()
        .businessAllowances(List.of(
            BusinessAllowance.builder()
                .allowance("premierInnBreakfast")
                .budget(BigDecimal.ONE)
                .isAuthorised(true)
                .build())
        ).build();

    //Act
    BusinessItems businessItems = BusinessAllowancesUtils.getBusinessItems(initialBusinessItems,
        businessAllowanceRules,
        businessNotesResponse, List.of("BFADBF"), "PIBA");

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
    assertEquals("premierInnBreakfast",
        businessItems.getBusinessAllowances().get(0).getAllowance());
    assertEquals("BFADBF", businessItems.getBusinessAllowances().get(1).getAllowance());
    assertEquals(ACCOMMODATION_ALLOWANCE_NAME,
        businessItems.getBusinessAllowances().get(2).getAllowance());

    assertEquals(expectedBusinessNotes, businessItems.getBusinessNotes());
  }

  @Test
  void testFilterPackageAllowancesAndGenerateBusinessItems_success() {
    final var bookingAllowances = buildBookingAllowances();
    final var businessAllowancesRules = buildBusinessAllowanceRules();

    //Act
    BusinessItems businessItems = BusinessAllowancesUtils.filterPackageAllowancesAndGenerateBusinessItems(
        bookingAllowances, businessAllowancesRules);

    //Assert
    assertEquals(2, businessItems.getBusinessAllowances().size());
    assertEquals("ultimateWifi", businessItems.getBusinessAllowances().get(0).getAllowance());
    assertEquals("carParking", businessItems.getBusinessAllowances().get(1).getAllowance());
  }

  @Test
  void testGetBusinessItems_cityTaxPackage_success() {
    final var initialBusinessItems = BusinessItems.builder()
        .businessAllowances(Collections.emptyList()).build();

    //Act
    BusinessItems businessItems = BusinessAllowancesUtils.getBusinessItems(initialBusinessItems,
        businessAllowanceRules,
        businessNotesResponse, List.of("BFADBF", "CITYTAX"), "PIBA");

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
    assertEquals(ACCOMMODATION_ALLOWANCE_NAME,
        businessItems.getBusinessAllowances().get(2).getAllowance());

    assertEquals(expectedBusinessNotes, businessItems.getBusinessNotes());
  }

  public static List<BusinessAllowanceRule> buildBusinessAllowanceRules() {
    return List.of(
        BusinessAllowanceRule.builder().sourceId("BFADBF").aemId("premierInnBreakfast")
            .sourceType("PACKAGE")
            .pms("PMS").isNotesMandatory(true).build(),
        BusinessAllowanceRule.builder().sourceId("OBFBOX").aemId("boxedBreakfast")
            .sourceType("PACKAGE")
            .pms("PMS").isNotesMandatory(true).build(),
        BusinessAllowanceRule.builder().sourceId("BFADCT").aemId("continentalBreakfast")
            .sourceType("PACKAGE")
            .pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("MD2DIN").aemId("continentalBreakfast")
            .sourceType("PACKAGE")
            .pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("MDBFST").aemId("mealDeal").sourceType("PACKAGE")
            .pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("MDBEVA").aemId("mealDeal").sourceType("PACKAGE")
            .pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("CITYTAX").aemId("cityTax").sourceType("PACKAGE")
            .pms("OP").targetId("CITY").isTransactionCode(false).isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("dinner").aemId("dinner").sourceType("ALLOWANCE")
            .pms("PMS").isNotesMandatory(true).build(),
        BusinessAllowanceRule.builder().sourceId("alcohol").aemId("alcohol").sourceType("ALLOWANCE")
            .pms("PMS").isNotesMandatory(true).build(),
        BusinessAllowanceRule.builder().sourceId("mealDeal").aemId("mealDeal")
            .sourceType("ALLOWANCE")
            .pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("continentalBreakfast")
            .aemId("continentalBreakfast")
            .sourceType("ALLOWANCE").pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("premierInnBreakfast").aemId("premierInnBreakfast")
            .sourceType("ALLOWANCE").pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("carParking").aemId("carParking")
            .sourceType("ALLOWANCE")
            .pms("PMS").isNotesMandatory(true).build(),
        BusinessAllowanceRule.builder().sourceId("otherCharges").aemId("otherCharges")
            .sourceType("ALLOWANCE")
            .pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("ultimateWifi").aemId("ultimateWifi")
            .sourceType("ALLOWANCE")
            .pms("PMS").isNotesMandatory(false).build(),
        BusinessAllowanceRule.builder().sourceId("accommodation").aemId("accommodation")
            .sourceType("ACCOMMODATION")
            .pms("PMS").isNotesMandatory(false).build());
  }

  public static BusinessAllowanceRuleResponse buildBusinessAllowanceRuleResponse() {
    return BusinessAllowanceRuleResponse.builder()
        .businessAllowances(buildBusinessAllowanceRules())
        .build();
  }

  public static BusinessNotesResponse buildBusinessNotesResponse() {
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
            BusinessNote.builder().id("premierInnBreakfast").lang("en")
                .allow("Premier Inn Breakfast is Authorised.")
                .deny("Premier Inn Breakfast is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("ultimateWifi").lang("en")
                .allow("Wi-Fi Access is authorised.")
                .deny("WiFi is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("dinner").lang("en")
                .allow("{price} Dinner Allowance is Authorised.")
                .deny("Dinner Allowance is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("alcohol").lang("en")
                .allow("Alcohol is Authorised with the evening meal.")
                .deny("Alcohol is NOT Authorised with the evening meal.")
                .build(),
            BusinessNote.builder().id("otherCharges").lang("en").allow("Other charges "
                    + "(Travel Refresh Kits, Newspapers, etc.) are Authorised.")
                .deny("Other charges (Travel Refresh Kits, Newspapers, etc.) are Not Authorised.")
                .build())
        )
        .packages(List.of(
            Note.builder().id("premierInnBreakfast")
                .value("Premier Inn Breakfast is Pre-Booked and Authorised.")
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

  private static Map<Integer, String> buildMealsMap() {

    final var mealsMapResult = new HashMap<Integer, String>();
    mealsMapResult.put(11, "PREMIER_BREAKFAST");
    mealsMapResult.put(12, "CONTINENTAL_BREAKFAST");
    mealsMapResult.put(17, "MEAL_DEAL");
    mealsMapResult.put(18, "BOXED_BREAKFAST");

    return mealsMapResult;
  }

  private static List<BookingAllowance> buildBookingAllowances() {
    return List.of(
        BookingAllowance.builder().allowance("BFADBF").build(),
        BookingAllowance.builder().allowance("MD2DIN").build(),
        BookingAllowance.builder().allowance("MDBFST").build(),
        BookingAllowance.builder().allowance("MDBEVA").build(),
        BookingAllowance.builder().allowance("ultimateWifi").build(),
        BookingAllowance.builder().allowance("carParking").build());
  }

}
