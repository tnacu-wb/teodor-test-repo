package uk.co.whitbread.basket.domain.model.payments.out;

public enum OrchestrationPaymentStatus {

  SUCCESS("SUCCESS"),
  FAILURE("FAILURE"),
  AUTHORIZED("AUTHORIZED");

  final String status;

  OrchestrationPaymentStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }
}