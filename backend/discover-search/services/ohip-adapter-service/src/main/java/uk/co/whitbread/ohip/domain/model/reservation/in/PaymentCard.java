package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class PaymentCard implements SelfValidation<PaymentCard> {

  @NotEmpty
  private String cardType;
  @NotEmpty
  private String token;
  @NotEmpty
  private String expirationDate;
  //@NotEmpty --> fixed for amend add new room
  private String cardHolderName;
  private String cardNumberLast4Digits;
  private String citId;

  public PaymentCard(String cardType, String token, String expirationDate,
      String cardHolderName, String cardNumberLast4Digits, String citId) {
    this.cardType = cardType;
    this.token = token;
    this.expirationDate = expirationDate;
    this.cardHolderName = cardHolderName;
    this.cardNumberLast4Digits = cardNumberLast4Digits;
    this.citId = citId;
    this.validateSelf();
  }
}
