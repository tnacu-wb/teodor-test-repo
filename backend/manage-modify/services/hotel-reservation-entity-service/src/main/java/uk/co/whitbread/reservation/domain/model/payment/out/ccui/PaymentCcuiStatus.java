package uk.co.whitbread.reservation.domain.model.payment.out.ccui;

public enum PaymentCcuiStatus {
  OPEN,
  PROCESSING,
  AMENDING,
  COMPLETED,
  AMENDED,
  CANCELLED,
  PAY_PENDING,
  AMEND_FAILED,
  FAILED,
  PRE_CHECKED_IN,
  CIOL_FAILED,
  PRE_CHECKED_OUT,
  CIOL_RC_FAILED,
  SECURE_FAILED
}
