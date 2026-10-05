package uk.co.whitbread.basket.processor.infrastructure.queue.model.order;

public enum BasketRequestAction {

  COMMIT("COMMIT"),
  CANCEL("CANCEL"),
  AMEND("AMEND"),
  ROLLBACK("ROLLBACK"),
  CHANGE_PAY("CHANGE_PAY");

  final String reqAction;

  BasketRequestAction(String reqAction) {
    this.reqAction = reqAction;
  }

  public String getReqAction() {
    return reqAction;
  }
}
