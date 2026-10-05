package uk.co.whitbread.basket.domain.model.basket.in;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.basket.out.PromoKind;

@Data
@Builder
public class PromotionsInformationRequest {

  private String promotionCode;
  private PromoKind promoKind;
}
