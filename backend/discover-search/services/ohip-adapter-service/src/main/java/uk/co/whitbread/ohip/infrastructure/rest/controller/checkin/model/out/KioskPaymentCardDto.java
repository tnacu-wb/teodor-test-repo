package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KioskPaymentCardDto {

  private CardIdDto cardId;
  private String cardType;
  private String cardNumberMasked;
  private String expirationDateMasked;
  private boolean expirationDateExpired;
  private String cardHolderName;
  private String processing;
  private boolean swiped;
  private boolean cardPresent;
  private String cardOrToken;

}
