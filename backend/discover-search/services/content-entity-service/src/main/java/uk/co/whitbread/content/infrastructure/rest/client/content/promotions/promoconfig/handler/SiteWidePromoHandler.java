package uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.handler;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionItems;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsInformationResponse;
import uk.co.whitbread.content.domain.model.promoconfig.evaluator.PromoEvaluationStrategy;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoContext;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoFlowType;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoKind;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver.PromoEvaluationService;

@Component
@RequiredArgsConstructor
public class SiteWidePromoHandler extends NoPromoHandler
    implements PromoEvaluationStrategy {

  private final PromoEvaluationService promoEvaluationService;

  @Override
  public PromoFlowType flowType() {
    return PromoFlowType.SITE_WIDE;
  }

  @Override
  public PromotionsInformationResponse evaluate(
      PromoContext promoContext,
      PromotionsConfig promotionsConfig) {

    return promotionsConfig.getPromoItems().stream()
        .filter(PromotionItems::isEnabled)
        .filter(p -> StringUtils.isBlank(p.getLandingPage()))
        .map(p -> promoEvaluationService.evaluatePromo(
            p,
            promoContext.getBookingDate(),
            promoContext.getStayStartDate(),
            promoContext.getStayEndDate(),
            PromoKind.SITE_WIDE))
        .filter(PromotionsInformationResponse::getShowPromo)
        .findFirst()
        .orElse(noPromo());
  }
}