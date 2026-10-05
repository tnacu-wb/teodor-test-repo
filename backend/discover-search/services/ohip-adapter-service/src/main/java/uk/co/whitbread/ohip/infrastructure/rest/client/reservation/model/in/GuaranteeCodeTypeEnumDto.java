package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in;

public enum GuaranteeCodeTypeEnumDto {
  ON_HOLD("O9ONHOLD");

  private final String value;

  GuaranteeCodeTypeEnumDto(String s) {
    this.value = s;
  }

  public String value() {
    return value;
  }
}