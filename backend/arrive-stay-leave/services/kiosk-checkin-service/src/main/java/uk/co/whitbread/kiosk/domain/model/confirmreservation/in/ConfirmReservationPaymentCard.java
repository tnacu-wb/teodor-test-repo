package uk.co.whitbread.kiosk.domain.model.confirmreservation.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmReservationPaymentCard {

  private String cardType;
  private String token;
  private String expirationDate;
  private String cardHolderName;
  private String cardNumberLast4Digits;

}
