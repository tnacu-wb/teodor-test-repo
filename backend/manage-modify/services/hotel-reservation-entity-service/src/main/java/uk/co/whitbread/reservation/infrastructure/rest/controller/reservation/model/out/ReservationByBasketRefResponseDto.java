package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromoKind;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationByBasketRefResponseDto {

  private List<ReservationByIdDto> reservationByIdList;
  private BigDecimal previousTotal;
  private BigDecimal balanceOutstanding;
  private BigDecimal newTotal;
  private BigDecimal totalCost;
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
  private String basketStatus;
  private String customReferenceNumber;
  private String idContext;
  private String promotionCode;
  private PromoKind promoKind;
  private String paymentOption;
  private Boolean upsellsAddonsEnabled;
}
