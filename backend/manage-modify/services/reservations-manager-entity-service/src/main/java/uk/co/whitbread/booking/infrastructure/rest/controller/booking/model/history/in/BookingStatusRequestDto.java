package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.in;

public enum BookingStatusRequestDto {

  FUTURE("U"),
  PAST("P"),
  CANCELLED("C"),
  CHECKED_IN("H");

  private String bartCode;

  BookingStatusRequestDto(String bartCode) {
    this.bartCode = bartCode;
  }

  public String getBartCode() {
    return bartCode;
  }

}
