package uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.content.domain.model.ErrorCode;
import uk.co.whitbread.content.domain.model.promoconfig.evaluator.PromoEvaluationStrategy;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoFlowType;
import uk.co.whitbread.content.infrastructure.rest.client.content.exceptions.PromotionException;

@Component
@Slf4j
public class PromoStrategyRegistry {

  private final Map<PromoFlowType, PromoEvaluationStrategy> registry;

  public PromoStrategyRegistry(List<PromoEvaluationStrategy> strategies) {
    this.registry = Collections.unmodifiableMap(buildRegistry(strategies));
  }

  private Map<PromoFlowType, PromoEvaluationStrategy> buildRegistry(
      List<PromoEvaluationStrategy> strategies) {

    EnumMap<PromoFlowType, PromoEvaluationStrategy> map =
        new EnumMap<>(PromoFlowType.class);

    for (PromoEvaluationStrategy strategy : strategies) {
      PromoFlowType flowType = strategy.flowType();

      if (flowType == null) {

        String debugMessage = String.format(
                "PromoEvaluationStrategy '%s' returned null flowType()",
                strategy.getClass().getName()
        );

        PromotionException exception =
                new PromotionException(
                        ErrorCode.DIGITAL_PROMO_STRATEGY_CONFIGURATION_EXCEPTION,
                        debugMessage
                );

        ExceptionLogger.log(log, exception);
        throw exception;
      }

      PromoEvaluationStrategy existing = map.putIfAbsent(flowType, strategy);
      if (existing != null) {
        String debugMessage = String.format(
                "Multiple PromoEvaluationStrategy implementations found for flowType '%s': %s, %s",
                flowType,
                existing.getClass().getName(),
                strategy.getClass().getName()
        );

        PromotionException exception =
                new PromotionException(
                        ErrorCode.DIGITAL_PROMO_STRATEGY_CONFIGURATION_EXCEPTION,
                        debugMessage
                );

        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }

    return map;
  }

  public PromoEvaluationStrategy get(PromoFlowType flowType) {
    PromoEvaluationStrategy strategy = registry.get(flowType);

    if (strategy == null) {
      String debugMessage = String.format(
              "No PromoEvaluationStrategy registered for flowType '%s'",
              flowType
      );

      PromotionException exception =
              new PromotionException(
                      ErrorCode.DIGITAL_PROMO_STRATEGY_NOT_FOUND_EXCEPTION,
                      debugMessage
              );

      ExceptionLogger.log(log, exception);
      throw exception;
    }

    return strategy;
  }
}