package uk.co.whitbread.payments.domain.model.out;

import static uk.co.whitbread.payments.domain.logic.common.PaymentMethodsConstant.BUSINESS_ACCOUNT;
import static uk.co.whitbread.payments.domain.logic.common.PaymentMethodsConstant.MASTERCARD_CREDIT;

public enum CardType {
  AC(MASTERCARD_CREDIT),
  AM("American Express"),
  AT(BUSINESS_ACCOUNT),
  DI("Diners Club"),
  DL("Visa Debit"),
  EL("Electron"),
  MA("Maestro"),
  MD("Mastercard Debit"),
  VI("Visa Credit"),
  MC(MASTERCARD_CREDIT),
  AX(MASTERCARD_CREDIT),
  PI(BUSINESS_ACCOUNT),
  DN("Diners Club"),
  VS("Visa Debit"),
  BD(BUSINESS_ACCOUNT),
  AP("Apple Pay"),
  GP("Google Pay"),

  PP("Paypal"),
  PE("Business Account"),
  // Datatrans payment scheme codes
  ECA("Mastercard Credit/Debit"),
  AMX("American Express"),
  VIS("Visa"),
  DIN("Diners Club"),
  MAU("Maestro");

  private final String type;

  CardType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  private static final String LEISURE_CARD = "CARD";

  public static boolean isBusinessCard(String type) {
    return CardType.PI.name().equals(type) || CardType.AT.name().equals(type) || CardType.BD.name().equals(type);
  }

  public static String resolveCardName(String type) {
    var cardType = CardType.valueOf(type);
    return switch (cardType) {
      case AT, PI -> PibaCard.UK.getType();
      case BD -> PibaCard.DE.getType();
      default -> LEISURE_CARD;
    };
  }
}
