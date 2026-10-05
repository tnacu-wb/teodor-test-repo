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
public class ReservationByBasketRefResponseSingleCall {
  private List<ReservationByIdResponseSingleCall> reservationByIdList;
  private BigDecimal previousTotal;
  private BigDecimal balanceOutstanding;
  private BigDecimal newTotal;
  private BigDecimal totalCost;
  private BigDecimal amountPaid;
  private BigDecimal discount;
  private BigDecimal totalCostWoDiscount;
  private String policyCode;
  private String hotelId;
  private String currencyCode;
  private String bookingReference;
  private String basketReference;
  private String channel;
  private Boolean hasCityTax;
  private Boolean isCnp;
  private String companyId;
  private String purchaseOrderNumber;
  private String customReferenceNumber;
}