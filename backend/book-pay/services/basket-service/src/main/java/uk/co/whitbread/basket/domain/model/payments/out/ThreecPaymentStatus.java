package uk.co.whitbread.basket.domain.model.payments.out;

public enum ThreecPaymentStatus {

  SUCCESS("SUCCESS"),
  FAILURE("FAILURE"),
  PENDING("PENDING"),
  NO_PAYMENT_ATTEMPT("NO_PAYMENT_ATTEMPT");

  final String status;

  ThreecPaymentStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }
}