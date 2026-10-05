package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

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
public class ReservationByBasketRefResponseDto {

  private List<ReservationByIdDto> reservationByIdList;
  private BigDecimal previousTotal;
  private BigDecimal balanceOutstanding;
  private BigDecimal newTotal;
  private BigDecimal totalCost;
  private BigDecimal amountPaid;
  private BigDecimal totalCostWoDiscount;
  private BigDecimal discount;
  private String policyCode;
  private String currencyCode;
  private String hotelId;
  private Boolean isCnp;
  private String companyId;
  private String bookingReference;
  private String purchaseOrderNumber;
  private String customReferenceNumber;
  private String idContext;
}
