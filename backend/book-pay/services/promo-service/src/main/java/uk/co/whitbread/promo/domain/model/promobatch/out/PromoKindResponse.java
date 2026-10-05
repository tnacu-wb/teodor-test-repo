package uk.co.whitbread.promo.domain.model.promobatch.out;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.promo.domain.model.promocode.out.PromoCodeStatus;

@Data
@Builder
public class PromoKindResponse {
  private PromoKind promoKind;
  private String operaPromoCode;
  private PromoCodeStatus uniquePromoCodeStatus;
}
