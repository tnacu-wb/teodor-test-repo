package uk.co.whitbread.payments.infrastructure.rest.client.basket.model.out;

public enum BasketStatusEnum {
  COMPLETED,
  OPEN,
  PROCESSING,
  AMENDING,
  AMENDED,
  PRE_CHECKED_IN,
  CANCELLED,
  PAY_PENDING,
  AMEND_FAILED,
  FAILED,
  CIOL_FAILED,
  PRE_CHECKED_OUT,
  CIOL_RC_FAILED,
  SECURE_FAILED
}
