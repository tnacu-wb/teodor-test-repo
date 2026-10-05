package uk.co.whitbread.booking.domain.model.history.in;

public enum BookingStatus {

  FUTURE("U"),
  PAST("P"),
  CANCELLED("C"),
  CHECKED_IN("H");

  private String bartCode;

  BookingStatus(String bartCode) {
    this.bartCode = bartCode;
  }

  public String getBartCode() {
    return bartCode;
  }
}
