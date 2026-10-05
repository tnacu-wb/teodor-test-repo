package uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.payment;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.DatatransWebhookProperties;

class WebhookSignatureValidatorTest {

  /** Fake hex sign key used only by this test — never a real Datatrans credential. */
  private static final String HMAC_KEY =
      "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

  private static final String TIMESTAMP = "1700000000000";

  private static final String RAW_BODY =
      "{\"transactionId\":\"240412103042123456\",\"status\":\"authorized\"}";

  /** HMAC-SHA256 of {@code TIMESTAMP + RAW_BODY} keyed with {@code HMAC_KEY}, precomputed. */
  private static final String EXPECTED_SIGNATURE =
      "759c9f056795c431b5d20b640e9c456f6ce27a061b10643f6da2aed705f1a5ce";

  private static final String VALID_HEADER = "t=" + TIMESTAMP + ",s0=" + EXPECTED_SIGNATURE;

  private DatatransWebhookProperties properties;
  private WebhookSignatureValidator validator;

  @BeforeEach
  void setUp() {
    properties = new DatatransWebhookProperties();
    properties.setHmacKey(HMAC_KEY);
    properties.setValidationEnabled(true);
    validator = new WebhookSignatureValidator(properties);
  }

  @Nested
  class ValidSignature {

    @Test
    void precomputedHmac_isAccepted() {
      assertThat(validator.isValid(RAW_BODY, VALID_HEADER)).isTrue();
    }

    @Test
    void uppercaseHexSignature_isAccepted() {
      String header = "t=" + TIMESTAMP + ",s0=" + EXPECTED_SIGNATURE.toUpperCase(Locale.ROOT);
      assertThat(validator.isValid(RAW_BODY, header)).isTrue();
    }

    @Test
    void whitespaceAroundHeaderParts_isTolerated() {
      String header = " t = " + TIMESTAMP + " , s0 = " + EXPECTED_SIGNATURE + " ";
      assertThat(validator.isValid(RAW_BODY, header)).isTrue();
    }
  }

  @Nested
  class InvalidSignature {

    @Test
    void signatureMismatch_isRejected() {
      String header = "t=" + TIMESTAMP + ",s0=" + "0".repeat(64);
      assertThat(validator.isValid(RAW_BODY, header)).isFalse();
    }

    @Test
    void tamperedBody_isRejected() {
      String tampered = RAW_BODY.replace("authorized", "failed");
      assertThat(validator.isValid(tampered, VALID_HEADER)).isFalse();
    }

    @Test
    void differentTimestamp_isRejected() {
      String header = "t=1700000000001,s0=" + EXPECTED_SIGNATURE;
      assertThat(validator.isValid(RAW_BODY, header)).isFalse();
    }

    @Test
    void signatureFromDifferentKey_isRejected() {
      properties.setHmacKey("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
      assertThat(validator.isValid(RAW_BODY, VALID_HEADER)).isFalse();
    }
  }

  @Nested
  class MissingOrMalformedHeader {

    @Test
    void nullHeader_isRejected() {
      assertThat(validator.isValid(RAW_BODY, null)).isFalse();
    }

    @Test
    void blankHeader_isRejected() {
      assertThat(validator.isValid(RAW_BODY, "   ")).isFalse();
    }

    @Test
    void headerWithoutTimestampPart_isRejected() {
      assertThat(validator.isValid(RAW_BODY, "s0=" + EXPECTED_SIGNATURE)).isFalse();
    }

    @Test
    void headerWithoutSignaturePart_isRejected() {
      assertThat(validator.isValid(RAW_BODY, "t=" + TIMESTAMP)).isFalse();
    }

    @Test
    void headerWithBlankSignatureValue_isRejected() {
      assertThat(validator.isValid(RAW_BODY, "t=" + TIMESTAMP + ",s0=")).isFalse();
    }

    @Test
    void headerWithoutKeyValueSeparators_isRejected() {
      assertThat(validator.isValid(RAW_BODY, "not-a-signature-header")).isFalse();
    }

    @Test
    void nullBody_isRejected() {
      assertThat(validator.isValid(null, VALID_HEADER)).isFalse();
    }
  }

  @Nested
  class MissingOrInvalidKey {

    @Test
    void nullKey_isRejected() {
      properties.setHmacKey(null);
      assertThat(validator.isValid(RAW_BODY, VALID_HEADER)).isFalse();
    }

    @Test
    void blankKey_isRejected() {
      properties.setHmacKey("  ");
      assertThat(validator.isValid(RAW_BODY, VALID_HEADER)).isFalse();
    }

    @Test
    void nonHexKey_isRejected() {
      properties.setHmacKey("not-hex-at-all");
      assertThat(validator.isValid(RAW_BODY, VALID_HEADER)).isFalse();
    }

    @Test
    void oddLengthHexKey_isRejected() {
      properties.setHmacKey("abc");
      assertThat(validator.isValid(RAW_BODY, VALID_HEADER)).isFalse();
    }
  }

  @Nested
  class ValidationDisabled {

    @BeforeEach
    void disableValidation() {
      properties.setValidationEnabled(false);
    }

    @Test
    void invalidSignature_isAccepted() {
      assertThat(validator.isValid(RAW_BODY, "t=" + TIMESTAMP + ",s0=" + "0".repeat(64))).isTrue();
    }

    @Test
    void missingHeaderAndKey_isAccepted() {
      properties.setHmacKey(null);
      assertThat(validator.isValid(RAW_BODY, null)).isTrue();
    }
  }
}
