package uk.co.whitbread.promo.domain.model.promobatch.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RedeemPromoCodeResponse {
  private boolean success;
  private RedeemStatus status;
  private String promoCode;
  private String message;
}
