package uk.co.whitbread.basket.domain.model.promotion.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.generated.models.promotion.PromoCodeStatus;
import uk.co.whitbread.basket.generated.models.promotion.PromoKind;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoKindResponse {

  private PromoKind promoKind;
  private String operaPromoCode;
  private PromoCodeStatus uniquePromoCodeStatus;
}