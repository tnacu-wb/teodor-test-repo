package uk.co.whitbread.basket.infrastructure.rest.client.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.domain.model.promotion.out.PromoKindResponse;
import uk.co.whitbread.basket.domain.model.promotion.out.RedeemPromoCodeResponse;
import uk.co.whitbread.basket.domain.ports.secondary.PromoOutPort;
import uk.co.whitbread.basket.infrastructure.rest.client.promotion.mapper.PromoKindResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.promotion.mapper.RedeemPromoCodeResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.promotion.service.PromoServiceClient;

@Slf4j
@RequiredArgsConstructor
public class PromoOutPortImpl implements PromoOutPort {

  private final PromoServiceClient promoServiceClient;
  private final PromoKindResponseMapper promoKindResponseMapper;
  private final RedeemPromoCodeResponseMapper redeemPromoCodeResponseMapper;

  @Override
  public PromoKindResponse getPromoKind(String promoCode) {
    log.debug("Entered getPromoKind with promoCode={}", promoCode);
    final var promoKind = promoServiceClient.getPromoKind(promoCode);
    return promoKindResponseMapper.toModel(promoKind);
  }

  @Override
  public RedeemPromoCodeResponse redeemPromoCode(String promoCode,
      String bookingReference) {
    log.debug("Entered redeemPromoCode with promoCode={}, ", promoCode);
    var response = promoServiceClient.redeemPromoCode(promoCode, bookingReference);
    return redeemPromoCodeResponseMapper.toModel(response);
  }
}
