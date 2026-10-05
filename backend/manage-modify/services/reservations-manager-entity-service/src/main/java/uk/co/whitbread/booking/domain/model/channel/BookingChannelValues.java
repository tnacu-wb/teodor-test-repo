package uk.co.whitbread.booking.domain.model.channel;

public enum BookingChannelValues {
  CHANNEL_BB("BB"),
  CHANNEL_PI("PI"),
  SUBCHANNEL_PI("WEB");

  private final String value;

  BookingChannelValues(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
