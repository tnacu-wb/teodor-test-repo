package uk.co.whitbread.basket.domain.model.promotion.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.generated.models.promotion.RedeemStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedeemPromoCodeResponse {
  private boolean success;
  private RedeemStatus status;
  private String promoCode;
  private String message;
}
