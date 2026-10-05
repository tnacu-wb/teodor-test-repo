package uk.co.whitbread.reservation.infrastructure.rest.client.reservation.service;

public enum TableBookingEnum {
  FROM("from"),
  UNTIL("until"),

  SITE_ID("siteId"),

  RESTAURANT_NAME("restaurantName");

  private final String value;

  TableBookingEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}