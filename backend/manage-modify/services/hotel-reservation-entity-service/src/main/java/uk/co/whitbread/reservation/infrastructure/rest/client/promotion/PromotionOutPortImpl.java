package uk.co.whitbread.reservation.infrastructure.rest.client.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.reservation.domain.model.promotion.out.PromoKindResponse;
import uk.co.whitbread.reservation.domain.ports.secondary.PromotionOutPort;
import uk.co.whitbread.reservation.infrastructure.rest.client.promotion.mapper.PromoKindResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.promotion.service.PromotionClient;

@Slf4j
@RequiredArgsConstructor
public class PromotionOutPortImpl implements PromotionOutPort {

  private final PromotionClient promotionClient;
  private final PromoKindResponseMapper promoKindResponseMapper;

  @Override
  public PromoKindResponse getPromoKind(String promoCode) {
    log.debug("Entered getPromoKind with promoCode={}", promoCode);
    final var promoKind = promotionClient.getPromoKind(promoCode);
    return promoKindResponseMapper.toModel(promoKind);
  }
}
