package uk.co.whitbread.basket.domain.model.payments.out;

public enum BasketPaymentStatus {
  COMPLETED("COMPLETED"),
  REFUNDING("REFUNDING"),
  REFUNDED("REFUNDED"),
  FAILED_REFUND("FAILED_REFUND");

  private final String paymentStatus;

  BasketPaymentStatus(String paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  @Override
  public String toString() {
    return paymentStatus;
  }
}
