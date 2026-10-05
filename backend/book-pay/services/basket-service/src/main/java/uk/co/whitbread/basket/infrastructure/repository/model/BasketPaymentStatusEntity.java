package uk.co.whitbread.basket.infrastructure.repository.model;

public enum BasketPaymentStatusEntity {
  COMPLETED("COMPLETED"),
  REFUNDING("REFUNDING"),
  REFUNDED("REFUNDED"),
  FAILED_REFUND("FAILED_REFUND");

  private final String paymentStatus;

  BasketPaymentStatusEntity(String paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  @Override
  public String toString() {
    return paymentStatus;
  }
}
