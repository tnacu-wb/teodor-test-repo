package uk.co.whitbread.booking.domain.model.history.out;

public enum BasketStatus {
  OPEN("OPEN"),
  PROCESSING("PROCESSING"),
  AMENDING("AMENDING"),
  COMPLETED("COMPLETED"),
  AMENDED("AMENDED"),
  CANCELLED("CANCELLED"),
  PAY_PENDING("PAY_PENDING"),
  AMEND_FAILED("AMEND_FAILED"),
  FAILED("FAILED"),
  PRE_CHECKED_IN("PRE_CHECKED_IN"),
  CIOL_FAILED("CIOL_FAILED"),
  PRE_CHECKED_OUT("PRE_CHECKED_OUT"),
  CIOL_RC_FAILED("CIOL_RC_FAILED");

  private final String status;

  BasketStatus(String status) {
    this.status = status;
  }

  public static boolean shouldDisableCiol(BasketStatus basketStatus) {
    return CIOL_RC_FAILED.equals(basketStatus) || PRE_CHECKED_IN.equals(basketStatus);
  }

  @Override
  public String toString() {
    return status;
  }
}
