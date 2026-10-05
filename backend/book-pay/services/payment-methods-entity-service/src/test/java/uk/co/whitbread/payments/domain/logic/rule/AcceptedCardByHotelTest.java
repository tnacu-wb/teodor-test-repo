package uk.co.whitbread.payments.domain.logic.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_CARD;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.AcceptedCardType;
import uk.co.whitbread.payments.domain.model.out.PaymentProviderType;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;

class AcceptedCardByHotelTest {

  private final AcceptedCardByHotel rule = new AcceptedCardByHotel();

  @Test
  void calculate__newCardsAcceptedAtHotel() {
    // Arrange
    RuleData ruleData = RuleDataTest.createRequest();

    // Act
    var result = rule.calculate(ruleData);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_CARD.name()))
        .forEach(paymentMethod -> {
          assertTrue(paymentMethod.isEnabled());
          assertEquals(RuleDataTest.getAcceptedCardTypes(), paymentMethod.getAcceptedCardTypes());
          paymentMethod.getPaymentOptions()
              .forEach(paymentOption -> assertTrue(paymentOption.isEnabled()));
        });
  }

  @Test
  void calculate_acceptedCardsToApplePay() {
      // Arrange
      RuleData ruleData = RuleDataTest.createRequestForApplePay();

      // Act
      var result = rule.calculate(ruleData);

      // Assert
      result.getPaymentMethods()
                .stream()
                .filter(paymentMethod -> paymentMethod.getType().equals("AP"))
                .forEach(paymentMethod -> {
                    assertTrue(paymentMethod.isEnabled());
                    assertEquals(RuleDataTest.getAcceptedCardTypes(), paymentMethod.getAcceptedCardTypes());
                    paymentMethod.getPaymentOptions()
                            .forEach(paymentOption -> assertTrue(paymentOption.isEnabled()));
                });
  }

  @Test
  void calculate_acceptedCardsToGooglePay() {
      // Arrange
      RuleData ruleData = RuleDataTest.createRequestForGooglePay();

      // Act
      var result = rule.calculate(ruleData);

      // Assert
      result.getPaymentMethods()
                .stream()
                .filter(paymentMethod -> paymentMethod.getType().equals("GP"))
                .forEach(paymentMethod -> {
                    assertTrue(paymentMethod.isEnabled());
                    assertEquals(RuleDataTest.getAcceptedCardTypes(), paymentMethod.getAcceptedCardTypes());
                    paymentMethod.getPaymentOptions()
                            .forEach(paymentOption -> assertTrue(paymentOption.isEnabled()));
                });
  }

  @Test
  void calculate__newCardsAcceptedAtHotel_NotReturnPIBA() {
    // Arrange
    RuleData rule = RuleDataTest.createRequest();

    // Act
    var result = this.rule.calculate(rule);
    var acceptCardType = new AcceptedCardType();
    acceptCardType.setType("PI");
    acceptCardType.setName("Business Account");

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_CARD.name()))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.getAcceptedCardTypes().contains(acceptCardType));
        });
  }


  //For PIBA EURO
  @Test
  void calculate__newCardsAcceptedAtHotelForPibaEuro() {
    // Arrange
    RuleData ruleData = RuleDataTest.createRequest();
    ruleData.setPibaEuroEnabled(Boolean.TRUE);

    // Act
    var result = rule.calculate(ruleData);

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_CARD.name()))
        .forEach(paymentMethod -> {
          assertTrue(paymentMethod.isEnabled());
          assertEquals(RuleDataTest.getAcceptedCardTypes(), paymentMethod.getAcceptedCardTypes());
          paymentMethod.getPaymentOptions()
              .forEach(paymentOption -> assertTrue(paymentOption.isEnabled()));
        });
  }

  @Test
  void calculate__newCardsAcceptedAtHotel_NotReturnPIBAForPibaEuro() {
    // Arrange
    RuleData rule = RuleDataTest.createRequest();
    rule.setPibaEuroEnabled(Boolean.TRUE);

    // Act
    var result = this.rule.calculate(rule);
    var acceptCardType = new AcceptedCardType();
    acceptCardType.setType("PI");
    acceptCardType.setName("Business Account");

    // Assert
    result.getPaymentMethods()
        .stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_CARD.name()))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.getAcceptedCardTypes().contains(acceptCardType));
        });
  }

  // ── Datatrans tests ───────────────────────────────────────────────────────

  @Test
  void calculate_newCard_usesDatatransCards_andSetsProviderDatatrans_whenEnabled() {
    // Arrange — isDataTransEnabled=true, NEW_CARD is not excluded
    RuleData ruleData = RuleDataTest.createRequestWithDatatransEnabled();

    // Act
    var result = rule.calculate(ruleData);

    // Assert — NEW_CARD method should use Datatrans cards and have provider=DATATRANS
    result.getPaymentMethods()
        .stream()
        .filter(pm -> NEW_CARD.name().equals(pm.getType()))
        .forEach(pm -> {
          assertEquals(PaymentProviderType.DATATRANS, pm.getPaymentProvider());
          // cards must come from the Datatrans list (ECA, VIS) — not the 3CP list
          pm.getAcceptedCardTypes().forEach(act ->
              assertTrue(RuleDataTest.getDatatransAcceptedCardTypes().contains(act),
                  "Expected Datatrans card but got: " + act.getType()));
        });
  }

  @Test
  void calculate_newCard_uses3CPCards_andSetsProviderPlanet3CP_whenDataTransDisabled() {
    // Arrange — isDataTransEnabled=false
    RuleData ruleData = RuleDataTest.createRequest(); // no Datatrans fields → disabled

    // Act
    var result = rule.calculate(ruleData);

    // Assert — provider should be PLANET_3CP
    result.getPaymentMethods()
        .stream()
        .filter(pm -> NEW_CARD.name().equals(pm.getType()))
        .forEach(pm ->
            assertEquals(PaymentProviderType.PLANET_3CP, pm.getPaymentProvider()));
  }

  @Test
  void calculate_newCard_uses3CPCards_whenEnabledButOptionExcluded() {
    // Arrange — isDataTransEnabled=true but NEW_CARD in exclusion list
    RuleData ruleData = RuleDataTest.createRequestWithDatatransEnabledButNewCardExcluded();

    // Act
    var result = rule.calculate(ruleData);

    // Assert — NEW_CARD still gets 3CP cards and PLANET_3CP provider
    result.getPaymentMethods()
        .stream()
        .filter(pm -> NEW_CARD.name().equals(pm.getType()))
        .forEach(pm -> {
          assertEquals(PaymentProviderType.PLANET_3CP, pm.getPaymentProvider());
          // Cards must come from the 3CP list, not the Datatrans list
          pm.getAcceptedCardTypes().forEach(act ->
              assertFalse(RuleDataTest.getDatatransAcceptedCardTypes().contains(act),
                  "Did not expect Datatrans card: " + act.getType()));
        });
  }}
