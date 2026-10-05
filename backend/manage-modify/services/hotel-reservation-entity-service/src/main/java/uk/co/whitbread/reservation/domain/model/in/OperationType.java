package uk.co.whitbread.reservation.domain.model.in;

public enum OperationType {

  NEW("NEW"), AMEND("AMEND"), CANCEL("CANCEL");

  String type;

  OperationType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

}
