package uk.co.whitbread.basket.domain.ports.secondary;

import uk.co.whitbread.basket.domain.model.promotion.out.PromoKindResponse;
import uk.co.whitbread.basket.domain.model.promotion.out.RedeemPromoCodeResponse;

public interface PromoOutPort {

  PromoKindResponse getPromoKind(String promoCode);

  RedeemPromoCodeResponse redeemPromoCode(
      String promotionCode, String reference);
}
