package uk.co.whitbread.basket.domain.model.payments.in;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class Card implements SelfValidation<Card> {

  private String cardholderName;
  private String token;
  private String expiryMonth;
  private String expiryYear;
  private String cardType;
  private Boolean cnpRequired;
  private String logoUrl;
  private String type;
  private String last4Digits;

  public Card(String cardholderName, String token, String expiryMonth, String expiryYear,
      String cardType, Boolean cnpRequired, String logoUrl, String type, String last4Digits) {
    this.cardholderName = cardholderName;
    this.token = token;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
    this.cardType = cardType;
    this.cnpRequired = cnpRequired;
    this.logoUrl = logoUrl;
    this.type = type;
    this.last4Digits = last4Digits;
    this.validateSelf();
  }
}
