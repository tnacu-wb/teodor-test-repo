package uk.co.whitbread.payments.domain.model.out;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class DataTransConfigTest {

  // ── isDatatransSupported ──────────────────────────────────────────────────

  @Test
  void isDatatransSupported_returnsFalse_whenOptionInExclusionList() {
    var config = new DataTransConfig(List.of("SAVED_CARD", "NEW_PIBA"));
    assertFalse(config.isDatatransSupported("SAVED_CARD"));
  }

  @Test
  void isDatatransSupported_returnsTrue_whenOptionNotInExclusionList() {
    var config = new DataTransConfig(List.of("SAVED_CARD", "NEW_PIBA"));
    assertTrue(config.isDatatransSupported("NEW_CARD"));
  }

  @Test
  void isDatatransSupported_returnsTrue_whenExclusionListIsEmpty() {
    var config = new DataTransConfig(List.of());
    assertTrue(config.isDatatransSupported("NEW_CARD"));
    assertTrue(config.isDatatransSupported("SAVED_CARD"));
  }

  // ── resolveCardSource ─────────────────────────────────────────────────────

  @Test
  void resolveCardSource_returnsThreecCards_whenDataTransDisabled() {
    var threecCard = cardType("MC", "Mastercard");
    var datatransCard = cardType("ECA", "Mastercard Datatrans");

    var ruleData = RuleData.builder()
        .isDataTransEnabled(false)
        .threecAcceptedCardType(List.of(threecCard))
        .datatransAcceptedCardType(List.of(datatransCard))
        .dataTransProperties(new DataTransConfig(List.of()))
        .build();

    assertEquals(List.of(threecCard), ruleData.resolveCardSource("NEW_CARD"));
  }

  @Test
  void resolveCardSource_returnsDatatransCards_whenEnabledAndOptionSupported() {
    var threecCard = cardType("MC", "Mastercard");
    var datatransCard = cardType("ECA", "Mastercard Datatrans");

    var ruleData = RuleData.builder()
        .isDataTransEnabled(true)
        .threecAcceptedCardType(List.of(threecCard))
        .datatransAcceptedCardType(List.of(datatransCard))
        .dataTransProperties(new DataTransConfig(List.of("SAVED_CARD"))) // NEW_CARD not excluded
        .build();

    assertEquals(List.of(datatransCard), ruleData.resolveCardSource("NEW_CARD"));
  }

  @Test
  void resolveCardSource_returnsThreecCards_whenEnabledButOptionExcluded() {
    var threecCard = cardType("MC", "Mastercard");
    var datatransCard = cardType("ECA", "Mastercard Datatrans");

    var ruleData = RuleData.builder()
        .isDataTransEnabled(true)
        .threecAcceptedCardType(List.of(threecCard))
        .datatransAcceptedCardType(List.of(datatransCard))
        .dataTransProperties(new DataTransConfig(List.of("SAVED_CARD", "NEW_CARD")))
        .build();

    assertEquals(List.of(threecCard), ruleData.resolveCardSource("NEW_CARD"));
  }

  @Test
  void resolveCardSource_returnsEmpty_whenDatatransEnabledAndListNull() {
    var ruleData = RuleData.builder()
        .isDataTransEnabled(true)
        .threecAcceptedCardType(List.of(cardType("MC", "Mastercard")))
        .datatransAcceptedCardType(null)
        .dataTransProperties(new DataTransConfig(List.of())) // nothing excluded → Datatrans
        .build();

    assertEquals(List.of(), ruleData.resolveCardSource("NEW_CARD"));
  }

  // ── resolvePaymentProvider ────────────────────────────────────────────────

  @Test
  void resolvePaymentProvider_returnsPlanet3CP_whenDataTransDisabled() {
    var ruleData = RuleData.builder()
        .isDataTransEnabled(false)
        .dataTransProperties(new DataTransConfig(List.of()))
        .build();

    assertEquals(PaymentProviderType.PLANET_3CP, ruleData.resolvePaymentProvider("NEW_CARD"));
  }

  @Test
  void resolvePaymentProvider_returnsDatatrans_whenEnabledAndOptionSupported() {
    var ruleData = RuleData.builder()
        .isDataTransEnabled(true)
        .dataTransProperties(new DataTransConfig(List.of("SAVED_CARD")))
        .build();

    assertEquals(PaymentProviderType.DATATRANS, ruleData.resolvePaymentProvider("NEW_CARD"));
  }

  @Test
  void resolvePaymentProvider_returnsPlanet3CP_whenEnabledButOptionExcluded() {
    var ruleData = RuleData.builder()
        .isDataTransEnabled(true)
        .dataTransProperties(new DataTransConfig(List.of("NEW_CARD")))
        .build();

    assertEquals(PaymentProviderType.PLANET_3CP, ruleData.resolvePaymentProvider("NEW_CARD"));
  }

  @Test
  void resolvePaymentProvider_returnsPlanet3CP_whenConfigIsNull() {
    var ruleData = RuleData.builder()
        .isDataTransEnabled(true)
        .dataTransProperties(null)
        .build();

    assertEquals(PaymentProviderType.PLANET_3CP, ruleData.resolvePaymentProvider("NEW_CARD"));
  }

  // ── helpers ───────────────────────────────────────────────────────────────

  private AcceptedCardType cardType(String type, String name) {
    var act = new AcceptedCardType();
    act.setType(type);
    act.setName(name);
    return act;
  }
}
