package uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.payment;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.PaymentSecurityProperties;

class ReturnUrlValidatorTest {

  private static final List<String> ALLOWED_HOSTS =
      List.of("premierinn.com", "www.premierinn.com", "premierinn.digital");

  private ReturnUrlValidator validator;

  @BeforeEach
  void setUp() {
    var securityProperties = new PaymentSecurityProperties();
    securityProperties.setAllowedReturnUrlHosts(ALLOWED_HOSTS);
    validator = new ReturnUrlValidator(securityProperties);
  }

  @Nested
  class ValidUrls {

    @Test
    void httpsWithAllowedHost_isAccepted() {
      assertThat(validator.isValidReturnUrl("https://www.premierinn.com/payments/3ds-return"))
          .isTrue();
    }

    @Test
    void httpsWithAllowedHostNoPath_isAccepted() {
      assertThat(validator.isValidReturnUrl("https://premierinn.com")).isTrue();
    }

    @Test
    void httpsWithAllowedDigitalHost_isAccepted() {
      assertThat(validator.isValidReturnUrl("https://premierinn.digital/callback")).isTrue();
    }

    @Test
    void httpsWithQueryParams_isAccepted() {
      assertThat(validator.isValidReturnUrl(
          "https://www.premierinn.com/payments/return?basketId=123&status=ok"))
          .isTrue();
    }

    @Test
    void subdomainOfAllowedHost_isAccepted() {
      assertThat(validator.isValidReturnUrl("https://subdomain.premierinn.com/callback"))
          .isTrue();
    }

    @Test
    void ephemeralEnvironmentSubdomain_isAccepted() {
      assertThat(validator.isValidReturnUrl(
          "https://premier-inn-feature-ctech-12715.dev.premierinn.digital/payments/return"))
          .isTrue();
    }
  }

  @Nested
  class InvalidUrls {

    @Test
    void httpScheme_isRejected() {
      assertThat(validator.isValidReturnUrl("http://www.premierinn.com/payments/3ds-return"))
          .isFalse();
    }

    @Test
    void hostNotInAllowlist_isRejected() {
      assertThat(validator.isValidReturnUrl("https://evil.com/phishing")).isFalse();
    }

    @Test
    void suffixBypassHost_isRejected() {
      assertThat(validator.isValidReturnUrl("https://premierinn.digital.evil.com/phishing"))
          .isFalse();
    }

    @Test
    void hostEndingWithAllowedLabelWithoutDotBoundary_isRejected() {
      assertThat(validator.isValidReturnUrl("https://notpremierinn.digital/callback"))
          .isFalse();
    }

    @Test
    void malformedUrl_isRejected() {
      assertThat(validator.isValidReturnUrl("not a url at all")).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    void nullOrBlankUrl_isRejected(String url) {
      assertThat(validator.isValidReturnUrl(url)).isFalse();
    }

    @Test
    void ftpScheme_isRejected() {
      assertThat(validator.isValidReturnUrl("ftp://premierinn.com/file")).isFalse();
    }

    @Test
    void noScheme_isRejected() {
      assertThat(validator.isValidReturnUrl("premierinn.com/callback")).isFalse();
    }
  }
}
