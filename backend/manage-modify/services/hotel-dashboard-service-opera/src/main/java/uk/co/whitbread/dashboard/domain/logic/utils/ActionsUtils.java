package uk.co.whitbread.dashboard.domain.logic.utils;

import static java.lang.Boolean.TRUE;
import static uk.co.whitbread.dashboard.domain.model.out.ActionType.BOOKING_DETAILS;
import static uk.co.whitbread.dashboard.domain.model.out.ActionType.CIOL;
import static uk.co.whitbread.dashboard.domain.model.out.ActionType.DIRECTIONS;
import static uk.co.whitbread.dashboard.domain.model.out.ActionType.UPSELLS;

import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.dashboard.domain.model.in.ManageBookingResponse;
import uk.co.whitbread.dashboard.domain.model.out.Action;
import uk.co.whitbread.dashboard.domain.model.out.Content;
import uk.co.whitbread.dashboard.domain.properties.DashboardProperties;

@Slf4j
public class ActionsUtils {

  public static final String DE_LANGUAGE_CODE = "de";
  public static final String EN_LANGUAGE_CODE = "en";
  public static final String GB_COUNTRY_CODE = "gb";
  public static final String DE_COUNTRY_CODE = "de";


  private ActionsUtils() {
  }

  public static void assignActions(final Content content, final DashboardProperties properties,
      ManageBookingResponse manageBookingResponse, final boolean isBusiness, final String language, String status) {

    content.setActions(new ArrayList<>());
    final String lowerCaseLanguageCode = validateLanguage(language).toLowerCase();
    if (shouldAddCiolAction(manageBookingResponse, isBusiness, status)) {
      addCiol(content, properties, lowerCaseLanguageCode);
    }

    if (shouldDisplayMeals(manageBookingResponse) && !isBusiness) {
      addMeals(content, properties, lowerCaseLanguageCode);
    }

    if (content.getActions().size() < 2 && content.getMap() != null) {
      addDirections(content, properties, lowerCaseLanguageCode);
    }

    addBookingDetails(content, properties, lowerCaseLanguageCode);

  }

  private static boolean shouldAddCiolAction(ManageBookingResponse manageBookingResponse, boolean isBusiness,
      String status) {
    return TRUE.equals(manageBookingResponse.getIsCheckInOnlineAvailable()) && !DashboardUtils.isCheckedIn(status)
        && !DashboardUtils.isCiolPerformed(status) && !isBusiness;
  }

  private static String validateLanguage(final String language) {
    if (StringUtils.isEmpty(language)) {
      log.warn("Language is null or empty, defaulting to " + EN_LANGUAGE_CODE);
      return EN_LANGUAGE_CODE;
    } else if (language.equals(DE_LANGUAGE_CODE)) {
      return DE_LANGUAGE_CODE;
    }
    return EN_LANGUAGE_CODE;
  }

  /**
   * Historically (BART) this method was based on the following conditions:
   * noBreakfasts && notCheckedIn && notRestricted && upsellItemsAvailableNotEmpty;
   * noBreakfasts: From CDH we need to get the upsells to find this out
   * notCheckedIn: We already have the DashboardUtils.isCheckedIn() to find it out
   * upsellItemsAvailableNotEmpty: We need to get it from Opera get Packages.
   * manageBookingResponse.amendable(): We don't need to show the meals if the reservation is not amendable
   *
   * @param manageBookingResponse as ResultsDto
   * @return we will send it always true for now without any conditions.
   */
  public static boolean shouldDisplayMeals(final ManageBookingResponse manageBookingResponse) {
    return TRUE.equals(manageBookingResponse.getIsAmendable());
  }

  private static void addCiol(final Content content, final DashboardProperties properties, final String language) {
    final var action = Action.builder()
        .type(CIOL)
        .title(properties.getActionMapping().get(language).get(CIOL.name()))
        .build();
    content.getActions().add(action);
  }

  private static void addMeals(final Content content, final DashboardProperties properties, final String language) {
    final var action = Action.builder()
        .type(UPSELLS)
        .title(properties.getActionMapping().get(language).get(UPSELLS.name()))
        .build();
    content.getActions().add(action);
  }

  private static void addDirections(final Content content, final DashboardProperties properties,
      final String language) {
    final var action = Action.builder()
        .type(DIRECTIONS)
        .title(properties.getActionMapping().get(language).get(DIRECTIONS.name()))
        .build();
    content.getActions().add(action);
  }

  private static void addBookingDetails(final Content content, final DashboardProperties properties,
      final String language) {
    final var action = Action.builder()
        .type(BOOKING_DETAILS)
        .title(properties.getActionMapping().get(language).get(BOOKING_DETAILS.name()))
        .build();
    content.getActions().add(action);
  }
}
