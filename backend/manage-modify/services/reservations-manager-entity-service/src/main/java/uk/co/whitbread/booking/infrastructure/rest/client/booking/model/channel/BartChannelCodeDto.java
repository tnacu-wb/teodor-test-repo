package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.channel;

public enum BartChannelCodeDto {
  MOBILE("MOBILE"),
  WEB("WEB"),
  WEB_DE("WEB_de"),
  CBT("CBT");

  private final String value;

  BartChannelCodeDto(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
