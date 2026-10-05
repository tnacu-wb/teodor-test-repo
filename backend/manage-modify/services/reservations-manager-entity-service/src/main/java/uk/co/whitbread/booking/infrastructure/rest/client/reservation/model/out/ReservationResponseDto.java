package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out;

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
public class ReservationResponseDto {

  private List<ReservationDetailsDto> reservationByIdList;
  private BigDecimal previousTotal;
  private BigDecimal balanceOutstanding;
  private BigDecimal newTotal;
  private BigDecimal totalCost;
  private BigDecimal discount;
  private BigDecimal totalCostWoDiscount;
  private String policyCode;
  private String hotelId;
  private String currencyCode;
  private String basketReference;

}
