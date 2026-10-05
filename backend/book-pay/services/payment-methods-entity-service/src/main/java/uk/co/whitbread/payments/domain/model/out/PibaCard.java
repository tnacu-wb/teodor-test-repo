package uk.co.whitbread.payments.domain.model.out;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum PibaCard {
  UK("PIBA", "PIBAGB", Country.GB, List.of(CardType.AT, CardType.PI)),
  DE("PIBA", "PIBADE", Country.DE, List.of(CardType.BD));
  private final String type;
  private final String subType;
  private final Country country;

  private final List<CardType> cardTypes;

  PibaCard(String type, String subType, Country country, List<CardType> cardTypes) {
    this.type = type;
    this.subType = subType;
    this.country = country;
    this.cardTypes = cardTypes;
  }

  public String getType() {
    return type;
  }

  public String getSubType() {
    return subType;
  }

  public Country getCountry() {
    return  country;
  }

  public List<CardType> getCardTypes() {
    return cardTypes;
  }

  public static boolean isEnabledCountry(Country country) {
    return Arrays.stream(PibaCard.values())
            .anyMatch(i -> i.getCountry().equals(country));
  }

  public static boolean isPibaUkInInvalidCountry(String paymentMethodName, Country country) {
    return UK.getSubType().equals(paymentMethodName)
            && !UK.getCountry().equals(country);
  }

  public static boolean isPibaEuInInvalidCountry(String paymentMethodName, Country country) {
    return DE.getSubType().equals(paymentMethodName)
            && !DE.getCountry().equals(country);
  }

  public static Optional<PibaCard> resolveApplicablePibaCard(Country country) {
    return Arrays.stream(PibaCard.values())
            .filter(pibaCard -> pibaCard.getCountry().equals(country))
            .findFirst();
  }

  public static Optional<PibaCard> resolveSubTypeForPibaCards(String cardTypeVal) {
    CardType cardType = CardType.valueOf(cardTypeVal);
    return Arrays.stream(PibaCard.values())
            .filter(pibaCard -> CardType.isBusinessCard(cardType.name()) && pibaCard.getCardTypes().contains(cardType))
            .findFirst();
  }

}
