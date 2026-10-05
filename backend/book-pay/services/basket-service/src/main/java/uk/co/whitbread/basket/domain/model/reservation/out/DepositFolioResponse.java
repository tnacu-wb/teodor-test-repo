package uk.co.whitbread.basket.domain.model.reservation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositFolioResponse {

  private List<DepositFolioCharge> charges;
  private String defaultPaymentMethod;
  private String hotelId;
  private String paymentId;
  private String reservationId;
  private String vatRegion;
}