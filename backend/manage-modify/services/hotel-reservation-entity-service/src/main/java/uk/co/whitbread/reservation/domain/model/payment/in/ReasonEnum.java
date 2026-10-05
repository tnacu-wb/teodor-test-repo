package uk.co.whitbread.reservation.domain.model.payment.in;

public enum ReasonEnum {

  CANCEL("CANCEL"),

  AMEND("AMEND"),

  ROLLBACK("ROLLBACK");

  private String value;

  ReasonEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static ReasonEnum fromValue(String value) {
    for (ReasonEnum b : ReasonEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }

}
