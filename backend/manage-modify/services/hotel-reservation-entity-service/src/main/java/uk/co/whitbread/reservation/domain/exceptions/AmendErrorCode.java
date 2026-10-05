package uk.co.whitbread.reservation.domain.exceptions;

public enum AmendErrorCode {
  AMEND_GENERIC_EXCEPTION("amend.generic.exception"),
  AMEND_CONFIRM_EXCEPTION("amend.confirm.exception"),
  AMEND_DEPOSIT_FOLIOS_EXCEPTION("amend.deposit.folios.exception"),
  AMEND_REFUND_EXCEPTION("amend.refund.exception"),
  AMEND_REVERT_EXCEPTION("amend.revert.exception"),
  AMEND_DATE_EXCEPTION("amend.date.exception");
  private final String code;

  AmendErrorCode(final String code) {
    this.code = code;
  }

  public String getCode() {
    return this.code;
  }
}
