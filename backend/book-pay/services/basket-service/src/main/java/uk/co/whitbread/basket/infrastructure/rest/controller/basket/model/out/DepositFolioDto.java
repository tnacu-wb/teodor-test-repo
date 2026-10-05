package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DepositFolioDto {

  private String hotelId;
  private String reservationId;
  private String vatRegion;
  private String paymentId;
  private String defaultPaymentMethod;
  private List<DepositFolioChargeDto> charges;

}
