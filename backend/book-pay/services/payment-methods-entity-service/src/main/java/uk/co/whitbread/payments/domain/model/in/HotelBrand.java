package uk.co.whitbread.payments.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HotelBrand {

  HUB("HUB"),

  PI("PI"),

  PID("PID"),

  CBT("CBT"),

  ZIP("ZIP");

  private String value;

  public static HotelBrand fromValue(String value) {
    for (HotelBrand b : HotelBrand.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}