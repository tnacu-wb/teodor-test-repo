package uk.co.whitbread.dashboard.domain.logic.utils;


import static org.mockito.Mockito.when;
import static uk.co.whitbread.dashboard.domain.logic.utils.ActionsUtils.DE_LANGUAGE_CODE;
import static uk.co.whitbread.dashboard.domain.logic.utils.ActionsUtils.EN_LANGUAGE_CODE;

import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.ResultsDto;
import uk.co.whitbread.dashboard.domain.model.in.ManageBookingResponse;
import uk.co.whitbread.dashboard.domain.model.out.Content;
import uk.co.whitbread.dashboard.domain.model.out.Map;
import uk.co.whitbread.dashboard.domain.properties.DashboardProperties;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ActionsUtilsTest {

  @Mock
  private DashboardProperties properties;

  @Test
  void testAssignActionsWithDifferentLanguage() {
    final Content content = new Content();
    content.setMap(new Map());

    ActionsUtils.assignActions(content, properties, new ManageBookingResponse(), false,
        EN_LANGUAGE_CODE, "CHECKED_IN");

    Assertions.assertFalse(content.getActions().isEmpty());
    Assertions.assertEquals(2, content.getActions().size());
  }

  @Test
  void testAssignActionsWithEmptyReservation() {
    final Content content = new Content();
    final ResultsDto reservationDetails = new ResultsDto();

    var manageBookingResponse = new ManageBookingResponse();
    manageBookingResponse.setIsCheckInOnlineAvailable(true);

    ActionsUtils.assignActions(content, properties, manageBookingResponse, false, EN_LANGUAGE_CODE,
        reservationDetails.getStatus());

    Assertions.assertFalse(content.getActions().isEmpty());
  }

  @Test
  void testShouldNotDisplayMealsWhenBreakfastListEmpty() {
    var manageBookingResponse = new ManageBookingResponse();
    manageBookingResponse.setIsAmendable(true);

    final boolean shouldDisplayMeals = ActionsUtils.shouldDisplayMeals(manageBookingResponse);
    Assertions.assertTrue(shouldDisplayMeals);
  }

  @Test
  void testGetActionMappingWithFrenchLanguage() {
    final String FR_LANGUAGE_CODE = "fr";
    final HashMap<String, HashMap<String, String>> actionMap = getActionMapping(FR_LANGUAGE_CODE);

    Assertions.assertTrue(actionMap.containsKey(FR_LANGUAGE_CODE));
    Assertions.assertEquals("Check in", actionMap.get(FR_LANGUAGE_CODE).get("CIOL"));
  }

  @Test
  void testAssignActionsWithNullContent() {
    final ManageBookingResponse manageBookingResponse = new ManageBookingResponse();

    Assertions.assertThrows(NullPointerException.class,
        () -> ActionsUtils.assignActions(null, properties, manageBookingResponse, false,
            EN_LANGUAGE_CODE, "")
    );
  }

  @Test
  void testAssignActionsWithNullProperties() {
    final Content content = new Content();
    final ManageBookingResponse manageBookingResponse = new ManageBookingResponse();

    Assertions.assertThrows(NullPointerException.class,
        () -> ActionsUtils.assignActions(content, null, manageBookingResponse, true,
            EN_LANGUAGE_CODE, "PRE_CHECKED_IN")
    );
  }

  @BeforeEach
  void setUp() {
    when(properties.getActionMapping()).thenReturn(getActionMapping(EN_LANGUAGE_CODE));
  }

  private HashMap<String, HashMap<String, String>> getActionMapping(final String countryCode) {
    final HashMap<String, HashMap<String, String>> actionMap = new HashMap<>();
    final HashMap<String, String> map = new HashMap<>();
    if (countryCode.equals(DE_LANGUAGE_CODE)) {
      map.put("CIOL", "Check in");
      map.put("UPSELLS", "Mahlzeiten oder Extras hinzufügen");
      map.put("DIRECTIONS", "Hotelanfahrt anzeigen");
      map.put("BOOKING_DETAILS", "Buchungsdetails anzeigen");
    } else {
      map.put("CIOL", "Check in");
      map.put("UPSELLS", "Add meals or extras");
      map.put("DIRECTIONS", "Show hotel directions");
      map.put("BOOKING_DETAILS", "View booking details");
    }
    actionMap.put(countryCode, map);
    return actionMap;
  }

}