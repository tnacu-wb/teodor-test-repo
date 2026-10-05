package uk.co.whitbread.basket.domain.model.payments.in;

import java.util.Map;

public enum Allowance {

  DINNER("dinner"), MEAL_DEAL("mealDeal"), ALLOW_ALCOHOL("alcohol"), ULTIMATE_WIFI("ultimateWifi"),
  CAR_PARKING("carParking"), PREMIER_BREAKFAST("premierInnBreakfast"), CONTINENTAL_BREAKFAST("continentalBreakfast"),
  ADDITIONAL_CHARGES("otherCharges"), BOXED_BREAKFAST("boxedBreakfast"), CARD_PRESENT("cardPresent"),
  CONTINENTAL_BUNDLE("continentalBundle"), LIGHTER_BUNDLE("lighterBundle"),
  BREAKFAST_ROLL_BUNDLE("breakfastRollBundle");
  Map<Integer, Allowance> breakfastIdMap;
  private final String code;

  Allowance(String code) {
    this.code = code;

  }

  public String getCode() {
    return this.code;
  }

}
