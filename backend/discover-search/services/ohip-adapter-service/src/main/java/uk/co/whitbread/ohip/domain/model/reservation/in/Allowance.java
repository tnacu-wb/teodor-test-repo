package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.Arrays;

public enum Allowance {

  DINNER("dinner"), MEAL_DEAL("mealDeal"), ALLOW_ALCOHOL("alcohol"), ULTIMATE_WIFI("ultimateWifi"),
  CAR_PARKING("carParking"), PREMIER_BREAKFAST("premierInn"), CONTINENTAL_BREAKFAST("continental"),
  ADDITIONAL_CHARGES("anyAdditionalCharges");

  private final String code;

  Allowance(String code) {
    this.code = code;
  }

  public static Allowance getByCode(String code) {
    var filteredAllowances =
        Arrays.stream(Allowance.values()).filter(allowance -> allowance.getCode().equals(code))
            .toList();
    if (filteredAllowances.isEmpty()) {
      return null;
    }
    return filteredAllowances.get(0);
  }

  public String getCode() {
    return this.code;
  }
}
