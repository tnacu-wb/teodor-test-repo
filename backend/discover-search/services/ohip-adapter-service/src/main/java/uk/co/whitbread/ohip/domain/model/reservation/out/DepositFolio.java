package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositFolio {
  private String hotelId;
  private String reservationId;
  private String paymentId;
  private String vatRegion;
  private String defaultPaymentMethod;
  private List<DepositFolioCharge> charges;

}
