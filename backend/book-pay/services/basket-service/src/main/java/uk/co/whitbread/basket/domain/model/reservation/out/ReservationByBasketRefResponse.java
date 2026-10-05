package uk.co.whitbread.basket.domain.model.reservation.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationByBasketRefResponse {
  private List<Reservation> reservationByIdList;
  private BigDecimal previousTotal;
  private BigDecimal balanceOutstanding;
  private BigDecimal newTotal;
  private BigDecimal totalCost;
  private BigDecimal discount;
  private String policyCode;
  private String hotelId;
  private String currencyCode;
  private String bookingReference;
  private String companyId;
  private String paymentMethod;
}

