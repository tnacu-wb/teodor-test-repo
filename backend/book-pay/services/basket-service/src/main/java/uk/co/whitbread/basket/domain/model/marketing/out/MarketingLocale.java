package uk.co.whitbread.basket.domain.model.marketing.out;

import java.util.Arrays;

public enum MarketingLocale {

  UK("en"),
  DE("de");

  private String language;

  MarketingLocale(String language) {
    this.language = language;
  }

  public static MarketingLocale fromLanguage(String language) {
    return Arrays.stream(MarketingLocale.values()).filter(marketingLocale -> language.equals(marketingLocale.language))
        .findFirst().orElse(MarketingLocale.UK);
  }

}
