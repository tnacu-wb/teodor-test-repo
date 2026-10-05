package uk.co.whitbread.shared.azureemail.properties;

import lombok.Data;

@Data
public abstract class AzureEmailProperties {

  public static final String LANGUAGE_EN = "en";
  public static final String LANGUAGE_DE = "de";

  private String host;
  private String username;
  private String password;
  private String subscriptionKeyHeaderName;
  private String subscriptionKey;
  private String defaultLanguageCode = LANGUAGE_EN;
}
