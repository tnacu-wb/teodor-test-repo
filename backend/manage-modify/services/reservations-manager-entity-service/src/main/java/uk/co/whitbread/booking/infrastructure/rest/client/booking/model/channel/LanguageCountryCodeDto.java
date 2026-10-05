package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.channel;

public enum LanguageCountryCodeDto {
  LANGUAGE_DE("de"),
  LANGUAGE_EN("en"),
  COUNTRY_DE("de"),
  COUNTRY_EN("gb");

  private final String value;

  LanguageCountryCodeDto(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
