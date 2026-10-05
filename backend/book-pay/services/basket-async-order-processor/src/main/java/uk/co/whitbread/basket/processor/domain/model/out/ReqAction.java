package uk.co.whitbread.basket.processor.domain.model.out;

public enum ReqAction {

  COMMIT("COMMIT"),
  CANCEL("CANCEL"),
  ROLLBACK("ROLLBACK");

  final String reqAction;

  ReqAction(String reqAction) {
    this.reqAction = reqAction;
  }

  public String getReqAction() {
    return reqAction;
  }
}