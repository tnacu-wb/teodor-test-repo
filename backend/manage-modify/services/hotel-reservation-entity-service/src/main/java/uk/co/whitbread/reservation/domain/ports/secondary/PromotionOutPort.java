package uk.co.whitbread.reservation.domain.ports.secondary;

import uk.co.whitbread.reservation.domain.model.promotion.out.PromoKindResponse;

public interface PromotionOutPort {

  PromoKindResponse getPromoKind(String promoCode);
}
