package uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoContext;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoFlowType;

@Component
public class PromoFlowTypeResolver {

  public PromoFlowType resolve(PromoContext context) {

    if (context.isPromoBox() && !context.isAmendRequest()) {
      return PromoFlowType.PROMO_BOX;
    }

    if (context.isAmendRequest()) {
      return PromoFlowType.AMEND;
    }

    if (StringUtils.isNotBlank(context.getPromoCode())) {
      return PromoFlowType.LANDING_PAGE;
    }

    return PromoFlowType.SITE_WIDE;
  }
}