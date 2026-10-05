package uk.co.whitbread.reservation.domain.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DepositFolio {

  private String hotelId;
  private String reservationId;
  private String vatRegion;
  private String paymentId;
  private String defaultPaymentMethod;
  private List<DepositFolioCharge> charges;

}
