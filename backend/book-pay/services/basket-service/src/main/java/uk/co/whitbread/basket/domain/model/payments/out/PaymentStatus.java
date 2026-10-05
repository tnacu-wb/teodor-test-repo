package uk.co.whitbread.basket.domain.model.payments.out;

public enum PaymentStatus {
  /**
   * The payment step is not required for
   * {@link uk.co.whitbread.basket.domain.model.payments.in.PaymentOption#RESERVE_WITHOUT_CARD}.
   */
  NOT_REQUIRED,
  /**
   * Payment/card verification step is required for
   * {@link uk.co.whitbread.basket.domain.model.payments.in.PaymentOption#PAY_NOW}/{@link
   * uk.co.whitbread.basket.domain.model.payments.in.PaymentOption#PAY_ON_ARRIVAL}.
   */
  PAYMENT_REQUIRED
}
