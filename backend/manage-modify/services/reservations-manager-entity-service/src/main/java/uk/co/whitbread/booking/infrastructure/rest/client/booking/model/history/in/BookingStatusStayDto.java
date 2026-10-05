package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in;

public enum BookingStatusStayDto {

  FUTURE("U"),
  PAST("P"),
  CANCELLED("C"),
  CHECKED_IN("H");

  private String bartCode;

  BookingStatusStayDto(String bartCode) {
    this.bartCode = bartCode;
  }

  public String getBartCode() {
    return bartCode;
  }
}
