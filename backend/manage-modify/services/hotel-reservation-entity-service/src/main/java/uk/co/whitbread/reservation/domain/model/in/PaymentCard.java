package uk.co.whitbread.reservation.domain.model.in;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class PaymentCard implements SelfValidation<PaymentCard> {

  private String cardType;
  private String token;
  private String expirationDate;
  private String cardHolderName;
  private String cardNumberLast4Digits;
  private String citId;

}
