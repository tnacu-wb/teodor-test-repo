package uk.co.whitbread.payments.domain.model.out;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentProviderType {

  PLANET_3CP("3CP"),
  DATATRANS("Datatrans");

  private final String value;

  PaymentProviderType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
