package uk.co.whitbread.hotel.account.model;

import lombok.Getter;

@Getter
public enum BookingChannelCode {
  MOBILE("MOBILE"),
  WEB("WEB2014"),
  WEB_DE("WEB_de"),
  CBT("CBT");

  private final String value;

  BookingChannelCode(String value) { this.value = value; }

}
