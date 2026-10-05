package uk.co.whitbread.digitalkey.domain.model.axp;

public enum OtpType {
  EMAIL("email"),
  SMS("sms");

  private final String value;

  OtpType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}