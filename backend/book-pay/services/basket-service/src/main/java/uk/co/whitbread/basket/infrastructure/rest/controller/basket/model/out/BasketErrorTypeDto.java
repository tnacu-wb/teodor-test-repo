package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

public enum BasketErrorTypeDto {

  PAYMENT("PAYMENT"),
  CONFIRMATION("CONFIRMATION"),
  TIMEOUT("TIMEOUT"),
  AMEND_CONFIRM("AMEND_CONFIRM"),
  AMEND_DEPOSIT_FOLIOS("AMEND_DEPOSIT_FOLIOS"),
  AMEND_REFUND("AMEND_REFUND"),
  AMEND_REVERT("AMEND_REVERT");

  private final String type;

  BasketErrorTypeDto(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return type;
  }

}
