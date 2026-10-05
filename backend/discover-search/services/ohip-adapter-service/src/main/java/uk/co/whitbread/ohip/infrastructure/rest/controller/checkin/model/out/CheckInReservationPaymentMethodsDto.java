package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInReservationPaymentMethodsDto {

  private KioskPaymentCardDto paymentCard;
  private EmailFolioInfoDto emailFolioInfo;
  private String paymentMethod;
  private String description;
  private int folioView;

}
