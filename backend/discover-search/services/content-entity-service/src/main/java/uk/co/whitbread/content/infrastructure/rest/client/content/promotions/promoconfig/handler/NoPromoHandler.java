package uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.handler;

import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsInformationResponse;

public abstract class NoPromoHandler {

  protected PromotionsInformationResponse noPromo() {
    return PromotionsInformationResponse.builder()
        .showPromo(false)
        .isWithinPromoWindow(false)
        .promoInvalidMessage("No applicable promotions")
        .build();
  }
}