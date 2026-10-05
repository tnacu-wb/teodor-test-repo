package uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.promo.domain.model.promobatch.out.RedeemStatus;

@Data
@Builder
public class RedeemPromoCodeResponseDto {
  private boolean success;
  private RedeemStatus status;
  private String promoCode;
  private String message;
}
