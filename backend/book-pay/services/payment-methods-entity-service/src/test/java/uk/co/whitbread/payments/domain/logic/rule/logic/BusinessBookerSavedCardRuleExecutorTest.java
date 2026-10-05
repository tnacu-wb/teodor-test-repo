package uk.co.whitbread.payments.domain.logic.rule.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.CardOption.SAVED_CARD;
import static uk.co.whitbread.payments.domain.model.out.CardStatus.*;
import static uk.co.whitbread.payments.domain.model.out.CardType.AT;
import static uk.co.whitbread.payments.domain.model.out.CardType.PI;
import static uk.co.whitbread.payments.domain.model.out.PaymentMethodsTestUtils.paymentMethod;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_ON_ARRIVAL;
import static uk.co.whitbread.payments.domain.model.out.RuleDataTest.getAcceptedCardTypes;
import static uk.co.whitbread.payments.domain.model.out.RuleDataTest.getThreecAcceptedCardTypes;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_CENTRALLY_STORED_CARD;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_PERSONAL_STORED_CARD;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.LEISURE_STORED_CARD;
import static uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out.PaymentType.PAY_NOW;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payments.domain.model.out.PaypalRequest;
import uk.co.whitbread.payments.domain.model.out.Reservation;
import uk.co.whitbread.payments.domain.model.out.RuleData;
import uk.co.whitbread.payments.domain.model.out.RuleDataTest;
import uk.co.whitbread.payments.domain.model.out.StoredCardType;

class BusinessBookerSavedCardRuleExecutorTest {

  @Test
  void givenARequest_returnsEmptyList() {
    // Arrange
    BusinessBookerSavedCardRuleExecutor businessBookerSavedCardRuleExecutor =
        new BusinessBookerSavedCardRuleExecutor(RuleDataTest.createRequest());

    // Act
    var result = businessBookerSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods().stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
            .stream()
            .filter(paymentOption -> paymentOption.getType().equals(PAY_NOW.name()))
            .forEach(paymentOption -> assertFalse(paymentOption.isEnabled())));
  }

  @Test
  void givenRequestWithPrepaymentAllowedFlag_returnsCompanyNewPIBADisabled() {
    // Arrange
    BusinessBookerSavedCardRuleExecutor businessBookerSavedCardRuleExecutor =
        new BusinessBookerSavedCardRuleExecutor(RuleDataTest.createRequest());

    // Act
    var result = businessBookerSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods()
        .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          paymentMethod.getPaymentOptions()
              .forEach(paymentOption -> assertFalse(paymentOption.isEnabled()));
          assertEquals(List.of(PIBA_UK_ALLOWED_ONLY_IN_UK.name(), COMPANY_DISABLED_NEW_CARD.name()),
              paymentMethod.getReasons());
        });
  }

  @Test
  void givenRequestWithPrepaymentAllowedFlag_returnsPersonalSavedCardDisabled() {
    // Arrange
    BusinessBookerSavedCardRuleExecutor businessBookerSavedCardRuleExecutor =
        new BusinessBookerSavedCardRuleExecutor(RuleDataTest.createRequest());

    // Act
    var result = businessBookerSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods()
        .stream().filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && BUSINESS_PERSONAL_STORED_CARD.name().equals(paymentMethod.getCard().getCardType()))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          assertTrue(paymentMethod.getReasons().contains(PERSONAL_STORED_CARD_DISABLED.name()));
        });
  }

  @Test
  void givenRequestWithPrepaymentAllowedFlag_returnsSavedPibaDisabled() {
    // Arrange
    BusinessBookerSavedCardRuleExecutor businessBookerSavedCardRuleExecutor =
        new BusinessBookerSavedCardRuleExecutor(RuleDataTest.createRequest());

    // Act
    var result = businessBookerSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods()
        .stream().filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && (AT.name().equals(paymentMethod.getCard().getType()) ||
            PI.name().equals(paymentMethod.getCard().getType())))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          assertEquals(List.of(CARD_NOT_ACCEPTED_AT_HOTEL.name(), EXPIRED.name(),
              PIBA_UK_ALLOWED_ONLY_IN_UK.name()), paymentMethod.getReasons());
        });
  }

  @Test
  void givenPaymentMethods_returnsSavedCentralCardSorted() {
    // Arrange
    BusinessBookerSavedCardRuleExecutor businessBookerSavedCardRuleExecutor =
        new BusinessBookerSavedCardRuleExecutor(RuleDataTest.getCard());

    // Act
    var result = businessBookerSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    var paymentMethod = result.get().getPaymentMethods().get(0);
    assertEquals(StoredCardType.BUSINESS_CENTRALLY_STORED_CARD.name(),
        paymentMethod.getCard().getCardType());
  }

  @Test
  void givenPaymentMethods_returnsSavedCentralCardSortedWhenMultipleCardMethods() {
    // Arrange
    BusinessBookerSavedCardRuleExecutor businessBookerSavedCardRuleExecutor =
        new BusinessBookerSavedCardRuleExecutor(createMultipleCards());

    // Act
    var result = businessBookerSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    Assertions.assertThat(result.get().getPaymentMethods())
        .usingRecursiveComparison()
        .comparingOnlyFields("cardType")
        .isEqualTo(new ArrayList<>(List.of(
            paymentMethod(NEW_CARD, AT, BUSINESS_CENTRALLY_STORED_CARD, PAY_ON_ARRIVAL),
            paymentMethod(NEW_CARD, AT, BUSINESS_CENTRALLY_STORED_CARD, PAY_ON_ARRIVAL),
            paymentMethod(NEW_CARD, AT, LEISURE_STORED_CARD, PAY_ON_ARRIVAL)
        )));
  }

  private RuleData createMultipleCards() {
    return RuleData.builder()
        .allowIndividualCards(false)
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of())
            .build())
        .paymentMethods(new ArrayList<>(List.of(
            paymentMethod(NEW_CARD, AT, BUSINESS_CENTRALLY_STORED_CARD, PAY_ON_ARRIVAL),
            paymentMethod(NEW_CARD, AT, LEISURE_STORED_CARD, PAY_ON_ARRIVAL),
            paymentMethod(NEW_CARD, AT, BUSINESS_CENTRALLY_STORED_CARD, PAY_ON_ARRIVAL)
        ))).acceptedCardType(getAcceptedCardTypes())
        .threecAcceptedCardType(getThreecAcceptedCardTypes())
        .paypalRequest(new PaypalRequest())
        .build();
  }


  // FOR PIBA EURO

  @Test
  void givenARequest_returnsEmptyListForPibaEuro() {
    // Arrange
    BusinessBookerSavedCardRuleExecutor businessBookerSavedCardRuleExecutor =
        new BusinessBookerSavedCardRuleExecutor(RuleDataTest.createRequestForPibaEuro());

    // Act
    var result = businessBookerSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods().stream()
        .filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentMethod -> paymentMethod.getPaymentOptions()
            .stream()
            .filter(paymentOption -> paymentOption.getType().equals(PAY_NOW.name()))
            .forEach(paymentOption -> assertFalse(paymentOption.isEnabled())));
  }

  @Test
  void givenRequestWithPrepaymentAllowedFlag_returnsCompanyNewPIBADisabledForPibaEuro() {
    // Arrange
    BusinessBookerSavedCardRuleExecutor businessBookerSavedCardRuleExecutor =
        new BusinessBookerSavedCardRuleExecutor(RuleDataTest.createRequestForPibaEuro());

    // Act
    var result = businessBookerSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods()
        .stream().filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          paymentMethod.getPaymentOptions()
              .forEach(paymentOption -> assertFalse(paymentOption.isEnabled()));
          assertEquals(List.of(PIBA_EU_ALLOWED_ONLY_IN_EU.name(), COMPANY_DISABLED_NEW_CARD.name()),
              paymentMethod.getReasons());
        });
  }

  @Test
  void givenRequestWithPrepaymentAllowedFlag_returnsPersonalSavedCardDisabledForPibaEuro() {
    // Arrange
    BusinessBookerSavedCardRuleExecutor businessBookerSavedCardRuleExecutor =
        new BusinessBookerSavedCardRuleExecutor(RuleDataTest.createRequestForPibaEuro());

    // Act
    var result = businessBookerSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods()
        .stream().filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && BUSINESS_PERSONAL_STORED_CARD.name().equals(paymentMethod.getCard().getCardType()))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          assertTrue(paymentMethod.getReasons().contains(PERSONAL_STORED_CARD_DISABLED.name()));
        });
  }

  @Test
  void givenRequestWithPrepaymentAllowedFlag_returnsSavedPibaDisabledForPibaEuro() {
    // Arrange
    BusinessBookerSavedCardRuleExecutor businessBookerSavedCardRuleExecutor =
        new BusinessBookerSavedCardRuleExecutor(RuleDataTest.createRequestForPibaEuro());

    // Act
    var result = businessBookerSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    result.get().getPaymentMethods()
        .stream().filter(paymentMethod -> paymentMethod.getType().equals(SAVED_CARD.name())
            && (AT.name().equals(paymentMethod.getCard().getType()) ||
            PI.name().equals(paymentMethod.getCard().getType())))
        .forEach(paymentMethod -> {
          assertFalse(paymentMethod.isEnabled());
          assertEquals(List.of(CARD_NOT_ACCEPTED_AT_HOTEL.name(), EXPIRED.name(),
              PIBA_UK_ALLOWED_ONLY_IN_UK.name()), paymentMethod.getReasons());
        });
  }

  @Test
  void givenPaymentMethods_returnsSavedCentralCardSortedForPibaEuro() {
    // Arrange
    BusinessBookerSavedCardRuleExecutor businessBookerSavedCardRuleExecutor =
        new BusinessBookerSavedCardRuleExecutor(RuleDataTest.getCardForPibaEuro());

    // Act
    var result = businessBookerSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    var paymentMethod = result.get().getPaymentMethods().get(0);
    assertEquals(StoredCardType.BUSINESS_CENTRALLY_STORED_CARD.name(),
        paymentMethod.getCard().getCardType());
  }

  @Test
  void givenPaymentMethods_returnsSavedCentralCardSortedWhenMultipleCardMethodsForPibaEuro() {
    // Arrange
    BusinessBookerSavedCardRuleExecutor businessBookerSavedCardRuleExecutor =
        new BusinessBookerSavedCardRuleExecutor(createMultipleCardsForPibaEuro());

    // Act
    var result = businessBookerSavedCardRuleExecutor.execute();

    // Assert
    assertTrue(result.isPresent());
    Assertions.assertThat(result.get().getPaymentMethods())
        .usingRecursiveComparison()
        .comparingOnlyFields("cardType")
        .isEqualTo(new ArrayList<>(List.of(
            paymentMethod(NEW_CARD, AT, BUSINESS_CENTRALLY_STORED_CARD, PAY_ON_ARRIVAL),
            paymentMethod(NEW_CARD, AT, BUSINESS_CENTRALLY_STORED_CARD, PAY_ON_ARRIVAL),
            paymentMethod(NEW_CARD, AT, LEISURE_STORED_CARD, PAY_ON_ARRIVAL)
        )));
  }
  private RuleData createMultipleCardsForPibaEuro() {
    return RuleData.builder()
        .allowIndividualCards(false)
        .reservation(Reservation.builder()
            .hotelPaymentPolicies(Set.of())
            .build())
        .paymentMethods(new ArrayList<>(List.of(
            paymentMethod(NEW_CARD, AT, BUSINESS_CENTRALLY_STORED_CARD, PAY_ON_ARRIVAL),
            paymentMethod(NEW_CARD, AT, LEISURE_STORED_CARD, PAY_ON_ARRIVAL),
            paymentMethod(NEW_CARD, AT, BUSINESS_CENTRALLY_STORED_CARD, PAY_ON_ARRIVAL)
        ))).acceptedCardType(getAcceptedCardTypes())
        .threecAcceptedCardType(getThreecAcceptedCardTypes())
        .paypalRequest(new PaypalRequest())
        .isPibaEuroEnabled(Boolean.TRUE)
        .build();
  }
}