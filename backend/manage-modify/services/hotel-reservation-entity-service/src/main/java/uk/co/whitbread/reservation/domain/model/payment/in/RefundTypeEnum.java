package uk.co.whitbread.reservation.domain.model.payment.in;

public enum RefundTypeEnum {

  FULL("FULL"),

  PARTIAL("PARTIAL");

  private String value;

  RefundTypeEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static RefundTypeEnum fromValue(String value) {
    for (RefundTypeEnum b : RefundTypeEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
