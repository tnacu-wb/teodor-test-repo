package uk.co.whitbread.ohip.domain.logic.utils;

import java.util.List;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitution;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitutionRuleResponse;

public final class RulesAgentUtils {

  private RulesAgentUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Create a list of acceptable Opera room types by using the substitution rules. Removes
   * duplicates to avoid unnecessary OHIP query parameters
   *
   * @param roomSubstitutions The list of room substitution rules from the Rules Engine.
   * @return A list of Opera room types to be requested availability for (e.g. "FMTRPL", "FMQUAD", etc.)
   */
  public static List<String> convertWbRoomTypesToPmsRoomTypes(
      List<RoomSubstitutionRuleResponse> roomSubstitutions) {
    return roomSubstitutions.stream()
        .map(RoomSubstitutionRuleResponse::getSubstitutionList)
        .flatMap(List::stream)
        .map(RoomSubstitution::getType)
        .distinct()
        .toList();
  }
}
