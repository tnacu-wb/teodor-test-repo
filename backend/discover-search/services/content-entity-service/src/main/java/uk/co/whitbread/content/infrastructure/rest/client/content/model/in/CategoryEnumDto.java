package uk.co.whitbread.content.infrastructure.rest.client.content.model.in;

import lombok.Getter;

@Getter
public enum CategoryEnumDto {
  MAIN("main"),
  PI_BOOKINGS("piBookings"),
  BOOKING("booking"),
  PI_PRE_CHECKIN("piPreCheckIn"),
  PI_GROUP_BOOKING("piGroupBooking"),
  EXTRAS("extras"),
  PROMOTIONS("promotions");

  final String field;

  CategoryEnumDto(String field) {
    this.field = field;
  }

}
