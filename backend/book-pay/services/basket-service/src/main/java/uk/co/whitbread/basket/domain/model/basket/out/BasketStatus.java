package uk.co.whitbread.basket.domain.model.basket.out;

public enum BasketStatus {
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

  BasketStatus(String status) {
    this.status = status;
  }

  public static boolean isReadyForCheckOut(BasketStatus status) {
    return status == COMPLETED || status == PRE_CHECKED_IN || status == CIOL_FAILED
        || status == SECURE_FAILED;
  }

  public static boolean isReadyForCheckIn(BasketStatus status) {
    return status == COMPLETED || status == CIOL_FAILED || status == SECURE_FAILED;
  }

  @Override
  public String toString() {
    return status;
  }
}
