package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

public enum BasketPaymentStatusDto {
  COMPLETED("COMPLETED"),
  REFUNDING("REFUNDING"),
  REFUNDED("REFUNDED"),
  FAILED_REFUND("FAILED_REFUND");

  private final String paymentStatus;

  BasketPaymentStatusDto(String paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  @Override
  public String toString() {
    return paymentStatus;
  }
}
