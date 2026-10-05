package uk.co.whitbread.ohip.domain.model.checkin.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInReservationPaymentMethods {

  private KioskPaymentCard paymentCard;
  private EmailFolioInfo emailFolioInfo;
  private String paymentMethod;
  private String description;
  private int folioView;

}
