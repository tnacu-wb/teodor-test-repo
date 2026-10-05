package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationByBasketRefResponse {

  private List<ReservationById> reservationByIdList;
  private BigDecimal previousTotal;
  private BigDecimal balanceOutstanding;
  private BigDecimal newTotal;
  private BigDecimal totalCost;
  private BigDecimal amountPaid;
  private BigDecimal totalCostWoDiscount;
  private BigDecimal discount;
  private String currencyCode;
  private String policyCode;
  private String hotelId;
  private Boolean isCnp;
  private String companyId;
  private String bookingReference;
  private String purchaseOrderNumber;
  private String customReferenceNumber;
  private String idContext;

}