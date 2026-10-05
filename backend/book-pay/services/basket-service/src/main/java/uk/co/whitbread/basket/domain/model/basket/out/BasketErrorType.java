package uk.co.whitbread.basket.domain.model.basket.out;

public enum BasketErrorType {

  PAYMENT("PAYMENT"),
  CONFIRMATION("CONFIRMATION"),
  TIMEOUT("TIMEOUT"),
  AMEND_CONFIRM("AMEND_CONFIRM"),
  AMEND_DEPOSIT_FOLIOS("AMEND_DEPOSIT_FOLIOS"),
  AMEND_REFUND("AMEND_REFUND"),
  AMEND_REVERT("AMEND_REVERT");
  private final String type;

  BasketErrorType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return type;
  }
}
