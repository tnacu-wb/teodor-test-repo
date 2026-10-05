package uk.co.whitbread.shared.azureemail.requestbuilder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import uk.co.whitbread.shared.azureemail.properties.PTIEmailProperties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class RequestBuilderData {

  private static final String USERNAME = "UserName";
  private static final String PASSWORD = "PassWord";

  static final PTIEmailProperties ptiEmailProperties = new PTIEmailProperties();

  static {
    ptiEmailProperties.setUsername(USERNAME);
    ptiEmailProperties.setPassword(PASSWORD);
  }
}
