package uk.co.whitbread.basket.domain.model.reservation.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositFolioRequest {
  private List<DepositFolioCharge> charges;
  private String defaultPaymentMethod;
  private String hotelId;
  private String paymentId;
  private String reservationId;
  private String vatRegion;
}