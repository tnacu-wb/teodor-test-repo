package uk.co.whitbread.ohip.domain.model.eckoh.in;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.ohip.domain.model.reservation.in.BaseValidation;

class EckohWebhookTest extends BaseValidation {

  @Test
  void constructor_emptyResult_shouldSelfValidateAndThrow() {
    String expectedMessage = "result: must not be null";

    checkErrorThrown(() -> EckohWebhook.builder()
        .expiry("1226")
        .resultCode(100)
        .type(EckohCardType.CREDIT)
        .scheme(CardScheme.VISA.name())
        .maskedPan("432cxz")
        .token("4325453534534534")
        .reference("1234")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyExpiry_shouldSelfValidateAndThrow() {
    String expectedMessage = "expiry: must not be null";

    checkErrorThrown(() -> EckohWebhook.builder()
        .result("succes")
        .resultCode(100)
        .type(EckohCardType.CREDIT)
        .scheme(CardScheme.VISA.name())
        .maskedPan("432cxz")
        .token("4325453534534534")
        .reference("123")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyType_shouldSelfValidateAndThrow() {
    String expectedMessage = "type: must not be null";

    checkErrorThrown(() -> EckohWebhook.builder()
        .result("succes")
        .resultCode(100)
        .expiry("1226")
        .scheme(CardScheme.VISA.name())
        .maskedPan("432cxz")
        .token("4325453534534534")
        .reference("123")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyScheme_shouldSelfValidateAndThrow() {
    String expectedMessage = "scheme: must not be null";

    checkErrorThrown(() -> EckohWebhook.builder()
        .result("succes")
        .resultCode(100)
        .expiry("1226")
        .type(EckohCardType.CREDIT)
        .maskedPan("432cxz")
        .token("4325453534534534")
        .reference("123")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyMaskedPan_shouldSelfValidateAndThrow() {
    String expectedMessage = "maskedPan: must not be null";

    checkErrorThrown(() -> EckohWebhook.builder()
        .result("succes")
        .resultCode(100)
        .expiry("1226")
        .type(EckohCardType.CREDIT)
        .scheme(CardScheme.VISA.name())
        .token("4325453534534534")
        .reference("h-432423423")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyToken_shouldSelfValidateAndThrow() {
    String expectedMessage = "token: must not be null";

    checkErrorThrown(() -> EckohWebhook.builder()
        .result("succes")
        .resultCode(100)
        .expiry("1226")
        .type(EckohCardType.CREDIT)
        .scheme(CardScheme.VISA.name())
        .maskedPan("4324x")
        .reference("123")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyReference_shouldSelfValidateAndThrow() {
    String expectedMessage = "reference: must not be null";

    checkErrorThrown(() -> EckohWebhook.builder()
        .result("succes")
        .resultCode(100)
        .expiry("1226")
        .type(EckohCardType.CREDIT)
        .scheme(CardScheme.VISA.name())
        .maskedPan("4324x")
        .token("43243242432")
        .build(), expectedMessage);
  }

}