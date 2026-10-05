package uk.co.whitbread.domain.ports.secondary;

import uk.co.whitbread.domain.model.promotion.in.PromoKindRequest;
import uk.co.whitbread.domain.model.promotion.out.PromoKindResponse;

public interface PromotionOutPort {

  PromoKindResponse getPromoKind(PromoKindRequest promoKindRequest);
}
