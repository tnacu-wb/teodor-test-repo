package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.out;

public enum BookingStatusDto {

  FUTURE("U"),
  PAST("P"),
  CANCELLED("C"),
  CHECKED_IN("H");

  private String bartCode;

  BookingStatusDto(String bartCode) {
    this.bartCode = bartCode;
  }

  public String getBartCode() {
    return bartCode;
  }
}
