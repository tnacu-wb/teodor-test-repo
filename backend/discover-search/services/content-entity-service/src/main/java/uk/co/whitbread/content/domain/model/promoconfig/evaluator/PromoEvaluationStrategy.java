package uk.co.whitbread.content.domain.model.promoconfig.evaluator;

import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsInformationResponse;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoContext;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoFlowType;

public interface PromoEvaluationStrategy {

  PromoFlowType flowType();

  PromotionsInformationResponse evaluate(
      PromoContext context,
      PromotionsConfig promotionsConfig
  );
}
