package uk.co.whitbread.ohip.infrastructure.rest.controller.eckoh.model.in;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CardTypeDto {
  CREDIT("credit"),
  DEBIT("debit"),
  PREPAID("prepaid");

  private final String value;

  CardTypeDto(String value) {
    this.value = value;
  }

  @JsonValue
  public String getCardType() {
    return this.value;
  }
}
