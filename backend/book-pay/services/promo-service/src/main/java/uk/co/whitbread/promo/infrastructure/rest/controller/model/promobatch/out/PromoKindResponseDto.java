package uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out;

import lombok.Data;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoKind;
import uk.co.whitbread.promo.domain.model.promocode.out.PromoCodeStatus;

@Data
public class PromoKindResponseDto {

  private PromoKind promoKind;
  private String operaPromoCode;
  private PromoCodeStatus uniquePromoCodeStatus;
}
