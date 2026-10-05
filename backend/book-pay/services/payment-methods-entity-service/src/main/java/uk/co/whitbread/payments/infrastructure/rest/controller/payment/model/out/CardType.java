package uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum CardType {
  AC("Mastercard Credit"),
  AM("American Express"),
  AT("Business Account"),
  DI("Diners Club"),
  DL("Visa Debit"),
  EL("Electron"),
  MA("Maestro"),
  MD("Mastercard Debit"),
  VI("Visa Credit"),
  MC("Mastercard"),
  AX("American Express"),
  PI("PIBA UK"),
  DN("Diners"),
  VS("Visa"),
  BD("InnBusiness Pay Card"),
  AP("Apple Pay"),
  GP("Google Pay"),
  PP("Paypal"),
  PE("PIBA EUR"),
  // Datatrans payment scheme codes
  ECA("Mastercard Credit/Debit"),
  AMX("American Express"),
  VIS("Visa"),
  DIN("Diners Club"),
  MAU("Maestro");

  private final String cardName;

  CardType(String cardName) {
    this.cardName = cardName;
  }


  public static String getCardName(String type) {
    for (CardType cardType : values()) {
      if (cardType.name().equalsIgnoreCase(type)) {
        return cardType.cardName;
      }
    }
    log.error("CardType unexpected value '" + type + "'");
    return null;
  }
}
