package uk.co.whitbread.hotel.account.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static uk.co.whitbread.hotel.account.utils.ResetPasswordDataGenerator.ResetPasswordData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.whitbread.hotel.account.properties.EmailProperties;

class ResetPasswordDataGeneratorTest {

  private static final String PI_RESET_PASSWORD_URL = "pi";
  private static final String INNB_RESET_PASSWORD_URL = "innb";
  private static final String INNB_RESET_PASSWORD_URL_DE = "innbde";

  private ResetPasswordDataGenerator generator;

  @BeforeEach
  void setUp() {
    final EmailProperties emailProperties = new EmailProperties();
    emailProperties.setPiResetPasswordUrl(PI_RESET_PASSWORD_URL);
    emailProperties.setInnBusinessResetPasswordUrl(INNB_RESET_PASSWORD_URL);
    emailProperties.setInnBusinessResetPasswordUrlDe(INNB_RESET_PASSWORD_URL_DE);

    generator = new ResetPasswordDataGenerator(emailProperties);
  }

  @ParameterizedTest
  @CsvSource({
      "null, null, false, 8, pi",
      "null, null, true, 10, innb",
      "null, uk, true, 10, innb",
      "null, de, true, 10, innbde"
  })
  void generateResetPasswordData_tokenAndUrlAreGenerated(String requestUrl, String language, boolean business,
                                                         int expectedTokenLength, String expectedUrlPrefix) {
    if ("null".equals(requestUrl)) requestUrl = null;
    if ("null".equals(language)) language = null;

    final ResetPasswordData resetPasswordData = generator.generateResetPasswordData(requestUrl, language, business);

    final String resetToken = resetPasswordData.getResetToken();
    final String resetUrl = resetPasswordData.getResetUrl();

    assertThat(resetToken, notNullValue());
    assertThat(resetToken.length(), is(expectedTokenLength));
    assertThat(resetUrl, notNullValue());
    assertThat(resetUrl, is(expectedUrlPrefix + resetToken));
  }

  @ParameterizedTest
  @CsvSource({
      "requestUrl, false, requestUrl?token=",
      "requestUrl, true, requestUrl?key="
  })
  void generateResetPasswordData_requestUrlHasParamAppended(String requestUrl, boolean business, String expectedUrlPrefix) {
    final ResetPasswordData resetPasswordData = generator.generateResetPasswordData(requestUrl, null, business);

    final String resetToken = resetPasswordData.getResetToken();
    final String resetUrl = resetPasswordData.getResetUrl();

    assertThat(resetToken, notNullValue());
    assertThat(resetUrl, notNullValue());
    assertThat(resetUrl, is(expectedUrlPrefix + resetToken));
  }

  @ParameterizedTest
  @CsvSource({
      "requestUrl?token=, false",
      "requestUrl?key=, true"
  })
  void generateResetPasswordData_requestUrlOverridesConfig(String requestUrl, boolean business) {
    final ResetPasswordData resetPasswordData = generator.generateResetPasswordData(requestUrl, null, business);

    final String resetToken = resetPasswordData.getResetToken();
    final String resetUrl = resetPasswordData.getResetUrl();

    assertThat(resetToken, notNullValue());
    assertThat(resetUrl, notNullValue());
    assertThat(resetUrl, is(requestUrl + resetToken));
  }

}