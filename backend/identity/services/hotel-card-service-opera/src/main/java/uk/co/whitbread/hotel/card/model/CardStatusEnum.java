package uk.co.whitbread.hotel.card.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardStatusEnum {
  PENDING("Pending"),
  NOT_ACTIVATED("Not activated"),
  CANCELLED("Cancelled"),
  CURRENT("Current"),
  HOT("HOT");

  private final String value;

  public static CardStatusEnum fromValue(String value) {
    for (CardStatusEnum b : CardStatusEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
