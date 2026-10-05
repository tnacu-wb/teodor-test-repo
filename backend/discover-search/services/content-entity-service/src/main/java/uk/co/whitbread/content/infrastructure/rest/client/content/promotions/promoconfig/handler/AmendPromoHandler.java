package uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.handler;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
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
public class AmendPromoHandler extends NoPromoHandler
    implements PromoEvaluationStrategy {

  private final PromoEvaluationService promoEvaluationService;

  @Override
  public PromoFlowType flowType() {
    return PromoFlowType.AMEND;
  }

  @Override
  public PromotionsInformationResponse evaluate(
      PromoContext context,
      PromotionsConfig promotionsConfig) {

    if (context.getPromoKind() != null) {
      return promotionsConfig.getPromoItems().stream()
          .filter(PromotionItems::isEnabled)
          .filter(p -> Objects.equals(p.getPromoCode(), context.getPromoCode()))
          .findFirst()
          .map(p -> promoEvaluationService.evaluatePromo(
              p,
              context.getBookingDate(),
              context.getStayStartDate(),
              context.getStayEndDate(),
              context.getPromoKind()))
          .orElse(noPromo());
    }

    return promotionsConfig.getPromoItems().stream()
        .filter(PromotionItems::isEnabled)
        .filter(p -> Objects.equals(p.getPromoCode(), context.getPromoCode()))
        .findFirst()
        .map(p -> promoEvaluationService.evaluatePromo(
            p,
            context.getBookingDate(),
            context.getStayStartDate(),
            context.getStayEndDate(),
            PromoKind.LANDING_PAGE))
        .orElse(noPromo());
  }
}