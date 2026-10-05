package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

public enum BasketStatusDto {
  OPEN("OPEN"),
  PROCESSING("PROCESSING"),
  AMENDING("AMENDING"),
  COMPLETED("COMPLETED"),
  AMENDED("AMENDED"),
  PRE_CHECKED_IN("PRE_CHECKED_IN"),
  CANCELLED("CANCELLED"),
  PAY_PENDING("PAY_PENDING"),
  AMEND_FAILED("AMEND_FAILED"),
  FAILED("FAILED"),
  CIOL_FAILED("CIOL_FAILED"),
  PRE_CHECKED_OUT("PRE_CHECKED_OUT"),
  CIOL_RC_FAILED("CIOL_RC_FAILED"),
  SECURE_FAILED("SECURE_FAILED");

  private final String status;

  BasketStatusDto(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return status;
  }
}
