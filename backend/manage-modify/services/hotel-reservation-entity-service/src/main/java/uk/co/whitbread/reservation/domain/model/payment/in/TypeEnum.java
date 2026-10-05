package uk.co.whitbread.reservation.domain.model.payment.in;

public enum TypeEnum {

  CARD("CARD"),

  PIBA("PIBA"),

  PIBA_EU("PIBA_EU");

  private String value;

  TypeEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static TypeEnum fromValue(String value) {
    for (TypeEnum b : TypeEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
