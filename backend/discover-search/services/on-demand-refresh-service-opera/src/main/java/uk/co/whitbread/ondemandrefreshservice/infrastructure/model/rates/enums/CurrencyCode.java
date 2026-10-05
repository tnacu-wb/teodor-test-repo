package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.enums;

public enum CurrencyCode {
  GBP ("G"),
  EUR ("E");

  private String code;

  CurrencyCode(final String code){
    this.code = code;
  }

  public String currencyCode(){
    return code;
  }
}
