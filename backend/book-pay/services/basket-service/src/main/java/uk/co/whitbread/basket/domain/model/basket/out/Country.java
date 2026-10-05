package uk.co.whitbread.basket.domain.model.basket.out;

public enum Country {

  EN("gb"),
  DE("de");

  String countryCode;

  Country(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getCountryCode() {
    return countryCode;
  }
}
