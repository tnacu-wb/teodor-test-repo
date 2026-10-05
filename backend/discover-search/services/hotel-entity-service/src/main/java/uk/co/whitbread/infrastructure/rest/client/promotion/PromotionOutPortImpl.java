package uk.co.whitbread.infrastructure.rest.client.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.model.promotion.in.PromoKindRequest;
import uk.co.whitbread.domain.model.promotion.out.PromoKindResponse;
import uk.co.whitbread.domain.ports.secondary.PromotionOutPort;
import uk.co.whitbread.infrastructure.rest.client.promotion.mapper.PromoKindResponseMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class PromotionOutPortImpl implements PromotionOutPort {

  private final PromoServiceClient promoServiceClient;
  private final PromoKindResponseMapper promoKindResponseMapper;

  @Override
  public PromoKindResponse getPromoKind(PromoKindRequest request) {
    String promoCode = request.getPromoCode();
    log.debug("Entered getPromoKind with promoCode={}", promoCode);
    final var promoKind = promoServiceClient.getPromoKind(request);
    return promoKindResponseMapper.toModel(promoKind);
  }
}
