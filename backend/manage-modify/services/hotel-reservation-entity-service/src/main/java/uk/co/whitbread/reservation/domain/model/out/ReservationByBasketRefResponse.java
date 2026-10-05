package uk.co.whitbread.reservation.domain.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromoKind;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationByBasketRefResponse {

  private List<ReservationByIdResponse> reservationByIdList;
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
  private String basketStatus;
  private String distributionIATANumber;
  private String idContext;
  private String promotionCode;
  private PromoKind promoKind;
  private String paymentOption;
  private Boolean upsellsAddonsEnabled;
}
