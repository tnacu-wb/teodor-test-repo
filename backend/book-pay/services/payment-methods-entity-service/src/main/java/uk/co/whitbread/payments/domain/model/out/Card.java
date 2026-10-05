package uk.co.whitbread.payments.domain.model.out;

import lombok.Data;

@Data
public class Card {
  private String token;
  private String cardNumber;
  private String expiryMonth;
  private String expiryYear;
  private String type;
  private String logoSrc;
  private String cardHolderName;
  private String cardType;
  private String cardName;
  private boolean cnpRequired;
}

